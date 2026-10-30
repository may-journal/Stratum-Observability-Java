package stratum;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * The engine an app publishes catalogued events through, usually one shared constant. It gives each
 * event to the plugins that take it, and is safe to call from any thread.
 */
public final class Stratum {

  private final String productName;
  private final String productVersion;
  private final Object lock = new Object();
  private final Map<String, Catalog> catalogs = new LinkedHashMap<>();
  private final List<Plugin> plugins = new ArrayList<>();
  private final Map<String, String> context = new HashMap<>();
  private final List<Consumer<Snapshot>> listeners = new ArrayList<>();

  /**
   * Starts an engine for one product, with its catalog and plugins.
   *
   * @throws CatalogException when an event cannot be published, or a plugin refuses it
   */
  public Stratum(String productName, String productVersion, Catalog catalog, Plugin... plugins) {
    this.productName = productName;
    this.productVersion = productVersion;
    add(catalog);
    for (Plugin plugin : plugins) {
      add(plugin);
    }
  }

  /**
   * Adds a catalog, or replaces the one with the same name.
   *
   * @throws CatalogException when an event cannot be published, its key or id is another event's,
   *     or a plugin refuses it
   */
  public void add(Catalog catalog) {
    validate(catalog, held());
    synchronized (lock) {
      Map<String, Catalog> others = new HashMap<>(catalogs);
      others.remove(catalog.name());
      Map<String, String> keys = new HashMap<>();
      Map<Integer, String> ids = new HashMap<>();
      others.forEach(
          (name, other) ->
              other.items().forEach((key, event) -> claim(keys, ids, key, event, name)));
      for (Map.Entry<CatalogKey, Event> item : catalog.sorted()) {
        claim(keys, ids, item.getKey(), item.getValue(), catalog.name());
      }
      catalogs.put(catalog.name(), catalog);
    }
  }

  /** Records that {@code catalog} holds {@code key} and its event's id, or throws for a clash. */
  private static void claim(
      Map<String, String> keys,
      Map<Integer, String> ids,
      CatalogKey key,
      Event event,
      String catalog) {
    String owner = keys.putIfAbsent(key.key(), catalog);
    if (owner != null) {
      throw CatalogException.duplicateKey(key, owner);
    }
    String other = ids.putIfAbsent(event.id(), key.key());
    if (other != null) {
      throw CatalogException.duplicateId(key, event.id(), other);
    }
  }

  /** Throws for the first event, by key, that cannot be published or a plugin refuses. */
  private static void validate(Catalog catalog, List<Plugin> plugins) {
    for (Map.Entry<CatalogKey, Event> item : catalog.sorted()) {
      String reason = item.getValue().problem();
      if (reason != null) {
        throw CatalogException.invalidEvent(item.getKey(), reason);
      }
      for (Plugin plugin : plugins) {
        if (Event.reaches(item.getValue().eventType(), plugin.acceptedTypes())) {
          plugin.check(item.getKey(), item.getValue());
        }
      }
    }
  }

  private List<Plugin> held() {
    synchronized (lock) {
      return List.copyOf(plugins);
    }
  }

  /** Removes the catalog named {@code name}, so its events no longer publish. */
  public void removeCatalog(String name) {
    synchronized (lock) {
      catalogs.remove(name);
    }
  }

  /**
   * Adds a plugin, or replaces the one with the same name.
   *
   * @throws CatalogException when the plugin refuses an event of a catalog the engine holds
   */
  public void add(Plugin plugin) {
    final List<Catalog> held;
    synchronized (lock) {
      held = List.copyOf(catalogs.values());
    }
    for (Catalog catalog : held) {
      validate(catalog, List.of(plugin));
    }
    synchronized (lock) {
      plugins.removeIf(other -> other.name().equals(plugin.name()));
      plugins.add(plugin);
    }
  }

  /** Sets a context value every later snapshot carries, such as {@code entry(SCREEN, "Today")}. */
  public void setContext(Map.Entry<CatalogField, String> value) {
    synchronized (lock) {
      context.put(value.getKey().key(), value.getValue());
    }
  }

  /** Clears the context value for {@code field}, so later snapshots leave it out. */
  public void clearContext(CatalogField field) {
    synchronized (lock) {
      context.remove(field.key());
    }
  }

  /** Calls {@code listener} with every snapshot this engine publishes. */
  public void addListener(Consumer<Snapshot> listener) {
    synchronized (lock) {
      listeners.add(listener);
    }
  }

  /**
   * Publishes the event under {@code key} with {@code fields}, such as {@code
   * entry(NoteField.WORDS, "120")}; a field it does not name is left out. Returns whether a catalog
   * held the key.
   */
  @SafeVarargs
  public final boolean publish(CatalogKey key, Map.Entry<CatalogField, String>... fields) {
    Map<CatalogField, String> given = new HashMap<>();
    for (Map.Entry<CatalogField, String> field : fields) {
      given.put(field.getKey(), field.getValue());
    }
    final Snapshot snapshot;
    final List<Consumer<Snapshot>> heard;
    final List<Plugin> targets;
    synchronized (lock) {
      Event event =
          catalogs.values().stream()
              .map(catalog -> catalog.items().get(key))
              .filter(Objects::nonNull)
              .findFirst()
              .orElse(null);
      if (event == null) {
        return false;
      }
      snapshot = snapshot(key, event, given);
      heard = List.copyOf(listeners);
      targets = List.copyOf(plugins);
    }
    heard.forEach(listener -> listener.accept(snapshot));
    targets.stream()
        .filter(plugin -> Event.reaches(snapshot.eventType(), plugin.acceptedTypes()))
        .filter(plugin -> plugin.shouldPublish(snapshot))
        .forEach(plugin -> plugin.publish(snapshot));
    return true;
  }

  private Snapshot snapshot(CatalogKey key, Event event, Map<CatalogField, String> given) {
    return new Snapshot(
        key.key(),
        event.id(),
        event.description(),
        event.eventType(),
        event.level(),
        event.settings(),
        Event.only(given, event.publicFields()),
        Event.only(given, event.privateFields()),
        context,
        Instant.now(),
        productName,
        productVersion);
  }
}

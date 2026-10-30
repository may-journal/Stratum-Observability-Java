package io.github.mayjournal.stratum;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * The service an app publishes catalogued events through. It holds the catalogs, the plugins and
 * the context, and turns each published key into one snapshot for every plugin that accepts it.
 * Every method is safe to call from any thread.
 */
public final class Stratum {

  private final String product;
  private final String version;
  private final Object lock = new Object();
  private final Map<String, Catalog> catalogs = new LinkedHashMap<>();
  private final List<Plugin> plugins = new ArrayList<>();
  private final Map<String, String> context = new HashMap<>();
  private final List<Consumer<Snapshot>> listeners = new ArrayList<>();

  /**
   * Starts a service for one product, with its first catalogs and plugins.
   *
   * @throws CatalogException when a catalog holds an event that cannot be published, or a key
   *     another catalog already holds
   */
  public Stratum(String product, String version, List<Catalog> catalogs, List<Plugin> plugins)
      throws CatalogException {
    this.product = product;
    this.version = version;
    for (Catalog catalog : catalogs) {
      add(catalog);
    }
    for (Plugin plugin : plugins) {
      add(plugin);
    }
  }

  /**
   * Adds a catalog, or replaces the one with the same name.
   *
   * @throws CatalogException when an event cannot be published or another catalog already holds one
   *     of its keys
   */
  public void add(Catalog catalog) throws CatalogException {
    validate(catalog);
    synchronized (lock) {
      Map<String, Catalog> others = new HashMap<>(catalogs);
      others.remove(catalog.name());
      for (Catalog other : others.values()) {
        rejectShared(catalog, other);
      }
      catalogs.put(catalog.name(), catalog);
    }
  }

  /** Throws for the first event, by key, that cannot be published. */
  private static void validate(Catalog catalog) throws CatalogException {
    for (Map.Entry<String, Event> entry : new TreeMap<>(catalog.events()).entrySet()) {
      String reason = entry.getValue().problem(entry.getKey());
      if (reason != null) {
        throw CatalogException.invalidEvent(entry.getKey(), reason);
      }
    }
  }

  /** Throws for the first key, in order, that {@code other} already holds. */
  private static void rejectShared(Catalog catalog, Catalog other) throws CatalogException {
    for (String key : new TreeSet<>(catalog.events().keySet())) {
      if (other.events().containsKey(key)) {
        throw CatalogException.duplicateKey(key, other.name());
      }
    }
  }

  /** Removes the catalog named {@code name}, so its events no longer publish. */
  public void removeCatalog(String name) {
    synchronized (lock) {
      catalogs.remove(name);
    }
  }

  /** Adds a plugin, or replaces the one with the same name. */
  public void add(Plugin plugin) {
    synchronized (lock) {
      plugins.removeIf(other -> other.name().equals(plugin.name()));
      plugins.add(plugin);
    }
  }

  /**
   * Sets a context value every later snapshot carries, or clears it with null. Context is public:
   * keep what a person wrote out of it.
   */
  public void setContext(String key, String value) {
    synchronized (lock) {
      if (value == null) {
        context.remove(key);
      } else {
        context.put(key, value);
      }
    }
  }

  /** Calls {@code listener} with every snapshot this service publishes. */
  public void addListener(Consumer<Snapshot> listener) {
    synchronized (lock) {
      listeners.add(listener);
    }
  }

  /** Publishes the event a catalog holds under {@code key}, with no fields. */
  public boolean publish(String key) {
    return publish(key, Map.of());
  }

  /**
   * Publishes the event a catalog holds under {@code key}, with the fields it names. Returns
   * whether a catalog held the key; nothing is delivered when none does.
   */
  public boolean publish(String key, Map<String, String> fields) {
    final Snapshot snapshot;
    final List<Consumer<Snapshot>> heard;
    final List<Plugin> targets;
    synchronized (lock) {
      Event event =
          catalogs.values().stream()
              .map(catalog -> catalog.events().get(key))
              .filter(Objects::nonNull)
              .findFirst()
              .orElse(null);
      if (event == null) {
        return false;
      }
      snapshot =
          new Snapshot(
              key,
              event.meaning(),
              event.category(),
              event.level(),
              only(fields, event.publicFields()),
              only(fields, event.privateFields()),
              context,
              Instant.now(),
              product,
              version);
      heard = List.copyOf(listeners);
      targets = List.copyOf(plugins);
    }
    heard.forEach(listener -> listener.accept(snapshot));
    targets.stream().filter(plugin -> plugin.accepts(snapshot)).forEach(p -> p.publish(snapshot));
    return true;
  }

  private static Map<String, String> only(Map<String, String> fields, Set<String> names) {
    Map<String, String> kept = new HashMap<>(fields);
    kept.keySet().retainAll(names);
    return kept;
  }
}

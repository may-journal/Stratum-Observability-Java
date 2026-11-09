package stratum;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** A named, versioned set of events by key, declared once by an app as a constant. */
public record Catalog(String name, String version, Map<CatalogKey, Event> items) {

  /** Copies the items, so a later change to the caller's map never reaches the catalog. */
  public Catalog {
    items = Map.copyOf(items);
  }

  /** The items in key order, so checks always report the same event first. */
  List<Map.Entry<CatalogKey, Event>> sorted() {
    return items.entrySet().stream()
        .sorted(Comparator.comparing(item -> item.getKey().key()))
        .toList();
  }
}

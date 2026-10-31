package stratum;

import java.util.Map;

/** A second catalog, which a test adds after the notes engine starts. */
final class SharingCatalog {

  static final Catalog CATALOG =
      new Catalog(
          "Sharing",
          "1.0",
          Map.of(
              ShareKey.SHARED,
              Event.of(BaseType.BASE, "A person shared a note", 10)
                  .publicFields(ShareField.TO)
                  .privateFields(ShareField.MESSAGE)));

  private SharingCatalog() {}
}

package stratum.log;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Requirement 0011: quoting text log values. */
class LogQuotingTests {

  @Test
  void test0011_1() {
    assertEquals("note.saved place=\"Front desk\" screen=Today", LogTests.saved("Front desk"));
  }

  @Test
  void test0011_2() {
    assertEquals(
        "note.saved place=\"desk\\nnote.deleted\" screen=Today",
        LogTests.saved("desk\nnote.deleted"));
  }

  @Test
  void test0011_3() {
    String place = "a=b \"c\" d\\e\tf\rg\u0007h\u2028i\u2029j";
    String shown = "\"a=b \\\"c\\\" d\\\\e\\tf\\rg\\u0007h\\u2028i\\u2029j\"";
    assertEquals("note.saved place=" + shown + " screen=Today", LogTests.saved(place));
    assertEquals("note.saved place=\"a=b\" screen=Today", LogTests.saved("a=b"));
  }

  @Test
  void test0011_4() {
    assertEquals("note.saved place=\"\" screen=Today", LogTests.saved(""));
  }

  @Test
  void test0011_5() {
    assertEquals(
        "note.saved place=\"front\u00a0desk\" screen=Today", LogTests.saved("front\u00a0desk"));
  }
}

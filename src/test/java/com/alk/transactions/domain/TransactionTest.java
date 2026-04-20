package com.alk.transactions.domain;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class TransactionTest {

  @Test
  void shouldRequireId() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Transaction(null, BigDecimal.TEN, "cars", null));
  }

  @Test
  void shouldRequireAmount() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Transaction(10L, null, "cars", null));
  }

  @Test
  void shouldRequireType() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Transaction(10L, BigDecimal.TEN, null, null));
  }

  @Test
  void shouldRejectBlankType() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Transaction(10L, BigDecimal.TEN, "   ", null));
  }

  @Test
  void shouldAllowNullParentId() {
    assertDoesNotThrow(() -> new Transaction(10L, BigDecimal.TEN, "cars", null));
  }

  @Test
  void shouldAllowParentId() {
    assertDoesNotThrow(() -> new Transaction(11L, BigDecimal.ONE, "shopping", 10L));
  }
}

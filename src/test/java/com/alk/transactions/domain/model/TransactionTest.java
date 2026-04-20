package com.alk.transactions.domain.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class TransactionTest {

  @Test
  void shouldCreateValidTransaction() {
    assertDoesNotThrow(() -> new Transaction(1L, 10.5, "cars", null));
  }

  @Test
  void shouldFailWhenIdIsNull() {
    assertThrows(IllegalArgumentException.class, () -> new Transaction(null, 10.5, "cars", null));
  }

  @Test
  void shouldFailWhenAmountIsNull() {
    assertThrows(IllegalArgumentException.class, () -> new Transaction(1L, null, "cars", null));
  }

  @Test
  void shouldFailWhenTypeIsBlank() {
    assertThrows(IllegalArgumentException.class, () -> new Transaction(1L, 10.5, "   ", null));
  }
}

package com.alk.transactions.infrastructure.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.alk.transactions.domain.model.Transaction;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InMemoryTransactionRepositoryTest {

  @Test
  void shouldSaveTransaction() {
    InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
    Transaction transaction = new Transaction(1L, 10.0, "cars", null);

    repository.save(transaction);

    Optional<Transaction> stored = repository.findById(1L);
    assertTrue(stored.isPresent());
  }

  @Test
  void shouldFindByIdWhenStored() {
    InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
    Transaction transaction = new Transaction(1L, 10.0, "cars", null);
    repository.save(transaction);

    Optional<Transaction> result = repository.findById(1L);

    assertTrue(result.isPresent());
    assertEquals(1L, result.get().getId());
  }

  @Test
  void shouldReturnEmptyWhenFindByIdNotFound() {
    InMemoryTransactionRepository repository = new InMemoryTransactionRepository();

    Optional<Transaction> result = repository.findById(999L);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldFindIdsByType() {
    InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
    repository.save(new Transaction(1L, 10.0, "cars", null));
    repository.save(new Transaction(2L, 20.0, "shopping", null));
    repository.save(new Transaction(3L, 30.0, "cars", null));

    List<Long> ids = repository.findIdsByType("cars");

    assertEquals(List.of(1L, 3L), ids);
  }

  @Test
  void shouldReturnEmptyListWhenTypeNotFound() {
    InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
    repository.save(new Transaction(1L, 10.0, "cars", null));

    List<Long> ids = repository.findIdsByType("shopping");

    assertTrue(ids.isEmpty());
  }

  @Test
  void shouldFindChildrenByParentId() {
    InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
    repository.save(new Transaction(1L, 10.0, "cars", null));
    repository.save(new Transaction(2L, 20.0, "cars", 1L));
    repository.save(new Transaction(3L, 30.0, "cars", 1L));
    repository.save(new Transaction(4L, 40.0, "cars", 2L));

    List<Transaction> children = repository.findChildren(1L);

    assertEquals(2, children.size());
    assertEquals(List.of(2L, 3L), children.stream().map(Transaction::getId).toList());
  }

  @Test
  void shouldReturnEmptyListWhenChildrenNotFound() {
    InMemoryTransactionRepository repository = new InMemoryTransactionRepository();
    repository.save(new Transaction(1L, 10.0, "cars", null));

    List<Transaction> children = repository.findChildren(999L);

    assertTrue(children.isEmpty());
  }
}

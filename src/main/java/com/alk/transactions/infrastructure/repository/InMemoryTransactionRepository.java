package com.alk.transactions.infrastructure.repository;

import com.alk.transactions.domain.model.Transaction;
import com.alk.transactions.domain.repository.TransactionRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryTransactionRepository implements TransactionRepository {

  private final Map<Long, Transaction> transactions = new HashMap<>();
  private final Map<String, List<Long>> transactionsByType = new HashMap<>();
  private final Map<Long, List<Transaction>> childrenByParent = new HashMap<>();

  @Override
  public void save(Transaction transaction) {
    transactions.put(transaction.getId(), transaction);
    indexByType(transaction);
    indexByParent(transaction);
  }

  @Override
  public Optional<Transaction> findById(Long id) {
    return Optional.ofNullable(transactions.get(id));
  }

  @Override
  public List<Long> findIdsByType(String type) {
    return copyOf(transactionsByType.get(type));
  }

  @Override
  public List<Transaction> findChildren(Long parentId) {
    return copyOf(childrenByParent.get(parentId));
  }

  private void indexByType(Transaction transaction) {
    transactionsByType.computeIfAbsent(transaction.getType(), key -> new ArrayList<>())
        .add(transaction.getId());
  }

  private void indexByParent(Transaction transaction) {
    if (transaction.getParentId() == null) {
      return;
    }
    childrenByParent.computeIfAbsent(transaction.getParentId(), key -> new ArrayList<>())
        .add(transaction);
  }

  private static <T> List<T> copyOf(List<T> source) {
    if (source == null) {
      return List.of();
    }
    return new ArrayList<>(source);
  }
}

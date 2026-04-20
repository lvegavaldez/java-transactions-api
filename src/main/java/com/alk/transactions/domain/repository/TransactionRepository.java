package com.alk.transactions.domain.repository;

import com.alk.transactions.domain.model.Transaction;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository {

  void save(Transaction transaction);

  Optional<Transaction> findById(Long id);

  List<Long> findIdsByType(String type);

  List<Transaction> findChildren(Long parentId);
}

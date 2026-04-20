package com.alk.transactions.application.usecase;

import com.alk.transactions.domain.repository.TransactionRepository;
import java.util.List;

public class GetTransactionsByTypeUseCase {

  private final TransactionRepository transactionRepository;

  public GetTransactionsByTypeUseCase(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  public List<Long> execute(String type) {
    return List.copyOf(transactionRepository.findIdsByType(type));
  }
}

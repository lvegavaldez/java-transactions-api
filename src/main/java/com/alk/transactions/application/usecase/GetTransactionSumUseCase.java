package com.alk.transactions.application.usecase;

import com.alk.transactions.domain.model.Transaction;
import com.alk.transactions.domain.repository.TransactionRepository;

public class GetTransactionSumUseCase {

  private final TransactionRepository transactionRepository;

  public GetTransactionSumUseCase(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  public double execute(Long transactionId) {
    Transaction root = transactionRepository.findById(transactionId)
        .orElseThrow(() -> new TransactionNotFoundException(transactionId));
    return calculateTransitiveSum(root);
  }

  private double calculateTransitiveSum(Transaction transaction) {
    double childrenSum = transactionRepository.findChildren(transaction.getId()).stream()
        .mapToDouble(this::calculateTransitiveSum)
        .sum();
    return transaction.getAmount() + childrenSum;
  }

  public static class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(Long transactionId) {
      super("Transaction not found: " + transactionId);
    }
  }
}

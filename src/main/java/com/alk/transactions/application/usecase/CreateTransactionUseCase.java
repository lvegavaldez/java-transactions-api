package com.alk.transactions.application.usecase;

import com.alk.transactions.domain.model.Transaction;
import com.alk.transactions.domain.repository.TransactionRepository;

public class CreateTransactionUseCase {

  private final TransactionRepository transactionRepository;

  public CreateTransactionUseCase(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  public void execute(Long transactionId, Double amount, String type, Long parentId) {
    ensureTransactionDoesNotExist(transactionId);
    ensureParentExistsWhenProvided(parentId);

    Transaction transaction = new Transaction(transactionId, amount, type, parentId);
    transactionRepository.save(transaction);
  }

  private void ensureTransactionDoesNotExist(Long transactionId) {
    if (transactionRepository.findById(transactionId).isPresent()) {
      throw new TransactionAlreadyExistsException(transactionId);
    }
  }

  private void ensureParentExistsWhenProvided(Long parentId) {
    if (parentId != null && transactionRepository.findById(parentId).isEmpty()) {
      throw new ParentTransactionNotFoundException(parentId);
    }
  }

  public static class TransactionAlreadyExistsException extends RuntimeException {
    public TransactionAlreadyExistsException(Long transactionId) {
      super("Transaction already exists: " + transactionId);
    }
  }

  public static class ParentTransactionNotFoundException extends RuntimeException {
    public ParentTransactionNotFoundException(Long parentId) {
      super("Parent transaction not found: " + parentId);
    }
  }
}

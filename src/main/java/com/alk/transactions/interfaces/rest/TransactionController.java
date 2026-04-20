package com.alk.transactions.interfaces.rest;

import com.alk.transactions.application.usecase.CreateTransactionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

  private final CreateTransactionUseCase createTransactionUseCase;

  public TransactionController(CreateTransactionUseCase createTransactionUseCase) {
    this.createTransactionUseCase = createTransactionUseCase;
  }

  @PutMapping("/transactions/{transactionId}")
  public ResponseEntity<StatusResponse> createTransaction(
      @PathVariable Long transactionId,
      @RequestBody CreateTransactionRequest request) {
    try {
      createTransactionUseCase.execute(
          transactionId,
          request.amount(),
          request.type(),
          request.parent_id());
      return ResponseEntity.ok(new StatusResponse("ok"));
    } catch (CreateTransactionUseCase.TransactionAlreadyExistsException ex) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    } catch (CreateTransactionUseCase.ParentTransactionNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
  }

  public record CreateTransactionRequest(Double amount, String type, Long parent_id) {}

  public record StatusResponse(String status) {}
}

package com.alk.transactions.interfaces.rest;

import com.alk.transactions.application.usecase.CreateTransactionUseCase;
import com.alk.transactions.application.usecase.GetTransactionSumUseCase;
import com.alk.transactions.application.usecase.GetTransactionsByTypeUseCase;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

  private final CreateTransactionUseCase createTransactionUseCase;
  private final GetTransactionsByTypeUseCase getTransactionsByTypeUseCase;
  private final GetTransactionSumUseCase getTransactionSumUseCase;

  public TransactionController(
      CreateTransactionUseCase createTransactionUseCase,
      GetTransactionsByTypeUseCase getTransactionsByTypeUseCase,
      GetTransactionSumUseCase getTransactionSumUseCase) {
    this.createTransactionUseCase = createTransactionUseCase;
    this.getTransactionsByTypeUseCase = getTransactionsByTypeUseCase;
    this.getTransactionSumUseCase = getTransactionSumUseCase;
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

  @GetMapping("/transactions/types/{type}")
  public List<Long> getTransactionsByType(@PathVariable String type) {
    return getTransactionsByTypeUseCase.execute(type);
  }

  @GetMapping("/transactions/sum/{transactionId}")
  public ResponseEntity<?> getTransactionSum(@PathVariable Long transactionId) {
    try {
      double sum = getTransactionSumUseCase.execute(transactionId);
      return ResponseEntity.ok(new SumResponse(sum));
    } catch (GetTransactionSumUseCase.TransactionNotFoundException ex) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(ex.getMessage()));
    }
  }

  public record CreateTransactionRequest(Double amount, String type, Long parent_id) {}

  public record StatusResponse(String status) {}

  public record SumResponse(Double sum) {}

  public record ErrorResponse(String message) {}
}

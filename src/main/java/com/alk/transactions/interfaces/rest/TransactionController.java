package com.alk.transactions.interfaces.rest;

import com.alk.transactions.application.usecase.CreateTransactionUseCase;
import com.alk.transactions.application.usecase.GetTransactionSumUseCase;
import com.alk.transactions.application.usecase.GetTransactionsByTypeUseCase;
import java.util.List;
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
  public StatusResponse createTransaction(
      @PathVariable Long transactionId,
      @RequestBody CreateTransactionRequest request) {
    createTransactionUseCase.execute(
        transactionId,
        request.amount(),
        request.type(),
        request.parent_id());
    return new StatusResponse("ok");
  }

  @GetMapping("/transactions/types/{type}")
  public List<Long> getTransactionsByType(@PathVariable String type) {
    return getTransactionsByTypeUseCase.execute(type);
  }

  @GetMapping("/transactions/sum/{transactionId}")
  public SumResponse getTransactionSum(@PathVariable Long transactionId) {
    double sum = getTransactionSumUseCase.execute(transactionId);
    return new SumResponse(sum);
  }

  public record CreateTransactionRequest(Double amount, String type, Long parent_id) {}

  public record StatusResponse(String status) {}

  public record SumResponse(Double sum) {}
}

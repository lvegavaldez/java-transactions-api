package com.alk.transactions.infrastructure.config;

import com.alk.transactions.application.usecase.CreateTransactionUseCase;
import com.alk.transactions.application.usecase.GetTransactionsByTypeUseCase;
import com.alk.transactions.domain.repository.TransactionRepository;
import com.alk.transactions.infrastructure.repository.InMemoryTransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TransactionBeansConfig {

  @Bean
  public TransactionRepository transactionRepository() {
    return new InMemoryTransactionRepository();
  }

  @Bean
  public CreateTransactionUseCase createTransactionUseCase(TransactionRepository transactionRepository) {
    return new CreateTransactionUseCase(transactionRepository);
  }

  @Bean
  public GetTransactionsByTypeUseCase getTransactionsByTypeUseCase(TransactionRepository transactionRepository) {
    return new GetTransactionsByTypeUseCase(transactionRepository);
  }
}

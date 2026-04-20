package com.alk.transactions.domain.model;

public class Transaction {

  private final Long id;
  private final Double amount;
  private final String type;
  private final Long parentId;

  public Transaction(Long id, Double amount, String type, Long parentId) {
    if (id == null) {
      throw new IllegalArgumentException("id is required");
    }
    if (amount == null) {
      throw new IllegalArgumentException("amount is required");
    }
    if (type == null || type.isBlank()) {
      throw new IllegalArgumentException("type is required");
    }

    this.id = id;
    this.amount = amount;
    this.type = type;
    this.parentId = parentId;
  }

  public Long getId() {
    return id;
  }

  public Double getAmount() {
    return amount;
  }

  public String getType() {
    return type;
  }

  public Long getParentId() {
    return parentId;
  }
}

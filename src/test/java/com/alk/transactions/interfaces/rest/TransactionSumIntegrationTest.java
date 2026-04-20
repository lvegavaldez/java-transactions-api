package com.alk.transactions.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TransactionSumIntegrationTest {

  @Value("${local.server.port}")
  private int port;

  @Test
  void shouldReturnOwnAmountWhenTransactionHasNoChildren() throws Exception {
    putTransaction(7001L, """
        {
          "amount": 15.5,
          "type": "sum-no-children"
        }
        """);

    HttpResponse<String> response = getTransactionSum(7001L);

    assertEquals(200, response.statusCode());
    assertEquals(15.5, parseSum(response.body()));
  }

  @Test
  void shouldReturnSumIncludingDirectChild() throws Exception {
    putTransaction(7010L, """
        {
          "amount": 10.0,
          "type": "sum-direct"
        }
        """);
    putTransaction(7011L, """
        {
          "amount": 5.0,
          "type": "sum-direct",
          "parent_id": 7010
        }
        """);

    HttpResponse<String> response = getTransactionSum(7010L);

    assertEquals(200, response.statusCode());
    assertEquals(15.0, parseSum(response.body()));
  }

  @Test
  void shouldReturnSumIncludingNestedDescendants() throws Exception {
    putTransaction(7020L, """
        {
          "amount": 10.0,
          "type": "sum-nested"
        }
        """);
    putTransaction(7021L, """
        {
          "amount": 20.0,
          "type": "sum-nested",
          "parent_id": 7020
        }
        """);
    putTransaction(7022L, """
        {
          "amount": 30.0,
          "type": "sum-nested",
          "parent_id": 7021
        }
        """);

    HttpResponse<String> response = getTransactionSum(7020L);

    assertEquals(200, response.statusCode());
    assertEquals(60.0, parseSum(response.body()));
  }

  @Test
  void shouldFailWhenTransactionDoesNotExist() throws Exception {
    HttpResponse<String> response = getTransactionSum(7999L);

    assertEquals(404, response.statusCode());
    assertTrue(response.body().toLowerCase().contains("not found"));
  }

  private HttpResponse<String> putTransaction(Long transactionId, String body)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" + port + "/transactions/" + transactionId))
        .header("Content-Type", "application/json")
        .PUT(HttpRequest.BodyPublishers.ofString(body))
        .build();

    return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
  }

  private HttpResponse<String> getTransactionSum(Long transactionId)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" + port + "/transactions/sum/" + transactionId))
        .GET()
        .build();

    return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
  }

  private double parseSum(String body) {
    String trimmed = body.trim();
    int separatorIndex = trimmed.indexOf(':');
    int endIndex = trimmed.lastIndexOf('}');
    String value = trimmed.substring(separatorIndex + 1, endIndex).trim();
    return Double.parseDouble(value);
  }
}

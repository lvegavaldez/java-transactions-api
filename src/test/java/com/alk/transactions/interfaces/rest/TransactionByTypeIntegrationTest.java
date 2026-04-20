package com.alk.transactions.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TransactionByTypeIntegrationTest {

  @Value("${local.server.port}")
  private int port;

  @Test
  void shouldReturnIdsForGivenType() throws Exception {
    String targetType = "cars-one-" + System.nanoTime();
    String otherType = "other-" + System.nanoTime();
    putTransaction(4001L, """
        {
          "amount": 10.0,
          "type": "%s"
        }
        """.formatted(targetType));
    putTransaction(4002L, """
        {
          "amount": 20.0,
          "type": "%s"
        }
        """.formatted(otherType));

    HttpResponse<String> response = getTransactionsByType(targetType);

    assertEquals(200, response.statusCode());
    assertEquals(List.of(4001L), parseIds(response.body()));
  }

  @Test
  void shouldReturnMultipleIdsForSameType() throws Exception {
    String type = "cars-many-" + System.nanoTime();
    putTransaction(4011L, """
        {
          "amount": 10.0,
          "type": "%s"
        }
        """.formatted(type));
    putTransaction(4012L, """
        {
          "amount": 20.0,
          "type": "%s"
        }
        """.formatted(type));

    HttpResponse<String> response = getTransactionsByType(type);
    assertEquals(200, response.statusCode());
    List<Long> ids = parseIds(response.body());
    assertEquals(2, ids.size());
    assertTrue(ids.contains(4011L));
    assertTrue(ids.contains(4012L));
  }

  @Test
  void shouldReturnEmptyListWhenNoTransactionsExistForType() throws Exception {
    String type = "missing-" + System.nanoTime();

    HttpResponse<String> response = getTransactionsByType(type);

    assertEquals(200, response.statusCode());
    assertEquals(List.of(), parseIds(response.body()));
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

  private HttpResponse<String> getTransactionsByType(String type)
      throws IOException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://localhost:" + port + "/transactions/types/" + type))
        .GET()
        .build();

    return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
  }

  private List<Long> parseIds(String body) {
    String trimmed = body.trim();
    if ("[]".equals(trimmed)) {
      return List.of();
    }

    String content = trimmed.substring(1, trimmed.length() - 1);
    return java.util.Arrays.stream(content.split(","))
        .map(String::trim)
        .map(Long::parseLong)
        .toList();
  }
}

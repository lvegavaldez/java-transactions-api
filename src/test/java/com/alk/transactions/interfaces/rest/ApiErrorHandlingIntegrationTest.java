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
class ApiErrorHandlingIntegrationTest {

  @Value("${local.server.port}")
  private int port;

  @Test
  void shouldReturnConsistentErrorForDuplicateTransactionId() throws Exception {
    HttpResponse<String> first = putTransaction(8100L, """
        {
          "amount": 10.0,
          "type": "duplicate-test"
        }
        """);
    assertEquals(200, first.statusCode());

    HttpResponse<String> duplicate = putTransaction(8100L, """
        {
          "amount": 20.0,
          "type": "duplicate-test"
        }
        """);

    assertEquals(409, duplicate.statusCode());
    assertConsistentErrorBody(duplicate.body(), 409, "/transactions/8100");
    assertTrue(duplicate.body().contains("Transaction already exists"));
  }

  @Test
  void shouldReturnConsistentErrorForMissingParentTransaction() throws Exception {
    HttpResponse<String> response = putTransaction(8200L, """
        {
          "amount": 10.0,
          "type": "missing-parent-test",
          "parent_id": 99999
        }
        """);

    assertEquals(404, response.statusCode());
    assertConsistentErrorBody(response.body(), 404, "/transactions/8200");
    assertTrue(response.body().contains("Parent transaction not found"));
  }

  @Test
  void shouldReturnConsistentErrorForMissingTransactionSum() throws Exception {
    HttpResponse<String> response = getTransactionSum(8300L);

    assertEquals(404, response.statusCode());
    assertConsistentErrorBody(response.body(), 404, "/transactions/sum/8300");
    assertTrue(response.body().contains("Transaction not found"));
  }

  @Test
  void shouldReturnConsistentErrorForInvalidRequestPayload() throws Exception {
    HttpResponse<String> response = putTransaction(8400L, """
        {
          "amount": 10.0,
          "type": " "
        }
        """);

    assertEquals(400, response.statusCode());
    assertConsistentErrorBody(response.body(), 400, "/transactions/8400");
    assertTrue(response.body().contains("type is required"));
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

  private void assertConsistentErrorBody(String body, int status, String path) {
    assertTrue(body.contains("\"status\":" + status));
    assertTrue(body.contains("\"error\""));
    assertTrue(body.contains("\"path\":\"" + path + "\""));
  }
}

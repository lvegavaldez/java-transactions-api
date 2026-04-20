package com.alk.transactions.interfaces.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.beans.factory.annotation.Value;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TransactionControllerIntegrationTest {

  @Value("${local.server.port}")
  private int port;

  @Test
  void shouldCreateTransactionWithoutParent() throws Exception {
    HttpResponse<String> response = putTransaction(1001L, """
        {
          "amount": 10.5,
          "type": "cars"
        }
        """);

    assertEquals(200, response.statusCode());
    assertTrue(response.body().contains("\"status\":\"ok\""));
  }

  @Test
  void shouldCreateTransactionWithParent() throws Exception {
    HttpResponse<String> parentResponse = putTransaction(1002L, """
        {
          "amount": 10.5,
          "type": "cars"
        }
        """);
    assertEquals(200, parentResponse.statusCode());

    HttpResponse<String> childResponse = putTransaction(1003L, """
        {
          "amount": 20.0,
          "type": "cars",
          "parent_id": 1002
        }
        """);
    assertEquals(200, childResponse.statusCode());
    assertTrue(childResponse.body().contains("\"status\":\"ok\""));
  }

  @Test
  void shouldFailWhenParentDoesNotExist() throws Exception {
    HttpResponse<String> response = putTransaction(1004L, """
        {
          "amount": 20.0,
          "type": "cars",
          "parent_id": 9999
        }
        """);

    assertEquals(404, response.statusCode());
  }

  @Test
  void shouldFailWhenIdAlreadyExists() throws Exception {
    HttpResponse<String> firstResponse = putTransaction(1005L, """
        {
          "amount": 20.0,
          "type": "cars"
        }
        """);
    assertEquals(200, firstResponse.statusCode());

    HttpResponse<String> secondResponse = putTransaction(1005L, """
        {
          "amount": 21.0,
          "type": "cars"
        }
        """);
    assertEquals(409, secondResponse.statusCode());
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
}

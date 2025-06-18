package application;

import io.muserver.MuServer;
import java.util.Map;
import org.junit.jupiter.api.*;
import java.io.*;
import java.net.*;
import service.BookingRepository;

class SimpleReservationAppTest {

  private MuServer server;
  private int port = 8080;

  @BeforeEach
  void setUp() {
    server = SimpleReservationApp.runServer(port, Map.of(2, 5, 4, 5, 6, 2));
  }

  @AfterEach
  void tearDown() {
    if (server != null) {
      server.stop();
    }
  }

  @Test
  public void book_test() throws Exception {
    //booking#1
    String payload1 = "{\"customerName\":\"Roman\",\"date\":\"2025-06-17\",\"tableSize\":2,\"timeSlotBegins\":\"02:30:00\"}";
    String response1 = send("/book", "POST", payload1);
    Assertions.assertEquals("\"SUCCESS\"", response1);//SUCCESS

    //booking#1
    String payload2 = "{\"customerName\":\"Michael\",\"date\":\"2025-06-18\",\"tableSize\":4,\"timeSlotBegins\":\"18:00:00\"}";
    String response2 = send("/book", "POST", payload2);
    Assertions.assertEquals("\"SUCCESS\"", response2);//SUCCESS

    //checking the storage state
    Assertions.assertEquals(
        "{\"2025-06-17\":[{\"customerName\":\"Roman\",\"date\":\"2025-06-17\",\"tableSize\":2,\"timeSlotBegins\":\"02:30:00\"}],"
            + "\"2025-06-18\":[{\"customerName\":\"Michael\",\"date\":\"2025-06-18\",\"tableSize\":4,\"timeSlotBegins\":\"18:00:00\"}]}",
        BookingRepository.getState());
  }

  @Test
  public void view_test() throws Exception {

    //booking#1
    String payload1 = "{\"customerName\":\"Roman\",\"date\":\"2025-06-17\",\"tableSize\":2,\"timeSlotBegins\":\"02:30:00\"}";
    String response1 = send("/book", "POST", payload1);
    Assertions.assertEquals("\"SUCCESS\"", response1);

    //booking#2
    String payload2 = "{\"customerName\":\"Michael\",\"date\":\"2025-06-17\",\"tableSize\":4,\"timeSlotBegins\":\"18:00:00\"}";
    String response2 = send("/book", "POST", payload2);
    Assertions.assertEquals("\"SUCCESS\"", response2);

    //view_request
    String response3 = send("/view/2025-06-17", "GET", null);
    Assertions.assertEquals("["
            + "{\"customerName\":\"Roman\",\"date\":\"2025-06-17\",\"tableSize\":2,\"timeSlotBegins\":\"02:30:00\"},"
            + "{\"customerName\":\"Michael\",\"date\":\"2025-06-17\",\"tableSize\":4,\"timeSlotBegins\":\"18:00:00\"}"
            + "]",
        response3);

  }

  @Test
  public void available_test() throws Exception {

    //booking#1
    String payload1 = "{\"customerName\":\"Roman\",\"date\":\"2025-06-17\",\"tableSize\":2,\"timeSlotBegins\":\"14:20:00\"}";
    String response1 = send("/book", "POST", payload1);
    Assertions.assertEquals("\"SUCCESS\"", response1);

    //booking#2
    String payload2 = "{\"customerName\":\"Michael\",\"date\":\"2025-06-17\",\"tableSize\":4,\"timeSlotBegins\":\"18:00:00\"}";
    String response2 = send("/book", "POST", payload2);
    Assertions.assertEquals("\"SUCCESS\"", response2);

    //view_request
    String response3 = send("/available/2025-06-17", "GET", null);
    Assertions.assertEquals(
        "{\"12:00\":{\"2\":5,\"4\":5,\"6\":2},\"12:20\":{\"2\":5,\"4\":5,\"6\":2},\"12:40\":{\"2\":5,\"4\":5,\"6\":2},"
            + "\"13:00\":{\"2\":5,\"4\":5,\"6\":2},\"13:20\":{\"2\":5,\"4\":5,\"6\":2},\"13:40\":{\"2\":5,\"4\":5,\"6\":2},"
            + "\"14:00\":{\"2\":5,\"4\":5,\"6\":2},\"14:20\":{\"2\":4,\"4\":5,\"6\":2},\"14:40\":{\"2\":4,\"4\":5,\"6\":2},"
            + "\"15:00\":{\"2\":4,\"4\":5,\"6\":2},\"15:20\":{\"2\":4,\"4\":5,\"6\":2},\"15:40\":{\"2\":4,\"4\":5,\"6\":2},"
            + "\"16:00\":{\"2\":4,\"4\":5,\"6\":2},\"16:20\":{\"2\":5,\"4\":5,\"6\":2},\"16:40\":{\"2\":5,\"4\":5,\"6\":2},"
            + "\"17:00\":{\"2\":5,\"4\":5,\"6\":2},\"17:20\":{\"2\":5,\"4\":5,\"6\":2},\"17:40\":{\"2\":5,\"4\":5,\"6\":2},"
            + "\"18:00\":{\"2\":5,\"4\":4,\"6\":2},\"18:20\":{\"2\":5,\"4\":4,\"6\":2},\"18:40\":{\"2\":5,\"4\":4,\"6\":2},"
            + "\"19:00\":{\"2\":5,\"4\":4,\"6\":2},\"19:20\":{\"2\":5,\"4\":4,\"6\":2},\"19:40\":{\"2\":5,\"4\":4,\"6\":2},"
            + "\"20:00\":{\"2\":5,\"4\":5,\"6\":2}}",
        response3);

  }

  private String send(String path, String method, String payload) throws IOException {
    URL url = new URL("http://localhost:" + port + path);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

    conn.setRequestMethod(method);
    conn.setDoOutput(true);
    conn.setRequestProperty("Content-Type", "application/json");

    if (payload != null) {
      try (OutputStream os = conn.getOutputStream()) {
        os.write(payload.getBytes());
      }
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
      return reader.readLine();
    }
  }


}
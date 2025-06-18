package application;

import io.muserver.*;
import java.util.Map;
import rest.BookingController;
import rest.ViewAvailableController;
import rest.ViewBookingsController;
import service.BookingRepository;

public class SimpleReservationApp {

  public static void main(String[] args) {
    MuServer server = runServer(8080, Map.of(2, 5, 4, 5, 6, 2));
    System.out.println("Started server at " + server.uri());
  }

  public static MuServer runServer(int port, Map<Integer, Integer> tables) {
    BookingRepository.reset(tables);
    return MuServerBuilder.httpServer()
        .withHttpPort(port)
        .addHandler(Method.POST, "/book", BookingController.getInstance()::handle)
        .addHandler(Method.GET, "/view/{date}", ViewBookingsController.getInstance()::handle)
        .addHandler(Method.GET, "/available/{date}", ViewAvailableController.getInstance()::handle)
        .start();
  }

}

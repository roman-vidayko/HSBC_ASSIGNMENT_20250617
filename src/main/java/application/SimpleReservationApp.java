package application;

import io.muserver.*;
import rest.BookingController;
import rest.ViewBookingsController;
import service.BookingRepository;

public class SimpleReservationApp {

  public static void main(String[] args) {
    MuServer server = runServer(8080);
    System.out.println("Started server at " + server.uri());
  }

  public static MuServer runServer(int port) {
    BookingRepository.reset();
    return MuServerBuilder.httpServer()
        .withHttpPort(port)
        .addHandler(Method.POST, "/book", BookingController.getInstance()::handle)
        .addHandler(Method.GET, "/view/{date}", ViewBookingsController.getInstance()::handle)
        .start();
  }

}

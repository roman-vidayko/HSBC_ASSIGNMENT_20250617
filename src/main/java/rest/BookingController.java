package rest;

import io.muserver.*;
import java.util.Map;
import lombok.Getter;
import model.Booking;
import service.*;

public class BookingController implements RouteHandler {

  @Getter
  private static final BookingController instance = new BookingController();

  @Override
  public void handle(MuRequest request, MuResponse response, Map<String, String> pathParams)
      throws Exception {

    final Booking requested = DataMapper.getReader().readValue(
        request.readBodyAsString(), Booking.class);

    response.contentType("application/json");
    response.write(
        DataMapper.getWriter().writeValueAsString(
            BookingRepository.set(requested.getDate(), requested) ? "SUCCESS" : "FAILED"));
  }
}

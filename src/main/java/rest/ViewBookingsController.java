package rest;

import io.muserver.*;
import java.time.LocalDate;
import java.util.Map;
import lombok.Getter;
import service.BookingRepository;
import service.DataMapper;

public class ViewBookingsController implements RouteHandler {

  @Getter
  private static final ViewBookingsController instance = new ViewBookingsController();

  @Override
  public void handle(MuRequest request, MuResponse response, Map<String, String> pathParams)
      throws Exception {

    final LocalDate requested = LocalDate.parse(pathParams.get("date"));
    response.contentType("application/json");
    response.write(
        DataMapper.getWriter().writeValueAsString(BookingRepository.get(requested)));

  }
}

package rest;

import io.muserver.MuRequest;
import io.muserver.MuResponse;
import io.muserver.RouteHandler;
import java.time.LocalDate;
import java.util.Map;
import lombok.Getter;
import service.BookingRepository;
import service.DataMapper;

public class ViewAvailableController implements RouteHandler {

  @Getter
  private static final ViewAvailableController instance = new ViewAvailableController();

  @Override
  public void handle(MuRequest request, MuResponse response, Map<String, String> pathParams)
      throws Exception {
    final LocalDate requested = LocalDate.parse(pathParams.get("date"));

    response.contentType("application/json");
    response.write(
        DataMapper.getWriter().writeValueAsString(BookingRepository.getAvailableSlots(requested)));

  }
}

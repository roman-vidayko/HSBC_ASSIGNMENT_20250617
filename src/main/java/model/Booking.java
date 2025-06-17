package model;

import java.time.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Booking {

  private String customerName;
  private int tableSize;
  private LocalDate date;
  private LocalTime timeSlotBegins;

}

package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.util.*;
import model.Booking;

/**
 * Since there are no requirements regarding the number of tables of a specific size,
 * a table-availability check is not required.
 */
public class BookingRepository {

  private static long timeslotDurationHours = 2;// slots are fixed at 2-hour intervals.

  private static Map<LocalDate, Collection<Booking>> storage = new HashMap<>();

  /**
   * Adding a booking
   */
  public static boolean set(LocalDate date, Booking booking) {
    return storage.computeIfAbsent(date, k -> new ArrayList<>()).add(booking);
  }

  /**
   * Getting a collection of bookings for the specified date
   */
  public static Collection<Booking> get(LocalDate date) {
    return Collections.unmodifiableCollection(
        storage.get(date) == null ? new ArrayList<>() : storage.get(date));
  }

  /**
   * Repo initialization
   */
  public static void reset() {
    storage.clear();
  }

  /**
   * Getting status (method purposed for testing)
   */
  public static String getState() throws JsonProcessingException {
    return DataMapper.getWriter().writeValueAsString(storage);
  }

}

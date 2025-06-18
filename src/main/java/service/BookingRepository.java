package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import model.Booking;

public class BookingRepository {

  private static long timeslotDurationHours = 2;// slots are fixed at 2-hour intervals.

  private static Map<LocalDate, Collection<Booking>> storage = new HashMap<>();
  private static Map<Integer, Integer> tables = Map.of();

  /**
   * Availability check
   */
  public static boolean isAvailable(LocalDate date, LocalTime timeSlotBegins, int tableSize) {
    long occupied = countOccupied(date, timeSlotBegins, tableSize);
    return occupied < tables.get(tableSize);
  }

  /**
   * Counts available tables for specified date, timeSlotBegins and tableSize
   */
  private static long countOccupied(LocalDate date, LocalTime timeSlotBegins, int tableSize) {
    long occupied = get(date).stream()
        .filter(b -> b.getTableSize() == tableSize)
        .filter(b -> {
          LocalTime timeSlotEnds = b.getTimeSlotBegins().plusHours(timeslotDurationHours);
          return (b.getTimeSlotBegins().isBefore(timeSlotBegins) && timeSlotBegins.isBefore(
              timeSlotEnds))
              || (b.getTimeSlotBegins().equals(timeSlotBegins));
        }).count();
    return occupied;
  }

  /**
   * Returns an ordered map of timeslots for specified date (default hours, default delta), having a
   * table[size, availableCount] for each timeslot
   */
  public static TreeMap<LocalTime, TreeMap<Integer, Long>> getAvailableSlots(LocalDate date) {
    return getAvailableSlots(date, LocalTime.of(12, 0), LocalTime.of(20, 0), 20);
  }

  /**
   * Returns an ordered map of timeslots for specified date, having a table[size, availableCount]
   * for each timeslot
   */
  public static TreeMap<LocalTime, TreeMap<Integer, Long>> getAvailableSlots(LocalDate date,
      LocalTime first, LocalTime last, long deltaMinutes) {
    TreeMap<LocalTime, TreeMap<Integer, Long>> availableSlots = new TreeMap<>();
    LocalTime current = LocalTime.from(first);
    while (current.isBefore(last) || current.equals(last)) {
      TreeMap<Integer, Long> availableTables = new TreeMap<>();
      for (Integer size : tables.keySet()) {
        availableTables.put(size, tables.get(size) - countOccupied(date, current, size));
      }
      availableSlots.put(current, availableTables);
      current = current.plusMinutes(deltaMinutes);
    }
    return availableSlots;
  }

  /**
   * Adding a booking
   */
  public static synchronized boolean set(LocalDate date, Booking booking) {
    if (isAvailable(date, booking.getTimeSlotBegins(), booking.getTableSize())) {
      return storage.computeIfAbsent(date, k -> new ArrayList<>()).add(booking);
    }
    return false;
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
  public static void reset(Map<Integer, Integer> tables) {
    BookingRepository.tables = Collections.unmodifiableMap(tables);
    storage.clear();
  }

  /**
   * Getting status (method purposed for testing)
   */
  public static String getState() throws JsonProcessingException {
    return DataMapper.getWriter().writeValueAsString(storage);
  }

}

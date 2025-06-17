package service;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.core.JsonParser.Feature.ALLOW_SINGLE_QUOTES;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES;
import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;
import static com.fasterxml.jackson.databind.SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class DataMapper {

  private static final ObjectMapper mapper = new ObjectMapper()
      .configure(FAIL_ON_IGNORED_PROPERTIES, false)
      .configure(ALLOW_SINGLE_QUOTES, true)
      .configure(ORDER_MAP_ENTRIES_BY_KEYS, true)
      .configure(SORT_PROPERTIES_ALPHABETICALLY, true)
      .setSerializationInclusion(NON_NULL);

  static {
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  public static ObjectReader getReader() {
    return mapper.reader();
  }

  public static ObjectWriter getWriter() {
    return mapper.writer();
  }

}

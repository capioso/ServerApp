package networksTwo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperUtils {
    private static ObjectMapper INSTANCE;

    private ObjectMapperUtils() {
    }

    public static ObjectMapper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ObjectMapper();
        }
        return INSTANCE;
    }
}

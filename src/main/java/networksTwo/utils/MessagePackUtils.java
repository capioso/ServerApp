package networksTwo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.msgpack.jackson.dataformat.MessagePackFactory;

public class MessagePackUtils {
    private static ObjectMapper INSTANCE;

    private MessagePackUtils() {
    }

    public static ObjectMapper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ObjectMapper(new MessagePackFactory());
        }
        return INSTANCE;
    }
}

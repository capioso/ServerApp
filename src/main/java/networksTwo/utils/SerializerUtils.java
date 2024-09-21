package networksTwo.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import networksTwo.domain.model.Response;

public class SerializerUtils {

    public static String handleString(String title, String body){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Response responseObject = new Response(title, body);
            return objectMapper.writeValueAsString(responseObject);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}

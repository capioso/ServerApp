package networksTwo.utils;

import networksTwo.domain.model.Response;

public class SerializerUtils {

    public static String handleString(String title, Object body){
        try {
            Response responseObject = new Response(title, body);
            return ObjectMapperUtils.getInstance().writeValueAsString(responseObject);
        }catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
}

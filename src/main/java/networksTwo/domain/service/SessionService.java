package networksTwo.domain.service;

import networksTwo.adapter.out.ActiveSessions;
import networksTwo.domain.model.Session;

import java.io.PrintWriter;
import java.util.UUID;

public class SessionService {

    public static PrintWriter getOutByUserId(UUID userId){
        for (Session session: ActiveSessions.activeUsers.values()){
            if(session.getUserId().equals(userId)){
                return session.getOut();
            }
        }
        return null;
    }
}

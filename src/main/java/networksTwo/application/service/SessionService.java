package networksTwo.application.service;

import networksTwo.domain.persistence.SessionRepository;
import networksTwo.domain.model.Session;

import java.io.PrintWriter;
import java.util.UUID;

public class SessionService {

    public static PrintWriter getOutByUserId(UUID userId){
        for (Session session: SessionRepository.activeUsers.values()){
            if(session.getUserId().equals(userId)){
                return session.getOut();
            }
        }
        return null;
    }
}

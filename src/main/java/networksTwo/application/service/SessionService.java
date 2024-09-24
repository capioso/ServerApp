package networksTwo.application.service;

import networksTwo.domain.model.Session;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.UUID;

import static networksTwo.domain.repository.SessionRepository.ACTIVE_USERS;

public class SessionService {

    public static Optional<Boolean> setUserIdBySessionId(UUID sessionId, UUID userId) {
        try {
            ACTIVE_USERS.get(sessionId).setUserId(userId);
            return Optional.of(true);
        }catch (Exception e) {
            return Optional.empty();
        }
    }

    public static Optional<PrintWriter> getOutByUserId(UUID id){
        try {
            return ACTIVE_USERS.values().stream()
                    .filter(session -> session.getUserId().equals(id))
                    .map(Session::getOut)
                    .findFirst();
        }catch (Exception e) {
            return Optional.empty();
        }
    }
}

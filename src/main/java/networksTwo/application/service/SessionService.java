package networksTwo.application.service;

import networksTwo.domain.model.Session;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.UUID;

import static networksTwo.domain.repository.SessionRepository.ACTIVE_USERS;

public class SessionService {

    public static Optional<PrintWriter> getOutByUserId(UUID id){
        return ACTIVE_USERS.values().stream()
                .filter(session -> session.getUserId().equals(id))
                .map(Session::getOut)
                .findFirst();
    }
}

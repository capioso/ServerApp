package networksTwo.domain.repository;

import networksTwo.domain.model.Session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionRepository {
    public static ConcurrentHashMap<UUID, Session> ACTIVE_USERS = new ConcurrentHashMap<>();
}

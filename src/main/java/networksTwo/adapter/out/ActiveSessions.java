package networksTwo.adapter.out;

import networksTwo.domain.model.Session;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveSessions {
    public static ConcurrentHashMap<UUID, Session> activeUsers = new ConcurrentHashMap<>();
}

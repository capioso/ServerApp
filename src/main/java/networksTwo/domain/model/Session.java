package networksTwo.domain.model;

import java.io.OutputStream;
import java.util.UUID;

public class Session {
    private UUID userId;
    private final OutputStream out;

    public Session(UUID userId, OutputStream out) {
        this.userId = userId;
        this.out = out;
    }

    public OutputStream getOut() {
        return out;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}

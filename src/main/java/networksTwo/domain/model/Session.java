package networksTwo.domain.model;

import java.io.PrintWriter;
import java.util.UUID;

public class Session {
    private UUID userId;
    private final PrintWriter out;

    public Session(UUID userId, PrintWriter out) {
        this.userId = userId;
        this.out = out;
    }

    public PrintWriter getOut() {
        return out;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}

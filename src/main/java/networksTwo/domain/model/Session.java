package networksTwo.domain.model;

import java.io.PrintWriter;
import java.util.UUID;

public class Session {
    private UUID userId;
    private PrintWriter out;

    public Session (UUID userId, PrintWriter out){
        this.userId = userId;
        this.out = out;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }
}

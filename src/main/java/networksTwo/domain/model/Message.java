package networksTwo.domain.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
public class Message {
    @Id
    @Column(nullable = false, unique = true)
    private UUID id;

    @Column(nullable = false)
    private UUID sender;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false, updatable = false)
    private String createdAt;

    @ManyToOne
    @JoinColumn(name = "chatId", nullable = false)
    private Chat chat;


    public void setSender(UUID sender) {
        this.sender = sender;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Chat getChat() {
        return chat;
    }

    public String getContent() {
        return content;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getId() {
        return id;
    }

    public UUID getSender() {
        return sender;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }
}

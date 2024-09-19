package networksTwo.domain.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private UUID id;

    @Column(nullable = false)
    private UUID sender;

    @Column(nullable = false)
    private String content;

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

    public UUID getId() {
        return id;
    }

    public UUID getSender() {
        return sender;
    }
}

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
    private UUID receiver;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "chatId", nullable = false)
    private Chat chat;
}

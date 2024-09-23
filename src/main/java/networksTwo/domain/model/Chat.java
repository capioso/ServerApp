package networksTwo.domain.model;

import jakarta.persistence.*;
import jakarta.transaction.Transactional;

import java.util.*;

@Entity
public class Chat {
    @Id
    @Column(nullable = false)
    private UUID id;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Message> messages = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "ownerId", nullable = false)
    private User owner;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "chat_user",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users = new ArrayList<>();




    @Transactional
    public List<User> getUsers() {
        return users;
    }

    @Transactional
    public List<Message> getMessages() {
        return messages;
    }

    public User getOwner() {
        return owner;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public UUID getId() {
        return id;
    }
}

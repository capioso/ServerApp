package networksTwo.application.service;

import networksTwo.domain.repository.ChatRepository;
import networksTwo.domain.model.Chat;
import networksTwo.domain.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    @Autowired
    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Transactional
    public Optional<Boolean> createChat(Chat chat) {
        return chatRepository.save(chat);
    }

    @Transactional
    public Optional<Boolean> updateChat(Chat chat) {
        return chatRepository.save(chat);
    }

    @Transactional
    public Optional<Chat> getById(UUID id) {
        return chatRepository.findById(id);
    }

    @Transactional
    public Optional<List<UUID>> getReceptorsByChatWithoutSender(Chat chat, UUID sender) {
        List<UUID> receivers = chat.getUsers().stream()
                .map(User::getId)
                .filter(id -> !id.equals(sender))
                .collect(Collectors.toList());
        return receivers.isEmpty() ? Optional.empty() : Optional.of(receivers);
    }

    @Transactional
    public Optional<List<UUID>> getReceptorsByChatWithSender(Chat chat) {
        List<UUID> receivers = chat.getUsers().stream()
                .map(User::getId)
                .collect(Collectors.toList());
        return receivers.isEmpty() ? Optional.empty() : Optional.of(receivers);
    }

    @Transactional
    public Boolean isGroupChat(Chat chat, String ownerUsername) {
        List<String> filteredUsers = getTitlesByChatWithoutOwner(chat, ownerUsername)
                .orElseThrow(() -> new RuntimeException("Filtered Users by chat not executed."));
        return filteredUsers.size() > 1;
    }

    @Transactional
    public Optional<List<String>> getTitlesByChatWithoutOwner(Chat chat, String ownerUsername) {
        List<String> filteredUsers = chat.getUsers().stream()
                .map(User::getUsername)
                .filter(username -> !username.equals(ownerUsername))
                .toList();
        return filteredUsers.isEmpty() ? Optional.empty() : Optional.of(filteredUsers);
    }

    @Transactional
    public Optional<List<String>> getTitlesByChat(Chat chat) {
        List<String> filteredUsers = chat.getUsers().stream()
                .map(User::getUsername)
                .toList();
        return filteredUsers.isEmpty() ? Optional.empty() : Optional.of(filteredUsers);
    }
}

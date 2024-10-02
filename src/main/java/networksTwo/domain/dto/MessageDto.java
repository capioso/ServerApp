package networksTwo.domain.dto;

import java.util.UUID;

public record MessageDto(UUID messageId, String sender, String content, String timestamp) {
}

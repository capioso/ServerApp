package networksTwo.domain.dto;

import java.util.UUID;

public record MessageToSendDto(
        UUID chatId,
        UUID messageId,
        String usernameSender,
        String content
) {
}

package com.example.ssauc;


import com.example.ssauc.user.chat.entity.ChatParticipant;
import com.example.ssauc.user.chat.entity.ChatRoom;
import com.example.ssauc.user.chat.repository.ChatParticipantRepository;
import com.example.ssauc.user.chat.repository.ChatRoomRepository;
import com.example.ssauc.user.login.entity.Users;
import com.example.ssauc.user.login.repository.UsersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatParticipantRepositoryTest {

    @Autowired
    private ChatParticipantRepository chatParticipantRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Test
    void testSaveAndFindChatParticipant() {
        // Given
        Users user = usersRepository.save(Users.builder()
                .userName("participantUser")
                .email("participant@example.com")
                .password("securepassword")
                .createdAt(LocalDateTime.now())
                .build());

        ChatRoom chatRoom = chatRoomRepository.save(ChatRoom.builder()
                .createdAt(LocalDateTime.now())
                .build());

        ChatParticipant participant = ChatParticipant.builder()
                .user(user)
                .chatRoom(chatRoom)
                .joinedAt(LocalDateTime.now())
                .build();

        // When
        ChatParticipant savedParticipant = chatParticipantRepository.save(participant);
        ChatParticipant foundParticipant = chatParticipantRepository.findById(savedParticipant.getId()).orElse(null);

        // Then
        assertThat(foundParticipant).isNotNull();
        assertThat(foundParticipant.getUser().getUserId()).isEqualTo(user.getUserId());
    }
}

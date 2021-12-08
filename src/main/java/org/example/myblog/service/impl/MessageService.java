package org.example.myblog.service.impl;

import org.example.myblog.domain.User;
import org.example.myblog.domain.dto.MessageDto;
import org.example.myblog.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Page<MessageDto> messageList(Pageable pageable, String filter, User user) {
        if (filter != null && !filter.isEmpty())
            return messageRepository.findByTag(filter, pageable, user);

        return messageRepository.findAll(pageable, user);
    }

    public Page<MessageDto> messageListForUser(Pageable pageable, User author, User currentUser) {
        return messageRepository.findByUser(pageable, author, currentUser);
    }
}

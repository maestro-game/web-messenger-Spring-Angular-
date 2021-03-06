package ru.itis.semwork3.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.semwork3.dto.message.InnerMessageDto;
import ru.itis.semwork3.dto.message.RemoveMessageDto;
import ru.itis.semwork3.model.Channel;
import ru.itis.semwork3.model.ContentSource;
import ru.itis.semwork3.model.Message;
import ru.itis.semwork3.model.User;
import ru.itis.semwork3.repository.ContentSourceRepository;
import ru.itis.semwork3.repository.DtoRepository;
import ru.itis.semwork3.repository.MessageRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final ContentSourceRepository sourceRepository;
    private final DtoRepository dtoRepository;
    private final Converter<Message, InnerMessageDto> converter;

    @Override
    public Optional<InnerMessageDto> saveNew(String text, String userId, String sourceId) {
        var source = sourceRepository.findByIdAndMembersContaining(sourceId, User.builder().id(userId).build());
        if (source.isPresent()) {
            if (!(source.get() instanceof Channel) || ((Channel) source.get()).getAdmin().getId().equals(userId)) {
                return messageRepository.findById(messageRepository.save(Message.builder()
                        .text(text)
                        .author(source.get() instanceof Channel ? source.get() : User.builder().id(userId).build())
                        .source(ContentSource.builder().id(sourceId).build())
                        .build()).getId()).map(converter::convert);
            }
        }
        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<InnerMessageDto> saveNewRepost(Long messageId, String userId, String sourceId) {
        var source = sourceRepository.findByIdAndMembersContaining(sourceId, User.builder().id(userId).build());
        if (source.isPresent()) {
            if (!(source.get() instanceof Channel) || ((Channel) source.get()).getAdmin().getId().equals(userId)) {
                var message = messageRepository.findById(messageId).get();
                return messageRepository.findById(messageRepository.save(Message.builder()
                        .text(message.getText())
                        .author(source.get() instanceof Channel ? source.get() : User.builder().id(userId).build())
                        .source(ContentSource.builder().id(sourceId).build())
                        .from(message.getFrom() == null ? message.getAuthor() : message.getFrom())
                        .build()).getId()).map(converter::convert);
            }
        }
        return Optional.empty();
    }

    @Override
    public RemoveMessageDto delete(Long id, String userId, String sourceId) {
        return messageRepository.deleteByIdAndAuthorAndSource_Id(id, User.builder().id(userId).build(), sourceId) == 1 ?
                dtoRepository.findRemoveMessageDtoBySourceIdAndMessageId(sourceId, id) : null;
    }
}

package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.CommentDto;
import ru.practicum.ewm.entity.Comment;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.UserService;

@Mapper(componentModel = "spring", uses = {UserService.class, EventService.class})
public interface CommentMapper {
    @Mapping(source = "commentator.id", target = "commentatorId")
    @Mapping(source = "event.id", target = "eventId")
    CommentDto commentToCommentDto(Comment comment);

    @Mapping(source = "commentatorId", target = "commentator")
    @Mapping(source = "eventId", target = "event")
    Comment commentDtoToComment(CommentDto commentDto);
}
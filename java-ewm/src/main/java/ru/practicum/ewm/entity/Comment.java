package ru.practicum.ewm.entity;

import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import ru.practicum.ewm.other.Status;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder


@FilterDef(name = "dateComFilter",
        parameters = {
                @ParamDef(name = "rangeStart", type = "java.time.LocalDateTime"),
                @ParamDef(name = "rangeEnd", type = "java.time.LocalDateTime")})
@Filter(name = "dateComFilter", condition = "created >= :rangeStart and created <= :rangeEnd")

@FilterDef(name = "statusComFilter", parameters = @ParamDef(name = "status", type = "string"))
@Filter(name = "statusComFilter", condition = "status =:status")

@FilterDef(name = "eventsComFilter", parameters = @ParamDef(name = "eventIds", type = "java.lang.Long"))
@Filter(name = "eventsComFilter", condition = "event_id in (:eventIds)")


@Entity
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private LocalDateTime created;
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @ManyToOne
    @JoinColumn(name = "commentator_id")
    private User commentator;
    @Enumerated(EnumType.STRING)
    private Status status;

}


package ru.practicum.ewm.entity;

import lombok.*;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import ru.practicum.ewm.other.State;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

@FilterDef(name = "paidFilter", parameters = @ParamDef(name = "paid", type = "boolean"))
@Filter(name = "paidFilter", condition = "paid =:paid")

@FilterDef(name = "dateFilter",
        parameters = {
                @ParamDef(name = "rangeStart", type = "java.time.LocalDateTime"),
                @ParamDef(name = "rangeEnd", type = "java.time.LocalDateTime")})
@Filter(name = "dateFilter", condition = "event_date >= :rangeStart and event_date <= :rangeEnd")

@FilterDef(name = "stateFilter", parameters = @ParamDef(name = "state", type = "string"))
@Filter(name = "stateFilter", condition = "state =:state")

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private String description;
    private LocalDateTime eventDate;
    private Double lat;
    private Double lon;
    private LocalDateTime createdOn;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;

    private Boolean paid;
    private Integer participantLimit;
    private LocalDateTime publishedOn;
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private State state;

}
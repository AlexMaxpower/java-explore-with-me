package ru.practicum.ewm.other;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public interface Pager {

    default Pageable getPage(int from, int size, String sort, Sort.Direction direction) {
        Sort sortBy = Sort.by(direction, sort);
        return PageRequest.of((from / size), size, sortBy);
    }
}

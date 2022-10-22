package ru.practicum.ewm.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {

    private JacksonTester<UserDto> json;
    private UserDto userDto;

    public UserDtoTest(@Autowired JacksonTester<UserDto> json) {
        this.json = json;
    }

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(
                1L,
                "alex@alex.ru",
                "Alex"
        );
    }

    @Test
    void testJsonUserDto() throws Exception {

        JsonContent<UserDto> result = json.write(userDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Alex");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("alex@alex.ru");
    }
}
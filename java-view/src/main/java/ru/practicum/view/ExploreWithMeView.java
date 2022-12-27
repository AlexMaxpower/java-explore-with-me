package ru.practicum.view;

import feign.codec.ErrorDecoder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import ru.practicum.view.exception.FeignErrorDecoder;

@EnableFeignClients
@SpringBootApplication
public class ExploreWithMeView {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }

    public static void main(String[] args) {
        SpringApplication.run(ExploreWithMeView.class, args);
    }

}
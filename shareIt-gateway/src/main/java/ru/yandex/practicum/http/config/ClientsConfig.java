package ru.yandex.practicum.http.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import ru.yandex.practicum.http.clients.*;

@Configuration
@RequiredArgsConstructor
public class ClientsConfig {

    private final HttpServiceProxyFactory factory;

    @Bean
    public UserHttpClient userHttpClient() {
        return factory.createClient(UserHttpClient.class);
    }

    @Bean
    public ItemHttpClient itemHttpClient() {
        return factory.createClient(ItemHttpClient.class);
    }

    @Bean
    public CommentHttpClient commentHttpClient() {
        return factory.createClient(CommentHttpClient.class);
    }

    @Bean
    public BookingHttpClient bookingHttpClient() {
        return factory.createClient(BookingHttpClient.class);
    }

    @Bean
    public ItemRequestHttpClient itemRequestHttpClient() {
        return factory.createClient(ItemRequestHttpClient.class);
    }

}


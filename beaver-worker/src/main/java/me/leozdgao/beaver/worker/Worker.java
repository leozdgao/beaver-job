package me.leozdgao.beaver.worker;

import lombok.Builder;
import lombok.Data;

import java.net.URI;
import java.util.UUID;

@Data
@Builder
public class Worker {
    private final String id;
    private final String host;
    private final int port;
    private boolean enabled;

    public static Worker of(String url) {
        URI uri = URI.create(url);
        return Worker.builder()
                .id(UUID.randomUUID().toString())
                .host(uri.getHost())
                .port(uri.getPort())
                .build();
    }
}

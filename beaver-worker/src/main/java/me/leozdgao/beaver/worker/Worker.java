package me.leozdgao.beaver.worker;

import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.UUID;

public class Worker {
    @Getter
    private final String id;

    @Getter
    private final InetSocketAddress address;

    @Getter
    private final String scope;

    @Getter
    @Setter
    private Long registerTime;

    @Getter
    @Setter
    private Long lastConnectionTime;


    @Getter
    @Setter
    private boolean enabled = true;

    public Worker(String scope, String host, int port) {
        this(scope, host, port, null);
    }

    public Worker(String scope, String host, int port, String id) {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }

        if (scope == null) {
            throw new IllegalArgumentException("worker scope 不可为空");
        }

        this.id = id;
        this.address = new InetSocketAddress(host, port);
        this.scope = scope;
    }

    public static Worker of(String scope, String url) {
        URI uri = URI.create(url);
        return new Worker(scope, uri.getHost(), uri.getPort());
    }
}

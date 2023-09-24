package me.leozdgao.beaver.worker.protocol;

import lombok.Getter;

/**
 * @author leozdgao
 */
@Getter
public class WorkerResponseException extends RuntimeException {
    private final String code;
    private final String traceId;

    public WorkerResponseException(String traceId, String code, String msg) {
        super(msg);

        this.traceId = traceId;
        this.code = code;
    }
}

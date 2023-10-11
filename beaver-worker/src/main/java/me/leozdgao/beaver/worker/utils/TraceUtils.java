package me.leozdgao.beaver.worker.utils;

import org.slf4j.MDC;

/**
 * @author leozdgao
 */
public class TraceUtils {
    public static String getTraceId() {
        return MDC.get("EagleEye-TraceID");
    }

    public static void setTraceId(String traceId) {
        MDC.put("EagleEye-TraceID", traceId);
    }
}

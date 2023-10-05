package me.leozdgao.beaver.adaptor.web.filter;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author leozdgao
 */
@Component
public class TracingAppenderFilter extends OncePerRequestFilter {
    private static final String TRACE_ID_HEADER = "X-TraceId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.addHeader(TRACE_ID_HEADER, TraceContext.traceId());
        filterChain.doFilter(request, response);
    }
}

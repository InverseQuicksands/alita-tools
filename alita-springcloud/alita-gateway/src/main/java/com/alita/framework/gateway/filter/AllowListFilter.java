package com.alita.framework.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;


/**
 * 白名单过滤器.
 *
 * <p>Gateway 网关层的白名单实现原理是在过滤器内判断请求地址是否符合白名单，如果通过则跳过当前过滤器。
 * 某些请求不需要携带 token 值（如：上传/下载文件，登录等请求）。
 *
 * <p>白名单规则支持正则表达式。
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-16 14:30
 */
public class AllowListFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AllowListFilter.class);

    private static final String TOKEN_KEY = "token";

//    private DataSource dataSource;
//
//    public AllowListFilter(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }

    /**
     * Process the Web request and (optionally) delegate to the next {@code WebFilter}
     * through the given {@link GatewayFilterChain}.
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().value();
        logger.debug("request path: {}", requestPath);
        // 判断是否符合白名单
        if (validateAllowList(requestPath)) {
            return chain.filter(exchange);
        }
        List<String> tokenList = exchange.getRequest().getHeaders().get(TOKEN_KEY);
        logger.debug("token: {}", tokenList);
        if (CollectionUtils.isEmpty(tokenList) || tokenList.get(0).trim().isEmpty()) {
            ServerHttpResponse response = exchange.getResponse();
            // 错误信息
            byte[] data = "Token must not be null".getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(data);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            return response.writeWith(Mono.just(buffer));
        }
        return chain.filter(exchange);
    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }


    public boolean validateAllowList(String requestPath) {
//        List<AllowList> allowList = dataSource.getAllowList();
//        boolean match = allowList.stream().anyMatch(allow ->
//                requestPath.contains(allow.getPath()) || requestPath.matches(allow.getPath()));
//        return match;
        return false;
    }
}

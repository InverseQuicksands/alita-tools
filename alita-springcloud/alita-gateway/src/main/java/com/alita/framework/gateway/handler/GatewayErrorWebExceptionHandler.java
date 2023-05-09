package com.alita.framework.gateway.handler;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * CustomerErrorWebExceptionHandler
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2023-03-19 14:27
 */
public class GatewayErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler implements Ordered {


    public GatewayErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                           WebProperties.Resources resources,
                                           ErrorProperties errorProperties,
                                           ApplicationContext applicationContext) {

        super(errorAttributes, resources, errorProperties, applicationContext);
    }

    /**
     * Create a {@link RouterFunction} that can route and handle errors as JSON responses
     * or HTML views.
     * <p>
     * If the returned {@link RouterFunction} doesn't route to a {@code HandlerFunction},
     * the original exception is propagated in the pipeline and can be processed by other
     * {@link WebExceptionHandler}s.
     *
     * @param errorAttributes the {@code ErrorAttributes} instance to use to extract error
     *                        information
     * @return a {@link RouterFunction} that routes and handles errors
     */
    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    /**
     * Handle the given exception. A completion signal through the return value
     * indicates error handling is complete while an error signal indicates the
     * exception is still not handled.
     *
     * @param exchange the current exchange
     * @param throwable the exception to handle
     * @return {@code Mono<Void>} to indicate when exception handling is complete
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable throwable) {
        ServerHttpRequest request = exchange.getRequest();
        String rawQuery = request.getURI().toString();
        String query = StringUtils.hasText(rawQuery) ? "?" + rawQuery : "";
        String path = request.getPath() + query ;
        String message ;
        HttpStatus status = determineStatus(throwable);
        // 通过状态码自定义异常信息
        if (status.value() == 404) {
            message = "访问路径不存在或断言不匹配！";
        } else {
            message = "路由服务异常！";
        }
        ServerHttpResponse response = exchange.getResponse();
        byte[] data = message.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(data);
        response.setStatusCode(status);
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return response.writeWith(Mono.just(buffer));
    }

    @Nullable
    protected HttpStatus determineStatus(Throwable ex) {
//        if (ex instanceof ResponseStatusException) {
//            return ((ResponseStatusException) ex).get();
//        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
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
        return -1;
    }
}

package com.alita.framework.event.context.handler;

import com.alita.framework.event.context.event.DisruptorBindEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.Map;

public class PathMatchingHandlerChainResolver {

    private static final Logger logger = LoggerFactory.getLogger(PathMatchingHandlerChainResolver.class);

    private final Map<String, DisruptorHandler<DisruptorBindEvent>> disruptorHandler;

    private final Map<String, String> handlerDefinition;

    public PathMatchingHandlerChainResolver(Map<String, DisruptorHandler<DisruptorBindEvent>> disruptorHandler,
                                            Map<String, String> handlerDefinition) {
        this.disruptorHandler = disruptorHandler;
        this.handlerDefinition = handlerDefinition;
    }

    /**
     * 路径匹配器
     */
    private PathMatcher pathMatcher = new AntPathMatcher();


    /**
     * 获取事件中表达式是否在当前应用中存在.
     *
     * @param event 数据事件
     * @return DisruptorHandler instance
     * @throws Exception
     */
    public DisruptorHandler getExecutionChain(DisruptorBindEvent event) throws Exception {
        DisruptorHandler disruptorHandler = null;

        String expression = event.getRouteExpression();
        if (StringUtils.isBlank(expression)) {
            throw new NullPointerException("Event Expression must not be null!");
        }

        for (String pattern: this.handlerDefinition.keySet()) {
            if (pathMatcher.match(pattern, expression)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Matched path pattern [{}] for expression [{}].  " +
                            "Utilizing corresponding handler chain...", pattern, expression);
                }
                String beanName = this.handlerDefinition.get(pattern);
                disruptorHandler = this.disruptorHandler.get(beanName);
            }
        }

        if (disruptorHandler == null) {
            logger.info("No matched path pattern for the current event.  Will not be processed.");
        }

        return disruptorHandler;
    }

}

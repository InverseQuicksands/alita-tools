/*
 *
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package com.alita.framework.platform.autoconfigure.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Indexed;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

/**
 * <p>
 *
 * @author Zhang Liang
 * @date 2021/2/9
 * @since 1.0
 */

@Configuration
@Indexed
public class InterceptorConfiguration implements WebMvcConfigurer {


    /**
     * Add handlers to serve static resources such as images, js, and, css
     * files from specific locations under web application root, the classpath,
     * and others.
     * 静态资源配置
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //静态资源路径 css,js,img等
//        registry.addResourceHandler("/statics/**")
//                .addResourceLocations("classpath:/statics/");
        //视图
//        registry.addResourceHandler("/templates/**")
//                .addResourceLocations("classpath:/templates/");
        //mapper.xml
//        registry.addResourceHandler("/mapper/**")
//                .addResourceLocations("classpath:/mapper/");
    }

    /**
     * Add Spring MVC lifecycle interceptors for pre- and post-processing of
     * controller method invocations and resource handler requests.
     * Interceptors can be registered to apply to all requests or be limited
     * to a subset of URL patterns.
     * 拦截器配置
     * 拦截器的执行顺序和配置顺序有关系
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new BaseInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns(//添加排除拦截路径
//                        "/index", "/login",
//                        "/logout", "/register",
//                        "/**/*.css", "/**/*.png",
//                        "/**/*.jpeg", "/**/*.jpg",
//                        "/**/*.js", "/swagger-resources/**"
//                );
    }

    /**
     * Configure cross origin requests processing.
     * 跨域配置
     *
     * @param registry
     * @since 4.2
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //配置允许跨域的路径
//        registry.addMapping("/**")
//                //配置允许访问的跨域资源的请求域名
//                .allowedOrigins("*")
//                //配置允许访问该跨域资源服务器的请求方法，如：POST、GET、PUT、DELETE等
//                .allowedMethods("PUT,POST,GET,DELETE,OPTIONS")
//                //配置允许请求header的访问，如 ：X-TOKEN
//                .allowedHeaders("*");
    }

    /**
     * Configure simple automated controllers pre-configured with the response
     * status code and/or a view to render the response body. This is useful in
     * cases where there is no need for custom controller logic -- e.g. render a
     * home page, perform simple site URL redirects, return a 404 status with
     * HTML content, a 204 with no content, and more.
     * 视图控制器配置
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //默认视图跳转
//        registry.addViewController("/").setViewName("/index");
//        registry.addViewController("/index").setViewName("/index");
//        registry.addViewController("/article").setViewName("/article");
//        registry.addViewController("/error/404").setViewName("/error/404");
//        registry.addViewController("/error/500").setViewName("/error/500");
//        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }


    /**
     * Configure the {@link HttpMessageConverter HttpMessageConverters} to use for reading or writing
     * to the body of the request or response. If no converters are added, a
     * default list of converters is registered.
     * <p><strong>Note</strong> that adding converters to the list, turns off
     * default converter registration. To simply add a converter without impacting
     * default registration, consider using the method
     * {@link #extendMessageConverters(List)} instead.
     * 消息转换器配置
     *
     * @param converters initially an empty list of converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //1.需要定义一个convert转换消息的对象;
//        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = new FastJsonHttpMessageConverter();
        //2.添加fastJson的配置信息，比如：是否要格式化返回的json数据;
//        FastJsonConfig fastJsonConfig = new FastJsonConfig();
//        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        //3处理中文乱码问题
//        List<MediaType> fastMediaTypes = new ArrayList<>();
//        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        //4.在convert中添加配置信息.
//        fastJsonHttpMessageConverter.setSupportedMediaTypes(fastMediaTypes);
//        fastJsonHttpMessageConverter.setFastJsonConfig(fastJsonConfig);
        //5.将convert添加到converters当中.
//        converters.add(fastJsonHttpMessageConverter);
    }

    /**
     * Add {@link Converter Converters} and {@link Formatter Formatters} in addition to the ones
     * registered by default.
     * 数据格式化器配置
     *
     * @param registry
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
//        registry.addFormatter(new DateFormatter("yyyy-MM-dd"));
    }

    /**
     * Configure view resolvers to translate String-based view names returned from
     * controllers into concrete {@link View}
     * implementations to perform rendering with.
     * 视图解析器配置
     *
     * @param registry
     * @since 4.1
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//        viewResolver.setPrefix("");
//        viewResolver.setSuffix(".html");
//        viewResolver.setCache(false);
//        viewResolver.setContentType("text/html;charset=UTF-8");
//        viewResolver.setOrder(0);
//        registry.viewResolver(viewResolver);
    }
}

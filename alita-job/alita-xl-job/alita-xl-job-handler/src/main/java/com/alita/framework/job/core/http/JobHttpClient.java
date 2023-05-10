package com.alita.framework.job.core.http;

import com.alita.framework.job.utils.MapUtils;
import com.alita.framework.job.utils.StringUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.apache.hc.client5.http.auth.CredentialsStore;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.ContentBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.fluent.Executor;
import org.apache.hc.client5.http.fluent.Form;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.client5.http.ssl.TrustSelfSignedStrategy;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class JobHttpClient {

    private static final Logger logger = LoggerFactory.getLogger(JobHttpClient.class);

    private static final JobHttpClient INSTANCE = new JobHttpClient();
    private static final HttpClientConfig httpClientConfig = HttpClientConfig.INSTANCE;
    private static final CloseableHttpClient httpClient = httpClientConfig.build();
    private static final Executor executor = Executor.newInstance(httpClient);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 属性为 null 不进行序列化
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        // 解析 @XmlRootElement 相关的注解
        objectMapper.registerModules(new JaxbAnnotationModule(), new JacksonXmlModule());
        // 忽略在 json 字符串中存在但 Java 对象不存在的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 当实体类没有 setter 方法时，返回一个空对象，不抛出异常
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    private JobHttpClient() {

    }

    public static JobHttpClient getInstance() {
        return INSTANCE;
    }

    public static HttpClientConfig getHttpClientConfig() {
        return httpClientConfig;
    }

    private String formatURI(String uri) {
        if (!uri.toLowerCase().startsWith("http://") && !uri.toLowerCase().startsWith("https://")){
            return "http://" + uri;
        }
        return uri;
    }


    public <T> T get(String url) throws Exception {
        return this.get(url, null);
    }

    public <T> T get(String url, Map<String, String> params) throws Exception {
        return this.get(url, null, params);
    }


    public <T> T get(String url, Map<String, String> headers, Map<String, String> params) throws Exception {
        return this.get(url, headers, params, StandardCharsets.UTF_8);
    }


    public <T> T get(String url, Map<String, String> headers, Map<String, String> params,
                     Charset charset) throws Exception {

        return (T) this.get(url, headers, params, charset, String.class);
    }


    public <T> T get(String url, Map<String, String> headers, Map<String, String> params,
                     Charset charset, Class<T> tClass) throws Exception {

        url = formatURI(url);
        Form form = Form.form();
        String param = null;
        // ？号拼接参数
        if (MapUtils.isNotEmpty(params)) {
            params.forEach((key, value) -> {
                form.add(key,value);
            });
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(form.build(), charset);
            param = EntityUtils.toString(urlEncodedFormEntity);
        }

        if (param != null) {
            url = url + '?' + param;
        }
        Request request = Request.get(url);
        // 请求头
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach((key, value) -> {
                request.addHeader(key, value);
            });
        }

        Response response = executor.execute(request);
        String result = response.returnContent().asString(charset);
        if (tClass.isAssignableFrom(String.class)) {
            return (T) result;
        } else {
            return objectMapper.readValue(result, tClass);
        }
    }


    public <T> T postForm(String url, Map<String, String> params) throws IOException {
        return this.postForm(url, null, params);
    }

    public <T> T postForm(String url, Map<String, String> headers, Map<String, String> params) throws IOException {
        return this.postForm(url, headers, params, StandardCharsets.UTF_8);
    }

    public <T> T postForm(String url, Map<String, String> headers,
                          Map<String, String> params, Charset charset) throws IOException {

        return (T) this.postForm(url, headers, params, charset, String.class);
    }


    public <T> T postForm(String url, Map<String, String> headers, Map<String, String> params,
                          Charset charset, Class<T> tClass) throws IOException {

        url = formatURI(url);
        Request request = Request.post(url);
        Form form = Form.form();
        if (MapUtils.isNotEmpty(params)) {
            params.forEach((key, value) -> {
                form.add(key,value);
            });
            request.bodyForm(form.build());
        }

        // 请求头
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach((key, value) -> {
                request.addHeader(key, value);
            });
        }
        Response response = executor.execute(request);
        String result = response.returnContent().asString(charset);
        if (tClass.isAssignableFrom(String.class)) {
            return (T) result;
        } else {
            return objectMapper.readValue(result, tClass);
        }
    }


    public <T> T post(String url, String body) throws IOException {
        return this.post(url, body, null);
    }


    public <T> T post(String url, String body, Map<String, String> headers) throws IOException {
        return this.post(url, body, headers, StandardCharsets.UTF_8);
    }

    public <T> T post(String url, String body, Map<String, String> headers, Charset charset) throws IOException {
        return (T) this.post(url, body, headers, charset, String.class);
    }

    public <T> T post(String url, String body, Map<String, String> headers,
                      Charset charset, Class<T> tClass) throws IOException {

        return this.post(url, body, headers, charset, tClass, ContentType.APPLICATION_JSON);
    }


    public <T> T post(String url, String body, Map<String, String> headers,
                      Charset charset, Class<T> tClass, ContentType contentType) throws IOException {

        url = formatURI(url);
        Request request = Request.post(url);
        if (StringUtils.isNotBlank(body)) {
            StringEntity entity = new StringEntity(body, contentType);
            request.body(entity);
        }
        // 请求头
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach((key, value) -> {
                request.addHeader(key, value);
            });
            request.addHeader("Authorization", "Bearer " + "asdasdas3453dfgdvxsdfm786vsdfsdfs");
        }
        Response response = executor.execute(request);
        String result = response.returnContent().asString(charset);
        if (tClass.isAssignableFrom(String.class)) {
            return (T) result;
        } else {
            return objectMapper.readValue(result, tClass);
        }
    }


    public <T> T put(String url, String body) throws IOException {
        return this.put(url, body, null);
    }


    public <T> T put(String url, String body, Map<String, String> headers) throws IOException {
        return this.put(url, body, headers, StandardCharsets.UTF_8);
    }

    public <T> T put(String url, String body, Map<String, String> headers, Charset charset) throws IOException {
        return (T) this.put(url, body, headers, charset, String.class);
    }


    public <T> T put(String url, String body, Map<String, String> headers,
                      Charset charset, Class<T> tClass) throws IOException {

        return this.put(url, body, headers, charset, tClass, ContentType.APPLICATION_JSON);
    }


    public <T> T put(String url, String body, Map<String, String> headers,
                      Charset charset, Class<T> tClass, ContentType contentType) throws IOException {

        url = formatURI(url);
        StringEntity entity = new StringEntity(body, contentType);
        Request request = Request.put(url);
        request.body(entity);
        // 请求头
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach((key, value) -> {
                request.addHeader(key, value);
            });
        }
        Response response = executor.execute(request);
        String result = response.returnContent().asString(charset);
        if (tClass.isAssignableFrom(String.class)) {
            return (T) result;
        } else {
            return objectMapper.readValue(result, tClass);
        }
    }


    public <T> T delete(String url, String body) throws IOException {
        return this.delete(url, body, null);
    }


    public <T> T delete(String url, String body, Map<String, String> headers) throws IOException {
        return this.delete(url, body, headers, StandardCharsets.UTF_8);
    }

    public <T> T delete(String url, String body, Map<String, String> headers, Charset charset) throws IOException {
        return (T) this.delete(url, body, headers, charset, String.class);
    }


    public <T> T delete(String url, String body, Map<String, String> headers,
                     Charset charset, Class<T> tClass) throws IOException {

        return this.delete(url, body, headers, charset, tClass, ContentType.APPLICATION_JSON);
    }


    public <T> T delete(String url, String body, Map<String, String> headers,
                     Charset charset, Class<T> tClass, ContentType contentType) throws IOException {

        url = formatURI(url);
        StringEntity entity = new StringEntity(body, contentType);
        Request request = Request.delete(url);
        request.body(entity);
        // 请求头
        if (MapUtils.isNotEmpty(headers)) {
            headers.forEach((key, value) -> {
                request.addHeader(key, value);
            });
        }
        Response response = executor.execute(request);
        String result = response.returnContent().asString(charset);
        if (tClass.isAssignableFrom(String.class)) {
            return (T) result;
        } else {
            return objectMapper.readValue(result, tClass);
        }
    }



    public String uploadFile(String url, Map<String, Object> params, Charset charset) throws IOException {

        url = formatURI(url);
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create().setCharset(charset);
        params.forEach((k, v) -> {
            //判断是文件还是文本
            if (v instanceof File) {
                File file = (File) v;
                entityBuilder.addBinaryBody(k, file, ContentType.MULTIPART_FORM_DATA.withCharset(charset), file.getName());
            } else if (v instanceof ContentBody) {
                entityBuilder.addPart(k, (ContentBody) v);
            } else {
                entityBuilder.addTextBody(k, String.valueOf(v), ContentType.TEXT_PLAIN.withCharset(charset));
            }
        });
        Request request = Request.post(url);
        request.body(entityBuilder.build());
        Response response = executor.execute(request);
        return response.returnContent().asString(charset);
    }



    public static final class HttpClientConfig {

        private static final HttpClientConfig INSTANCE = new HttpClientConfig();

        /**
         * 重试次数
         */
        private static final int HTTP_CLIENT_RETRY_COUNT = 3;
        /**
         * 最大链接数
         */
        private static final int MAXIUM_TOTAL_CONNECTION = 100;
        /**
         * 每个路由最大链接数
         */
        private static final int MAXIUM_CONNECTION_PER_ROUTE = 50;
        /**
         * 空闲连接检查间隔
         */
        private static final int CONNECTION_VALIDATE_AFTER_INACTIVITY = 60;
        /**
         * 关闭空闲的链接时间
         */
        private static final int CLOSEIDLE_TIME = 30;
        /**
         * 重定向次数
         */
        private static final int REDIRECT_COUNT = 20;

        private transient HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        private static final Timeout connectTimeout = Timeout.ofSeconds(10L);
        private static final Timeout requestTimeout = Timeout.ofSeconds(30L);
        private static final Timeout responseTimeout = Timeout.ofSeconds(30L);

        /**
         * cookieStore 对象
         */
        private CookieStore cookieStore;
        /**
         * Basic Auth管理对象
         */
        private CredentialsStore credentialsStore;


        private HttpClientConfig() {

        }

        private CloseableHttpClient build() {
            //兼容http以及https请求
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
//                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                    .register("https", initSSlFactory())
                    .build();

            //适配http以及https请求 通过new创建PoolingHttpClientConnectionManager
            PoolingHttpClientConnectionManager clientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            // 连接池最大生成连接数
            clientConnectionManager.setMaxTotal(MAXIUM_TOTAL_CONNECTION);
            // 默认的每个路由上最大的连接数（不能超过连接池总连接数）
            clientConnectionManager.setDefaultMaxPerRoute(MAXIUM_CONNECTION_PER_ROUTE);
            // 空闲永久连接检查间隔
            clientConnectionManager.setValidateAfterInactivity(TimeValue.ofSeconds(CONNECTION_VALIDATE_AFTER_INACTIVITY));
            // 关闭过期的链接
            //clientConnectionManager.closeExpired();
            // 选择关闭 空闲30秒的链接
            //clientConnectionManager.closeIdle(TimeValue.ofSeconds(CLOSEIDLE_TIME));

            RequestConfig requestConfig = RequestConfig.custom()
                    // 指从连接池获取连接的超时时间
                    .setConnectTimeout(connectTimeout)
                    // 指客户端和服务器建立连接的超时时间，超时后会报 ConnectionTimeOutException异常；
                    .setConnectionRequestTimeout(requestTimeout)
                    // 设置启用重定向
                    .setRedirectsEnabled(true)
                    // 设置最大重定向次数
                    .setMaxRedirects(REDIRECT_COUNT)
                    // 设置响应超时时间
                    .setResponseTimeout(responseTimeout)
                    .build();

            if (this.cookieStore == null) {
                this.cookieStore = new BasicCookieStore();
            }

            if (this.credentialsStore == null) {
                this.credentialsStore = new BasicCredentialsProvider();
            }

            CloseableHttpClient httpClient = httpClientBuilder.setConnectionManager(clientConnectionManager)
                    .setDefaultRequestConfig(requestConfig)
                    // 设置定期清理连接池中过期的连接，底层是开启了一个线程去执行清理任务，因此注意不能多次实例化httpclient相关的实例，会导致不断创建线程
                    .evictExpiredConnections()
                    .evictIdleConnections(TimeValue.ofSeconds(30))
                    // 禁用请求重试
                    //.disableAutomaticRetries()
                    .setDefaultCookieStore(this.cookieStore)
                    .setDefaultCredentialsProvider(this.credentialsStore)
                    .build();

            return httpClient;
        }


        private SSLConnectionSocketFactory initSSlFactory() {
            SSLConnectionSocketFactory socketFactory = null;
            try {
                SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
                sslContextBuilder.loadTrustMaterial(null, new TrustSelfSignedStrategy());

                SSLConnectionSocketFactoryBuilder socketFactoryBuilder = new SSLConnectionSocketFactoryBuilder();
                socketFactoryBuilder.setSslContext(sslContextBuilder.build());
                socketFactory = socketFactoryBuilder.build();

            } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
                logger.error(e.getMessage(), e);
            }
            assert socketFactory != null;
            return socketFactory;
        }


        public CookieStore getCookieStore() {
            return cookieStore;
        }

        public void setCookieStore(CookieStore cookieStore) {
            this.cookieStore = cookieStore;
        }

        public CredentialsStore getCredentialsStore() {
            return credentialsStore;
        }

        public void setCredentialsStore(CredentialsStore credentialsStore) {
            this.credentialsStore = credentialsStore;
        }
    }



}

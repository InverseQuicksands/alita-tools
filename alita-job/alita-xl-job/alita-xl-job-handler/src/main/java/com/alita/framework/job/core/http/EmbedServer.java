package com.alita.framework.job.core.http;

import com.alibaba.fastjson.JSON;
import com.alita.framework.job.core.biz.JobHandlerExecutor;
import com.alita.framework.job.core.biz.impl.JobHandlerExecutorImpl;
import com.alita.framework.job.core.biz.model.ExecutorParam;
import com.alita.framework.job.core.biz.model.LogParam;
import com.alita.framework.job.core.biz.model.TriggerParam;
import com.alita.framework.job.core.thread.ExecutorRegistryThread;
import com.alita.framework.job.core.thread.NamedThreadFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * EmbedServer
 *
 * @date 2022-11-25 22:43
 */
public class EmbedServer {

    private static final Logger logger = LoggerFactory.getLogger(EmbedServer.class);

    private JobHandlerExecutor jobHandlerExecutor;
    private Thread thread;

    public void start(final String address, final int port, final String appname, final String accessToken) {
        jobHandlerExecutor = new JobHandlerExecutorImpl();

        Runnable runnable = () -> {
            // param
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            NamedThreadFactory threadFactory = new NamedThreadFactory("EmbedServer bizThreadPool-", false);
            ThreadPoolExecutor bizThreadPool = new ThreadPoolExecutor(
                    0,
                    200,
                    60L,
                    TimeUnit.SECONDS,
                    new LinkedBlockingQueue<Runnable>(2000),
                    threadFactory,
                    new RejectedExecutionHandler() {
                        @Override
                        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                            throw new RuntimeException("EmbedServer bizThreadPool is EXHAUSTED!");
                        }
                    });
            try {
                // start server
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline()
                                        .addLast(new IdleStateHandler(0, 0, 30 * 3, TimeUnit.SECONDS))  // beat 3N, close if idle
                                        .addLast(new HttpServerCodec())
                                        .addLast(new HttpObjectAggregator(5 * 1024 * 1024))  // merge request & reponse to FULL
                                        .addLast(new EmbedHttpServerHandler(jobHandlerExecutor, bizThreadPool));
                            }
                        })
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                // bind
                ChannelFuture future = bootstrap.bind(port).sync();

                logger.info(">>>>>>>>>>> brilliance-job remoting server start success, nettype = {}, port = {}", EmbedServer.class, port);

                // start registry
                startRegistry(appname, address);

                // wait util stop
                future.channel().closeFuture().sync();

            } catch (InterruptedException e) {
                logger.info(">>>>>>>>>>> brilliance-job remoting server stop.");
            } catch (Exception e) {
                logger.error(">>>>>>>>>>> brilliance-job remoting server error.", e);
            } finally {
                // stop
                try {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        };

        thread = new Thread(runnable);
        thread.setDaemon(true);    // daemon, service jvm, user thread leave >>> daemon leave >>> jvm leave
        thread.start();
    }

    public void stop() throws Exception {
        // destroy server thread
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }

        // stop registry
        stopRegistry();
        logger.info(">>>>>>>>>>> brilliance-job remoting server destroy success.");
    }


    // ---------------------- registry ----------------------

    /**
     * netty_http
     *
     */
    public static class EmbedHttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        private static final Logger logger = LoggerFactory.getLogger(EmbedHttpServerHandler.class);

        private JobHandlerExecutor jobHandlerExecutor;
        private ThreadPoolExecutor bizThreadPool;

        public EmbedHttpServerHandler(JobHandlerExecutor executorBiz, ThreadPoolExecutor bizThreadPool) {
            this.jobHandlerExecutor = executorBiz;
            this.bizThreadPool = bizThreadPool;
        }

        @Override
        protected void channelRead0(final ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
            // request parse
            //final byte[] requestBytes = ByteBufUtil.getBytes(msg.content());    // byteBuf.toString(io.netty.util.CharsetUtil.UTF_8);
            String requestData = msg.content().toString(CharsetUtil.UTF_8);
            String uri = msg.uri();
            HttpMethod httpMethod = msg.method();
            boolean keepAlive = HttpUtil.isKeepAlive(msg);
            String accessTokenReq = msg.headers().get("JOB-ACCESS-TOKEN");

            // invoke
            bizThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    // do invoke
                    Object responseObj = process(httpMethod, uri, requestData, accessTokenReq);

                    // to json
                    String responseJson = JSON.toJSONString(responseObj);

                    // write response
                    writeResponse(ctx, keepAlive, responseJson);
                }
            });
        }

        private Object process(HttpMethod httpMethod, String uri, String requestData, String accessTokenReq) {
            // valid
            if (HttpMethod.POST != httpMethod) {
                logger.warn("invalid request, HttpMethod not support.");
                return "99999999";
            }
            if (uri == null || uri.trim().length() == 0) {
                logger.warn("invalid request, uri-mapping empty.");
                return "99999999";
            }
//            if (accessToken != null
//                    && accessToken.trim().length() > 0
//                    && !accessToken.equals(accessTokenReq)) {
//                logger.warn("The access token is wrong.");
//                return "99999999";
//            }

            // services mapping
            try {
                switch (uri) {
                    case "/beat":
                        return jobHandlerExecutor.beat();
                    case "/idleBeat":
                        ExecutorParam idleBeatParam = JSON.parseObject(requestData, ExecutorParam.class);
                        return jobHandlerExecutor.idleBeat(idleBeatParam);
                    case "/run":
                        TriggerParam triggerParam = JSON.parseObject(requestData, TriggerParam.class);
                        return jobHandlerExecutor.run(triggerParam);
                    case "/kill":
                        ExecutorParam killParam = JSON.parseObject(requestData, ExecutorParam.class);
                        return jobHandlerExecutor.kill(killParam);
                    case "/log":
                        LogParam logParam = JSON.parseObject(requestData, LogParam.class);
                        return jobHandlerExecutor.log(logParam);
                    default:
                        logger.warn("invalid request, uri-mapping(" + uri + ") not found.");
                        return "99999999";
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return "99999999";
            }
        }

        /**
         * write response
         */
        private void writeResponse(ChannelHandlerContext ctx, boolean keepAlive, String responseJson) {
            // write response
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copiedBuffer(responseJson, CharsetUtil.UTF_8));   //  Unpooled.wrappedBuffer(responseJson)
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=UTF-8");       // HttpHeaderValues.TEXT_PLAIN.toString()
            response.headers().set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.writeAndFlush(response);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            logger.error(">>>>>>>>>>> brilliance-job provider netty_http server caught exception", cause);
            ctx.close();
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            if (evt instanceof IdleStateEvent) {
                ctx.channel().close();      // beat 3N, close if idle
                logger.debug(">>>>>>>>>>> brilliance-job provider netty_http server close an idle channel.");
            } else {
                super.userEventTriggered(ctx, evt);
            }
        }
    }

    // ---------------------- registry ----------------------

    public void startRegistry(final String appname, final String address) {
        // start registry
        ExecutorRegistryThread.getInstance().start(appname, address);
    }

    public void stopRegistry() {
        // stop registry
        ExecutorRegistryThread.getInstance().toStop();
    }
}

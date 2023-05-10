package com.alita.framework.id.tinyid.client.factory;

import com.alita.framework.id.tinyid.AbstractIdGeneratorFactory;
import com.alita.framework.id.tinyid.IdGenerator;
import com.alita.framework.id.tinyid.cache.CachedIdGenerator;
import com.alita.framework.id.tinyid.client.HttpSegmentIdServiceImpl;
import com.alita.framework.id.tinyid.domain.TinyIdClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author: <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date: 2022-11-17 18:13
 **/
public class IdGeneratorFactoryClient extends AbstractIdGeneratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorFactoryClient.class.getName());

    private static IdGeneratorFactoryClient idGeneratorFactoryClient;

    private static final String DEFAULT_PROP = "tinyid_client.properties";

    private static final int DEFAULT_TIME_OUT = 5000;

    private static String serverUrl = "http://{0}/tinyid/id/nextSegmentIdSimple?bizType=";

    private IdGeneratorFactoryClient() {

    }

    public static IdGeneratorFactoryClient getInstance(String serverUrl) {
        if (idGeneratorFactoryClient == null) {
            synchronized (IdGeneratorFactoryClient.class) {
                if (idGeneratorFactoryClient == null) {
                    init(serverUrl);
                }
            }
        }
        return idGeneratorFactoryClient;
    }


    private static void init(String url) {
        TinyIdClient tinyIdClient = TinyIdClient.getInstance();
        String nextSegmentIdSimpleUrl = MessageFormat.format(serverUrl, url);
        tinyIdClient.setTinyIdServer(nextSegmentIdSimpleUrl);

        String[] tinyIdServers = url.split(",");
        List<String> serverList = new ArrayList<>(tinyIdServers.length);
        for (String server : tinyIdServers) {
            String nsisUrl = MessageFormat.format(serverUrl, server);
            serverList.add(nsisUrl);
        }
        tinyIdClient.setServerList(serverList);

        idGeneratorFactoryClient = new IdGeneratorFactoryClient();
        logger.info("init tinyId client success url info：" + serverList);
    }

    @Override
    protected IdGenerator createIdGenerator(String bizType) {
        return new CachedIdGenerator(bizType, new HttpSegmentIdServiceImpl());
    }


}

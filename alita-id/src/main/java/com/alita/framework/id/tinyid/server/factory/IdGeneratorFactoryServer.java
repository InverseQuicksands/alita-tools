package com.alita.framework.id.tinyid.server.factory;

import com.alita.framework.id.tinyid.AbstractIdGeneratorFactory;
import com.alita.framework.id.tinyid.IdGenerator;
import com.alita.framework.id.tinyid.SegmentIdService;
import com.alita.framework.id.tinyid.cache.CachedIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * server端 id 生成工厂.
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2022-11-18 13:13
 **/
@Component
public class IdGeneratorFactoryServer extends AbstractIdGeneratorFactory {

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorFactoryServer.class);
    @Autowired
    private SegmentIdService tinyIdService;

    @Override
    public IdGenerator createIdGenerator(String bizType) {
        logger.info("createIdGenerator :{}", bizType);
        return new CachedIdGenerator(bizType, tinyIdService);
    }
}

package com.alita.framework.id.tinyid.client;

import com.alita.framework.id.tinyid.IdGenerator;
import com.alita.framework.id.tinyid.client.factory.IdGeneratorFactoryClient;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 每个客户端连接服务端获取 id 时，号段都会加一个 step，导致每个客户端获取到的并不是相互连续的.
 *
 * <p>
 * 比如：<br>
 * A客户端: 400001, 400002, 400003, 400004, 400005, 400006, 400007, 400008, 400009, 400010; <br>
 * B客户端: 500001, 500002, 500003, 500004, 500005, 500006, 500007, 500008, 500009, 500010; <br>
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2022-11-19 00:05
 **/
public class Tinyid {

    private static IdGeneratorFactoryClient client;

    private static class TinyidSingleton {
        private static final Tinyid tinyid = new Tinyid();
    }

    public static Tinyid getInstance(String url) {
        if (client == null) {
            client = IdGeneratorFactoryClient.getInstance(url);
        }
        return TinyidSingleton.tinyid;
    }

    private Tinyid() {

    }

    public Long nextId(String bizType) {
        Assert.notNull(bizType, "type is null");
        IdGenerator idGenerator = client.getIdGenerator(bizType);
        return idGenerator.nextId();
    }

    public List<Long> nextId(String bizType, Long batchSize) {
        if(batchSize == null) {
            Long id = nextId(bizType);
            List<Long> list = new ArrayList<>();
            list.add(id);
            return list;
        }
        IdGenerator idGenerator = client.getIdGenerator(bizType);
        return idGenerator.nextId(batchSize);
    }

}

package com.alita.framework.id.tinyid.client;

import com.alita.framework.id.tinyid.SegmentIdService;
import com.alita.framework.id.tinyid.TinyIdSysException;
import com.alita.framework.id.tinyid.domain.SegmentId;
import com.alita.framework.id.tinyid.domain.TinyIdClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * tinyid http 客户端
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2022-11-17 18:35
 **/
public class HttpSegmentIdServiceImpl implements SegmentIdService {

    public static final Logger logger = LoggerFactory.getLogger(HttpSegmentIdServiceImpl.class);

    @Override
    public SegmentId getNextSegmentId(String bizType) {
        String url = chooseService(bizType);
        String response = null;
        try {
            response = HttpClient.getInstance().get(url);
        } catch (Exception e) {
            logger.error("tinyId client getNextSegmentId error", e);
            throw new TinyIdSysException("tinyId client getNextSegmentId error：" + e.getMessage());
        }
        logger.info("tinyId client getNextSegmentId end, response: " + response);

        if (response == null || "".equals(response.trim())) {
            return null;
        }

        SegmentId segmentId = new SegmentId();
        String[] arr = response.split(",");
        segmentId.setCurrentId(new AtomicLong(Long.parseLong(arr[0])));
        segmentId.setLoadingId(Long.parseLong(arr[1]));
        segmentId.setMaxId(Long.parseLong(arr[2]));
        segmentId.setDelta(Integer.parseInt(arr[3]));
        segmentId.setRemainder(Integer.parseInt(arr[4]));
        return segmentId;
    }


    /**
     * 随机选取一台服务.
     *
     * @param bizType
     * @return
     */
    private String chooseService(String bizType) {
        List<String> serverList = TinyIdClient.getInstance().getServerList();
        String url = "";
        if (serverList != null && serverList.size() == 1) {
            url = serverList.get(0);
        } else if (serverList != null && serverList.size() > 1) {
            Random r = new Random();
            url = serverList.get(r.nextInt(serverList.size()));
        }
        url += bizType;
        return url;
    }

}

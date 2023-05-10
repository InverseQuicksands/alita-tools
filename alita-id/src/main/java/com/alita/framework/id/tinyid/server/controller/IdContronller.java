package com.alita.framework.id.tinyid.server.controller;

import com.alita.framework.id.tinyid.IdGenerator;
import com.alita.framework.id.tinyid.SegmentIdService;
import com.alita.framework.id.tinyid.domain.SegmentId;
import com.alita.framework.id.tinyid.server.factory.IdGeneratorFactoryServer;
import com.alita.framework.id.tinyid.server.vo.ErrorCode;
import com.alita.framework.id.tinyid.server.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * IdContronller
 *
 * @author <a href="mailto:zhangliang0231@gmail.com">zhang</a>
 * @date 2022-11-18 13:11
 **/
@RestController
@RequestMapping("/id")
public class IdContronller {

    private static final Logger logger = LoggerFactory.getLogger(IdContronller.class);
    @Autowired
    private IdGeneratorFactoryServer idGeneratorFactoryServer;
    @Autowired
    private SegmentIdService segmentIdService;

    /**
     * 控制每次获取 id 的最大数量
     */
    @Value("${batch.size.max}")
    private Long batchSizeMax;

    /**
     * 获取下一个ID.
     *
     * @param bizType
     * @param batchSize
     * @return
     */
    @GetMapping("nextId")
    public Response<List<Long>> nextId(String bizType, Long batchSize) {
        Response<List<Long>> response = new Response<>();
        Long newBatchSize = checkBatchSize(batchSize);
        try {
            IdGenerator idGenerator = idGeneratorFactoryServer.getIdGenerator(bizType);
            List<Long> ids = idGenerator.nextId(newBatchSize);
            response.setData(ids);
        } catch (Exception e) {
            response.setCode(ErrorCode.SYS_ERR.getCode());
            response.setMessage(e.getMessage());
            logger.error("nextId error", e);
        }
        return response;
    }

    private Long checkBatchSize(Long batchSize) {
        if (batchSize == null) {
            batchSize = 1L;
        }
        if (batchSize > batchSizeMax) {
            batchSize = batchSizeMax;
        }
        return batchSize;
    }

    /**
     * 获取下一个ID.
     *
     * @param bizType
     * @param batchSize
     * @return
     */
    @GetMapping("nextIdSimple")
    public String nextIdSimple(String bizType, Long batchSize) {
        Long newBatchSize = checkBatchSize(batchSize);

        String response = "";
        try {
            IdGenerator idGenerator = idGeneratorFactoryServer.getIdGenerator(bizType);
            if (newBatchSize == 1) {
                Long id = idGenerator.nextId();
                response = id + "";
            } else {
                List<Long> idList = idGenerator.nextId(newBatchSize);
                StringBuilder sb = new StringBuilder();
                for (Long id : idList) {
                    sb.append(id).append(",");
                }
                response = sb.deleteCharAt(sb.length() - 1).toString();
            }
        } catch (Exception e) {
            logger.error("nextIdSimple error", e);
        }
        return response;
    }

    /**
     * 获取下一个可用号段
     *
     * @param bizType
     * @return
     */
    @GetMapping("nextSegmentId")
    public Response<SegmentId> nextSegmentId(String bizType) {
        Response<SegmentId> response = new Response<>();
        try {
            SegmentId segmentId = segmentIdService.getNextSegmentId(bizType);
            response.setData(segmentId);
        } catch (Exception e) {
            response.setCode(ErrorCode.SYS_ERR.getCode());
            response.setMessage(e.getMessage());
            logger.error("nextSegmentId error", e);
        }
        return response;
    }

    /**
     * 获取下一个可用号段
     *
     * @param bizType
     * @return
     */
    @GetMapping("nextSegmentIdSimple")
    public String nextSegmentIdSimple(String bizType) {
        String response = "";
        try {
            SegmentId segmentId = segmentIdService.getNextSegmentId(bizType);
            response = segmentId.getCurrentId() + "," + segmentId.getLoadingId() + "," + segmentId.getMaxId()
                    + "," + segmentId.getDelta() + "," + segmentId.getRemainder();
        } catch (Exception e) {
            logger.error("nextSegmentIdSimple error", e);
        }
        return response;
    }

}

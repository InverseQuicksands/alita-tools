package com.alita.framework.job.controller;

import com.alibaba.fastjson.JSON;
import com.alita.framework.job.core.biz.JobAdminExecutor;
import com.alita.framework.job.core.biz.model.HandleCallbackParam;
import com.alita.framework.job.core.biz.model.RegistryParam;
import com.alita.framework.job.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * 执行器执行任务结束后回调调度中心
 */
@RestController
@RequestMapping("/api")
public class JobApiController {

    @Autowired
    private JobAdminExecutor adminExecutor;

    /**
     * 执行器执行任务结束后回调管理端
     *
     * @param type 类型
     * @param data 数据
     * @return 00000000-成功，99999999-失败
     */
    @PostMapping("/{type}")
    public String api(@PathVariable("type") String type,
                      @RequestBody(required = false) String data) throws IOException {
        if (StringUtils.isBlank(type)) {
            return "invalid request, type-mapping empty.";
        }

        // services mapping
        switch (type) {
            case "callback":
                List<HandleCallbackParam> callbackParamList = JSON.parseArray(data, HandleCallbackParam.class);
                return adminExecutor.callback(callbackParamList);
            case "registry":
                RegistryParam registryParam = JSON.parseObject(data, RegistryParam.class);
                return adminExecutor.registry(registryParam);
            case "registryRemove":
                RegistryParam param = JSON.parseObject(data, RegistryParam.class);
                return adminExecutor.registryRemove(param);
            default:
                return "invalid request, type-mapping("+ type +") not found.";
        }
    }

}

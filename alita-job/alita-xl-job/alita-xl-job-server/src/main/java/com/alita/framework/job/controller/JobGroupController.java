package com.alita.framework.job.controller;

import com.alita.framework.job.common.PageInfo;
import com.alita.framework.job.common.Response;
import com.alita.framework.job.common.ResponseStatus;
import com.alita.framework.job.core.enums.RegistryConfig;
import com.alita.framework.job.dto.JobGroupDto;
import com.alita.framework.job.model.JobGroup;
import com.alita.framework.job.model.JobRegistry;
import com.alita.framework.job.service.JobGroupService;
import com.alita.framework.job.utils.I18nUtils;
import com.alita.framework.job.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * job group controller
 */
@RestController
@RequestMapping("/jobGroup")
public class JobGroupController {

    @Autowired
    private JobGroupService jobGroupService;


    /**
     * JobGroup 分页查询.
     *
     * @param jobGroupDto
     * @return Response
     */
    @RequestMapping("/pageList")
    public Response pageList(@RequestBody JobGroupDto jobGroupDto) {
        jobGroupDto.setCurrentPage(jobGroupDto.getOffset());
        PageInfo<JobGroup> pageInfo = jobGroupService.pageList(jobGroupDto);

        return Response.success(pageInfo);
    }

    /**
     * 保存 JobGroup
     *
     * @param jobGroupDto 信息
     * @return
     */
    @PostMapping("/save")
    public Response save(@RequestBody JobGroupDto jobGroupDto) {

        // valid
        if (StringUtils.isBlank(jobGroupDto.getAppName())) {
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("system_please_input") + "AppName");
        }
        if (jobGroupDto.getAppName().length() < 4 || jobGroupDto.getAppName().length() > 64) {
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobgroup_field_appname_length"));
        }
        if (jobGroupDto.getAppName().contains(">") || jobGroupDto.getAppName().contains("<")) {
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), "AppName" + I18nUtils.getProperty("system_unvalid"));
        }
        if (StringUtils.isBlank(jobGroupDto.getTitle())) {
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("system_please_input") + I18nUtils.getProperty("jobgroup_field_title"));
        }
        if (jobGroupDto.getTitle().contains(">") || jobGroupDto.getTitle().contains("<")) {
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobgroup_field_title") + I18nUtils.getProperty("system_unvalid"));
        }
        if (jobGroupDto.getAddressType() != 0) {
            if (StringUtils.isBlank(jobGroupDto.getAddressList())) {
                return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobgroup_field_addressType_limit"));
            }
            if (jobGroupDto.getAddressList().contains(">") || jobGroupDto.getAddressList().contains("<")) {
                return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobgroup_field_registryList") + I18nUtils.getProperty("system_unvalid"));
            }

            String[] addresss = jobGroupDto.getAddressList().split(",");
            for (String item : addresss) {
                if (StringUtils.isBlank(item)) {
                    return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobgroup_field_registryList_unvalid"));
                }
            }
        }

        JobGroup jobGroup = new JobGroup();
        jobGroup.setAppName(jobGroupDto.getAppName());
        jobGroup.setTitle(jobGroupDto.getTitle());
        jobGroup.setAddressList(jobGroupDto.getAddressList());
        jobGroup.setAddressType(jobGroupDto.getAddressType());
        jobGroup.setUpdateTime(new Date());
        jobGroupService.save(jobGroup);

        return Response.success();
    }

    /**
     * 更新 JobGroup
     *
     * @param jobGroupDto
     * @return Response
     */
    @PutMapping("/update")
    public Response update(@RequestBody JobGroupDto jobGroupDto) {
        // valid
        if (StringUtils.isBlank(jobGroupDto.getAppName())) {
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("system_please_input") + "AppName");
        }
        if (jobGroupDto.getAppName().length() < 4 || jobGroupDto.getAppName().length() > 64) {
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobgroup_field_appname_length"));
        }
        if (StringUtils.isBlank(jobGroupDto.getTitle())) {
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("system_please_input") + I18nUtils.getProperty("jobgroup_field_title"));
        }
        if (jobGroupDto.getAddressType() == 0) {
            // 0=自动注册
            List<String> registryList = findRegistryByAppName(jobGroupDto.getAppName());
            StringJoiner joiner = new StringJoiner(",");
            if (registryList != null && !registryList.isEmpty()) {
                Collections.sort(registryList);
                for (String item : registryList) {
                    joiner.add(item);
                }
            }
            jobGroupDto.setAddressList(joiner.toString());
        } else {
            // 1=手动录入
            if (StringUtils.isBlank(jobGroupDto.getAddressList())) {
                return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobgroup_field_addressType_limit"));
            }
            String[] addresss = jobGroupDto.getAddressList().split(",");
            for (String item : addresss) {
                if (StringUtils.isBlank(item)) {
                    return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobgroup_field_registryList_unvalid"));
                }
            }
        }

        JobGroup jobGroup = new JobGroup();
        jobGroup.setAppName(jobGroup.getAppName());
        jobGroup.setTitle(jobGroup.getTitle());
        jobGroup.setAddressList(jobGroup.getAddressList());
        jobGroup.setAddressType(jobGroup.getAddressType());
        jobGroup.setUpdateTime(new Date());
        jobGroup.setId(jobGroupDto.getId());
        jobGroupService.update(jobGroup);

        return Response.success();
    }

    private List<String> findRegistryByAppName(String appnameParam) {
        HashMap<String, List<String>> appAddressMap = new HashMap<String, List<String>>();
        List<JobRegistry> list = jobGroupService.findJobRegistry();
        if (list != null) {
            for (JobRegistry item : list) {
                if (RegistryConfig.RegistType.EXECUTOR.name().equals(item.getRegistryGroup())) {
                    String appname = item.getRegistryKey();
                    List<String> registryList = appAddressMap.get(appname);
                    if (registryList == null) {
                        registryList = new ArrayList<String>();
                    }

                    if (!registryList.contains(item.getRegistryValue())) {
                        registryList.add(item.getRegistryValue());
                    }
                    appAddressMap.put(appname, registryList);
                }
            }
        }
        return appAddressMap.get(appnameParam);
    }

    /**
     * 删除 JobGroup
     *
     * @param id 主键
     * @return Response
     */
    @DeleteMapping("/remove")
    public Response remove(String id) {
        // valid
        int count = jobGroupService.pageJobInfoListCount(id, -1, null, null);
        if (count > 0) {
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobgroup_del_limit_0"));
        }

        List<JobGroup> allList = jobGroupService.findAll();
        if (allList.size() == 1) {
            return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobgroup_del_limit_1"));
        }

        jobGroupService.remove(id);
        return Response.success();
    }

    /**
     * 通过主键查询 JobGroup
     *
     * @param id 主键
     * @return JobGroup
     */
    @PostMapping("/loadById")
    public JobGroup loadById(String id) {
        JobGroup jobGroup = jobGroupService.queryById(id);
        return jobGroup;
    }

}

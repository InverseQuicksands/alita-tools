package com.alita.framework.job.controller;

import com.alita.framework.job.common.Response;
import com.alita.framework.job.dto.JobInfoDto;
import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.common.PageInfo;
import com.alita.framework.job.service.JobInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 任务管理
 * MediaType.APPLICATION_JSON_VALUE
 */
@RestController
@RequestMapping(path = "/jobInfo")
public class JobInfoController {

    @Autowired
    private JobInfoService jobInfoService;


    @PostMapping("/pageList")
    public PageInfo<JobInfo> getJobList(@RequestBody JobInfoDto jobInfoDto) throws Exception {
        jobInfoDto.setCurrentPage(jobInfoDto.getOffset());
        PageInfo<JobInfo> jobInfoList = jobInfoService.getJobList(jobInfoDto);
        return jobInfoList;
    }


    @PostMapping("/add")
    public void add(@RequestBody JobInfo jobInfo) throws Exception  {
        jobInfoService.add(jobInfo);
    }


    @PutMapping ("/update")
    public void update(@RequestBody JobInfo jobInfo) throws Exception  {
        jobInfoService.update(jobInfo);
    }

    @DeleteMapping("/remove")
    public void remove(@RequestBody String id) throws Exception  {
        jobInfoService.remove(id);
    }


    @PostMapping("/stop")
    public Response stop(@RequestBody JobInfo jobInfo) throws Exception  {
        return jobInfoService.stop(jobInfo);
    }


    @PostMapping("/start")
    public Response start(@RequestBody JobInfo jobInfo) throws Exception  {
        return jobInfoService.start(jobInfo);
    }


    @PostMapping("/trigger")
    public Response trigger(@RequestBody JobInfo jobInfo, String addressList) throws Exception  {
        return jobInfoService.trigger(jobInfo, addressList);
    }


    @PostMapping("/nextTriggerTime")
    public List<String> nextTriggerTime(@RequestBody JobInfo jobInfo) throws Exception  {
        return jobInfoService.nextTriggerTime(jobInfo);
    }

    @GetMapping("/loadById")
    public Response queryById(@RequestBody String jobId) {
        JobInfo jobInfo = jobInfoService.queryById(jobId);
        return Response.success(jobInfo);
    }

}

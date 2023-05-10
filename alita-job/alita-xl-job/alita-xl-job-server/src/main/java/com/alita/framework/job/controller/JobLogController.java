package com.alita.framework.job.controller;

import com.alita.framework.job.common.PageInfo;
import com.alita.framework.job.common.Response;
import com.alita.framework.job.common.ResponseStatus;
import com.alita.framework.job.core.biz.JobHandlerExecutor;
import com.alita.framework.job.core.biz.model.ExecutorParam;
import com.alita.framework.job.core.biz.model.LogParam;
import com.alita.framework.job.core.biz.model.LogResult;
import com.alita.framework.job.core.scheduler.JobCompleter;
import com.alita.framework.job.core.scheduler.JobScheduler;
import com.alita.framework.job.dto.JobLogDto;
import com.alita.framework.job.model.JobGroup;
import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.model.JobLog;
import com.alita.framework.job.service.JobGroupService;
import com.alita.framework.job.service.JobInfoService;
import com.alita.framework.job.service.JobLogService;
import com.alita.framework.job.utils.DateUtils;
import com.alita.framework.job.utils.I18nUtils;
import com.alita.framework.job.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;

/**
 * index controller
 *
 */
@RestController
@RequestMapping("/joblog")
public class JobLogController {
	private static Logger logger = LoggerFactory.getLogger(JobLogController.class);

	@Autowired
	private JobGroupService jobGroupService;

	@Autowired
	private JobInfoService jobInfoService;

	@Autowired
	private JobLogService jobLogService;


	@GetMapping("/index")
	public Response index(@RequestParam(required = false, defaultValue = "0") String jobId) {

		// 执行器列表
		List<JobGroup> jobGroups = jobGroupService.findAll();

		Map<String, Object> map = new HashMap<>(3);
		map.put("JobGroupList", jobGroups);

		// 任务
		JobInfo jobInfo = jobInfoService.queryById(jobId);
		if (jobInfo == null) {
			throw new RuntimeException(I18nUtils.getProperty("jobinfo_field_id") + I18nUtils.getProperty("system_unvalid"));
		}
		map.put("jobInfo", jobInfo);

		return Response.success(map);
	}

	@GetMapping("/getJobsByGroup")
	public List<JobInfo> getJobsByGroup(String jobGroupId){
		List<JobInfo> list = jobInfoService.getJobsByGroup(jobGroupId);
		return list;
	}
	
	@PostMapping("/pageList")
	public Response pageList(@RequestBody JobLogDto jobLogDto) throws ParseException {

		// parse param
		Date triggerTimeStart = null;
		Date triggerTimeEnd = null;
		if (StringUtils.isNotBlank(jobLogDto.getFilterTime())) {
			String[] temp = jobLogDto.getFilterTime().split(" - ");
			if (temp.length == 2) {
				triggerTimeStart = DateUtils.parseDateTime(temp[0]);
				triggerTimeEnd = DateUtils.parseDateTime(temp[1]);

				jobLogDto.setTriggerTimeStart(triggerTimeStart);
				jobLogDto.setTriggerTimeEnd(triggerTimeEnd);
			}
		}
		
		// page query
		PageInfo<JobLog> list = jobLogService.pageList(jobLogDto);

		return Response.success(list);
	}

	@PostMapping("/logDetailPage")
	public Response logDetailPage(@RequestBody String id){

		// base check
		JobLog jobLog = jobLogService.queryById(id);
		if (jobLog == null) {
            throw new RuntimeException(I18nUtils.getProperty("joblog_logid_unvalid"));
		}

		return Response.success(jobLog);
	}

	@PostMapping("/logDetailCat")
	public LogResult logDetailCat(@RequestBody String executorAddress, long triggerTime, String logId, int fromLineNum){
		try {
			JobHandlerExecutor jobHandlerExecutor = JobScheduler.getJobHandlerExecutor(executorAddress);
			LogResult logResult = jobHandlerExecutor.log(new LogParam(triggerTime, Long.valueOf(logId), fromLineNum));

			// is end
            if (logResult !=null && logResult.getFromLineNum() > logResult.getToLineNum()) {
                JobLog jobLog = jobLogService.queryById(logId);
                if (jobLog.getHandleCode().equals("00000000")) {
                    logResult.setEnd(true);
                }
            }

			return logResult;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return new LogResult();
	}

	@PostMapping("/logKill")
	public String logKill(@RequestBody String id){
		// base check
		JobLog log = jobLogService.queryById(id);
		JobInfo jobInfo = jobInfoService.queryById(log.getJobId());
		if (jobInfo==null) {
			return I18nUtils.getProperty("jobinfo_glue_jobid_unvalid");
		}
		if (1 != log.getTriggerCode()) {
			return I18nUtils.getProperty("joblog_kill_log_limit");
		}

		// request of kill
		String runResult = null;
		try {
			JobHandlerExecutor jobHandlerExecutor = JobScheduler.getJobHandlerExecutor(log.getExecutorAddress());
			ExecutorParam executorParam = new ExecutorParam();
			executorParam.setJobId(jobInfo.getId());
			runResult = jobHandlerExecutor.kill(executorParam);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			runResult = "99999999";
		}

		if ("00000000".equals(runResult)) {
			log.setHandleCode(runResult);
			log.setHandleMsg(I18nUtils.getProperty("joblog_kill_log_byman"));
			log.setHandleTime(new Date());
			JobCompleter.updateHandleInfoAndFinish(log);
		}
		return runResult;
	}

	@PostMapping("/clearLog")
	public Response clearLog(@RequestBody int jobGroup, int jobId, int type){

		Date clearBeforeTime = null;
		int clearBeforeNum = 0;
		if (type == 1) {
			clearBeforeTime = DateUtils.minusMonths(new Date(), 1);	// 清理一个月之前日志数据
		} else if (type == 2) {
			clearBeforeTime = DateUtils.minusMonths(new Date(), 3);	// 清理三个月之前日志数据
		} else if (type == 3) {
			clearBeforeTime = DateUtils.minusMonths(new Date(), 6);	// 清理六个月之前日志数据
		} else if (type == 4) {
			clearBeforeTime = DateUtils.minusYears(new Date(), 1);	// 清理一年之前日志数据
		} else if (type == 5) {
			clearBeforeNum = 1000;		// 清理一千条以前日志数据
		} else if (type == 6) {
			clearBeforeNum = 10000;		// 清理一万条以前日志数据
		} else if (type == 7) {
			clearBeforeNum = 30000;		// 清理三万条以前日志数据
		} else if (type == 8) {
			clearBeforeNum = 100000;	// 清理十万条以前日志数据
		} else if (type == 9) {
			clearBeforeNum = 0;			// 清理所有日志数据
		} else {
			return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("joblog_clean_type_unvalid"));
		}

		List<Long> logIds = null;
		do {
			logIds = jobLogService.findClearLogIds(jobGroup, jobId, clearBeforeTime, clearBeforeNum);
			if (logIds!=null && logIds.size()>0) {
				jobLogService.clearLog(logIds);
			}
		} while (logIds!=null && logIds.size()>0);

		return Response.success();
	}

}

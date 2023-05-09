package com.alita.framework.job.controller;

import com.alita.framework.job.common.Response;
import com.alita.framework.job.common.ResponseStatus;
import com.alita.framework.job.core.glue.GlueTypeEnum;
import com.alita.framework.job.model.JobInfo;
import com.alita.framework.job.model.JobLogGlue;
import com.alita.framework.job.service.JobCodeService;
import com.alita.framework.job.utils.I18nUtils;
import com.alita.framework.job.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * job code 管理
 */
@RestController
@RequestMapping("/jobCode")
public class JobCodeController {

	@Autowired
	private JobCodeService jobCodeService;

	/**
	 * 通过 jobId 查询任务信息和 JobLogGlue.
	 *
	 * @param jobId
	 * @return Response
	 */
	@PostMapping("/jobId")
	public Response index(String jobId) {
		JobInfo jobInfo = jobCodeService.queryJobInfoById(jobId);
		List<JobLogGlue> jobLogGlues = jobCodeService.queryJobLogGlueById(jobId);
		if (jobInfo == null) {
			throw new RuntimeException(I18nUtils.getProperty("jobinfo_glue_jobid_unvalid"));
		}
		if (GlueTypeEnum.BEAN == GlueTypeEnum.match(jobInfo.getGlueType())) {
			throw new RuntimeException(I18nUtils.getProperty("jobinfo_glue_gluetype_unvalid"));
		}

		Map<String, Object> map = new HashMap<>(3);
		map.put("GlueTypeEnum", GlueTypeEnum.values());
		map.put("jobInfo", jobInfo);
		map.put("jobLogGlues", jobLogGlues);

		return Response.success(map);
	}

	/**
	 * 保存 job code。
	 *
	 * @param id job id
	 * @param glueSource 源代码
	 * @param glueRemark 描述
	 * @return Response
	 */
	@PostMapping("/save")
	public Response save(String id, String glueSource, String glueRemark) {
		// valid
		if (StringUtils.isBlank(glueRemark)) {
			String message = I18nUtils.getProperty("system_please_input") + I18nUtils.getProperty("jobinfo_glue_remark");
			return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), message);
		}
		if (glueRemark.length()<4 || glueRemark.length()>100) {
			return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobinfo_glue_remark_limit"));
		}
		JobInfo exists_jobInfo = jobCodeService.queryJobInfoById(id);
		if (exists_jobInfo == null) {
			return Response.error(ResponseStatus.INTERNAL_SERVER_ERROR.getCode(), I18nUtils.getProperty("jobinfo_glue_jobid_unvalid"));
		}
		
		// update new code
		exists_jobInfo.setGlueSource(glueSource);
		exists_jobInfo.setGlueRemark(glueRemark);
		exists_jobInfo.setGlueUpdatetime(new Date());
		exists_jobInfo.setUpdateTime(new Date());
		jobCodeService.updateJobInfo(exists_jobInfo);

		// log old code
		JobLogGlue jobLogGlue = new JobLogGlue();
		jobLogGlue.setJobId(exists_jobInfo.getId());
		jobLogGlue.setGlueType(exists_jobInfo.getGlueType());
		jobLogGlue.setGlueSource(glueSource);
		jobLogGlue.setGlueRemark(glueRemark);
		jobLogGlue.setAddTime(new Date());
		jobLogGlue.setUpdateTime(new Date());
		jobCodeService.saveJobLogGlue(jobLogGlue);

		// remove code backup more than 30
		jobCodeService.removeJobLogGlueOld(exists_jobInfo.getId(), 30);

		return Response.success();
	}
	
}

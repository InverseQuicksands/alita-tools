package com.alita.framework.job.core.handler.impl;

import com.alita.framework.job.core.context.JobHelper;
import com.alita.framework.job.core.handler.IJobHandler;

/**
 * glue job handler
 *
 *
 */
public class GlueJobHandler implements IJobHandler {

	private long glueUpdatetime;
	private IJobHandler jobHandler;
	public GlueJobHandler(IJobHandler jobHandler, long glueUpdatetime) {
		this.jobHandler = jobHandler;
		this.glueUpdatetime = glueUpdatetime;
	}
	public long getGlueUpdatetime() {
		return glueUpdatetime;
	}

	@Override
	public void execute() throws Exception {
		JobHelper.log("----------- glue.version:"+ glueUpdatetime +" -----------");
		jobHandler.execute();
	}

	@Override
	public void init() throws Exception {
		this.jobHandler.init();
	}

	@Override
	public void destroy() throws Exception {
		this.jobHandler.destroy();
	}
}

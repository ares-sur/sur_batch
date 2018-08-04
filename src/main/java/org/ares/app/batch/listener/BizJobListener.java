package org.ares.app.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

public class BizJobListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println(System.currentTimeMillis());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		System.out.println(System.currentTimeMillis());
	}

}

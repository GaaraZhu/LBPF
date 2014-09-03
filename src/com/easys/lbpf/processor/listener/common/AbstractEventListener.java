package com.easys.lbpf.processor.listener.common;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

public abstract class AbstractEventListener<E extends ApplicationEvent>
		implements ApplicationListener<E> {

	protected static Logger logger = LogManager
			.getLogger(AbstractEventListener.class);

	protected InterfaceTaskService taskService;

	protected InterfaceTaskService getTaskService() {
		return ((InterfaceTaskService) ApplicationContextHolder.getInstance()
				.getBean("interfaceTaskService"));
	}

	protected void setTaskService(InterfaceTaskService taskService) {
		this.taskService = taskService;
	}

	protected void updateITaskstatus(final Long taskOid, final String status) {
		FWJPATransactionUtils.executeInWriteTx(new SimpleCallback() {
			@Override
			public void doInCallback() {
				InterfaceTask task = getTaskService().findObject(taskOid);
				task.setStatus(status);
				getTaskService().store(task);
			}
		});
	}

	protected String getITaskStatus(final long oid) {
		return FWJPATransactionUtils
				.executeInReadTx(new SimpleResultableCallback<String>() {
					@Override
					public String doInCallback() {
						return getTaskService().getInterfaceTaskStatus(oid);
					}
				});
	}

	protected void updatePipelineStatus(final long pipelineOid) {
		FWJPATransactionUtils.executeInWriteTx(new SimpleCallback() {
			@Override
			public void doInCallback() {
				getTaskService().updatePipelineStatusInLBPF(pipelineOid);
			}
		});
	}

}

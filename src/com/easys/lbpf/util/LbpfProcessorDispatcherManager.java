package com.easys.lbpf.util;

import com.easys.frm.spring.ApplicationContextHolder;

public enum LbpfProcessorDispatcherManager {
	instance {

		public LbpfProcessorDispatcher getDispatcher() {
			if (this.dispatcher == null) {
				this.dispatcher = (LbpfProcessorDispatcher) ApplicationContextHolder
						.getInstance().getBean("lbpfProcessorDispatcher");
			}
			return this.dispatcher;
		}

		@Override
		public String getTaskProcessor(String taskType) {
			return getDispatcher().getDispatcher().get(taskType);
		}

		private LbpfProcessorDispatcher dispatcher;

	};

	public abstract String getTaskProcessor(String taskType);
}

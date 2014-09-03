package com.easys.lbpf.processor.listener;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.easys.lbpf.model.LightweightTask;
import com.easys.lbpf.model.event.InputStreamEvent;
import com.easys.lbpf.model.event.OutputStreamEvent;
import com.easys.lbpf.processor.listener.common.AbstractEventListener;
import com.easys.lbpf.util.EventManager;

/**
 * @ClassName: OutputStreamEventListener
 * @Description:
 * @author ZHUGA3
 * 
 * @date Oct 24, 2013
 */
public class OutputStreamEventListener extends
		AbstractEventListener<OutputStreamEvent> {

	@Override
	public void onApplicationEvent(OutputStreamEvent event) {
		LightweightTask sourceLTask = (LightweightTask) event.getCurrentStage();
		// Convert one Output event to Input event(s)
		if (!CollectionUtils.isEmpty(sourceLTask.getSubTasks())) {
			for (LightweightTask subLTask : sourceLTask.getSubTasks()) {
				if (isAllUpStreamTaskSucceed(subLTask)) {
					EventManager.instance.publishEvent(new InputStreamEvent(
							subLTask));
				}
			}
		}
	}

	private boolean isAllUpStreamTaskSucceed(LightweightTask lTask) {
		List<LightweightTask> preLTasks = lTask.getPreTasks();
		if (!CollectionUtils.isEmpty(preLTasks)) {
			for (LightweightTask preLTask : preLTasks) {
				if (!InterfaceTaskStatus.SUCCESS.toString().equals(
						getITaskStatus(preLTask.getTaskOid()))) {
					return false;
				}
			}
		}

		return true;
	}
}

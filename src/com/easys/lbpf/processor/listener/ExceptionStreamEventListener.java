package com.easys.lbpf.processor.listener;

import org.apache.commons.collections.CollectionUtils;

import com.easys.lbpf.model.LightweightTask;
import com.easys.lbpf.model.event.ExceptionStreamEvent;
import com.easys.lbpf.model.event.InputStreamEvent;
import com.easys.lbpf.processor.listener.common.AbstractEventListener;
import com.easys.lbpf.util.EventManager;

/**
 * @ClassName: ExceptionStreamEventListener
 * @Description: ExceptionStreamEvent listener, will check root cause of
 *               exception event, and generate a new input stream even if
 *               concurrent issue, else update task(s) status and pipeline
 *               status if the exception event comes from a root stage
 * @author ZHUGA3
 * 
 * @date Oct 24, 2013
 */
public class ExceptionStreamEventListener extends
		AbstractEventListener<ExceptionStreamEvent> {

	@Override
	public void onApplicationEvent(final ExceptionStreamEvent exceptionEvent) {
		// 1. Get the exception inside this event
		final LightweightTask currentLTask = exceptionEvent.getCurrentStage();
		Exception e = exceptionEvent.getException();

		// 2. Find the root exception
		while (e.getCause() instanceof Exception && e != e.getCause()) {
			try {
				e = (Exception) e.getCause();
			} catch (Exception e1) {
				break;
			}
		}

		// 3. If it's concurrent exception, convert this exception event to an
		// input event
		if (e instanceof FrmPersistenceRootVersionConflictException
				|| e instanceof javax.persistence.OptimisticLockException
				|| e instanceof org.eclipse.persistence.exceptions.OptimisticLockException) {
			EventManager.instance.publishEvent(new InputStreamEvent(
					currentLTask));

			return;
		}

		// 4. Else, stop the following process and update task status
		logger.warn("LBPF PROCESSOR: Exceptional task - "
				+ currentLTask.getTaskOid() + " :"
				+ ExceptionUtils.getFullStackTrace(e));

		updateITaskstatus(currentLTask.getTaskOid(),
				InterfaceTaskStatus.FAILED.toString());
		if (!CollectionUtils.isEmpty(currentLTask.getSubTasks())) {
			updateSubTaskToDFStatus(currentLTask);
		}
		if (currentLTask.isRootTask()) {
			updatePipelineStatus(currentLTask.getPipelineOid());
		}

	}

	private void updateSubTaskToDFStatus(LightweightTask currentLTask) {
		if (!CollectionUtils.isEmpty(currentLTask.getSubTasks())) {
			for (LightweightTask subStage : currentLTask.getSubTasks()) {
				updateITaskstatus(subStage.getTaskOid(),
						InterfaceTaskStatus.DEPENDENCY_FAILD.toString());
				updateSubTaskToDFStatus(subStage);
			}
		}
	}
}

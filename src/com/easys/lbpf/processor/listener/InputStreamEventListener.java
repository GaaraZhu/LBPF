package com.easys.lbpf.processor.listener;

import com.easys.lbpf.model.LightweightTask; 
import com.easys.lbpf.model.event.ExceptionStreamEvent;
import com.easys.lbpf.model.event.InputStreamEvent;
import com.easys.lbpf.model.event.OutputStreamEvent;
import com.easys.lbpf.processor.listener.common.AbstractEventListener;
import com.easys.lbpf.util.EventManager;
import com.easys.lbpf.util.LbpfProcessorDispatcherManager;

/**
 * @ClassName: InputStreamEventListener
 * @Description:
 * @author ZHUGA3
 * 
 * @date Oct 24, 2013
 */
public class InputStreamEventListener extends
		AbstractEventListener<InputStreamEvent> {

	@Override
	public void onApplicationEvent(InputStreamEvent event) {

		final LightweightTask lTask = event.getCurrentStage();
		logger.debug("LBPF PROCESSOR: CURRENT TASK OID IS "
				+ lTask.getTaskOid());

		if (!InterfaceTaskStatus.SUCCESS.toString().equals(
				getITaskStatus(lTask.getTaskOid()))
				&& !InterfaceTaskStatus.PENDING.toString().equals(
						getITaskStatus(lTask.getTaskOid()))) {
			try {
				// Here we need inside exception if any to distinguish whether
				// the process failure comes after an concurrent issue
				new FWJPAWriteTransaction() {
					@Override
					protected void process() {

						// 1. Get processor name class by task type
						String processorClass = LbpfProcessorDispatcherManager.instance
								.getTaskProcessor(lTask.getActionType());
						try {

							// 2. Create processor instance
							LbpfProcessor processor = ((LbpfProcessor) Class
									.forName(processorClass).newInstance());

							// 3. Process this task
							processor.process(lTask.getTaskOid());

							// 4. Update task status
							updateITaskstatus(
									lTask.getTaskOid(),
									processor.isProcessorSynchronized() ? InterfaceTaskStatus.SUCCESS
											.toString()
											: InterfaceTaskStatus.PENDING
													.toString());
						} catch (Exception e) {
							throw new RuntimeException(e);
						}
					}
				}.execute();
			} catch (FWJPATransactionException e) {
				// 5. If exception, convert this Input event to an Exception
				// event
				EventManager.instance.publishEvent(new ExceptionStreamEvent(
						lTask, e));
				return;
			}
		}

		// 6. Else, continue the following process
		if (InterfaceTaskStatus.SUCCESS.toString().equals(
				getITaskStatus(lTask.getTaskOid()))) {
			// 6.1. Convert this Input event to an Output event
			EventManager.instance.publishEvent(new OutputStreamEvent(lTask));
			if (lTask.isRootTask()) {
				// 6.2. Update pipeline status
				updatePipelineStatus(lTask.getPipelineOid());
			}
		}
	}

}

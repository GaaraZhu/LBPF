package com.easys.lbpf.processor;

import java.util.concurrent.Callable;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.easys.entity.backendtask.InterfaceTask;
import com.easys.frm.component.SimpleResultableCallback;
import com.easys.frm.spring.ApplicationContextHolder;
import com.easys.frm.transaction.FWJPATransactionUtils;
import com.easys.service.appinterface.InterfaceTaskService;
import com.easys.service.lbpf.model.LightweightTask;
import com.easys.service.lbpf.model.event.InputStreamEvent;
import com.easys.service.lbpf.util.EventManager;

/**
 * @ClassName: PipeLineWorker
 * @Description: working unit of task pipeline
 * @author ZHUGA3
 * 
 * @date Oct 24, 2013
 */
public class PipeLineWorker implements Callable<Object>,
		Comparable<PipeLineWorker> {

	private static Logger logger = LogManager.getLogger(PipeLineWorker.class);

	private int priority;

	private long taskOid;

	public PipeLineWorker(long taskOid, int priority) {
		this.priority = priority;
		this.taskOid = taskOid;
	}

	@Override
	public int compareTo(PipeLineWorker o) {
		return Integer.valueOf(this.priority).compareTo(
				Integer.valueOf(o.priority));
	}

	@Override
	public Object call() throws Exception {
		// 1. Find out root interface task by task oid
		InterfaceTask rootITask = FWJPATransactionUtils
				.executeInReadTx(new SimpleResultableCallback<InterfaceTask>() {
					@Override
					public InterfaceTask doInCallback() {
						return ((InterfaceTaskService) ApplicationContextHolder
								.getInstance().getBean("interfaceTaskService"))
								.findObject(Long.valueOf(taskOid));
					}
				});
		logger.debug("LBPF PROCESSOR WORKER: ROOT TASK OID IS " + taskOid);
		
		// 2. Create lightweighttask for LBPF(a lightweight interface task)
		LightweightTask rootLTask = new LightweightTask(taskOid,
				rootITask.getAction(), true, rootITask.getTaskPipeline()
						.getOid());

		createLightweightTask(rootITask, rootLTask);

		// 3. publish an input event for root task
		EventManager.instance.publishEvent(new InputStreamEvent(rootLTask));

		return null;
	}

	/*
	 * InterfaceTask is a application domain model which is more compliated than
	 * lightweightTask. The transform between interfaceTask and lightweightTask
	 * is somehow like to clone a directed graph, since no cycle inside the
	 * graph, a depth-first traverse recursion will make it
	 */
	private void createLightweightTask(InterfaceTask rootITask,
			LightweightTask rootLTask) {
		if (!CollectionUtils.isEmpty(rootITask.getSubTasks())) {
			for (InterfaceTask iTask : rootITask.getSubTasks()) {
				LightweightTask subLTask = new LightweightTask(iTask.getOid(),
						iTask.getAction(), false, rootLTask.getPipelineOid());
				rootLTask.addSubTask(subLTask);
				for (InterfaceTask ipreTask : iTask.getPreTasks()) {
					subLTask.getPreTasks().add(
							new LightweightTask(ipreTask.getOid(), ipreTask
									.getAction(), false, rootLTask
									.getPipelineOid()));
				}

				createLightweightTask(iTask, subLTask);
			}
		}
	}

}

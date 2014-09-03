package com.easys.lbpf.processor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.easys.frm.component.SimpleResultableCallback;
import com.easys.frm.transaction.FWJPATransactionUtils;
import com.easys.service.appinterface.InterfaceTaskService;
import com.easys.service.lbpf.LbpfService;

/**
 * @ClassName: DefaultLbpfService
 * @Description: LBPF Trigger, responsible for task execution, involving
 *               retrieving all executable task pipelines and dispatching to
 *               multiple working units within a thread pool.
 * @author ZHUGA3
 * 
 * @date Oct 23, 2013
 */
public class DefaultLbpfService implements LbpfService {

	private static Logger logger = LogManager
			.getLogger(DefaultLbpfService.class);

	private InterfaceTaskService taskService;

	private ExecutorService lbpfExecutor;

	public void execute() {

		try {
			/*
			 * 1. Find out all active task pipelines
			 */
			List<Number> activeHeaderTask = FWJPATransactionUtils
					.executeInReadTx(new SimpleResultableCallback<List<Number>>() {
						@Override
						public List<Number> doInCallback() {
							return getTaskService().findActiveTasks4LBPF();
						}
					});

			/*
			 * 2. Execute the task pipelines with the help of thread pool
			 */
			if (!CollectionUtils.isEmpty(activeHeaderTask)) {
				List<PipeLineWorker> workers = new ArrayList<PipeLineWorker>(
						256);
				for (Number oid : activeHeaderTask) {
					PipeLineWorker worker = new PipeLineWorker(oid.longValue(),
							0);
					workers.add(worker);
				}
				ExecutorService executor = getLbpfExecutor();
				try {
					executor.invokeAll(workers);
				} catch (InterruptedException e) {
					logger.warn("LBPF PROCESSOR: EXCEPTION OCCURS WHEN INVOKE TASKS' EXECUTION..."
							+ ExceptionUtils.getFullStackTrace(e));
				}
			}
		} catch (Exception e) { // NOPMD
			logger.warn("LBPF PROCESSOR: EXCEPTION OCCURS IN LBPF SERVICE..."
					+ ExceptionUtils.getFullStackTrace(e));
		}  
	}

	public ExecutorService getLbpfExecutor() {
		return lbpfExecutor;
	}

	public void setLbpfExecutor(ExecutorService lbpfExecutor) {
		this.lbpfExecutor = lbpfExecutor;
	}

	public InterfaceTaskService getTaskService() {
		return taskService;
	}

	public void setTaskService(InterfaceTaskService taskService) {
		this.taskService = taskService;
	}

}

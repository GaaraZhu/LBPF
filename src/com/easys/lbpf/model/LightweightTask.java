package com.easys.lbpf.model;

import java.util.ArrayList;
import java.util.List;

public class LightweightTask implements java.io.Serializable {

	private static final long serialVersionUID = 3605023303623284003L;

	private long taskOid;

	private String actionType;

	private long pipelineOid;
	
	private boolean isRootTask;

	private List<LightweightTask> subTasks = new ArrayList<LightweightTask>();

	private List<LightweightTask> preTasks = new ArrayList<LightweightTask>();

	public LightweightTask(long taskOid, String actionType, boolean isRootTask,
			long pipelineOid) {
		this.taskOid = taskOid;
		this.isRootTask = isRootTask;
		this.actionType = actionType;
		this.pipelineOid = pipelineOid;

	}

	public long getPipelineOid() {
		return pipelineOid;
	}

	public void setPipelineOid(long pipelineOid) {
		this.pipelineOid = pipelineOid;
	}

	public boolean isRootTask() {
		return isRootTask;
	}

	public void setRootTask(boolean isRootTask) {
		this.isRootTask = isRootTask;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public List<LightweightTask> getSubTasks() {
		return subTasks;
	}

	public void setSubTasks(List<LightweightTask> subTasks) {
		this.subTasks = subTasks;
	}

	public List<LightweightTask> getPreTasks() {
		return preTasks;
	}

	public void setPreTasks(List<LightweightTask> preTasks) {
		this.preTasks = preTasks;
	}

	public void addPreTask(LightweightTask preTask) {
		this.preTasks.add(preTask);
	}

	public void addSubTask(LightweightTask subTask) {
		this.subTasks.add(subTask);
		subTask.addPreTask(this);
	}

	public long getTaskOid() {
		return taskOid;
	}

	public void setTaskOid(long taskOid) {
		this.taskOid = taskOid;
	}

}

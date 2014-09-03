package com.easys.lbpf.model.event;

import org.springframework.context.ApplicationEvent;

import com.easys.lbpf.model.LightweightTask;

/**
 * @ClassName: OutputStreamEvent
 * @Description:
 * @author ZHUGA3
 * 
 * @date Oct 23, 2013
 */
public class OutputStreamEvent extends ApplicationEvent {

	private LightweightTask currentStage;

	public OutputStreamEvent(LightweightTask currentStage) {
		super(new Object());
		this.currentStage = currentStage;
	}

	public LightweightTask getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(LightweightTask currentStage) {
		this.currentStage = currentStage;
	}

	private static final long serialVersionUID = -5127499937533004909L;

}

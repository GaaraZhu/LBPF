package com.easys.lbpf.model.event;

import org.springframework.context.ApplicationEvent;

import com.easys.lbpf.model.LightweightTask;

/**
 * @ClassName: ExceptionStreamEvent
 * @Description:
 * @author ZHUGA3
 * 
 * @date Oct 23, 2013
 */
public class ExceptionStreamEvent extends ApplicationEvent {

	public ExceptionStreamEvent(LightweightTask currentStage,
			Exception exception) {
		super(new Object());
		this.currentStage = currentStage;
		this.exception = exception;
	}

	private LightweightTask currentStage;

	public LightweightTask getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(LightweightTask currentStage) {
		this.currentStage = currentStage;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	private static final long serialVersionUID = 5261936501720543153L;
	private Exception exception;
}

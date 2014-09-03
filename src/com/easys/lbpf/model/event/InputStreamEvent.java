package com.easys.lbpf.model.event;

import org.springframework.context.ApplicationEvent;

import com.easys.lbpf.model.LightweightTask;

/**
 * @ClassName: InputStreamEvent
 * @Description: Java event used to monitor the process stream within task
 *               pipeline network. Attention that, no inheritance should be used
 *               within Event POJO(s), 'cause it would lead to event
 *               multi-processings.
 * @author ZHUGA3
 * @date Oct 23, 2013
 */
public class InputStreamEvent extends ApplicationEvent {

	private LightweightTask currentStage;

	/**
	 * @param currentStage
	 * @param isRootStage
	 */
	public InputStreamEvent(LightweightTask currentStage) {
		// Attention: Spring Event FWK takes Event Type and Event Source Object
		// Type as an unique key for event listener, here we only care about the
		// event type, hence simply use Object type as the event source type
		super(new Object());
		this.currentStage = currentStage;
	}

	public LightweightTask getCurrentStage() {
		return currentStage;
	}

	public void setCurrentStage(LightweightTask currentStage) {
		this.currentStage = currentStage;
	}

	private static final long serialVersionUID = -6855044300379366418L;

}

package com.easys.lbpf.util;

import java.util.Map;

/**
 * @ClassName: LbpfProcessorDispatcher
 * @Description: This class provide the mapping between different task type and
 *               corresponding process class
 * @author ZHUGA3
 * 
 * @date Nov 5, 2013
 */
public class LbpfProcessorDispatcher {

	private Map<String, String> dispatcher;

	public Map<String, String> getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(Map<String, String> dispatcher) {
		this.dispatcher = dispatcher;
	}

}

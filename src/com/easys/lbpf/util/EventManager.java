package com.easys.lbpf.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

import com.easys.frm.spring.ApplicationContextHolder;

/**
 * @ClassName: EventManager
 * @Description: Singleton pattern implemented in Enum way, attention that, Enum
 *               is a static type, which can not be designed as an injection
 *               target, something e.g. like ApplicationContextAware, retrieving
 *               that from ApplicationContextHolder is recommended
 * @author ZHUGA3
 * 
 * @date Oct 24, 2013
 */
public enum EventManager {
	instance {
		private ApplicationContext appContext;

		private ApplicationContext getApplicationContext() {
			if (null == this.appContext) {
				this.appContext = ApplicationContextHolder.instance
						.getAppContext();
			}
			return appContext;
		}

		public void publishEvent(ApplicationEvent event) {
			getApplicationContext().publishEvent(event);
		}
	};

	public abstract void publishEvent(ApplicationEvent event);
}

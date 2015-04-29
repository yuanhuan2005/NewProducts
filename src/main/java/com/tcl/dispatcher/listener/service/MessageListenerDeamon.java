package com.tcl.dispatcher.listener.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.dispatcher.common.service.CommonService;

public class MessageListenerDeamon extends Thread
{
	final static private Log DEBUGGER = LogFactory.getLog(MessageListenerDeamon.class);

	public MessageListenerDeamon()
	{
		setDaemon(true);
	}

	@Override
	public void run()
	{
		// ��ȡ��Ϣ������ʱ��������λ����
		int queueMessageCheckDuration = 60;
		String queueMessageCheckDurationstr = CommonService.getDispatcherConfValue("queueMessageCheckDuration");
		if (null != queueMessageCheckDurationstr && !"".equals(queueMessageCheckDurationstr))
		{
			queueMessageCheckDuration = Integer.valueOf(queueMessageCheckDurationstr);
		}

		while (true)
		{
			MessageListenerDeamon.DEBUGGER.info("begin to dispatch message");

			CommonService.doSleep(queueMessageCheckDuration);

			MessageListenerDeamon.DEBUGGER.info("end to dispatch message");
		}
	}
}

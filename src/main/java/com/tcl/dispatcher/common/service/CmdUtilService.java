package com.tcl.dispatcher.common.service;

public class CmdUtilService
{
	/**
	 * ��ȡffmpegִ�еĳ�ʱʱ��
	 * 
	 * @return ��ʱʱ��
	 */
	public static long getCommandExecTimeout()
	{
		long timeout = 86400000;
		String confTimeout = CommonService.getDispatcherConfValue("cmdExecTimeout");
		if (CommonService.isStringNotNull(confTimeout))
		{
			timeout = Long.valueOf(confTimeout);
			timeout = timeout * 1000;
		}
		return timeout;
	}

	/**
	 * ��ȡffmpegִ�н������ʱ����
	 * 
	 * @return ʱ����
	 */
	public static long getCommandResultCheckInterval()
	{
		long interval = 300000;
		String confInterval = CommonService.getDispatcherConfValue("cmdResultCheckInterval");
		if (CommonService.isStringNotNull(confInterval))
		{
			interval = Long.valueOf(confInterval) * 1000;
		}
		return interval;
	}
}

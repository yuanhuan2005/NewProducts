package com.tcl.dispatcher.common.service;

public class CmdUtilService
{
	/**
	 * 获取ffmpeg执行的超时时间
	 * 
	 * @return 超时时间
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
	 * 获取ffmpeg执行结果检查的时间间隔
	 * 
	 * @return 时间间隔
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

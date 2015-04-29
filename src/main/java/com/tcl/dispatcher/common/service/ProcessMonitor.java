package com.tcl.dispatcher.common.service;

public class ProcessMonitor extends Thread
{
	private final Process process;
	private Integer exitValue;

	public Integer getExitValue()
	{
		return exitValue;
	}

	public void setExitValue(Integer exitValue)
	{
		this.exitValue = exitValue;
	}

	public ProcessMonitor(Process process)
	{
		this.process = process;
	}

	@Override
	public void run()
	{
		try
		{
			exitValue = process.waitFor();
		}
		catch (InterruptedException ignore)
		{
			return;
		}
	}
}

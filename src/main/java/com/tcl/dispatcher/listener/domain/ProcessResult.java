package com.tcl.dispatcher.listener.domain;

public class ProcessResult
{
	/**
	 * 执行结果：success/failed
	 */
	private String result;

	/**
	 * 失败消息
	 */
	private String message;

	public String getResult()
	{
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	@Override
	public String toString()
	{
		return "ProcessResult [message=" + message + ", result=" + result + "]";
	}

}

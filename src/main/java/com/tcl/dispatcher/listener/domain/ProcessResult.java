package com.tcl.dispatcher.listener.domain;

public class ProcessResult
{
	/**
	 * ִ�н����success/failed
	 */
	private String result;

	/**
	 * ʧ����Ϣ
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

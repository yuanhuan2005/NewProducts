package com.tcl.dispatcher.listener.domain;

import com.tcl.dispatcher.common.service.CommonService;

public class MessageReq
{
	/**
	 * ��ҵ��ID
	 */
	protected String jobId;

	/**
	 * ��ҵ�е�������ID
	 */
	protected String stepId;

	/**
	 * ��Ϣ���� ����ServiceType���еĶ���
	 */
	protected String serviceType;

	/**
	 * ��ƵID
	 */
	protected String videoId;

	/**
	 * �������Ƶ�ļ�·��
	 */
	protected String inputPath;

	/**
	 * �������Ƶ�ļ�·��
	 */
	protected String outputPath;

	public String getJobId()
	{
		return jobId;
	}

	public void setJobId(String jobId)
	{
		this.jobId = jobId;
	}

	public String getStepId()
	{
		return stepId;
	}

	public void setStepId(String stepId)
	{
		this.stepId = stepId;
	}

	public String getServiceType()
	{
		return serviceType;
	}

	public void setServiceType(String serviceType)
	{
		this.serviceType = serviceType;
	}

	public String getVideoId()
	{
		return videoId;
	}

	public void setVideoId(String videoId)
	{
		this.videoId = videoId;
	}

	public String getInputPath()
	{
		return inputPath;
	}

	public void setInputPath(String inputPath)
	{
		this.inputPath = inputPath;
	}

	public String getOutputPath()
	{
		return outputPath;
	}

	public void setOutputPath(String outputPath)
	{
		this.outputPath = outputPath;
	}

	@Override
	public String toString()
	{
		return "MessageReq [jobId=" + jobId + ", stepId=" + stepId + ", serviceType=" + serviceType + ", videoId="
		        + videoId + ", inputPath=" + inputPath + ", outputPath=" + outputPath + "]";
	}

	/**
	 * ��鹫����ѡ����
	 * 
	 * @return true��ʾ��ѡ�������OK��false��ʾȱ�ٱ�ѡ����
	 */
	public boolean checkPublicRequiredArguments()
	{
		if (CommonService.isStringNull(jobId))
		{
			return false;
		}

		if (CommonService.isStringNull(stepId))
		{
			return false;
		}

		if (CommonService.isStringNull(inputPath))
		{
			return false;
		}

		if (CommonService.isStringNull(outputPath))
		{
			return false;
		}

		if (CommonService.isStringNull(serviceType))
		{
			return false;
		}

		if (CommonService.isStringNull(videoId))
		{
			return false;
		}

		return true;
	}

}

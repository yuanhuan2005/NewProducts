package com.tcl.dispatcher.listener.domain;

import com.tcl.dispatcher.common.service.CommonService;

public class MessageReq
{
	/**
	 * 作业的ID
	 */
	protected String jobId;

	/**
	 * 作业中的任务步骤ID
	 */
	protected String stepId;

	/**
	 * 消息类型 ，见ServiceType类中的定义
	 */
	protected String serviceType;

	/**
	 * 视频ID
	 */
	protected String videoId;

	/**
	 * 输入的视频文件路径
	 */
	protected String inputPath;

	/**
	 * 输出的视频文件路径
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
	 * 检查公共必选参数
	 * 
	 * @return true表示必选参数检查OK，false表示缺少必选参数
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

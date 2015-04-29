package com.tcl.dispatcher.common.domain;

public class AudioVideoInfo
{
	/**
	 * ��Ƶ������
	 */
	protected Number audioBitrate;

	/**
	 * ��Ƶ������
	 */
	protected Number audioSamplingRate;

	/**
	 * ��Ƶ������
	 */
	protected Number videoBitrate;

	/**
	 * ��Ƶ֡��
	 */
	protected Number videoFrameRate;

	/**
	 * ��Ƶ�ֱ���
	 */
	protected String videoFrameSize;

	/**
	 * �ļ���ʽ
	 */
	protected String fileFormat;

	public Number getAudioBitrate()
	{
		return audioBitrate;
	}

	public void setAudioBitrate(Number audioBitrate)
	{
		this.audioBitrate = audioBitrate;
	}

	public Number getAudioSamplingRate()
	{
		return audioSamplingRate;
	}

	public void setAudioSamplingRate(Number audioSamplingRate)
	{
		this.audioSamplingRate = audioSamplingRate;
	}

	public Number getVideoBitrate()
	{
		return videoBitrate;
	}

	public void setVideoBitrate(Number videoBitrate)
	{
		this.videoBitrate = videoBitrate;
	}

	public Number getVideoFrameRate()
	{
		return videoFrameRate;
	}

	public void setVideoFrameRate(Number videoFrameRate)
	{
		this.videoFrameRate = videoFrameRate;
	}

	public String getVideoFrameSize()
	{
		return videoFrameSize;
	}

	public void setVideoFrameSize(String videoFrameSize)
	{
		this.videoFrameSize = videoFrameSize;
	}

	public String getFileFormat()
	{
		return fileFormat;
	}

	public void setFileFormat(String fileFormat)
	{
		this.fileFormat = fileFormat;
	}

	@Override
	public String toString()
	{
		return "AudioVideoInfo [audioBitrate=" + audioBitrate + ", audioSamplingRate=" + audioSamplingRate
		        + ", videoBitrate=" + videoBitrate + ", videoFrameRate=" + videoFrameRate + ", videoFrameSize="
		        + videoFrameSize + ", fileFormat=" + fileFormat + "]";
	}

}

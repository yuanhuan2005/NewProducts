package com.tcl.dispatcher.listener.domain;

public interface ServiceType
{

	/**
	 * 视频转码
	 */
	final static int TRANSCODING = 1;

	/**
	 * 视频剪切
	 */
	final static int CLIPPING = 2;

	/**
	 * object detection
	 */
	final static int OBJECT_DETECTION = 3;

	/**
	 * script
	 */
	final static int SCRIPT = 5;

	/**
	 * face detection
	 */
	final static int FACE_DETECTION = 6;

	/**
	 * 视频截图
	 */
	final static int SNAPSHOTS = 7;

	/**
	 * 视频插入
	 */
	final static int INSERTION = 8;

	/**
	 * 视频添加LOGO
	 */
	final static int LOGO = 9;

	/**
	 * face recognition
	 */
	final static int FACE_RECOGNITION = 10;

	/**
	 * picture book
	 */
	final static int PICTURE_BOOK = 11;
}

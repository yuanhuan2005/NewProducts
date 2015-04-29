package com.tcl.dispatcher.listener.domain;

public interface ServiceType
{

	/**
	 * ��Ƶת��
	 */
	final static int TRANSCODING = 1;

	/**
	 * ��Ƶ����
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
	 * ��Ƶ��ͼ
	 */
	final static int SNAPSHOTS = 7;

	/**
	 * ��Ƶ����
	 */
	final static int INSERTION = 8;

	/**
	 * ��Ƶ���LOGO
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

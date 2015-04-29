package com.tcl.dispatcher.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.tcl.dispatcher.common.service.CommonService;

public class TestPrintDebugLog
{
	/**
	 * ����Ϊ��λ��ȡ�ļ��������ڶ������еĸ�ʽ���ļ�
	 */
	public static void readFileByLines(String fileName)
	{
		File file = new File(fileName);
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			while (true)
			{
				tempString = reader.readLine();
				if (CommonService.isStringNotNull(tempString))
				{
					System.out.println(tempString);
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e1)
				{
					e1.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args)
	{
		System.out.println("Enter main");

		String debugLogFilePath = "//192.168.1.228/Volume_1/face_workspace/face_debug_log.txt";
		TestPrintErrorLog.readFileByLines(debugLogFilePath);

		System.out.println("End main");
	}
}

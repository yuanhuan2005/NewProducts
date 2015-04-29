package com.tcl.dispatcher.test;

import java.io.File;

import com.tcl.dispatcher.common.service.CommonService;

public class TestRenameFileName
{
	private static void renameFile(String dirPath, String oldStr, String newStr)
	{
		try
		{
			File dest = null;
			File folder = new File(dirPath);
			File[] files = folder.listFiles();
			if (null != files)
			{
				for (File file : files)
				{
					if (file.isDirectory())
					{
						TestRenameFileName.renameFile(file.getAbsolutePath(), oldStr, newStr);
					}
					// else
					{
						String fileName = file.getName();
						String newFileName = fileName.replaceAll(oldStr, newStr);
						if (!fileName.equals(newFileName))
						{
							String newFilePath = CommonService.addSlashToDirPathIfNecessary(file.getParent())
							        + newFileName;
							dest = new File(newFilePath);
							file.renameTo(dest);
							System.out.println("new file name: " + dest.getAbsolutePath());
						}
					}
				}
			}
			System.out.println("file name: " + folder.getAbsolutePath());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		System.out.println("Enter main");
		String dirPath = "U:/TCL_workspace/PictureBook/results_old/";

		TestRenameFileName.renameFile(dirPath, "���ֱ�֮����", "tzbzhfh");
		TestRenameFileName.renameFile(dirPath, "һ��������", "yklmx");
		TestRenameFileName.renameFile(dirPath, "��������", "sgyy");
		TestRenameFileName.renameFile(dirPath, "��", "di");
		TestRenameFileName.renameFile(dirPath, "��", "ji");

		/*
		TestRenameFileName.renameFile(dirPath, "tzbzhfhdi", "���ֱ�֮���˵�");
		TestRenameFileName.renameFile(dirPath, "yklmxdi", "һ���������");
		TestRenameFileName.renameFile(dirPath, "sgyydi", "���������");
		TestRenameFileName.renameFile(dirPath, "tzbzhfh", "���ֱ�֮����");
		TestRenameFileName.renameFile(dirPath, "yklmx", "һ��������");
		TestRenameFileName.renameFile(dirPath, "sgyy", "��������");
		TestRenameFileName.renameFile(dirPath, "ji", "��");
		*/

		System.out.println("End main");
	}
}

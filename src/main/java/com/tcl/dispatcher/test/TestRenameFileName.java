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

		TestRenameFileName.renameFile(dirPath, "特种兵之火凤凰", "tzbzhfh");
		TestRenameFileName.renameFile(dirPath, "一克拉梦想", "yklmx");
		TestRenameFileName.renameFile(dirPath, "三国演义", "sgyy");
		TestRenameFileName.renameFile(dirPath, "第", "di");
		TestRenameFileName.renameFile(dirPath, "集", "ji");

		/*
		TestRenameFileName.renameFile(dirPath, "tzbzhfhdi", "特种兵之火凤凰第");
		TestRenameFileName.renameFile(dirPath, "yklmxdi", "一克拉梦想第");
		TestRenameFileName.renameFile(dirPath, "sgyydi", "三国演义第");
		TestRenameFileName.renameFile(dirPath, "tzbzhfh", "特种兵之火凤凰");
		TestRenameFileName.renameFile(dirPath, "yklmx", "一克拉梦想");
		TestRenameFileName.renameFile(dirPath, "sgyy", "三国演义");
		TestRenameFileName.renameFile(dirPath, "ji", "集");
		*/

		System.out.println("End main");
	}
}

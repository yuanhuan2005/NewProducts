package com.tcl.dispatcher.test;

import java.io.IOException;
import java.util.Date;

import org.apache.http.client.ClientProtocolException;

public class Test
{

	public static void main(String[] args) throws ClientProtocolException, IOException, InterruptedException
	{
		System.out.println("Enter main");
		long startTime = new Date().getTime();

		String jsonStr = "{\"2\":\"2\",}";

		jsonStr = jsonStr.replaceAll(",}", "}");
		System.out.println("jsonStr: " + jsonStr);

		long endTime = new Date().getTime();
		System.out.println("Cost time: " + (endTime - startTime) + " ms");
		System.out.println("End main");
	}
}

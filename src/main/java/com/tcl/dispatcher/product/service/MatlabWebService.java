package com.tcl.dispatcher.product.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.MatlabProxyFactoryOptions;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/")
public class MatlabWebService
{
	final static private Log DEBUGGER = LogFactory.getLog(MatlabWebService.class);

	synchronized public void doMatlab(String searchPath) throws MatlabConnectionException, MatlabInvocationException
	{
		MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder()
		        .setUsePreviouslyControlledSession(false).setHidden(true).setMatlabLocation(null).build();
		MatlabProxyFactory factory = new MatlabProxyFactory(options);
		MatlabProxy proxy = null;

		proxy = factory.getProxy();
		String funcStr = "main()";
		MatlabWebService.DEBUGGER.debug(funcStr);

		proxy.eval("addpath('" + searchPath + "')");
		proxy.eval("disp('Add search path " + searchPath + "')");
		proxy.eval(funcStr);
		proxy.disconnect();
	}

	synchronized public void doMatlabBak(String searchPath)
	{
		MatlabProxyFactoryOptions options = new MatlabProxyFactoryOptions.Builder()
		        .setUsePreviouslyControlledSession(false).setHidden(true).setMatlabLocation(null).build();
		MatlabProxyFactory factory = new MatlabProxyFactory(options);
		MatlabProxy proxy = null;

		try
		{
			proxy = factory.getProxy();
			String funcStr = "main()";
			MatlabWebService.DEBUGGER.debug(funcStr);

			proxy.eval("addpath('" + searchPath + "')");
			proxy.eval("disp('Add search path " + searchPath + "')");
			proxy.eval(funcStr);
		}
		catch (MatlabConnectionException e)
		{
			MatlabWebService.DEBUGGER.error("Exception: " + e.toString());
		}
		catch (MatlabInvocationException e)
		{
			MatlabWebService.DEBUGGER.error("Exception: " + e.toString());
		}
		finally
		{
			if (null != proxy)
			{
				proxy.disconnect();
			}
		}
	}

	@RequestMapping(value = "/testMatlab", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String testMatlab(HttpServletRequest request, HttpServletResponse response)
	{
		MatlabWebService.DEBUGGER.debug("enter testMatlab");
		String respStr = "success";

		try
		{
			doMatlab("/home/fausto/test");
			// doMatlab("C:/yuanhuan");
		}
		catch (MatlabConnectionException e)
		{
			MatlabWebService.DEBUGGER.error("Exception: " + e.toString());
			respStr = "Exception: " + e.toString();
		}
		catch (MatlabInvocationException e)
		{
			if (e.toString().indexOf("Object attempting to be received cannot be transferred") > 0)
			{
				MatlabWebService.DEBUGGER.debug("end testMatlab");
				return respStr;
			}

			MatlabWebService.DEBUGGER.error("Exception: " + e.toString());
			respStr = "Exception: " + e.toString();
		}

		MatlabWebService.DEBUGGER.debug("end testMatlab");
		return respStr;
	}
}

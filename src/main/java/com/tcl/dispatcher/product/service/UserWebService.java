package com.tcl.dispatcher.product.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.datastax.driver.core.utils.UUIDs;
import com.tcl.dispatcher.common.service.CommonService;
import com.tcl.dispatcher.product.dao.UserDao;
import com.tcl.dispatcher.product.domain.User;

@Controller
@RequestMapping("/")
public class UserWebService
{
	final static private Log DEBUGGER = LogFactory.getLog(UserWebService.class);

	@RequestMapping(value = "/getUser", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String getUser(HttpServletRequest request, HttpServletResponse response)
	{
		UserWebService.DEBUGGER.debug("enter getUser");

		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");
		String userId = "";
		try
		{
			userId = new String(request.getParameter("userId").getBytes("ISO-8859-1"), "UTF-8");
			UserWebService.DEBUGGER.debug("userId = " + userId);
		}
		catch (Exception e)
		{
			response.setStatus(400);
			UserWebService.DEBUGGER.error("Exception: " + e.toString());
			UserWebService.DEBUGGER.debug("end getUser");
			return CommonService.genErrorMessageInJson("fail", e.toString());
		}

		UserDao userDao = new UserDao();
		User user = userDao.getUser(userId);
		if (null == user)
		{
			response.setStatus(400);
			UserWebService.DEBUGGER.error("user not found");
			UserWebService.DEBUGGER.debug("end getUser");
			return CommonService.genErrorMessageInJson("fail", "user not found");
		}

		UserWebService.DEBUGGER.debug("user = " + user.toString());
		UserWebService.DEBUGGER.debug("end getUser");
		return user.toJsonString();
	}

	@RequestMapping(value = "/addUser", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String addUser(HttpServletRequest request, HttpServletResponse response)
	{
		UserWebService.DEBUGGER.debug("enter addUser");

		User user = null;
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");
		String userId = "";
		String userName = "";
		String gender = "";
		int age = 0;
		try
		{
			userId = UUIDs.random().toString();
			userName = new String(request.getParameter("userName").getBytes("ISO-8859-1"), "UTF-8");
			gender = new String(request.getParameter("gender").getBytes("ISO-8859-1"), "UTF-8");
			age = Integer.valueOf(new String(request.getParameter("age").getBytes("ISO-8859-1"), "UTF-8"));
			user = new User();
			user.setUserId(userId);
			user.setUserName(userName);
			user.setGender(gender);
			user.setAge(age);
			UserDao userDao = new UserDao();
			boolean result = userDao.addUser(user);
			if (!result)
			{
				response.setStatus(400);
				UserWebService.DEBUGGER.error("failed to add user");
				UserWebService.DEBUGGER.debug("end addUser");
				return CommonService.genErrorMessageInJson("fail", "failed to add user");
			}
		}
		catch (Exception e)
		{
			response.setStatus(400);
			UserWebService.DEBUGGER.error("Exception: " + e.toString());
			UserWebService.DEBUGGER.debug("end addUser");
			return CommonService.genErrorMessageInJson("fail", e.toString());
		}

		UserWebService.DEBUGGER.debug("end addUser");
		return user.toJsonString();
	}
}

package com.tcl.dispatcher.product.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONObject;
import com.datastax.driver.core.utils.UUIDs;
import com.tcl.dispatcher.common.service.CommonService;
import com.tcl.dispatcher.product.dao.SubscriptionDao;
import com.tcl.dispatcher.product.domain.Subscription;
import com.tcl.dispatcher.product.domain.SubscriptionStatus;
import com.tcl.dispatcher.test.RkitHttpResponse;

@Controller
@RequestMapping("/*")
public class SubscriptionWebService
{
	final static private Log DEBUGGER = LogFactory.getLog(SubscriptionWebService.class);

	private void updateUserSubscriptionsStatus(List<Subscription> userSubscriptionList)
	{
		SubscriptionDao subscriptionDao = new SubscriptionDao();
		if (null == userSubscriptionList || userSubscriptionList.isEmpty())
		{
			return;
		}

		RkitHttpResponse result = null;
		int statusCode = -1;
		String currSubscriptionContentMD5 = "";
		String lastTimeSubscriptionContentMD5 = "";

		for (Subscription subscription : userSubscriptionList)
		{
			subscription.setSubscriptionStatus(SubscriptionStatus.LATEST);

			result = CommonService.sendGetRequest(subscription.getSubscriptionUrl());
			statusCode = result.getHttpStatusCode();
			if (200 == statusCode)
			{
				currSubscriptionContentMD5 = CommonService.encodeStrByMd5(CommonService.deleteHtmlTags(result
				        .getResponseMessage()));
				lastTimeSubscriptionContentMD5 = subscriptionDao.getSubscription(subscription.getSubscriptionId())
				        .getSubscriptionContentMD5();

				if (!currSubscriptionContentMD5.equals(lastTimeSubscriptionContentMD5))
				{
					subscription.setSubscriptionContentMD5(currSubscriptionContentMD5);
					subscription.setSubscriptionStatus(SubscriptionStatus.NEED_TO_UPDATE);
					subscriptionDao.updateSubscription(subscription);
				}
			}
		}
	}

	private String convertUserSubscriptionListToJson(List<Subscription> userSubscriptionList)
	{
		JSONArray jsonArray = new JSONArray();
		if (null == userSubscriptionList || userSubscriptionList.isEmpty())
		{
			return jsonArray.toString();
		}

		try
		{
			for (Subscription subscription : userSubscriptionList)
			{
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("SubscriptionId", subscription.getSubscriptionId());
				jsonObject.put("SubscriptionName", subscription.getSubscriptionName());
				jsonObject.put("SubscriptionDesc", subscription.getSubscriptionDesc());
				jsonObject.put("SubscriptionDate", subscription.getSubscriptionDate());
				jsonObject.put("SubscriptionUrl", subscription.getSubscriptionUrl());
				jsonObject.put("SubscriptionContentMD5", subscription.getSubscriptionContentMD5());
				jsonObject.put("SubscriptionStatus", subscription.getSubscriptionStatus());
				jsonArray.put(jsonObject);
			}
		}
		catch (Exception e)
		{
			return jsonArray.toString();
		}

		return jsonArray.toString();
	}

	@RequestMapping(value = "/getUserSubscriptions", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String getUserSubscriptions(HttpServletRequest request, HttpServletResponse response)
	{
		SubscriptionWebService.DEBUGGER.debug("enter getUserSubscriptions");

		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");
		String userId = "";
		List<Subscription> userSubscriptionList = null;
		try
		{
			userId = new String(request.getParameter("userId").getBytes("ISO-8859-1"), "UTF-8");
			SubscriptionDao subscriptionDao = new SubscriptionDao();
			userSubscriptionList = subscriptionDao.getUserSubscriptionList(userId);
			updateUserSubscriptionsStatus(userSubscriptionList);
		}
		catch (Exception e)
		{
			response.setStatus(400);
			SubscriptionWebService.DEBUGGER.error("Exception: " + e.toString());
			SubscriptionWebService.DEBUGGER.debug("end getUserSubscriptions");
			return CommonService.genErrorMessageInJson("fail", e.toString());
		}

		SubscriptionWebService.DEBUGGER.debug("end getUserSubscriptions");
		return convertUserSubscriptionListToJson(userSubscriptionList);
	}

	@RequestMapping(value = "/addSubscription", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String addSubscription(HttpServletRequest request, HttpServletResponse response)
	{
		SubscriptionWebService.DEBUGGER.debug("enter addSubscription");

		Subscription subscription = null;
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");
		String subscriptionId = "";
		String subscriptionName = "";
		String subscriptionDesc = "";
		String subscriptionUrl = "";
		String userId = "";
		try
		{
			subscriptionId = UUIDs.random().toString();
			userId = new String(request.getParameter("userId").getBytes("ISO-8859-1"), "UTF-8");
			subscriptionName = new String(request.getParameter("subscriptionName").getBytes("ISO-8859-1"), "UTF-8");
			subscriptionDesc = new String(request.getParameter("subscriptionDesc").getBytes("ISO-8859-1"), "UTF-8");
			subscriptionUrl = new String(request.getParameter("subscriptionUrl").getBytes("ISO-8859-1"), "UTF-8");
			if (CommonService.isStringNull(subscriptionUrl))
			{
				response.setStatus(400);
				SubscriptionWebService.DEBUGGER.error("subscription url is null");
				SubscriptionWebService.DEBUGGER.debug("end addSubscription");
				return CommonService.genErrorMessageInJson("MissParameters", "subscription url is null");
			}

			RkitHttpResponse rkitHttpResponse = CommonService.sendGetRequest(subscriptionUrl);
			if (200 != rkitHttpResponse.getHttpStatusCode())
			{
				response.setStatus(400);
				SubscriptionWebService.DEBUGGER.error("subscription url can not access");
				SubscriptionWebService.DEBUGGER.debug("end addSubscription");
				return CommonService.genErrorMessageInJson("MissParameters", "subscription url can not access");
			}
			subscription = new Subscription();
			subscription.setSubscriptionId(subscriptionId);
			subscription.setSubscriptionName(subscriptionName);
			subscription.setSubscriptionDesc(subscriptionDesc);
			subscription.setSubscriptionDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			subscription.setSubscriptionUrl(subscriptionUrl);
			SubscriptionDao subscriptionDao = new SubscriptionDao();
			boolean result = subscriptionDao.addSubscription(subscription);
			if (!result)
			{
				response.setStatus(400);
				SubscriptionWebService.DEBUGGER.error("failed to add subscription");
				SubscriptionWebService.DEBUGGER.debug("end addSubscription");
				return CommonService.genErrorMessageInJson("fail", "failed to add subscription");
			}

			result = subscriptionDao.addUserSubscriptionMap(userId, subscriptionId);
			if (!result)
			{
				subscriptionDao.deleteSubscription(subscriptionId);
				response.setStatus(400);
				SubscriptionWebService.DEBUGGER.error("failed to add subscription");
				SubscriptionWebService.DEBUGGER.debug("end addSubscription");
				return CommonService.genErrorMessageInJson("fail", "failed to add subscription");
			}
		}
		catch (Exception e)
		{
			response.setStatus(400);
			SubscriptionWebService.DEBUGGER.error("Exception: " + e.toString());
			SubscriptionWebService.DEBUGGER.debug("end addSubscription");
			return CommonService.genErrorMessageInJson("fail", e.toString());
		}

		SubscriptionWebService.DEBUGGER.debug("end addSubscription");
		return subscription.toJsonString();
	}

	@RequestMapping(value = "/deleteSubscription", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	String deleteSubscription(HttpServletRequest request, HttpServletResponse response)
	{
		SubscriptionWebService.DEBUGGER.debug("enter deleteSubscription");

		Subscription subscription = null;
		String contentType = "text/json;charset=UTF-8";
		response.setContentType(contentType);
		response.setCharacterEncoding("utf-8");
		String subscriptionId = "";
		String userId = "";
		boolean result = false;

		try
		{
			userId = new String(request.getParameter("userId").getBytes("ISO-8859-1"), "UTF-8");
			subscriptionId = new String(request.getParameter("subscriptionId").getBytes("ISO-8859-1"), "UTF-8");
			SubscriptionDao subscriptionDao = new SubscriptionDao();
			subscription = subscriptionDao.getSubscription(subscriptionId);
			if (null == subscription)
			{
				response.setStatus(400);
				SubscriptionWebService.DEBUGGER.error("subscription not exist");
				SubscriptionWebService.DEBUGGER.debug("end deleteSubscription");
				return CommonService.genErrorMessageInJson("fail", "subscription not exist");
			}

			result = subscriptionDao.deleteSubscription(subscriptionId);
			if (!result)
			{
				response.setStatus(400);
				SubscriptionWebService.DEBUGGER.error("failed to delete subscription");
				SubscriptionWebService.DEBUGGER.debug("end deleteSubscription");
				return CommonService.genErrorMessageInJson("fail", "failed to delete subscription");
			}

			result = subscriptionDao.deleteUserSubscriptionMap(userId, subscriptionId);
			if (!result)
			{
				subscriptionDao.addSubscription(subscription);
				response.setStatus(400);
				SubscriptionWebService.DEBUGGER.error("failed to delete subscription");
				SubscriptionWebService.DEBUGGER.debug("end deleteSubscription");
				return CommonService.genErrorMessageInJson("fail", "failed to delete subscription");
			}
		}
		catch (Exception e)
		{
			response.setStatus(400);
			SubscriptionWebService.DEBUGGER.error("Exception: " + e.toString());
			SubscriptionWebService.DEBUGGER.debug("end deleteSubscription");
			return CommonService.genErrorMessageInJson("fail", e.toString());
		}

		SubscriptionWebService.DEBUGGER.debug("end deleteSubscription");
		return subscription.toJsonString();
	}
}

package com.tcl.dispatcher.product.domain;

import com.tcl.dispatcher.common.service.CommonService;

public class Subscription
{
	private String subscriptionId;

	private String subscriptionName;

	private String subscriptionDesc;

	private String subscriptionDate;

	private String subscriptionUrl;

	private String subscriptionContentMD5;

	private String subscriptionStatus;

	public String getSubscriptionId()
	{
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId)
	{
		this.subscriptionId = subscriptionId;
	}

	public String getSubscriptionName()
	{
		return subscriptionName;
	}

	public void setSubscriptionName(String subscriptionName)
	{
		this.subscriptionName = subscriptionName;
	}

	public String getSubscriptionDesc()
	{
		return subscriptionDesc;
	}

	public void setSubscriptionDesc(String subscriptionDesc)
	{
		this.subscriptionDesc = subscriptionDesc;
	}

	public String getSubscriptionDate()
	{
		return subscriptionDate;
	}

	public void setSubscriptionDate(String subscriptionDate)
	{
		this.subscriptionDate = subscriptionDate;
	}

	public String getSubscriptionUrl()
	{
		return subscriptionUrl;
	}

	public void setSubscriptionUrl(String subscriptionUrl)
	{
		this.subscriptionUrl = subscriptionUrl;
	}

	public String getSubscriptionContentMD5()
	{
		return subscriptionContentMD5;
	}

	public void setSubscriptionContentMD5(String subscriptionContentMD5)
	{
		this.subscriptionContentMD5 = subscriptionContentMD5;
	}

	public String getSubscriptionStatus()
	{
		return subscriptionStatus;
	}

	public void setSubscriptionStatus(String subscriptionStatus)
	{
		this.subscriptionStatus = subscriptionStatus;
	}

	@Override
	public String toString()
	{
		return "Subscription [subscriptionId=" + subscriptionId + ", subscriptionName=" + subscriptionName
		        + ", subscriptionDesc=" + subscriptionDesc + ", subscriptionDate=" + subscriptionDate
		        + ", subscriptionUrl=" + subscriptionUrl + ", subscriptionContentMD5=" + subscriptionContentMD5
		        + ", subscriptionStatus=" + subscriptionStatus + "]";
	}

	public String toJsonString()
	{
		String jsonStr = "{";
		if (CommonService.isStringNotNull(subscriptionId))
		{
			jsonStr += "\"subscriptionId\":\"" + subscriptionId + "\",";
		}
		if (CommonService.isStringNotNull(subscriptionName))
		{
			jsonStr += "\"subscriptionName\":\"" + subscriptionName + "\",";
		}
		if (CommonService.isStringNotNull(subscriptionDesc))
		{
			jsonStr += "\"subscriptionDesc\":\"" + subscriptionDesc + "\",";
		}
		if (CommonService.isStringNotNull(subscriptionDate))
		{
			jsonStr += "\"subscriptionDate\":\"" + subscriptionDate + "\",";
		}
		if (CommonService.isStringNotNull(subscriptionUrl))
		{
			jsonStr += "\"subscriptionUrl\":\"" + subscriptionUrl + "\",";
		}
		if (CommonService.isStringNotNull(subscriptionContentMD5))
		{
			jsonStr += "\"subscriptionContentMD5\":\"" + subscriptionContentMD5 + "\",";
		}
		if (CommonService.isStringNotNull(subscriptionStatus))
		{
			jsonStr += "\"subscriptionStatus\":\"" + subscriptionStatus + "\",";
		}
		jsonStr += "}";
		jsonStr = jsonStr.replaceAll(",}", "}");

		return jsonStr;
	}
}

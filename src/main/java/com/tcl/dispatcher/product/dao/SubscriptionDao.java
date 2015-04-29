package com.tcl.dispatcher.product.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.dispatcher.common.service.CommonService;
import com.tcl.dispatcher.common.service.DbService;
import com.tcl.dispatcher.product.domain.Subscription;

public class SubscriptionDao
{
	final static private Log DEBUGGER = LogFactory.getLog(SubscriptionDao.class);

	private DbService dbService = null;

	private DbService getDbService()
	{
		if (null == dbService)
		{
			dbService = new DbService();
		}

		return dbService;
	}

	public boolean addSubscription(Subscription subscription)
	{
		if (null == subscription)
		{
			return false;
		}

		String sql = "insert into t_np_subscription(SubscriptionId,SubscriptionName,SubscriptionDesc,SubscriptionDate,SubscriptionUrl,SubscriptionContentMD5) "
		        + "values('"
		        + subscription.getSubscriptionId()
		        + "','"
		        + subscription.getSubscriptionName()
		        + "','"
		        + subscription.getSubscriptionDesc()
		        + "','"
		        + subscription.getSubscriptionDate()
		        + "','"
		        + subscription.getSubscriptionUrl() + "','" + subscription.getSubscriptionContentMD5() + "')";
		boolean result = getDbService().insertSQL(sql);
		return result;
	}

	public boolean addUserSubscriptionMap(String userId, String subscriptionId)
	{
		if (CommonService.isStringNull(userId) || CommonService.isStringNull(subscriptionId))
		{
			return false;
		}

		String sql = "insert into t_np_user_subscription_map(UserId,SubscriptionId) values('" + userId + "','"
		        + subscriptionId + "')";
		boolean result = getDbService().insertSQL(sql);
		return result;
	}

	public List<Subscription> getSubscriptionList()
	{
		List<Subscription> subscriptionList = new ArrayList<Subscription>();
		Subscription subscription = null;

		String sql = "select * from t_np_subscription";
		ResultSet resultSet = getDbService().selectSQL(sql);
		try
		{
			while (resultSet.next())
			{
				subscription = new Subscription();
				subscription.setSubscriptionId(resultSet.getString("SubscriptionId"));
				subscription.setSubscriptionName(resultSet.getString("SubscriptionName"));
				subscription.setSubscriptionDesc(resultSet.getString("SubscriptionDesc"));
				subscription.setSubscriptionDate(resultSet.getString("SubscriptionDate"));
				subscription.setSubscriptionUrl(resultSet.getString("SubscriptionUrl"));
				subscription.setSubscriptionContentMD5(resultSet.getString("SubscriptionContentMD5"));
				subscriptionList.add(subscription);
			}
		}
		catch (Exception e)
		{
			SubscriptionDao.DEBUGGER.error("Exception: " + e.toString());
		}

		return subscriptionList;
	}

	public List<Subscription> getUserSubscriptionList(String userId)
	{
		List<Subscription> subscriptionList = new ArrayList<Subscription>();
		Subscription subscription = null;

		if (CommonService.isStringNull(userId))
		{
			return subscriptionList;
		}

		String sql = "SELECT * FROM t_np_subscription s, t_np_user_subscription_map us " + "WHERE us.UserId = '"
		        + userId + "' " + "AND s.subscriptionId = us.subscriptionId";
		ResultSet resultSet = getDbService().selectSQL(sql);
		try
		{
			while (resultSet.next())
			{
				subscription = new Subscription();
				subscription.setSubscriptionId(resultSet.getString("SubscriptionId"));
				subscription.setSubscriptionName(resultSet.getString("SubscriptionName"));
				subscription.setSubscriptionDesc(resultSet.getString("SubscriptionDesc"));
				subscription.setSubscriptionDate(resultSet.getString("SubscriptionDate"));
				subscription.setSubscriptionUrl(resultSet.getString("SubscriptionUrl"));
				subscription.setSubscriptionContentMD5(resultSet.getString("SubscriptionContentMD5"));
				subscriptionList.add(subscription);
			}
		}
		catch (Exception e)
		{
			SubscriptionDao.DEBUGGER.error("Exception: " + e.toString());
		}

		return subscriptionList;
	}

	public Subscription getSubscription(String subscriptionId)
	{
		Subscription subscription = null;

		if (CommonService.isStringNull(subscriptionId))
		{
			return subscription;
		}

		String sql = "select * from t_np_subscription where SubscriptionId = '" + subscriptionId + "'";
		ResultSet resultSet = getDbService().selectSQL(sql);
		try
		{
			if (resultSet.next())
			{
				subscription = new Subscription();
				subscription.setSubscriptionId(resultSet.getString("SubscriptionId"));
				subscription.setSubscriptionName(resultSet.getString("SubscriptionName"));
				subscription.setSubscriptionDesc(resultSet.getString("SubscriptionDesc"));
				subscription.setSubscriptionDate(resultSet.getString("SubscriptionDate"));
				subscription.setSubscriptionUrl(resultSet.getString("SubscriptionUrl"));
				subscription.setSubscriptionContentMD5(resultSet.getString("SubscriptionContentMD5"));
			}
		}
		catch (Exception e)
		{
			SubscriptionDao.DEBUGGER.error("Exception: " + e.toString());
		}

		return subscription;
	}

	public boolean deleteSubscription(String subscriptionId)
	{
		if (CommonService.isStringNull(subscriptionId))
		{
			return false;
		}

		String sql = "delete from t_np_subscription where SubscriptionId = '" + subscriptionId + "'";
		boolean result = getDbService().deleteSQL(sql);
		return result;
	}

	public boolean deleteUserSubscriptionMap(String userId, String subscriptionId)
	{
		if (CommonService.isStringNull(subscriptionId))
		{
			return false;
		}

		String sql = "delete from t_np_user_subscription_map where UserId = '" + userId + "' and SubscriptionId = '"
		        + subscriptionId + "'";
		boolean result = getDbService().deleteSQL(sql);
		return result;
	}

	public boolean updateSubscription(Subscription subscription)
	{
		if (null == subscription)
		{
			return false;
		}

		String sql = "update t_np_subscription set SubscriptionName = '" + subscription.getSubscriptionName()
		        + "', SubscriptionDesc = '" + subscription.getSubscriptionDesc() + "', SubscriptionDate = '"
		        + subscription.getSubscriptionDate() + "', SubscriptionUrl = '" + subscription.getSubscriptionUrl()
		        + "', SubscriptionContentMD5 = '" + subscription.getSubscriptionContentMD5()
		        + "' where SubscriptionId = '" + subscription.getSubscriptionId() + "'";
		boolean result = getDbService().updateSQL(sql);
		return result;
	}
}

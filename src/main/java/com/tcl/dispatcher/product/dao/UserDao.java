package com.tcl.dispatcher.product.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.dispatcher.common.service.CommonService;
import com.tcl.dispatcher.common.service.DbService;
import com.tcl.dispatcher.product.domain.User;

public class UserDao
{
	final static private Log DEBUGGER = LogFactory.getLog(UserDao.class);

	private DbService dbService = null;

	private DbService getDbService()
	{
		if (null == dbService)
		{
			dbService = new DbService();
		}

		return dbService;
	}

	public boolean addUser(User user)
	{
		if (null == user)
		{
			return false;
		}

		String sql = "insert into t_np_user(UserId,UserName,Gender,Age) values('" + user.getUserId() + "','"
		        + user.getUserName() + "','" + user.getGender() + "'," + user.getAge() + ")";
		boolean result = getDbService().insertSQL(sql);
		return result;
	}

	public List<User> getUserList()
	{
		List<User> userList = new ArrayList<User>();
		User user = null;

		String sql = "select * from t_np_user";
		ResultSet resultSet = dbService.selectSQL(sql);
		try
		{
			while (resultSet.next())
			{
				user = new User();
				user.setUserId(resultSet.getString("UserId"));
				user.setUserName(resultSet.getString("UserName"));
				user.setGender(resultSet.getString("Gender"));
				user.setAge(resultSet.getInt("Age"));
				userList.add(user);
			}
		}
		catch (Exception e)
		{
			UserDao.DEBUGGER.error("Exception: " + e.toString());
		}

		return userList;
	}

	public User getUser(String userId)
	{
		User user = null;

		if (CommonService.isStringNull(userId))
		{
			return user;
		}

		String sql = "select * from t_np_user where UserId = '" + userId + "'";
		ResultSet resultSet = getDbService().selectSQL(sql);
		try
		{
			if (resultSet.next())
			{
				user = new User();
				user.setUserId(resultSet.getString("UserId"));
				user.setUserName(resultSet.getString("UserName"));
				user.setGender(resultSet.getString("Gender"));
				user.setAge(resultSet.getInt("Age"));
			}
		}
		catch (Exception e)
		{
			UserDao.DEBUGGER.error("Exception: " + e.toString());
		}

		return user;
	}

	public boolean deleteUser(String userId)
	{
		if (CommonService.isStringNull(userId))
		{
			return false;
		}

		String sql = "delete from t_np_user where UserId = '" + userId + "'";
		boolean result = getDbService().deleteSQL(sql);
		return result;
	}

	public boolean updateUser(User user)
	{
		if (null == user)
		{
			return false;
		}

		String sql = "update t_np_user set UserName = " + user.getUserName() + ", Gender = " + user.getGender()
		        + ", Age = " + user.getAge() + " where UserId = '" + user.getUserId() + "'";
		boolean result = getDbService().updateSQL(sql);
		return result;
	}
}

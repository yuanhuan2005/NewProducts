package com.tcl.dispatcher.common.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.tcl.dispatcher.common.domain.JdbcConf;

public class DbService
{
	final static private Log DEBUGGER = LogFactory.getLog(DbService.class);

	private Connection conn = null;
	private PreparedStatement statement = null;

	public DbService()
	{
		connSQL();
	}

	private JdbcConf getJdbcConf()
	{
		JdbcConf jdbcConf = new JdbcConf();
		String jdbcPropertiesFile = "jdbc.properties";
		jdbcConf.setJdbcDriver(CommonService.getBaseConfValue(jdbcPropertiesFile, "jdbc.driver"));
		jdbcConf.setJdbcUrl(CommonService.getBaseConfValue(jdbcPropertiesFile, "jdbc.url"));
		jdbcConf.setJdbcUsername(CommonService.getBaseConfValue(jdbcPropertiesFile, "jdbc.username"));
		jdbcConf.setJdbcPassword(CommonService.getBaseConfValue(jdbcPropertiesFile, "jdbc.password"));
		return jdbcConf;
	}

	// connect to MySQL
	void connSQL()
	{
		JdbcConf jdbcConf = getJdbcConf();
		String url = jdbcConf.getJdbcUrl() + "?characterEncoding=UTF-8";
		String username = jdbcConf.getJdbcUsername();
		String password = jdbcConf.getJdbcPassword();
		try
		{
			Class.forName(jdbcConf.getJdbcDriver());
			conn = DriverManager.getConnection(url, username, password);
		}
		catch (Exception e)
		{
			DbService.DEBUGGER.error("Exception: " + e.toString());
		}
	}

	// disconnect to MySQL
	void deconnSQL()
	{
		try
		{
			if (conn != null)
			{
				conn.close();
			}
		}
		catch (Exception e)
		{
			DbService.DEBUGGER.error("Exception: " + e.toString());
		}
	}

	// execute selection language
	public ResultSet selectSQL(String sql)
	{
		ResultSet rs = null;
		try
		{
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery(sql);
		}
		catch (Exception e)
		{
			DbService.DEBUGGER.error("Exception: " + e.toString());
		}
		return rs;
	}

	// execute insertion language
	public boolean insertSQL(String sql)
	{
		try
		{
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			return true;
		}
		catch (Exception e)
		{
			DbService.DEBUGGER.error("Exception: " + e.toString());
		}
		return false;
	}

	// execute delete language
	public boolean deleteSQL(String sql)
	{
		try
		{
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			return true;
		}
		catch (Exception e)
		{
			DbService.DEBUGGER.error("Exception: " + e.toString());
		}
		return false;
	}

	// execute update language
	public boolean updateSQL(String sql)
	{
		try
		{
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			return true;
		}
		catch (Exception e)
		{
			DbService.DEBUGGER.error("Exception: " + e.toString());
		}
		return false;
	}

}

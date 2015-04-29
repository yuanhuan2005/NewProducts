package com.tcl.dispatcher.product.domain;

public class User
{
	private String userId;

	private String userName;

	private String gender;

	private int age;

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String gender)
	{
		this.gender = gender;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	@Override
	public String toString()
	{
		return "User [userId=" + userId + ", userName=" + userName + ", gender=" + gender + ", age=" + age + "]";
	}

	public String toJsonString()
	{
		return "{\"userId\":\"" + userId + "\", \"userName\":\"" + userName + "\", \"gender\":\"" + gender
		        + "\", \"age\":" + age + "}";
	}

}

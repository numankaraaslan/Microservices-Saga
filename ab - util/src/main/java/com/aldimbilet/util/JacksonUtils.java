package com.aldimbilet.util;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonUtils
{
	public static <T> T readValueAsJson(byte[] bytes, Class<T> type)
	{
		try
		{
			return new ObjectMapper().readValue(bytes, type);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static String writeValueToJson(Object value)
	{
		try
		{
			return new ObjectMapper().writeValueAsString(value);
		}
		catch (Exception e)
		{
			return "";
		}
	}
}

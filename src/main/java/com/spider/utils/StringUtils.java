package com.spider.utils;

import java.util.UUID;

public class StringUtils {
	public static String uuid() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
}

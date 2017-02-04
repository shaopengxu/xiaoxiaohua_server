package com.u.bops.util;

public class ParseUtils {

	public static int parseInt(String s) {
		try {
			int i = Integer.parseInt(s);
			return i;
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}

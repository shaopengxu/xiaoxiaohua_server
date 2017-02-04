package com.u.bops.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

	public static String getExceptionStack(Throwable t) {
		StringWriter sw = new StringWriter();
		t.printStackTrace(new PrintWriter(sw));

		return sw.toString();
	}

}

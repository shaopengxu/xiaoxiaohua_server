package com.u.bops.websockets;

import org.json.JSONObject;

public class Message {

	public static int NOT_LOGIN = -1;
	public static int MISSING_PARAM = -2;
	public static int INVALID_PARAM = -3;
	public static int NOT_ENOUGH_BALANCE = -4;
	public static int IP_LOGIN_FORBID = -5;
	public static int PHONE_NUMBER_NOT_VALID = -6;
	public static int NOT_SUCH_LOGIN_TYPE = -7;
	public static int VALID_CODE_ERROR = -8;
	public static int NO_SUCH_HANDLER = -9;
	public static int API_NOT_EXIST = -10;
	public static int GAME_FINISHED = -105;
	public static int TIME_SIGN_ERROR = -100;
	public static int OTHER_ERROR = -999;
	public static int EXCEPTION_ERROR = -1000;
	public static int NO_REPLY = -1001;
	public static int AUTH_ERROR = -1002;
	public static int DEAD_ERROR = -9999;

	public static int OK_CODE = 1;

	public static String returnResult(int errorCode, String msg) {
		JSONObject json = new JSONObject();
		json.put("error_code", errorCode);
		json.put("msg", msg);
		return json.toString();
	}

	public static String returnResult(int errorCode, String msg, String api) {
		JSONObject json = new JSONObject();
		json.put("error_code", errorCode);
		json.put("msg", msg);
		json.put("api", api);
		return json.toString();
	}

	public static JSONObject returnResultJson(int errorCode, String msg) {
		JSONObject json = new JSONObject();
		json.put("error_code", errorCode);
		json.put("msg", msg);
		return json;
	}

	public static <T> String returnSuccess(T data) {
		JSONObject json = new JSONObject();
		json.put("error_code", OK_CODE);
		json.put("data", data);
		return json.toString();
	}
}

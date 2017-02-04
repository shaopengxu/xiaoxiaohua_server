package com.u.bops.util;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.u.bops.biz.vo.Result;
import com.u.bops.common.constants.ResultCode;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

	private static CloseableHttpClient httpclient;
	public final static String IPHONE5_USER_AGENT = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X;"
			+ " en-us) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53";

	static {
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout((int) TimeUnit.SECONDS.toMillis(120L))
				.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5L)).build();
		httpclient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig).build();

	}

	public static Result<?> download(String url, File saveTo)
			throws IOException {
		if (StringUtils.isEmpty(url)) {
			throw new IllegalArgumentException("url required!");
		}
		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		BufferedOutputStream outputStream = null;
		try {
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != 200) {
				return Result.error(ResultCode.SYSTEM_EXCEPTION,
						"request error, code:" + statusCode);
			}

			HttpEntity entity = response.getEntity();

			outputStream = new BufferedOutputStream(
					new FileOutputStream(saveTo));

			entity.writeTo(outputStream);

			EntityUtils.consume(entity);

			return Result.success(null);
		} finally {
			response.close();
			IOUtils.closeQuietly(outputStream);
		}
	}

	public static String getOrFail(String url, String encoding)
			throws IOException, URISyntaxException {
		Pair<Integer, String> p = get(url, null, encoding);
		if (p.getL() != 200) {
			throw new IOException("response code not 200:" + p.getL()
					+ ", body:" + p.getR());
		}
		return p.getR();
	}

	public static String getOrFail(String url, Map<String, String> params,
			String encoding) throws IOException, URISyntaxException {
		Pair<Integer, String> p = get(url, params, encoding);
		if (p.getL() != 200) {
			throw new IOException("response code not 200:" + p.getL()
					+ ", body:" + p.getR());
		}
		return p.getR();
	}

	public static Pair<Integer, String> get(String url,
			Map<String, String> params, String encoding,
			Map<String, String> headers) throws IOException, URISyntaxException {
		if (StringUtils.isEmpty(url)) {
			throw new IllegalArgumentException("url required!");
		}
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> e : params.entrySet()) {
				nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));
			}
		}
		URIBuilder builder = new URIBuilder(url);
		builder.setParameters(nvps);
		// httpPost.addHeader("Content-type", "text/json; charset=" + encoding);
		HttpGet httpGet = new HttpGet(builder.build());
		if (headers != null) {
			for (String name : headers.keySet()) {
				httpGet.setHeader(name, headers.get(name));
			}
		}
		CloseableHttpResponse response = httpclient.execute(httpGet);
		try {
			int statusCode = response.getStatusLine().getStatusCode();

			HttpEntity entity = response.getEntity();

			Header cookieHeader = response.getFirstHeader("Set-Cookie");
			if (headers != null && cookieHeader != null) {
				headers.put(cookieHeader.getName(), cookieHeader.getValue());
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			entity.writeTo(baos);

			String responseBody = new String(baos.toByteArray(), encoding);

			EntityUtils.consume(entity);

			return new Pair<Integer, String>(statusCode, responseBody);
		} finally {
			response.close();
		}

	}

	public static void main(String []args){
		Map<String,String > params = new HashMap<>();
		Map<String,String> headers = new HashMap<>();
		try {
			get("http://www.baidu.com", params, "utf-8", headers);
			System.out.println(headers);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static Pair<Integer, String> get(String url,
			Map<String, String> params, String encoding) throws IOException,
			URISyntaxException {
		return get(url, params, encoding, null);

	}

	public static Pair<Integer, String> post(String url,
			Map<String, String> params, String encoding) throws IOException {
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> e : params.entrySet()) {
				nvps.add(new BasicNameValuePair(e.getKey(), e.getValue()));
			}
		}
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, encoding));
		// httpPost.addHeader("Content-type", "text/json; charset=" + encoding);
		CloseableHttpResponse response = httpclient.execute(httpPost);
		try {
			int statusCode = response.getStatusLine().getStatusCode();

			HttpEntity entity = response.getEntity();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			entity.writeTo(baos);

			String responseBody = new String(baos.toByteArray(), encoding);

			EntityUtils.consume(entity);

			return new Pair<Integer, String>(statusCode, responseBody);
		} finally {
			response.close();
		}

	}

	public static String postOrFail(String url, String postBody, String encoding)
			throws IOException {
		HttpPost httpPost = new HttpPost(url);
		httpPost.setHeader("User-Agent", "Mozilla/5.0");
		httpPost.setEntity(new StringEntity(postBody, encoding));
		CloseableHttpResponse response = httpclient.execute(httpPost);
		try {
			int statusCode = response.getStatusLine().getStatusCode();

			HttpEntity entity = response.getEntity();

			ByteArrayOutputStream baos = new ByteArrayOutputStream();

			entity.writeTo(baos);

			String responseBody = new String(baos.toByteArray(), encoding);

			EntityUtils.consume(entity);

			if (statusCode != 200) {
				throw new IOException("response code not 200:" + statusCode
						+ ", body:" + responseBody);
			}
			return responseBody;
		} finally {
			response.close();
		}
	}

}

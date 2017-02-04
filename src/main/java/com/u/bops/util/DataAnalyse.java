package com.u.bops.util;

import java.util.Arrays;

public class DataAnalyse {

	public static String[] sourceErrorRecordAnalyse(Integer[] number,
			Integer[] total) {
		if (number.length == 0 || total.length == 0) {
			String[] result = new String[2];
			result[0] = "0/0(0%)";
			result[1] = "0%";
			return result;
		}
		if (number.length > total.length) {
			number = Arrays.copyOfRange(number, 0, total.length);
		}
		double[] rate = new double[number.length];
		double totalAve = 0;
		int realSize = 0;
		double rateAve = 0.0;
		for (int i = 0; i < number.length; i++) {
			if (number[i] > total[i]) {
				total[i] = number[i];
			}
			if (total[i] == 0) {
				total[i] = 1;
			}
			rate[i] = number[i] * 1.0 / total[i];
			rateAve += rate[i];
			totalAve += total[i];
			realSize++;
		}
		realSize = realSize == 0 ? 1 : realSize;
		totalAve = totalAve / realSize;
		totalAve = totalAve * 0.7;
		rateAve = rateAve / realSize;
		int maxIndex = 0;
		double maxTemp = 0.0;
		for (int i = 0; i < number.length; i++) {
			if (maxTemp < rate[i] && total[i] >= totalAve) {
				maxTemp = rate[i];
				maxIndex = i;
			}
		}
		String[] result = new String[2];
		result[0] = number[maxIndex] + "/" + total[maxIndex] + "("
				+ String.format("%.2f", maxTemp * 100.0) + "%)";
		result[1] = String.format("%.2f", rateAve * 100.0) + "%";

		return result;
	}
}

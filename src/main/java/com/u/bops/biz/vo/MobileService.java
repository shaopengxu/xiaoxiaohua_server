package com.u.bops.biz.vo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shpng on 2015/9/3.
 */
public class MobileService {

    public static Map<String, String> mobileServices = new HashMap<>();

    static {
        mobileServices.put("yidong", "移动");
        mobileServices.put("liantong", "联通");
        mobileServices.put("dianxin", "电信");
    }
}

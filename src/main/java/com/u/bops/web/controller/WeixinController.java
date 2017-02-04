package com.u.bops.web.controller;

import com.u.bops.biz.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by shpng on 2017/2/4.
 */

@RequestMapping("/weixin")
@Controller
public class WeixinController {

    @RequestMapping(value = "/check_user_info", produces = "application/json")
    public @ResponseBody
    Result<?> checkUserInfo(String nickName) {
        Map<String, Object> result = new HashMap<>();
        result.put("openId", nickName);
        result.put("isFirst", true);
        return Result.success(result);
    }
}

package com.u.bops.web.controller;

import com.u.bops.biz.domain.WeixinUser;
import com.u.bops.biz.vo.Result;
import com.u.bops.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shpng on 2017/2/4.
 */

@RequestMapping("/weixin")
@Controller
public class WeixinController {

    private Map<String, WeixinUser> userMap = new HashMap<>();
    private Map<String, List<WeixinUser>> friendMap = new HashMap<>();

    @RequestMapping(value = "/check_user_info", produces = "application/json")
    public @ResponseBody
    Result<?> checkUserInfo(String nickName) {
        Map<String, Object> result = new HashMap<>();
        result.put("openId", nickName);
        result.put("isFirst", !userMap.containsKey(nickName));
        return Result.success(result);
    }

    @RequestMapping(value = "/register", produces = "application/json")
    public @ResponseBody
    Result<?> register(String nickName, String password, String openId) {

        if(!userMap.containsKey(openId)){
            WeixinUser user = new WeixinUser();
            user.setNickName(nickName);
            user.setPassword(password);
            user.setOpenId(openId);
            userMap.put(openId, user);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success(result);
    }

    @RequestMapping(value = "/login", produces = "application/json")
    public @ResponseBody
    Result<?> login(String nickName, String password, String openId) {

        WeixinUser user = userMap.get(openId);
        boolean success = false;
        if(password!= null && password.equals(user.getPassword())){
            success = true;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return Result.success(result);
    }

    @RequestMapping(value = "/get_friends", produces = "application/json")
    public @ResponseBody
    Result<?> getFriends(String openId) {
        Map<String, Object> result = new HashMap<>();
        System.out.println("get friends " + openId);
        System.out.println("get friends " + friendMap.get(openId));
        result.put("friends", friendMap.get(openId));
        return Result.success(result);
    }

    @RequestMapping(value= "/add_friend", produces = "application/json")
    public @ResponseBody
    Result<?> addFriends(String openId, String friendOpenId) {
        System.out.println("add friend openId " + openId + ", friendOpenId " + friendOpenId);
        List<WeixinUser> list = friendMap.get(openId);
        if (list == null) {
            list = new ArrayList<>();
            friendMap.put(openId, list);
        }
        WeixinUser friend = userMap.get(friendOpenId);
        if (friend != null) {
            if (!list.contains(friend)) {
                list.add(friend);
            }
        }
        list = friendMap.get(friendOpenId);
        if (list == null) {
            list = new ArrayList<>();
            friendMap.put(friendOpenId, list);
        }
        WeixinUser me = userMap.get(openId);
        if (me != null) {
            if (!list.contains(me)) {
                list.add(me);
            }
        }
        System.out.println(friendMap.get(openId));
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success(result);
    }
}

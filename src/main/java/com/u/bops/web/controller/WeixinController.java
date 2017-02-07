package com.u.bops.web.controller;

import com.u.bops.biz.vo.WeixinUserInfo;
import com.google.gson.Gson;
import com.u.bops.biz.domain.FriendShip;
import com.u.bops.biz.domain.WeixinUser;
import com.u.bops.biz.service.ChatMessageService;
import com.u.bops.biz.service.FriendShipService;
import com.u.bops.biz.service.WeixinUserService;
import com.u.bops.biz.vo.Result;
import com.u.bops.websockets.Message;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.u.bops.util.AES;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.lang.management.OperatingSystemMXBean;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.microsoft.schemas.office.x2006.encryption.STCipherAlgorithm.AES;

/**
 * Created by shpng on 2017/2/4.
 */

@RequestMapping("/weixin")
@Controller
public class WeixinController {

    private static final Logger logger = LoggerFactory.getLogger(WeixinController.class);

    @Autowired
    private WeixinUserService weixinUserService;

    @Autowired
    private FriendShipService friendShipService;

    @Autowired
    private ChatMessageService chatMessageService;

    @RequestMapping(value = "/check_user_info", produces = "application/json")
    public
    @ResponseBody
    Result<?> checkUserInfo(@RequestParam(required = true, value = "encryptedData") String encryptedData,
                            @RequestParam(required = true, value = "iv") String iv,
                            @RequestParam(required = true, value = "sessionKey") String sessionKey, HttpSession httpSession) {

        Map<String, Object> result = new HashMap<>();
        WeixinUserInfo weixinUserInfo = getUserInfoFromEncryptedData(sessionKey, iv, encryptedData);
        if (weixinUserInfo != null) {
            result.put("openId", weixinUserInfo.getOpenId());
            result.put("isFirst", weixinUserService.getWeixinUser(weixinUserInfo.getOpenId()) == null);
            httpSession.setAttribute("userInfo", weixinUserInfo);
            return Result.success(result);
        }
        return Result.error(Message.EXCEPTION_ERROR, "格式不正确");
    }

    private WeixinUserInfo getUserInfoFromEncryptedData(String sessionKey, String iv, String encryptedData) {
        try {
            Map<String, Object> result = new HashMap<>();

            AES aes = new AES();
            byte[] resultByte = aes.decrypt(Base64.decodeBase64(encryptedData), Base64.decodeBase64(sessionKey), Base64.decodeBase64(iv));
            if (null != resultByte && resultByte.length > 0) {
                String userInfo = new String(resultByte, "UTF-8");
                Gson gson = new Gson();
                WeixinUserInfo weixinUser = gson.fromJson(userInfo, WeixinUserInfo.class);
                return weixinUser;
            }
        } catch (InvalidAlgorithmParameterException e) {
            logger.error("", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("", e);
        }
        return null;
    }

    @RequestMapping(value = "/register", produces = "application/json")
    public
    @ResponseBody
    Result<?> register(String password, HttpSession session) {

        WeixinUserInfo weixinUserInfo = (WeixinUserInfo) session.getAttribute("userInfo");
        if (weixinUserInfo == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        WeixinUser weixinUser = weixinUserInfo.toWeixinUser();
        weixinUser.setPassword(password);
        //检查是否存在该用户
        weixinUserService.createWeixinUser(weixinUser);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success(result);
    }

    @RequestMapping(value = "/login", produces = "application/json")
    public
    @ResponseBody
    Result<?> login(String password, HttpSession session) {
        WeixinUserInfo weixinUserInfo = (WeixinUserInfo) session.getAttribute("userInfo");
        if (weixinUserInfo == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        session.removeAttribute("userInfo");
        WeixinUser weixinUser = weixinUserService.getWeixinUser(weixinUserInfo.getOpenId());
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "用户不存在");
        }
        boolean success = false;
        if (StringUtils.equals(password, weixinUser.getPassword())) {

            session.setAttribute("loginUser", weixinUser);
            success = true;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return Result.success(result);
    }

    @RequestMapping(value = "/get_friends", produces = "application/json")
    public
    @ResponseBody
    Result<?> getFriends(HttpSession session) {
        WeixinUser weixinUser = (WeixinUser) session.getAttribute("loginUser");
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        List<FriendShip> friendShips = friendShipService.getFriends(weixinUser.getOpenId());
        return Result.success(friendShips);
    }

    @RequestMapping(value = "/add_friend", produces = "application/json")
    public
    @ResponseBody
    Result<?> addFriend(String friendOpenId, HttpSession session) {

        WeixinUser weixinUser = (WeixinUser) session.getAttribute("loginUser");
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        WeixinUser friendUserInfo = weixinUserService.getWeixinUser(friendOpenId);
        if (friendUserInfo == null) {
            if (friendUserInfo == null) {
                return Result.error(Message.INVALID, "该好友不存在");
            }
        }
        friendShipService.addFriend(weixinUser.getOpenId(), friendOpenId, friendUserInfo.getNickName(), friendUserInfo.getAvatarUrl());
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success(result);
    }

    @RequestMapping(value = "/remove_friend", produces = "application/json")
    public
    @ResponseBody
    Result<?> removeFriend(String friendOpenId, HttpSession session) {

        WeixinUser weixinUser = (WeixinUser) session.getAttribute("loginUser");
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        WeixinUser friendUserInfo = weixinUserService.getWeixinUser(friendOpenId);
        if (friendUserInfo == null) {
            if (friendUserInfo == null) {
                return Result.error(Message.INVALID, "该好友不存在");
            }
        }
        if (friendShipService.existsFriendShip(weixinUser.getOpenId(), friendOpenId)) {
            friendShipService.removeFriendShip(weixinUser.getOpenId(), friendOpenId);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success(result);

    }

    /**
     *
     * @param friendOpenId
     * @param nickName
     * @param image
     * @param session
     * @return
     */
    @RequestMapping(value = "/update_friend_message", produces = "application/json")
    public
    @ResponseBody
    Result<?> updateFriendMessage(String friendOpenId, String nickName, String image, HttpSession session) {

        WeixinUser weixinUser = (WeixinUser) session.getAttribute("loginUser");
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        WeixinUser friendUserInfo = weixinUserService.getWeixinUser(friendOpenId);
        if (friendUserInfo == null) {
            if (friendUserInfo == null) {
                return Result.error(Message.INVALID, "该好友不存在");
            }
        }
        friendShipService.updateFriendMessage(weixinUser.getOpenId(), friendOpenId, nickName, image);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success(result);
    }

    /**
     * 请求未读聊天推送
     * @param session
     * @return
     */
    @RequestMapping(value = "/ask_for_msg_push", produces = "application/json")
    public
    @ResponseBody
    Result<?> askForMessagePsuh(HttpSession session) {

        WeixinUser weixinUser = (WeixinUser) session.getAttribute("loginUser");
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        chatMessageService.askForMsgPush(weixinUser.getOpenId());
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success(result);
    }

    /**
     * 删除聊天记录
     * @param friendOpenId
     * @param session
     * @return
     */
    @RequestMapping(value = "/delete_chat_message", produces = "application/json")
    public
    @ResponseBody
    Result<?> deleteChatMessage(String friendOpenId, HttpSession session) {

        WeixinUser weixinUser = (WeixinUser) session.getAttribute("loginUser");
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        WeixinUser friendUserInfo = weixinUserService.getWeixinUser(friendOpenId);
        if (friendUserInfo == null) {
            if (friendUserInfo == null) {
                return Result.error(Message.INVALID, "该好友不存在");
            }
        }
        chatMessageService.deleteChatMessages(weixinUser.getOpenId(), friendOpenId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success(result);
    }
}

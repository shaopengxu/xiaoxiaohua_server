package com.u.bops.web.controller;

import com.u.bops.biz.vo.WeixinUserInfo;
import com.google.gson.Gson;
import com.u.bops.biz.domain.FriendShip;
import com.u.bops.biz.domain.WeixinUser;
import com.u.bops.biz.service.ChatMessageService;
import com.u.bops.biz.service.FriendShipService;
import com.u.bops.biz.service.WeixinUserService;
import com.u.bops.biz.vo.Result;
import com.u.bops.util.HttpUtils;
import com.u.bops.util.Pair;
import com.u.bops.websockets.Message;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.u.bops.util.AES;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.management.OperatingSystemMXBean;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.microsoft.schemas.office.x2006.encryption.STCipherAlgorithm.AES;

/**
 * Created by shpng on 2017/2/4.
 */

@RequestMapping("/weixin")
@Controller
public class WeixinController {

    private static final Logger logger = LoggerFactory.getLogger(WeixinController.class);

    /**
     * TODO session 过期
     */
    private Map<String, Map<String, Object>> sessionMap = new ConcurrentHashMap<>();

    @Autowired
    private WeixinUserService weixinUserService;

    @Autowired
    private FriendShipService friendShipService;

    @Autowired
    private ChatMessageService chatMessageService;

    private String getSessionKeyUrl = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code={code}&grant_type=authorization_code";

    private Object getSessionAttribute(String sessionId, String attribute) {
        Map<String, Object> session = sessionMap.get(sessionId);
        if (session == null) {
            return null;
        }
        return session.get(attribute);
    }

    private void putSessionAttribute(String sessionId, String attribute, Object value) {
        Map<String, Object> session = sessionMap.get(sessionId);
        if (session == null) {
            session = new HashedMap();
            sessionMap.put(sessionId, session);
        }
        session.put(attribute, value);
    }

    private void removeSessionAttribute(String sessionId, String attribute) {
        Map<String, Object> session = sessionMap.get(sessionId);
        if (session != null) {
            session.remove(attribute);
        }
    }

    @RequestMapping(value = "/check_user_info1", produces = "application/json")
    public
    @ResponseBody
    Result<?> checkUserInfo(@RequestParam(required = true, value = "encryptedData") String encryptedData,
                            @RequestParam(required = true, value = "iv") String iv,
                            @RequestParam(required = true, value = "code") String code) {

        //TODO 通过code获取sessionKey
        String url = getSessionKeyUrl.replace("{code}", code);
        try {
            Pair<Integer, String> result = HttpUtils.get(url, new HashMap<String, String>(), "UTF-8", new HashMap<String, String>());
        } catch (IOException e) {
            logger.error("", e);
        } catch (URISyntaxException e) {
            logger.error("", e);
        }
        String sessionKey = "";
        Map<String, Object> result = new HashMap<>();
        WeixinUserInfo weixinUserInfo = getUserInfoFromEncryptedData(sessionKey, iv, encryptedData);
        if (weixinUserInfo != null) {
            result.put("openId", weixinUserInfo.getOpenId());
            result.put("isFirst", weixinUserService.getWeixinUser(weixinUserInfo.getOpenId()) == null);

            String sessionId = String.valueOf(System.nanoTime());
            result.put("sessionId", sessionId);
            putSessionAttribute(sessionId, "userInfo", weixinUserInfo);
            return Result.success(result);
        }
        return Result.error(Message.EXCEPTION_ERROR, "格式不正确");
    }

    @RequestMapping(value = "/check_user_info", produces = "application/json")
    public
    @ResponseBody
    Result<?> checkUserInfoOfTest(@RequestParam(required = true, value = "encryptedData") String encryptedData,
                                  @RequestParam(required = true, value = "iv") String iv,
                                  @RequestParam(required = true, value = "code") String code,
                                  @RequestParam(required = true, value = "nickName") String nickName,
                                  @RequestParam("avatarUrl") String avatarUrl) {


        WeixinUserInfo weixinUserInfo = new WeixinUserInfo();
        weixinUserInfo.setNickName(nickName);
        weixinUserInfo.setOpenId(nickName);
        weixinUserInfo.setAvatarUrl(avatarUrl);

        Map<String, Object> result = new HashMap<>();
        if (weixinUserInfo != null) {
            result.put("openId", weixinUserInfo.getOpenId());
            result.put("isFirst", weixinUserService.getWeixinUser(weixinUserInfo.getOpenId()) == null);

            String sessionId = String.valueOf(System.nanoTime());
            result.put("sessionId", sessionId);
            putSessionAttribute(sessionId, "userInfo", weixinUserInfo);

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
    Result<?> register(String password, String sessionId) {

        WeixinUserInfo weixinUserInfo = (WeixinUserInfo) getSessionAttribute(sessionId, "userInfo");
        if (weixinUserInfo == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        WeixinUser weixinUser = weixinUserInfo.toWeixinUser();
        weixinUser.setPassword(password);
        //检查是否存在该用户
        weixinUserService.createWeixinUser(weixinUser);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        putSessionAttribute(sessionId, "loginUser", weixinUser);
        return Result.success(result);
    }

    @RequestMapping(value = "/login", produces = "application/json")
    public
    @ResponseBody
    Result<?> login(String password, String sessionId) {
        WeixinUserInfo weixinUserInfo = (WeixinUserInfo) getSessionAttribute(sessionId, "userInfo");
        if (weixinUserInfo == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        removeSessionAttribute(sessionId, "userInfo");
        WeixinUser weixinUser = weixinUserService.getWeixinUser(weixinUserInfo.getOpenId());
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "用户不存在");
        }
        boolean success = false;
        if (StringUtils.equals(password, weixinUser.getPassword())) {
            putSessionAttribute(sessionId, "loginUser", weixinUser);
            success = true;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        return Result.success(result);
    }

    @RequestMapping(value = "/get_friends", produces = "application/json")
    public
    @ResponseBody
    Result<?> getFriends(String sessionId) {
        WeixinUser weixinUser = (WeixinUser) getSessionAttribute(sessionId, "loginUser");
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        List<FriendShip> friendShips = friendShipService.getFriends(weixinUser.getOpenId());

        return Result.success(friendShips);
    }

    @RequestMapping(value = "/get_friend_size", produces = "application/json")
    public
    @ResponseBody
    Result<?> getFriendSize(String sessionId) {
        WeixinUser weixinUser = (WeixinUser) getSessionAttribute(sessionId, "loginUser");
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        int friendSize = friendShipService.getFriendSize(weixinUser.getOpenId());

        return Result.success(friendSize);
    }

    @RequestMapping(value = "/add_friend", produces = "application/json")
    public
    @ResponseBody
    Result<?> addFriend(String friendOpenId, String sessionId) {

        WeixinUser weixinUser = (WeixinUser) getSessionAttribute(sessionId, "loginUser");
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        // 先判断是不是加自己为好友
        if (StringUtils.equals(friendOpenId, weixinUser.getOpenId())) {
            return Result.error(Message.INVALID, "不能添加自己为好友");
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
    Result<?> removeFriend(String friendOpenId, String sessionId) {

        WeixinUser weixinUser = (WeixinUser) getSessionAttribute(sessionId, "loginUser");
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
     * @param friendOpenId
     * @param nickName
     * @param image
     * @param sessionId
     * @return
     */
    @RequestMapping(value = "/update_friend_message", produces = "application/json")
    public
    @ResponseBody
    Result<?> updateFriendMessage(String friendOpenId, String nickName, String image, String sessionId) {

        WeixinUser weixinUser = (WeixinUser) getSessionAttribute(sessionId, "loginUser");
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
     *
     * @param sessionId
     * @return
     */
    @RequestMapping(value = "/ask_for_msg_push", produces = "application/json")
    public
    @ResponseBody
    Result<?> askForMessagePsuh(@Param("lastMessageId") String lastMessageId, @Param("friendOpenId")String friendOpenId, @Param("sessionId") String sessionId) {

        WeixinUser weixinUser = (WeixinUser) getSessionAttribute(sessionId, "loginUser");
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        chatMessageService.askForMsgPush(weixinUser.getOpenId(), friendOpenId, lastMessageId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success(result);
    }

    /**
     * 删除聊天记录
     *
     * @param friendOpenId
     * @param sessionId
     * @return
     */
    @RequestMapping(value = "/delete_chat_message", produces = "application/json")
    public
    @ResponseBody
    Result<?> deleteChatMessage(String friendOpenId, String sessionId) {

        WeixinUser weixinUser = (WeixinUser) getSessionAttribute(sessionId, "loginUser");
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

    /**
     * 聊天记录已读
     *
     * @param friendOpenId
     * @param sessionId
     * @return
     */
    @RequestMapping(value = "/message_read", produces = "application/json")
    public
    @ResponseBody
    Result<?> messageRead(String friendOpenId, String sessionId) {

        WeixinUser weixinUser = (WeixinUser) getSessionAttribute(sessionId, "loginUser");
        if (weixinUser == null) {
            return Result.error(Message.INVALID, "获取不到用户信息");
        }
        WeixinUser friendUserInfo = weixinUserService.getWeixinUser(friendOpenId);
        if (friendUserInfo == null) {
            if (friendUserInfo == null) {
                return Result.error(Message.INVALID, "该好友不存在");
            }
        }
        chatMessageService.readMessage(weixinUser.getOpenId(), friendOpenId);
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        return Result.success(result);
    }
}

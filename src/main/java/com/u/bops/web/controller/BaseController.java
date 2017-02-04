package com.u.bops.web.controller;

import com.u.bops.biz.dal.mapper.UserMapper;
import com.u.bops.biz.domain.User;
import com.u.bops.biz.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by shpng on 2015/5/9.
 */
public class BaseController {

    @Autowired
    private UserService userService;

    public User getUser(){
        Long userId = (Long) SecurityUtils.getSubject().getSession().getAttribute("userId");
        User user = userService.getUser(userId);
        return user;
    }

    public boolean isUserAdmin(User user) {
        return userService.isUserAdmin(user);
    }

}

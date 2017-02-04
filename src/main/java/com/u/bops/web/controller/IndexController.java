package com.u.bops.web.controller;

import com.u.bops.biz.dal.mapper.UserMapper;
import com.u.bops.biz.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class IndexController {

    //@Autowired
    private UserMapper userMapper;

    public User getUser(){
        Long userId = (Long) SecurityUtils.getSubject().getSession().getAttribute("userId");
        User user = userMapper.getByUserId(userId);
        return user;
    }

    @RequestMapping("/index")
    public ModelAndView index() {
        User user = getUser();
        ModelAndView mav;
        if(user.getUserType() == 1) {
            mav = new ModelAndView("redirect:company/list");
        }else{
            mav = new ModelAndView("redirect:/cuser/project/list");
        }
        return mav;
    }

    @RequestMapping("/")
    public ModelAndView root() {
        ModelAndView mav = new ModelAndView("redirect:/index");
        return mav;
    }

    @RequestMapping(value = "/test", produces = "application/json")
    public @ResponseBody
    String getParams(String test) {
        return test;
    }
}

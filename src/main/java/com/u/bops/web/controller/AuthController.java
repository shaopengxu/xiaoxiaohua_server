package com.u.bops.web.controller;

import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String VIEW_LOGIN = "login";

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login(Map<String, String> params) {
        Subject currentUser = SecurityUtils.getSubject();
        if (currentUser.isAuthenticated()) {
            return new ModelAndView("redirect:/index");
        }
        ModelAndView mav = new ModelAndView(VIEW_LOGIN);
        if (params != null) {
            mav.getModel().putAll(params);
        }
        return mav;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ModelAndView doLogin(HttpServletRequest request,
                                HttpServletResponse response,
                                String username, String password) {

        ModelAndView mav = new ModelAndView("/login");
        return mav;
    }

    @RequestMapping("/unauthorized")
    public String unauthorized() {
        return "/login";
    }

    @RequestMapping("/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:login";
    }

}

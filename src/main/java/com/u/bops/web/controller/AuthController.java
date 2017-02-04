package com.u.bops.web.controller;

import com.u.bops.biz.dal.mapper.UserMapper;
import com.u.bops.biz.domain.User;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;


@Controller
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private static final String VIEW_LOGIN = "login";

    //@Autowired
    private UserMapper userMapper;

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

        Subject currentUser = SecurityUtils.getSubject();

        try {
            if (currentUser.isAuthenticated()) {
                if(!StringUtils.equals((String)currentUser.getPrincipal(), username)) {
                    logger.info("login with different name, " + currentUser.getPrincipal());
                    SecurityUtils.getSubject().logout();
                }
            }
            if (!currentUser.isAuthenticated()) {
                UsernamePasswordToken token = new UsernamePasswordToken(username, password);
                SecurityUtils.getSubject().login(token);
            }
            User user = userMapper.getByUserName(username);
            SecurityUtils.getSubject().getSession().setAttribute("userId", user.getId());
            String forwardUrl = null;
            if (user.getUserType() == 1) {
                forwardUrl = "redirect:/company/list";
            } else {
                forwardUrl = "redirect:/cuser/project/list";
            }
            ModelAndView mav = new ModelAndView(forwardUrl);
            //WebUtils.redirectToSavedRequest(request, response, forwardUrl);
            return mav;
        } catch (AuthenticationException e) {
            logger.warn("Error authenticating.", e);
            ModelAndView mav = new ModelAndView("/login");
            mav.addObject("errorMessage", "user/password not correct!");
            return mav;
        }
//        catch (IOException e) {
//            e.printStackTrace();
//            ModelAndView mav = new ModelAndView("/login");
//            return mav;
//        }
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

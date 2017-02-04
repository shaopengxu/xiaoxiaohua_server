package com.u.bops.web.controller;

import com.u.bops.biz.domain.User;
import com.u.bops.biz.vo.Result;
import com.u.bops.biz.service.UserService;
import com.u.bops.common.constants.ResultCode;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequestMapping("/user")
@Controller
public class UserController {

    @Autowired
    private UserService userService;


    private final String SEC = "ytbops";

    @RequestMapping("/create0")
    public
    @ResponseBody
    Result<Boolean> create0(String sec, String username, String password, String roles) {
        if (SEC.equals(sec)) {
            Result<Long> result = doCreate(username, password, roles, 1);
            if (result.isSuccess()) {
                return Result.success(true);
            }
        }
        return Result.error(ResultCode.INVALID_PARAM, "");
    }

    private Result<Long> doCreate(String username, String password, String roles, int userType) {
        List<String> roleList = new ArrayList<String>(3);
        if (StringUtils.isNotBlank(roles)) {
            roleList.addAll(Arrays.asList(StringUtils.split(roles, ",")));
        }
        Result<Long> bizResult = userService.createUser(username, password, roleList, userType, 0l);
        if (!bizResult.isSuccess()) {
            return Result.error(bizResult.getCode(), bizResult.getErrorMessage());
        }
        return bizResult;
    }

    @RequestMapping(value = "/register")
    public ModelAndView register(String username, String password) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
            ModelAndView mav = new ModelAndView("register");
            return mav;
        }
        Result<Long> result = doCreate(username, password, "1", 0);
        if (result.isSuccess()) {

            User user = userService.getUser(result.getData());
            userService.saveUser(user);
            if (SecurityUtils.getSubject().isAuthenticated()) {
                SecurityUtils.getSubject().logout();
            }
            UsernamePasswordToken token = new UsernamePasswordToken(username, password);
            SecurityUtils.getSubject().login(token);
            SecurityUtils.getSubject().getSession().setAttribute("userId", user.getId());
            ModelAndView mav = new ModelAndView("redirect:/index.htm");
            return mav;
        } else {
            ModelAndView mav = new ModelAndView("register");
            return mav;
        }
    }

    @RequestMapping(value = "/modify_password_page")
    public ModelAndView modifyPasswordPage() {
        return new ModelAndView("/modify_password_page");
    }

    @RequestMapping(value = "/modify_password")
    public ModelAndView modifyPassword(String oldPassword, String password) {
        String username = (String) SecurityUtils.getSubject().getPrincipal();

        User user = userService.getUser(username);
        boolean success = userService.updatePassword(user, password, oldPassword);
        if(success){
            return new ModelAndView("redirect:/index");
        }else{
            ModelAndView mav = new ModelAndView("/modify_password_page");
            mav.addObject("errorMsg", "密码错误");
            return mav;
        }

    }

}

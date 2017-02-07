package com.u.bops.web.controller;

import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class IndexController {

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

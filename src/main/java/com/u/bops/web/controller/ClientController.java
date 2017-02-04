package com.u.bops.web.controller;

import com.u.bops.biz.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;


@RequestMapping("/client")
@Controller
public class ClientController {



    @RequestMapping("/list")
    public ModelAndView list() {
        ModelAndView mav = new ModelAndView("client/list");
        return mav;
    }

    @RequestMapping(value = "/get_clients", produces = "application/json")
    public @ResponseBody
    Result<?> getClients() {
        Map<String, Object> params = new HashMap<>();
        params.put("offset", 0);
        params.put("rowCount", 100);
        return null;
        //return Result.success(clientMapper.queryByParam(params));
    }

}

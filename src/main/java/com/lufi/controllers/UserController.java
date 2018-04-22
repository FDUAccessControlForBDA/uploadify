package com.lufi.controllers;

import com.lufi.services.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "signup", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> signup(@RequestParam(value = "userName") String userName,
                                      @RequestParam(value = "password") String password,
                                      HttpServletRequest request, HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        Map<String, Object> rm = new HashMap<String, Object>();
        try{
            long ret = userService.signup(userName,password);
            if (ret > 0) {
                rm.put("result", "true");
                rm.put("message", Long.toString(ret));
            } else {
                rm.put("result", "false");
                rm.put("message", "duplication");
            }
        }catch (Exception e){
            e.printStackTrace();
            rm.put("flag", "false");
            rm.put("message", "server error");
        }

        return rm;
    }

    @RequestMapping(value = "signin", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> signin(@RequestParam(value = "userName") String userName,
                                      @RequestParam(value = "password") String password,
                                      HttpServletRequest request, HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin", "*");
        Map<String, Object> rm = new HashMap<String, Object>();
        try{
            long ret = userService.signin(userName,password);
            if (ret > 0) {
                rm.put("result", "true");
                rm.put("message", Long.toString(ret));
            } else {
                rm.put("result", "false");
                rm.put("message", "no such user");
            }
        }catch (Exception e){
            e.printStackTrace();
            rm.put("flag", "false");
            rm.put("message", "server error");
        }

        return rm;
    }
}

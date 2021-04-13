package com.cyg.consumerdemo.controller;

import com.cyg.consumerdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author cyg
 * @date 2021/4/12 17:09
 **/
@RestController
public class UserHystrixController {

    private final UserService userService;

    public UserHystrixController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/testCache/{id}")
    public String testCache(@PathVariable int id) {
        //只会调用一次，后两次会走缓存
        userService.getUserCache(id);
        userService.getUserCache(id);
        userService.getUserCache(id);
        return "测试缓存操作成功";
    }

    @GetMapping("/testRemoveCache/{id}")
    public String testRemoveCache(@PathVariable int id) {
        userService.getUserCache(id);
        userService.removeCache(id);
        userService.getUserCache(id);
        return "移除缓存操作成功";
    }
}

package com.cqupt.xuetu.controller;

import com.cqupt.xuetu.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserControllerTest {

    @Autowired
    UserService userService;

    @Test
    void query() throws Exception {
       // userService.login("'"+"张三"+"'", "12344", "user");


    }
}

package com.gyb.chat.controller;


import com.gyb.chat.dao.RecordDao;
import com.gyb.chat.dao.UserDao;
import com.gyb.chat.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class HelloController {


    @Autowired
    UserDao userDao;
    @Autowired
    RecordDao recordDao;

    @Autowired
    HelloService helloService;

    @GetMapping("/user/{userid}/init.json")
    public Object init(@PathVariable Long userid){
        return helloService.init(userid);
    }

    @GetMapping("/test/{userid}")
    public Object test(@PathVariable Long userid){
        return userDao.findByWhoFollwMe(userid);
    }


    @GetMapping("/record/{userid}")
    public Object getRecords(@PathVariable Long userid){
        return recordDao.findByFromUserId(userid);
    }

}

package com.gyb.chat.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gyb.chat.bean.Group;
import com.gyb.chat.bean.User;
import com.gyb.chat.dao.GroupDao;
import com.gyb.chat.dao.UserDao;
import com.gyb.chat.service.HelloService;
import com.itshidu.common.ftp.config.FtpPoolConfig;
import com.itshidu.common.ftp.core.FTPClientFactory;
import com.itshidu.common.ftp.core.FTPClientPool;
import com.itshidu.common.ftp.core.FtpClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class HelloServiceImpl implements HelloService {

    @Autowired
    UserDao userDao;
    @Autowired
    GroupDao groupDao;

    @Autowired
    FtpClientUtils ftpUtil;

    @Value("${ftp.host}")
    String ftpHost;

    @Override
    public Object init(long userid) {


        /**
         * {
         *   "code": 0 //0表示成功，其它表示失败
         *   ,"msg": "" //失败信息
         *   ,"data": {
         *
         *     //我的信息
         *     "mine": {
         *       "username": "纸飞机" //我的昵称
         *       ,"id": "100000" //我的ID
         *       ,"status": "online" //在线状态 online：在线、hide：隐身
         *       ,"sign": "在深邃的编码世界，做一枚轻盈的纸飞机" //我的签名
         *       ,"avatar": "a.jpg" //我的头像
         *     }
         *
         *     //好友列表
         *     ,"friend": [{
         *       "groupname": "前端码屌" //好友分组名
         *       ,"id": 1 //分组ID
         *       ,"list": [{ //分组下的好友列表
         *         "username": "贤心" //好友昵称
         *         ,"id": "100001" //好友ID
         *         ,"avatar": "a.jpg" //好友头像
         *         ,"sign": "这些都是测试数据，实际使用请严格按照该格式返回" //好友签名
         *         ,"status": "online" //若值为offline代表离线，online或者不填为在线
         *       }, …… ]
         *     }, …… ]
         *
         *     //群组列表
         *     ,"group": [{
         *       "groupname": "前端群" //群组名
         *       ,"id": "101" //群组ID
         *       ,"avatar": "a.jpg" //群组头像
         *     }, …… ]
         *   }
         * }
         */

        User user = userDao.findById(userid).get();

        if(user==null)return "error";

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode root = mapper.createObjectNode();
        root.put("code", 0);
        root.put("msg", "");
        ObjectNode data = mapper.createObjectNode();
        root.set("data", data);
        ObjectNode mine = mapper.createObjectNode();
        ArrayNode friend = mapper.createArrayNode();
        ArrayNode group = mapper.createArrayNode();
        data.set("mine", mine);
        data.set("friend", friend);
        data.set("group", group);



        //个人信息
        mine.put("username", user.getUsername());
        mine.put("id", user.getId());
        mine.put("avatar", user.getAvatar());
        mine.put("sign", user.getSign());

        //friend分组信息
        List<Group> groupList = groupDao.findGroupsByUser(userid);
        groupList.forEach(g->{
            ObjectNode gnode = mapper.createObjectNode();
            gnode.put("id", g.getId());
            gnode.put("groupname", g.getName());
            //friend分组中的好友
            ArrayNode listNode = mapper.createArrayNode();
            gnode.set("list", listNode);
            g.getList().forEach(u -> {
                System.out.println("----------------"+u.getUsername()+"-----------");
                ObjectNode unode = mapper.createObjectNode();
                unode.put("username", u.getUsername());
                unode.put("id", u.getId());
                unode.put("avatar", u.getAvatar());
                unode.put("sign", u.getSign());
                unode.put("status", u.getStatus());
                listNode.add(unode);
            });
            friend.add(gnode);
        });

        try {
            return mapper.writeValueAsString(root);
        }catch (Exception e){

        }


        return null;
    }

    @Override
    public Object createUser(String username, String password, String sign, MultipartFile avatar) {
        User user = userDao.findByUsername(username);
        if(user!=null){
            return "error";
        }
        user = new User();
        user.setStatus("offline");
        user.setCreated(new Date());
        user.setSign(sign);
        user.setUsername(username);
        user.setPassword(password);

        //文件上传到ftp服务器
        String name=avatar.getOriginalFilename();
        String houzhui=name.substring(name.lastIndexOf("."));
        String filename = UUID.randomUUID().toString().replaceAll("-", "")+houzhui;
        try {
            ftpUtil.store(avatar.getInputStream(), "/avatar/", filename);
            user.setAvatar("http://"+ftpHost+"/avatar/"+filename);
            userDao.save(user);
            Map<String, Object> data = new HashMap();
            data.put("code", 1);
            data.put("msg", "注册成功");
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "error";
    }


    public static void main(String[] args) {
        File file = new File("C:\\Users\\GYB\\Pictures\\壁纸\\2.jpg");
        try {
            InputStream in = new FileInputStream("C:\\Users\\GYB\\Pictures\\壁纸\\2.jpg");
            FtpPoolConfig cfg = new FtpPoolConfig();
            cfg.setHost("192.168.1.107");
            cfg.setPort(21);
            cfg.setUsername("user");
            cfg.setPassword("user");
            FTPClientFactory clientFactory = new FTPClientFactory(cfg);
            FTPClientPool ftpClientPool = new FTPClientPool(clientFactory);
            FtpClientUtils ftpClientUtils = new FtpClientUtils(ftpClientPool);
            ftpClientUtils.store(file, "/avatar/", "hello.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}

package com.zp.mongo.myspringbootmongo.controller;

import com.mongodb.client.FindIterable;
import com.zp.mongo.myspringbootmongo.service.MongoService;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author zp
 * @create 2020/6/4 10:46
 */
@RestController
public class MongoController {
    @Autowired
    private MongoService mongoService;

    @RequestMapping("/announce/insert")
    public String insertAnnounce() {
        String json = "{'title':'公告1','content':'公告内容1','createTime':'2020-6-4','projectId':'pj001'}";
        mongoService.insert("announce", json);
        return "OK";
    }

//    @RequestMapping("/announce/user/insert")
//    public String insertAnnounceUser(String userId) {
//        mongoService.insertAnnounceUser(userId);
//        return "OK";
//    }

    @RequestMapping("/announce/user/read")
    public String announceUserRead(String userId) {
        String announceId = "5ed89b69d7d4a723ac6cef2b";
        mongoService.update("announce_user",userId, announceId);
        return "OK";
    }

    @RequestMapping("/announce/list")
    public String announceList(String userId) {
        String projectId = "prj001";
        return mongoService.getAnnounceList(userId, projectId);
    }

    @RequestMapping("/all")
    public String findAll(String name) {
        return mongoService.findAllToJson(name);
    }

}

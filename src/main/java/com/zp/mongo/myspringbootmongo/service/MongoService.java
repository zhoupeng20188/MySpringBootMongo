package com.zp.mongo.myspringbootmongo.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


/**
 * @Author zp
 * @create 2020/6/4 10:40
 */
@Service
public class MongoService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(String collectionName, String json) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        Document document = Document.parse(json);
        collection.insertOne(document);
    }

    public void insert(String collectionName, Document document) {
//        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
//        collection.insertOne(document);
        mongoTemplate.insert(document, collectionName);
    }

    public FindIterable<Document> findByObjectId(ObjectId objectId, String collectionName){
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        BasicDBObject doc2 = new BasicDBObject();
        doc2.put("_id",objectId);
        FindIterable<Document> documents2 = collection.find(doc2);
        return documents2;
    }

    public void insertAnnounceUser(String userId, String projectId) {
        FindIterable<Document> announce = findAll("announce");
        for (Document document : announce) {
            String objectId = document.getObjectId("_id").toString();
            if(checkAnnounceUser(objectId, "announce_user")){

                Document announceUserDocument = new Document();
                announceUserDocument.put("userId", userId);
                announceUserDocument.put("isRead", false);
                announceUserDocument.put("ReadTime", null);
                announceUserDocument.put("projectId", projectId);
                announceUserDocument.put("announceId", objectId);
                insert("announce_user", announceUserDocument);
            }
        }
    }

    private boolean checkAnnounceUser(String announceId, String collectionName) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        BasicDBObject doc2 = new BasicDBObject();
        doc2.put("announceId",announceId);
        FindIterable<Document> documents2 = collection.find(doc2);

        return documents2.first()==null ? true : false;
    }

    public void update(String collectionName, String userId, String announceId) {
//        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
//        collection.updateOne(Filters.eq("userId", userId), new Document("$set", new Document("isRead", true)));
        Query query = new Query();
        Criteria criteria = Criteria.where("userId").is(userId).and("announceId").is(announceId);
        query.addCriteria(criteria);
        Update update = new Update();
        update.set("isRead",true);
        update.set("readTime","2020-6-4 15:34");
        mongoTemplate.updateFirst(query, update, collectionName);
    }

    public String findAllToJson(String collectionName) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        FindIterable<Document> documents = collection.find();
        JSONArray jsonArray = new JSONArray();

        for (Document document : documents) {
            ObjectId objectId = document.getObjectId("_id");
            System.out.println(objectId);
            System.out.println(document.toJson());
            jsonArray.add(JSONObject.parseObject(document.toJson()));
        }
        return jsonArray.toJSONString();
    }

    public FindIterable<Document> findAll(String collectionName) {
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        FindIterable<Document> documents = collection.find();
        return documents;
    }

    public void delete() {
        MongoCollection<Document> collection = mongoTemplate.getCollection("test");
    }

    public String getAnnounceList(String userId, String projectId) {
        insertAnnounceUser(userId, projectId);
        String collectionName = "announce_user";
        MongoCollection<Document> collection = mongoTemplate.getCollection(collectionName);
        String collectionName2 = "announce";
        MongoCollection<Document> collection2 = mongoTemplate.getCollection(collectionName2);
        BasicDBObject doc = new BasicDBObject();
        doc.put("userId",userId);
        doc.put("projectId",projectId);
        FindIterable<Document> documents = collection.find(doc);
        JSONArray jsonArray = new JSONArray();
        for (Document document : documents) {
            String announceId = document.getString("announceId");
            BasicDBObject doc2 = new BasicDBObject();
            doc2.put("_id",new ObjectId(announceId));
            FindIterable<Document> documents2 = collection2.find(doc2);
            JSONObject jsonObject = JSONObject.parseObject(document.toJson());
            jsonObject.put("announceData",JSONObject.parseObject(documents2.first().toJson()));
            jsonArray.add(jsonObject);
        }
        return jsonArray.toJSONString();

    }
}

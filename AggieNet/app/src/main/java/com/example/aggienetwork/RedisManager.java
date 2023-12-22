//package com.example.aggienetwork;
//
//
//import redis.clients.jedis.Connection;
//import redis.clients.jedis.Jedis;
//
//public class RedisManager {
//    private Jedis jedis;
//
//    public RedisManager(Jedis pjedis) {
//        // Initialize Jedis using connection string
//        jedis = pjedis;
//        Connection connection = jedis.getConnection();
//    }
//
//    public void setLike(String postID, String userID) {
//        jedis.sadd("likes:" + postID, userID);
//    }
//
//    public void removeLike(String postID, String userID) {
//        jedis.srem("likes:" + postID, userID);
//    }
//
//    public boolean checkLike(String postID, String userID) {
//        return jedis.sismember("likes:" + postID, userID);
//    }
//
//    public long getLikesCount(String postID) {
//        return jedis.scard("likes:" + postID);
//    }
//
//    // Make sure to close the connection
//    public void close() {
//        jedis.close();
//    }
//}
//

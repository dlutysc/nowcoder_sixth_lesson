package com.nowcoder.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.net.ServerSocket;
import java.util.Map;
@Service
public class JedisAdapter implements InitializingBean{
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool pool = null;

    public static  void print(int index, Object obj){
        System.out.println(String.format("%d, %s", index, obj.toString()));
    }

    public static void main(String[] argv){
        Jedis jedis = new Jedis();
        jedis.flushAll();

        jedis.set("hello", "world");
        print(1, jedis.get("hello"));
        jedis.rename("hello", "newhello");
        print(2, jedis.get("newhello"));
        jedis.setex("hello2", 10, "world");

        jedis.set("pv", "100");
        jedis.incr("pv");
        print(3, jedis.get("pv"));
        jedis.incrBy("pv", 5);
        jedis.incrBy("pv", 7);
        print(4, jedis.get("pv"));

        //列表操作
        String listName = "listA";
        for(int i = 0; i < 10; ++i){
            jedis.lpush(listName, "a" + String.valueOf(i));
        }
        print(5, jedis.lrange(listName, 0, 10));
        print(6, jedis.llen(listName));
        print(7, jedis.lpop(listName));
        print(8, jedis.llen(listName));
        print(9, jedis.lindex(listName, 3));
        print(10, jedis.linsert(listName, BinaryClient.LIST_POSITION.AFTER, "a4", "xx"));
        print(11, jedis.linsert(listName, BinaryClient.LIST_POSITION.BEFORE, "a4", "BB"));
        print(12, jedis.lrange(listName, 0, 12));

        String userKey = "user12";
        jedis.hset(userKey, "name", "jim");
        jedis.hset(userKey, "age", "12");
        jedis.hset(userKey, "phone", "187112338888");

        print(13, jedis.hget(userKey, "name"));
        print(14, jedis.hgetAll(userKey));
        jedis.hdel(userKey, "phone");
        print(14, jedis.hgetAll(userKey));
        print(16, jedis.hkeys(userKey));
        print(17, jedis.hvals(userKey));
        print(18, jedis.hexists(userKey, "email"));
        print(19, jedis.hexists(userKey, "age"));
        jedis.hsetnx(userKey, "school", "aa");
        jedis.hsetnx(userKey, "name", "ysc");
        print(20, jedis.hgetAll(userKey));

        //set
        String likeKeys1 = "newsLike1";
        String likeKeys2= "newsLike2";
        for(int i = 0; i < 10; ++i){
            jedis.sadd(likeKeys1, String.valueOf(i));
            jedis.sadd(likeKeys2, String.valueOf(i * 2));
        }
        print(21, jedis.smembers(likeKeys1));
        print(22, jedis.smembers(likeKeys2));
        print(24, jedis.sinter(likeKeys1, likeKeys2));
        print(25, jedis.sunion(likeKeys1, likeKeys2));
        print(26, jedis.sdiff(likeKeys1, likeKeys2));
        print(27, jedis.sismember(likeKeys1, "5"));
        jedis.srem(likeKeys1, "5");
        print(28, jedis.smembers(likeKeys1));
        print(31, jedis.scard(likeKeys1));
        jedis.smove(likeKeys2, likeKeys1, "14");
        print(29, jedis.scard(likeKeys1));
        print(30, jedis.smembers(likeKeys1));

        //
        String rankKey = "rankKey";
        jedis.zadd(rankKey, 15, "Jim");
        jedis.zadd(rankKey, 70, "Tom");
        jedis.zadd(rankKey, 60, "Kit");
        jedis.zadd(rankKey, 90, "Lucy");
        jedis.zadd(rankKey, 75, "Lily");
        print(31, jedis.zcard(rankKey));
        print(32, jedis.zcount(rankKey, 60, 100));
        print(33, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Lucy");
        print(33, jedis.zscore(rankKey, "Lucy"));
        jedis.zincrby(rankKey, 2, "Luck");
        print(33, jedis.zscore(rankKey, "Luck"));
        print(34, jedis.zcount(rankKey, 0, 100));
        print(35, jedis.zrange(rankKey, 1, 3));  //?
        print(36, jedis.zrevrange(rankKey, 1, 3));  //?
        for(Tuple tuple : jedis.zrangeByScoreWithScores(rankKey, "0", "100")){
            print(37, tuple.getElement() + ":" + String.valueOf(tuple.getScore()));

        }

        print(38, jedis.zrank(rankKey, "Lily"));
        print(39, jedis.zrevrank(rankKey, "Lily"));

        JedisPool pool = new JedisPool();
        for (int i = 0; i < 100; ++i){
            Jedis j = pool.getResource();
            j.get("a");
            System.out.println("POOL" + i);
            j.close();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("localhost", 6379);
    }

    private Jedis getJedis(){
        return pool.getResource();
    }

    public long sadd(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sadd(key, value);

        }catch (Exception e){
            logger.error("发生异常", e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long srem(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.srem(key, value);

        }catch (Exception e){
            logger.error("发生异常", e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public boolean sismember(String key, String value){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.sismember(key, value);

        }catch (Exception e){
            logger.error("发生异常", e.getMessage());
            return false;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

    public long scard(String key){
        Jedis jedis = null;
        try{
            jedis = pool.getResource();
            return jedis.scard(key);

        }catch (Exception e){
            logger.error("发生异常", e.getMessage());
            return 0;
        }finally {
            if (jedis != null){
                jedis.close();
            }
        }
    }

}



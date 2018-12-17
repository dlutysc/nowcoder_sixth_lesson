package com.nowcoder.util;

/**
 * Created by inch
 */
public class RedisKeyUtil {
    private static String SPLIT = ":";
    private static String BIZ_LIKE = "LIKE";
    private static String BIZ_DISLIKE = "DISLIKE";

    public static String getLikeKey(int entityId, int entityType){
        return BIZ_LIKE + SPLIT + String.valueOf(entityType) + SPLIT +  String.valueOf(entityId);
    }

    public static String getDislikeKey(int entityId, int entityType){
        return BIZ_DISLIKE + SPLIT + String.valueOf(entityType) + SPLIT + String.valueOf(entityId);
    }
}

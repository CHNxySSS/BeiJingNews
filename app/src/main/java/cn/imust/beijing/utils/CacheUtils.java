package cn.imust.beijing.utils;

import android.content.Context;

/**
 * 网络缓存工具类
 */
public class CacheUtils {
    //写缓存
    //以url为key（标识），以json为值，保存在本地
    public static void setCache(Context context, String url, String json){
        PrefUtils.putString(context,url,json);
    }
    //读缓存
    public static String getCache(Context context, String url){
        return PrefUtils.getString(context,url,null);
    }
}

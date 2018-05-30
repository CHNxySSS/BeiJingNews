package cn.imust.beijing.domain;

import java.util.ArrayList;

/**
 * 组图的网络数据
 */
public class PhotosBean {
    public PhotoData data;
    public class PhotoData{
        public ArrayList<PhotoNews> news;
    }
    public class PhotoNews{
        public String title;
        public String listimage;
    }
}

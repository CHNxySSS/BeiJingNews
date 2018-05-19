package cn.imust.beijing.domain;

import java.util.ArrayList;

/*
*分类数据的封装
* */
public class NewsMenu {
    public int retcode;
    public ArrayList<NewsMenuData> data;
    public ArrayList<String> extend;
    //四个分类菜单的信息：新闻，专题，组图，互动
    public class NewsMenuData {
        public String id;
        public String title;
        public int type;
        public ArrayList<NewsTabData> children;

        @Override
        public String toString() {
            return "NewsMenuData{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    //12个页签对象的封装
    public class NewsTabData{
        public String id;
        public String title;
        public int type;
        public String url;

        @Override
        public String toString() {
            return "NewsTabData{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NewsMenu{" +
                "data=" + data +
                '}';
    }
}

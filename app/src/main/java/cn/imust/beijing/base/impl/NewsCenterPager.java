package cn.imust.beijing.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

import cn.imust.beijing.MainActivity;
import cn.imust.beijing.base.BaseMenuDetailPager;
import cn.imust.beijing.base.BasePager;
import cn.imust.beijing.base.impl.menudetail.InteractMenuDetailPager;
import cn.imust.beijing.base.impl.menudetail.NewsMenuDetailPager;
import cn.imust.beijing.base.impl.menudetail.PhotosMenuDetailPager;
import cn.imust.beijing.base.impl.menudetail.TopicMenuDetailPager;
import cn.imust.beijing.domain.NewsMenu;
import cn.imust.beijing.fragment.LeftMenuFragment;
import cn.imust.beijing.utils.CacheUtils;

/**
 * 新闻中心
 * */
public class NewsCenterPager extends BasePager {
    private ArrayList<BaseMenuDetailPager> mPagers;
    private NewsMenu mNewsMenu;

    public NewsCenterPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        //给空的帧布局动态添加布局对象
//        TextView view = new TextView(mActivity);
//        view.setTextSize(22);
//        view.setTextColor(Color.RED);
//        view.setGravity(Gravity.CENTER);//居中显示
//        view.setText("新闻中心");
//        flContainer.addView(view);

        //修改标题
        tvTitle.setText("新闻");
        String cache = CacheUtils.getCache(mActivity,"http://10.0.2.2:8080/zhbj/categories.json");
        if(!TextUtils.isEmpty(cache)){
            //有缓存
            processData(cache);
        }else {
            getDataFromServer();
        }
        //继续请求服务器，，保证缓存最新
        getDataFromServer();

        Log.i("NewsCenterPager","SSS");
    }
    //从服务器获取数据
    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, "http://10.0.2.2:8080/zhbj/categories.json", new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                Log.i("NewsCenterPager",result);
                processData(result);
                //写缓存
                CacheUtils.setCache(mActivity,"http://10.0.2.2:8080/zhbj/categories.json",result);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
    //解析数据
    private void processData(String json) {
        Gson gson = new Gson();
        //通过json和对象类，生成一个对象
        mNewsMenu = gson.fromJson(json, NewsMenu.class);
        //找到侧边栏对象
        MainActivity mainUI = (MainActivity) mActivity;
        LeftMenuFragment fragment = mainUI.getLeftMenuFragment();
        fragment.setMenuData(mNewsMenu.data);
        //网络请求成功之后，初始化四个菜单详情页
        mPagers = new ArrayList<BaseMenuDetailPager>();
        mPagers.add(new NewsMenuDetailPager(mActivity,mNewsMenu.data.get(0).children));
        mPagers.add(new TopicMenuDetailPager(mActivity));
        mPagers.add(new PhotosMenuDetailPager(mActivity,btnDisplay));
        mPagers.add(new InteractMenuDetailPager(mActivity));
        //设置新闻菜单详情页为默认页面
        setMenuDetailPager(0);
    }
    //修改菜单详情页
    public void setMenuDetailPager(int position) {
        BaseMenuDetailPager pager = mPagers.get(position);
        //判断是否是组图，如果是，显示切换按钮，否则隐藏
        if(pager instanceof PhotosMenuDetailPager){
            btnDisplay.setVisibility(View.VISIBLE);
        }else {
            btnDisplay.setVisibility(View.GONE);
        }
        //清除之前帧布局显示的内容
        flContainer.removeAllViews();
        //修改当前帧布局显示的内容
        flContainer.addView(pager.mRootView);
        //初始化当前页面的数据
        pager.initData();
        //修改标题
        tvTitle.setText(mNewsMenu.data.get(position).title);
    }
}

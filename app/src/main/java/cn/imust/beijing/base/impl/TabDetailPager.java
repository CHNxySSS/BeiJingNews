package cn.imust.beijing.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.support.constraint.solver.Cache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;

import cn.imust.beijing.R;
import cn.imust.beijing.base.BaseMenuDetailPager;
import cn.imust.beijing.domain.NewsMenu;
import cn.imust.beijing.domain.NewsTab;
import cn.imust.beijing.global.GlobalConstants;
import cn.imust.beijing.utils.CacheUtils;
import cn.imust.beijing.view.TopNewsViewPager;

/**
 * 页签详情页
 */
public class TabDetailPager extends BaseMenuDetailPager {
    private NewsMenu.NewsTabData newsTabData;//当前页签的网络数据
    //private TextView view;
    private TopNewsViewPager mViewPager;
    private ArrayList<NewsTab.TopNews> mTopNewsList;
    private TextView tvTitle;
    private CirclePageIndicator mIndicator;
    private ListView lvList;
    private ArrayList<NewsTab.News> mNewsList;

    public TabDetailPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
        super(activity);
        this.newsTabData = newsTabData;
    }

    @Override
    public View initViews() {
//        view = new TextView(mActivity);
//        view.setTextSize(22);
//        view.setTextColor(Color.RED);
//        view.setGravity(Gravity.CENTER);//居中显示
//        view.setText("页签");
        View view = View.inflate(mActivity, R.layout.pager_tab_detail,null);
        //加载头条新闻头布局
        View headerView = View.inflate(mActivity,R.layout.list_item_header,null);

        mViewPager = headerView.findViewById(R.id.vp_tab_detail);
        tvTitle = headerView.findViewById(R.id.tv_title);
        mIndicator = headerView.findViewById(R.id.indicator);
        lvList = view.findViewById(R.id.lv_list);
        lvList.addHeaderView(headerView);//给listview添加头布局
        return view;
    }

    @Override
    public void initData() {
        //view.setText(newsTabData.title);
        String cache = CacheUtils.getCache(mActivity,GlobalConstants.SERVER_URL + newsTabData.url);
        if(!TextUtils.isEmpty(cache)){
            processData(cache);
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.SERVER_URL + newsTabData.url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result);
                CacheUtils.setCache(mActivity,GlobalConstants.SERVER_URL + newsTabData.url,result);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected void processData(String result) {
        Gson gson = new Gson();
        NewsTab newsTab = gson.fromJson(result,NewsTab.class);
        Log.i("TabDetailPager",newsTab.toString());
        //初始化头条新闻数据
        mTopNewsList = newsTab.data.topnews;
        if(mTopNewsList!=null){
            mViewPager.setAdapter(new TopNewsAdapter());
            mIndicator.setViewPager(mViewPager);//将圆形指示器和vp绑定
            mIndicator.setSnap(true);//快照展示方式
            mIndicator.onPageSelected(0);//将圆点位置归0，保证圆点和页面同步
            mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    //更新头条新闻标题
                    tvTitle.setText(mTopNewsList.get(position).title);
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            //初始化第一页头条新闻标题
            tvTitle.setText(mTopNewsList.get(0).title);
        }
        //初始化新闻列表数据
        mNewsList = newsTab.data.news;
        if(mNewsList != null){
            lvList.setAdapter(new NewsAdapter());
        }
    }

    //头条新闻的数据适配器
    class TopNewsAdapter extends PagerAdapter{
        //加载图片的工具类
        private BitmapUtils mBitmapUtils;
        public TopNewsAdapter(){
            mBitmapUtils = new BitmapUtils(mActivity);
        }
        @Override
        public int getCount() {
            return mTopNewsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView view = new ImageView(mActivity);
            NewsTab.TopNews topNews = mTopNewsList.get(position);
            String topimage = topNews.topimage;//图片的下载链接
            view.setScaleType(ImageView.ScaleType.FIT_XY);//设置缩放模式，图片
            mBitmapUtils.display(view,topimage);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
    //新闻列表适配器
    class NewsAdapter extends BaseAdapter{
        private BitmapUtils mBitmapUtils1;

        public NewsAdapter(){
            mBitmapUtils1 = new BitmapUtils(mActivity);
            mBitmapUtils1.configDefaultLoadingImage(R.drawable.news_pic_default);
        }
        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public NewsTab.News getItem(int i) {
            return mNewsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if(view == null){
                view = View.inflate(mActivity,R.layout.list_item_news,null);
                holder = new ViewHolder();
                holder.ivIcon = view.findViewById(R.id.iv_icon);
                holder.tvTitle = view.findViewById(R.id.tv_title);
                holder.tvTime = view.findViewById(R.id.tv_time);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            NewsTab.News info = getItem(i);
            holder.tvTitle.setText(info.title);
            holder.tvTime.setText(info.pubdate);
            //String str ="http://10.0.2.2"+info.listimage.substring(20);
            Log.i("SSS",info.listimage);
            mBitmapUtils1.display(holder.ivIcon,info.listimage);

            return view;
        }
    }
    static class ViewHolder{
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvTime;
    }
}

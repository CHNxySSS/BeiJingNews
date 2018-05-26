package cn.imust.beijing.base.impl;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.solver.Cache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

import cn.imust.beijing.NewsDetailActivity;
import cn.imust.beijing.R;
import cn.imust.beijing.base.BaseMenuDetailPager;
import cn.imust.beijing.domain.NewsMenu;
import cn.imust.beijing.domain.NewsTab;
import cn.imust.beijing.global.GlobalConstants;
import cn.imust.beijing.utils.CacheUtils;
import cn.imust.beijing.utils.PrefUtils;
import cn.imust.beijing.view.RefreshListView;
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
    private RefreshListView lvList;
    private ArrayList<NewsTab.News> mNewsList;
    private String moreUrl;
    private NewsAdapter mNewsAdapter;
    private Handler mHandler;
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
        lvList.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //刷新数据
                getDataFromServer();
            }

            @Override
            public void onLoadMore() {
                if(moreUrl!=null){
                    getMoreDataFromServer();
                }else {
                    Toast.makeText(mActivity,"没有更多数据啦",Toast.LENGTH_SHORT).show();
                    //隐藏加载更多控件
                    lvList.onRefreshComplete();
                }
            }
        });
        //设置点击事件
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //头布局也算位置，所有用position时要将头布局个数减掉
                int headerViewsCount = lvList.getHeaderViewsCount();
                position -= headerViewsCount;
                NewsTab.News news = mNewsList.get(position);
                //标记已读未读：将已读新闻id保存在sp中
                //"read_ids" = 10000,10001,10002
                String read_ids = PrefUtils.getString(mActivity,"read_ids","");
                if(!read_ids.contains(news.id)){
                    Log.i("read_ids","AAA");
                    read_ids = read_ids + news.id + ",";
                    PrefUtils.putString(mActivity,"read_ids",read_ids);
                }
                //刷新ListView，全局刷新
                //mNewsAdapter.notifyDataSetChanged();
                //局部刷新
                TextView tvTitle = view.findViewById(R.id.tv_title);
                tvTitle.setTextColor(Color.GRAY);
                //跳到新闻详情页
                Intent intent = new Intent(mActivity, NewsDetailActivity.class);
                intent.putExtra("url",news.url);//传递网页链接
                mActivity.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void initData() {
        //view.setText(newsTabData.title);
        String cache = CacheUtils.getCache(mActivity,GlobalConstants.SERVER_URL + newsTabData.url);
        if(!TextUtils.isEmpty(cache)){
            processData(cache,false);
        }
        getDataFromServer();
    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.SERVER_URL + newsTabData.url, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result,false);
                CacheUtils.setCache(mActivity,GlobalConstants.SERVER_URL + newsTabData.url,result);
                //隐藏下拉刷新控件
                lvList.onRefreshComplete();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();
                //隐藏下拉刷新控件
                lvList.onRefreshComplete();
            }
        });
    }
    //请求下一页网络数据
    private void getMoreDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, moreUrl, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result,true);

                //隐藏下拉刷新控件
                lvList.onRefreshComplete();
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();
                //隐藏下拉刷新控件
                lvList.onRefreshComplete();
            }
        });
    }
    protected void processData(String result,boolean isMore) {
        Gson gson = new Gson();
        NewsTab newsTab = gson.fromJson(result,NewsTab.class);
        Log.i("TabDetailPager",newsTab.toString());
        //获取下一页数据地址
        String more = newsTab.data.more;
        if(!TextUtils.isEmpty(more)){
            moreUrl = GlobalConstants.SERVER_URL + more;
        }else {
            moreUrl =null;
        }
        if(!isMore){
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
                //启动自动轮播效果
                if(mHandler == null){
                    mHandler = new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            //super.handleMessage(msg);
                            int currentItem = mViewPager.getCurrentItem();
                            if(currentItem<mTopNewsList.size()-1){
                                currentItem++;
                            }else {
                                currentItem=0;
                            }
                            mViewPager.setCurrentItem(currentItem);
                            mHandler.sendEmptyMessageDelayed(0,2000);
                        }
                    };
                    //发送延时消息，启动自动轮播
                    mHandler.sendEmptyMessageDelayed(0,2000);
                }
                mViewPager.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                mHandler.removeCallbacksAndMessages(null);//移除消息，停止轮播
                                break;
                            case MotionEvent.ACTION_UP:
                                //发送延时消息，启动自动轮播
                                mHandler.sendEmptyMessageDelayed(0,2000);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
            }
            //初始化新闻列表数据
            mNewsList = newsTab.data.news;
            if(mNewsList != null){
                mNewsAdapter = new NewsAdapter();
                lvList.setAdapter(mNewsAdapter);
            }
        }else {
            //加载更多
            ArrayList<NewsTab.News> moreNews = newsTab.data.news;
            mNewsList.addAll(moreNews);//追加更多数据
            //刷新ListView
            mNewsAdapter.notifyDataSetChanged();
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
            //判断已读未读
            String readIds = PrefUtils.getString(mActivity,"read_ids","");
            if(readIds.contains(info.id)){
                holder.tvTitle.setTextColor(Color.GRAY);
            }else {
                holder.tvTitle.setTextColor(Color.BLACK);
            }
            return view;
        }
    }
    static class ViewHolder{
        public ImageView ivIcon;
        public TextView tvTitle;
        public TextView tvTime;
    }
}

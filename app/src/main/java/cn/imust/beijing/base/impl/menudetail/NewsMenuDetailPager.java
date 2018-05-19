package cn.imust.beijing.base.impl.menudetail;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
//import com.viewpagerindicator.TabPageIndicator;
import com.jeremyfeinstein.slidingmenu.lib.CustomViewAbove;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

import java.util.ArrayList;

import cn.imust.beijing.MainActivity;
import cn.imust.beijing.R;
import cn.imust.beijing.base.BaseMenuDetailPager;
import cn.imust.beijing.base.impl.TabDetailPager;
import cn.imust.beijing.domain.NewsMenu;

/**
 * 新闻的菜单详情页
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private ArrayList<NewsMenu.NewsTabData> children;
    private ArrayList<TabDetailPager> mPagers;
    private TabPageIndicator mIndicator;

    public NewsMenuDetailPager(Activity activity) {
        super(activity);
    }

    public NewsMenuDetailPager(Activity mActivity, ArrayList<NewsMenu.NewsTabData> children) {
        super(mActivity);
        this.children=children;
        //super();
    }

    @Override
    public View initViews() {
//        TextView view = new TextView(mActivity);
//        view.setTextSize(22);
//        view.setTextColor(Color.RED);
//        view.setGravity(Gravity.CENTER);//居中显示
//        view.setText("新闻的菜单详情页");
        View view= View.inflate(mActivity, R.layout.pager_news_menu_detail,null);
        mViewPager = view.findViewById(R.id.vp_news_menu_detail);
        mIndicator = view.findViewById(R.id.indicator);
        //@OnClick(R.id.btn_next) //通过xUtils注解的方式绑定事件

            //跳到下一个页面
            view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int currentPos = mViewPager.getCurrentItem();
                    mViewPager.setCurrentItem(++currentPos);
                }
            });


        return view;
    }

    @Override
    public void initData() {
        //初始化12个页签对象
        //以服务器为准
        mPagers = new ArrayList<TabDetailPager>();
        for(int i=0;i< children.size();i++){
            TabDetailPager pager = new TabDetailPager(mActivity,children.get(i));
            mPagers.add(pager);
        }
        mViewPager.setAdapter(new NewsMenuDetailAdapter());
        mIndicator.setViewPager(mViewPager);//将ViewPager和Indicator关联在一起

        //mViewPager.setOnPageChangeListener(this);
        mIndicator.setOnPageChangeListener(this);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position==0){
            //打开侧边栏
            setSlidingMenuEnable(true);
        }else {
            setSlidingMenuEnable(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //开启or禁用侧边栏
    private void setSlidingMenuEnable(boolean enable){
        //获取SlidingMenu对象
        //获取MainActivity对象
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        if(!enable){
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        }else {
            slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }


    }
    class NewsMenuDetailAdapter extends PagerAdapter{

        //返回指示器indicator的标题
        @Override
        public CharSequence getPageTitle(int position) {
            return children.get(position).title;
            //return null;
        }

        @Override
        public int getCount() {
            return mPagers.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager pager = mPagers.get(position);
            pager.initData();//初始化数据
            container.addView(pager.mRootView);
            return pager.mRootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

}

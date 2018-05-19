package cn.imust.beijing.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;

import java.util.ArrayList;

import cn.imust.beijing.MainActivity;
import cn.imust.beijing.R;
import cn.imust.beijing.base.BasePager;
import cn.imust.beijing.base.impl.GovAffairsPager;
import cn.imust.beijing.base.impl.HomePager;
import cn.imust.beijing.base.impl.NewsCenterPager;
import cn.imust.beijing.base.impl.SettingPager;
import cn.imust.beijing.base.impl.SmartServicePager;
import cn.imust.beijing.view.NoScrollViewPager;

public class ContentFragment extends BaseFragment {

    private NoScrollViewPager mViewPager;
    private ArrayList<BasePager> mList;//5个标签页的集合
    private RadioGroup rgGroup;

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_content,null);
        ViewUtils.inject(this,view);
        mViewPager = view.findViewById(R.id.vp_content);
        rgGroup = view.findViewById(R.id.rg_group);
        return view;
    }

    @Override
    public void initData() {
        //初始化5个标签页面对象
        mList = new ArrayList<BasePager>();
        mList.add(new HomePager(mActivity));
        mList.add(new NewsCenterPager(mActivity));
        mList.add(new SmartServicePager(mActivity));
        mList.add(new GovAffairsPager(mActivity));
        mList.add(new SettingPager(mActivity));
        mViewPager.setAdapter(new ContentAdapter());

        rgGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_home:
                        //mViewPager.setCurrentItem(0);
                        mViewPager.setCurrentItem(0,false);//去掉页面切换的动画
                        break;
                    case R.id.rb_news:
                        mViewPager.setCurrentItem(1,false);
                        break;
                    case R.id.rb_smart:
                        mViewPager.setCurrentItem(2,false);
                        break;
                    case R.id.rb_gov:
                        mViewPager.setCurrentItem(3,false);
                        break;
                    case R.id.rb_setting:
                        mViewPager.setCurrentItem(4,false);
                        break;
                    default:
                        break;
                }
            }
        });
        //监听ViewPager页面切换事件，初始化当前页面数据
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                BasePager pager = mList.get(position);
                pager.initData();
                if(position==0||position==mList.size()-1){
                    //禁用
                    setSlidingMenuEnable(false);
                }else {
                    setSlidingMenuEnable(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //手动初始化第一个页面
        mList.get(0).initData();

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
    class ContentAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //获取当前页面的对象
            BasePager pager = mList.get(position);
            //此方法导致每次都提前加载下一页数据，浪费流量和性能，不建议在此处初始化数据
            //pager.initData();//初始化布局（给帧布局添加布局对象），以子类实现为准
            //布局对象
            //pager.mRootView当前页面根部局
            container.addView(pager.mRootView);
            return pager.mRootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
    //获取新闻中心对象
    public NewsCenterPager getNewsCenterPager(){
        NewsCenterPager pager = (NewsCenterPager) mList.get(1);
        return pager;
    }
}

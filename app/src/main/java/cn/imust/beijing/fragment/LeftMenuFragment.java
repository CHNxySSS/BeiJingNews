package cn.imust.beijing.fragment;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import cn.imust.beijing.MainActivity;
import cn.imust.beijing.R;
import cn.imust.beijing.base.impl.NewsCenterPager;
import cn.imust.beijing.domain.NewsMenu;

/*
* 侧边栏Fragment
* */
public class LeftMenuFragment extends BaseFragment {
    private ArrayList<NewsMenu.NewsMenuData> data;//分类的网络数据
    private ListView lvList;
    private int mCurrentPos;//当前选中的菜单位置

    @Override
    public View initViews() {
        View view = View.inflate(mActivity, R.layout.fragment_left_menu,null);
        lvList = view.findViewById(R.id.lv_menu);

        return view;
    }
    //设置侧边栏数据的方法
    //通过此方法，可以从新闻中心页面将网络数据传递过来
    public void setMenuData(ArrayList<NewsMenu.NewsMenuData> data) {
        //将当前选中位置归0，避免侧边栏选中位置和菜单详情页不同步
        mCurrentPos = 0;
        Log.i("LeftMenuFragment","SSS"+data.toString());
        this.data=data;
        final LeftMenuAdapter mAdapter = new LeftMenuAdapter();
        lvList.setAdapter(mAdapter);
        lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                mCurrentPos=position;//更新当前点击位置
                //刷新ListView
                mAdapter.notifyDataSetChanged();
                //收回侧边栏
                toggle();

                setMenuDetailPager(position);
            }
        });
    }
    //修改菜单详情页
    private void setMenuDetailPager(int position) {
        //修改新闻中心的帧布局
        //获取新闻中心对象
        MainActivity mainUI = (MainActivity) mActivity;
        ContentFragment fragment = mainUI.getContentFragment();
        NewsCenterPager pager = fragment.getNewsCenterPager();
        //由新闻中心修改菜单详情页
        pager.setMenuDetailPager(position);
    }

    //控制侧边栏的开关
    private void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();//如果当前为开，则关；反之亦然
    }

    class LeftMenuAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public NewsMenu.NewsMenuData getItem(int i) {
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View view1 = View.inflate(mActivity,R.layout.list_item_left_menu,null);
            TextView tvMenu = view1.findViewById(R.id.tv_menu);
            //设置TextView的可用或不可用来控制颜色
            if(mCurrentPos == i){
                //当前item选中
                tvMenu.setEnabled(true);
            }else {
                tvMenu.setEnabled(false);
            }
            NewsMenu.NewsMenuData info = getItem(i);
            tvMenu.setText(info.title);
            return view1;
        }
    }
}

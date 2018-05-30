package cn.imust.beijing.base;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import cn.imust.beijing.MainActivity;
import cn.imust.beijing.R;

/*
* 5个标签页的基类
* 共性：子类都有标题栏，所以可以在父类中加载布局页面
* */
public class BasePager {
    public Activity mActivity;
    public TextView tvTitle;
    public ImageButton btnMenu;
    public FrameLayout flContainer;//空的帧布局，由子类动态填充

    public ImageButton btnDisplay;//组图切换按钮
    public View mRootView;//当前页面的根布局

    public BasePager(Activity activity){
        mActivity=activity;
        //在页面对象创建时，就初始化了布局
        mRootView = initViews();
    }
    //初始化布局
    public View initViews(){
        View view = View.inflate(mActivity, R.layout.base_pager,null);
        tvTitle = view.findViewById(R.id.tv_title);
        btnMenu = view.findViewById(R.id.btn_menu);
        flContainer = view.findViewById(R.id.fl_container);
        btnDisplay = view.findViewById(R.id.btn_display);
        //点击菜单按钮，控制侧边栏开关
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });
        return view;
    }
    //控制侧边栏的开关
    private void toggle() {
        MainActivity mainUI = (MainActivity) mActivity;
        SlidingMenu slidingMenu = mainUI.getSlidingMenu();
        slidingMenu.toggle();//如果当前为开，则关；反之亦然
    }
    //初始化数据
    public void initData(){}
}

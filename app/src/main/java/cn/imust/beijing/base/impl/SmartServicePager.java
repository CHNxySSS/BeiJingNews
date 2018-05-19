package cn.imust.beijing.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import cn.imust.beijing.base.BasePager;

/**
 * 智慧服务
 * */
public class SmartServicePager extends BasePager {
    public SmartServicePager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        //给空的帧布局动态添加布局对象
        TextView view = new TextView(mActivity);
        view.setTextSize(22);
        view.setTextColor(Color.RED);
        view.setGravity(Gravity.CENTER);//居中显示
        view.setText("智慧服务");
        flContainer.addView(view);

        //修改标题
        tvTitle.setText("生活");
    }
}

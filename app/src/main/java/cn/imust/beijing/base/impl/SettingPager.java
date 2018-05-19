package cn.imust.beijing.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import cn.imust.beijing.base.BasePager;

/**
 * 设置
 * */
public class SettingPager extends BasePager {
    public SettingPager(Activity activity) {
        super(activity);
    }

    @Override
    public void initData() {
        //给空的帧布局动态添加布局对象
        TextView view = new TextView(mActivity);
        view.setTextSize(22);
        view.setTextColor(Color.RED);
        view.setGravity(Gravity.CENTER);//居中显示
        view.setText("设置");
        flContainer.addView(view);

        //修改标题
        tvTitle.setText("设置");
        //隐藏菜单按钮
        btnMenu.setVisibility(View.GONE);
    }
}

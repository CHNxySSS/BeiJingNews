package cn.imust.beijing.base.impl.menudetail;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import cn.imust.beijing.base.BaseMenuDetailPager;

/**
 * 组图的菜单详情页
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager {
    public PhotosMenuDetailPager(Activity activity) {
        super(activity);
    }

    @Override
    public View initViews() {
        TextView view = new TextView(mActivity);
        view.setTextSize(22);
        view.setTextColor(Color.RED);
        view.setGravity(Gravity.CENTER);//居中显示
        view.setText("组图的菜单详情页");
        return view;
    }
}

package cn.imust.beijing.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 *Fragment基类
 */

public abstract class BaseFragment extends Fragment {

    public Activity mActivity;//当作Context去使用
    public View mRootView;//fragment的根布局

    //fragment的创建
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();//获取fragment所依赖的Activity对象
    }

    //初始化fragment布局
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = initViews();
        return mRootView;
    }

    //fragment所在的activity创建完成
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    //初始化布局，必须由子类去实现
    public abstract View initViews();

    //初始化数据,子类可以不实现，因为布局可能是写死的，不需要填充数据
    public void initData(){}

}

package cn.imust.beijing.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 头条新闻的ViewPager
 */
public class TopNewsViewPager extends ViewPager {

    private int startX;
    private int startY;

    public TopNewsViewPager(Context context) {
        super(context);
    }

    public TopNewsViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //请求父控件不拦截事件
        getParent().requestDisallowInterceptTouchEvent(true);
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                //按下
                startX = (int) ev.getX();
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                //移动
                int endX = (int) ev.getX();
                int endY = (int) ev.getY();
                int dx = endX-startX;
                int dy = endY-startY;
                if(Math.abs(dx)>Math.abs(dy)){
                    //左右滑动
                    if(dx>0){
                        //右滑
                        if(getCurrentItem()==0){
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }else {
                        if(getCurrentItem()==(getAdapter().getCount()-1)){
                            getParent().requestDisallowInterceptTouchEvent(false);
                        }
                    }
                }else {
                    //上下滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
                default:
                    break;
        }
        return super.dispatchTouchEvent(ev);
    }
}

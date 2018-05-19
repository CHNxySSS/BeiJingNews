package cn.imust.beijing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import cn.imust.beijing.fragment.ContentFragment;
import cn.imust.beijing.fragment.LeftMenuFragment;

/**
 * 主页面
 *
 * 当一个activity要展示Fragment的话，必须继承FragmentActivity
 * */
public class MainActivity extends SlidingFragmentActivity {
    private static final String TAG_LEFT_MENU = "fragment_left_menu";
    private static final String TAG_CONTENT = "fragment_content";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //设置侧边栏布局
        setBehindContentView(R.layout.left_menu);

        SlidingMenu slidingMenu = getSlidingMenu();
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);//全屏触摸
        slidingMenu.setBehindOffset(800);//屏幕预留300px
        initFragment();
    }
    //初始化Fragment
    private void initFragment(){
        //获取Fragment管理器
        FragmentManager fm = getSupportFragmentManager();
        //开始一个事物
        FragmentTransaction transaction = fm.beginTransaction();
        //使用fragment替换现有布局
        //参数1-当前布局的id 参数2-要替换的fragment对象
        transaction.replace(R.id.fl_content,new ContentFragment(),TAG_CONTENT);
        transaction.replace(R.id.fl_left_menu,new LeftMenuFragment(),TAG_LEFT_MENU);
        transaction.commit();//提交事物

    }
    //获取侧边栏对象
    public LeftMenuFragment getLeftMenuFragment(){
        FragmentManager fm = getSupportFragmentManager();
        LeftMenuFragment fragment = (LeftMenuFragment) fm.findFragmentByTag(TAG_LEFT_MENU);
        return fragment;
    }
    //获取主页Fragment对象
    public ContentFragment getContentFragment(){
        FragmentManager fm = getSupportFragmentManager();
        ContentFragment fragment = (ContentFragment) fm.findFragmentByTag(TAG_CONTENT);
        return fragment;
    }
}

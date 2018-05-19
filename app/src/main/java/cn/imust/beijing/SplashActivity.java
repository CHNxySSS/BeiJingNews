package cn.imust.beijing;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import cn.imust.beijing.utils.PrefUtils;

public class SplashActivity extends Activity {

    private RelativeLayout rlRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        //旋转
        RotateAnimation animation=new RotateAnimation(0,360,
                Animation.RELATIVE_TO_SELF,0.5f, Animation.RELATIVE_TO_SELF,0.5f);
        animation.setDuration(2000);//动画时间
        animation.setFillAfter(true);//保持住动画结束状态
        // /缩放
        ScaleAnimation scaleAnimation=new ScaleAnimation(0,1,0,1,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        scaleAnimation.setDuration(2000);
        scaleAnimation.setFillAfter(true);
        //渐变
        AlphaAnimation alphaAnimation=new AlphaAnimation(0,1);
        alphaAnimation.setDuration(2000);
        alphaAnimation.setFillAfter(true);
        //动画集合
        AnimationSet set=new AnimationSet(false);
        set.addAnimation(animation);
        set.addAnimation(scaleAnimation);
        set.addAnimation(alphaAnimation);
        rlRoot.startAnimation(set);
        //给动画结束加监听
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            //动画结束
            @Override
            public void onAnimationEnd(Animation animation) {
                boolean isGuideShow = PrefUtils.getBoolean(getApplicationContext(),"is_guide_show",false);
                if(!isGuideShow){
                    //跳到新手引导页
                    startActivity(new Intent(SplashActivity.this,GuideActivity.class));
                }else {
                    //跳到主页面
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
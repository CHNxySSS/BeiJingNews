package cn.imust.beijing;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import cn.imust.beijing.utils.PrefUtils;

/*
新手引导页
* */
public class GuideActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout llContainer;
    //图片id集合
    private int[] mImageIds = new int[]{R.drawable.guide_1,
            R.drawable.guide_2,R.drawable.guide_3};
    private ArrayList<ImageView> mImageViews;
    private int mPointDis;//圆点移动距离
    private ImageView ivRedPoint;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initViews();
        initData();
    }

    //初始化布局
    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.vp_guide);
        llContainer = findViewById(R.id.ll_container);
        ivRedPoint = findViewById(R.id.iv_red_point);
        btnStart = findViewById(R.id.btn_start);

    }
    //初始化数据
    private void initData(){
        //初始化三张图片的ImageView
        mImageViews = new ArrayList<ImageView>();
        for(int i=0;i<mImageIds.length;i++){
            ImageView view = new ImageView(this);
            view.setBackgroundResource(mImageIds[i]);
            mImageViews.add(view);

            //初始化小圆点
            ImageView point = new ImageView(this);
            point.setImageResource(R.drawable.shape_point_normal);

            //初始化布局参数
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if(i>0){
                params.leftMargin = 10;//从第二个点开始设置左边距10px
            }
            point.setLayoutParams(params);
            llContainer.addView(point);
        }
        viewPager.setAdapter(new GuideAdapter());

        //监听ViewPager滑动事件，更新小红点位置
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            //监听页面滑动事件
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //通过修改小红点的左边距来更新小红点的位置
                int leftMargin = (int) (mPointDis * positionOffset+position*mPointDis + 0.5f);
                //获取小红点的布局参数
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRedPoint.getLayoutParams();
                params.leftMargin = leftMargin;
                ivRedPoint.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                //最后一个页面显示开始体验按钮
                if(position == mImageIds.length-1){
                    btnStart.setVisibility(View.VISIBLE);
                }else {
                    btnStart.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        ivRedPoint.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            //一旦视图树的layout方法调用完成，就会回调此方法
            @Override
            public void onGlobalLayout() {
                //布局位置已经确定，可以拿到布局信息了
                //计算圆点的距离 = 第二个圆点的左边距 - 第一个圆点的左边距
                mPointDis = llContainer.getChildAt(1).getLeft()-llContainer.getChildAt(0).getLeft();
                //拿到距离之后及时移除观察者
                //ivRedPoint.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ivRedPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        //开始体验按钮点击
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //在sp中记录访问过引导页的状态
                PrefUtils.setBoolean(getApplicationContext(),"is_guide_show",true);
                //跳到主页面
                Intent intent = new Intent(GuideActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
    class GuideAdapter extends PagerAdapter{
        @Override
        public int getCount() {
            return mImageIds.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        //初始化布局
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = mImageViews.get(position);
            container.addView(imageView);
            return imageView;
        }

        //销毁布局
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);注释掉，因为会引起底层的异常
            container.removeView((View) object);
        }
    }

}
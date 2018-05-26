package cn.imust.beijing.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.imust.beijing.R;

/**
 * 下拉刷新ListView
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener{

    private View mHeaderView;
    private int mHeaderViewHeight;
    private int startY=-1;
    private static final int STATE_PULL_TO_REFRESH = 0;//下拉刷新状态
    private static final int STATE_RELEASE_TO_REFRESH = 1;//松开刷新
    private static final int STATE_REFRESHING = 2;//正在刷新
    private int mCurrentState = STATE_PULL_TO_REFRESH;//当前状态，默认下拉刷新
    private TextView tvState;
    private TextView tvTime;
    private ImageView ivArrow;
    private ProgressBar pbLoading;
    private RotateAnimation animUp;
    private RotateAnimation animDown;
    private View mFooterView;
    private int mFooterViewHeight;
    private boolean isLoadMore = false;//标记是否正在加载更多

    public RefreshListView(Context context) {
        this(context,null);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initHeaderView();
        initFooterView();
    }
    //初始化头布局
    private void initHeaderView(){
        mHeaderView = View.inflate(getContext(), R.layout.pull_to_refresh_header,null);
        addHeaderView(mHeaderView);//给listview添加头布局
        tvState = mHeaderView.findViewById(R.id.tv_state);
        tvTime = mHeaderView.findViewById(R.id.tv_time);
        ivArrow = mHeaderView.findViewById((R.id.iv_arrow));
        pbLoading = mHeaderView.findViewById(R.id.pb_loading);
        //隐藏头布局
        //获取当前头布局的高度，然后设置负的paddingTop,布局就会向上走
        //int height = mHeaderView.getHeight();//拿不到高度，控件没有绘制完成
        mHeaderView.measure(0,0);//手动测量
        mHeaderViewHeight = mHeaderView.getMeasuredHeight();//获取测量后的高度
        mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
        initArrowAnim();
        //刷新时间
        setRefreshTime();
    }
    //初始化脚布局
    private void initFooterView(){
        mFooterView = View.inflate(getContext(), R.layout.pull_to_refresh_foot,null);
        addFooterView(mFooterView);
        mFooterView.measure(0,0);
        mFooterViewHeight = mFooterView.getMeasuredHeight();
        //隐藏脚布局
        mFooterView.setPadding(0,-mFooterViewHeight,0,0);
        //设置滑动监听
        setOnScrollListener(this);
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                startY = (int) ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if(startY==-1){
                    startY = (int) ev.getY();
                }
                int endY = (int) ev.getY();
                int dy = endY - startY;
                //如果正在刷新，什么都不做
                if(mCurrentState == STATE_REFRESHING){
                    break;
                }
                int firstVisiblePosition = this.getFirstVisiblePosition();//当前显示的第一个item的位置
                if(dy>0 && firstVisiblePosition==0){
                    //下拉动作&&在listview的顶部
                    int padding = -mHeaderViewHeight + dy;
                    if(padding>0&&mCurrentState!=STATE_RELEASE_TO_REFRESH){
                        //切换到松开刷新
                        mCurrentState = STATE_RELEASE_TO_REFRESH;
                        refreshState();
                    }else if(padding<=0&&mCurrentState!=STATE_PULL_TO_REFRESH){
                        mCurrentState = STATE_PULL_TO_REFRESH;
                        refreshState();
                    }
                    mHeaderView.setPadding(0,padding,0,0);
                    return true;//消费此事件，处理下拉刷新，不要listview参与
                }else
                break;
            case MotionEvent.ACTION_UP:
                startY=-1;//起始坐标归-1
                if(mCurrentState==STATE_RELEASE_TO_REFRESH){
                    mCurrentState=STATE_REFRESHING;
                    mHeaderView.setPadding(0,0,0,0);
                    //pbLoading.setVisibility(View.INVISIBLE);
                    refreshState();
                }else if(mCurrentState==STATE_PULL_TO_REFRESH){
                    mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }
    //初始化箭头动画
    private void initArrowAnim(){
        animUp = new RotateAnimation(0,-180,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        animUp.setDuration(300);
        animUp.setFillAfter(true);//保持住动画结束的状态
        animDown = new RotateAnimation(-180,0,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);
        animDown.setDuration(300);
        animDown.setFillAfter(true);//保持住动画结束的状态
    }
    //根据当前状态刷新界面
    private void refreshState() {
        switch (mCurrentState){
            case STATE_PULL_TO_REFRESH:
                tvState.setText("下拉刷新");
                //ivArrow.clearAnimation();
                //pbLoading.clearAnimation();
                pbLoading.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.startAnimation(animDown);
                break;
            case STATE_RELEASE_TO_REFRESH:
                tvState.setText("松开刷新");
                pbLoading.setVisibility(View.INVISIBLE);
                ivArrow.setVisibility(View.VISIBLE);
                ivArrow.startAnimation(animUp);

                break;
            case STATE_REFRESHING:
                tvState.setText("正在刷新...");
                pbLoading.setVisibility(View.VISIBLE);
                ivArrow.clearAnimation();
                ivArrow.setVisibility(View.INVISIBLE);

                //回调下拉刷新
                if(mListener!=null){
                    mListener.onRefresh();
                }
                break;
                default:
                    break;
        }
    }
    private OnRefreshListener mListener;
    //刷新结束，隐藏控件
    public void onRefreshComplete(){
        if(!isLoadMore){
            mHeaderView.setPadding(0,-mHeaderViewHeight,0,0);
            //初始化状态
            tvState.setText("下拉刷新");
            pbLoading.setVisibility(View.INVISIBLE);
            ivArrow.setVisibility(View.VISIBLE);
            mCurrentState = STATE_PULL_TO_REFRESH;
            //更新刷新时间
            setRefreshTime();
        }else {
            //隐藏加载更多控件
            mFooterView.setPadding(0,-mFooterViewHeight,0,0);
            isLoadMore=false;
        }

    }
    //设置刷新回调监听
    public void setOnRefreshListener(OnRefreshListener listener){
        mListener = listener;
    }

    //滑动状态发送变化
    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
        if(i==SCROLL_STATE_IDLE){//空闲状态
            int lastVisiblePosition = getLastVisiblePosition();//当前显示的最后一个item的位置
            if(lastVisiblePosition == getCount()-1 && !isLoadMore){
                isLoadMore = true;
                //显示加载中布局
                mFooterView.setPadding(0,0,0,0);
                setSelection(getCount()-1);//显示最后一个item的位置（加载中布局）
                //加载更多数据
                if(mListener != null){
                    mListener.onLoadMore();
                }
            }
        }
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {

    }

    //回调接口，通知刷新状态
    public interface OnRefreshListener{
        //下拉刷新的回调
        void onRefresh();
        //加载更多的回调
        void onLoadMore();
    }
    //设置刷新时间
    private void setRefreshTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(new Date());
        tvTime.setText(time);
    }
}

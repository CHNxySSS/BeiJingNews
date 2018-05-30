package cn.imust.beijing.base.impl.menudetail;

import android.app.Activity;
import android.graphics.Color;
import android.support.constraint.solver.Cache;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import java.util.ArrayList;

import cn.imust.beijing.R;
import cn.imust.beijing.base.BaseMenuDetailPager;
import cn.imust.beijing.domain.PhotosBean;
import cn.imust.beijing.global.GlobalConstants;
import cn.imust.beijing.utils.CacheUtils;

/**
 * 组图的菜单详情页
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager implements View.OnClickListener{

    private ListView lvList;
    private GridView gvList;
    private ArrayList<PhotosBean.PhotoNews> mPhotoList;
    private ImageButton btnDisplay;
    public PhotosMenuDetailPager(Activity activity, ImageButton btnDisplay) {
        super(activity);
        this.btnDisplay = btnDisplay;
        btnDisplay.setOnClickListener(this);//设置切换按钮的监听
    }

    @Override
    public View initViews() {
//        TextView view = new TextView(mActivity);
//        view.setTextSize(22);
//        view.setTextColor(Color.RED);
//        view.setGravity(Gravity.CENTER);//居中显示
//        view.setText("组图的菜单详情页");
        View view = View.inflate(mActivity, R.layout.pager_photos_menu_detail,null);
        lvList = view.findViewById(R.id.lv_list);
        gvList = view.findViewById(R.id.gv_list);
        return view;
    }

    @Override
    public void initData() {
        //super.initData();
        String cache = CacheUtils.getCache(mActivity,GlobalConstants.PHOTOS_URL);
        if(!TextUtils.isEmpty(cache)){
            processData(cache);
        }
        getDataFromServer();

    }

    private void getDataFromServer() {
        HttpUtils utils = new HttpUtils();
        utils.send(HttpRequest.HttpMethod.GET, GlobalConstants.PHOTOS_URL, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                String result = responseInfo.result;
                processData(result);
                CacheUtils.setCache(mActivity,GlobalConstants.PHOTOS_URL,result);
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                error.printStackTrace();
                Toast.makeText(mActivity,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }
    //解析数据
    private void processData(String result) {
        Gson gson = new Gson();
        PhotosBean photosBean = gson.fromJson(result, PhotosBean.class);
        mPhotoList = photosBean.data.news;
        //给listview设置数据
        lvList.setAdapter(new PhotosAdapter());
        //给GridView设置数据
        gvList.setAdapter(new PhotosAdapter());
    }
    private boolean isListView = true;//判断是否是ListView
    @Override
    public void onClick(View view) {
        if(isListView){
            //显示GridView
            lvList.setVisibility(View.GONE);
            gvList.setVisibility(View.VISIBLE);
            btnDisplay.setImageResource(R.drawable.icon_pic_list_type);
            isListView = false;
        }else {
            lvList.setVisibility(View.VISIBLE);
            gvList.setVisibility(View.GONE);
            btnDisplay.setImageResource(R.drawable.icon_pic_grid_type);
            isListView = true;
        }
    }

    class PhotosAdapter extends BaseAdapter{

        private final BitmapUtils mBitmapUtils;

        public PhotosAdapter(){
            mBitmapUtils = new BitmapUtils(mActivity);
            mBitmapUtils.configDefaultLoadingImage(R.drawable.pic_item_list_default);
        }
        @Override
        public int getCount() {
            return mPhotoList.size();
        }

        @Override
        public PhotosBean.PhotoNews getItem(int i) {
            return mPhotoList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder;
            if(view==null){
                view = View.inflate(mActivity,R.layout.list_item_photo,null);
                holder = new ViewHolder();
                holder.ivPic = view.findViewById(R.id.iv_pic);
                holder.tvTitle = view.findViewById(R.id.tv_title);
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }
            PhotosBean.PhotoNews item = getItem(i);
            holder.tvTitle.setText(item.title);
            mBitmapUtils.display(holder.ivPic,item.listimage);
            return view;
        }
    }
    static class ViewHolder{
        public TextView tvTitle;
        public ImageView ivPic;
    }
}

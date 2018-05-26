package cn.imust.beijing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 新闻详情页
 */
public class NewsDetailActivity extends Activity implements View.OnClickListener{
    private LinearLayout llControl;
    private ImageButton btnBack;
    private ImageButton btnMenu;
    private ImageButton btnTextSize;
    private ImageButton btnShare;
    private WebView mWebView;
    private ProgressBar pbLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_detail);
        llControl = findViewById(R.id.ll_control);
        btnBack = findViewById(R.id.btn_back);
        btnMenu = findViewById(R.id.btn_menu);
        btnTextSize = findViewById(R.id.btn_textsize);
        btnShare = findViewById(R.id.btn_share);
        mWebView = findViewById(R.id.webview);
        pbLoading = findViewById(R.id.pb_loading);
        initViews();

        //给WebView设置监听
        mWebView.setWebViewClient(new WebViewClient(){
            //页面开始加载
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pbLoading.setVisibility(View.VISIBLE);
            }
            //跳转链接
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }
            //加载结束
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pbLoading.setVisibility(View.GONE);
            }
        });
        String url = getIntent().getStringExtra("url");
        mWebView.loadUrl(url);
    }

    private void initViews() {
        btnBack.setVisibility(View.VISIBLE);
        btnMenu.setVisibility(View.GONE);
        llControl.setVisibility(View.VISIBLE);
        btnBack.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        btnTextSize.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_textsize:
                showChooseDialog();
                break;
            case R.id.btn_share:
                showShare();
                break;
                default:
                    break;
        }
    }
    private int mTempWhich;
    private int mCurrentWhich;//当前选中的字体位置
    //显示选择字体的弹窗
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("字体设置");
        String[] items = new String[]{"超大号字体","大号字体","正常字体","小号字体","超小号字体"};
        //显示单选框，参1：单选字符串数组；参2：当前默认选中的位置；参3：选中监听
        builder.setSingleChoiceItems(items, mCurrentWhich, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mTempWhich = i;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //获取webview的设置对象
                WebSettings settings = mWebView.getSettings();
                switch (mTempWhich){
                    case 0:
                        settings.setTextSize(WebSettings.TextSize.LARGEST);
                        break;
                    case 1:
                        settings.setTextSize(WebSettings.TextSize.LARGER);
                        break;
                    case 2:
                        settings.setTextSize(WebSettings.TextSize.NORMAL);
                        break;
                    case 3:
                        settings.setTextSize(WebSettings.TextSize.SMALLER);
                        break;
                    case 4:
                        settings.setTextSize(WebSettings.TextSize.SMALLEST);
                        break;
                        default:
                            break;
                }
                mCurrentWhich = mTempWhich;//更新字体的位置
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.show();
    }
    private void showShare() {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，微信、QQ和QQ空间等平台使用
        oks.setTitle("");
        // titleUrl QQ和QQ空间跳转链接
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url在微信、微博，Facebook等平台中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网使用
        oks.setComment("我是测试评论文本");
        // 启动分享GUI
        oks.show(this);
    }
}

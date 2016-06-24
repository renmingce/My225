package com.example.administrator.imageloader.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.imageloader.R;
import com.example.administrator.imageloader.bean.ImageBean;
import com.example.administrator.imageloader.fragment.ImageFragment;
import com.example.administrator.imageloader.listenner.MyCacheCallBack;
import com.example.administrator.imageloader.util.AppUtils;
import com.example.administrator.imageloader.util.Constants;

import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DragImageActivity extends FragmentActivity {

    private TextView tv_drag_url;
    private TextView tv_drag_pageno;
    private ViewPager vp_drap;
    private ImageView iv_drag_download;
    private ImageView iv_drag_share;


    private pageSlidePageAdapter adapter;
    private int position;
    private List<ImageBean> imageBeans;
    private ViewPager.OnPageChangeListener listener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            DragImageActivity.this.position = position;
            tv_drag_url.setText(imageBeans.get(position).getUrl());
            tv_drag_pageno.setText((position + 1) + "/" + imageBeans.size());

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_image);

        tv_drag_url = (TextView) findViewById(R.id.tv_drag_url);
        tv_drag_pageno = (TextView) findViewById(R.id.tv_drag_pageno);
        vp_drap = (ViewPager) findViewById(R.id.vp_drap);
        iv_drag_download = (ImageView) findViewById(R.id.iv_drag_download);
        iv_drag_share = (ImageView) findViewById(R.id.iv_drag_share);

        getActionBar().hide();

        Intent intent = getIntent();
        intent.getIntExtra("position", 0);
        imageBeans = (List<ImageBean>) intent.getSerializableExtra("imagebeans");

        tv_drag_url.setText(imageBeans.get(position).getUrl());
        tv_drag_pageno.setText((position + 1) + "/" + imageBeans.size());

        if (Constants.state == Constants.s_web) {
            iv_drag_download.setImageResource(R.drawable.icon_s_download_press);
            iv_drag_share.setVisibility(View.GONE);
        } else {
            iv_drag_share.setVisibility(View.VISIBLE);
            iv_drag_download.setImageResource(R.drawable.garbage_media_cache);
        }

        adapter=new pageSlidePageAdapter(this.getSupportFragmentManager());
        vp_drap.setAdapter(adapter);

        vp_drap.setCurrentItem(position);


        vp_drap.addOnPageChangeListener(listener);
    }


    class pageSlidePageAdapter extends FragmentStatePagerAdapter {

        public pageSlidePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String imagepath = imageBeans.get(position).getUrl();

            return ImageFragment.getInstance(imagepath);
        }

        @Override
        public int getCount() {
            return imageBeans.size();
        }
    }
    
    public void downloadImage(View v) {
        if(Constants.state==Constants.s_web){
            String url = imageBeans.get(position).getUrl();
            downloadImage(url);
            Toast.makeText(DragImageActivity.this, "下载完成", Toast.LENGTH_SHORT).show();
        } else if(Constants.state==Constants.s_local) {
            Bitmap bitmap = BitmapFactory.decodeFile(imageBeans.get(position).getUrl());

            try {
                setWallpaper(bitmap);
                Toast.makeText(DragImageActivity.this, "设置壁纸成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(DragImageActivity.this, "设置壁纸失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void shareImage(View v) {

        File file=new File(imageBeans.get(position).getUrl());
        Intent intent = new Intent();
        intent.setAction(intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),"image/*");
        startActivity(intent);
    }
    private void downloadImage(String url) {

        File filesDir = new File(Constants.downloadpath);
        if (!filesDir.exists()) {
            filesDir.mkdirs();
        }
        final String filepath = Constants.downloadpath + "/" + System.currentTimeMillis() + AppUtils.getImageName(url);
        RequestParams requestParams = new RequestParams(url);
        requestParams.setConnectTimeout(5000);
        x.http().get(requestParams, new MyCacheCallBack<File>() {
                    @Override
                    public boolean onCache(File result) {

                        FileUtil.copy(result.getAbsolutePath(), filepath);

                        return true;
                    }

                    @Override
                    public void onSuccess(File result) {

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(DragImageActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }
}

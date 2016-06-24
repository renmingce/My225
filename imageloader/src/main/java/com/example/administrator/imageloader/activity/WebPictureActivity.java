package com.example.administrator.imageloader.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.imageloader.R;
import com.example.administrator.imageloader.adapter.ImageAdapter;
import com.example.administrator.imageloader.bean.HistoryUrl;
import com.example.administrator.imageloader.bean.ImageBean;
import com.example.administrator.imageloader.dao.HistoryDAO;
import com.example.administrator.imageloader.listenner.MyCacheCallBack;
import com.example.administrator.imageloader.util.AppUtils;
import com.example.administrator.imageloader.util.Constants;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.common.Callback;
import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WebPictureActivity extends Activity implements AdapterView.OnItemLongClickListener, AdapterView.OnItemClickListener, CompoundButton.OnCheckedChangeListener {

    private TextView tv_pictures_info;//显示文本信息
    private CheckBox cb_pictures_select; //勾选图片
    private ImageView iv_pictures_download; //下载或删除图片
    private Button btn_pictures_stop; //停止抓取图片
    private ProgressBar pb_pictures_loading; //深度抓取进度
    private GridView gv_pictures_pic;

    private ImageAdapter adapter;

    private HistoryDAO historyDAO;
    private List<HistoryUrl> historyUrls;

    private List<ImageBean> imageBeans = new ArrayList<ImageBean>();

    private HashSet<ImageBean> set = new HashSet<ImageBean>();
    private SearchView.OnQueryTextListener listener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String url) {

            url = checkUrlPre(url);
            getHttpImages(url);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );
        setContentView(R.layout.activity_web_picture);

        init();

        Constants.state = Constants.s_web;
    }

    private void init() {

        tv_pictures_info = (TextView) findViewById(R.id.tv_pictures_info);
        cb_pictures_select = (CheckBox) findViewById(R.id.cb_pictures_select);
        iv_pictures_download = (ImageView) findViewById(R.id.iv_pictures_download);
        btn_pictures_stop = (Button) findViewById(R.id.btn_pictures_stop);
        pb_pictures_loading = (ProgressBar) findViewById(R.id.pb_pictures_loading);
        gv_pictures_pic = (GridView) findViewById(R.id.gv_pictures_pics);


        adapter = new ImageAdapter(this);

        gv_pictures_pic.setAdapter(adapter);


        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        url = checkUrlPre(url);

        historyDAO = new HistoryDAO();
        historyUrls = historyDAO.getAll();


        getHttpImages(url);

        gv_pictures_pic.setOnItemLongClickListener(this);

        gv_pictures_pic.setOnItemClickListener(this);

        cb_pictures_select.setOnCheckedChangeListener(this);

        getActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private ProgressDialog dialog;

    private void getHttpImages(final String url) {

        addToHistory(url);

        isEdit = false;
        selectcount = 0;
        cb_pictures_select.setVisibility(View.GONE);
        iv_pictures_download.setVisibility(View.GONE);
        tv_pictures_info.setText("请输入网址");

        this.url = url;
        showProgressDialog("正在抓取" + url + "网站的图片", false);

        x.http().get(new RequestParams(url), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String html) {

                imageBeans.clear();
                set.clear();
                showImageFromHtml(url, html);

                dialog.dismiss();

                showDeepSearchDialog(url, html);
            }


            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Toast.makeText(WebPictureActivity.this, "数据请求失败", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void addToHistory(String url) {
        HistoryUrl historyUrl = new HistoryUrl(1, url);
        if (!historyUrls.contains(historyUrl)) {

            historyDAO.add(historyUrl);

            historyUrls.add(historyUrl);
        }
    }

    private void showDeepSearchDialog(final String url, final String html) {
        new AlertDialog.Builder(this)
                .setTitle("请确认")
                .setMessage(url + "首页数据抓取完毕，是否进行深度抓取")
                .setPositiveButton("深度抓取", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deepSearch(url, html);
                    }
                })
                .setNegativeButton("下回吧", null)
                .show();
    }

    private void deepSearch(String url, String html) {
        pb_pictures_loading.setVisibility(View.VISIBLE);
        btn_pictures_stop.setVisibility(View.VISIBLE);

        Document doc = Jsoup.parse(html);// 解析HTML页面

        // 获取页面中的所有连接
        Elements links = doc.select("a[href]");
        List<String> useLinks = getUseableLinks(links);// 过滤


        pb_pictures_loading.setMax(useLinks.size());
        for (int i = 0; i < useLinks.size(); i++) {
            final String uselink = useLinks.get(i);

            x.http().get(new RequestParams(uselink), new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String html) {
                    if (stop) {
                        return;
                    }
                    showImageFromHtml(uselink, html);
                    pb_pictures_loading.incrementProgressBy(1);
                    tv_pictures_info.setText("抓取到" + imageBeans.size() + "张图片");

                    if (pb_pictures_loading.getProgress() == pb_pictures_loading.getMax()) {
                        pb_pictures_loading.setVisibility(View.GONE);
                        btn_pictures_stop.setVisibility(View.GONE);
                        tv_pictures_info.setText("抓取完毕，共抓取到" + imageBeans.size() + "张图片");
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {

                    if (stop) {
                        return;
                    }
                    pb_pictures_loading.incrementProgressBy(1);


                    if (pb_pictures_loading.getProgress() == pb_pictures_loading.getMax()) {
                        pb_pictures_loading.setVisibility(View.GONE);
                        btn_pictures_stop.setVisibility(View.GONE);
                        tv_pictures_info.setText("部分抓取失败，共抓取到" + imageBeans.size() + "张图片");
                    }

                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }
            });
        }
    }

    private boolean stop = false;

    public void stopSearch(View v) {
        stop = true;
        pb_pictures_loading.setVisibility(View.GONE);
        btn_pictures_stop.setVisibility(View.GONE);
        tv_pictures_info.setText("停止抓取，共抓取到" + imageBeans.size() + "张图片");

    }

    private List<String> getUseableLinks(Elements links) {
        //用于过滤重复url的集合
        HashSet<String> set1 = new HashSet<String>();
        //用于保存有效url的集合
        List<String> lstLinks = new ArrayList<String>();

        //遍历所有links,过滤,保存有效链接
        for (Element link : links) {
            String href = link.attr("href");// abs:href, "http://"
            //Log.i("spl","过滤前,链接:"+href);
            // 设置过滤条件
            if (href.equals("")) {
                continue;// 跳过
            }
            if (href.equals(url)) {
                continue;// 跳过
            }
            if (href.startsWith("javascript")) {
                continue;// 跳过
            }

            if (href.startsWith("/")) {
                href = url + href;
            }
            if (!set1.contains(href)) {
                set1.add(href);// 将有效链接保存至哈希表中
                lstLinks.add(href);
            }

            Log.i("spl", "有效链接:" + href);
        }
        return lstLinks;
    }

    private void showImageFromHtml(String url, String html) {
        List<ImageBean> list = parseHtml(url, html);

        imageBeans.addAll(list);
        adapter.setList(imageBeans);
        adapter.notifyDataSetChanged();
    }

    private List<ImageBean> parseHtml(String url, String html) {
        List<ImageBean> list = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        List<Element> imgs = doc.getElementsByTag("img");
        for (Element img : imgs) {
            String src = img.attr("src");
            if (src.toLowerCase().endsWith("jpg") || src.toLowerCase().endsWith("png")) {
                src = checkSrc(url, src);
                ImageBean imageBean = new ImageBean(src);
                if (!set.contains(imageBean) && src.indexOf("/../") == -1) {
                    set.add(imageBean);
                    list.add(imageBean);
                }
            }
        }
        return list;


    }

    private String checkSrc(String url, String src) {
        if (src.startsWith("http")) {
            url = src;
        } else {
            if (src.startsWith("/")) {
                url = url + src;
            } else {
                url = url + "/" + src;
            }
        }
        return url;
    }


    public void showProgressDialog(String msg, boolean isHorizontal) {

        dialog = new ProgressDialog(this);
        if (isHorizontal) {
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        } else {
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        dialog.setTitle("提示信息");
        dialog.setMessage(msg);
        dialog.show();
    }

    private String checkUrlPre(String url) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        return url;
    }

    public static boolean isEdit;

    private String url;

    private int selectcount = 0;

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (!isEdit) {
            isEdit = true;

            cb_pictures_select.setVisibility(View.VISIBLE);
            iv_pictures_download.setVisibility(View.VISIBLE);

        }

        boolean isChecked = adapter.getImageCheckedStatus(position);
        selectcount = (isChecked) ? selectcount - 1 : selectcount + 1;

        tv_pictures_info.setText(selectcount + "/" + imageBeans.size());

        adapter.changeImageCheckedStaus(position, !isChecked);
        adapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (!isEdit) {
            Intent intent = new Intent(this, DragImageActivity.class);

            intent.putExtra("position", position);
            intent.putExtra("imagebeans", (ArrayList) imageBeans);
            startActivity(intent);
        }
        boolean isChecked = adapter.getImageCheckedStatus(position);
        selectcount = (isChecked) ? selectcount - 1 : selectcount + 1;

        tv_pictures_info.setText(selectcount + "/" + imageBeans.size());

        adapter.changeImageCheckedStaus(position, !isChecked);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        adapter.changAllImagesStatus(isChecked);

        if (isChecked) {
            selectcount = imageBeans.size();
        } else {
            selectcount = 0;
        }
        tv_pictures_info.setText(selectcount + "/" + imageBeans.size());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isEdit = false;
    }

    private SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu, menu);

        MenuItem item = menu.findItem(R.id.item_menu_search);
        searchView = (SearchView) item.getActionView();

        searchView.setQueryHint("请输入网址");
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(listener);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.item_menu_hos:
                showHistory();
                break;
            case R.id.item_menu_local:
                showLocalDownloadImages();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLocalDownloadImages() {

        Constants.state = Constants.s_local;

        selectcount = 0;
        iv_pictures_download.setImageResource(R.drawable.op_del_press);
        iv_pictures_download.setVisibility(View.GONE);
        cb_pictures_select.setChecked(false);
        isEdit = false;

        imageBeans = getLocalImages(Constants.downloadpath);
        adapter.setList(imageBeans);
        tv_pictures_info.setText("本地一共搜索到" + imageBeans.size() + "张图片");
    }

    private List<ImageBean> getLocalImages(String downloadpath) {

        List<ImageBean> list = new ArrayList<ImageBean>();

        File file = new File(downloadpath);
        File[] files = file.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                ImageBean imageBean = new ImageBean(files[i].getAbsolutePath());
                list.add(imageBean);
            }
        }

        return list;
    }

    private void showHistory() {

        final String[] items = new String[historyUrls.size()];

        for (int i = 0; i < historyUrls.size(); i++) {
            items[i] = historyUrls.get(i).getUrl();
        }

        new AlertDialog.Builder(this)
                .setTitle("历史浏览网站")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        getHttpImages(items[which]);
                    }
                })

                .show();
    }

    public void download(View v) {
        if (Constants.state == Constants.s_web) {
            showProgressDialog("正在下载中...", true);
            dialog.setTitle("下载");
            dialog.setMax(selectcount);

            for (int i = 0; i < imageBeans.size(); i++) {
                ImageBean imageBean = imageBeans.get(i);
                if (imageBean.isChecked()) {
                    downloadImage(imageBean.getUrl());

                    dialog.incrementProgressBy(1);
                    SystemClock.sleep(1000);
                    if (dialog.getProgress() == dialog.getMax()) {
                        dialog.dismiss();
                    }
                }
            }
            Toast.makeText(WebPictureActivity.this, "下载结束", Toast.LENGTH_SHORT).show();

            tv_pictures_info.setText("请在输入框输入网址搜索");
            selectcount = 0;
            isEdit = false;
            cb_pictures_select.setChecked(false);
            iv_pictures_download.setVisibility(View.GONE);
            cb_pictures_select.setVisibility(View.GONE);
            adapter.changAllImagesStatus(false);
        } else if (Constants.state == Constants.s_local) {
            for (int i = 0; i < imageBeans.size(); i++) {
                ImageBean imageBean = imageBeans.get(i);
                if (imageBean.isChecked()) {

                    File file = new File(imageBean.getUrl());
                    if (file.exists()) {
                        file.delete();
                    }

                    imageBeans.remove(i);
                    i--;
                }
            }
            adapter.notifyDataSetChanged();
            Toast.makeText(WebPictureActivity.this, "删除完毕", Toast.LENGTH_SHORT).show();
            tv_pictures_info.setText("本地共有" + imageBeans.size() + "张图片");
            selectcount = 0;
            isEdit = false;
            cb_pictures_select.setChecked(false);
            iv_pictures_download.setVisibility(View.GONE);
            cb_pictures_select.setVisibility(View.GONE);
        }

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
                        Toast.makeText(WebPictureActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
                    }
                }
        );


    }

    @Override
    public void onBackPressed() {
      if(!isEdit) {
          finish();
      }else{
          isEdit=false;
          selectcount=0;
          tv_pictures_info.setText("请在输入框输入网址搜索");
          cb_pictures_select.setChecked(false);
          iv_pictures_download.setVisibility(View.GONE);
          cb_pictures_select.setVisibility(View.GONE);
          adapter.changAllImagesStatus(false);
      }

    }
}

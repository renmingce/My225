package com.example.administrator.imageloader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.administrator.imageloader.R;
import com.example.administrator.imageloader.activity.WebPictureActivity;
import com.example.administrator.imageloader.bean.ImageBean;
import com.example.administrator.imageloader.util.AppUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by Administrator on 2016/6/21.
 */
public class ImageAdapter extends BaseAdapter {


    private Context context;
    private List <ImageBean> list;

    public ImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return (list==null)?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return (list==null)?null:list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView = View.inflate(context, R.layout.item_pictures, null);
            holder=new ViewHolder();
            holder.iv_item_pic= (ImageView) convertView.findViewById(R.id.iv_item_pic);
            holder.iv_item_checked= (ImageView) convertView.findViewById(R.id.iv_item_checked);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageBean imageBean = list.get(position);

        x.image().bind(holder.iv_item_pic,imageBean.getUrl(), AppUtils.smallImageOptions);

        if(WebPictureActivity.isEdit){
            holder.iv_item_checked.setVisibility(View.VISIBLE);
            if(imageBean.isChecked()){
                holder.iv_item_checked.setImageResource(R.drawable.blue_selected);
            } else {
                holder.iv_item_checked.setImageResource(R.drawable.blue_unselected);
            }
        } else {
                holder.iv_item_checked.setVisibility(View.GONE);
        }
        return convertView;
    }

    public void setList(List<ImageBean> list) {
        this.list = list;
    }

    public boolean getImageCheckedStatus(int position) {
        return  list.get(position).isChecked();
    }

    public void changeImageCheckedStaus(int position, boolean isChecked) {
        list.get(position).setChecked(isChecked);
    }

    public void changAllImagesStatus(boolean isChecked) {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setChecked(isChecked);
        }
        this.notifyDataSetChanged();
    }

    static  class ViewHolder{
    private ImageView iv_item_pic;
        private ImageView iv_item_checked;
    }
}

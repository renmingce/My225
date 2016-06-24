package com.example.administrator.imageloader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.imageloader.R;
import com.example.administrator.imageloader.bean.WebLink;

import java.util.List;

/**
 * Created by Administrator on 2016/6/21.
 */
public class MainAdapter extends BaseAdapter {

    private Context context;
    private List<WebLink> list;

    public MainAdapter(Context context, List<WebLink> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

       ViewHolder holder;
        if(convertView==null){
          convertView = View.inflate(context, R.layout.item_main, null);

          holder=new ViewHolder();
           holder.iv_item_icon = (ImageView) convertView.findViewById(R.id.iv_item_icon);
            holder.tv_wangzhan = (TextView) convertView.findViewById(R.id.tv_wangzhan);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }

        WebLink webLink=list.get(position);

        holder.iv_item_icon.setImageResource(webLink.getIcon());
        holder.tv_wangzhan.setText(webLink.getName());


        return convertView;
    }

   static  class ViewHolder{
       private ImageView iv_item_icon;
       private TextView  tv_wangzhan;
    }
}

package com.example.administrator.imageloader.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.administrator.imageloader.R;
import com.example.administrator.imageloader.util.AppUtils;

import org.xutils.x;

/**
 * Created by Administrator on 2016/6/23.
 */
public class ImageFragment extends Fragment {


    private  String imagepath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ImageView view = (ImageView) View.inflate(getActivity(), R.layout.item_tdrag_image, null);

        x.image().bind(view,imagepath, AppUtils.bigImageOptions);


        return view;
    }

    public static Fragment getInstance(String imagepath) {
        ImageFragment fragment= new ImageFragment();
        fragment.imagepath=imagepath;
        return  fragment;
    }
}

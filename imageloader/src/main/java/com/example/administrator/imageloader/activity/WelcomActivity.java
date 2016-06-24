package com.example.administrator.imageloader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.example.administrator.imageloader.R;

public class WelcomActivity extends Activity {

    private RelativeLayout rl_welcome;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case  1:
              Intent intent= new Intent(WelcomActivity.this,MainActivity.class);

                    startActivity(intent);
                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcom);

        rl_welcome = (RelativeLayout)findViewById(R.id.rl_welcome);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(2000);
        rl_welcome.startAnimation(alphaAnimation);


        handler.sendEmptyMessageDelayed(1, 2000);

       /* alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(WelcomActivity.this,MainActivity.class);

                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}

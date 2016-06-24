package com.example.administrator.quickindex;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Administrator on 2016/6/24.
 */
public class QuickIndex extends View {

      private Paint paint;
    private String[] arr = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};


    private  float viewwidth,viewheight,itemwidth,itemheight;

    public QuickIndex(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint=new Paint();

        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setTextSize(10);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        viewwidth=itemwidth=this.getMeasuredWidth();
        viewheight=this.getMeasuredHeight();
        itemheight=viewheight/26;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (int i = 0; i < arr.length; i++) {

            Rect bounds=new Rect();



            paint.getTextBounds(arr[i],0,arr[i].length(),bounds);


            float x=itemwidth/2-bounds.width()/2;
            float y =itemheight/2+bounds.height()/2+i*itemheight;

            canvas.drawText(arr[i], x, y, paint);
        }



    }
}

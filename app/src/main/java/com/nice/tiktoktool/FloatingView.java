package com.nice.tiktoktool;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;

import android.widget.Toast;
import com.nice.config.Config;
import com.nice.service.TikTokAccessibilityService;

/**
 * 悬浮窗view
 */
public class FloatingView extends FrameLayout implements View.OnTouchListener, View.OnClickListener {
    private Context mContext;
    private View mView;
    private Button beginBtn;
    private int mTouchStartX, mTouchStartY;//手指按下时坐标
    private WindowManager.LayoutParams mParams;
    private FloatingManager mWindowManager;

    public FloatingView(Context context) {
        super(context);
        mContext = context.getApplicationContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        mView = mLayoutInflater.inflate(R.layout.float_static, null);
        beginBtn = mView.findViewById(R.id.begin_btn);
        mWindowManager = FloatingManager.getInstance(mContext);
        beginBtn.setOnClickListener(this);

        mView.setOnTouchListener(this);
    }

    public void show() {
        mParams = new WindowManager.LayoutParams();
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = 0;
        mParams.y = 100;
        //总是出现在应用程序窗口之上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//6.0
            mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            mParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //设置图片格式，效果为背景透明
        mParams.format = PixelFormat.RGBA_8888;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        mParams.format = PixelFormat.TRANSPARENT;
        mParams.width = LayoutParams.WRAP_CONTENT;
        mParams.height = LayoutParams.WRAP_CONTENT;
        mWindowManager.addView(mView, mParams);
    }

    public void hide() {
        mWindowManager.removeView(mView);
    }


    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartX = (int) event.getRawX();
                mTouchStartY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mParams.x += (int) event.getRawX() - mTouchStartX;
                mParams.y += (int) event.getRawY() - mTouchStartY;//相对于屏幕左上角的位置
                mWindowManager.updateView(mView, mParams);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.begin_btn) {
            if (Config.getInstance(getContext()).getStatus()) {
                Config.getInstance(getContext()).setStatus(false);
                beginBtn.setText("开始执行");
                TikTokAccessibilityService.attentionLetter = false;
                Toast.makeText(getContext(), "停止执行...", Toast.LENGTH_SHORT).show();
            } else {
                Config.getInstance(getContext()).setStatus(true);
                beginBtn.setText("停止执行");
                Toast.makeText(getContext(), "开始执行...", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
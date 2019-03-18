package com.junipersys.a3_chambertest;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;

public class InterceptTouchFrameLayout extends FrameLayout {
    private boolean mDisallowIntercept = true;
    public static final String FILE_NAME = "Touch_Log.txt";
    public Context mContext;

    //--------------------------------------
    // Constructors
    //--------------------------------------

    public InterceptTouchFrameLayout( Context context) {
        super(context);
        mContext = context;
    }

    public InterceptTouchFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptTouchFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }



    private static final OnInterceptTouchEventListener TOUCH_LISTENER = new InterceptTouchEventListener();

    private OnInterceptTouchEventListener mInterceptTouchEventListener = TOUCH_LISTENER;

    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        getParent().requestDisallowInterceptTouchEvent(disallowIntercept);
        mDisallowIntercept = disallowIntercept;
    }

    public void setOnInterceptTouchEventListener(OnInterceptTouchEventListener interceptTouchEventListener) {
        mInterceptTouchEventListener = interceptTouchEventListener != null ? interceptTouchEventListener : TOUCH_LISTENER;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean stealTouchEvent = mInterceptTouchEventListener.onInterceptTouchEvent(this, ev, mDisallowIntercept);
        return stealTouchEvent && !mDisallowIntercept || super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled = mInterceptTouchEventListener.onTouchEvent(this, event);

        //Log touches to file
        View v = ((MainActivity)getContext()).findViewById(R.id.touch_layout);
        try {
            logTouches(v, event.getX(), event.getY());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return handled;
    }

    //--------------------------------------
    // Custom Listeners
    //--------------------------------------

    public interface OnInterceptTouchEventListener {
        //If disallowIntercept is true the touch event can't be stealed and the return value is ignored.
        boolean onInterceptTouchEvent(InterceptTouchFrameLayout view, MotionEvent ev, boolean disallowIntercept);

        //@see android.view.View#onTouchEvent(android.view.MotionEvent)
        boolean onTouchEvent(InterceptTouchFrameLayout view, MotionEvent event);
    }

    private static final class InterceptTouchEventListener implements OnInterceptTouchEventListener {
        @Override
        public boolean onInterceptTouchEvent(InterceptTouchFrameLayout view, MotionEvent ev, boolean disallowIntercept) {
            return true;
        }
        @Override
        public boolean onTouchEvent(InterceptTouchFrameLayout view, MotionEvent event) {
            return true;
        }
    }


    public void logTouches(View v, float X, float Y) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");

        formatter.setTimeZone(TimeZone.getTimeZone("America/Denver"));
        Calendar cal = Calendar.getInstance();
        Date dateTime = cal.getTime();

        String text = "Touch Detected: (X:" + X + ", Y:" + Y + ") at " + formatter.format(dateTime);
        FileOutputStream fos;

        fos = mContext.openFileOutput(FILE_NAME, MODE_PRIVATE);
        fos.write(text.getBytes());

        View contextView = findViewById(R.id.context_view);

        Snackbar sb = Snackbar.make(contextView, R.string.touch_logged, Snackbar.LENGTH_INDEFINITE);
        sb.setAction(R.string.goToLogFile, new GoToLogFileListener());
        //Toast.makeText(mContext, "Saved to " + mContext.getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_SHORT).show();
        fos.close();
    }
}
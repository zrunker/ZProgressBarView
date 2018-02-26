package cc.ibooker.zprogressbar;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.WeakReference;

import cc.ibooker.progressbarlib.HorizontalProgressWithProgress;

/**
 * 测试带进度的水平进度条
 * <p>
 * Created by 邹峰立 on 2018/2/26 0026.
 */
public class ThreeActivity extends AppCompatActivity {
    private static final int MSG_UPDATE_PROGRESS = 1;
    private HorizontalProgressWithProgress mHProgress;
    private int mPragress = 70;// 水平进度条最终的进度

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_three);

        mHProgress = findViewById(R.id.horizontal_progress);

        setHProgress();
    }

    // 设置进度条的进度
    private void setHProgress() {
        mHProgress.setProgress(0);
        mProgressHander.sendEmptyMessage(MSG_UPDATE_PROGRESS);
    }

    // 修改进度条Hander
    ProgressHander mProgressHander = new ProgressHander(this);

    static class ProgressHander extends Handler {
        // 定义一个对象用来引用Activity中的方法
        private final WeakReference<Activity> mActivity;

        public ProgressHander(Activity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            ThreeActivity currentActivity = (ThreeActivity) mActivity.get();
            int progress = currentActivity.mHProgress.getProgress();
            if (progress >= currentActivity.mPragress) {
                currentActivity.mProgressHander.removeMessages(MSG_UPDATE_PROGRESS);
            } else {
                currentActivity.mHProgress.setProgress(++progress);
                currentActivity.mProgressHander.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, 100);
            }
        }
    }
}

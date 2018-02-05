package cc.ibooker.progressbarlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * 自定义进度条
 *
 * @author 邹峰立
 */
public class SleepProgressBar extends ProgressBar {
    /**
     * 画笔对象
     */
    private Paint paint;
    /**
     * 当前进度
     */
    private int progress;
    /**
     * 最大进度
     */
    private int max;
    /**
     * 图片
     */
    private Bitmap bitmap;

    private ArrayList<Bitmap> bitmapList;

    public SleepProgressBar(Context context) {
        this(context, null);
    }

    public SleepProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SleepProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (bitmapList == null)
            bitmapList = new ArrayList<>();
        bitmapList.clear();
        bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.dropdown_anim_01));
        bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.dropdown_anim_02));
        bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.dropdown_anim_03));
        bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.dropdown_anim_04));
        bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.dropdown_anim_05));
        bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.dropdown_anim_06));
        bitmapList.add(BitmapFactory.decodeResource(getResources(), R.drawable.dropdown_anim_07));

        bitmap = bitmapList.get(0);
        paint = new Paint();
    }

    public synchronized int getMax() {
        return max;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画图片
        int company = max / bitmapList.size();
        if (progress <= company) {
            bitmap = bitmapList.get(0);
        } else if (progress > company && progress <= company * 2) {
            bitmap = bitmapList.get(1);
        } else if (progress > company * 2 && progress <= company * 3) {
            bitmap = bitmapList.get(2);
        } else if (progress > company * 3 && progress <= company * 4) {
            bitmap = bitmapList.get(3);
        } else if (progress > company * 4 && progress <= company * 5) {
            bitmap = bitmapList.get(4);
        } else if (progress > company * 5 && progress <= company * 6) {
            bitmap = bitmapList.get(5);
        } else if (progress > company * 6 && progress <= company * 7) {
            bitmap = bitmapList.get(6);
        }
        canvas.drawBitmap(bitmap, getWidth() / 2 - bitmap.getWidth() / 2, 0, paint);
    }

    /**
     * 设置控件大小
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(bitmap.getWidth(), bitmap.getHeight());
    }

    /**
     * 设置进度条的最大值
     *
     * @param max 最大进度值
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度线程同步
     *
     * @return 当前进度
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，线程同步 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress 进度
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }

}

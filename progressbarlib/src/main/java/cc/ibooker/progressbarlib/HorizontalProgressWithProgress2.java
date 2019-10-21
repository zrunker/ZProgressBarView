package cc.ibooker.progressbarlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * 自定义水平进度条
 * Created by 邹峰立 on 2016/9/21.
 */
public class HorizontalProgressWithProgress2 extends ProgressBar {
    private static final int DEFAULT_TEXT_SIZE = 13;//sp
    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_COLOR_UNREACH = 0xFFF5F6F9;
    private static final int DEFAULT_HEIGHT_UNREACH = 8;//dp
    private static final int DEFAULT_COLOR_REACH = 0xFF57CAC6;
    private static final int DEFAULT_HEIGHT_REACH = 8;//dp
    private static final int DEFAULT_TEXT_OFFSET = 7;//dp
    private static final int DEFAULT_TEXT_BG_COLOR = 0xFF57CAC6;
    private static final int DEFAULT_TEXT_BG_RADIUS = 11;// dp

    private int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mUnReachColor = DEFAULT_COLOR_UNREACH;
    private int mUnReachHeight = dp2px(DEFAULT_HEIGHT_UNREACH);
    private int mReachColor = DEFAULT_COLOR_REACH;
    private int mReachHeight = dp2px(DEFAULT_HEIGHT_REACH);
    private int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);
    private int mTextBgColor = DEFAULT_TEXT_BG_COLOR;
    private int mTextBgRadius = dp2px(DEFAULT_TEXT_BG_RADIUS);
    private boolean isRound = true;

    private Paint mTextBgPaint = new Paint();
    private RectF mReachRectF, mUnReachRectF, outerRect;
    private Paint mPaint = new Paint();
    private int mReachWidth;

    public HorizontalProgressWithProgress2(Context context) {
        this(context, null);
    }

    public HorizontalProgressWithProgress2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressWithProgress2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttrs(attrs);
    }

    /**
     * 获取自定义属性
     *
     * @param attrs 自定义属性文件
     */
    private void obtainStyledAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressWithProgress2);
        if (ta != null) {
            mTextSize = (int) ta.getDimension(R.styleable.HorizontalProgressWithProgress2_hprogress_text_size, mTextSize);
            mTextColor = ta.getColor(R.styleable.HorizontalProgressWithProgress2_hprogress_text_color, mTextColor);
            mUnReachColor = ta.getColor(R.styleable.HorizontalProgressWithProgress2_hprogress_unreach_color, mUnReachColor);
            mUnReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressWithProgress2_hprogress_unreach_height, mUnReachHeight);
            mReachColor = ta.getColor(R.styleable.HorizontalProgressWithProgress2_hprogress_reach_color, mReachColor);
            mReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressWithProgress2_hprogress_reach_height, mReachHeight);
            mTextOffset = (int) ta.getDimension(R.styleable.HorizontalProgressWithProgress2_hprogress_text_offset, mTextOffset);
            mTextBgColor = ta.getColor(R.styleable.HorizontalProgressWithProgress2_hprogress_text_bg_color, DEFAULT_TEXT_BG_COLOR);
            mTextBgRadius = (int) ta.getDimension(R.styleable.HorizontalProgressWithProgress2_hprogress_text_bg_radius, mTextBgRadius);
            isRound = ta.getBoolean(R.styleable.HorizontalProgressWithProgress2_hprogress_isround, true);
            ta.recycle();
        }
        mPaint.setTextSize(mTextSize);
    }

    /**
     * 控件的测量
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        setMeasuredDimension(widthVal, height);

        mReachWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     * 重绘
     *
     * @param canvas 画布
     */
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(getPaddingLeft(), getHeight() / 2);// 移动坐标

        mPaint.setAntiAlias(true);
        mTextBgPaint.setAntiAlias(true);

        boolean noNeedUnReac = false;
        String text = getProgress() + "/" + getMax();
        int textWidth = (int) mPaint.measureText(text);
        float radio = getProgress() * 1.0f / getMax();
        float progressX = radio * mReachWidth;
        if (progressX + textWidth > mReachWidth) {
            progressX = mReachWidth - textWidth;
            noNeedUnReac = true;
        }

        float endX = progressX - mTextOffset / 2;
        if (endX > 0) {
            mPaint.setColor(mReachColor);
            if (!isRound) {
                mPaint.setStrokeWidth(mReachHeight);
                canvas.drawLine(0, 0, endX, 0, mPaint);
            } else {
                if (mReachRectF == null)
                    mReachRectF = new RectF(0, -mReachHeight / 2, endX, mReachHeight / 2);
                canvas.drawRoundRect(mReachRectF, mReachHeight, mReachHeight, mPaint);
            }
        }

        // draw unreal bar
        if (!noNeedUnReac) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnReachColor);
            if (!isRound) {
                mPaint.setStrokeWidth(mUnReachHeight);
                canvas.drawLine(start, 0, mReachWidth, 0, mPaint);
            } else {
                if (mUnReachRectF == null)
                    mUnReachRectF = new RectF(start, -mUnReachHeight / 2, mReachWidth, mUnReachHeight / 2);
                canvas.drawRoundRect(mUnReachRectF, mUnReachHeight, mUnReachHeight, mPaint);
            }
        }

        // draw text
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);

        // draw text bg
        if (outerRect == null)
            outerRect = new RectF(progressX - mTextOffset, -mTextSize / 2 - 8, progressX + textWidth + mTextOffset, mTextSize / 2 + 8);
        mTextBgPaint.setColor(mTextBgColor);
        canvas.drawRoundRect(outerRect, mTextBgRadius, mTextBgRadius, mTextBgPaint);

        // draw text
        mPaint.setColor(mTextColor);
        canvas.drawText(text, progressX, y, mPaint);

        canvas.restore();
    }

    /**
     * 测量高度
     *
     * @param heightMeasureSpec 高度测量模式
     */
    private int measureHeight(int heightMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {// 如果是精确值
            result = size;
        } else {// 如果是其他情况
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            result = getPaddingBottom() + getPaddingTop() + Math.max(Math.max(mReachHeight, mUnReachHeight), Math.abs(textHeight));

            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(size, result);
            }
        }
        return result;
    }

    // dp->px
    private int dp2px(int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, getResources().getDisplayMetrics());
    }

    // sp->px
    private int sp2px(int spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, getResources().getDisplayMetrics());
    }
}

package cc.ibooker.progressbarlib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * 自定义水平进度条
 * Created by 邹峰立 on 2016/9/21.
 */
public class HorizontalProgressWithProgress extends ProgressBar {
    private static final int DEFAULT_TEXT_SIZE = 18;//sp
    private static final int DEFAULT_TEXT_COLOR = 0xFFFE7517;
    private static final int DEFAULT_COLOR_UNREACH = 0xFFD3D6DA;
    private static final int DEFAULT_HEIGHT_UNREACH = 2;//dp
    private static final int DEFAULT_COLOR_REACH = DEFAULT_TEXT_COLOR;
    private static final int DEFAULT_HEIGHT_REACH = 2;//dp
    private static final int DEFAULT_TEXT_OFFSET = 10;//dp

    private int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mUnReachColor = DEFAULT_COLOR_UNREACH;
    private int mUnReachHeight = dp2px(DEFAULT_HEIGHT_UNREACH);
    private int mReachColor = DEFAULT_COLOR_REACH;
    private int mReachHeight = dp2px(DEFAULT_HEIGHT_REACH);
    private int mTextOffset = dp2px(DEFAULT_TEXT_OFFSET);
    private String mTextSuffix = "%";
    private String mTextPrefix;
    private String mText;

    private Paint mPaint = new Paint();
    private int mReachWidth;

    public HorizontalProgressWithProgress(Context context) {
        this(context, null);
    }

    public HorizontalProgressWithProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressWithProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        obtainStyledAttrs(attrs);
    }

    /**
     * 获取自定义属性
     *
     * @param attrs 自定义属性文件
     */
    private void obtainStyledAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.HorizontalProgressWithProgress);
        mTextSize = (int) ta.getDimension(R.styleable.HorizontalProgressWithProgress_progress_text_size, mTextSize);
        mTextColor = ta.getColor(R.styleable.HorizontalProgressWithProgress_progress_text_color, mTextColor);
        mUnReachColor = ta.getColor(R.styleable.HorizontalProgressWithProgress_progress_unreach_color, mUnReachColor);
        mUnReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressWithProgress_progress_unreach_height, mUnReachHeight);
        mReachColor = ta.getColor(R.styleable.HorizontalProgressWithProgress_progress_reach_color, mReachColor);
        mReachHeight = (int) ta.getDimension(R.styleable.HorizontalProgressWithProgress_progress_reach_height, mReachHeight);
        mTextOffset = (int) ta.getDimension(R.styleable.HorizontalProgressWithProgress_progress_text_offset, mTextOffset);
        String suffix = ta.getString(R.styleable.HorizontalProgressWithProgress_progress_text_suffix);
        mTextSuffix = suffix == null ? mTextSuffix : suffix;
        mTextPrefix = ta.getString(R.styleable.HorizontalProgressWithProgress_progress_text_prefix);
        mTextPrefix = mTextPrefix == null ? "" : mTextPrefix;
        mText = ta.getString(R.styleable.HorizontalProgressWithProgress_progress_text);
        mText = mText == null ? "" : mText;
        ta.recycle();

        mPaint.setTextSize(mTextSize);
    }

    public int getTextSize() {
        return mTextSize;
    }

    public HorizontalProgressWithProgress setTextSize(int mTextSize) {
        this.mTextSize = mTextSize;
        return this;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public HorizontalProgressWithProgress setTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        return this;
    }

    public int getUnReachColor() {
        return mUnReachColor;
    }

    public HorizontalProgressWithProgress setUnReachColor(int mUnReachColor) {
        this.mUnReachColor = mUnReachColor;
        return this;
    }

    public int getUnReachHeight() {
        return mUnReachHeight;
    }

    public HorizontalProgressWithProgress setUnReachHeight(int mUnReachHeight) {
        this.mUnReachHeight = mUnReachHeight;
        return this;
    }

    public int getReachColor() {
        return mReachColor;
    }

    public HorizontalProgressWithProgress setReachColor(int mReachColor) {
        this.mReachColor = mReachColor;
        return this;
    }

    public int getReachHeight() {
        return mReachHeight;
    }

    public HorizontalProgressWithProgress setReachHeight(int mReachHeight) {
        this.mReachHeight = mReachHeight;
        return this;
    }

    public int getTextOffset() {
        return mTextOffset;
    }

    public HorizontalProgressWithProgress setTextOffset(int mTextOffset) {
        this.mTextOffset = mTextOffset;
        return this;
    }

    public String getTextSuffix() {
        return mTextSuffix;
    }

    public HorizontalProgressWithProgress setTextSuffix(String mTextSuffix) {
        this.mTextSuffix = mTextSuffix;
        return this;
    }

    public String getTextPrefix() {
        return mTextPrefix;
    }

    public HorizontalProgressWithProgress setTextPrefix(String mTextPrefix) {
        this.mTextPrefix = mTextPrefix;
        return this;
    }

    public String getText() {
        return mText;
    }

    public HorizontalProgressWithProgress setText(String mText) {
        this.mText = mText;
        return this;
    }

    public int getReachWidth() {
        return mReachWidth;
    }

    public HorizontalProgressWithProgress setReachWidth(int mReachWidth) {
        this.mReachWidth = mReachWidth;
        return this;
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

        boolean noNeedUnReac = false;
        String text = mText;
        if (TextUtils.isEmpty(text))
            text = mTextPrefix + getProgress() + mTextSuffix;
        else
            text = mTextPrefix + text + mTextSuffix;
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
            mPaint.setStrokeWidth(mReachHeight);
            canvas.drawLine(0, 0, endX, 0, mPaint);
        }

        // draw text
        mPaint.setColor(mTextColor);
        int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);
        canvas.drawText(text, progressX, y, mPaint);

        // draw unreal bar
        if (!noNeedUnReac) {
            float start = progressX + mTextOffset / 2 + textWidth;
            mPaint.setColor(mUnReachColor);
            mPaint.setStrokeWidth(mUnReachHeight);
            canvas.drawLine(start, 0, mReachWidth, 0, mPaint);
        }

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

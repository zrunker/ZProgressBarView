package cc.ibooker.progressbarlib;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.lang.ref.WeakReference;

/**
 * 自定义下拉刷新与加载更多控件 - 只支持ListView
 *
 * @author 邹峰立
 */
public class PullToRefreshAndLoadView extends LinearLayout implements OnTouchListener {
    /**
     * 下拉状态
     */
    public static final int STATUS_PULL_TO_REFRESH = 0;
    /**
     * 释放立即刷新状态
     */
    public static final int STATUS_RELEASE_TO_REFRESH = 1;
    /**
     * 正在刷新状态
     */
    public static final int STATUS_REFRESHING = 2;
    /**
     * 刷新完成或未刷新状态
     */
    public static final int STATUS_REFRESH_FINISHED = 3;
    /**
     * 当前处理什么状态，可选值有STATUS_PULL_TO_REFRESH, STATUS_RELEASE_TO_REFRESH,
     * STATUS_REFRESHING 和 STATUS_REFRESH_FINISHED
     */
    private int currentStatus = STATUS_REFRESH_FINISHED;
    /**
     * 记录上一次的状态是什么，避免进行重复操作
     */
    private int lastStatus = currentStatus;
    /**
     * 下拉头部回滚的速度
     */
    public static final int SCROLL_SPEED = -20;
    /**
     * 下拉头的View
     */
    private View header;
    /**
     * 下拉刷新进度条（自定义）
     */
    private SleepProgressBar progress;
    /**
     * 下拉完成动画图片
     */
    private ImageView loadImg;
    /**
     * 在被判定为滚动之前用户手指可以移动的最大值。
     */
    private int touchSlop;
    /**
     * 是否已加载过一次layout，这里onLayout中的初始化只需加载一次
     */
    private boolean loadOnce;
    /**
     * 下拉头的布局参数
     */
    private MarginLayoutParams headerLayoutParams;
    /**
     * 下拉头的高度
     */
    private int hideHeaderHeight;
    /**
     * 需要去下拉刷新的mListView
     */
    private ListView mListView;
    /**
     * 当前是否可以下拉，只有mListView滚动到头的时候才允许下拉
     */
    private boolean ableToPull;
    /**
     * 手指按下时的屏幕纵坐标
     */
    private float yDown;
    /**
     * 下拉刷新的回调接口
     */
    private PullToRefreshListener mRefreshListener;
    /**
     * 当前是否可以加载更多，只有mListView滚动得到底部的时候才允许
     */
    private boolean ableToLoad;
    /**
     * 加载更多的回调接口
     */
    private PullToLoadListener mLoadListener;
    /**
     * 底部布局
     */
    private View footer;
    /**
     * 布局管理器
     */
    private LayoutInflater inflater;

    public PullToRefreshAndLoadView(Context context) {
        this(context, null);
    }

    public PullToRefreshAndLoadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshAndLoadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        /**
         * LinearLayout相关
         */
        inflater = LayoutInflater.from(context);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();// 触发移动事件的最小距离
        setOrientation(VERTICAL);
        /**
         * 顶部布局的一些属性设置
         */
        header = inflater.inflate(R.layout.layout_header_progress, this, false);
        progress = header.findViewById(R.id.sleep_progressbar);
        loadImg = header.findViewById(R.id.load_img);
        AnimationDrawable aniDrawable = (AnimationDrawable) loadImg.getDrawable();
        aniDrawable.start();
        addView(header, 0);
    }

    /**
     * 进行一些关键性的初始化操作，比如：将下拉头向上偏移进行隐藏，给ListView注册touch事件。
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed && !loadOnce) {
            hideHeaderHeight = -header.getHeight();
            progress.setMax(910);// 设置进度条最大值
            headerLayoutParams = (MarginLayoutParams) header.getLayoutParams();
            headerLayoutParams.topMargin = hideHeaderHeight;

            View view = getChildAt(1);
            if (view != null) {
                view.setOnTouchListener(this);
                if (view instanceof ListView) {
                    mListView = (ListView) view;
                    /**
                     * 底部布局的设置
                     */
                    footer = inflater.inflate(R.layout.layout_footer_progress, mListView, false);
                    footer.setVisibility(View.GONE);
                    mListView.addFooterView(footer);
                }
            }
            loadOnce = true;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // 检查是否可以实现下拉刷新
        setIsAbleToPull(event);
        // 检查是否可以实现加载更多
        setIsAbleToLoad();
        if (ableToPull) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    yDown = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float yMove = event.getRawY();
                    int distance = (int) (yMove - yDown);
                    if (distance >= 0) {
                        progress.setProgress(distance);
                    } else {
                        progress.setProgress(0);
                    }
                    // 如果手指是下滑状态，并且下拉头是完全隐藏的，或者没有达到下拉标准，就屏蔽下拉事件
                    if ((distance <= 0 && headerLayoutParams.topMargin <= hideHeaderHeight)
                            || (distance < touchSlop)) {
                        return false;
                    }
                    if (currentStatus != STATUS_REFRESHING) {
                        if (headerLayoutParams.topMargin > 0) {
                            currentStatus = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            currentStatus = STATUS_PULL_TO_REFRESH;
                        }
                        // 通过偏移下拉头的topMargin值，来实现下拉效果
                        headerLayoutParams.topMargin = (distance / 2) + hideHeaderHeight;
                        header.setLayoutParams(headerLayoutParams);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                default:
                    if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                        // 松手时如果是释放立即刷新状态，就去调用正在刷新的任务
                        new RefreshingTask(this).execute();
                    } else if (currentStatus == STATUS_PULL_TO_REFRESH) {
                        // 松手时如果是下拉状态，就去调用隐藏下拉头的任务
                        new HideHeaderTask(this).execute();
                    }
                    break;
            }
            // 时刻记得更新下拉头中的信息
            if (currentStatus == STATUS_PULL_TO_REFRESH || currentStatus == STATUS_RELEASE_TO_REFRESH) {
                updateHeaderView();
                // 当前正处于下拉或释放状态，要让ListView失去焦点，否则被点击的那一项会一直处于选中状态
                mListView.setPressed(false);
                mListView.setFocusable(false);
                mListView.setFocusableInTouchMode(false);
                lastStatus = currentStatus;
                // 当前正处于下拉或释放状态，通过返回true屏蔽掉ListView的滚动事件
                return true;
            }
        }
        return false;
    }

    /**
     * 更新下拉头中的信息。
     */
    private void updateHeaderView() {
        if (lastStatus != currentStatus) {
            if (currentStatus == STATUS_PULL_TO_REFRESH) {
                progress.setVisibility(VISIBLE);
                loadImg.setVisibility(GONE);
            } else if (currentStatus == STATUS_RELEASE_TO_REFRESH) {
                progress.setVisibility(VISIBLE);
                loadImg.setVisibility(GONE);
            } else if (currentStatus == STATUS_REFRESHING) {
                progress.setVisibility(GONE);
                loadImg.setVisibility(VISIBLE);
            }
        }
    }

    /**
     * 判断是否可以进行下拉刷新
     *
     * @param event MotionEvent事件
     */
    private void setIsAbleToPull(MotionEvent event) {
        if (mListView != null) {
            View firstChild = mListView.getChildAt(0);
            if (firstChild != null) {
                int firstVisiblePosition = mListView.getFirstVisiblePosition(); // 该方法获取当前状态下listView的第一个可见item的position
                int top = firstChild.getTop();
                if (firstVisiblePosition == 0 && top == 0) {// 这时候处于顶部可以进行下拉刷新
                    if (!ableToPull) {
                        yDown = event.getRawY();
                    }
                    // 如果首个元素的上边缘，距离父布局值为0，就说明ListView滚动到了最顶部，此时应该允许下拉刷新
                    ableToPull = true;
                } else {
                    if (headerLayoutParams.topMargin != hideHeaderHeight) {
//                    headerLayoutParams.topMargin = hideHeaderHeight;
//                    header.setLayoutParams(headerLayoutParams);
                        new HideHeaderTask(this).execute();
                    }
                    ableToPull = false;
                }
            } else {
                ableToPull = false;
                // 如果mListView中没有元素,抛出异常
                try {
                    throw new Exception("ListView必须添加一个子布局");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断是否可以进行加载更多
     */
    private void setIsAbleToLoad() {
        if (ableToPull || ableToLoad) {// 判断当前是否正在进行下拉刷新或者加载更多
            return;
        } else {
            int laseVisiblePosition = mListView.getLastVisiblePosition();// 该方法获取当前状态下listView的最后一个可见item的position
            if (laseVisiblePosition == (mListView.getCount() - 1)) {
                View lastChild = mListView.getChildAt(mListView.getLastVisiblePosition() - mListView.getFirstVisiblePosition());
                if (lastChild != null) {
                    ableToLoad = (mListView.getHeight() >= lastChild.getBottom());
                }
            }
        }
        // 执行加载更多
        loadData();
    }

    /**
     * 实现加载更多
     */
    public void loadData() {
        if (ableToLoad) { // 是否可以加载更多
            // 1、显示底部，2、实现接口
            if (mLoadListener != null) {
                setLoading(true);
                mLoadListener.onLoad();
                return;
            }
        }
        setLoading(false);
    }

    /**
     * 设置加载更多
     *
     * @param loading 是否正在加载更多
     */
    public void setLoading(boolean loading) {
        ableToLoad = loading;
        if (footer != null)
            if (ableToLoad) {
                if (footer.getVisibility() == View.GONE)
                    footer.setVisibility(View.VISIBLE);
            } else {
                if (footer.getVisibility() == View.VISIBLE)
                    footer.setVisibility(View.GONE);
            }
    }

    /**
     * 正在刷新的任务，在此任务中会去回调注册进来的下拉刷新监听器。
     *
     * @author 邹峰立
     */
    private static class RefreshingTask extends AsyncTask<Void, Integer, Void> {
        private final WeakReference<PullToRefreshAndLoadView> mPullToRefreshAndLoadView;

        RefreshingTask(PullToRefreshAndLoadView pullToRefreshAndLoadView) {
            mPullToRefreshAndLoadView = new WeakReference<>(pullToRefreshAndLoadView);
        }

        @Override
        protected Void doInBackground(Void... params) {
            PullToRefreshAndLoadView currentView = mPullToRefreshAndLoadView.get();
            // 重置progressBar到达顶部距离 = 0
            int topMargin = currentView.headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= 0) {
                    break;
                }
                publishProgress(topMargin);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // 修改数据，发起下拉刷新事件
            currentView.currentStatus = STATUS_REFRESHING;
            publishProgress(0);
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            PullToRefreshAndLoadView currentView = mPullToRefreshAndLoadView.get();
            currentView.updateHeaderView();
            int marginTop = topMargin[0];
            currentView.headerLayoutParams.topMargin = marginTop;
            currentView.header.setLayoutParams(currentView.headerLayoutParams);
            // 实现下拉刷新接口
            if (marginTop == 0) {
                if (currentView.mRefreshListener != null) {
                    currentView.mRefreshListener.onRefresh();
                }
            }
        }
    }

    /**
     * 隐藏下拉头的任务，当未进行下拉刷新或下拉刷新完成后，此任务将会使下拉头重新隐藏。
     *
     * @author 邹峰立
     */
    private static class HideHeaderTask extends AsyncTask<Void, Integer, Integer> {
        private final WeakReference<PullToRefreshAndLoadView> mPullToRefreshAndLoadView;

        HideHeaderTask(PullToRefreshAndLoadView pullToRefreshAndLoadView) {
            mPullToRefreshAndLoadView = new WeakReference<>(pullToRefreshAndLoadView);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            PullToRefreshAndLoadView currentView = mPullToRefreshAndLoadView.get();
            int topMargin = currentView.headerLayoutParams.topMargin;
            while (true) {
                topMargin = topMargin + SCROLL_SPEED;
                if (topMargin <= currentView.hideHeaderHeight) {
                    topMargin = currentView.hideHeaderHeight;
                    break;
                }
                publishProgress(topMargin);
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return topMargin;
        }

        @Override
        protected void onProgressUpdate(Integer... topMargin) {
            PullToRefreshAndLoadView currentView = mPullToRefreshAndLoadView.get();
            currentView.headerLayoutParams.topMargin = topMargin[0];
            currentView.header.setLayoutParams(currentView.headerLayoutParams);
        }

        @Override
        protected void onPostExecute(Integer topMargin) {
            PullToRefreshAndLoadView currentView = mPullToRefreshAndLoadView.get();
            currentView.headerLayoutParams.topMargin = topMargin;
            currentView.header.setLayoutParams(currentView.headerLayoutParams);
            currentView.currentStatus = STATUS_REFRESH_FINISHED;
        }
    }

    /**
     * 给下拉刷新控件注册一个监听器。
     *
     * @param listener 监听器的实现。
     */
    public void setOnRefreshListener(PullToRefreshListener listener) {
        mRefreshListener = listener;
    }

    /**
     * 当所有的刷新逻辑完成后，记录调用一下，否则你的ListView将一直处于正在刷新状态。
     */
    public void finishRefresh() {
        currentStatus = STATUS_REFRESH_FINISHED;
        new HideHeaderTask(this).execute();
    }

    /**
     * 下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。
     */
    public interface PullToRefreshListener {
        /**
         * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑。注意此方法是在子线程中调用的， 你可以不必另开线程来进行耗时操作。
         */
        void onRefresh();
    }

    /**
     * 给加载更多控件注册一个监听器。
     *
     * @param listener 监听器的实现。
     */
    public void setOnLoadListener(PullToLoadListener listener) {
        mLoadListener = listener;
    }

    /**
     * 当所有的刷新逻辑完成后，记录调用一下，否则你的ListView将一直处于正在刷新状态。
     */
    public void finishLoading() {
        currentStatus = STATUS_REFRESH_FINISHED;
        setLoading(false);
    }

    /**
     * 下拉刷新的监听器，使用下拉刷新的地方应该注册此监听器来获取刷新回调。
     */
    public interface PullToLoadListener {
        /**
         * 刷新时会去回调此方法，在方法内编写具体的刷新逻辑。注意此方法是在子线程中调用的， 你可以不必另开线程来进行耗时操作。
         */
        void onLoad();
    }
}

package cc.ibooker.zprogressbar;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import cc.ibooker.progressbarlib.PullToRefreshLayout;

/**
 * RecycleView下拉刷新控件测试
 * <p>
 * Created by 邹峰立 on 2018/2/5.
 */
public class SecondActivity extends AppCompatActivity implements PullToRefreshLayout.PullToRefreshListener {
    PullToRefreshLayout pullToRefreshLayout;
    RvAdapter adapter;
    ArrayList<String> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        pullToRefreshLayout = findViewById(R.id.pull);
        pullToRefreshLayout.setOnRefreshListener(this);

        final RecyclerView recyclerView = findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list = new ArrayList<>();
                for (int i = 0; i < 30; i++) {
                    list.add("A" + i);
                    list.add("B" + i);
                    list.add("C" + i);
                    list.add("D" + i);
                    list.add("E" + i);
                    list.add("F" + i);
                    list.add("G" + i);
                }
                adapter = new RvAdapter(SecondActivity.this, list);
                recyclerView.setAdapter(adapter);
            }
        }, 2000);
    }

    // 下拉刷新
    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list.clear();
                for (int i = 0; i < 30; i++) {
                    list.add("A" + i);
                    list.add("B" + i);
                    list.add("C" + i);
                    list.add("D" + i);
                    list.add("E" + i);
                    list.add("F" + i);
                    list.add("G" + i);
                }
                adapter.reflashData(list);
                Toast.makeText(SecondActivity.this, "下拉刷新成功", Toast.LENGTH_SHORT).show();
                // 关闭下拉刷新
                pullToRefreshLayout.finishRefresh();
            }
        }, 5000);
    }
}

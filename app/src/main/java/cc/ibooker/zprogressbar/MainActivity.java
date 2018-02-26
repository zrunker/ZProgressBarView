package cc.ibooker.zprogressbar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import cc.ibooker.progressbarlib.PullToRefreshAndLoadView;

/**
 * ListView下拉刷新与加载更多
 *
 * @author 邹峰立
 */
public class MainActivity extends AppCompatActivity implements PullToRefreshAndLoadView.PullToRefreshListener, PullToRefreshAndLoadView.PullToLoadListener {
    PullToRefreshAndLoadView pullToRefreshAndLoadView;
    ArrayList<String> list;
    LvAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Intent intent = new Intent(this, SecondActivity.class);
//        startActivity(intent);

        Intent intent = new Intent(this, ThreeActivity.class);
        startActivity(intent);

        pullToRefreshAndLoadView = findViewById(R.id.pull);
        pullToRefreshAndLoadView.setOnRefreshListener(this);
        pullToRefreshAndLoadView.setOnLoadListener(this);

        final ListView listView = findViewById(R.id.listview);
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
                adapter = new LvAdapter(MainActivity.this, list);
                listView.setAdapter(adapter);
            }
        }, 2000);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "点击", Toast.LENGTH_LONG).show();
            }
        });
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
                Toast.makeText(MainActivity.this, "刷新成功", Toast.LENGTH_LONG).show();
                // 关闭刷新状态
                pullToRefreshAndLoadView.finishRefresh();
            }
        }, 5000);
    }

    // 加载更多
    @Override
    public void onLoad() {
        // 下拉刷新
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                list.add("ABC");
                list.add("ABC");
                list.add("ABC");
                list.add("ABC");
                adapter.reflashData(list);
                Toast.makeText(MainActivity.this, "加载更多成功", Toast.LENGTH_LONG).show();
                // 关闭刷新状态
                pullToRefreshAndLoadView.finishLoading();
            }
        }, 5000);
    }
}

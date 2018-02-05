# ZProgressBar
自定义动画图片下拉刷新进度条，并实现列表下拉刷新和加载更多功能。支持ListView和RecycleView。

引入资源：

1、在build.gradle文件中添加以下代码：
```
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}

dependencies {
	compile 'com.github.zrunker:ZProgressBar:vtest1.0'
}
```
2、在maven文件中添加以下代码：
```
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>

<dependency>
	<groupId>com.github.zrunker</groupId>
	<artifactId>ZProgressBar</artifactId>
	<version>vtest1.0</version>
</dependency>
```

### 使用

#### 一、ListView

```
<cc.ibooker.progressbarlib.PullToRefreshAndLoadView
        android:id="@+id/pull"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    <ListView
            android:id="@+id/listview"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
</cc.ibooker.progressbarlib.PullToRefreshAndLoadView>
```

```
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

        Intent intent = new Intent(this, SecondActivity.class);
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
```

#### 二、RecycleView

```
<cc.ibooker.progressbarlib.PullToRefreshLayout
        android:id="@+id/pull"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

    <android.support.v7.widget.RecyclerView
            android:id="@+id/recycleview"
            android:layout_width="match_parent"
            android:layout_height="wrap_content" />

</cc.ibooker.progressbarlib.PullToRefreshLayout>
```
```
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
```

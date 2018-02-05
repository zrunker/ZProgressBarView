package cc.ibooker.zprogressbar;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * RecycleView适配器
 * Created by 邹峰立 on 2018/2/5.
 */
public class RvAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private ArrayList<String> mDatas;

    public RvAdapter(Context context, ArrayList<String> list) {
        inflater = LayoutInflater.from(context);
        mDatas = list;
    }

    public void reflashData(ArrayList<String> list) {
        mDatas = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RyHolder(inflater.inflate(R.layout.lv_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((RyHolder) holder).onBind(mDatas.get(position));
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}

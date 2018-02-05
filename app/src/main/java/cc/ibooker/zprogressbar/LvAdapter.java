package cc.ibooker.zprogressbar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 简易ListView适配器
 * Created by 邹峰立 on 2018/2/5.
 */
public class LvAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<String> mDatas;

    public LvAdapter(Context context, ArrayList<String> list) {
        inflater = LayoutInflater.from(context);
        mDatas = list;
    }

    public void reflashData(ArrayList<String> list) {
        mDatas = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.lv_item, parent, false);
            holder.textView = convertView.findViewById(R.id.tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(mDatas.get(position));
        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}

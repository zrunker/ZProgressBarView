package cc.ibooker.zprogressbar;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * RecycleView ViewHolder
 * Created by 邹峰立 on 2018/2/5.
 */
public class RyHolder extends RecyclerView.ViewHolder {
    private TextView textView;

    public RyHolder(View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.tv);
    }

    public void onBind(String data) {
        textView.setText(data);
    }
}

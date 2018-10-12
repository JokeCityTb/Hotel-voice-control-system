package com.eningqu.emeeting.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.eningqu.emeeting.R;
import com.eningqu.emeeting.bean.TranslateInfo;

import java.util.List;


public class MyAdapter extends BaseAdapter {

    private List<TranslateInfo> mTranslateInfoList;
    private Context mContext;

    public MyAdapter(Context context,List<TranslateInfo> translateInfoList) {
        mContext = context;
        mTranslateInfoList = translateInfoList;
    }

    @Override
    public int getCount() {
        return mTranslateInfoList.size();
    }

    @Override
    public TranslateInfo getItem(int position) {
        return mTranslateInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /* @Override
     public boolean areAllItemsEnabled() {
         return false;
     }

     @Override
     public boolean isEnabled(int position) {
         return false;
     }*/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.listview_item, null);
            holder.tv_receive = (TextView) convertView.findViewById(R.id.tv_receive);
            holder.tv_receive_other = (TextView) convertView.findViewById(R.id.tv_receive_other);
            holder.tv_send = (TextView) convertView.findViewById(R.id.tv_send);
            holder.tv_send_other = (TextView) convertView.findViewById(R.id.tv_send_other);
            holder.ly_receive = (LinearLayout) convertView.findViewById(R.id.ly_receive);
            holder.ly_send = (LinearLayout) convertView.findViewById(R.id.ly_send);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TranslateInfo msg = getItem(position);
        if (msg.getType() == TranslateInfo.TYPE_RECEIVE) {
            holder.ly_receive.setVisibility(View.VISIBLE);
            holder.ly_send.setVisibility(View.GONE);
            holder.tv_receive.setText(msg.getTranslate_old());
            holder.tv_receive_other.setText(msg.getTranslate_other());
        } else if (msg.getType() == TranslateInfo.TYPE_SEND) {
            holder.ly_send.setVisibility(View.VISIBLE);
            holder.ly_receive.setVisibility(View.GONE);
            holder.tv_send.setText(msg.getTranslate_old());
            holder.tv_send_other.setText(msg.getTranslate_other());
        }
        return convertView;
    }

    private static class ViewHolder {
        LinearLayout ly_send;
        LinearLayout ly_receive;
        TextView tv_receive;
        TextView tv_receive_other;
        TextView tv_send;
        TextView tv_send_other;
    }


}




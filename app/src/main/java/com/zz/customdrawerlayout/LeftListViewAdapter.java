package com.zz.customdrawerlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhonglei on 2017/4/17.
 */

public class LeftListViewAdapter extends BaseAdapter {

    private Context mContext;
    private final static String ICON = "icon";
    private final static String TITLE = "title";
    private ArrayList<Map<String, Object>> mLists = new ArrayList<>();

    public LeftListViewAdapter(Context context) {
        this.mContext = context;
        initData();
    }

    private void initData() {
        String[] items = mContext.getResources().getStringArray(R.array.function_items);
        TypedArray array = mContext.getResources().obtainTypedArray(R.array.function_icons);
        int[] icons = new int[array.length()];
        for (int i = 0; i < icons.length; i++) {
            icons[i] = array.getResourceId(i, 0);
        }

        for (int i = 0; i < items.length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put(ICON, icons[i]);
            map.put(TITLE, items[i]);
            mLists.add(map);
        }
    }

    @Override
    public int getCount() {
        return mLists.size();
    }

    @Override
    public Object getItem(int i) {
        return mLists.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View contentView, ViewGroup viewGroup) {
        View view;
        ViewHolder holder;
        if (contentView == null) {
            holder = new ViewHolder();
            view = LayoutInflater.from(mContext).inflate(R.layout.left_lv_item_layout, viewGroup, false);
            holder.icon = (ImageView) view.findViewById(R.id.item_icon);
            holder.title = (TextView) view.findViewById(R.id.item_title);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
            view = contentView;
        }
        holder.icon.setImageResource((Integer) mLists.get(i).get(ICON));
        holder.title.setText((String) mLists.get(i).get(TITLE));

        return view;
    }

    class ViewHolder {
        ImageView icon;
        TextView title;
    }
}

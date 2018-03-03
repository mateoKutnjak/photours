package com.example.mateo.photours.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.mateo.photours.R;

import java.util.List;
import java.util.Map;

public class ELVAdapter extends BaseExpandableListAdapter {

    private List<String> listCategory;
    private Map<String, List<String>> childMap;
    private Context context;

    public ELVAdapter(List<String> listCategory, Map<String, List<String>> childMap, Context context) {
        this.listCategory = listCategory;
        this.childMap = childMap;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return listCategory.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childMap.get(listCategory.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listCategory.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childMap.get(listCategory.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String categoryTitle = (String)getGroup(groupPosition);
        convertView = LayoutInflater.from(context).inflate(R.layout.elv_group, null);
        TextView tvGroup = (TextView)convertView.findViewById(R.id.tvGroup);
        tvGroup.setText(categoryTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String item = (String)getChild(groupPosition, childPosition);
        convertView = LayoutInflater.from(context).inflate(R.layout.elv_child, null);
        TextView tvChild = convertView.findViewById(R.id.tvChild);
        tvChild.setText(item);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}

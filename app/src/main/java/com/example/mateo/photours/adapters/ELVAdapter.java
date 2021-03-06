package com.example.mateo.photours.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.mateo.photours.R;
import com.example.mateo.photours.database.views.RouteView;
import com.example.mateo.photours.util.UnitsUtil;

import java.util.List;
import java.util.Map;

public class ELVAdapter extends BaseExpandableListAdapter {

    private List<RouteView> listCategory;
    private Map<RouteView, List<String>> childMap;
    private Context context;

    public ELVAdapter(List<RouteView> listCategory, Map<RouteView, List<String>> childMap, Context context) {
        this.listCategory = listCategory;
        this.childMap = childMap;
        this.context = context;
    }

    public void refresh(List<RouteView> listCategory, Map<RouteView, List<String>> childMap) {
        this.listCategory = listCategory;
        this.childMap = childMap;
        notifyDataSetChanged();
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
        String categoryTitle = ((RouteView)getGroup(groupPosition)).name;
        double distance = ((RouteView)getGroup(groupPosition)).length;
        int duration = ((RouteView)getGroup(groupPosition)).duration;
        int visited = ((RouteView)getGroup(groupPosition)).visited;
        int totalLandmarks = ((RouteView)getGroup(groupPosition)).totalLandmarks;

        convertView = LayoutInflater.from(context).inflate(R.layout.elv_group, null);

        TextView tvGroup = (TextView)convertView.findViewById(R.id.tvGroup);
        TextView tvGroupDistance = (TextView)convertView.findViewById(R.id.tvGroupDistance);
        TextView tvGroupDuration = (TextView)convertView.findViewById(R.id.tvGroupDuration);
        TextView tvGroupVisited = (TextView)convertView.findViewById(R.id.tvGroupVisited);

        tvGroup.setText(categoryTitle);
        tvGroupDistance.setText(String.valueOf(distance));
        tvGroupDuration.setText(String.valueOf(UnitsUtil.seconds2HHmmss(duration)));
        tvGroupVisited.setText(visited + "/" + totalLandmarks);

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

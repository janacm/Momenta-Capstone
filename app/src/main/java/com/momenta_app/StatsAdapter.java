package com.momenta_app;

import android.content.Context;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter for the Legend on the Stats Fragment
 */

class StatsAdapter extends RecyclerView.Adapter<StatsAdapter.ViewHolder>{
    private List<Pair<Integer, String>> list;

    StatsAdapter(List<Pair<Integer, String>> list) {
        this.list = list;
    }
    @Override
    public StatsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View activity = inflater.inflate(R.layout.legend_item, parent, false);
        return new StatsAdapter.ViewHolder(activity);
    }

    @Override
    public void onBindViewHolder(StatsAdapter.ViewHolder holder, int position) {
        Pair<Integer, String> pair = list.get(position);
        holder.color.setBackgroundColor(pair.first);
        holder.name.setText(pair.second);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setPairs(List<Pair<Integer, String>> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        final View color;
        final TextView name;

        ViewHolder(View itemView) {
            super(itemView);
            color = itemView.findViewById(R.id.legend_color);
            name = (TextView) itemView.findViewById(R.id.legend_name);
        }
    }
}

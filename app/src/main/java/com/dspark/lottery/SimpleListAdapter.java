package com.dspark.lottery;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class SimpleListAdapter extends RecyclerView.Adapter<SimpleListAdapter.ItemViewHolder> {
    ArrayList<SimpleListItem> mitems;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    /*
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }
    */

    //아이템 클릭시 실행 함수
    private SimpleListAdapter.ItemClick itemClick;
    public interface ItemClick {
        public void onClick(View view, int position);
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public SimpleListAdapter(ArrayList<SimpleListItem> items) {
        mitems = items;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SimpleListAdapter.ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        //TextView v = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple_recycler, parent, false);

        //ViewHolder vh = new ViewHolder(v);
        return new SimpleListAdapter.ItemViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SimpleListAdapter.ItemViewHolder holder, int position) {
        final int Position = position;
        //중략 ...................
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClick != null){
                    itemClick.onClick(v, Position);
                }
            }
        });

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        //holder.mTextView.setText(mDataset[position]);
        holder.mName.setText(mitems.get(position).getName());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mitems.size();
    }

    //아이템 클릭시 실행 함수 등록 함수
    public void setItemClick(SimpleListAdapter.ItemClick itemClick) {
        this.itemClick = itemClick;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        View view;
        private TextView mName;
        public ItemViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            mName = (TextView) itemView.findViewById(R.id.txtSimpleRecyclerViewName);
        }
    }
}

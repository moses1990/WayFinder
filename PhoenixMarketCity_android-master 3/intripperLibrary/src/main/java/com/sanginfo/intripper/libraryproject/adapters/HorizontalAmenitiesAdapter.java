package com.sanginfo.intripper.libraryproject.adapters;

import android.content.Context;
import android.graphics.LinearGradient;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanginfo.intripper.libraryproject.R;
import com.sanginfo.intripper.model.MapArea;

import java.util.ArrayList;

/**
 * Created by mosesafonso on 02/02/18.
 */

public class HorizontalAmenitiesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

   // private List<String> mDataList;
    private ArrayList<MapArea> mDataList;
    private int mRowIndex = -1;
    private int verticalPosition;
    Context myContext;

    public void setVerticalPosition(int verticalPosition)
    {
        this.verticalPosition = verticalPosition;
    }

    int getVerticalPosition()
    {
        return this.verticalPosition;
    }

    public HorizontalAmenitiesAdapter() {
    }

    public void setData(ArrayList<MapArea> data) {
        if (mDataList != data) {
            mDataList = data;
            notifyDataSetChanged();
        }
    }

    public void setRowIndex(int index) {
        mRowIndex = index;
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView text;

        public ItemViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.horizontal_item_text);
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        myContext = context;
        View itemView = LayoutInflater.from(context).inflate(R.layout.amenities_horizontal_item, parent, false);
        ItemViewHolder holder = new ItemViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder rawHolder, int position) {
        ItemViewHolder holder = (ItemViewHolder) rawHolder;

       // PG == LG
        //PUG == UG
        //P1 == FF

        String floor = "";

        if (mDataList.get(position).getLevelDec().equals("PG"))
        {
         floor = "LG" ;
        }

        if (mDataList.get(position).getLevelDec().equals("PUG"))
        {
         floor = "UG";
        }

        if (mDataList.get(position).getLevelDec().equals("P1"))
        {
         floor = "FF";
        }

        holder.text.setText(mDataList.get(position).getName() + " - " + floor);
        holder.itemView.setTag(position);

        Typeface typeFace=Typeface.createFromAsset(myContext.getAssets(),"fonts/Muli-SemiBold.ttf");
        holder.text.setTypeface(typeFace);


    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

}



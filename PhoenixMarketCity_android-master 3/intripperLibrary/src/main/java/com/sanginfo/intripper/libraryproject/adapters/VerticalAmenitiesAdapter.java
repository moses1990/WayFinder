package com.sanginfo.intripper.libraryproject.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sanginfo.intripper.libraryproject.R;
import com.sanginfo.intripper.libraryproject.common.AmenitiesDataModal;
import com.sanginfo.intripper.libraryproject.common.NestedClickInterface;
import com.sanginfo.intripper.libraryproject.common.RecyclerItemClickListener;
import com.sanginfo.intripper.model.MapArea;

import java.util.ArrayList;

/**
 * Created by mosesafonso on 02/02/18.
 */
public class VerticalAmenitiesAdapter extends RecyclerView.Adapter<VerticalAmenitiesAdapter.SimpleViewHolder> {

         Context mContext;
       // private static List<String> mData;
        private ArrayList<AmenitiesDataModal> mData;
        private static ArrayList<AmenitiesDataModal> dummydata;
        private static RecyclerView horizontalList;
        private  static NestedClickInterface nestedClickInterface;

        public static class SimpleViewHolder extends RecyclerView.ViewHolder {
            public final TextView title;
            public final ImageView myImageView;
            private HorizontalAmenitiesAdapter horizontalAdapter;

            public SimpleViewHolder(View view) {
                super(view);
                Context context = itemView.getContext();
                title = (TextView) itemView.findViewById(R.id.amenity_item_name);
                myImageView =(ImageView)itemView.findViewById(R.id.amenityImage);
                horizontalList = (RecyclerView) itemView.findViewById(R.id.horizontal_list);
                horizontalList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                horizontalAdapter = new HorizontalAmenitiesAdapter();
                horizontalAdapter.setVerticalPosition(getAdapterPosition());
                horizontalList.setAdapter(horizontalAdapter);

                horizontalList.addOnItemTouchListener(
                        new RecyclerItemClickListener(context, new   RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                // TODO Handle item click

                                Log.e("@@@@@",""+position+getAdapterPosition());

                             //   mData.get(getAdapterPosition())

                               getMData(getAdapterPosition(),position);

                            }
                        })
                );

            }
        }

    static public void getMData(int root,int child)
    {
        MapArea myMap = dummydata.get(root).getAmenityData().get(child);

        nestedClickInterface.MapAreaSelected(myMap);
    }

        public VerticalAmenitiesAdapter(Context context, ArrayList<AmenitiesDataModal>data,NestedClickInterface nestedClickInterface) {
            mContext = context;
           this.nestedClickInterface = nestedClickInterface;
            if (data != null) {
                mData = new ArrayList<>(data);
                dummydata = mData;
            }
            else mData = new ArrayList<>();
        }

        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.amenities_item, parent, false);
            return new SimpleViewHolder(view);
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, final int position) {
             holder.title.setText(mData.get(position).getAmenityName());
            Typeface typeFace=Typeface.createFromAsset(mContext.getAssets(),"fonts/Muli-SemiBold.ttf");
            holder.title.setTypeface(typeFace);
           //  holder.title.setTag(position);
             holder.horizontalAdapter.setData(mData.get(position).getAmenityData()); // List of Strings
            holder.myImageView.setImageResource(mContext.getResources().getIdentifier(mData.get(position).getAmenityName().toLowerCase(), "drawable", mContext.getPackageName()));
            holder.horizontalAdapter.setRowIndex(position);
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

    }


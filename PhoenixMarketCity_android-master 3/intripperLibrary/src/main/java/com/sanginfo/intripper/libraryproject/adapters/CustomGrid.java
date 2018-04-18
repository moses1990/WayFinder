package com.sanginfo.intripper.libraryproject.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanginfo.intripper.libraryproject.R;
import com.sanginfo.intripper.model.MapArea;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mosesafonso on 30/01/18.
 */

public class CustomGrid extends BaseAdapter {

    private Context mContext;
  //  private final String[] web;
  //  private final int[] Imageid;
   // private ArrayList< MapArea > finalData;

    private final List<MapArea> finalData = new ArrayList<MapArea>();

    private ArrayList<MapArea> si_in_lb__data;

    public CustomGrid(Context c, ArrayList<MapArea> si_in_lb__data ) {
        mContext = c;
        this.si_in_lb__data = si_in_lb__data;
      //  if (si_in_lb__data.size()!=0) {
            this.finalData.addAll(si_in_lb__data);
      //  }
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return si_in_lb__data.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View grid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {

            grid = new View(mContext);
            grid = inflater.inflate(R.layout.grid_single, null);
            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            Typeface typeFace1 =Typeface.createFromAsset(mContext.getAssets(),"fonts/Muli-SemiBold.ttf");
            textView.setTypeface(typeFace1);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);

            final MapArea area;
            area = si_in_lb__data.get( position );

            textView.setText(area.getName());

            String logoUrl = area.getLogoUrl();

            String logoName = area.getName().toLowerCase().trim();

           logoName = logoName.replace(" ", "");

            logoName = logoName.replace("&", "");

            Log.e("Logo Name",logoName);

            int checkExistence = mContext.getResources().getIdentifier(logoName, "drawable", mContext.getPackageName());

            if ( checkExistence != 0 ) {  // the resouce exists...
                imageView.setImageResource(mContext.getResources().getIdentifier(logoName, "drawable", mContext.getPackageName()));
            }
            else {  // checkExistence == 0  // the resouce does NOT exist!!
              //  result = false;

                if (logoUrl.length()>0)
            {
                Picasso.with(mContext)
                        .load(logoUrl)
                        .placeholder(R.drawable.gridplaceholder)
                        .resize(100, 100)
                        .centerCrop()
                        .into(imageView);
            }
            else
            {
                imageView.setImageResource(R.drawable.gridplaceholder);
            }
            }

        } else {
            grid = (View) convertView;

            TextView textView = (TextView) grid.findViewById(R.id.grid_text);
            Typeface typeFace1 =Typeface.createFromAsset(mContext.getAssets(),"fonts/Muli-SemiBold.ttf");
            textView.setTypeface(typeFace1);
            ImageView imageView = (ImageView)grid.findViewById(R.id.grid_image);

            final MapArea area;
            area = si_in_lb__data.get( position );

            textView.setText(area.getName());

            String logoUrl = area.getLogoUrl();

            String logoName = area.getName().toLowerCase().trim();

            logoName = logoName.replace(" ", "");

            logoName = logoName.replace("&", "");

            Log.e("Logo Name",logoName);

            imageView.setImageResource(mContext.getResources().getIdentifier(logoName, "drawable", mContext.getPackageName()));

            int checkExistence = mContext.getResources().getIdentifier(logoName, "drawable", mContext.getPackageName());

            if ( checkExistence != 0 ) {  // the resouce exists...
                imageView.setImageResource(mContext.getResources().getIdentifier(logoName, "drawable", mContext.getPackageName()));
            }
            else {  // checkExistence == 0  // the resouce does NOT exist!!
                //  result = false;

                if (logoUrl.length()>0)
                {
                    Picasso.with(mContext)
                            .load(logoUrl)
                            .placeholder(R.drawable.gridplaceholder)
                            .resize(100, 100)
                            .centerCrop()
                            .into(imageView);
                }
                else
                {
                    imageView.setImageResource(R.drawable.gridplaceholder);
                }
            }

        }

        return grid;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase();
        si_in_lb__data.clear();
        if (charText.length() == 0) {
            si_in_lb__data.addAll(finalData);
        } else {
            for (MapArea wp : finalData) {
                if (wp.getName().toLowerCase()
                        .contains(charText)) {
                    si_in_lb__data.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}

package com.sanginfo.intripper.libraryproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanginfo.intripper.libraryproject.common.CommonMethods;

import org.json.JSONObject;

import java.util.ArrayList;

import com.sanginfo.intripper.libraryproject.R;

public class SearchHistAdapterV2 extends ArrayAdapter< JSONObject >
{
      /**
       * The data.
       */
      private ArrayList< JSONObject > data;

      /**
       * Instantiates a new search history adapter.
       *
       * @param context
       *             the si_in_lb__context
       * @param resource
       *             the resource
       */
      public SearchHistAdapterV2( Context context, int resource )
      {
            super( context, resource );
      }

      /**
       * Instantiates a new search history adapter.
       *
       * @param context
       *             the si_in_lb__context
       * @param textViewResourceId
       *             the text view resource id
       * @param data
       *             the data
       */
      public SearchHistAdapterV2( Context context, int textViewResourceId, ArrayList< JSONObject > data )
      {
            super( context, textViewResourceId, data );
            this.data = data;
      }

      /*
       * (non-Javadoc)
       *
       * @see android.widget.ArrayAdapter#getView(int, android.view.View,
       * android.view.ViewGroup)
       */
      @Override
      public View getView( int position, View convertView, ViewGroup parent )
      {
            View             view          = convertView;
            final ViewHolder holder;
            final JSONObject searchData;
            String           categoryImage = "";
            int              resourceId;
            searchData = data.get( position );
            if ( view == null )
            {
                  LayoutInflater layoutInflater = ( LayoutInflater ) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                  view = layoutInflater.inflate( R.layout.si_in_lb__search_hist_list_item, null );
                  holder = new ViewHolder();
                  holder.searchTxt = ( TextView ) view.findViewById( R.id.section_name );
                  CommonMethods.setFontOpenSans(holder.searchTxt);
                  holder.searchHistoryIcon = ( ImageView ) view.findViewById( R.id.search_history_icon );
                  view.setTag( holder );
            }
            else
            {
                  holder = ( ViewHolder ) view.getTag();
            }
            try
            {
                  holder.searchTxt.setText( searchData.getString( "resultText" ) );
                  holder.searchHistoryIcon.setImageResource( R.drawable.si_in_lb__ic_clock_3 );
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
            holder.viewData = searchData;
            return view;
      }

      /**
       * The Class ViewHolder.
       */
      public static class ViewHolder
      {
            /**
             * The search text.
             */
            public TextView searchTxt;

            /**
             * The search history icon.
             */
            public ImageView searchHistoryIcon;

            /**
             * The view data.
             */
            public JSONObject viewData;
      }
}
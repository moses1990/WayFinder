package com.sanginfo.intripper.libraryproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sanginfo.intripper.libraryproject.R;
import com.sanginfo.intripper.libraryproject.common.CommonMethods;
import com.sanginfo.intripper.model.MapArea;
import com.sanginfo.intripper.model.VenueInfo;

import java.util.ArrayList;
// TODO: Auto-generated Javadoc

/**
 * The Class MapSearchAutoSuggestAdapterV2.
 */
public class MapSearchAutoSuggestAdapterV2 extends ArrayAdapter< MapArea >
{
      /**
       * The si_in_lb__data.
       */
      private ArrayList< MapArea > si_in_lb__data;

      /**
       * The si_in_lb__filtered.
       */
      private ArrayList< MapArea > si_in_lb__filtered;

      /**
       * The search string.
       */
      private String si_in_lb__searchString = "";

      /**
       * The si_in_lb__filter.
       */
      private Filter si_in_lb__filter;

      /**
       * The si_in_lb__context.
       */
      private Context si_in_lb__context;

      private VenueInfo venueInfo;

      /**
       * Instantiates a new map search auto suggest adapter.
       *
       * @param si_in_lb__context
       *             the si_in_lb__context
       * @param textViewResourceId
       *             the text view resource id
       */
      public MapSearchAutoSuggestAdapterV2( Context si_in_lb__context, int textViewResourceId )
      {
            super( si_in_lb__context, textViewResourceId );
      }

      /**
       * Instantiates a new map search auto suggest adapter.
       *
       * @param si_in_lb__context
       *             the si_in_lb__context
       * @param textViewResourceId
       *             the text view resource id
       * @param si_in_lb__data
       *             the si_in_lb__data
       */
      public MapSearchAutoSuggestAdapterV2( Context si_in_lb__context, int textViewResourceId, ArrayList< MapArea > si_in_lb__data,VenueInfo venueInfo)
      {
            super( si_in_lb__context, textViewResourceId, si_in_lb__data );
            this.si_in_lb__data = ( ArrayList< MapArea > ) si_in_lb__data.clone();
            this.si_in_lb__filtered = new ArrayList< MapArea >();
            this.si_in_lb__context = si_in_lb__context;
            this.venueInfo=venueInfo;
      }

      /* (non-Javadoc)
       * @see android.widget.ArrayAdapter#getCount()
       */
      @Override
      public int getCount()
      {
            return si_in_lb__filtered != null ? si_in_lb__filtered.size() : 0;
      }

      /* (non-Javadoc)
       * @see android.widget.ArrayAdapter#getItem(int)
       */
      @Override
      public MapArea getItem( int position )
      {
            //return super.getItem(position);
            return this.si_in_lb__filtered.get( position );
      }

      /* (non-Javadoc)
       * @see android.widget.ArrayAdapter#getItemId(int)
       */
      @Override
      public long getItemId( int position )
      {
            return position;
      }

      /* (non-Javadoc)
       * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
       */
      @Override
      public View getView( int position, View convertView, ViewGroup parent )
      {
            View             view = convertView;
            final ViewHolder holder;
            final MapArea    area;
            area = si_in_lb__filtered.get( position );
            if ( view == null )
            {
                  LayoutInflater layoutInflater = ( LayoutInflater ) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                  view = layoutInflater.inflate( R.layout.si_in_lb__search_list_item, null );
                  holder = new ViewHolder();
                  holder.sectionName = ( TextView ) view.findViewById( R.id.map_area_name );
                  holder.sectionDetail = ( TextView ) view.findViewById( R.id.map_area_detail );
                  view.setTag( holder );
            }
            else
            {
                  holder = ( ViewHolder ) view.getTag();
            }
            holder.sectionName.setText( area.getName() );
            holder.sectionDetail.setText( "Level " + venueInfo.getFloors().get(area.getFloor()).getName() );
            holder.viewData = area;
            CommonMethods.setFontOpenSans(holder.sectionName);
            CommonMethods.setFontOpenSans( holder.sectionDetail );
            return view;
      }

      /* (non-Javadoc)
       * @see android.widget.ArrayAdapter#getFilter()
       */
      @Override
      public Filter getFilter()
      {
            if ( si_in_lb__filter == null )
            {
                  si_in_lb__filter = new StoreNameFilter();
            }
            return si_in_lb__filter;
      }

      /**
       * The Class ViewHolder.
       */
      public static class ViewHolder
      {
            /**
             * The section name.
             */
            public TextView sectionName;

            /**
             * The section detail.
             */
            public TextView sectionDetail;

            /**
             * The view si_in_lb__data.
             */
            public MapArea viewData;

            /**
             * The area_logo.
             */
            public ImageView areaLogo;
      }

      /**
       * The Class StoreNameFilter.
       */
      private class StoreNameFilter extends Filter
      {
            /* (non-Javadoc)
             * @see android.widget.Filter#performFiltering(java.lang.CharSequence)
             */
            @Override
            protected FilterResults performFiltering( CharSequence constraint )
            {
                  FilterResults        result = new FilterResults();
                  ArrayList< MapArea > output = new ArrayList< MapArea >();
                  if ( constraint != null && constraint.toString().length() > 0 )
                  {
                        String searchText;
                        String sectionName;
                        searchText = constraint.toString();
                        searchText = searchText.toLowerCase();
                        for ( int counter = 0 ; counter < si_in_lb__data.size() ; counter++ )
                        {
                              sectionName = si_in_lb__data.get( counter ).getName();
                              sectionName = sectionName.toLowerCase();
                              if ( sectionName.contains( searchText ) )
                              {
                                    output.add( si_in_lb__data.get( counter ) );
                              }
                        }
                  }
                  result.count = output.size();
                  result.values = output;
                  return result;
            }

            /* (non-Javadoc)
             * @see android.widget.Filter#publishResults(java.lang.CharSequence, android.widget.Filter.FilterResults)
             */
            @Override
            protected void publishResults( CharSequence constraint, FilterResults results )
            {
                  try
                  {
                        si_in_lb__filtered.clear();
                        si_in_lb__filtered = ( ArrayList< MapArea > ) results.values;
                        si_in_lb__searchString = constraint.toString().toLowerCase();
                        notifyDataSetChanged();
                  }
                  catch ( Exception ex )
                  {
                        System.err.println( ex );
                  }
            }
      }
}

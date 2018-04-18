package com.sanginfo.intripper.libraryproject.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanginfo.intripper.model.MapArea;
import com.sanginfo.intripper.model.SearchPOI;
import com.sanginfo.intripper.model.VenueInfo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import com.sanginfo.intripper.libraryproject.R;
import com.sanginfo.intripper.libraryproject.common.CommonMethods;

/**
 * Created by Sang.26 on 10/15/2015.
 */
public class SearchAdapterV2 extends ArrayAdapter< SearchPOI >
{

      /**
       * The si_in_lb__context.
       */
      public Context si_in_lb__context;

      /**
       * The si_in_lb__filtered.
       */
      private ArrayList< SearchPOI > si_in_lb__filtered;

      /**
       * The map areas.
       */
      private ArrayList< MapArea > si_in_lb__mapAreas;

      /**
       * The search text.
       */
      private String searchText;
      /**
       * the venue info
       */

      private VenueInfo venueInfo;

      /**
       * Instantiates a new search auto suggest adapter.
       *
       * @param si_in_lb__context
       *             the si_in_lb__context
       * @param resource
       *             the resource
       */
      public SearchAdapterV2( Context si_in_lb__context, int resource )
      {
            super( si_in_lb__context, resource );
      }

      /**
       * Instantiates a new search auto suggest adapter.
       *
       * @param si_in_lb__context
       *             the si_in_lb__context
       * @param resource
       *             the resource
       * @param data
       *             the data
       * @param si_in_lb__mapAreas
       *             the map areas
       * @param searchedText
       *             the searched text
       * @param storeCategories
       *             the store categories
       */
      public SearchAdapterV2( Context si_in_lb__context, int resource, ArrayList< SearchPOI > data, ArrayList< MapArea > si_in_lb__mapAreas, String searchedText, ArrayList< JSONObject > storeCategories,VenueInfo venueInfo )
      {
            super( si_in_lb__context, resource, data );
            this.si_in_lb__context = si_in_lb__context;
            this.si_in_lb__filtered = data;
            this.si_in_lb__mapAreas = si_in_lb__mapAreas;
            this.searchText = searchedText;

            //uncomment this if category image is to be displayed
            // this.storeCategories = storeCategories;
            this.venueInfo=venueInfo;
      }

      /**
       * Sets the si_in_lb__filtered data.
       *
       * @param searchPOIs
       *             the search pois
       * @param searchText
       *             the search text
       */
      public void setFilteredData( ArrayList< SearchPOI > searchPOIs, String searchText )
      {
            this.si_in_lb__filtered = new ArrayList< SearchPOI >( searchPOIs );
            this.searchText = searchText;
      }

      /*
       * (non-Javadoc)
       *
       * @see android.widget.ArrayAdapter#getCount()
       */
      @Override
      public int getCount()
      {
            return si_in_lb__filtered != null ? si_in_lb__filtered.size() : 0;
      }

      /*
       * (non-Javadoc)
       *
       * @see android.widget.ArrayAdapter#getItem(int)
       */
      @Override
      public SearchPOI getItem( int position )
      {
            return this.si_in_lb__filtered.get( position );
      }

      /*
       * (non-Javadoc)
       *
       * @see android.widget.ArrayAdapter#getItemId(int)
       */
      @Override
      public long getItemId( int position )
      {
            return position;
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
            View             view = convertView;
            final ViewHolder holder;
            final SearchPOI  data;
            data = si_in_lb__filtered.get( position );
            if ( view == null )
            {
                  LayoutInflater layoutInflater = ( LayoutInflater ) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                  view = layoutInflater.inflate( R.layout.si_in_lb__search_autocomplete_item, null );
                  holder = new ViewHolder();
                  holder.searchResult = ( TextView ) view.findViewById( R.id.search_result_text );
                  holder.level = ( TextView ) view.findViewById( R.id.level );
                  holder.relListItemContainer = ( RelativeLayout ) view.findViewById( R.id.rel_list_item_container );

                  CommonMethods.setFontOpenSans( holder.searchResult );
                  CommonMethods.setFontOpenSans( holder.level );

                  view.setTag( holder );
            }
            else
            {
                  holder = ( ViewHolder ) view.getTag();
            }
            try
            {
                  String[]        matchArray;
                  SpannableString spannable;
                  holder.searchResult.setTextColor( getContext().getResources().getColor( R.color.band_green ) );
                  String resultText = data.getResultText();
                  spannable = new SpannableString( resultText );
                  matchArray = searchText.split( "\\ " );
                  for ( int counter = 0 ; counter < matchArray.length ; counter++ )
                  {
                        String word      = matchArray[ counter ];
                        int    lastIndex = 0;
                        while ( lastIndex != -1 )
                        {
                              lastIndex = resultText.toLowerCase( Locale.US ).indexOf( word.toLowerCase( Locale.US ), lastIndex );
                              if ( lastIndex != -1 )
                              {
                                    int                intEndPos     = lastIndex + word.length();
                                    TextAppearanceSpan highlightSpan = new TextAppearanceSpan( null, Typeface.BOLD, -1, null, null );
                                    spannable.setSpan( highlightSpan, lastIndex, intEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
                                    spannable.setSpan( new UnderlineSpan(), lastIndex, intEndPos, 0 );
                                    lastIndex += word.length();
                              }
                        }
                  }
                  if ( data.getPoint() == null )
                  {
                        holder.searchResult.setTextColor( getContext().getResources().getColor( R.color.band_blue ) );
                  }
                  holder.searchResult.setText( spannable );
                  holder.level.setText( "Level: " + venueInfo.getFloors().get(data.getArea().getFloor()).getName() );
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
            holder.viewData = data;
            return view;
      }

      /**
       * The Class ViewHolder.
       */
      public static class ViewHolder
      {
            /**
             * The search result.
             */
            public TextView searchResult;

            /**
             * The level.
             */
            public TextView level;

            /**
             * The view data.
             */
            public SearchPOI viewData;

            /**
             * The category image.
             */
            public ImageView categoryImage;

            /**
             * The List Item Container.
             */
            public RelativeLayout relListItemContainer;
      }
}

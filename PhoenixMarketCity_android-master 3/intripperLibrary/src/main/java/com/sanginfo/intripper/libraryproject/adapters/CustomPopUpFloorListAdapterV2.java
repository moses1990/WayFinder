package com.sanginfo.intripper.libraryproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanginfo.intripper.libraryproject.R;
import com.sanginfo.intripper.libraryproject.common.CommonMethods;
import com.sanginfo.intripper.model.FloorInfo;

import java.util.ArrayList;

/**
 * Created by Sang.24 on 12/10/2015.
 */
public class CustomPopUpFloorListAdapterV2 extends ArrayAdapter< FloorInfo >
{
      /**
       * The si_in_lb__data.
       */
      private ArrayList< FloorInfo > si_in_lb__data;

      /**
       * The selected floor.
       */
      private int si_in_lb__selectedFloor;

      /**
       * The positioning floor.
       */
      private int si_in_lb__positioningFloor;

      /**
       * The si_in_lb__context.
       */
      private Context si_in_lb__context;

      /**
       * Instantiates a new floor list adapter.
       *
       * @param si_in_lb__context
       *             the si_in_lb__context
       * @param si_in_lb__resource
       *             the si_in_lb__resource
       */
      public CustomPopUpFloorListAdapterV2( Context si_in_lb__context, int si_in_lb__resource )
      {
            super( si_in_lb__context, si_in_lb__resource );
      }

      /**
       * Instantiates a new floor list adapter.
       *
       * @param si_in_lb__context
       *             the si_in_lb__context
       * @param resource
       *             the resource
       * @param si_in_lb__data
       *             the si_in_lb__data
       * @param si_in_lb__selectedFloor
       *             the selected floor
       * @param si_in_lb__positioningFloor
       *             the positioning floor
       */
      public CustomPopUpFloorListAdapterV2( Context si_in_lb__context, int resource, ArrayList< FloorInfo > si_in_lb__data, int si_in_lb__selectedFloor, int si_in_lb__positioningFloor )
      {
            super( si_in_lb__context, resource, si_in_lb__data );
            this.si_in_lb__data = si_in_lb__data;
            this.si_in_lb__selectedFloor = si_in_lb__selectedFloor;
            this.si_in_lb__positioningFloor = si_in_lb__positioningFloor;
            this.si_in_lb__context = si_in_lb__context;
      }

      /* (non-Javadoc)
       * @see android.widget.ArrayAdapter#getCount()
       */
      @Override
      public int getCount()
      {
            return this.si_in_lb__data.size();
      }

      /* (non-Javadoc)
       * @see android.widget.ArrayAdapter#getItem(FloorInfo)
       */

      @Override
      public FloorInfo getItem( int position )
      {
            return super.getItem( position );
      }

      /* (non-Javadoc)
       * @see android.widget.ArrayAdapter#getItemId(int)
       */
      @Override
      public long getItemId( int position )
      {
            return super.getItemId( position );
      }

      /* (non-Javadoc)
       * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
       */
      @Override
      public View getView( final int position, View convertView, ViewGroup parent )
      {
            View             view = convertView;
            final ViewHolder holder;
            if ( view == null )
            {
                  LayoutInflater layoutInflater = ( LayoutInflater ) getContext().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
                  view = layoutInflater.inflate( R.layout.si_in_lb__custom_popup_floorlist_item, null );
                  holder = new ViewHolder();
                  holder.floorNumber = ( TextView ) view.findViewById( R.id.floorNumber );
                  holder.resultCount = ( TextView ) view.findViewById( R.id.resultCountBadger );
                  holder.floorNumberContainer = ( RelativeLayout ) view.findViewById( R.id.floorNumberContainer );
                  holder.innerFloorContainer = ( RelativeLayout ) view.findViewById( R.id.innerFloorContainer );
                  holder.separator = ( View ) view.findViewById( R.id.separator );
                  view.setTag( holder );
            }
            else
            {
                  holder = ( ViewHolder ) view.getTag();
            }
            //String floorNumber = CommonMethods.getFloorName( this.si_in_lb__data.get( position ).getFloorNumber() );
            String floorNumber = si_in_lb__data.get(position).getName();
            if ( this.si_in_lb__data.get( position ).getFloorNumber() == this.si_in_lb__selectedFloor )
            {
                  //Check if position of blue dot is on the same as floor position.
                  holder.floorNumber.setText( floorNumber );
                  holder.floorNumber.setTextColor( getContext().getResources().getColor( R.color.si_in_lb__white ) );
            }
            else
            {
                  holder.floorNumber.setText( floorNumber );
                  holder.floorNumber.setTextColor( getContext().getResources().getColor( R.color.si_in_lb__floor_level_color ) );
            }
            if ( this.si_in_lb__data.get( position ).getFloorNumber() == this.si_in_lb__selectedFloor )
            {
                  //Highlight the floor Number
                  holder.innerFloorContainer.setBackgroundResource( R.drawable.si_in_lb__floor_selector_background );
            }
            else
            {
                  holder.innerFloorContainer.setBackgroundResource( R.drawable.si_in_lb__user_position_anchor_background );
            }
            holder.viewData = this.si_in_lb__data.get( position ).getFloorNumber();
            CommonMethods.setFontOpenSans( holder.floorNumber );
            return view;
      }

      /**
       * The Class ViewHolder.
       */
      public static class ViewHolder
      {
            /**
             * The floor number.
             */
            public TextView floorNumber;

            /**
             * The result count.
             */
            public TextView resultCount;

            /**
             * The view si_in_lb__data.
             */
            public int viewData;

            /**
             * The floor_number_container.
             */
            public RelativeLayout floorNumberContainer;

            /**
             * The inner_floor_container.
             */
            public RelativeLayout innerFloorContainer;

            /**
             * The separator.
             */
            public View separator;
      }
}

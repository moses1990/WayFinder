package com.sanginfo.intripper.libraryproject.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanginfo.intripper.libraryproject.interfaces.NavigationPagerHandler;
import com.sanginfo.intripper.model.PathSegment;

import java.util.ArrayList;

import com.sanginfo.intripper.libraryproject.R;

public class NavigationContainerFragment extends Fragment implements OnClickListener
{
      /**
       * The handler.
       */
      public NavigationPagerHandler handler;

      /**
       * The feature.
       */
      public PathSegment feature;

      /**
       * The navigation previuos point.
       */
      private ImageView navigationPreviuosPoint;

      /**
       * The navigation next point.
       */
      private ImageView navigationNextPoint;

      /**
       * The path info.
       */
      private ArrayList< PathSegment > pathInfo;

      /**
       * The position.
       */
      private int position;

      private RelativeLayout navigation_container;

      /**
       * New instance.
       *
       * @param position
       *             the position
       * @param pathInfo
       *             the path info
       * @param handler
       *             the handler
       *
       * @return the navigation container fragment
       */
      public static NavigationContainerFragment newInstance( int position, ArrayList< PathSegment > pathInfo, NavigationPagerHandler handler )
      {
            NavigationContainerFragment fragment = null;
            Bundle                      bundle;
            try
            {
                  fragment = new NavigationContainerFragment();
                  bundle = new Bundle();
                  bundle.putInt( "position", position );
                  fragment.pathInfo = pathInfo;
                  fragment.handler = handler;
                  fragment.setArguments( bundle );
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
            return fragment;
      }

      /* (non-Javadoc)
       * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
       */
      @Override
      public void onCreate( Bundle savedInstanceState )
      {
            super.onCreate( savedInstanceState );
            position = getArguments().getInt( "position" );
      }

      /* (non-Javadoc)
       * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
       */
      @Override
      public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState )
      {
            View      view = null;
            int       imageResource;
            ImageView imageView;
            TextView  textView;
            try
            {
                  view = inflater.inflate( R.layout.si_in_lb__navigation_container_layout, null );
                  feature = pathInfo.get( position );
                  navigationPreviuosPoint = ( ImageView ) view.findViewById( R.id.previous_point );
                  navigationPreviuosPoint.setOnClickListener( new OnClickListener()
                  {
                        @Override
                        public void onClick( View view )
                        {
                              handler.onPreviousPage( position );
                        }
                  } );
                  navigationNextPoint = ( ImageView ) view.findViewById( R.id.next_point );
                  navigationNextPoint.setOnClickListener( new OnClickListener()
                  {
                        @Override
                        public void onClick( View view )
                        {
                              handler.onNextPage( position );
                        }
                  } );
                  navigation_container = ( RelativeLayout ) view.findViewById( R.id.navigation_container );
                  imageView = ( ImageView ) view.findViewById( R.id.direction_sign );
                  imageResource = R.drawable.si_in_lb__turn_straight_white;
                  if ( feature.getWalkingDirection() == PathSegment.DIRECTION_STRAIGHT )
                  {
                        imageResource = R.drawable.si_in_lb__turn_straight_white;
                  }
                  if ( feature.getWalkingDirection() == PathSegment.DIRECTION_SLIGHT_LEFT )
                  {
                        imageResource = R.drawable.si_in_lb__turn_slight_left_white;
                  }
                  if ( feature.getWalkingDirection() == PathSegment.DIRECTION_LEFT )
                  {
                        imageResource = R.drawable.si_in_lb__turn_left_white;
                  }
                  if ( feature.getWalkingDirection() == PathSegment.DIRECTION_SLIGHT_RIGHT )
                  {
                        imageResource = R.drawable.si_in_lb__turn_slight_right_white;
                  }
                  if ( feature.getWalkingDirection() == PathSegment.DIRECTION_RIGHT )
                  {
                        imageResource = R.drawable.si_in_lb__turn_right_white;
                  }
                  if ( position == 0 )
                  {
                        imageResource = R.drawable.si_in_lb__turn_start_white;
                  }
                  if ( position == ( pathInfo.size() - 1 ) )
                  {
                        imageResource = R.drawable.si_in_lb__finish_flag_white;
                        feature.setInstructions( "Arrive at your destination" );
                  }
                  imageView.setImageResource( imageResource );
                  textView = ( TextView ) view.findViewById( R.id.direction_text );
                  textView.setText( feature.getInstructions() );
                  handleNavigationButtons();
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
            return view;
      }

      /**
       * Handle navigation buttons.
       */
      private void handleNavigationButtons()
      {
            try
            {
                  if ( position <= 0 )
                  {
                        navigationPreviuosPoint.setVisibility( View.INVISIBLE );
                  }
                  else
                  {
                        navigationPreviuosPoint.setVisibility( View.VISIBLE );
                  }
                  if ( position == pathInfo.size() - 1 )
                  {
                        navigationNextPoint.setVisibility( View.INVISIBLE );
                  }
                  else
                  {
                        navigationNextPoint.setVisibility( View.VISIBLE );
                  }
            }
            catch ( Exception ex )
            {
            }
      }

      @Override
      public void onClick( View view )
      {
      }
}

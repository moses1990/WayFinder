package com.sanginfo.intripper.libraryproject.components;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class CustomViewpager extends ViewPager
{
      private boolean pagingEnabled = false;

      public CustomViewpager( Context context, AttributeSet attrs )
      {
            super( context, attrs );
      }

      public CustomViewpager( Context context )
      {
            super( context );
      }

      public void setPagingEnabled( boolean enabled )
      {
            pagingEnabled = enabled;
      }

      @Override
      public boolean onInterceptTouchEvent( MotionEvent event )
      {
            if ( !pagingEnabled )
            {
                  return false; // do not intercept
            }
            return super.onInterceptTouchEvent( event );
      }

      @Override
      public boolean onTouchEvent( MotionEvent event )
      {
            if ( !pagingEnabled )
            {
                  return false; // do not consume
            }
            return super.onTouchEvent( event );
      }
}

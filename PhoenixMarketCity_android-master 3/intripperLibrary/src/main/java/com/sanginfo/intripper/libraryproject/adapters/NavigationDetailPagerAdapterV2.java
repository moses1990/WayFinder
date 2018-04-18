package com.sanginfo.intripper.libraryproject.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.sanginfo.intripper.libraryproject.fragments.NavigationContainerFragment;
import com.sanginfo.intripper.libraryproject.interfaces.NavigationPagerHandler;
import com.sanginfo.intripper.model.PathSegment;

import java.util.ArrayList;

public class NavigationDetailPagerAdapterV2 extends FragmentStatePagerAdapter
{
      /**
       * The si_in_lb__count.
       */
      private int si_in_lb__count;

      private NavigationPagerHandler si_in_lb__handler;

      private ArrayList< PathSegment > si_in_lb__pathInfo;

      public NavigationDetailPagerAdapterV2( FragmentManager fm )
      {
            super( fm );
      }

      public NavigationDetailPagerAdapterV2( FragmentManager fm, int si_in_lb__count, ArrayList< PathSegment > si_in_lb__pathInfo, NavigationPagerHandler si_in_lb__handler )
      {
            super( fm );
            this.si_in_lb__count = si_in_lb__count;
            this.si_in_lb__pathInfo = si_in_lb__pathInfo;
            this.si_in_lb__handler = si_in_lb__handler;
      }

      @Override
      public Fragment getItem( int position )
      {
            Fragment fragment = null;
            try
            {
                  fragment = NavigationContainerFragment.newInstance(position, si_in_lb__pathInfo, si_in_lb__handler);
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
            return fragment;
      }

      @Override
      public int getCount()
      {
            return si_in_lb__count;
      }
}

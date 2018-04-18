package com.sanginfo.intripper.libraryproject.interfaces;

public interface NavigationPagerHandler
{
      /**
       * On next page.
       *
       * @param currentPosition
       *             the current position
       */
      public void onNextPage( int currentPosition );

      /**
       * On previous page.
       *
       * @param currentPosition
       *             the current position
       */
      public void onPreviousPage( int currentPosition );
}

package com.sanginfo.intripper.libraryproject.components;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListView;
// TODO: Auto-generated Javadoc

/**
 * The Class FloorListView.
 */
public class FloorListView extends ListView
{
      /**
       * The Constant DOUBLE_TAP.
       */
      private final static int DOUBLE_TAP = 2;

      /**
       * The Constant SINGLE_TAP.
       */
      private final static int SINGLE_TAP = 1;

      /**
       * The Constant LONG_TAP.
       */
      private final static int LONG_TAP = 3;

      /**
       * The Constant DELAY.
       */
      private final static int DELAY = ViewConfiguration.getDoubleTapTimeout();

      /**
       * The tag.
       */
      private static String TAG = "LongTapListView";

      /**
       * The m position holder.
       */
      private int mPositionHolder = -1;

      /**
       * The m position.
       */
      private int mPosition = -1;

      /**
       * The m on long tap listener.
       */
      private OnItemLongTapListener mOnLongTapListener = null;

      /**
       * The m parent.
       */
      private AdapterView< ? > mParent = null;

      /**
       * The m view.
       */
      private View mView = null;

      /**
       * The m id.
       */
      private long mId = 12315;

      /**
       * The m message.
       */
      private Message mMessage = null;

      /**
       * The m handler.
       */
      private Handler mHandler = new Handler()
      {
            @Override
            public void handleMessage( Message msg )
            {
                  super.handleMessage( msg );
                  switch ( msg.what )
                  {
                        case SINGLE_TAP:
                              Log.i( TAG, "Single tap entry" );
                              mOnLongTapListener.OnSingleTap( mParent, mView, mPosition, mId );
                              mPositionHolder = -1;
                              break;
                        case LONG_TAP:
                              Log.i( TAG, "Long tap entry" );
                              mOnLongTapListener.OnLongTap( mParent, mView, mPosition, mId );
                              break;
                  }
            }
      };

      /**
       * Instantiates a new Long tap list view.
       *
       * @param context
       *             the si_in_lb__context
       * @param attrs
       *             the attrs
       * @param defStyle
       *             the def style
       */
      public FloorListView( Context context, AttributeSet attrs, int defStyle )
      {
            super( context, attrs, defStyle );
            removeSelector();
      }

      /**
       * Removes the selector.
       */
      public void removeSelector()
      {
            setSelector( android.R.color.transparent ); // optional
      }

      /**
       * Instantiates a new Long tap list view.
       *
       * @param context
       *             the si_in_lb__context
       * @param attrs
       *             the attrs
       */
      public FloorListView( Context context, AttributeSet attrs )
      {
            super( context, attrs );
            removeSelector();
      }

      /**
       * Instantiates a new Long tap list view.
       *
       * @param context
       *             the si_in_lb__context
       */
      public FloorListView( Context context )
      {
            super( context );
            removeSelector();//optional
      }

      /**
       * Sets the on item Long click listener.
       *
       * @param listener
       *             the new on item Long click listener
       */
      public void setOnItemLongClickListener( OnItemLongTapListener listener )
      {
            mOnLongTapListener = listener;
            /*If the listener is null then throw exception*/
            if ( mOnLongTapListener == null )
            {
                  throw new IllegalArgumentException( "OnItemLongTapListener cannot be null" );
            }
            else
            {
                  setOnItemClickListener( new OnItemClickListener()
                  {
                        @Override
                        public void onItemClick( AdapterView< ? > parent, View view, int position, long id )
                        {
                              try
                              {
                                    mParent = parent;
                                    mView = view;
                                    mPosition = position;
                                    mId = id;
                                    mPositionHolder = position;
                                    mMessage = mMessage == null ? new Message() : mHandler.obtainMessage();
                                    mMessage.what = SINGLE_TAP;
                                    mHandler.sendMessage( mMessage );
                              }
                              catch ( Exception e )
                              {
                                    e.printStackTrace();
                              }
                        }
                  } );
                  setOnItemLongClickListener( new OnItemLongClickListener()
                  {
                        @Override
                        public boolean onItemLongClick( AdapterView< ? > parent, View view, int position, long id )
                        {
                              try
                              {
                                    mParent = parent;
                                    mView = view;
                                    mPosition = position;
                                    mPositionHolder = position;
                                    mMessage = mMessage == null ? new Message() : mHandler.obtainMessage();
                                    mMessage.what = LONG_TAP;
                                    mHandler.sendMessage( mMessage );
                              }
                              catch ( Exception e )
                              {
                                    e.printStackTrace();
                              }
                              return true;
                        }
                  } );
            }
      }

      /**
       * The Interface OnItemLongTapListener.
       */
      public interface OnItemLongTapListener
      {
            /**
             * On long tap.
             *
             * @param parent
             *             the parent
             * @param view
             *             the view
             * @param position
             *             the position
             * @param id
             *             the id
             */
            public void OnLongTap( AdapterView< ? > parent, View view, int position, long id );

            /**
             * On single tap.
             *
             * @param parent
             *             the parent
             * @param view
             *             the view
             * @param position
             *             the position
             * @param id
             *             the id
             */
            public void OnSingleTap( AdapterView< ? > parent, View view, int position, long id );
      }
}

package com.sanginfo.intripper.libraryproject.common;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sanginfo.intripper.libraryproject.utils.Typefaces;
import com.sanginfo.intripper.model.VenueInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public class CommonMethods
{
      /**
       * sets font of textview to opensans
       *
       * @param textView
       *             text to which font has to be set.
       */
      public static void setFontOpenSans( TextView textView )
      {
            try
            {
                  Typeface typeface = Typefaces.get( textView.getContext(), "fonts/OpenSans-Regular.ttf" );
                  textView.setTypeface( typeface );
            }
            catch ( Exception e )
            {
                  e.toString();
            }
      }

      /**
       * sets font of textview to Helvetica
       *
       * @param textView
       *             text to which font has to be set.
       */
      public static void setFontHelvetica( TextView textView )
      {
            try
            {
                  Typeface typeface = Typefaces.get( textView.getContext(), "fonts/HelveticaNeue-Light.otf" );
                  textView.setTypeface( typeface );
            }
            catch ( Exception e )
            {
                  e.toString();
            }
      }

      /**
       * converts device independent pixel unit to (device dependent) pixels
       *
       * @param context
       *             the si_in_lb__context instance
       * @param dp
       *             value of device independent pixel unit
       *
       * @return pixels of particular device
       */
      public static int dpToPx( Context context, int dp )
      {
            float density = context.getResources().getDisplayMetrics().density;
            return Math.round( ( float ) dp * density );
      }


      /**
       * create a text as a bitmap object
       *
       * @param context
       *             the instance of constant
       * @param text
       *             text to be converted into bitmap
       * @param textSize
       *             size of the bitmap to be specified
       * @param textColor
       *             color of text
       *
       * @return text as bitmap
       */
      public static Bitmap textAsBitmap( Context context, String text, float textSize, int textColor )
      {
            Resources resources = context.getResources();
            float     scale     = resources.getDisplayMetrics().density;
            TextPaint paint     = new TextPaint( TextPaint.LINEAR_TEXT_FLAG | TextPaint.ANTI_ALIAS_FLAG );
            paint.setTextSize( textSize * scale );
            paint.setColor( textColor );
            paint.setFakeBoldText( true );
            String        longText    = CommonMethods.longest_word( text );
            int           width       = ( int ) ( paint.measureText( longText ) + 10f ); // round
            float         baseline    = ( int ) ( -paint.ascent() + 0.5f ); // ascent() is negative
            int           height      = ( ( int ) ( baseline + paint.descent() + 0.5f ) ) * text.split( "\n" ).length + 10;
            Bitmap        image       = Bitmap.createBitmap( width, height, Bitmap.Config.ARGB_8888 );
            Canvas        canvas      = new Canvas( image );
            DynamicLayout mTextLayout = new DynamicLayout( text, paint, canvas.getWidth(), Layout.Alignment.ALIGN_CENTER, 0.8f, 0.8f, true );
            canvas.save();
            canvas.translate( 0, 0 );
            mTextLayout.draw( canvas );
            canvas.restore();
            return image;
      }

      /**
       * to find the longest word from the given text
       *
       * @param gText
       *             the text given
       *
       * @return the longest word from the given text
       */
      public static String longest_word( String gText )
      {
            String   longest_word = "";
            String[] req          = gText.split( "\n" );
            int      maxLength    = 0;
            for ( String str : req )
            {
                  if ( str.length() > maxLength )
                  {
                        maxLength = str.length();
                        longest_word = str;
                  }
            }
            return longest_word;
      }

      /**
       * @param context the instance of context
       * @param strKey the key of shared preference
       * @param strValue the value of shared preference
       */
      public static void savePreferences( Context context, String strKey, String strValue )
      {
            try
            {
                  SharedPreferences        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                  SharedPreferences.Editor editor            = sharedPreferences.edit();
                  editor.putString( strKey, strValue );
                  editor.commit();
            }
            catch ( Exception e )
            {
                  e.toString();
            }
      }

      /**
       * @param search the searched text which is to be saved
       * @param context  the instance of context
       */
      public static void setSearchHistory( JSONObject search, Context context )
      {
            try
            {
                  JSONArray history = getSearchHistory(context);
                  if ( history != null )
                  {
                        String text     = search.getString( "resultText" );
                        String position = checkSearchHistory( text, context, history );
                        if ( position != null )
                        {
                              history = removeElement( history, Integer.parseInt( position ) );
                              history.put( search );
                              savePreferences( context, "searchHistory", history.toString() );
                        }
                        else
                        {
                              if ( history.length() == 5 )
                              {
                                    history = removeElement( history, 0 );
                                    history.put( search );
                                    savePreferences( context, "searchHistory", history.toString() );
                              }
                              else
                              {
                                    history.put( search );
                                    savePreferences( context, "searchHistory", history.toString() );
                              }
                        }
                  }
            }
            catch ( Exception ex )
            {
                  System.err.println(ex);
            }
      }

      /**
       * @param context  the instance of context
       *
       * @return Json array of searched text.
       */
      public static JSONArray getSearchHistory( Context context )
      {
            String    prefValue = "";
            Object    data;
            JSONArray stores    = new JSONArray();
            try
            {
                  data = getPreferences( context, "searchHistory", CommonEnvironment.PREFTYPE_STRING );
                  prefValue = String.valueOf( data );
                  stores = new JSONArray( prefValue );
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
                  stores = new JSONArray();
            }
            return stores;
      }

      /**
       * @param text the text
       * @param context  the instance of context
       * @param searchHistoryArray  Json array of searched text.
       *
       * @return
       */
      public static String checkSearchHistory( String text, Context context, JSONArray searchHistoryArray )
      {
            String position = null;
            try
            {
                  if ( searchHistoryArray != null )
                  {
                        for ( int counter = 0 ; counter < searchHistoryArray.length() ; counter++ )
                        {
                              JSONObject json = searchHistoryArray.getJSONObject( counter );
                              if ( json.getString( "resultText" ).equalsIgnoreCase( text ) )
                              {
                                    position = Integer.toString( counter );
                                    break;
                              }
                        }
                  }
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
            return position;
      }

      /**
       * @param array
       * @param position
       *
       * @return
       */
      public static JSONArray removeElement( JSONArray array, int position )
      {
            JSONArray list = new JSONArray();
            for ( int counter = 0 ; counter < array.length() ; counter++ )
            {
                  if ( counter != position )
                  {
                        try
                        {
                              list.put( array.get( counter ) );
                        }
                        catch ( JSONException ex )
                        {
                              ex.printStackTrace();
                        }
                  }
            }
            return list;
      }

      /**
       * get the type of data from preferences
       *
       * @param context
       *             the instance of si_in_lb__context
       * @param key
       *             the preference name
       * @param preferenceDataType
       *             type of data, any 1 constant from Common Enviornment
       *
       * @return returns the value of the respective data type
       */
      public static Object getPreferences( Context context, String key, int preferenceDataType )
      {
            Object value = null;
            try
            {
                  SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
                  if ( sharedPreferences.contains( key ) )
                  {
                        switch ( preferenceDataType )
                        {
                              case CommonEnvironment.PREFTYPE_BOOLEAN:
                                    value = sharedPreferences.getBoolean( key, false );
                                    break;
                              case CommonEnvironment.PREFTYPE_INT:
                                    value = sharedPreferences.getInt( key, 0 );
                                    break;
                              case CommonEnvironment.PREFTYPE_STRING:
                                    value = sharedPreferences.getString( key, "" );
                                    break;
                              case CommonEnvironment.PREFTYPE_SET:
                                    Gson gson = new Gson();
                                    String json = sharedPreferences.getString( key, "" );
                                    value = gson.fromJson( json, VenueInfo.class );
                                    break;
                        }
                  }
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
                  return null;
            }
            return value;
      }



      /**
       * general method to make a view visible using animations from xml
       *
       * @param view
       *             the view to make visible
       * @param context
       *             the instance of si_in_lb__context
       * @param animationID
       *             the resource id of animation which to to be performed. eg: R.amin.si_in_lb__fadein_top
       */
      public static void showView( final View view, int animationID, Context context )
      {
            Animation animation;
            try
            {
                  if ( view.getVisibility() != View.VISIBLE )
                  {
                        animation = AnimationUtils.loadAnimation( context, animationID );
                        animation.setDuration( 250 );
                        animation.setAnimationListener( new Animation.AnimationListener()
                        {
                              @Override
                              public void onAnimationStart( Animation animation )
                              {
                              }

                              @Override
                              public void onAnimationRepeat( Animation animation )
                              {
                              }

                              @Override
                              public void onAnimationEnd( Animation animation )
                              {
                                    view.setVisibility( View.VISIBLE );
                              }
                        } );
                        view.startAnimation( animation );
                  }
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }

      /**
       * to check whether device supoorts voice or not.
       *
       * @param context
       *             instance of Context
       *
       * @return whether supported or not - boolean
       */
      public static boolean getEnableVoiceNavigation(Context context) {
            Object data;
            boolean enableVoiceNavigation = false;
            try {
                  data = getPreferences(context, "enableVoiceNavigation", CommonEnvironment.PREFTYPE_BOOLEAN);
                  if (data != null) {
                        enableVoiceNavigation = Boolean.parseBoolean(String.valueOf(data));
                  }
            } catch (Exception ex) {
                  System.err.println(ex);
                  enableVoiceNavigation = false;
            }
            return enableVoiceNavigation;
      }

      /**
       * set voice enabling option to on/off
       *
       * @param enableVoiceNavigation
       *             value of boolean
       * @param context
       *             the instance of si_in_lb__context
       */
      public static void setEnableVoiceNavigation( boolean enableVoiceNavigation, Context context )
      {
            try
            {
                  savePreferences(context, "enableVoiceNavigation", enableVoiceNavigation);
            }
            catch ( Exception ex )
            {
                  System.err.println(ex);
            }
      }

      /**
       * Store boolean values in Preferences
       *
       * @param context
       *             the instance of si_in_lb__context
       * @param strKey
       *             key of shared preference,the name
       * @param blnValue
       *             value of boolean
       */
      public static void savePreferences( Context context, String strKey, Boolean blnValue )
      {
            try
            {
                  SharedPreferences        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
                  SharedPreferences.Editor editor            = sharedPreferences.edit();
                  editor.putBoolean( strKey, blnValue );
                  editor.commit();
            }
            catch ( Exception e )
            {
                  e.toString();
            }
      }

      /**
       * general method to make a view gone using animations from xml
       *
       * @param view
       *             the view to make gone
       * @param context
       *             the instance of si_in_lb__context
       * @param animationID
       *             the resource id of animation which to to be performed. eg: R.amin.si_in_lb__fadeout_bottom
       */
      public static void hideView( final View view, int animationID, Context context )
      {
            Animation animation;
            try
            {
                  if ( view.getVisibility() == View.VISIBLE )
                  {
                        animation = AnimationUtils.loadAnimation( context, animationID );
                        animation.setDuration(250);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                              @Override
                              public void onAnimationStart(Animation animation) {
                              }

                              @Override
                              public void onAnimationRepeat(Animation animation) {
                              }

                              @Override
                              public void onAnimationEnd(Animation animation) {
                                    view.setVisibility(View.GONE);
                              }
                        });
                        view.startAnimation(animation);
                  }
            }
            catch ( Exception ex )
            {
                  System.err.println(ex);
            }
      }

      /**
       * circular reveal animation performed by center of circle being center of view with il_duration of 250 milliseconds.
       *
       * @param circularlayout
       *             view in which circular reveal animation has to be performed
       */

      public static void showCircularRevealAnimation( View circularlayout )
      {
            try
            {
                  if ( circularlayout.getVisibility() == View.VISIBLE )
                  {
                        return;
                  }
                  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                        // get the center for the clipping circle
                        int cx = circularlayout.getMeasuredWidth() / 2;
                        int cy = circularlayout.getMeasuredHeight() / 2;
                        // get the final radius for the clipping circle
                        int      finalRadius = Math.max(circularlayout.getWidth(), circularlayout.getHeight()) / 2;
                        Animator animator    = ViewAnimationUtils.createCircularReveal(circularlayout, cx, cy, 0, finalRadius);
                        animator.setDuration( 250 );
                        animator.setInterpolator(new DecelerateInterpolator());
                        animator.addListener(new Animator.AnimatorListener() {
                              @Override
                              public void onAnimationStart(Animator arg0) {
                              }

                              @Override
                              public void onAnimationRepeat(Animator arg0) {
                              }

                              @Override
                              public void onAnimationEnd(Animator arg0) {
                              }

                              @Override
                              public void onAnimationCancel(Animator arg0) {
                              }
                        });
                        circularlayout.setVisibility(View.VISIBLE);
                        animator.start();
                  }else {
                        ObjectAnimator fadein = ObjectAnimator.ofFloat(circularlayout, "alpha",  0f, 1f);
                        fadein.setDuration(250);
                        fadein.setInterpolator(new DecelerateInterpolator());
                        fadein.addListener(new Animator.AnimatorListener() {
                              @Override
                              public void onAnimationStart(Animator animation) {

                              }

                              @Override
                              public void onAnimationEnd(Animator animation) {

                              }

                              @Override
                              public void onAnimationCancel(Animator animation) {

                              }

                              @Override
                              public void onAnimationRepeat(Animator animation) {

                              }
                        });
                        circularlayout.setVisibility(View.VISIBLE);
                        fadein.start();
                  }

            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }


      /**
       * circular hide animation performed by center of circle being center of view with il_duration of 250 milliseconds.
       *
       * @param circularlayout
       *             view in which circular hide animation has to be performed
       */

      public static void hideCircularRevealAnimation( final View circularlayout )
      {
            try
            {
                  if ( circularlayout.getVisibility() == View.INVISIBLE || circularlayout.getVisibility() == View.GONE )
                  {
                        return;
                  }
                  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                        // get the center for the clipping circle
                        int cx = circularlayout.getMeasuredWidth() / 2;
                        int cy = circularlayout.getMeasuredHeight() / 2;
                        // get the initial radius for the clipping circle
                        int      initialRadius = circularlayout.getWidth() / 2;
                        Animator animator      = ViewAnimationUtils.createCircularReveal( circularlayout, cx, cy, initialRadius, 0 );
                        animator.setDuration(250);
                        animator.setInterpolator(new DecelerateInterpolator());
                        animator.addListener(new Animator.AnimatorListener() {
                              @Override
                              public void onAnimationStart(Animator arg0) {
                              }

                              @Override
                              public void onAnimationRepeat(Animator arg0) {
                              }

                              @Override
                              public void onAnimationEnd(Animator arg0) {
                                    circularlayout.setVisibility(View.GONE);
                              }

                              @Override
                              public void onAnimationCancel(Animator arg0) {
                              }
                        });
                        animator.start();
                  }else {
                        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(circularlayout, "alpha",  1f, 0f);
                        fadeOut.setDuration(250);
                        fadeOut.setInterpolator(new DecelerateInterpolator());
                        fadeOut.addListener(new Animator.AnimatorListener() {
                              @Override
                              public void onAnimationStart(Animator animation) {

                              }

                              @Override
                              public void onAnimationEnd(Animator animation) {
                                    circularlayout.setVisibility(View.GONE);

                              }

                              @Override
                              public void onAnimationCancel(Animator animation) {

                              }

                              @Override
                              public void onAnimationRepeat(Animator animation) {

                              }
                        });
                        fadeOut.start();

                  }

            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }


      /**
       * converts meters to feet
       *
       * @param meters
       *             distance in meters
       *
       * @return distance in feet
       */
      public static int meterToFeet( double meters )
      {
            int feet = 0;
            try
            {
                  feet = ( int ) Math.round( meters * 3.28084 );
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
            return feet;
      }

      /**
       * Time to walk.
       *
       * @param feetDistance
       *             the feet distance
       *
       * @return the int
       */
      public static int timeToWalk( int feetDistance )
      {
            int time = 0;
            try
            {
                  time = ( int ) Math.ceil( ( double ) feetDistance / ( double ) 150 ); //assuming 150 feet = 1 min.
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
            return time;
      }

      /**
       * to hide the keyboard
       *
       * @param context
       *             the instance of Context
       * @param view
       *             instance of view
       */
      public static void hideKeyboard( Context context, View view )
      {
            try
            {
                  InputMethodManager inputMethodManager = ( InputMethodManager ) context.getSystemService( Context.INPUT_METHOD_SERVICE );
                  inputMethodManager.hideSoftInputFromWindow( view.getWindowToken(), 0 );
            }
            catch ( Exception e )
            {
                  e.printStackTrace();
            }
      }

      /**
       * to show the keyboard
       *
       * @param context
       *             the instance of Context
       */
      public static void showKeyboard( Context context )
      {
            InputMethodManager inputMethodManager = ( InputMethodManager ) context.getSystemService( Context.INPUT_METHOD_SERVICE );
            inputMethodManager.toggleSoftInput( InputMethodManager.SHOW_FORCED, 0 );
      }

      /**
       * @param context
       * @param strKey
       * @param venueInfo
       */
      public static void saveClassObjectPreferences( Context context, String strKey, Object venueInfo )
      {
            try
            {
                  SharedPreferences        sharedPreferences = PreferenceManager.getDefaultSharedPreferences( context );
                  SharedPreferences.Editor editor            = sharedPreferences.edit();
                  Gson                     gson              = new Gson();
                  String                   json              = gson.toJson( venueInfo );
                  editor.putString( strKey, json );
                  editor.commit();
            }
            catch ( Exception e )
            {
                  e.toString();
            }
      }


}

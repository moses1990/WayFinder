package com.sanginfo.intripper.libraryproject.ui;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sanginfo.intripper.AsyncTask.IntripperSearchTask;
import com.sanginfo.intripper.libraryproject.R;
import com.sanginfo.intripper.libraryproject.adapters.SearchAdapterV2;
import com.sanginfo.intripper.libraryproject.adapters.SearchHistAdapterV2;
import com.sanginfo.intripper.libraryproject.common.CommonEnvironment;
import com.sanginfo.intripper.libraryproject.common.CommonMethods;
import com.sanginfo.intripper.listeners.IntripperSearchListener;
import com.sanginfo.intripper.model.MapArea;
import com.sanginfo.intripper.model.SearchPOI;
import com.sanginfo.intripper.model.VenueInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Sang.29 on 1/28/2016.
 */
public class SearchScreen extends Activity implements IntripperSearchListener
{
      // Tag set for logging functions...
      private static final String TAG = "SearchScreen";

      //
      private  int venueID;

      private String authToken="";

      // search text threshold value...
      private int searchTextThreshold = 1;

      // reference of marker of mapArea...
      private ArrayList< MapArea > mapAreas;

      //reference of search text
      private EditText searchText;

      //list of SearchPOI
      private ArrayList< SearchPOI > searchPOIs;

      //reference of search Loader Window
      private PopupWindow searchLoaderWindow;

      //reference of searched String
      private String searchedString = "";


      //instance of search adaptor
      private SearchAdapterV2 searchAutoSuggestAdapter;

      //instance of search result list
      private ListView searchResultListView;

      //instance of search history list
      private ListView searchHistoryListView;

      //reference of search bar
      private View searchBar;

      //reference of near by container layout
      private LinearLayout nearbyContainer;

      //reference of near by text
      private TextView nearbyHeader;

      //reference of history si_in_lb__header text
      private TextView historyHeader;

      //reference of background container
      private RelativeLayout bgContainer;

      //instance of search icon
      private ImageView searchIcon;

      //reference of root container
      private RelativeLayout rootContainer;

      //Instance of handler class...
      private Handler mHandler = new Handler();

      //reference of content container...
      private RelativeLayout contentContainer;

      // flag to check Aminities ...
      private boolean isAminity = false;

      //Instance of back button...
      private ImageView backButton;

      private IntripperSearchTask getFilterItem;

      private VenueInfo venueInfo;

      /**
       *
       * @param savedInstanceState
       */


      @Override
      protected void onCreate( Bundle savedInstanceState )
      {
            super.onCreate(savedInstanceState);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.si_in_lb__search_screen);
            try {
                  initializeMetadataValues();
            } catch (Exception e) {
                  e.printStackTrace();
                  throw new RuntimeException(e);
            }
            getVenueDataFromCache();
            initializeActivityControls();
            changeStatusBarColor( getResources().getColor( R.color.black_semi_transparent ) );
            mHandler.postDelayed( new Runnable()
            {
                  @Override
                  public void run()
                  {
                        // TODO Auto-generated method stub
                        showRootLayout();
                  }
            }, 100 );
      }

      /**
       * get venue data from preferences
       */
      private void getVenueDataFromCache()
      {
            try
            {
                  venueInfo = ( VenueInfo ) CommonMethods.getPreferences( getApplicationContext(), "venueData", CommonEnvironment.PREFTYPE_SET );
                  mapAreas = venueInfo.getMapAreas();
            }
            catch ( Exception error )
            {
                  System.out.println( error.toString() );
            }
      }

      /**
       * initialize activity view
       */
      private void initializeActivityControls()
      {
            contentContainer = ( RelativeLayout ) findViewById( R.id.content_container );
            bgContainer = ( RelativeLayout ) findViewById( R.id.bg_container );
            rootContainer = ( RelativeLayout ) findViewById( R.id.root_container );
            searchBar = ( View ) findViewById( R.id.search_bar );
            searchIcon = ( ImageView ) searchBar.findViewById( R.id.search_icon );
            searchText = ( EditText ) searchBar.findViewById( R.id.search_text );
            CommonMethods.setFontOpenSans( searchText );
            searchText.requestFocus();
            nearbyHeader = ( TextView ) findViewById( R.id.nearby_header );
            CommonMethods.setFontOpenSans( nearbyHeader );
            historyHeader = ( TextView ) findViewById( R.id.history_header );
            CommonMethods.setFontOpenSans( historyHeader );
            nearbyContainer = ( LinearLayout ) findViewById( R.id.nearby_container );
            bindDataToSearchResultList();
            bindDataToSearchHistoryDialog();
            initializeSearchTextView();
            initializeBackButton();
            initializeNearBYContainer();
      }

      /**
       * change status bar color.
       * @param color color id which is to be applied at status bar.
       */
      private void changeStatusBarColor( int color )
      {
            try
            {
                  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        getWindow().setStatusBarColor(color);
                  }

            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }

      /**
       * show root layout.
       */
      private void showRootLayout()
      {
            try
            {
                  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        int      cx          = rootContainer.getWidth() - ( searchIcon.getWidth() / 2 );
                        int      cy          = ( searchIcon.getHeight() / 2 );
                        int      finalRadius = Math.max( rootContainer.getWidth(), rootContainer.getHeight() );
                        Animator animator    = ViewAnimationUtils.createCircularReveal(rootContainer, cx, cy, 0, finalRadius);
                        rootContainer.setVisibility(View.VISIBLE);
                        animator.setDuration(300);
                        animator.setInterpolator(new DecelerateInterpolator());
                        animator.addListener(new Animator.AnimatorListener() {
                              @Override
                              public void onAnimationStart(Animator arg0) {

                                    changeStatusBarColor(getResources().getColor(R.color.search_screen_status_bar_tint));
                              }

                              @Override
                              public void onAnimationRepeat(Animator arg0) {
                              }

                              @Override
                              public void onAnimationEnd(Animator arg0) {
                                    showContent();
                              }

                              @Override
                              public void onAnimationCancel(Animator arg0) {
                              }
                        });
                        animator.start();
                  }else {
                        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(rootContainer, "alpha",  0f, 1f);
                        fadeIn.setDuration(300);
                        fadeIn.setInterpolator(new DecelerateInterpolator());
                        fadeIn.addListener(new Animator.AnimatorListener() {
                              @Override
                              public void onAnimationStart(Animator animation) {

                              }

                              @Override
                              public void onAnimationEnd(Animator animation) {
                                    showContent();

                              }

                              @Override
                              public void onAnimationCancel(Animator animation) {

                              }

                              @Override
                              public void onAnimationRepeat(Animator animation) {

                              }
                        });
                        rootContainer.setVisibility(View.VISIBLE);
                        fadeIn.start();
                  }

            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }

      /**
       * Bind data to search list adapter.
       */
      private void bindDataToSearchResultList()
      {
            try
            {
                  TextView emptySearchResultText;
                  emptySearchResultText = ( TextView ) findViewById( R.id.empty_search_result_text );
                  CommonMethods.setFontOpenSans( emptySearchResultText );
                  showSearchResultList();
                  searchResultListView = ( ListView ) findViewById( R.id.text_search_result_list );
                  searchResultListView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                              CommonMethods.hideKeyboard(getApplicationContext(), v);
                              return false;
                        }
                  });
                  if ( searchPOIs == null )
                  {
                        searchPOIs = new ArrayList< SearchPOI >();
                  }
                  searchAutoSuggestAdapter = new SearchAdapterV2( getApplicationContext(), R.layout.si_in_lb__search_autocomplete_item, searchPOIs, mapAreas, searchedString, null,venueInfo );
                  searchResultListView.setAdapter(searchAutoSuggestAdapter);
                  searchResultListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                              SearchPOI poi;
                              Intent intent;
                              SearchAdapterV2.ViewHolder holder;
                              try {
                                    poi = ((SearchAdapterV2.ViewHolder) view.getTag()).viewData;
                                    JSONObject searchedTerm;
                                    searchedTerm = poi.toJSON();
                                    CommonMethods.setSearchHistory(searchedTerm, getApplicationContext());
                                    CommonMethods.hideKeyboard(getApplicationContext(), searchText);
                                    intent = new Intent();
                                    intent.putExtra("poi", poi);
                                    setResult(RESULT_OK, intent);
                                    finish();
                              } catch (Exception ex) {
                                    System.err.println(ex);
                              }
                        }
                  });
            }
            catch ( Exception error )
            {
                  System.out.println( error.toString() );
            }
      }

      /**
       * bind data to search history adapter.
       */
      private void bindDataToSearchHistoryDialog()
      {
            SearchHistAdapterV2     adapter;
            ArrayList< JSONObject > historyText;
            TextView                emptyHistoryText;
            try
            {
                  historyText = getSearchHistory();
                  searchHistoryListView = ( ListView ) findViewById( R.id.search_history_list );
                  adapter = new SearchHistAdapterV2( getApplicationContext(), R.layout.si_in_lb__search_hist_list_item, historyText );
                  emptyHistoryText = ( TextView ) findViewById( R.id.empty_history_text );
                  CommonMethods.setFontOpenSans( emptyHistoryText );
                  searchHistoryListView.setEmptyView(emptyHistoryText);
                  searchHistoryListView.setAdapter( adapter );
                  searchHistoryListView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                              CommonMethods.hideKeyboard(getApplicationContext(), v);
                              return false;
                        }
                  });
                  searchHistoryListView.setOnItemClickListener( new AdapterView.OnItemClickListener()
                  {
                        @Override
                        public void onItemClick( AdapterView< ? > adapterView, View view, int position, long id )
                        {
                              SearchHistAdapterV2.ViewHolder holder;
                              JSONObject                     viewData;
                              holder = ( SearchHistAdapterV2.ViewHolder ) view.getTag();
                              viewData = holder.viewData;
                              Intent    intent;
                              SearchPOI poi;
                              CommonMethods.hideKeyboard( getApplicationContext(), searchText );
                              poi = SearchPOI.fromJson( viewData, mapAreas );
                              intent = new Intent();
                              intent.putExtra( "poi", poi );
                              setResult( RESULT_OK, intent );
                              finish();
                        }
                  } );
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }

      /**
       * initialize search textView.
       */
      private void initializeSearchTextView()
      {
            try
            {
                  final ImageView cancelIcon, micIcon;
                  cancelIcon = ( ImageView ) findViewById( R.id.text_cancel_icon );
                  cancelIcon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                              searchText.setText("");
                              hideSearchLoaderPopup();
                        }
                  });
                  if ( searchedString.length() != 0 )
                  {
                        searchText.setText( searchedString );
                        searchText.setSelection( searchText.getText().length() );
                  }
                  if ( searchPOIs == null )
                  {
                        searchPOIs = new ArrayList< SearchPOI >();
                  }
                  searchText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                              try {
                                    RelativeLayout searchHistory;
                                    // ImageView micIcon = (ImageView) findViewById(R.id.mic_icon);
                                    String textValue = searchText.getText().toString().toLowerCase();
                                    if (textValue.equals("restroom") || textValue.equals("atm") || textValue.equals("info_desk")) {
                                          SearchScreen.this.isAminity = true;
                                    }
                                    if (searchText.getText().toString().length() > searchTextThreshold) {
                                          searchHistory = (RelativeLayout) findViewById(R.id.search_history_container);
                                          if (searchHistory.getVisibility() == View.VISIBLE) {
                                                hideSearchHistory();
                                          }
                                          if (getFilterItem != null && getFilterItem.getStatus() == AsyncTask.Status.RUNNING) {
                                                getFilterItem.cancel(true);
                                          }

                                          //getFilterItem = new IntripperSearchTask(textValue, SearchScreen.this.isAminity, mapAreas, SearchScreen.this, SearchScreen.this, venueID,authToken);
                                        getFilterItem = new IntripperSearchTask(textValue, SearchScreen.this.isAminity, mapAreas, SearchScreen.this, SearchScreen.this, venueID,authToken,null,venueInfo);
                                          getFilterItem.execute();
                                          showSearchLoaderPopup();
                                    } else {
                                          if (searchText.getText().toString() == null || searchText.getText().toString().toString().length() < searchTextThreshold) {
                                                if (getFilterItem != null && getFilterItem.getStatus() == AsyncTask.Status.RUNNING) {
                                                      getFilterItem.cancel(true);
                                                }
                                                searchAutoSuggestAdapter.setFilteredData(new ArrayList<SearchPOI>(), "");
                                                searchAutoSuggestAdapter.notifyDataSetChanged();
                                                showSearchHistory();
                                                hideSearchLoaderPopup();
                                          }
                                    }
                                    if (!editable.toString().trim().equalsIgnoreCase("")) {
                                          cancelIcon.setVisibility(View.VISIBLE);
                                          searchIcon.setVisibility(View.GONE);
                                    } else {
                                          if (editable.toString().trim().equalsIgnoreCase("")) {
                                                cancelIcon.setVisibility(View.GONE);
                                                searchIcon.setVisibility(View.VISIBLE);
                                          }
                                    }
                              } catch (Exception ex) {
                                    System.err.println(ex);
                              }
                        }
                  });
            }
            catch ( Exception error )
            {
                  System.err.println( error );
            }
      }

      /**
       * initialize back button at header.
       */
      private void initializeBackButton()
      {
            try
            {
                  backButton = ( ImageView ) findViewById( R.id.back_button );
                  backButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                              setResult(RESULT_CANCELED);
                              hideContent();
                              InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                              imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                        }
                  });
            }
            catch ( Exception ex )
            {
                  Log.e(TAG, " initializeBackButton " + ex);
            }
      }

      /**
       * initialize near by container.
       */
      private void initializeNearBYContainer()
      {
            ImageView restroomIcon, atmIcon, cafeIcon, infoDeskIcon;
            restroomIcon = ( ImageView ) findViewById( R.id.restroom_icon );
            restroomIcon.setOnClickListener( new View.OnClickListener()
            {
                  @Override
                  public void onClick( View view )
                  {
                        isAminity = true;
                        searchText.setText( "Restroom" );
                  }
            } );

            atmIcon = ( ImageView ) findViewById( R.id.atm_icon );
            atmIcon.setOnClickListener( new View.OnClickListener()
            {
                  @Override
                  public void onClick( View view )
                  {
                        isAminity = true;
                        searchText.setText( "ATM" );
                  }
            } );

            cafeIcon = ( ImageView ) findViewById( R.id.cafe_icon );
            cafeIcon.setOnClickListener( new View.OnClickListener()
            {
                  @Override
                  public void onClick( View view )
                  {
                        searchText.setText( "Cafe" );
                  }
            } );

            infoDeskIcon = ( ImageView ) findViewById( R.id.info_desk_icon );
            infoDeskIcon.setOnClickListener( new View.OnClickListener()
            {
                  @Override
                  public void onClick( View view )
                  {
                        isAminity = true;
                        searchText.setText( "Info_Desk" );
                  }
            } );
      }

      /**
       *
       */
      private void showContent()
      {
            Animation animation;
            try
            {
                  animation = AnimationUtils.loadAnimation( getApplicationContext(), R.anim.si_in_lb__fadein_bottom );
                  animation.setInterpolator(new DecelerateInterpolator());
                  animation.setAnimationListener( new Animation.AnimationListener()
                  {
                        @Override
                        public void onAnimationStart( Animation arg0 )
                        {
                        }

                        @Override
                        public void onAnimationRepeat( Animation arg0 )
                        {
                        }

                        @Override
                        public void onAnimationEnd( Animation arg0 )
                        {
                              contentContainer.setVisibility( View.VISIBLE );
                              mHandler.postDelayed( new Runnable()
                              {
                                    @Override
                                    public void run()
                                    {
                                          CommonMethods.showKeyboard( getApplicationContext() );
                                    }
                              }, 500 );
                        }
                  } );
                  contentContainer.startAnimation(animation);
            }
            catch ( Exception ex )
            {
                  System.err.println(ex);
            }
      }

      private void showSearchResultList()
      {
            RelativeLayout searchResult;
            try
            {
                  searchResult = ( RelativeLayout ) findViewById( R.id.textsearch_result_list_container );
                  if ( searchResult.getVisibility() == View.INVISIBLE )
                  {
                        searchResult.setVisibility( View.VISIBLE );
                  }
            }
            catch ( Exception error )
            {
                  System.out.println(error.toString());
            }
      }

      /**
       * @return
       */
      private ArrayList< JSONObject > getSearchHistory()
      {
            JSONArray               history     = CommonMethods.getSearchHistory( getApplicationContext() );
            ArrayList< JSONObject > historyList = new ArrayList< JSONObject >();
            for ( int i = 0 ; i < history.length() ; ++i )
            {
                  try
                  {
                        JSONObject obj = history.getJSONObject( i );
                        historyList.add( obj );
                  }
                  catch ( JSONException e )
                  {
                        e.printStackTrace();
                  }
            }
            Collections.reverse(historyList);
            return historyList;
      }

      /**
       *
       */
      private void hideSearchLoaderPopup()
      {
            try
            {
                  if ( searchLoaderWindow != null )
                  {
                        searchLoaderWindow.dismiss();
                  }
                  searchLoaderWindow = null;
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }

      /**
       *
       */
      private void hideSearchHistory()
      {
            final RelativeLayout searchHistory;
            final RelativeLayout searchResult;
            try
            {
                  searchResult = ( RelativeLayout ) findViewById( R.id.textsearch_result_list_container );
                  searchHistory = ( RelativeLayout ) findViewById( R.id.search_history_container );
                  if ( searchHistory.getVisibility() == View.VISIBLE )
                  {
                        searchHistory.setVisibility( View.INVISIBLE );
                  }
                  if ( searchResult.getVisibility() == View.INVISIBLE )
                  {
                        searchResult.setVisibility( View.VISIBLE );
                  }
                  nearbyHeader.setVisibility( View.GONE );
                  nearbyContainer.setVisibility( View.GONE );
                  historyHeader.setText( "Search Results" );
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }

      /**
       *
       */
      private void showSearchLoaderPopup()
      {
            View           view;
            LayoutInflater layoutInflater;
            try
            {
                  if ( searchLoaderWindow != null )
                  {
                        return;
                  }
                  layoutInflater = ( LayoutInflater ) getSystemService( LAYOUT_INFLATER_SERVICE );
                  view = layoutInflater.inflate( R.layout.si_in_lb__search_progress_popup, null );
                  view.setLayoutParams( new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT ) );
                  searchLoaderWindow = new PopupWindow( view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT );
                  searchLoaderWindow.setTouchable( true );
                  searchLoaderWindow.setOutsideTouchable(false);
                  searchLoaderWindow.setBackgroundDrawable(new BitmapDrawable());
                  searchLoaderWindow.showAsDropDown(searchText);
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }

      /**
       *
       */
      private void showSearchHistory()
      {
            final RelativeLayout searchResult;
            final RelativeLayout searchHistory;
            try
            {
                  searchResult = ( RelativeLayout ) findViewById( R.id.textsearch_result_list_container );
                  searchHistory = ( RelativeLayout ) findViewById( R.id.search_history_container );
                  if ( searchResult.getVisibility() == View.VISIBLE )
                  {
                        searchResult.setVisibility( View.INVISIBLE );
                  }
                  if ( searchHistory.getVisibility() == View.INVISIBLE )
                  {
                        searchHistory.setVisibility( View.VISIBLE );
                  }
                  //nearbyHeader.setVisibility(View.VISIBLE);
                  //nearbyContainer.setVisibility(View.VISIBLE);
                  historyHeader.setText("Recent");
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }

      /**
       *
       */
      private void hideContent()
      {
            Animation animation;
            try
            {
                  animation = AnimationUtils.loadAnimation( getApplicationContext(), R.anim.si_in_lb__fadeout_bottom );
                  animation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation arg0) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation arg0) {
                        }

                        @Override
                        public void onAnimationEnd(Animation arg0) {
                              contentContainer.setVisibility(View.INVISIBLE);
                              hideRootLayout();
                        }
                  });
                  contentContainer.startAnimation(animation);
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }

      /**
       *
       */
      private void hideRootLayout()
      {
            try
            {
                  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        int      cx            = rootContainer.getWidth() - ( searchIcon.getWidth() / 2 );
                        int      cy            = ( searchIcon.getHeight() / 2 );
                        int      initialRadius = rootContainer.getWidth();
                        Animator animator      = ViewAnimationUtils.createCircularReveal(rootContainer, cx, cy, initialRadius, 0);
                        animator.setDuration( 300 );
                        animator.setInterpolator( new DecelerateInterpolator() );
                        animator.addListener(new Animator.AnimatorListener() {
                              @Override
                              public void onAnimationStart(Animator arg0) {
                              }

                              @Override
                              public void onAnimationRepeat(Animator arg0) {
                              }

                              @Override
                              public void onAnimationEnd(Animator arg0) {
                                    rootContainer.setVisibility(View.INVISIBLE);
                                    finish();
                                    overridePendingTransition(0, 0);
                              }

                              @Override
                              public void onAnimationCancel(Animator arg0) {
                              }
                        });
                        animator.start();
                  }else {
                        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(rootContainer, "alpha",  1f, 0f);
                        fadeOut.setDuration(300);
                        fadeOut.setInterpolator(new DecelerateInterpolator());
                        fadeOut.addListener(new Animator.AnimatorListener() {
                              @Override
                              public void onAnimationStart(Animator animation) {

                              }

                              @Override
                              public void onAnimationEnd(Animator animation) {
                                    rootContainer.setVisibility(View.INVISIBLE);
                                    finish();
                                    overridePendingTransition(0, 0);

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

      @Override
      public void onBackPressed()
      {
            try
            {
                  if ( findViewById( R.id.text_cancel_icon ).getVisibility() == View.VISIBLE )
                  {
                        searchText.setText("");
                        hideSearchLoaderPopup();
                        return;
                  }
                  setResult(RESULT_CANCELED);
                  hideContent();
                  CommonMethods.hideKeyboard(getApplicationContext(), searchText);
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }

      @Override
      public void onSearchStarted()
      {

      }

      @Override
      public void onSearchCompleted( ArrayList searchPOIList, String searchItem )
      {
            try
            {
                  SearchScreen.this.isAminity = false;
                  searchAutoSuggestAdapter.setFilteredData( searchPOIList, searchItem );
                  searchAutoSuggestAdapter.notifyDataSetChanged();
                  hideSearchLoaderPopup();
                  searchPOIs = searchPOIList;
                  TextView emptySearchResultText;
                  emptySearchResultText = ( TextView ) findViewById( R.id.empty_search_result_text );
                  if ( searchText.getText().toString().length() > searchTextThreshold )
                  {
                        if ( searchPOIList.size() == 0 )
                        {
                              emptySearchResultText.setVisibility( View.VISIBLE );
                        }
                        else
                        {
                              emptySearchResultText.setVisibility( View.GONE );
                        }
                  }
                  else
                  {
                        if ( emptySearchResultText.getVisibility() == View.VISIBLE )
                        {
                              emptySearchResultText.setVisibility( View.GONE );
                        }
                  }
            }
            catch ( Exception ex )
            {
                  System.err.println( ex );
            }
      }
      private void initializeMetadataValues() throws IllegalStateException,PackageManager.NameNotFoundException{
            Bundle bundle;
            bundle = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA).metaData;
            if (bundle.containsKey("com.sanginfo.intripper.VENUE_ID")){
                  venueID = bundle.getInt("com.sanginfo.intripper.VENUE_ID");
            }else {
                  throw new RuntimeException("venueID is not set");
            }
            if (bundle.containsKey("com.sanginfo.intripper.AUTH_TOKEN")){
                authToken = bundle.getString("com.sanginfo.intripper.AUTH_TOKEN");
            }else {
                  throw new RuntimeException("authToken is not set");
            }

      }


}

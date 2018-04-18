package com.sanginfo.intripper.libraryproject.ui;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.sanginfo.intripper.libraryproject.Database.DatabaseHandler;
import com.sanginfo.intripper.libraryproject.R;
import com.sanginfo.intripper.libraryproject.adapters.CustomGrid;
import com.sanginfo.intripper.libraryproject.adapters.CustomPopUpFloorListAdapterV2;
import com.sanginfo.intripper.libraryproject.adapters.MapSearchAutoSuggestAdapterV2;
import com.sanginfo.intripper.libraryproject.adapters.NavigationDetailPagerAdapterV2;
import com.sanginfo.intripper.libraryproject.adapters.VerticalAmenitiesAdapter;
import com.sanginfo.intripper.libraryproject.common.AmenitiesDataModal;
import com.sanginfo.intripper.libraryproject.common.CommonEnvironment;
import com.sanginfo.intripper.libraryproject.common.CommonMethods;
import com.sanginfo.intripper.libraryproject.common.NestedClickInterface;
import com.sanginfo.intripper.libraryproject.components.CustomViewpager;
import com.sanginfo.intripper.libraryproject.components.FloorListView;
import com.sanginfo.intripper.libraryproject.components.MaterialRippleLayout;
import com.sanginfo.intripper.libraryproject.interfaces.NavigationPagerHandler;
import com.sanginfo.intripper.listeners.IntripperMapListener;
import com.sanginfo.intripper.listeners.PathRenderingListener;
import com.sanginfo.intripper.model.FloorInfo;
import com.sanginfo.intripper.model.MapArea;
import com.sanginfo.intripper.model.Offer;
import com.sanginfo.intripper.model.PathSegment;
import com.sanginfo.intripper.model.PathSegments;
import com.sanginfo.intripper.model.PromoZone;
import com.sanginfo.intripper.model.SearchPOI;
import com.sanginfo.intripper.model.VenueInfo;
import com.sanginfo.intripper.ui.IntripperMapFragment;
import com.sanginfo.intripper.ui.PathOptions;
import com.sanginfo.intripper.ui.UiSettings;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

/**
 * Created by Intripper on 1/19/2016.
 */
public class MapActivity extends FragmentActivity implements IntripperMapListener, NavigationPagerHandler, PathRenderingListener ,NestedClickInterface {
    // Tag set for logging functions...
    private static final String TAG = "MapActivity";

    // Result code for intent...
    private static final int PROXIMITY_OFFER_LIST_INTENT = 101;

    // The source & destination areas...
    ArrayList<MapArea> sourceAreas, destinationAreas;

    // the source location..
    private MapArea sourceArea;

    // the destination location..
    private MapArea destinationArea;

    // instance of intripper map fragment...
    private IntripperMapFragment mapFragment;

    //instance of base map i.e google map fragment...
    private GoogleMap googleMap;

    // reference for AutoCompleteTextView of source Location...
    private AutoCompleteTextView sourceLocation = null;

    // reference for AutoCompleteTextView of destination Location...
    private AutoCompleteTextView destinationLocation = null;

    // reference of Intripper area detail band...
    private RelativeLayout mapAreaBand;

    // details of current map area in the band...
    private MapArea currentMapArea;

    // reference of Intripper area detail band's start icon...
    private ImageView startMapAreaBandImage;

    // reference of marker of mapArea...
    private Marker mapAreaMarker;

    // reference of blinking offer bubble...
    private RelativeLayout offerBubble;

    // reference of user position icon...
    private RelativeLayout userPositionIcon;

    //reference of navigation layout container...
    private RelativeLayout navigationContainer;

    // reference of navigation start icon in navigation band...
    private ImageView startNavigationImage;

    // reference of navigation stop icon in navigation band...
    private ImageView stopNavigationImage;

    private ImageView closeNavigationImage;

    // for voice navigation...
    private TextToSpeech textToSpeech;

    // current floor indicator...
    private int currentFloor = 5;

    // current floor indicator...
    private int positioningFloor = 0;

    // indoor atlas positioning service state flag...
    private boolean positioningOnGoing = false;

    private boolean isRendering = false;

    // for changing ui of IntripperMap element...
    private UiSettings uiSettings;

    // list of offers, when you enter a promo zone...
    private ArrayList<JSONObject> zonePromotions = null;

    //Instance of handler class...
    private Handler mHandler = new Handler();

    // meta data about the venue...
    private VenueInfo venueInfo;

    // reference of floor bubble icon...
    private RelativeLayout floorChangeIcon;

    // reference of floor list container..
    private RelativeLayout FloorlistContainer;

    // reference of floor list popup window...
    private PopupWindow floorPopupUpWindow;

    // reference of drop down floor list...
    private FloorListView customDropDownFloorlist;

    // reference of search icon
    private ImageView search_icon;

    //reference of top Navigation Detail Container...
    private RelativeLayout topNavigationDetailContainer;

    //search intent code
    private int INTENT_SEARCH = 101;

    //reference of top Navigation icon...
    private RelativeLayout navigationIcon;

    // reference of navigation volume icon...
    private ImageView navigationVolumeButton;

    // information of each path segment of navigation...
    private ArrayList<PathSegment> pathInfos;

    // flag to check voive output is enabled or not during navigation...
    private boolean isNavigationVolumeOn = false;

    //reference of back button of top Navigation Detail Container...
    private LinearLayout navigationBackImageContainer;

    // position of user on the map...
    private LatLng userPosition;

    // reference of Top swipable navigation pager...
    private CustomViewpager navigationPager;

    // reference of progress bar..
    private ProgressDialog progressDialog;

    //reference of search poi...
    private SearchPOI searchPOI;

    // reference of compass to rotate to north in map...
    private ImageView mapRotationIndicator;

    // to know whether destination is up or down during navigation
    private String comparedFloorPosition;

    //reference of poi marker...
    private Marker POIMarker = null;

    // reference of arrive at destination dialog box...
    private Dialog arrivalDialog;

    // flag to check visibilty of arrive destination popup...
    private boolean isArriveDialogVisible;

    // flag to check visibilty of reroute popup...
    private boolean isRerouteMessageVisible;

    // reference of arrive at reroute alert box...
    private AlertDialog reRouteDialogBox;

    // reference of recenter button...
    private Button reCenter;

    // flag to check whether navigation is on going or not...
    private boolean isNavigationOngoing = false;

    // flag to check whether Floor Change Message Band is Showing or not...
    private boolean isFloorChangeMessageBandShowing;

    // flag to delay of showing the next message band...
    private boolean delayShowOfFloorChangeBand;

    // reference of floor plan ID...
    String floorPlanId = "";
    //reference of IntripperLocationProvider...

    private boolean isIntripperLocationConnected = false;
    private String locateStoreID = "";
    private boolean isAppStarted = false;

    // reference of BottomTabBar
    private RelativeLayout bottomTabBar;

    // reference of FindByNameView
    private RelativeLayout findByNameView;

    // reference of FindByCategoryView
    private RelativeLayout findByCategoryView;

    // reference of Store Content
    private RelativeLayout storeContentView;

    // reference of Amenities View
    private RelativeLayout amenitiesView;

    private RelativeLayout customerSupprotView;

    private CustomGrid customGridadapter;

    //reference of Find By Name Button
    private Button findByName;

    //reference of Find By Category Button
    private Button findByCategory;

    private Button mallAmenities;

    private Button customerSupportButton;

    //reference of Close Find By Name Button
    private Button closeFindByName;

    private Button closeFindByCategory;

    private Button locateStoreButton;

    private Button storeContentBackButton;

    private GridView grid;

    private GridView gridCategoryStores;

    private ImageButton palladiumImageButton;

    private ImageButton pmcImageButton;

    private LinearLayout palladiumFloorLayout;

    private LinearLayout pmcFloorLayout;

    private Button pgButton;

    private Button pugButton;

    private Button p1Button;

    private Button lgButton;

    private Button gButton;

    private Button l1Button;

    private Button l2Button;

    private Button l3Button;

    private Button customerSupportBack;

    private Button amenitiesBack;

    ArrayList<MapArea> findByViewAreas;

    private EditText editsearch;

    private TextView storeNameLabel;

    private TextView storeDescriptionLabel;

    private VerticalAmenitiesAdapter verticalAmenitiesAdapter;

    private RecyclerView amenitiesRecyclerView;

    ArrayList<AmenitiesDataModal> amenitiesData;

    int myAutomatedPostion;

    private Handler myHandler;

    private Runnable mRunnable;

    private ImageView contentImageView;

    private Button lingerieandinnerwear;

    private Button footfashionbagslugguageandaccessories;

    private Button departmentstores;

    private Button cafesrestaurantsdesserts;

    private Button cosmaticssalonandspa;

    private Button jewellery;

    private Button womensfashion;

    private Button homeaccessoriesgifsandhobbies;

    private Button generalfashion;

    private Button mensfashion;

    private Button watchesfashionaccessories;

    private Button sportswear;

    private Button pensbookmusictoys;

    ArrayList <MapArea> selectedCategory;

    private RelativeLayout close_navigation_container;

    private ImageView centercircleImage;

    private EditText nameEditText;

    private EditText phoneEditText;

    private EditText qweryEditText;

    private Button submitButtonCustomerSupport;

    private ImageView findHeaderImage;

    private RelativeLayout videoOverlay;

    private TextView wayFinderTextView;

    private TextView wayFinderButton;

    private VideoView vv;



    DatabaseHandler db = new DatabaseHandler(MapActivity.this);

  //  private Timer timer;

    Handler activityHandler;
    Runnable activityR;

    /**
     * speak out text.
     *
     * @param sentence the text to speak out.
     */
    public void speakOut(String sentence) {
        try {
            if (textToSpeech != null) {
                if (CommonMethods.getEnableVoiceNavigation(getApplicationContext())) {
                    if (!textToSpeech.isSpeaking()) {
                        textToSpeech.speak(sentence, TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString() + " at speakOut");
        }
    }

    /**
     * initialize navigation band.
     */

    private void initializeNavigationBand() {
        startNavigationImage = (ImageView) navigationContainer.findViewById(R.id.start_navigation);
        startNavigationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommonMethods.hideCircularRevealAnimation(close_navigation_container);
                CommonMethods.hideCircularRevealAnimation(closeNavigationImage);
                handleStartNavigationFromBand();
            }
        });
        stopNavigationImage = (ImageView) navigationContainer.findViewById(R.id.stop_navigation);
        stopNavigationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitNavigation();
            }
        });

//        stopNavigationImage = (ImageView) navigationContainer.findViewById(R.id.stop_navigation);
//        stopNavigationImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                exitNavigation();
//            }
//        });

        closeNavigationImage = (ImageView) navigationContainer.findViewById(R.id.close_navigation);
        close_navigation_container = (RelativeLayout) navigationContainer.findViewById(R.id.close_navigation_container);
        closeNavigationImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exitNavigation();
            }
        });

        TextView navigationName = (TextView) findViewById(R.id.navigation_detail_description);
        navigationName.setText(Html.fromHtml("To <b>" + destinationArea.getName() + "</b>"));
        updateNavigationCurrentDetails(pathInfos.get(0), 0);
    }

    /**
     * update navigation detail band information.
     *
     * @param pathSegment current path segment details.
     * @param position    Position of path segment.
     */

    private void updateNavigationCurrentDetails(PathSegment pathSegment, int position) {
        TextView navigationTiming = (TextView) findViewById(R.id.navigation_time);
        TextView navigationDistance = (TextView) findViewById(R.id.navigation_distance);
        TextView navigationFloor = (TextView) findViewById(R.id.floor_descripter);
        try {
            int totalDistance = calculateTotalDistannce();
            int walkedDistance = calculateWalkedDistannce(position);
            int remainingDistance = totalDistance - walkedDistance;
            int time = CommonMethods.timeToWalk(remainingDistance);
            updateNavigationDistance(navigationDistance, remainingDistance);
            updateNavigationTime(navigationTiming, time);
            updateNavigationFloor(pathSegment, navigationFloor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void showNavigationPager() {
        View view;
        try {
            view = findViewById(R.id.navigation_pager);
            CommonMethods.showCircularRevealAnimation(view);
            Window window = this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.band_green));
            setFloorPopUpPosition(findViewById(R.id.navigation_pager), false);
            HandleNavigationVolumeButton();
        } catch (Exception ex) {
            System.err.println(ex);
        //    Crashlytics.log(Log.ERROR, TAG, ex.toString());
        }
    }

    /**
     * show or hide navigation volume button.
     */

    private void HandleNavigationVolumeButton() {
        try {
            navigationVolumeButton = (ImageView) findViewById(R.id.navigation_volume_button);
            CommonMethods.showCircularRevealAnimation(navigationVolumeButton);
            isNavigationVolumeOn = CommonMethods.getEnableVoiceNavigation(getBaseContext());
            if (CommonMethods.getEnableVoiceNavigation(getBaseContext())) {
                if (!isNavigationVolumeOn) {
                    navigationVolumeButton.setImageResource(R.drawable.si_in_lb__ic_av_volume_off);
                } else {
                    navigationVolumeButton.setImageResource(R.drawable.si_in_lb__ic_av_volume_up);
                }
            }
            navigationVolumeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isNavigationVolumeOn) {
                        CommonMethods.setEnableVoiceNavigation(true, getBaseContext());
                        navigationVolumeButton.setImageResource(R.drawable.si_in_lb__ic_av_volume_up);
                        isNavigationVolumeOn = true;
                        navigationVolumeButton.invalidate();
                    } else {
                        CommonMethods.setEnableVoiceNavigation(false, getBaseContext());
                        navigationVolumeButton.setImageResource(R.drawable.si_in_lb__ic_av_volume_off);
                        isNavigationVolumeOn = false;
                        navigationVolumeButton.invalidate();
                    }
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, " HandleNavigationVolumeButtom " + ex);
        }
    }

    /**
     * update navigation detail band distance text
     *
     * @param navigationDistance textView
     * @param remainingDistance  remaining distance between user position to destination.
     */

    private void updateNavigationDistance(TextView navigationDistance, int remainingDistance) {

        navigationDistance.setText(remainingDistance + " feet");
    }

    /**
     * calculate walked distance between starting position and  user position.
     *
     * @param position position of pathSegment
     * @return int
     */

    private int calculateWalkedDistannce(int position) {
        double distance = 0;
        for (int i = 0; i < position; i++) {
            distance = distance + pathInfos.get(i).getDistance();
        }
        return CommonMethods.meterToFeet(distance);
    }

    /**
     * calculate total distance between start position and destination position.
     *
     * @return int.
     */

    private int calculateTotalDistannce() {
        double distance = 0;
        for (PathSegment segment : pathInfos) {
            distance = distance + segment.getDistance();
        }
        return CommonMethods.meterToFeet(distance);
    }

    /**
     * calculate floor difference between user position floor and destination floor.
     *
     * @param pathSegment     current path segment details.
     * @param navigationFloor textView.
     */

    private void updateNavigationFloor(PathSegment pathSegment, TextView navigationFloor) {
        ImageView image_dot = (ImageView) findViewById(R.id.image_dot_1);
        int floorDistance = compareFloorforNavigation(pathSegment.getFloor(), pathInfos.get(pathInfos.size() - 1).getFloor());
        if (floorDistance == 0) {
            navigationFloor.setVisibility(View.INVISIBLE);
            image_dot.setVisibility(View.INVISIBLE);
        } else if (comparedFloorPosition.equalsIgnoreCase("up")) {
            image_dot.setVisibility(View.VISIBLE);
            navigationFloor.setVisibility(View.VISIBLE);
            if (floorDistance == 1) {
                navigationFloor.setText(" " + String.valueOf(floorDistance) + " level up ");
            } else {
                navigationFloor.setText(" " + String.valueOf(floorDistance) + " levels up ");
            }
        } else if (comparedFloorPosition.equalsIgnoreCase("down")) {
            image_dot.setVisibility(View.VISIBLE);
            navigationFloor.setVisibility(View.VISIBLE);
            if (floorDistance == 1) {
                navigationFloor.setText(" " + String.valueOf(floorDistance) + " level down ");
            } else {
                navigationFloor.setText(" " + String.valueOf(floorDistance) + " levels down ");
            }
        }
    }

    /**
     * calculate required time between user position and destination.
     *
     * @param navigationTiming TextView.
     * @param time             remaining time.
     */

    private void updateNavigationTime(TextView navigationTiming, int time) {
        if (time <= 1) {
            navigationTiming.setText("1 min away ");
        } else {
            navigationTiming.setText(String.valueOf(time) + " mins away ");
        }
    }

    /**
     * compare floor difference between start position and destination position.
     *
     * @param startFloor floor of start position.
     * @param endFloor   floor of end position.
     * @return int.
     */

    private int compareFloorforNavigation(int startFloor, int endFloor) {
        int compredFloor = 0;
        try {
            if (startFloor == endFloor) {
                compredFloor = 0;
            } else if (startFloor < endFloor) {
                compredFloor = endFloor - startFloor;
                comparedFloorPosition = "up";
            } else {
                compredFloor = startFloor - endFloor;
                comparedFloorPosition = "down";
            }
        } catch (Exception ex) {
            Log.e(TAG, " compareFloorforNavigation " + ex);
        }
        return compredFloor;
    }

    private void setFloorPopUpPosition(View view, boolean isNavigationPagerClosed) {
        View floorList_container = null;
        try {
            floorList_container = findViewById(R.id.rel_outer_floorList_container);
            if (view != null && floorList_container.getVisibility() == View.VISIBLE) {
                if (view.getVisibility() == View.VISIBLE && !isNavigationPagerClosed) {
                    animateViewHeightFromTop(floorList_container, ((RelativeLayout.LayoutParams) floorList_container.getLayoutParams()).topMargin, view.getHeight() + CommonMethods.dpToPx(getBaseContext(), 10));
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                } else {
                    animateViewHeightFromTop(floorList_container, ((RelativeLayout.LayoutParams) floorList_container.getLayoutParams()).topMargin, CommonMethods.dpToPx(getBaseContext(), 10));
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                }
            }
        } catch (Exception ex) {
     //       LoggingParseException("setFloorPopUpPosition", new Exception());
            Log.e(TAG, " setFloorPopUpPosition " + ex);
      //      Crashlytics.log(Log.ERROR, TAG, ex.toString());
        }
    }

    /**
     * update navigation pager information.
     *
     * @param pathInfo list of PathSegment.
     */

    private void updateNavigationPager(final ArrayList<PathSegment> pathInfo) {
        NavigationDetailPagerAdapterV2 adapter;
        try {
            pathInfos = pathInfo;
            adapter = new NavigationDetailPagerAdapterV2(getSupportFragmentManager(), pathInfo.size(), pathInfo, MapActivity.this);
            navigationPager = (CustomViewpager) findViewById(R.id.navigation_pager);
            navigationPager.setAdapter(adapter);
            navigationPager.getAdapter().notifyDataSetChanged();
            navigationPager.setPagingEnabled(true);
            navigationPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    if (pathInfos != null && !pathInfos.isEmpty()) {
                        PathSegment currentPathInfo = pathInfos.get(position);
                        speakOut(pathInfos.get(position).getInstructions());
                        updateNavigationCurrentDetails(currentPathInfo, position);
                    }
                    mapFragment.UpdatePathSegment(position);
                    updateFloorData(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

    /**
     * update floor data.
     *
     * @param position
     */

    private void updateFloorData(int position) {
        if (pathInfos.get(position).getFloor() != currentFloor) {
            currentFloor = pathInfos.get(position).getFloor();
            if (!uiSettings.isFloorSelectorEnabled()) {
                setSelectedFloorToText(MapActivity.this.currentFloor);
            }
        }
    }







    /**
     * Show any message with toast on ui with il_duration : Toast.LENGTH_LONG
     */
    private void showMessageOnUI(final String message) {
        mHandler.post(new Runnable() {
            public void run() {
                try {
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                } catch (Exception ex) {
                    System.err.println(ex);
                }
            }
        });
    }

    /**
     * when the right arrow button is pressed in the custom top navigation pager
     *
     * @param currentPosition the current position of top navigation pager
     * @see NavigationPagerHandler #onNextPage
     */
    @Override
    public void onNextPage(int currentPosition) {
        navigationPager.setCurrentItem(currentPosition + 1);
    }

    /**
     * when the left arrow button is pressed in the custom top navigation pager
     *
     * @param currentPosition the current position of top navigation pager
     * @see NavigationPagerHandler #onPreviousPage
     */
    @Override
    public void onPreviousPage(int currentPosition) {
        navigationPager.setCurrentItem(currentPosition - 1);
    }





    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == INTENT_SEARCH) {
                if ((resultCode == RESULT_OK)) {
                    if (data != null) {
                        if (data.hasExtra("poi")) {
                            searchPOI = data.getParcelableExtra("poi");
                            if (currentFloor != searchPOI.getArea().getFloor()) {
                                mapFragment.changeFloor(searchPOI.getArea().getFloor());
                                currentFloor = searchPOI.getArea().getFloor();
                                if (!uiSettings.isFloorSelectorEnabled()) {
                                    setSelectedFloorToText(MapActivity.this.currentFloor);
                                }
                            }
                            if (searchPOI.getPoint() != null) // is poi
                            {
                                try {
                                    removePOIMarker();
                                    CameraPosition cameraPosition = new CameraPosition.Builder().target(searchPOI.getPoint()).zoom(20).build();
                                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                    POIMarker = googleMap.addMarker(new MarkerOptions().title(searchPOI.getPoiName()).position(searchPOI.getPoint()));
                                    POIMarker.showInfoWindow();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else //is store
                            {
                                try {
                                    onMapClick(searchPOI.getArea().getBounds().getCenter(), searchPOI.getArea());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     *
     */

    @Override
    public void onBackPressed() {
//        try {
//            if (mapAreaBand != null && mapAreaBand.getVisibility() == View.VISIBLE) {
//                handleHideMapAreaDetailBand();
//                return;
//            }
//            if (topNavigationDetailContainer != null && topNavigationDetailContainer.getVisibility() == View.VISIBLE) {
//                handleHideTopNavigationDetailLayout();
//                return;
//            }
//            if (navigationContainer != null && navigationContainer.getVisibility() == View.VISIBLE) {
//                exitNavigation();
//                return;
//            }
//            if (isNavigationOngoing && reCenter.getVisibility() == View.VISIBLE) {
//                mapFragment.resumeNavigation();
//                CommonMethods.hideCircularRevealAnimation(reCenter);
//                CommonMethods.showCircularRevealAnimation(navigationContainer);
//                return;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        super.onBackPressed();
    }

    /**
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.si_in_lb__map_screen);
        try {

            activityHandler = new Handler();
            activityR = new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                  //  Toast.makeText(MapActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                   // finish();
                    if (!isRendering)
                    {
                        exitNavigation();
                        videoOverlay.setVisibility(View.VISIBLE);
                        vv.start();
                    }

                }
            };
            startHandler();


            Fabric.with(this, new Crashlytics());
            initializeMapData();
            initiailizeActivityControls();
            initialializeIntentData();
            checkForUpdates();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserInteraction(){
      //  MyTimerClass.getInstance().resetTimer();
        stopHandler();//stop first and then start
        startHandler();
    }


    public void stopHandler() {
        activityHandler.removeCallbacks(activityR);
    }
    public void startHandler() {
        activityHandler.postDelayed(activityR, 15*1000); //for 5 minutes
    }

    @Override
    public void onResume() {
        super.onResume();
        // ... your own onResume implementation
        checkForCrashes();
    }

    private void initialializeIntentData() {
        try {
            if (getIntent().hasExtra("storeID")) {
                locateStoreID = getIntent().getExtras().getString("storeID");
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

    /**
     * initialize map data.
     */

    private void initializeMapData() {
        try {
            //Custom Markers
            setStatusBarColor(R.color.si_in_lb__black);
            mapFragment = (IntripperMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_container);
            mapFragment.setMapListener(this);
            customizeUserPositionMarker();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * initialize intripper location provider.
     */



    /**
     * initialize floor list popup.
     */
    private void bindFloorlistPopupMenu() {
        CustomPopUpFloorListAdapterV2 floorListAdapter;
        int height = 0;
        ArrayList<FloorInfo> floorListArray = venueInfo.getFloors();
        try {
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popUpView = inflater.inflate(R.layout.si_in_lb__floor_list_popup, null, false);
            if (floorListArray.size() > 0) {
                int calheight = 40 * floorListArray.size();
                height = (CommonMethods.dpToPx(getBaseContext(), calheight));
               /* if (floorListArray.size() > 5) {
                    height = (CommonMethods.dpToPx(getBaseContext(), 320));
                } else {
                    int calheight = 40 * floorListArray.size();
                    height = (CommonMethods.dpToPx(getBaseContext(), calheight));
                }*/
            }
            floorPopupUpWindow = new PopupWindow(popUpView, CommonMethods.dpToPx(getBaseContext(), 50), height, true);
            floorPopupUpWindow.setContentView(popUpView);
            floorPopupUpWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.si_in_lb__transparent)));
            floorPopupUpWindow.setOutsideTouchable(true);
            floorPopupUpWindow.setAnimationStyle(R.style.si_in_lb__floorAnimation);
            floorPopupUpWindow.showAsDropDown(findViewById(R.id.rel_floorlist_container), 0, 1);
            customDropDownFloorlist = (FloorListView) popUpView.findViewById(R.id.floor_list);
            if (floorListArray.size() > 0) {
                floorListAdapter = new CustomPopUpFloorListAdapterV2(getApplicationContext(), R.layout.si_in_lb__custom_popup_floorlist_item, floorListArray, this.currentFloor, this.positioningFloor);
                customDropDownFloorlist.setOnItemLongClickListener(new FloorListView.OnItemLongTapListener() {
                    @Override
                    public void OnLongTap(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            vibrate();
                            hideMapAreaBandAndMarker();
                            mapFragment.changeFloor(position);
                            currentFloor = position;
                            positioningFloor = position;
                            if (!uiSettings.isFloorSelectorEnabled()) {
                                setSelectedFloorToText(MapActivity.this.currentFloor);
                            }


                            floorPopupUpWindow.dismiss();
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString() + " at bindFloorListViewOnLongTap");
                        }
                    }


                    @Override
                    public void OnSingleTap(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            // clear previous floor's marker,labels,etc & add new floor's here...
                            currentFloor = position;
                            hideMapAreaBandAndMarker();
                            if (!uiSettings.isFloorSelectorEnabled()) {
                                setSelectedFloorToText(MapActivity.this.currentFloor);
                            }

                            // change the floor map...
                            mapFragment.changeFloor(currentFloor);
                            floorPopupUpWindow.dismiss();
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString() + " at bindFloorListViewOnSingleTap");
                        }
                    }
                });
                customDropDownFloorlist.setAdapter(floorListAdapter);
            } else {
                customDropDownFloorlist.setVisibility(View.INVISIBLE);
            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * vibrates the phone.
     */
    private void vibrate() {
        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 200, 300, 400, 500};
        vibe.vibrate(pattern, -1);
    }

    /**
     * initialize all the ui components of the activity.
     */

    private void initiailizeActivityControls() {
        initializeAreaBand();
        initializeOfferBubble();
        initializeUserPositionIcon();
        initializeSearchIcon();
        initializeMapRotationIcon();
        initializeTextToSpeech();
        initializeNavigationPager();

        initializeBottomTabBar();
        initializeFindByNameView();
        initializeFindByCategoryView();
        initializeStoreContentView();
        initializeAmenitiesView();
        initializeCustomerSupportView();
        initializeVideoPreview();

    }

    /**
     * initialize area detail band.
     */

    private void initializeAreaBand() {
        mapAreaBand = (RelativeLayout) findViewById(R.id.map_area_info_container);
        startMapAreaBandImage = (ImageView) findViewById(R.id.start_map_area_icon);
        startMapAreaBandImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNavigateToStore();
            }
        });
    }

    /**
     * Initialize offerBubble icon on screen..
     */
    private void initializeOfferBubble() {
        offerBubble = (RelativeLayout) findViewById(R.id.offer_notification_bubble);
    }


    private  void  initializeVideoPreview()
    {
        videoOverlay = (RelativeLayout) findViewById(R.id.videoOverlay);

        wayFinderTextView = (TextView)findViewById(R.id.wayFinderTextView);

        wayFinderTextView.setText("W\nA\nY\nF\nI\nN\nD\nE\nR");

        wayFinderButton = (Button)findViewById(R.id.wayFinderButtonID);

        vv = (VideoView)findViewById(R.id.myVideoView);

        //Video Loop
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                vv.start(); //need to make transition seamless.
            }
        });

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.moses);

        vv.setVideoURI(uri);
        vv.requestFocus();
        vv.start();

        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //   mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                //   mp.setScreenOnWhilePlaying(false);
                mp.setLooping(true);
            }
        });

        wayFinderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    Answers.getInstance().logCustom(new CustomEvent("WAYFINDER Clicked"));

                    vv.pause();

                    videoOverlay.setVisibility(View.INVISIBLE);

//                    Intent intent;
//                    intent = new Intent( getApplicationContext(), MapActivity.class );
//                    startActivity( intent);
                    // overridePendingTransition(R.anim.open_next,R.anim.close_main);
                    // finish();
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

    }


    private void initializeBottomTabBar()
    {

        bottomTabBar = (RelativeLayout) findViewById(R.id.bottomTab);

        findByName = (Button) findViewById(R.id.findByName);
        findByCategory = (Button) findViewById(R.id.findByCategory);
        mallAmenities = (Button)findViewById(R.id.mallAmenitiesButton);
        customerSupportButton = (Button)findViewById(R.id.customerSupportButton);

        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Muli-ExtraLight.ttf");
        findByName.setTypeface(typeFace);
        findByCategory.setTypeface(typeFace);
        mallAmenities.setTypeface(typeFace);
        customerSupportButton.setTypeface(typeFace);

        palladiumImageButton = (ImageButton)findViewById(R.id.palladiumButton);
        pmcImageButton = (ImageButton)findViewById(R.id.pmcButton);
        palladiumFloorLayout = (LinearLayout)findViewById(R.id.palladiumfloors);
        pmcFloorLayout = (LinearLayout)findViewById(R.id.pmcfloors);


        Typeface typeFace1 =Typeface.createFromAsset(getAssets(),"fonts/Muli-SemiBold.ttf");
        pgButton = (Button)findViewById(R.id.pgButton);
        pugButton = (Button)findViewById(R.id.pugButton);
        p1Button = (Button)findViewById(R.id.p1Button);

        pgButton.setTypeface(typeFace1);
        pugButton.setTypeface(typeFace1);
        p1Button.setTypeface(typeFace1);

        pgButton.setBackgroundResource(R.drawable.bottomradiusselected);
        pgButton.setTextColor(getResources().getColor(R.color.floorhighlightedtext));

        lgButton = (Button)findViewById(R.id.lgButton);
        gButton = (Button)findViewById(R.id.gButton);
        l1Button = (Button)findViewById(R.id.l1Button);
        l2Button = (Button)findViewById(R.id.l2Button);
        l3Button = (Button)findViewById(R.id.l3Button);

        centercircleImage = (ImageView)findViewById(R.id.centerImage);

        //   CommonMethods.hideCircularRevealAnimation(pmcFloorLayout);



        findByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    mapFragment.resumeNavigation();
                CommonMethods.hideCircularRevealAnimation(bottomTabBar);
                CommonMethods.showCircularRevealAnimation(findByNameView);

             //   findByViewAreas =  (ArrayList<MapArea>) venueInfo.getMapAreas().clone();


                Answers.getInstance().logCustom(new CustomEvent("Find By Name Clicked"));

                findByViewAreas = new ArrayList<MapArea>();

               ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();


//                for (MapArea wp : dummyAreas) {
//                    if (wp.getLevelDec().toLowerCase()
//                            .contains("pg") || wp.getLevelDec().toLowerCase()
//                            .contains("pug") || wp.getLevelDec().toLowerCase()
//                            .contains("p1")) {
//                        findByViewAreas.add(wp);
//                    }
//                }

//                for (MapArea wp : dummyAreas) {
//                    if (wp.getLogoUrl().length()>0) {
//                        findByViewAreas.add(wp);
//                    }
//                }

                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }
//


                customGridadapter = new CustomGrid(getApplicationContext(), findByViewAreas);
                // customGridadapter.notifyDataSetChanged();
                grid.setAdapter(customGridadapter);
                editsearch.setText("");
            }
        });

        findByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    mapFragment.resumeNavigation();
                CommonMethods.hideCircularRevealAnimation(bottomTabBar);
                CommonMethods.showCircularRevealAnimation(findByCategoryView);

                Answers.getInstance().logCustom(new CustomEvent("Find By Category Clicked"));


             //   findByViewAreas =  (ArrayList<MapArea>) venueInfo.getMapAreas().clone();

                findByViewAreas = new ArrayList<MapArea>();

                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();


//                for (MapArea wp : dummyAreas) {
//                    if (wp.getLevelDec().toLowerCase()
//                            .contains("pg")) {
//                        findByViewAreas.add(wp);
//                    }
//                }

                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }



                customGridadapter = new CustomGrid(getApplicationContext(), findByViewAreas);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = findByViewAreas.get( position );

                        destinationArea = findByViewAreas.get(position);

                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);


                        UpdateStoreContent(area);

                    }
                });

            }
        });

        mallAmenities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    mapFragment.resumeNavigation();
                CommonMethods.hideCircularRevealAnimation(bottomTabBar);
                CommonMethods.showCircularRevealAnimation(amenitiesView);

                Answers.getInstance().logCustom(new CustomEvent("Mall Amenities Clicked"));


                findByViewAreas = new ArrayList<MapArea>();

                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();

                for (MapArea wp : dummyAreas) {
                    if (wp.getLevelDec().toLowerCase()
                            .contains("pg") || wp.getLevelDec().toLowerCase()
                            .contains("pug") || wp.getLevelDec().toLowerCase()
                            .contains("p1")) {
                        findByViewAreas.add(wp);
                    }
                }





                amenitiesRecyclerView.setHasFixedSize(true);
                LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                amenitiesRecyclerView.setLayoutManager(llm);

//                AmenitiesDataModal amenitiesDataModal = new AmenitiesDataModal("ATM",findByViewAreas);
                   amenitiesData = new ArrayList<>();
//                amenitiesData.add(amenitiesDataModal);



                ArrayList<MapArea> toiletAreas = new ArrayList<MapArea>();


                for (MapArea wp : findByViewAreas) {
                    if (wp.getName().contains("Restroom")) {
                        toiletAreas.add(wp);
                    }
                }

                AmenitiesDataModal  amenitiesDataModal = new AmenitiesDataModal("Toilets",toiletAreas);
                amenitiesData.add(amenitiesDataModal);

//                amenitiesDataModal = new AmenitiesDataModal("Artwork",findByViewAreas);
//                amenitiesData.add(amenitiesDataModal);


                //

                ArrayList<MapArea> elevatorsAreas = new ArrayList<MapArea>();


                for (MapArea wp : findByViewAreas) {
                    if (wp.getName().contains("Elevator")) {
                        elevatorsAreas.add(wp);
                    }
                }

                amenitiesDataModal = new AmenitiesDataModal("Elevators",elevatorsAreas);
                amenitiesData.add(amenitiesDataModal);

                verticalAmenitiesAdapter = new VerticalAmenitiesAdapter(getApplicationContext(), amenitiesData,MapActivity.this);
                amenitiesRecyclerView.setAdapter(verticalAmenitiesAdapter);

//                customGridadapter = new CustomGrid(getApplicationContext(), findByViewAreas);
//                gridCategoryStores.setAdapter(customGridadapter);
            }
        });

        customerSupportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    mapFragment.resumeNavigation();
                nameEditText.setText("");
                phoneEditText.setText("");
                qweryEditText.setText("");
                CommonMethods.hideCircularRevealAnimation(bottomTabBar);
                CommonMethods.showCircularRevealAnimation(customerSupprotView);

                Answers.getInstance().logCustom(new CustomEvent("Customer Support Clicked"));
            }
        });

        palladiumImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                CommonMethods.hideCircularRevealAnimation(pmcFloorLayout);
                CommonMethods.showCircularRevealAnimation(palladiumFloorLayout);

                Answers.getInstance().logCustom(new CustomEvent("Home Button Clicked"));

            }

        });

        pmcImageButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {


                CommonMethods.hideCircularRevealAnimation(palladiumFloorLayout);
                CommonMethods.showCircularRevealAnimation(pmcFloorLayout);

            }

        });

        pgButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                pgButton.setBackgroundResource(R.drawable.bottomradiusselected);
                pgButton.setTextColor(getResources().getColor(R.color.floorhighlightedtext));

                pugButton.setBackgroundColor(getResources().getColor(R.color.si_in_lb__white));
                pugButton.setTextColor(getResources().getColor(R.color.floortext));

                p1Button.setBackgroundResource(R.drawable.borderradius);
                p1Button.setTextColor(getResources().getColor(R.color.floortext));

                ArrayList<FloorInfo> floorListArray = venueInfo.getFloors();

                for (int i=0;i<floorListArray.size();i++)
                {
                    if (floorListArray.get(i).getName().equals("PG"))
                    {
                        currentFloor = i;
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);
                        // floorPopupUpWindow.dismiss();
                    }
                }


            }

        });

        pugButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                pgButton.setBackgroundResource(R.drawable.bottomradius);
                pgButton.setTextColor(getResources().getColor(R.color.floortext));

                pugButton.setBackgroundColor(getResources().getColor(R.color.floorhighlighted));
                pugButton.setTextColor(getResources().getColor(R.color.floorhighlightedtext));

                p1Button.setBackgroundResource(R.drawable.borderradius);
                p1Button.setTextColor(getResources().getColor(R.color.floortext));

                ArrayList<FloorInfo> floorListArray = venueInfo.getFloors();

                for (int i=0;i<floorListArray.size();i++)
                {
                    if (floorListArray.get(i).getName().equals("PUG"))
                    {
                        currentFloor = i;
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);
                        // floorPopupUpWindow.dismiss();
                    }
                }


            }

        });

        p1Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                pgButton.setBackgroundResource(R.drawable.bottomradius);
                pgButton.setTextColor(getResources().getColor(R.color.floortext));

                pugButton.setBackgroundColor(getResources().getColor(R.color.si_in_lb__white));
                pugButton.setTextColor(getResources().getColor(R.color.floortext));

                p1Button.setBackgroundResource(R.drawable.topradiusselected);
                p1Button.setTextColor(getResources().getColor(R.color.floorhighlightedtext));

                ArrayList<FloorInfo> floorListArray = venueInfo.getFloors();

                for (int i=0;i<floorListArray.size();i++)
                {
                    if (floorListArray.get(i).getName().equals("P1"))
                    {
                        currentFloor = i;
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);
                        // floorPopupUpWindow.dismiss();
                    }
                }


            }

        });

        lgButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                ArrayList<FloorInfo> floorListArray = venueInfo.getFloors();

                for (int i=0;i<floorListArray.size();i++)
                {
                    if (floorListArray.get(i).getName().equals("LG"))
                    {
                        currentFloor = i;
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);
                        // floorPopupUpWindow.dismiss();
                    }
                }


            }

        });

        gButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                ArrayList<FloorInfo> floorListArray = venueInfo.getFloors();

                for (int i=0;i<floorListArray.size();i++)
                {
                    if (floorListArray.get(i).getName().equals("G"))
                    {
                        currentFloor = i;
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);
                        // floorPopupUpWindow.dismiss();
                    }
                }


            }

        });

        l1Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                ArrayList<FloorInfo> floorListArray = venueInfo.getFloors();

                for (int i=0;i<floorListArray.size();i++)
                {
                    if (floorListArray.get(i).getName().equals("L1"))
                    {
                        currentFloor = i;
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);
                        // floorPopupUpWindow.dismiss();
                    }
                }


            }

        });

        l2Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                ArrayList<FloorInfo> floorListArray = venueInfo.getFloors();

                for (int i=0;i<floorListArray.size();i++)
                {
                    if (floorListArray.get(i).getName().equals("L2"))
                    {
                        currentFloor = i;
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);
                        // floorPopupUpWindow.dismiss();
                    }
                }


            }

        });

        l3Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                ArrayList<FloorInfo> floorListArray = venueInfo.getFloors();

                for (int i=0;i<floorListArray.size();i++)
                {
                    if (floorListArray.get(i).getName().equals("L3"))
                    {
                        currentFloor = i;
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);
                        // floorPopupUpWindow.dismiss();
                    }
                }


            }

        });


        centercircleImage.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

              //  mapFragment.changeFloor(5);

                currentFloor = 5;
                hideMapAreaBandAndMarker();
                if (!uiSettings.isFloorSelectorEnabled()) {
                    setSelectedFloorToText(MapActivity.this.currentFloor);
                }

                // change the floor map...
                mapFragment.changeFloor(currentFloor);

                pgButton.setBackgroundResource(R.drawable.bottomradiusselected);
                pgButton.setTextColor(getResources().getColor(R.color.floorhighlightedtext));

                pugButton.setBackgroundColor(getResources().getColor(R.color.si_in_lb__white));
                pugButton.setTextColor(getResources().getColor(R.color.floortext));

                p1Button.setBackgroundResource(R.drawable.borderradius);
                p1Button.setTextColor(getResources().getColor(R.color.floortext));

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mapFragment.getBaseMap().animateCamera(CameraUpdateFactory.newLatLngZoom(venueInfo.getCenter(),venueInfo.getMinZoom()));
                    }
                }, 2000);
            }

        });



    }

    private void initializeFindByNameView()
    {

        initializeTopNavigationBarControls();

        findHeaderImage = (ImageView) findViewById(R.id.findHeaderImage);

        findByNameView = (RelativeLayout) findViewById(R.id.findByNameLayout);

        try {

            grid=(GridView)findViewById(R.id.gridview);
         //  View emptyView = getLayoutInflater().inflate(R.layout.empty_gridview,null,false);
            grid.setEmptyView(findViewById(R.id.empty_gridview));
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    //         Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                    CommonMethods.hideCircularRevealAnimation(findByNameView);
                    CommonMethods.showCircularRevealAnimation(storeContentView);

                    final MapArea    area;
                    area = findByViewAreas.get( position );

                    destinationArea = findByViewAreas.get(position);
                    currentMapArea = area;

                    currentFloor = area.getFloor();
                    hideMapAreaBandAndMarker();
                    if (!uiSettings.isFloorSelectorEnabled()) {
                        setSelectedFloorToText(MapActivity.this.currentFloor);
                    }

                    // change the floor map...
                    mapFragment.changeFloor(currentFloor);


                    showMapAreaDuplicate(true);
//                    CommonMethods.hideCircularRevealAnimation(bottomTabBar);
//                    CommonMethods.hideCircularRevealAnimation(findByNameView);
//                    CommonMethods.hideCircularRevealAnimation(findByCategoryView);
//                    CommonMethods.hideCircularRevealAnimation(amenitiesView);
//                    CommonMethods.hideCircularRevealAnimation(customerSupprotView);
//                    CommonMethods.showCircularRevealAnimation(storeContentView);

                    UpdateStoreContent(area);

                }
            });

        } catch (Exception ex) {
            System.err.println(ex);
        }

        // Locate the EditText in listview_main.xml
        editsearch = (EditText) findViewById(R.id.searchEditText);

      //  editsearch.setImeActionLabel("Done", EditorInfo.IME_ACTION_DONE);

        // Capture Text in EditText
        editsearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
                String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
                customGridadapter.filter(text);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
                // TODO Auto-generated method stub
            }
        });

        closeFindByName = (Button) findViewById(R.id.closeFindByView);
        closeFindByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    mapFragment.resumeNavigation();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);
                CommonMethods.hideCircularRevealAnimation(findByNameView);
                CommonMethods.showCircularRevealAnimation(bottomTabBar);
            }
        });

                findHeaderImage.setOnTouchListener(new View.OnTouchListener() {

        Handler handler = new Handler();

        int numberOfTaps = 0;
        long lastTapTimeMs = 0;
        long touchDownMs = 0;

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchDownMs = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_UP:
                    handler.removeCallbacksAndMessages(null);

                    if ((System.currentTimeMillis() - touchDownMs) > ViewConfiguration.getTapTimeout()) {
                        //it was not a tap

                        numberOfTaps = 0;
                        lastTapTimeMs = 0;
                        break;
                    }

                    if (numberOfTaps > 0
                            && (System.currentTimeMillis() - lastTapTimeMs) < ViewConfiguration.getDoubleTapTimeout()) {
                        numberOfTaps += 1;
                    } else {
                        numberOfTaps = 1;
                    }

                    lastTapTimeMs = System.currentTimeMillis();

                    if (numberOfTaps == 1)
                    {
                      //  Toast.makeText(getApplicationContext(), "triple", Toast.LENGTH_SHORT).show();
                    }

                    if (numberOfTaps == 5) {
                     //   Toast.makeText(getApplicationContext(), "Five", Toast.LENGTH_SHORT).show();

                        final EditText txtUrl = new EditText(MapActivity.this);

                        txtUrl.setHint("Enter Admin Passcode");

                        new android.app.AlertDialog.Builder(MapActivity.this)
                                .setTitle("Exit Kiosk")
                                .setMessage("Type the passcode below to exit kiosk mode")
                                .setView(txtUrl)
                                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String url = txtUrl.getText().toString();

                                        if (url.contains("9833218480"))
                                        {
                                            android.os.Process.killProcess(android.os.Process.myPid());
                                            System.exit(1);

                                        }

                                        //  moustachify(null, url);
                                    }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                })
                                .show();

                        //handle triple tap
                    } else if (numberOfTaps == 2) {
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //handle double tap
                             //   Toast.makeText(getApplicationContext(), "double", Toast.LENGTH_SHORT).show();
                            }
                        }, ViewConfiguration.getDoubleTapTimeout());
                    }
            }

            return true;
        }
    });

    }

    private void initializeFindByCategoryView()
    {


        findByCategoryView = (RelativeLayout) findViewById(R.id.findByCategoryLayout);

        try {

            gridCategoryStores=(GridView)findViewById(R.id.gridviewcategory);
            gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                  //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                  //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                  //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                    CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                    CommonMethods.showCircularRevealAnimation(storeContentView);

                    final MapArea    area;
                    area = findByViewAreas.get( position );

                    destinationArea = findByViewAreas.get(position);

                    currentMapArea = area;

                    currentFloor = area.getFloor();
                    hideMapAreaBandAndMarker();
                    if (!uiSettings.isFloorSelectorEnabled()) {
                        setSelectedFloorToText(MapActivity.this.currentFloor);
                    }

                    // change the floor map...
                    mapFragment.changeFloor(currentFloor);

                    showMapAreaDuplicate(true);


                    UpdateStoreContent(area);

                }
            });

        } catch (Exception ex) {
            System.err.println(ex);
        }



        closeFindByCategory = (Button) findViewById(R.id.closeFindByCategory);
        closeFindByCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    mapFragment.resumeNavigation();
                CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                CommonMethods.showCircularRevealAnimation(bottomTabBar);
            }
        });

        lingerieandinnerwear = (Button) findViewById(R.id.Lingerie_Innerwears);
        lingerieandinnerwear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }

                selectedCategory = new ArrayList<MapArea>();

                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Lingerie & Innerwears")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);

                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

        footfashionbagslugguageandaccessories = (Button) findViewById(R.id.Foot_Fashion_Bags_Luggage_Accessories);
        footfashionbagslugguageandaccessories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }
                selectedCategory = new ArrayList<MapArea>();
                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Foot Fashion Bags Luggage & Accessories")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

        departmentstores = (Button) findViewById(R.id.Department_Stores);
        departmentstores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }

                selectedCategory = new ArrayList<MapArea>();
                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Department Stores")||wp.getCategories().contains("Mini Department Store")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

//        fashionandaccessories = (Button) findViewById(R.id.Fashion_Accessories);
//        fashionandaccessories.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                findByViewAreas = new ArrayList<MapArea>();
//                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
//                for (MapArea wp : dummyAreas) {
//                    if (wp.getLogoUrl().length()>0) {
//                        findByViewAreas.add(wp);
//                    }
//                }
//                selectedCategory = new ArrayList<MapArea>();
//                for (MapArea wp : findByViewAreas) {
//                    if (wp.getCategories().contains("Fashion & Accessories")) {
//                        selectedCategory.add(wp);
//                    }
//                }
//                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
//                gridCategoryStores.setAdapter(customGridadapter);
//
//                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view,
//                                            int position, long id) {
//                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();
//
//                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);
//
//                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
//                        CommonMethods.showCircularRevealAnimation(storeContentView);
//
//                        final MapArea    area;
//                        area = selectedCategory.get( position );
//
//                        destinationArea = selectedCategory.get(position);
//
//                        UpdateStoreContent(area);
//
//                    }
//                });
//            }
//        });

        cafesrestaurantsdesserts = (Button) findViewById(R.id.cafes_restaurants_desserts_gourmet);
        cafesrestaurantsdesserts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }
                 selectedCategory = new ArrayList<MapArea>();
                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Cafes Restaurants Desserts & Gourmet")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

        cosmaticssalonandspa = (Button) findViewById(R.id.cosmetics_salon_spas_optics);
        cosmaticssalonandspa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }
                selectedCategory = new ArrayList<MapArea>();
                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Cosmetics Salon Spas & Optics")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

        jewellery = (Button) findViewById(R.id.Jewellery);
        jewellery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }
                selectedCategory = new ArrayList<MapArea>();
                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Jewellery")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

        womensfashion = (Button) findViewById(R.id.Womens_Fashion);
        womensfashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }
                selectedCategory = new ArrayList<MapArea>();
                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Women's Fashion")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

        homeaccessoriesgifsandhobbies = (Button) findViewById(R.id.home_accessories_gifts_hobbies);
        homeaccessoriesgifsandhobbies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }
                 selectedCategory = new ArrayList<MapArea>();
                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Home Accessories Gifts & Hobbies")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

//        minidepartmentstores = (Button) findViewById(R.id.Mini_Department_Store);
//        minidepartmentstores.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                findByViewAreas = new ArrayList<MapArea>();
//                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
//                for (MapArea wp : dummyAreas) {
//                    if (wp.getLogoUrl().length()>0) {
//                        findByViewAreas.add(wp);
//                    }
//                }
//                selectedCategory = new ArrayList<MapArea>();
//                for (MapArea wp : findByViewAreas) {
//                    if (wp.getCategories().contains("Mini Department Store")) {
//                        selectedCategory.add(wp);
//                    }
//                }
//                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
//                gridCategoryStores.setAdapter(customGridadapter);
//
//                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                    @Override
//                    public void onItemClick(AdapterView<?> parent, View view,
//                                            int position, long id) {
//                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();
//
//                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);
//
//                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
//                        CommonMethods.showCircularRevealAnimation(storeContentView);
//
//                        final MapArea    area;
//                        area = selectedCategory.get( position );
//
//                        destinationArea = selectedCategory.get(position);
//
//                        UpdateStoreContent(area);
//
//                    }
//                });
//            }
//        });

        generalfashion = (Button) findViewById(R.id.General_Fashion);
        generalfashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }
               selectedCategory = new ArrayList<MapArea>();
                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("General Fashion")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

        mensfashion = (Button) findViewById(R.id.Mens_Fashion);
        mensfashion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }
                selectedCategory = new ArrayList<MapArea>();
                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Men's Fashion")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

        watchesfashionaccessories = (Button) findViewById(R.id.watches_fashion_accessories);
        watchesfashionaccessories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }

               selectedCategory = new ArrayList<MapArea>();


                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Watches & Fashion Accessories")||wp.getCategories().contains("Fashion & Accessories")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

        sportswear = (Button) findViewById(R.id.Sportswear_Sportsgear);
        sportswear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }
                 selectedCategory = new ArrayList<MapArea>();
                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Sportswear & Sportsgear")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });

        pensbookmusictoys = (Button) findViewById(R.id.Pens_Books_Music_Toys_Games);
        pensbookmusictoys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                findByViewAreas = new ArrayList<MapArea>();
                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
                for (MapArea wp : dummyAreas) {
                    if (wp.getLogoUrl().length()>0) {
                        findByViewAreas.add(wp);
                    }
                }
                 selectedCategory = new ArrayList<MapArea>();
                for (MapArea wp : findByViewAreas) {
                    if (wp.getCategories().contains("Pens Books Music Toys & Games")) {
                        selectedCategory.add(wp);
                    }
                }
                customGridadapter = new CustomGrid(getApplicationContext(), selectedCategory);
                gridCategoryStores.setAdapter(customGridadapter);

                gridCategoryStores.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //  Toast.makeText(getApplicationContext(), "You Clicked at " +findByViewAreas.get(position).getName(), Toast.LENGTH_SHORT).show();

                        //  InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        //  imm.hideSoftInputFromWindow(editsearch.getWindowToken(), 0);

                        CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                        CommonMethods.showCircularRevealAnimation(storeContentView);

                        final MapArea    area;
                        area = selectedCategory.get( position );

                        destinationArea = selectedCategory.get(position);
                        currentMapArea = area;

                        currentFloor = area.getFloor();
                        hideMapAreaBandAndMarker();
                        if (!uiSettings.isFloorSelectorEnabled()) {
                            setSelectedFloorToText(MapActivity.this.currentFloor);
                        }

                        // change the floor map...
                        mapFragment.changeFloor(currentFloor);

                        showMapAreaDuplicate(true);

                        UpdateStoreContent(area);

                    }
                });
            }
        });



    }

    private void initializeStoreContentView() {


        storeContentView = (RelativeLayout) findViewById(R.id.storecontentlayout);

        storeContentBackButton = (Button) findViewById(R.id.storecontentbackbutton);
        storeContentBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    mapFragment.resumeNavigation();
                CommonMethods.hideCircularRevealAnimation(storeContentView);
                CommonMethods.showCircularRevealAnimation(userPositionIcon);
                removePOIMarker();
                handleHideMapAreaDetailBand();
                CommonMethods.showCircularRevealAnimation(bottomTabBar);

            }
        });

        locateStoreButton = (Button) findViewById(R.id.locatestorebutton);
        Typeface typeFace2=Typeface.createFromAsset(getAssets(),"fonts/Muli-Bold.ttf");
        locateStoreButton.setTypeface(typeFace2);
        locateStoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //    mapFragment.resumeNavigation();
                //   CommonMethods.hideCircularRevealAnimation(storeContentView);
                //   CommonMethods.showCircularRevealAnimation(findByNameView);

              //  sourceArea = destinationAreas.get(0);

//                ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
//
//                for (MapArea wp : dummyAreas) {
//                    if (wp.getName().toLowerCase()
//                            .contains("h&m")) {
//                      //  findByViewAreas.add(wp);
//                        sourceArea = wp;
//                    }
//                }

                CommonMethods.hideCircularRevealAnimation(bottomTabBar);
                CommonMethods.hideCircularRevealAnimation(storeContentView);


                removePOIMarker();
              //  handleHideMapAreaDetailBand();

                CommonMethods.hideView(mapAreaBand, R.anim.si_in_lb__fadeout_bottom, getApplicationContext());
                // CommonMethods.hideCircularRevealAnimation(userPositionIcon);
                CommonMethods.hideCircularRevealAnimation(navigationIcon);
                CommonMethods.hideCircularRevealAnimation(search_icon);
                CommonMethods.hideCircularRevealAnimation(storeContentView);
                mapAreaMarker.hideInfoWindow();

              //  isNavigationOngoing = true;

                if (navigateSourceToDestination(sourceArea, destinationArea)) {
                    return;
                }

            }
        });

        storeNameLabel = (TextView)findViewById(R.id.storenamelabel);
        Typeface typeFace=Typeface.createFromAsset(getAssets(),"fonts/Muli-SemiBold.ttf");
        storeNameLabel.setTypeface(typeFace);

        storeDescriptionLabel = (TextView)findViewById(R.id.storedescription);
        Typeface typeFace1=Typeface.createFromAsset(getAssets(),"fonts/Muli-Regular.ttf");
        storeDescriptionLabel.setTypeface(typeFace1);

        contentImageView = (ImageView)findViewById(R.id.contentImageView);

    }

    private void UpdateStoreContent(MapArea mapArea)
    {

        storeNameLabel.setText(mapArea.getName());

        storeDescriptionLabel.setText(mapArea.getDescription());

        String logoUrl = mapArea.getLogoUrl();

        String logoName = mapArea.getName().toLowerCase().trim();

        logoName = logoName.replace(" ", "");

        logoName = logoName.replace("&", "");

        Log.e("Logo Name",logoName);

        contentImageView.setImageResource(this.getResources().getIdentifier(logoName, "drawable", this.getPackageName()));

        int checkExistence = this.getResources().getIdentifier(logoName, "drawable", this.getPackageName());

        if ( checkExistence != 0 ) {  // the resouce exists...
            contentImageView.setImageResource(this.getResources().getIdentifier(logoName, "drawable", this.getPackageName()));
        }
        else {  // checkExistence == 0  // the resouce does NOT exist!!
            //  result = false;

            if (logoUrl.length()>0)
            {

                Picasso.with(this)
                        .load(logoUrl)
                        .placeholder(R.drawable.gridplaceholder)
                        .resize(100, 100)
                        .centerCrop()
                        .into(contentImageView);
            }

            else
            {
                contentImageView.setImageResource(R.drawable.gridplaceholder);
            }
        }




    }

    private void initializeAmenitiesView() {

        amenitiesView = (RelativeLayout) findViewById(R.id.amenitiesid);

        amenitiesRecyclerView = (RecyclerView)findViewById(R.id.vertical_list);

        amenitiesBack = (Button)findViewById(R.id.amenitiesBack);

        amenitiesBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CommonMethods.hideCircularRevealAnimation(amenitiesView);
                CommonMethods.showCircularRevealAnimation(bottomTabBar);

            }
        });

    }

    private void initializeCustomerSupportView() {

        customerSupprotView = (RelativeLayout) findViewById(R.id.customerSupport);

        nameEditText = (EditText) findViewById(R.id.nameEditText);

        phoneEditText = (EditText) findViewById(R.id.phoneEditText);

        qweryEditText = (EditText) findViewById(R.id.qweryEditText);

        qweryEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

        qweryEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);

        submitButtonCustomerSupport = (Button)findViewById(R.id.submitButton);

        customerSupportBack = (Button)findViewById(R.id.customerSupportBack);

        customerSupportBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CommonMethods.hideCircularRevealAnimation(customerSupprotView);
                CommonMethods.showCircularRevealAnimation(bottomTabBar);

            }
        });


        submitButtonCustomerSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  CommonMethods.hideCircularRevealAnimation(customerSupprotView);
              //  CommonMethods.showCircularRevealAnimation(bottomTabBar);

                if (nameEditText.getText().length()==0)
                {
                    Toast.makeText(MapActivity.this, "Please enter your name", Toast.LENGTH_LONG).show();
                }
                else if (!isValidPhoneNumber(phoneEditText.getText()))
                {
                        Toast.makeText(MapActivity.this, "Invalid Number", Toast.LENGTH_LONG).show();
                }
                else if (phoneEditText.getText().length()<10)
                {
                    Toast.makeText(MapActivity.this, "Invalid Number", Toast.LENGTH_LONG).show();
                }
                else if (qweryEditText.getText().length()==0)

                {
                    Toast.makeText(MapActivity.this, "Please enter a query", Toast.LENGTH_LONG).show();
                }

                else
                {
                    db.addDetails(nameEditText.getText().toString(),phoneEditText.getText().toString(),qweryEditText.getText().toString());
                    nameEditText.setText("");
                    phoneEditText.setText("");
                    qweryEditText.setText("");
                    Toast.makeText(MapActivity.this, "Query submitted successfully", Toast.LENGTH_LONG).show();
                    CommonMethods.hideCircularRevealAnimation(customerSupprotView);
                    CommonMethods.showCircularRevealAnimation(bottomTabBar);
                }


            }
        });

        // amenitiesRecyclerView = (RecyclerView)findViewById(R.id.vertical_list);



    }

    private boolean isValidPhoneNumber(CharSequence phoneNumber) {
        if (!TextUtils.isEmpty(phoneNumber)) {
            return Patterns.PHONE.matcher(phoneNumber).matches();
        }
        return false;
    }



    /**
     * Initialize user position finder icon on screen..
     */
    private void initializeUserPositionIcon() {


        //positioningFloo




        userPositionIcon = (RelativeLayout) findViewById(R.id.user_position_anchor);
        userPositionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.goToUserPosition(18.10f);

                if(positioningFloor==5)
                {
                    pgButton.setBackgroundResource(R.drawable.bottomradiusselected);
                    pgButton.setTextColor(getResources().getColor(R.color.floorhighlightedtext));

                    pugButton.setBackgroundColor(getResources().getColor(R.color.si_in_lb__white));
                    pugButton.setTextColor(getResources().getColor(R.color.floortext));

                    p1Button.setBackgroundResource(R.drawable.borderradius);
                    p1Button.setTextColor(getResources().getColor(R.color.floortext));
                }

                if(positioningFloor==6)
                {
                    pgButton.setBackgroundResource(R.drawable.bottomradius);
                    pgButton.setTextColor(getResources().getColor(R.color.floortext));

                    pugButton.setBackgroundColor(getResources().getColor(R.color.floorhighlighted));
                    pugButton.setTextColor(getResources().getColor(R.color.floorhighlightedtext));

                    p1Button.setBackgroundResource(R.drawable.borderradius);
                    p1Button.setTextColor(getResources().getColor(R.color.floortext));
                }

                if(positioningFloor==7)
                {
                    pgButton.setBackgroundResource(R.drawable.bottomradius);
                    pgButton.setTextColor(getResources().getColor(R.color.floortext));

                    pugButton.setBackgroundColor(getResources().getColor(R.color.si_in_lb__white));
                    pugButton.setTextColor(getResources().getColor(R.color.floortext));

                    p1Button.setBackgroundResource(R.drawable.topradiusselected);
                    p1Button.setTextColor(getResources().getColor(R.color.floorhighlightedtext));
                }


            }
        });
       // CommonMethods.hideCircularRevealAnimation(userPositionIcon);

    }

    /**
     * initialize search icon.
     */

    private void initializeSearchIcon() {
        search_icon = (ImageView) findViewById(R.id.search_icon);
        search_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideMapAreaBandAndMarker();
                if (isNavigationOngoing){
                    exitNavigation();
                }
                openSearchScreen("");
            }
        });
    }

    /**
     * initialize map rotation icon.
     */

    private void initializeMapRotationIcon() {
        mapRotationIndicator = (ImageView) findViewById(R.id.map_rotation_indicator);
        mapRotationIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  mapRotationIndicator.setImageResource(R.drawable.si_in_lb__ic_compass_north);
              //  mapRotationIndicator.setRotation(0);


                unRotateMap();

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        CommonMethods.hideView(mapRotationIndicator, R.anim.si_in_lb__fadeout, getApplicationContext());
//                    }
//                }, 2000);
              //                    unRotateMap();
//                }
            }
        });
    }

    /**
     * initializing text to speak for navigation.
     */
    private void initializeTextToSpeech() {
        try {
            if (textToSpeech == null) {
                textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        try {
                            if (status != TextToSpeech.ERROR) {
                                textToSpeech.setLanguage(Locale.US);
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString() + " at initializeTextToSpeech");
                        }
                    }
                });
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString() + " at initializeTextToSpeech");
        }
    }

    /**
     * initialize navigation view pager.
     */

    private void initializeNavigationPager() {
        navigationContainer = (RelativeLayout) findViewById(R.id.navigation_detail_container);
        reCenter = (Button) findViewById(R.id.recenter);
        reCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapFragment.resumeNavigation();
                CommonMethods.hideCircularRevealAnimation(view);
                CommonMethods.showCircularRevealAnimation(navigationContainer);
            }
        });
    }

    /**
     * hide navigation pager container.
     * show map screen button.
     * reset floor picker view position.
     */

    private void handleHideTopNavigationDetailLayout() {
        setStatusBarColor(R.color.si_in_lb__black);
        CommonMethods.hideCircularRevealAnimation(topNavigationDetailContainer);
        CommonMethods.hideCircularRevealAnimation(navigationIcon);
        CommonMethods.hideCircularRevealAnimation(search_icon);
        CommonMethods.showCircularRevealAnimation(userPositionIcon);
        //animateViewHeightFromTop(floorChangeIcon, ((RelativeLayout.LayoutParams) floorChangeIcon.getLayoutParams()).topMargin, CommonMethods.dpToPx(getBaseContext(), 10));
    }

    /**
     * exit from navigation mode.
     */
    private void exitNavigation() {
        isRendering = false;
        CommonMethods.hideCircularRevealAnimation(navigationPager);
        CommonMethods.hideCircularRevealAnimation(navigationVolumeButton);
        CommonMethods.hideView(navigationContainer, R.anim.si_in_lb__fadeout_bottom, getApplicationContext());
        CommonMethods.showCircularRevealAnimation(userPositionIcon);
        CommonMethods.hideCircularRevealAnimation(navigationIcon);
        CommonMethods.hideCircularRevealAnimation(search_icon);
        CommonMethods.hideCircularRevealAnimation(reCenter);
        CommonMethods.showCircularRevealAnimation(bottomTabBar);
        CommonMethods.showCircularRevealAnimation(closeNavigationImage);
        CommonMethods.showCircularRevealAnimation(close_navigation_container);
        CommonMethods.showCircularRevealAnimation(mapRotationIndicator);
        setFloorPopUpPosition(findViewById(R.id.navigation_pager), true);
        hideFloorChangeMessageBand();
        setStatusBarColor(R.color.si_in_lb__black);
        mapFragment.removePlottedPath();
        //animateViewHeightFromTop(floorChangeIcon, ((RelativeLayout.LayoutParams) floorChangeIcon.getLayoutParams()).topMargin, CommonMethods.dpToPx(getBaseContext(), 10));
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    resetSourceDestinationArea();
                    startNavigationImage.setVisibility(View.VISIBLE);
                    stopNavigationImage.setVisibility(View.GONE);
                    if (userPosition != null) {
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(userPosition).tilt(0).zoom(googleMap.getMaxZoomLevel()).build();
                        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                    isNavigationOngoing = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 250);


        if (myHandler!=null)
        {
            myHandler.removeCallbacks(mRunnable);
        }


    }

    /**
     * set status bar color.
     *
     * @param colorID
     */

    private void setStatusBarColor(int colorID) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(colorID));
        }


    }

    /**
     * animate top margin of a view
     *
     * @param view        view in which animation has to be done
     * @param startHeight starting value of top margin in px
     * @param endHeight   ending value of top margin in px
     */
    private void animateViewHeightFromTop(final View view, final int startHeight, final int endHeight) {
        Animation animation;
        try {
            if (view != null) {
                animation = new Animation() {
                    @Override
                    protected void applyTransformation(float interpolatedTime, Transformation t) {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        params.topMargin = (int) ((endHeight - startHeight) * interpolatedTime + startHeight);
                        view.setLayoutParams(params);
                    }
                };
                animation.setDuration(200);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                view.startAnimation(animation);
            }
        } catch (Exception ex) {
            Log.e(TAG, " ReturnViewToOrginal " + ex);
        }
    }

    /**
     * reset source and destination area for navigation.
     */

    private void resetSourceDestinationArea() {
        try {
            destinationArea = null;
            destinationLocation.setText("");
            if (userPosition != null) {
                sourceArea = sourceAreas.get(0);
                MapSearchAutoSuggestAdapterV2 adapter = (MapSearchAutoSuggestAdapterV2) sourceLocation.getAdapter();
                sourceLocation.setAdapter(null);
                sourceLocation.setText(sourceArea.getName());
                sourceLocation.setAdapter(adapter);
            } else {
                sourceArea = null;
                sourceLocation.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * remove poi marker.
     */

    private void removePOIMarker() {
        if (POIMarker != null) {
            if (POIMarker.isInfoWindowShown()) {
                POIMarker.hideInfoWindow();
            }
            POIMarker.remove();
            POIMarker = null;
        }
    }

    /**
     * when a user clicks on a point on the map, this callback is made.
     *
     * @param latLng  the Latlng where the user has clicked on map.
     * @param mapArea returns details of Area where the user has clicked, returns null if no area is found.
     */
    @Override
    public void onMapClick(LatLng latLng, MapArea mapArea) {
        try {
            if (isNavigationOngoing) {
                return;
            }
            if (mapArea != null) {
               // handleAreaDetailBand(mapArea, true);
                // handleAreaDetailBand(mapArea, true);
               // mapArea.getCoordinates();
               // Create Poligon and render on the Map
                destinationArea = mapArea;
                currentMapArea = mapArea;
                showMapAreaDuplicate(true);
                CommonMethods.hideCircularRevealAnimation(bottomTabBar);
                CommonMethods.hideCircularRevealAnimation(findByNameView);
                CommonMethods.hideCircularRevealAnimation(findByCategoryView);
                CommonMethods.hideCircularRevealAnimation(amenitiesView);
                CommonMethods.hideCircularRevealAnimation(customerSupprotView);
                CommonMethods.showCircularRevealAnimation(storeContentView);
                UpdateStoreContent(mapArea);
            } else {
                //CommonMethods.hideCircularRevealAnimation(bottomTabBar);
             removePOIMarker();
             handleHideMapAreaDetailBand();
             CommonMethods.showCircularRevealAnimation(bottomTabBar);
            }
        } catch (Exception e) {
            CommonMethods.showCircularRevealAnimation(bottomTabBar);
            e.printStackTrace();
        }
    }

    /**
     * when a user long clicks on a point on the map, this callback is made.
     *
     * @param latLng  the Latlng where the user has long clicked on map.
     * @param mapArea returns details of Area where the user has long clicked, returns null if no area is found.
     */
    @Override
    public void onMapLongClick(LatLng latLng, MapArea mapArea) {
        try {
            if (userPosition==null)
            {
                userPosition = latLng;
                positioningFloor = currentFloor;
                mapFragment.setUserPosition(latLng, currentFloor, 10);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * rotate map to align with north position of base map.
     */

    private void unRotateMap() {
        try {
            CameraPosition cameraPositionWithBearing = new CameraPosition.Builder().target(userPosition).bearing(0).zoom(googleMap.getCameraPosition().zoom).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPositionWithBearing));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * this callback is made when changefloor method is called of IntripperMapFragment object.
     *
     * @param floor the changed floor
     */
    @Override
    public void onFloorChanged(int floor) {
        // clear previous floor's marker,labels,etc & add new floor's here...
        currentFloor = floor;
        if (!uiSettings.isFloorSelectorEnabled()) {
            setSelectedFloorToText(MapActivity.this.currentFloor);
        }
        removePOIMarker();

    }

    /**
     * for default floor list provided by Intripper sdk this callback is made when user clicks on an item of default floor band provided by intripper
     *
     * @param floor the changed floor
     */
    @Override
    public void onFloorListItemClick(int floor) {
        // to do on single click of any floor item ...
        currentFloor = floor;
    }

    /**
     * update source and destination area.
     */
    private void handleNavigateToStore() {
        destinationArea = currentMapArea;
        if (sourceArea == null) {
            if (userPosition != null) {
                sourceArea = sourceAreas.get(0);
            } else {
                showMessageOnUI("Oops! Looks like you are invisible on the map.");
                return;
            }
        }
        if (navigateSourceToDestination(sourceArea, destinationArea)) {
            return;
        }
        hideMapAreaBandAndMarker();
    }

    /**
     * for default floor list provided by Intripper sdk this callback is made when user clicks on an item of default floor band provided by intripper
     *
     * @param floor the changed floor
     */
    @Override
    public void onFloorListItemLongClick(int floor) {
        // to do on long click of any floor item ...
        // to start positioning on this floor and keep track of it...
        positioningFloor = floor;
        currentFloor = floor;

    }

    /**
     * start navigation.
     */

    private void handleStartNavigationFromBand() {

        myHandler = new Handler();

        isNavigationOngoing = true;
        CommonMethods.hideCircularRevealAnimation(startNavigationImage);
        CommonMethods.hideCircularRevealAnimation(mapRotationIndicator);
        CommonMethods.hideCircularRevealAnimation(search_icon);
        CommonMethods.hideCircularRevealAnimation(userPositionIcon);
        CommonMethods.showCircularRevealAnimation(stopNavigationImage);
        setStatusBarColor(R.color.pager_status_color);
        CommonMethods.showCircularRevealAnimation(navigationPager);
        HandleNavigationVolumeButton();
        mapFragment.startNavigation();

        myAutomatedPostion = 1;



        mRunnable = new Runnable() {
            /*
                public abstract void run ()
                    Starts executing the active part of the class' code. This method is
                    called when a thread is started that has been created with a class which
                    implements Runnable.
            */
            @Override
            public void run() {
                // Do some task on delay
                //   doTask();

                mapFragment.UpdatePathSegment(myAutomatedPostion);
                myAutomatedPostion = myAutomatedPostion + 1;
                myHandler.postDelayed(mRunnable, 3000);
                // mapFragment.UpdatePathSegment(4);
            }
        };

        myHandler.postDelayed(mRunnable, (3000));

        //animateViewHeightFromTop(floorChangeIcon, ((RelativeLayout.LayoutParams) floorChangeIcon.getLayoutParams()).topMargin, navigationPager.getHeight() + CommonMethods.dpToPx(getBaseContext(), 10));
    }

    /**
     * handle navigation view pager container
     */

    private void handleShowTopNavigationDetailLayout() {

        try {
            resetSourceDestinationArea();
            setStatusBarColor(R.color.dark_navy_blue_color_navigation);
            CommonMethods.showCircularRevealAnimation(topNavigationDetailContainer);
            CommonMethods.hideCircularRevealAnimation(navigationIcon);
            CommonMethods.hideCircularRevealAnimation(userPositionIcon);
            //animateViewHeightFromTop(floorChangeIcon, ((RelativeLayout.LayoutParams) floorChangeIcon.getLayoutParams()).topMargin, topNavigationDetailContainer.getHeight() + CommonMethods.dpToPx(getBaseContext(), 10));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * when map is ready to display,this callback is made
     *
     * @param googleMap instance of google map object used as the base map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;

       // this.googleMap.setMinZoomPreference(30.0f);
       // this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(30.0f),2000,null);
      //  this.googleMap.animateCamera();


        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.si_in_lb__info_window, null);
                TextView sectionName = (TextView) view.findViewById(R.id.map_area_name);
                sectionName.setText(marker.getTitle());
                return view;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (POIMarker != null && marker.equals(POIMarker)) {
                    if (userPosition == null) {
                        showMessageOnUI("Oops! Looks like you are invisible on the map.");
                    } else {
                        destinationArea = searchPOI.getArea();
                        destinationArea.setName(POIMarker.getTitle());

                        PathOptions myPathOptions  =    new PathOptions(MapActivity.this);
                        myPathOptions.setStartMarkerImageResource(R.drawable.startlocation);
                        myPathOptions.setEndMarkerImageResource(R.drawable.endlocation);
                        myPathOptions.setPathColor(R.color.black_semi_transparent);
                        myPathOptions.setPathBorderColor(R.color.grey_box_border_color);
                        myPathOptions.setPathWidth(5);
                       // myPathOptions.setChangeFloorMarkerImageResource(R.drawable.ic_escalator_charcoal_grey);
                        if (positioningFloor>destinationArea.getFloor())
                        {
                            myPathOptions.setChangeFloorMarkerImageResource(R.drawable.downesc);
                        }
                        else
                        {
                            myPathOptions.setChangeFloorMarkerImageResource(R.drawable.upesc);
                        }

                        mapFragment.plotPath(userPosition, POIMarker.getPosition(), positioningFloor, destinationArea.getFloor(), myPathOptions, MapActivity.this, false);
                    }
                }
            }
        });

        addEmptyTileOverlay();
        setPanRestrictions();
    }

    /***
     * Sets pan restrictions
     */
    private void setPanRestrictions(){
        LatLngBounds palladium;
        try{
            palladium = new LatLngBounds(new LatLng(12.993721, 80.216792), new LatLng(12.993721, 80.216792));
            palladium = palladium.including(new LatLng(12.993784, 80.218793));
            palladium = palladium.including(new LatLng(12.992263, 80.218718));
            palladium = palladium.including(new LatLng(12.992869, 80.216921));
            googleMap.setLatLngBoundsForCameraTarget(palladium);
        }
        catch (Exception ex){
            Log.e(TAG, ex.toString() + " at setPanRestrictions");
        }
    }

    /**
     * Adds background pattern to the map
     */
    private void addEmptyTileOverlay(){
        TileOverlay emptyTileOverlay;
        EmptyTileProvider emptyTileProvider;
        TileOverlayOptions opts;
        try{
            emptyTileProvider = new EmptyTileProvider(getApplicationContext());
            opts = emptyTileProvider.createTileOverlayOptions();
            emptyTileOverlay = googleMap.addTileOverlay(opts);
            emptyTileOverlay.setZIndex(-2);
        }
        catch (Exception ex){
            Log.e(TAG, ex.toString() + " at addEmptyTileOverlay");
        }
    }

    /**
     * to open the Search Screen
     *
     * @param searchedText
     */
    private void openSearchScreen(String searchedText) {
        Intent intent;
        try {
            intent = new Intent(getApplicationContext(), SearchScreen.class);
            intent.putExtra("searchedText", searchedText);
            startActivityForResult(intent, INTENT_SEARCH);
            overridePendingTransition(0, 0);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString() + " at openSearchScreen");
        }
    }

    /**
     * when venue info is fetched from servers,this callback is made
     *
     * @param venueInfo details of the venue. In this case the details of phoenix mall,lower parel
     */
    @Override
    public void onVenueInfoReady(VenueInfo venueInfo) {
        JSONObject userLastKnownLocation = null;
        try {
            this.venueInfo = venueInfo;
            if (mapFragment != null) {
                userLastKnownLocation = mapFragment.getUserLastLocation();
                if (userLastKnownLocation != null) {
                    this.currentFloor = userLastKnownLocation.getInt("userFloor");
                    this.positioningFloor = userLastKnownLocation.getInt("userFloor");
                    this.userPosition = new LatLng(userLastKnownLocation.getDouble("latitude"), userLastKnownLocation.getDouble("longitude"));

                }
            }
            customizeFloorBand();
            initializeTopNavigationDetailBand();
            VenueInfo venueInfoObject = (VenueInfo) CommonMethods.getPreferences(getApplicationContext(), "venueData", CommonEnvironment.PREFTYPE_SET);
            if (venueInfoObject == null) {
                CommonMethods.saveClassObjectPreferences(getApplicationContext(), "venueData", this.venueInfo);
            }

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mapFragment != null) {
                        if (!locateStoreID.isEmpty()) {
                            mapFragment.locateStore(locateStoreID);
                        }
                    }
                }
            }, 300);
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
    }

    /**
     * customizing the user position marker (blue dot) which represents location of the user.
     */
    private void customizeUserPositionMarker() {
        // for customization...
        uiSettings = mapFragment.getUiSettings();

        // to change the icon of userPositionIcon using uiSettings...
        uiSettings.setBlueDotImageResource(R.drawable.si_in_lb__custom_blue_dot); // ---- optional.
      //  uiSettings.
        //uiSettings.setBlueDotImageResource(R.drawable.user_location); // ---- optional.
        mapFragment.getUiSettings().setLabelHighlightStrokeWidth(0);
        mapFragment.getUiSettings().setLabelColor(R.color.floortext);
        mapFragment.getUiSettings().setLabelHighlightColor(R.color.floortext);
        mapFragment.getUiSettings().setEscalatorIconDrawable(R.drawable.map_escalator);
        mapFragment.getUiSettings().setElevatorIconDrawable(R.drawable.map_elevator);
        mapFragment.getUiSettings().setRestroomIconDrawable(R.drawable.map_restroom);

        mapFragment.getUiSettings().setMensRestroomIconDrawable(R.drawable.map_restroom);
        mapFragment.getUiSettings().setWomensRestroomIconDrawable(R.drawable.map_restroom);
        mapFragment.getUiSettings().setFamilyRestroomIconDrawable(R.drawable.map_restroom);
//0 to remove it entirely

    }

    /**
     * replacing the default floor band provided by intripper sdk with a custom one which matches the ui.
     */
    private void customizeFloorBand() {
        if (venueInfo != null) {
            // disable the default floor selector provided by Intripper sdk...
            mapFragment.getUiSettings().setEnableFloorSelector(false);
            initializeCustomFloorBand();
        }
    }

    /**
     * initialize navigation view pager.
     */

    private void initializeTopNavigationDetailBand() {
        navigationIcon = (RelativeLayout) findViewById(R.id.rel_get_directions);
        navigationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleShowTopNavigationDetailLayout();
            }
        });
        navigationBackImageContainer = (LinearLayout) findViewById(R.id.navigation_image_container);
        navigationBackImageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleHideTopNavigationDetailLayout();
            }
        });
        initializeTopNavigationBarControls();
    }

    /*
      * initialize data for custom floor band.
      */
    private void initializeCustomFloorBand() {
        FloorlistContainer = (RelativeLayout) findViewById(R.id.rel_floorlist_container);
        FloorlistContainer.setVisibility(View.VISIBLE);

        // set current floor name at side of floor icon...
        if (!uiSettings.isFloorSelectorEnabled() && isAppStarted == false) {
            setSelectedFloorToText(MapActivity.this.currentFloor);
            mapFragment.changeFloor(5);
            isAppStarted = true;
        }else {
            setSelectedFloorToText(MapActivity.this.currentFloor);
            mapFragment.changeFloor(MapActivity.this.currentFloor);
        }
        floorChangeIcon = (RelativeLayout) findViewById(R.id.rel_outer_floorList_container);
        floorChangeIcon.setVisibility(View.VISIBLE);
        floorChangeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              // bindFloorlistPopupMenu();
            }
        });
    }

    /**
     * Initialize activity controls.
     */
    private void initializeTopNavigationBarControls() {
        // the adapterd for source AutoCompleteTextView...
        MapSearchAutoSuggestAdapterV2 sourceAdapter;

        // the adapterd for destination AutoCompleteTextView...
        MapSearchAutoSuggestAdapterV2 destinationAdapter;

        try {
            destinationArea = new MapArea();
            sourceArea = new MapArea();
            sourceArea.setName("My Location");
            sourceArea.setAreaId("-99");
            sourceAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
            sourceAreas.add(0, sourceArea);
            destinationAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
            topNavigationDetailContainer = (RelativeLayout) findViewById(R.id.top_navigation_detail_container);
            sourceLocation = (AutoCompleteTextView) findViewById(R.id.source_location);
            sourceLocation.setThreshold(2);
            sourceLocation.setText(sourceAreas.get(0).getName());
            CommonMethods.setFontOpenSans(sourceLocation);
            CommonMethods.hideKeyboard(getApplicationContext(), sourceLocation);
            sourceAdapter = new MapSearchAutoSuggestAdapterV2(getApplicationContext(), R.layout.si_in_lb__search_list_item, sourceAreas, venueInfo);
            sourceLocation.setAdapter(sourceAdapter);
            sourceLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    MapSearchAutoSuggestAdapterV2.ViewHolder holder;
                    MapArea area;
                    try {
                        holder = (MapSearchAutoSuggestAdapterV2.ViewHolder) view.getTag();
                        area = holder.viewData;
                        sourceLocation.setText(area.getName());
                        sourceArea = area;
                        CommonMethods.hideKeyboard(getApplicationContext(), sourceLocation);
                        if (navigateSourceToDestination(sourceArea, destinationArea)) {
                            return;
                        }
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }
                }
            });
            sourceLocation.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().equalsIgnoreCase("")) {
                        sourceArea = null;
                    }
                }
            });
            destinationLocation = (AutoCompleteTextView) findViewById(R.id.destination_location);
            destinationLocation.setThreshold(2);
            CommonMethods.setFontOpenSans(destinationLocation);
            if (destinationArea != null) {
                destinationLocation.setText(destinationArea.getName());
            }
            destinationAdapter = new MapSearchAutoSuggestAdapterV2(getApplicationContext(), R.layout.si_in_lb__search_list_item, destinationAreas, venueInfo);
            destinationLocation.setAdapter(destinationAdapter);
            destinationLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    MapSearchAutoSuggestAdapterV2.ViewHolder holder;
                    MapArea area;
                    try {
                        holder = (MapSearchAutoSuggestAdapterV2.ViewHolder) view.getTag();
                        area = holder.viewData;
                        String text = "<font color=#A6A6A6>to</font>" + "  " + area.getName().trim();
                        destinationLocation.setText(Html.fromHtml(text));
                        destinationArea = area;
                        CommonMethods.hideKeyboard(getApplicationContext(), destinationLocation);
                        if (navigateSourceToDestination(sourceArea, destinationArea)) {
                            return;
                        }
                    } catch (Exception ex) {
                        System.err.println(ex);
                    }
                }
            });
            destinationLocation.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence sequence, int start, int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence sequence, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.toString().equalsIgnoreCase("")) {
                        destinationArea = null;
                    }
                }
            });
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /*
      * set current floor name at side of floor icon.
      */
    private void setSelectedFloorToText(int floor) {
        TextView selectedFloor;
        try {
            selectedFloor = (TextView) findViewById(R.id.selected_level);
            if (selectedFloor.getVisibility() == View.GONE) {
                selectedFloor.setVisibility(View.VISIBLE);
            }
            if (floor == 0) {
                String floorName = venueInfo.getFloors().get(floor).getName();
                selectedFloor.setText("");
             //   selectedFloor.setText("LEVEL " + floorName);
            } else {
         //       selectedFloor.setText("LEVEL " + String.valueOf(floor));
                selectedFloor.setText("");
             //   selectedFloor.setText("LEVEL " + venueInfo.getFloors().get(floor).getName());
            }
            selectedFloor.setShadowLayer(1, 3, 3, getResources().getColor(R.color.si_in_lb__white));
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * navigate from source location to destination of top Navigation detail layout if both areas are not null
     *
     * @param sourceArea      starting MapArea location
     * @param destinationArea ending MapArea location
     * @return true if user position is not set.
     */
    private boolean navigateSourceToDestination(MapArea sourceArea, MapArea destinationArea) {
        if (sourceArea != null && destinationArea != null) {
            if (sourceArea.getName().equals("My Location")) {
                if (userPosition == null) {
                    showMessageOnUI("Oops! Looks like you are invisible on the map.");
                    return true;
                } else {

                    isNavigationOngoing = true;

                    PathOptions myPathOptions  =    new PathOptions(MapActivity.this);
                    myPathOptions.setStartMarkerImageResource(R.drawable.startlocation);
                    myPathOptions.setEndMarkerImageResource(R.drawable.endlocation);
                    myPathOptions.setPathColor(R.color.black_semi_transparent);
                    myPathOptions.setPathBorderColor(R.color.grey_box_border_color);
                    myPathOptions.setPathWidth(5);
                    //  myPathOptions.setChangeFloorMarkerImageResource(R.drawable.ic_escalator_charcoal_grey);

                    if (positioningFloor>destinationArea.getFloor())
                    {
                        myPathOptions.setChangeFloorMarkerImageResource(R.drawable.downesc);
                    }
                    else
                    {
                        myPathOptions.setChangeFloorMarkerImageResource(R.drawable.upesc);
                    }
                    mapFragment.plotPath(userPosition, destinationArea.getBounds().getCenter(), positioningFloor, destinationArea.getFloor(), myPathOptions, this, false);
                }
            } else {

                isNavigationOngoing = true;

                PathOptions myPathOptions  =    new PathOptions(MapActivity.this);
                myPathOptions.setStartMarkerImageResource(R.drawable.startlocation);
                myPathOptions.setEndMarkerImageResource(R.drawable.endlocation);
                myPathOptions.setPathColor(R.color.black_semi_transparent);
                myPathOptions.setPathBorderColor(R.color.grey_box_border_color);
                myPathOptions.setPathWidth(5);
                //  myPathOptions.setChangeFloorMarkerImageResource(R.drawable.ic_escalator_charcoal_grey);

                if (positioningFloor>destinationArea.getFloor())
                {
                    myPathOptions.setChangeFloorMarkerImageResource(R.drawable.downesc);
                }
                else
                {
                    myPathOptions.setChangeFloorMarkerImageResource(R.drawable.upesc);
                }
                mapFragment.plotPath(sourceArea.getBounds().getCenter(), destinationArea.getBounds().getCenter(), sourceArea.getFloor(), destinationArea.getFloor(), myPathOptions, this, true);
            }
        }
        return false;
    }

    /**
     * whenever camera position is changed this callback is made.
     *
     * @param cameraPosition details of new camera position
     */
    @Override
    public void onCameraChanged(CameraPosition cameraPosition) {
        if (userPosition != null) {
            try {
                float northBearing = cameraPosition.bearing;
                if (northBearing != 0 && currentFloor == positioningFloor && !isNavigationOngoing) {
              //      mapRotationIndicator.setImageResource(R.drawable.si_in_lb__ic_compass_needle);
                    mapRotationIndicator.setVisibility(View.VISIBLE);
                    animateCompass(mapRotationIndicator, (float) northBearing, 500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * animate compass image.
     *
     * @param imageView compass icon imageView.
     * @param direction direction of map.
     * @param duration  animation duration.
     */

    private void animateCompass(ImageView imageView, float direction, int duration) {
        ValueAnimator animator;
        try {
            animator = ObjectAnimator.ofFloat(imageView, "rotation", direction);
            animator.setDuration(duration);
            animator.setEvaluator(new FloatEvaluator());
            animator.start();
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Called when a marker is clicked or tapped.
     *
     * @param marker  marker which is clicked by user
     * @param mapArea marker area.
     */

    @Override
    public void onMarkerClicked(Marker marker, MapArea mapArea) {
//        try {
//            if (isNavigationOngoing) {
//                return;
//            }
//            if (POIMarker != null && marker.equals(POIMarker)) {
//                hideMapAreaBandAndMarker();
//                POIMarker.showInfoWindow();
//                return;
//            }
//            if (mapArea != null) {
//                handleAreaDetailBand(mapArea, false);
//                mapAreaMarker = marker;
//                mapAreaMarker.setTitle(currentMapArea.getName());
//            } else {
//                handleHideMapAreaDetailBand();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        try {
            if (isNavigationOngoing) {
                return;
            }
            if (POIMarker != null && marker.equals(POIMarker)) {
                hideMapAreaBandAndMarker();
                POIMarker.showInfoWindow();
                return;
            }
            if (mapArea != null) {
                handleAreaDetailBandDuplicate(mapArea, false);
                destinationArea = mapArea;
                //   currentMapArea = mapArea;
                //   showMapAreaDuplicate(true);
                CommonMethods.hideCircularRevealAnimation(bottomTabBar);
                CommonMethods.showCircularRevealAnimation(storeContentView);
                UpdateStoreContent(mapArea);
                mapAreaMarker = marker;
                mapAreaMarker.setTitle(currentMapArea.getName());
            } else {
                handleHideMapAreaDetailBand();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * hide map area detail band and area marker infowindow.
     */
    private void hideMapAreaBandAndMarker() {
        try {
            if (mapAreaMarker != null) {
                mapAreaMarker.hideInfoWindow();
            }
            CommonMethods.hideView(mapAreaBand, R.anim.si_in_lb__fadeout_bottom, getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFloorChangeRegionEntered() {
        if (!isFloorChangeMessageBandShowing && !delayShowOfFloorChangeBand) {
            showFloorChangeMessageBand();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideFloorChangeMessageBand();
                }
            }, 20 * 1000); //20 seconds
        }
    }

    /*
    @Override
    public void onFloorChangeRegionEntered(String TravelModeName) {
        if (!isFloorChangeMessageBandShowing && !delayShowOfFloorChangeBand) {
            showFloorChangeMessageBand();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    hideFloorChangeMessageBand();
                }
            }, 20 * 1000); //20 seconds
        }
    }*/

    /**
     * when user has exited an escalator or an elevator region in the building.
     */
    @Override
    public void onFloorChangeRegionExited() {
    }

    /**
     * Called when user enters a promotion zone.
     *
     * @param promoZone - Entered promo zone info.
     */
    @Override
    public void onPromoZoneEntered(PromoZone promoZone) {
        updateOfferData(promoZone.getOffers());
        CommonMethods.showView(offerBubble, R.anim.si_in_lb__fadein_left, getApplicationContext());

        // start blinking animation after 250 milliseconds...
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startOfferBlinkingAnimation();
            }
        }, 250);
    }


    /**
     * updates the offer data before offer bubble is shown
     *
     * @param arrayList list of offers to be shown with it's details
     */
    private void updateOfferData(final ArrayList<Offer> arrayList) {
        offerBubble.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMessageOnUI(arrayList.size() + " offers near you!");
            }
        });
    }

    /**
     * animation to offer bubble -  blinking endlessly.
     */
    private void startOfferBlinkingAnimation() {
        RelativeLayout notificationBubble;
        ValueAnimator animator;
        try {
            notificationBubble = (RelativeLayout) findViewById(R.id.offer_notification_bubble);
            animator = ObjectAnimator.ofFloat(notificationBubble, "alpha", 1F, 0.1F);
            animator.setDuration(600);
            animator.setEvaluator(new FloatEvaluator());
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.REVERSE);
            animator.start();
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * when user has exited a promozone
     *
     * @param promoZone details of the zone where user has exited
     */
    @Override
    public void onPromoZoneExited(PromoZone promoZone) {
        CommonMethods.hideView(offerBubble, R.anim.si_in_lb__fadeout_left, getApplicationContext());
    }

    /**
     * called when user explicitly try to locate an area.
     *
     * @param mapArea details of map area.
     */

    @Override
    public void onLocateStore(MapArea mapArea) {
        try {
            if (mapArea != null) {
                handleAreaDetailBand(mapArea, true);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }

    /**
     * Show floor change message band.
     */
    private void showFloorChangeMessageBand() {
        final RelativeLayout floorChangeMessageBand;
        ImageView floorChangeMessagecloseIcon;
        try {
            floorChangeMessageBand = (RelativeLayout) findViewById(R.id.floor_change_band);
            floorChangeMessagecloseIcon = (ImageView) findViewById(R.id.floor_change_band_close);
            floorChangeMessagecloseIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hideFloorChangeMessageBand();
                    delayShowOfFloorChangeBand = true;
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            delayShowOfFloorChangeBand = false;
                        }
                    }, 20 * 1000); // 20 sec
                }
            });
            if (navigationContainer.getVisibility() == View.VISIBLE || mapAreaBand.getVisibility() == View.VISIBLE) {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) floorChangeMessageBand.getLayoutParams();
                params.bottomMargin = (int) CommonMethods.dpToPx(MapActivity.this, 70);
                floorChangeMessageBand.setLayoutParams(params);
            }
            if (floorChangeMessageBand.getVisibility() != View.VISIBLE) {
                isFloorChangeMessageBandShowing = true;
                CommonMethods.showCircularRevealAnimation(floorChangeMessageBand);
            }
        } catch (Exception ex) {
            Log.e(TAG, " showFloorChangeMessageBand " + ex);
        }
    }

    /**
     * Hide floor change message band.
     */

    private void hideFloorChangeMessageBand() {
        final View floorChangeMessageBand;
        try {
            floorChangeMessageBand = findViewById(R.id.floor_change_band);
            if (floorChangeMessageBand.getVisibility() == View.VISIBLE) {
                CommonMethods.hideCircularRevealAnimation(floorChangeMessageBand);
                isFloorChangeMessageBandShowing = false;
            }
        } catch (Exception ex) {
            Log.e(TAG, " hideFloorChangeMessageBand " + ex);
        }
    }

    /**
     * updates the ui of mapArea band before showing it on the screen.
     *
     * @param mapArea details of map area
     */
    private void handleAreaDetailBand(MapArea mapArea, boolean showInfoWindow) {
        try {
            currentMapArea = mapArea;
            TextView mapAreaName = (TextView) findViewById(R.id.map_area_name);
            TextView mapAreaTiming = (TextView) findViewById(R.id.map_area_timing);
            mapAreaName.setText(mapArea.getName());
            mapAreaTiming.setText(mapArea.getHours());
            showMapArea(showInfoWindow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAreaDetailBandDuplicate(MapArea mapArea, boolean showInfoWindow) {
        try {
            currentMapArea = mapArea;
            TextView mapAreaName = (TextView) findViewById(R.id.map_area_name);
            TextView mapAreaTiming = (TextView) findViewById(R.id.map_area_timing);
            mapAreaName.setText(mapArea.getName());
            mapAreaTiming.setText(mapArea.getHours());
            showMapAreaDuplicate(showInfoWindow);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Highlighting map area with infoWindow.
     *
     * @param showInfoWindow
     */

    private void showMapArea(boolean showInfoWindow) {
        CommonMethods.hideCircularRevealAnimation(userPositionIcon);
        CommonMethods.hideCircularRevealAnimation(navigationIcon);
        CommonMethods.showView(mapAreaBand, R.anim.si_in_lb__fadein_bottom, getApplicationContext());
        if (showInfoWindow) {
            if (mapAreaMarker != null) {
                mapAreaMarker.remove();
            }
            mapAreaMarker = googleMap.addMarker(new MarkerOptions().position(currentMapArea.getBounds().getCenter()).icon(BitmapDescriptorFactory.fromBitmap(CommonMethods.textAsBitmap(this, "", 12, Color.BLACK))).title(currentMapArea.getName()));
            mapAreaMarker.showInfoWindow();
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentMapArea.getBounds().getCenter()).zoom(googleMap.getMaxZoomLevel()).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void showMapAreaDuplicate(boolean showInfoWindow)
    {
        CommonMethods.hideCircularRevealAnimation(userPositionIcon);
        CommonMethods.hideCircularRevealAnimation(navigationIcon);
        // CommonMethods.showView(mapAreaBand, R.anim.si_in_lb__fadein_bottom, getApplicationContext());
        if (showInfoWindow) {
            if (mapAreaMarker != null) {
                mapAreaMarker.remove();
            }
            mapAreaMarker = googleMap.addMarker(new MarkerOptions().position(currentMapArea.getBounds().getCenter()).icon(BitmapDescriptorFactory.fromBitmap(CommonMethods.textAsBitmap(this, "", 12, Color.BLACK))).title(currentMapArea.getName()));
            mapAreaMarker.showInfoWindow();
        }
        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentMapArea.getBounds().getCenter()).zoom(googleMap.getMaxZoomLevel()).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Hide map area detail band.
     */

    private void handleHideMapAreaDetailBand() {
        CommonMethods.hideView(mapAreaBand, R.anim.si_in_lb__fadeout_bottom, getApplicationContext());
       // CommonMethods.hideCircularRevealAnimation(userPositionIcon);
        CommonMethods.hideCircularRevealAnimation(navigationIcon);
        CommonMethods.hideCircularRevealAnimation(search_icon);
        CommonMethods.hideCircularRevealAnimation(storeContentView);
        mapAreaMarker.hideInfoWindow();
        CommonMethods.showCircularRevealAnimation(bottomTabBar);
    }

    /**
     * callback for path finding started from one point to another point.
     */
    @Override
    public void onPathFindingStarted() {

        showLoader();

    }

    /**
     * callback for path finding completed from one point to another point.
     *
     * @param pathSegments
     */

    @Override
    public void onPathFindingCompleted(PathSegments pathSegments) {
        updateNavigationPager(pathSegments.getPathSegmentList());
        initializeNavigationBand();
        hideLoader();
        CommonMethods.hideCircularRevealAnimation(topNavigationDetailContainer);
        CommonMethods.hideCircularRevealAnimation(userPositionIcon);
        CommonMethods.hideCircularRevealAnimation(navigationIcon);
        //animateViewHeightFromTop(floorChangeIcon, ((RelativeLayout.LayoutParams) floorChangeIcon.getLayoutParams()).topMargin, CommonMethods.dpToPx(getBaseContext(), 10));
        updateFloorData(0);
    }

    /**
     * show loader.
     */
    private void showLoader() {
        try {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this, R.style.si_in_lb__MyDialogStyle);
                progressDialog.show();
                progressDialog.setContentView(R.layout.si_in_lb__loader_layout_black);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString() + " at showLoader");
        }
    }

    /**
     * called when path finding failed from one point to another point.
     *
     * @param s
     */
    @Override
    public void onPathFindingFailed(String s) {
        hideLoader();
        exitNavigation();
        showMessageOnUI("something went wrong!");
    }

    /**
     * hide loader
     */

    private void hideLoader() {
        try {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.toString() + " at hideLoader");
        }
    }

    /**
     * called when path rendering started from one point to another point.
     */

    @Override
    public void onPathRenderingStarted() {
        isRendering = true;
        removePOIMarker();
    }

    /**
     * called when path rendering completed from one point to another point.
     */

    @Override
    public void onPathRenderingComplete() {
        isRendering = false;
        CommonMethods.showView(navigationContainer, R.anim.si_in_lb__fadein_bottom, getApplicationContext());
    }

    /**
     * called when path rendering failed from one point to another point.
     *
     * @param s
     */

    @Override
    public void onPathRenderingFailed(String s) {
        isRendering = false;
        showMessageOnUI("something went wrong!");
        exitNavigation();
    }

    /**
     * called when user reach destination while navigating.
     */

    @Override
    public void onDestinationReached() {
        if (!isArriveDialogVisible) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    showArrivalAtDestinationPopup();
                }
            }, 1000);
        }
    }

    /**
     * show arrive at destination popup.
     */

    private void showArrivalAtDestinationPopup() {
        LayoutInflater inflater;
        View view;
        TextView textView;
        MaterialRippleLayout okButton;
        try {
            if (arrivalDialog != null && arrivalDialog.isShowing()) {
                return;
            }
            arrivalDialog = new Dialog(this, R.style.si_in_lb__IntripperDialogStyle);
            arrivalDialog.setCanceledOnTouchOutside(true);
            arrivalDialog.setCancelable(true);

            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.si_in_lb__arrived_at_destn_dialog, null);
            view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            textView = (TextView) view.findViewById(R.id.notification_message);
            CommonMethods.setFontHelvetica(textView);
            textView = (TextView) view.findViewById(R.id.txtExitNavigation);
            CommonMethods.setFontHelvetica(textView);
            okButton = (MaterialRippleLayout) view.findViewById(R.id.ok_button);
            okButton.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View view) {
                    try {
                        exitNavigation();
                        arrivalDialog.dismiss();
                    } catch (Exception ex) {
                        Log.e(TAG, ex.toString() + " at showArrivalAtDestinationPopup");
                    }
                }
            });

            arrivalDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    isArriveDialogVisible = false;
                }
            });
            arrivalDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    arrivalDialog.dismiss();
                }
            });
            setLayoutParamsToArriveDialog(view);
            arrivalDialog.show();
            isArriveDialogVisible = true;
        } catch (Exception ex) {
            Log.e(TAG, ex.toString() + " at showArrivalAtDestinationPopup");
        }
    }

    /**
     * set layout parameter of arrive dialog to bottom.
     *
     * @param view
     */

    private void setLayoutParamsToArriveDialog(View view) {
        arrivalDialog.setContentView(view, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        Window window = arrivalDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        window.setAttributes(layoutParams);
    }

    /**
     * called when user moves closer to one path segment than any other path segment
     *
     * @param position    Position of path segment.
     * @param pathSegment current path segment details
     */

    @Override
    public void onIndexChanged(int position, PathSegment pathSegment) {
        navigationPager.setCurrentItem(position);
    }

    /**
     * Called when user deviates from the path while navigating.
     */

    @Override
    public void onUserPositionDeviatedFromPath() {
        if (!isRerouteMessageVisible) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    ShowRerouteMessage();
                }
            }, 1000);
        }
    }

    /**
     * show reroute dialog box.
     */

    private void ShowRerouteMessage() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.si_in_lb__RerouteDialogStyle);
            builder.setMessage("Oops! Looks like you are heading some place else!");
            builder.setPositiveButton("Exit Navigation", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    exitNavigation();
                    dialog.dismiss();
                    isRerouteMessageVisible = false;
                }
            });
            builder.setNegativeButton("Reroute", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        final MapArea destination = destinationArea;
                        exitNavigation();

                        if (userPosition == null) {
                            Toast.makeText(getBaseContext(), "Oops! Looks like you are invisible on the map.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        isRerouteMessageVisible = true;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                destinationArea = destination;

                                PathOptions myPathOptions  =    new PathOptions(MapActivity.this);
                                myPathOptions.setStartMarkerImageResource(R.drawable.startlocation);
                                myPathOptions.setEndMarkerImageResource(R.drawable.endlocation);
                                myPathOptions.setPathColor(R.color.black_semi_transparent);
                                myPathOptions.setPathBorderColor(R.color.grey_box_border_color);
                                myPathOptions.setPathWidth(5);
                                // myPathOptions.setChangeFloorMarkerImageResource(R.drawable.ic_escalator_charcoal_grey);
                                if (positioningFloor>destinationArea.getFloor())
                                {
                                    myPathOptions.setChangeFloorMarkerImageResource(R.drawable.downesc);
                                }
                                else
                                {
                                    myPathOptions.setChangeFloorMarkerImageResource(R.drawable.upesc);
                                }
                                mapFragment.plotPath(userPosition, destination.getBounds().getCenter(), positioningFloor, destination.getFloor(), myPathOptions, MapActivity.this, false);
                            }
                        }, 500);
                    } catch (Exception ex) {
                        Log.e(TAG, " reroute " + ex);
                    }
                }
            });

            reRouteDialogBox = builder.create();
            setLayoutParamsToRerouteDialog();
            reRouteDialogBox.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    isRerouteMessageVisible = false;
                }
            });

            if (!isRerouteMessageVisible) {
                reRouteDialogBox.show();
                isRerouteMessageVisible = true;
            }
        } catch (Exception ex) {
            Log.e(TAG, " ShowRerouteMessage " + ex);
        }
    }

    /**
     * set layout Parameter of reroute dialog box.
     */

    private void setLayoutParamsToRerouteDialog() {
        Window window = reRouteDialogBox.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        window.setAttributes(wlp);
    }

    /**
     * Called when user explicitly changes the current Segment.
     * This may happen when user browses through the instructions for current wayfinding route using navigationPager.
     */

    @Override
    public void onNavigationInterrupted() {
        if (reCenter.getVisibility() != View.VISIBLE) {
            CommonMethods.showCircularRevealAnimation(reCenter);
            CommonMethods.hideCircularRevealAnimation(navigationContainer);
        }
    }

    /**
     * called when intripper location service status change.
     * @param s
     * @param i
     * @param bundle
     */


    /**
     *
     * @param iaLocation
     */


    /**
     *
     * @param iaRegion
     */


    /**
     *
     * @param iaRegion
     */


    /**
     * called when intripper positioning sdk connected.
     *
     */


    /**
     * called when intripper positioning sdk connection failed
     * @param message
     */



    /**
     * Start intripper location service for a given floor.
     *
     * @param floor
     */



    /**
     *
     */

    @Override
    protected void onDestroy() {
        try {


        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        super.onDestroy();
        unregisterManagers();
    }

    /**
     *
     */
    @Override
    protected void onPause() {
        try {

        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
        super.onPause();
        unregisterManagers();
    }



    @Override
    public void onFocusBuildingChanged(int buildingNumber) {
    }


    @Override
    public void MapAreaSelected(MapArea myMapArea)
    {
        destinationArea = myMapArea;

//        ArrayList<MapArea> dummyAreas = (ArrayList<MapArea>) venueInfo.getMapAreas().clone();
//
//        for (MapArea wp : dummyAreas) {
//            if (wp.getName().toLowerCase()
//                    .contains("h&m")) {
//                //  findByViewAreas.add(wp);
//                sourceArea = wp;
//            }
//        }

        CommonMethods.hideCircularRevealAnimation(amenitiesView);

        if (navigateSourceToDestination(sourceArea, destinationArea)) {
            return;
        }
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }

}

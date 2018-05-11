package com.zuby.user.zubbyrider.view.navigationdrawer.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.zuby.user.zubbyrider.R;
import com.zuby.user.zubbyrider.databinding.ActivityMainBinding;
import com.zuby.user.zubbyrider.interfaces.ResultInterface;
import com.zuby.user.zubbyrider.utils.ApiKeys;
import com.zuby.user.zubbyrider.utils.BaseActivity;
import com.zuby.user.zubbyrider.utils.PreferenceConnector;
import com.zuby.user.zubbyrider.view.navigationdrawer.adapter.CarAggregationAdapter;
import com.zuby.user.zubbyrider.view.navigationdrawer.adapter.FeedAdapter;
import com.zuby.user.zubbyrider.view.navigationdrawer.model.CarServiceModel;
import com.zuby.user.zubbyrider.view.navigationdrawer.model.FeedModel;
import com.zuby.user.zubbyrider.view.navigationdrawer.model.NearbyDriverModel;
import com.zuby.user.zubbyrider.view.navigationdrawer.presenter.GetCabServicePresenter;
import com.zuby.user.zubbyrider.view.navigationdrawer.presenter.NearbyCabPresenter;
import com.zuby.user.zubbyrider.view.ridermanagement.activity.RiderInformationManagementActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, RoutingListener {
    private GoogleMap mMap;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private FusedLocationProviderClient mFusedLocationClient;
    public static int PERMISSION = 102;
    private SupportMapFragment mapFragment;

    View mMapView;
    View bottomSheet;
    BottomSheetBehavior behavior;
    PlaceAutocompleteFragment autocompleteFragment, autocompleteFragment1;
    private List<Polyline> polylines = new ArrayList<>();
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    String mCityName, mPostalCode;
    View headerLayout;
    CircleImageView imageView;
    List<FeedModel.DataBean> mFeedData = new ArrayList<>();
    List<CarServiceModel.DataBean.DetailsBean> mDetailsBean = new ArrayList<>();
    int i = 0;
    int dx = 0, dy = 0;
    TextView book;
    LinearLayout carservicelayout;
    ActivityMainBinding mActivityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        toolbar = mActivityMainBinding.mainlayout.toolbar;
        setSupportActionBar(toolbar);
        carservicelayout = (LinearLayout) findViewById(R.id.carservice);
        book = (TextView) findViewById(R.id.continue2);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        autocompleteFragment1 = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment1);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerLayout = navigationView.getHeaderView(0);
        imageView = (CircleImageView) headerLayout.findViewById(R.id.imageView);
        Glide.with(MainActivity.this)
                .load(ApiKeys.RIDER_BASE_URL + PreferenceConnector.readString(
                        MainActivity.this, ApiKeys.IMAGEPATH, ""))
                .into(imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RiderInformationManagementActivity.class);
                startActivity(intent);
            }
        });
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapView = mapFragment.getView();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mapFragment.getMapAsync(this);
            } else {
                checkLocationPermission();
            }
        }
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                isSource = true;
                geolocate(place.getName().toString());
                mSourceLatLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
        autocompleteFragment1.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                isSource = false;
                geolocate(place.getName().toString());
                mDestinationLatLng = place.getLatLng();
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
            }
        });
        bottomSheet = findViewById(R.id.design_bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        behavior.setPeekHeight(150);
                        setDrawerState(false);
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        behavior.setPeekHeight(150);
                        setDrawerState(false);
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        behavior.setPeekHeight(150);
                        setDrawerState(false);
                        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.facebook);
                        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                        //mDrawerToggle.setDrawerIndicatorEnabled(false);
                        // Show back button
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        setDrawerState(true);
//                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//                       // mDrawerToggle.setDrawerIndicatorEnabled(true);
//                        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mActivityMainBinding.toolbar);
                        behavior.setPeekHeight(150);
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        setDrawerState(false);
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        behavior.setPeekHeight(150);
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                //  Log.i("BottomSheetCallback", "slideOffset: ");
                bottomSheet.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = MotionEventCompat.getActionMasked(event);
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                return false;
                            default:
                                return true;
                        }
                    }
                });
            }
        });
        getSupportActionBar().setTitle("");
        for (int i = 0; i < 5; i++) {
            FeedModel.DataBean dataBean = new FeedModel.DataBean();
            mFeedData.add(dataBean);
        }
        mActivityMainBinding.mainlayout.bottomsheetText.setAdapter(new FeedAdapter(mFeedData));
        mActivityMainBinding.mainlayout.bottomsheetText.setLayoutManager(new LinearLayoutManager(this));
        mActivityMainBinding.mainlayout.bottomsheetText.setNestedScrollingEnabled(false);
        mActivityMainBinding.mainlayout.bottomsheetText.setHasFixedSize(true);
        mActivityMainBinding.mainlayout.bottomsheetText.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (isMaxScrollReached(mActivityMainBinding.mainlayout.bottomsheetText)) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else {

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        new GetCabServicePresenter().show(new ResultInterface() {
            @Override
            public void onSuccess(Object object) {
                mDetailsBean.clear();
                CarServiceModel carServiceModel = (CarServiceModel) object;
                mDetailsBean = carServiceModel.getData().getDetails();
                mActivityMainBinding.mainlayout.cabService.setLayoutManager
                        (new LinearLayoutManager(MainActivity.this,
                                LinearLayoutManager.HORIZONTAL, false));
                mActivityMainBinding.mainlayout.cabService.setAdapter
                        (new CarAggregationAdapter(mDetailsBean, MainActivity.this));
                mActivityMainBinding.mainlayout.cabService.setNestedScrollingEnabled(false);
                //Log.e("cccc", "" + carServiceModel.getData().getDetails().size());
            }

            @Override
            public void onFailed(String string) {

            }
        }, context);
    }

    static private boolean isMaxScrollReached(RecyclerView recyclerView) {
        int maxScroll = recyclerView.computeVerticalScrollRange();
        int currentScroll = recyclerView.computeVerticalScrollOffset() + recyclerView.computeVerticalScrollExtent();
        return currentScroll >= maxScroll;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private List<Address> codeaddress(Location currentlatLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addressList = geocoder.getFromLocation(currentlatLng.getLatitude(), currentlatLng.getLongitude(), 1);
            if (addressList.isEmpty()) {
                // Utils.Message(this, "Waiting");
            } else {
                if (addressList.size() > 0) {
                    return addressList;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        mMap.setMyLocationEnabled(true);
        if (mMapView != null &&
                mMapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mMapView.findViewById(Integer.parseInt("1")).getParent()).
                    findViewById(Integer.parseInt("2"));
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 150);

        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng currentlatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mSourceLatLng = currentlatLng;
                MarkerOptions currentMarker = new MarkerOptions();
                currentMarker.position(currentlatLng).title("My Location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                List<Address> currentAddresses = codeaddress(location);
                autocompleteFragment.setHint(currentAddresses.get(0).getAddressLine(0));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentlatLng));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlatLng, 15));
                String cityName = currentAddresses.get(0).getAddressLine(0);
                String arr[] = cityName.split(",");
                String city = "" + arr[arr.length - 3];
                Log.e("postalcode", currentAddresses.get(0).getPostalCode());
                Log.e("postalcodexxx", arr[arr.length - 3]);
                Log.e("latitude", "" + mSourceLatLng.latitude);
                Log.e("longitude", "" + mSourceLatLng.longitude);
                Log.e("cityname", currentAddresses.get(0).getAddressLine(0));
                mPostalCode = currentAddresses.get(0).getPostalCode();
                mCityName = city;
                getNearbyCab(mSourceLatLng.latitude, mSourceLatLng.longitude, mCityName, mPostalCode);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Utils.Message(context, "Error trying to get last GPS location");
            }
        });
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {
                    mLastLocation = location;

                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                }
            }
        }
    };


    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                new android.app.AlertDialog.Builder(this)
                        .setTitle("give permission")
                        .setMessage("give permission message")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mapFragment.getMapAsync(this);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please provide the permission", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    LatLng mSourceLatLng, mDestinationLatLng;
    Boolean isSource = false;

    private void geolocate(String location) {
        if (!location.isEmpty()) {
            Address address = null;
            MarkerOptions destnationMarker = new MarkerOptions();
            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> destinationAddress = geocoder.getFromLocationName(location, 5);
                for (int i = 0; i < destinationAddress.size(); i++) {
                    address = destinationAddress.get(0);
                    LatLng Destlatlong = new LatLng(address.getLatitude(), address.getLongitude());
                    if (isSource) {
                        mSourceLatLng = Destlatlong;
                    } else {
                        mDestinationLatLng = Destlatlong;
                        getRouteToMarker(mSourceLatLng, mDestinationLatLng);
                        // mActivityMainBinding.continue2.setVisibility(View.VISIBLE);
                    }

                    destnationMarker.position(Destlatlong).title("Result Location=" + destinationAddress.get(0).getFeatureName() + "," + destinationAddress.get(0).getSubLocality() + "," + destinationAddress.get(0).getSubAdminArea()).snippet(destinationAddress.get(0).getLocality() + "," + destinationAddress.get(0).getAdminArea() + "," + destinationAddress.get(0).getCountryName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    mMap.addMarker(destnationMarker);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(Destlatlong));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Destlatlong, 12));
                    //  setupline();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static final int[] COLORS = new int[]{R.color.primary_dark_material_light};

    @Override
    public void onRoutingFailure(RouteException e) {
        if (e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i < route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Toast.makeText(getApplicationContext(), "Route " + (i + 1) + ": distance - " + route.get(i).getDistanceValue() + ": duration - " + route.get(i).getDurationValue(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingCancelled() {
    }

    private void erasePolylines() {
        for (Polyline line : polylines) {
            line.remove();
        }
        polylines.clear();
    }

    private void getRouteToMarker(LatLng sourceLatLng, LatLng pickupLatLng) {
        if (pickupLatLng != null && mLastLocation != null) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(sourceLatLng);
            builder.include(pickupLatLng);

            final LatLngBounds bounds = builder.build();

            final int zoomWidth = getResources().getDisplayMetrics().widthPixels;
            final int zoomHeight = getResources().getDisplayMetrics().heightPixels;
            final int zoomPadding = (int) (zoomWidth * 0.10); // offset from edges of the map 12% of screen

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, zoomWidth, zoomHeight, zoomPadding));
                }
            }, 500);
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(false)
                    .waypoints(sourceLatLng, pickupLatLng)
                    .build();
            routing.execute();
            bottomSheet.setVisibility(View.GONE);
            book.setVisibility(View.VISIBLE);
            carservicelayout.setVisibility(View.VISIBLE);
            mActivityMainBinding.mainlayout.data.setVisibility(View.GONE);
        }
    }

    public void setDrawerState(boolean isEnabled) {
        if (isEnabled) {
            toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            // toggle.syncState();
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();
        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.syncState();
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
        }
    }

    public void getNearbyCab(double latitude, double longitude, String cityName, String postalCode) {
        new NearbyCabPresenter().show(new ResultInterface() {
            @Override
            public void onSuccess(Object object) {
                NearbyDriverModel nearbyDriverModel = (NearbyDriverModel) object;
                //Toast.makeText(context, nearbyDriverModel.getMessage(), Toast.LENGTH_LONG).show();
                mList.clear();
                for (int i = 0; i < nearbyDriverModel.getData().size(); i++) {
                    NearbyDriverModel.DataBean dataBean = nearbyDriverModel.getData().get(i);
                    MarkerOptions destnationMarker1 = new MarkerOptions();
                    try {
                        destnationMarker1.position(new LatLng(Double.parseDouble(dataBean.getDriver_lat()),
                                Double.parseDouble(dataBean.getDriver_long()))).title("").
                                icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                        mMap.addMarker(destnationMarker1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailed(String string) {
                Toast.makeText(context, string, Toast.LENGTH_LONG).show();
            }
        }, context, "" + latitude, "" + longitude, cityName, postalCode);
    }

    List<NearbyDriverModel.DataBean> mList = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        Glide.with(MainActivity.this)
                .load(ApiKeys.RIDER_BASE_URL + PreferenceConnector.readString(
                        MainActivity.this, ApiKeys.IMAGEPATH, ""))
                // .placeholder(R.mipmap.ic_launcher)
                .into(imageView);
    }
}

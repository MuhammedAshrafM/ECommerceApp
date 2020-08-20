package com.example.ecommerceapp.ui.cart;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.BackPressedListener;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.MapClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentMapBinding;
import com.example.ecommerceapp.ui.home.HomeActivity;
import com.example.ecommerceapp.ui.search.SearchableActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.android.volley.VolleyLog.TAG;

public class MapFragment extends Fragment implements OnMapReadyCallback, View.OnClickListener, BackPressedListener,
        ConnectivityReceiver.ConnectivityReceiveListener {

    private FragmentMapBinding binding;
    private View root;
    private SupportMapFragment mapFragment;
    private GoogleMap gMap;
    private Context context;
    private Activity activity;
    private FusedLocationProviderClient fusedLocationClient;
    private MapClickListener listener;
    private Utils utils;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "MapClickListener";
    private static final float DEFAULT_ZOOM = 15f;
    private static final int REQUEST_AUTO_COMPLETE_PERMISSION = 1001;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = getContext();
        ((AppCompatActivity) activity).getSupportActionBar().hide();
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            listener = (MapClickListener) getArguments().getSerializable(ARG_PARAM1);
        }

        if (activity.getClass().getSimpleName().contains("HomeActivity")) {
            ((HomeActivity) activity).setOnBackPressedListener(this::OnBackPressed);
        } else if (activity.getClass().getSimpleName().contains("SearchableActivity")) {
            ((SearchableActivity) activity).setOnBackPressedListener(this::OnBackPressed);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false);
        root = binding.getRoot();

        return root;
    }


    @Override
    public void onStart() {
        super.onStart();

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        initMap();
    }


    @Override
    public void onResume() {
        super.onResume();

        // register intent filter
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
        context.registerReceiver(connectivityReceiver, intentFilter);

        MyApplication.getInstance().setConnectivityReceiveListener(this);

    }

    private void initMap() {

        // will use autocomplete after enable Billing on the Google Cloud Project at
        // https://console.cloud.google.com/project/_/billing/enable
//        Places.initialize(context.getApplicationContext(), getString(R.string.google_places_key), Locale.getDefault());
//        binding.mapSearchEt.setFocusable(false);
//        binding.mapSearchEt.setOnClickListener(this);

        binding.mapSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                        keyEvent.getAction() == KeyEvent.KEYCODE_ENTER) {
                    getLocate();
                }
                return false;
            }
        });

        binding.currentLocationImb.setOnClickListener(this);
        binding.searchLocationImb.setOnClickListener(this);

    }

    private void getLocate() {
        String searchText = binding.mapSearchEt.getText().toString().trim();
        if (!searchText.equals("")) {
            Geocoder gcd = new Geocoder(context, Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocationName(searchText, 1);
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    String addressFull = address.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                    LatLng latLng = new LatLng(address.getLatitude(), addresses.get(0).getLongitude());
                    moveCamera(latLng, DEFAULT_ZOOM, address.getAddressLine(0));
                }
            } catch (Exception e) {

            }
        }
    }

    private void getCurrentLocation() {
        if (ConnectivityReceiver.isConnected()) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            moveCamera(latLng, DEFAULT_ZOOM, getString(R.string.myLocation));
                        }
                    }
                }
            });
        } else {
            displaySnackBar(true, null, 0);
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        if (!title.equals(getString(R.string.myLocation))) {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);

            gMap.addMarker(options);
        }
    }

    private void hideSoftKeyword() {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void initAutoComplete() {
        List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(context);
        startActivityForResult(intent, REQUEST_AUTO_COMPLETE_PERMISSION);
    }

    private void displaySnackBar(boolean show, String msg, int duration) {
        if (msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerMap), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    private void stopBackPressedListener() {
        if (activity.getClass().getSimpleName().contains("HomeActivity")) {
            ((HomeActivity) activity).setOnBackPressedListener(null);
        } else if (activity.getClass().getSimpleName().contains("SearchableActivity")) {
            ((SearchableActivity) activity).setOnBackPressedListener(null);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        initMap();
        getCurrentLocation();

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        gMap.setMyLocationEnabled(true);
        gMap.getUiSettings().setMyLocationButtonEnabled(false);

        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                listener.onMapClick(latLng);
                stopBackPressedListener();
                activity.onBackPressed();
            }
        });

        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                listener.onMapClick(marker.getPosition());
                stopBackPressedListener();
                activity.onBackPressed();
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.current_location_imb:
                getCurrentLocation();
                break;

            case R.id.search_location_imb:
                getLocate();
                break;

            case R.id.map_search_et:
                initAutoComplete();
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_AUTO_COMPLETE_PERMISSION) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                binding.mapSearchEt.setText(place.getAddress());
                getLocate();
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
            return;
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, 0);
        }
    }

    @Override
    public void OnBackPressed() {
        listener.onMapClick(new LatLng(-1.0, -1.0));

        stopBackPressedListener();
        activity.onBackPressed();
    }
}

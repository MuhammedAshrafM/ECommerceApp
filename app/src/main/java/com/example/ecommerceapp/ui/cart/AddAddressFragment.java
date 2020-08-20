package com.example.ecommerceapp.ui.cart;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.MapClickListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.SnackBarActionListener;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentAddAddressBinding;
import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.UserModel;
import com.example.ecommerceapp.ui.home.HomeActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddAddressFragment#} factory method to
 * create an instance of this fragment.
 */
public class AddAddressFragment extends Fragment implements SnackBarActionListener, View.OnClickListener,
        MapClickListener, ConnectivityReceiver.ConnectivityReceiveListener {

    private CartViewModel cartViewModel;
    private FragmentAddAddressBinding binding;
    private View root;
    private NavController navController;
    private Utils utils;
    private String[] permissions;
    private Context context;
    private Activity activity;
    private UserModel user;
    private String id, recipientName, address, buildingNumber, floorNumber, apartmentNumber, mobileNumber,
            countryCodeName, mobileNumberSaved, countryCodeNameSaved, recipientPattern, buildingPattern,
            floorPattern, apartmentPattern, mobilePattern;
    private AddressModel addressModel, addressModelNew;
    private boolean editAddress = false;
    private Bundle bundleProducts;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "address";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int REQUEST_MAP_LOCATION_PERMISSION = 1001;
    private static final int REQUEST_GPS_LOCATION_PERMISSION = 1002;
    private static final int REQUEST_LOCATION_PERMISSION = 1003;
    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    private static final String PREFERENCES_ADDRESSES_SAVED = "ADDRESSES_SAVED";


    public AddAddressFragment() {
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

            bundleProducts = getArguments();
            addressModel = bundleProducts.getParcelable(ARG_PARAM1);
            if (bundleProducts.containsKey(ARG_PARAM1)) {
                bundleProducts.remove(ARG_PARAM1);
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_address, container, false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.toolbar.setTitle(getString(R.string.title_Address));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        selectActivity();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onStart() {
        super.onStart();

        recipientPattern = "[a-z\\sA-Z]*";
        buildingPattern = "(\\w)*";
        floorPattern = "(\\d)*";
        apartmentPattern = "(\\w)*";
        mobilePattern = "(\\d)*";

        user = Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).getDataUser();
        permissions = new String[]{FINE_LOCATION, COARSE_LOCATION};

        binding.keyCountryCp.registerCarrierNumberEditText(binding.mobileNumberEt);

        checkLocationPermission(REQUEST_LOCATION_PERMISSION);

        selectArguments();

        observeLiveData();
        setOnClickListener();
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

    private void selectActivity(){
        if(activity.getClass().getSimpleName().contains("HomeActivity")) {
            binding.setPadding(true);
        }
    }
    private void observeLiveData(){

        cartViewModel.editAddress().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);

                if (s.equals(getString(R.string.success))) {
                    Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).editAddressSaved(addressModel);
                    if (addressModel.getMobileNumberValidated() == 1) {
                        activity.onBackPressed();

                    } else {
                        navigateToMobileVerificationFragment();
                    }
                } else if (s.equals(getString(R.string.failed))) {
                    displaySnackBar(true, getString(R.string.addressField), 0, null);
                }

            }
        });
        cartViewModel.addAddress().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);

                if (s.startsWith("id")) {
                    addressModelNew.setId(s);
                    Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).setAddressSaved(addressModelNew);
                    navigateToMobileVerificationFragment();
                } else if (s.equals(getString(R.string.failed))) {
                    displaySnackBar(true, getString(R.string.addressField), 0, null);
                }

            }
        });
        cartViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null, 0, null);
            }
        });
    }
    private void setOnClickListener(){

        binding.saveBt.setOnClickListener(this::onClick);
        binding.mapAddress.setOnClickListener(this::onClick);
        binding.currentPosition.setOnClickListener(this::onClick);
    }
    private void selectArguments(){
        if (getArguments() != null) {
            double latitude = getArguments().getDouble("latitude");
            double longitude = getArguments().getDouble("longitude");
            if (latitude == 0.0 && longitude == 0.0) {
                if (addressModel != null) {
                    binding.toolbar.setTitle(getString(R.string.title_edit_Address));
                    editAddress = true;
                    address = addressModel.getAddress();
                    recipientName = addressModel.getRecipientName();
                    mobileNumberSaved = addressModel.getMobileNumber();
                    countryCodeNameSaved = addressModel.getCountryCodeName();
                    buildingNumber = addressModel.getBuildingNumber();
                    floorNumber = addressModel.getFloorNumber();
                    apartmentNumber = addressModel.getApartmentNumber();

                    setData(mobileNumberSaved, countryCodeNameSaved);
                }else {
                    recipientName = user.getName();
                    binding.nameRecipientEt.setText(recipientName);
                }
            } else if (!(latitude == -1.0 && longitude == -1.0)) {
                getAddress(latitude, longitude);
            }
        }
    }

    private void navigateToMobileVerificationFragment(){
        navController.navigate(R.id.action_addAddressFragment_to_mobileVerificationFragment, bundleProducts);
    }
    private void checkLocationPermission(int REQUEST_PERMISSION) {

        if (ContextCompat.checkSelfPermission(context, FINE_LOCATION) +
                ContextCompat.checkSelfPermission(context, COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, FINE_LOCATION) ||
                    ActivityCompat.shouldShowRequestPermissionRationale(activity, COARSE_LOCATION)) {

                displaySnackBar(true, getString(R.string.locationPermission), -2, this::onActionListener);

            } else {

                ActivityCompat.requestPermissions(activity, permissions, REQUEST_PERMISSION);
            }
        } else {
            if (REQUEST_PERMISSION == REQUEST_MAP_LOCATION_PERMISSION) {
                displayMapFragment();
            } else if (REQUEST_PERMISSION == REQUEST_GPS_LOCATION_PERMISSION) {
                getCurrentLocation();
            }

        }

    }

    private void getCurrentLocation() {
        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
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
                    displayProgressDialog(false);
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location != null) {
                            getAddress(location.getLatitude(), location.getLongitude());
                        }
                    } else {
                        displaySnackBar(true, null, 0, null);
                    }
                }
            });
        }else {
            displaySnackBar(true, null, 0, null);
        }
    }

    private void getAddress(double latitude, double longitude){
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(latitude,longitude, 1);
            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                binding.addressSelected.setText(address);
                binding.setVisible(true);
            }

        } catch (Exception e) {

        }
    }

    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }

    private void displaySnackBar(boolean show, String msg, int duration, SnackBarActionListener listener){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        if(listener != null) {
            utils = new Utils(context, this::onActionListener);
        }else {
            utils = new Utils(context);
        }
        utils.snackBar(root.findViewById(R.id.containerAddress), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    private void displayMapFragment(){
        Bundle bundle = new Bundle();
        bundle.putSerializable("MapClickListener", this);
        navController.navigate(R.id.action_addAddressFragment_to_mapFragment, bundle);
    }
    private void saveAddress(){
        if(validateAddressData()){
            if(editAddress){
                addressModel.setAddress(address);
                addressModel.setRecipientName(recipientName);
                addressModel.setBuildingNumber(buildingNumber);
                addressModel.setFloorNumber(floorNumber);
                addressModel.setApartmentNumber(apartmentNumber);
                if(!mobileNumber.equals(mobileNumberSaved) || !countryCodeName.equals(countryCodeNameSaved)){
                    addressModel.setMobileNumber(mobileNumber);
                    addressModel.setCountryCodeName(countryCodeName);
                    addressModel.setMobileNumberValidated(0);
                }
                if (ConnectivityReceiver.isConnected()) {
                    displayProgressDialog(true);
                    cartViewModel.editAddress(addressModel);
                }else {
                    displaySnackBar(true, null, 0, null);
                }

            }else {
                id = new StringBuilder("id_").append(System.currentTimeMillis()).toString();
                addressModelNew = new AddressModel(id, recipientName, address, mobileNumber, countryCodeName,
                        0, buildingNumber, floorNumber, apartmentNumber, user.getId());
                if (ConnectivityReceiver.isConnected()) {
                    displayProgressDialog(true);
                    cartViewModel.addAddress(addressModelNew);
                }else {
                    displaySnackBar(true, null, 0, null);
                }
            }
        }
    }

    private void setData(String mobileNumber, String countryCodeName){
        binding.nameRecipientEt.setText(recipientName);
        binding.addressSelected.setText(address);
        binding.setVisible(true);
        binding.mobileNumberEt.setText(mobileNumber);
        binding.keyCountryCp.setCountryForNameCode(countryCodeName);
        binding.buildingNumberEt.setText(buildingNumber);
        binding.floorNumberEt.setText(floorNumber);
        binding.apartmentNumberEt.setText(apartmentNumber);
    }
    private void getData(){
        binding.mobileNumberEt.setError(null);

        recipientName = binding.nameRecipientEt.getText().toString().trim();
        address = binding.addressSelected.getText().toString().trim();
        mobileNumber = binding.mobileNumberEt.getText().toString().trim();
        buildingNumber = binding.buildingNumberEt.getText().toString().trim();
        countryCodeName = binding.keyCountryCp.getSelectedCountryNameCode();

        floorNumber = binding.floorNumberEt.getText().toString().trim();
        apartmentNumber = binding.apartmentNumberEt.getText().toString().trim();
    }

    private boolean validateAddressData() {
        boolean cancel = false;
        View focusView = null;

        getData();

        if(TextUtils.isEmpty(recipientName)){
            binding.nameRecipientEt.setError(getString(R.string.nameRecipientField));
            focusView = binding.nameRecipientEt;
            cancel = true;
        }
        else if(!recipientName.matches(recipientPattern)){
            binding.nameRecipientEt.setError(getString(R.string.characterPatternField));
            focusView = binding.nameRecipientEt;
            cancel = true;
        }
        else if(TextUtils.isEmpty(address)){
            displaySnackBar(true, getString(R.string.locationField), -2, null);
            focusView = binding.addressSelected;
            cancel = true;
        }
        else if(TextUtils.isEmpty(buildingNumber)){
            binding.buildingNumberEt.setError(getString(R.string.buildingNumberField));
            focusView = binding.buildingNumberEt;
            cancel = true;
        }
        else if(!buildingNumber.matches(buildingPattern)){
            binding.buildingNumberEt.setError(getString(R.string.buildingPatternField));
            focusView = binding.buildingNumberEt;
            cancel = true;
        }
        else if(!TextUtils.isEmpty(floorNumber) && !floorNumber.matches(floorPattern)){
            binding.floorNumberEt.setError(getString(R.string.floorPatternField));
            focusView = binding.floorNumberEt;
            cancel = true;
        }
        else if(!TextUtils.isEmpty(apartmentNumber) && !apartmentNumber.matches(apartmentPattern)){
            binding.apartmentNumberEt.setError(getString(R.string.apartmentPatternField));
            focusView = binding.apartmentNumberEt;
            cancel = true;
        }
        else if(TextUtils.isEmpty(mobileNumber)){
            binding.mobileNumberEt.setError(getString(R.string.mobileNumberField));
            focusView = binding.mobileNumberEt;
            cancel = true;
        }
        else if(TextUtils.getTrimmedLength(mobileNumber) < 10){
            binding.mobileNumberEt.setError(getString(R.string.mobileNumberIncorrect));
            focusView = binding.mobileNumberEt;
            cancel = true;
        }
        else if(!mobileNumber.matches(mobilePattern)){
            binding.mobileNumberEt.setError(getString(R.string.mobilePatternField));
            focusView = binding.mobileNumberEt;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                            == PackageManager.PERMISSION_GRANTED){

                    }
                } else {
                    displaySnackBar(true, getString(R.string.locationPermission), -2, this::onActionListener);
                }
                break;
            case REQUEST_MAP_LOCATION_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                            == PackageManager.PERMISSION_GRANTED){
                        displayMapFragment();
                    }
                } else {
                    displaySnackBar(true, getString(R.string.locationPermission), -2, this::onActionListener);
                }
                break;
            case REQUEST_GPS_LOCATION_PERMISSION:
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0){
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1]
                            == PackageManager.PERMISSION_GRANTED){
                        getCurrentLocation();
                    }
                } else {
                    displaySnackBar(true, getString(R.string.locationPermission), -2, this::onActionListener);
                }
                break;

            default:

                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    @Override
    public void onActionListener(View view, int id) {
        if(binding.mapAddress.isChecked()) {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_MAP_LOCATION_PERMISSION);
        }else {
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_GPS_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save_bt:
                saveAddress();
                break;

            case R.id.map_address:
                checkLocationPermission(REQUEST_MAP_LOCATION_PERMISSION);
                break;

            case R.id.current_position:
                checkLocationPermission(REQUEST_GPS_LOCATION_PERMISSION);
                break;

            default:
                activity.onBackPressed();
                break;
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", latLng.latitude);
        bundle.putDouble("longitude", latLng.longitude);
        setArguments(bundle);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, 0, null);
        }
    }
}

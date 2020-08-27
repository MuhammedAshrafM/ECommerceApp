package com.example.ecommerceapp.ui.cart;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.BackPressedListener;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentCompleteOrderBinding;
import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.UserModel;
import com.example.ecommerceapp.ui.home.HomeActivity;
import com.example.ecommerceapp.ui.search.SearchableActivity;
import com.facebook.login.widget.ProfilePictureView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import static com.android.volley.VolleyLog.TAG;

public class CompleteOrderFragment extends Fragment implements View.OnClickListener, BackPressedListener,
        ConnectivityReceiver.ConnectivityReceiveListener{

    private NavController navController;
    private FragmentCompleteOrderBinding binding;
    private CartViewModel cartViewModel;
    private View root;
    private Utils utils;
    private Context context;
    private Activity activity;
    private DecimalFormat decimalFormat = new DecimalFormat();
    private AddressModel addressModel;
    private String orderId, userId, userToken, paymentOption;
    private ArrayList<String> productsId;
    private ArrayList<Integer> productsCount;
    private Dialog dialog;
    private TextView codeMessageTv;
    private Button cancelBt, copyBt;
    private UserModel user;

    private static final String PREFERENCES_DATA_USER = "DATA_USER";
    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private static final String PREFERENCES_ADDRESSES_SAVED = "ADDRESSES_SAVED";

    private static String ARG1 = "";
    private static String ARG2 = "";
    private static String ARG3 = "";
    private static String ARG4 = "";
    private static final String ARG5 = "counts";

    // TODO: Rename and change types of parameters
    private double subTotal;
    private double shippingFee;
    private double grandTotal;
    private int paymentMethod;

    public CompleteOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = getContext();

        ((AppCompatActivity) activity).getSupportActionBar().hide();
        setHasOptionsMenu(true);

        ARG1 = getString(R.string.subTotal);
        ARG2 = getString(R.string.shippingPlus);
        ARG3 = getString(R.string.grandTotal);
        ARG4 = getString(R.string.title_Payment_Options);

        if (getArguments() != null) {
            subTotal = getArguments().getDouble(ARG1);
            shippingFee = getArguments().getDouble(ARG2);
            grandTotal = getArguments().getDouble(ARG3);
            paymentMethod = getArguments().getInt(ARG4);
            productsCount = getArguments().getIntegerArrayList(ARG5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_complete_order,container,false);
        root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.toolbar.setTitle(getString(R.string.title_Complete_Order));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        cartViewModel =
                new ViewModelProvider(this).get(CartViewModel.class);

        selectActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        decimalFormat.applyPattern("#,###,###,###.##");

        setData();

        getSharedPreferences();

        observeLiveData();

        setAlertDialog();
        binding.completeOrder.setOnClickListener(this::onClick);
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
        }else {
            binding.setPadding(false);
        }
    }

    private void observeLiveData(){
        cartViewModel.sendOrder().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> productModels) {
                displayProgressDialog(false);
                if (productModels != null && productModels.size() > 0) {
                    if(productsId.size() == productModels.size()){
                        displaySnackBar(true, getString(R.string.allOrderFailed), -2);
                    }else {
                        displaySnackBar(true, getString(R.string.someOrderFailed), -2);
                        Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).setProductsCarted(productModels);
                    }
                }else {
                    dialog.show();
                    displaySnackBar(true, getString(R.string.orderSuccess), -2);
                    Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).removeProductsCarted();

                    completeOrder();

                }
            }
        });

        cartViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, null, 0);
            }
        });
    }

    private void getSharedPreferences(){
        productsId = Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
        user = Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).getDataUser();
        userId = user.getId();
        userToken = user.getToken();
        addressModel = Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).getAddressSavedSelected();
    }

    private void setData(){
        binding.subTotalPrice.setText(decimalFormat.format(subTotal) + " " + getString(R.string.egp));
        binding.shippingPrice.setText(decimalFormat.format(shippingFee) + " " + getString(R.string.egp));
        binding.grandTotalPrice.setText(decimalFormat.format(grandTotal) + " " + getString(R.string.egp));
    }

    private void completeOrder(){
        binding.completeOrder.setEnabled(false);

        if(binding.getPadding()) {
            ((HomeActivity) activity).setOnBackPressedListener(this::OnBackPressed);
        }else{
            ((SearchableActivity)activity).setOnBackPressedListener(this::OnBackPressed);
        }
    }

    private void stopBackPressedListener(){
        if(binding.getPadding()) {
            ((HomeActivity) activity).setOnBackPressedListener(null);
        }else {
            ((SearchableActivity)activity).setOnBackPressedListener(null);
        }
    }

    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }

    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerCompleteOrder), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    private void sendOrder(){
        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
            orderId = String.valueOf(System.currentTimeMillis());
            paymentOption = getString(paymentMethod);
            cartViewModel.sendOrder(orderId, productsId, userId, userToken, addressModel.getId(), productsCount, paymentOption,
                    subTotal, shippingFee);

            codeMessageTv.setText(getString(R.string.code) + orderId);
        }else {
            displaySnackBar(true, null, 0);
        }
    }



    private void setAlertDialog(){
        View view = getLayoutInflater().inflate(R.layout.layout_complete_order, null);
        copyBt = view.findViewById(R.id.copy_bt);
        cancelBt = view.findViewById(R.id.cancel_bt);
        codeMessageTv = view.findViewById(R.id.code_message_tv);

        copyBt.setOnClickListener(this);
        cancelBt.setOnClickListener(this);

        dialog = new Dialog(context, R.style.MaterialDialogSheet);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void copyPrivateCode(){
        ClipboardManager clipboard = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied",orderId);
        clipboard.setPrimaryClip(clip);

        displaySnackBar(true, getString(R.string.copied), -1);

        navigate();
    }


    private void navigate(){
        dialog.dismiss();

        popBackStack();

    }

    private void popBackStack(){
        if(binding.getPadding()) {
            navController.popBackStack(R.id.navigation_home, true);
        }else {
            navController.popBackStack(R.id.navigation_search, true);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.completeOrder:
                if(binding.completeOrder.isEnabled()) {
                    sendOrder();
                }else {
                    displaySnackBar(true, "Your order is completed", 0);
                }
                break;
            case R.id.copy_bt:
                copyPrivateCode();
                break;
            case R.id.cancel_bt:
                navigate();
                break;

            default:
                if(binding.completeOrder.isEnabled()) {
                    activity.onBackPressed();
                }else {
                    popBackStack();
                }
                break;
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
        popBackStack();
        stopBackPressedListener();
    }
}

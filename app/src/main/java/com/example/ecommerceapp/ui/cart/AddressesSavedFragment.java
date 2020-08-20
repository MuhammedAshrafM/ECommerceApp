package com.example.ecommerceapp.ui.cart;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.ItemTouchListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.RecyclerItemTouchHelper;
import com.example.ecommerceapp.data.SnackBarActionListener;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentAddressesSavedBinding;
import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.ProductModel;

import java.util.ArrayList;
import java.util.Locale;

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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class AddressesSavedFragment extends Fragment implements View.OnClickListener, ItemClickListener,
        ItemTouchListener, SnackBarActionListener, ConnectivityReceiver.ConnectivityReceiveListener{

    private NavController navController;
    private CartViewModel cartViewModel;
    private FragmentAddressesSavedBinding binding;
    private View root;
    private Utils utils;
    private Context context;
    private Activity activity;
    private AddressesSavedAdapter adapter;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private ArrayList<AddressModel> addressModels;
    private Bundle bundleProducts;
    private int indexAddressRemoved;
    private AddressModel addressRemoved;
    private boolean checkedAddressRemoved = false;

    private static final String PREFERENCES_ADDRESSES_SAVED = "ADDRESSES_SAVED";


    public AddressesSavedFragment() {
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_addresses_saved, container, false);
        root = binding.getRoot();

        return root;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        binding.toolbar.setTitle(getString(R.string.title_selectAddress));
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

        addressModels = Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).getAddressesSaved();

        binding.recyclerViewAddressesSaved.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerViewAddressesSaved.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerViewAddressesSaved.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        adapter = new AddressesSavedAdapter(context, this);
        binding.recyclerViewAddressesSaved.setAdapter(adapter);

        simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.recyclerViewAddressesSaved);
        adapter.setList(addressModels);

        binding.addAddressFAB.setOnClickListener(this::onClick);

        observeLiveData();
    }


    private void selectActivity(){
        if(activity.getClass().getSimpleName().contains("HomeActivity")) {
            binding.setPadding(true);
        }
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

    private void observeLiveData(){
        cartViewModel.removeAddress().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);

                if(s.equals(getString(R.string.success))){
                    removeAddress();
                }else{
                    adapter.setList(addressModels);
                    displaySnackBar(true, getString(R.string.addressField), R.string.ok, 0, null);
                }

            }
        });
        cartViewModel.addAddress().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);

                if(s.startsWith("id")){

                    addressRemoved.setId(s);

                    if(checkedAddressRemoved) {
                        addressModels = Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).setAddressSaved(addressRemoved);
                    }else {
                        addressModels.add(indexAddressRemoved, addressRemoved);
                        Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).setAddressesSaved(addressModels);
                    }
                    adapter.restoreItem(addressRemoved, indexAddressRemoved);
                    checkedAddressRemoved = false;

                }else if (s.equals(getString(R.string.failed))){
                    displaySnackBar(true, getString(R.string.addressField), R.string.ok, 0, null);
                }

            }
        });
        cartViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                displayProgressDialog(false);
                displaySnackBar(true, getString(R.string.addressField), R.string.ok, 0, null);
            }
        });
    }
    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }

    private void displaySnackBar(boolean show, String msg, int actionResource, int duration, SnackBarActionListener listener){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        if(listener != null) {
            utils = new Utils(context, this::onActionListener);
        }else {
            utils = new Utils(context);
        }
        utils.snackBar(root.findViewById(R.id.containerAddressesSaved), msg, actionResource, duration);
        utils.displaySnackBar(show);
    }

    private void removeAddress(){

        addressModels = Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).removeAddressSaved(addressRemoved);
        Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).setAddressesSaved(addressModels);

        String address = addressRemoved.getAddress();
        int addressLength = address.length() / 2;

        displaySnackBar(true, String.format(Locale.getDefault(),
                "%s %s", address.substring(0, addressLength), getString(R.string.removed)),
                R.string.undo, 0, this::onActionListener);
    }
    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.add_address_fAB){
            navController.navigate(R.id.action_addressesSavedFragment_to_addAddressFragment, bundleProducts);
        }else {
            activity.onBackPressed();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, R.string.ok, 0, null);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        activity.onBackPressed();
    }

    @Override
    public void onCartClick(View view, ProductModel productModel, boolean carted, int newSize) {

    }

    @Override
    public void onCartView(double subTotal, boolean added) {

    }

    @Override
    public void onWishClick(View view, ProductModel productModel, boolean wished) {

    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if(viewHolder instanceof AddressesSavedAdapter.AddressesSavedViewHolder) {
            if(addressModels.size() > 1) {
                indexAddressRemoved = viewHolder.getAdapterPosition();
                if (indexAddressRemoved == 0) {
                    checkedAddressRemoved = true;
                }
                addressRemoved = addressModels.get(indexAddressRemoved);

                if (ConnectivityReceiver.isConnected()) {
                    adapter.removeItem(indexAddressRemoved);
                    displayProgressDialog(true);
                    cartViewModel.removeAddress(addressRemoved.getId());
                }else {
                    displaySnackBar(true, null, R.string.ok, 0, null);
                }

            }else {
                adapter.setList(addressModels);
                displaySnackBar(true, getString(R.string.removeLastAddressMessage), R.string.ok, 0, null);
            }
        }
    }

    @Override
    public void onActionListener(View view, int id) {
        if(id == R.string.undo){
            if (ConnectivityReceiver.isConnected()) {
                displayProgressDialog(true);
                cartViewModel.addAddress(addressRemoved);
            }else {
                displaySnackBar(true, null, R.string.ok, 0, null);
            }

        }
    }
}

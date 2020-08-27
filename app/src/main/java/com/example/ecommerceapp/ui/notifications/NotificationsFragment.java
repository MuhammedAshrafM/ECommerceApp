package com.example.ecommerceapp.ui.notifications;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.ItemTouchListener;
import com.example.ecommerceapp.data.MyApplication;
import com.example.ecommerceapp.data.NotificationListener;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.data.RecyclerItemTouchHelper;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentNotificationsBinding;
import com.example.ecommerceapp.databinding.FragmentOrdersBinding;
import com.example.ecommerceapp.pojo.NotificationModel;
import com.example.ecommerceapp.pojo.OrderModel;
import com.example.ecommerceapp.pojo.UserModel;
import com.example.ecommerceapp.ui.cart.AddressesSavedAdapter;
import com.example.ecommerceapp.ui.home.HomeActivity;
import com.example.ecommerceapp.ui.orders.OrdersAdapter;
import com.example.ecommerceapp.ui.orders.OrdersViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.internal.FacebookDialogFragment.TAG;

public class NotificationsFragment extends Fragment implements SearchView.OnQueryTextListener, ItemTouchListener,
        SwipeRefreshLayout.OnRefreshListener, ConnectivityReceiver.ConnectivityReceiveListener{

    private NavController navController;
    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;
    private List<NotificationModel> notifications;
    private NotificationsAdapter adapter;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private ArrayList<String> productsCartedId;
    private View root;
    private SearchView searchView;
    private Menu menu;
    private MenuItem searchMenuItem, cartMenuItem;
    private UserModel user;
    private Context context;
    private Activity activity;
    private Utils utils;
    private NotificationListener listener;
    private int indexNotificationRemoved;
    private NotificationModel notificationRemoved;


    private static final String PREFERENCES_PRODUCTS_CARTED = "PRODUCTS_CARTED";
    private static final String PREFERENCES_DATA_USER = "DATA_USER";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = getContext();
        ((AppCompatActivity) activity).getSupportActionBar().hide();
        setHasOptionsMenu(true);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);
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

        binding.toolbar.setTitle(getString(R.string.title_notifications));
        notificationsViewModel = new ViewModelProvider(this).get(NotificationsViewModel.class);

        //        GoogleApiAvailability
//                .getInstance()
//                .makeGooglePlayServicesAvailable(getActivity()).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//
//            }
//        });
    }

    @Override
    public void onStart() {
        super.onStart();

        menu = null;

        handleUi();
        notificationsViewModel.getNotifications(context, user.getId());
        if (ConnectivityReceiver.isConnected()) {
            displayProgressDialog(true);
//            notificationsViewModel.getNotifications()(user.getId());
        } else {
            displaySnackBar(true, null);
//            notificationsViewModel.setNotifications(context);
        }

        observeLiveData();
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

    private void handleUi(){
        listener = (NotificationListener) context;

        binding.recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerViewNotifications.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerViewNotifications.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        adapter = new NotificationsAdapter(context);
        binding.recyclerViewNotifications.setAdapter(adapter);

        simpleCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.START, R.id.recyclerViewNotifications, this::onSwiped);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(binding.recyclerViewNotifications);

        user = Preferences.getINSTANCE(context, PREFERENCES_DATA_USER).getDataUser();

        binding.swipeRefresh.setColorSchemeColors(Color.rgb(3,169,244),
                Color.rgb(3,169,244),
                Color.rgb(13,179,163));
        binding.swipeRefresh.setOnRefreshListener(this);
    }


    private void observeLiveData(){

        notificationsViewModel.getNotifications().observe(getViewLifecycleOwner(), new Observer<List<NotificationModel>>() {
            @Override
            public void onChanged(List<NotificationModel> notificationModels) {
                displayProgressDialog(false);
                if (notificationModels != null && notificationModels.size() > 0) {
                    notifications = notificationModels;
                    adapter.setList(notifications);
                    notificationsViewModel.updateDisplayNotifications(context, user.getId());
                }
            }
        });

        notificationsViewModel.updateDisplayNotifications().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    listener.onNotifyChange();
                }
            }
        });
        notificationsViewModel.deleteNotification().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(aBoolean){
                    adapter.removeItem(indexNotificationRemoved);
                }
            }
        });
        notificationsViewModel.getErrorMessage().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                displayProgressDialog(false);
                displaySnackBar(true, null);
            }
        });
    }


    private void displayProgressDialog(boolean show){
        binding.setVisibleProgress(show);
    }

    private void displaySnackBar(boolean show, String msg){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerNotifications), msg, R.string.ok, 0);
        utils.displaySnackBar(show);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if(this.menu == null) {
            binding.toolbar.inflateMenu(R.menu.search_menu);
            binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    return onOptionsItemSelected(item);
                }
            });

            this.menu = binding.toolbar.getMenu();
            cartMenuItem = this.menu.findItem(R.id.myCart);
            SearchManager searchManager = (SearchManager)
                    activity.getSystemService(Context.SEARCH_SERVICE);
            searchMenuItem = this.menu.findItem(R.id.search);
            searchView = (SearchView) searchMenuItem.getActionView();

            searchView.setSearchableInfo(searchManager.
                    getSearchableInfo(activity.getComponentName()));
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(this);

            productsCartedId = Preferences.getINSTANCE(context, PREFERENCES_PRODUCTS_CARTED).getProductsCarted();
            cartMenuItem.setIcon(Utils.convertLayoutToImage(context,productsCartedId.size(),R.drawable.ic_cart));
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.myCart){
            navController.navigate(R.id.action_navigation_notifications_to_navigation_cart);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onRefresh() {
        if (ConnectivityReceiver.isConnected()) {
            binding.swipeRefresh.setRefreshing(true);
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    binding.swipeRefresh.setRefreshing(false);
                    displayProgressDialog(true);
//                    notificationsViewModel.getNotifications()(user.getId());
                }
            }, 3000);

        } else {
            binding.swipeRefresh.setRefreshing(false);
            displaySnackBar(true, null);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof NotificationsAdapter.NotificationsViewHolder) {
            indexNotificationRemoved = viewHolder.getAdapterPosition();
            notificationRemoved = notifications.get(indexNotificationRemoved);
            notificationsViewModel.deleteNotification(context, notificationRemoved.getId());
        }
    }
}

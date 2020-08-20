package com.example.ecommerceapp.ui.cart;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ConnectivityReceiver;
import com.example.ecommerceapp.data.Utils;
import com.example.ecommerceapp.databinding.FragmentPaymentMethodBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class PaymentMethodFragment extends Fragment implements View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiveListener{

    private NavController navController;
    private FragmentPaymentMethodBinding binding;
    private View root;
    private Utils utils;
    private Context context;
    private Activity activity;
    private Bundle bundle;

    public PaymentMethodFragment() {
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
            bundle = getArguments();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_payment_method,container,false);
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

        binding.toolbar.setTitle(getString(R.string.title_Payment_Options));
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_navigation_back_up));
        binding.toolbar.setNavigationOnClickListener(this);

        selectActivity();
    }

    @Override
    public void onStart() {
        super.onStart();

        binding.cardCola.setOnClickListener(this::onClick);
        binding.cashCola.setOnClickListener(this::onClick);
    }


    private void selectActivity(){
        if(activity.getClass().getSimpleName().contains("HomeActivity")) {
            binding.setPadding(true);
        }
    }

    private void displaySnackBar(boolean show, String msg, int duration){
        if(msg == null) {
            msg = getString(R.string.checkConnection);
        }
        utils = new Utils(context);
        utils.snackBar(root.findViewById(R.id.containerPaymentOptions), msg, R.string.ok, duration);
        utils.displaySnackBar(show);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_cola:
                bundle.putInt(getString(R.string.title_Payment_Options), R.string.card);
//                if(paymentSaved){
//                paymentCard();
//                navController.navigate(R.id.action_paymentMethodFragment_to_completeOrderFragment, bundle);
//              }else{
//                navController.navigate(R.id.action_paymentMethodFragment_to_paymentCardFragment, bundle);
//              }
                break;
            case R.id.cash_cola:
                bundle.putInt(getString(R.string.title_Payment_Options), R.string.cash);
                navController.navigate(R.id.action_paymentMethodFragment_to_completeOrderFragment, bundle);
                break;

            default:
                activity.onBackPressed();
                break;
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(!isConnected) {
            displaySnackBar(true, null, 0);
        }
    }
}

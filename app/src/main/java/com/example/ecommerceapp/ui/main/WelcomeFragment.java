package com.example.ecommerceapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.databinding.FragmentWelcomeBinding;
import com.example.ecommerceapp.ui.home.HomeActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;


public class WelcomeFragment extends Fragment {

    private static final String PREFERENCES_DATA_USER = "DATA_USER";

    private FragmentWelcomeBinding binding;
    private View root;
    private Thread thread;
    private Intent intent;
    private Animation animationTop, animationBottom;
    private NavController navController;

    public WelcomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                loginAuto();
            }

        });

        thread.start();
    }

        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_welcome,container,false);
        root = binding.getRoot();

        animationTop = AnimationUtils.loadAnimation(getContext(),R.anim.translate_from_top);
        animationBottom = AnimationUtils.loadAnimation(getContext(),R.anim.translate_from_bottom);
        binding.appLogo.startAnimation(animationTop);
        binding.welcomeTv.startAnimation(animationBottom);

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }

    private void loginAuto(){
        if(Preferences.getINSTANCE(getContext(), PREFERENCES_DATA_USER).isDataUserExist()){
            intent = new Intent(getContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }else {
            navController.navigate(R.id.action_navigation_welcome_to_mainFragment);

        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if(intent != null) {
            loginAuto();
        }
    }

}

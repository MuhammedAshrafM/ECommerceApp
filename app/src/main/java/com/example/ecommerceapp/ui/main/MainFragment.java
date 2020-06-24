package com.example.ecommerceapp.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.databinding.FragmentMainBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class MainFragment extends Fragment implements View.OnClickListener {

    private FragmentMainBinding binding;
    private View root;
    private NavController navController;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main,container,false);
        root = binding.getRoot();

        // Inflate the layout for this fragment
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

        Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.translate_from_top);
        binding.appLogo.startAnimation(animation);

        binding.joinAppButton.setOnClickListener(this);
        binding.openAppButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.joinAppButton:
                navController.navigate(R.id.action_mainFragment_to_registerFragment);
                break;

            case R.id.openAppButton:
                navController.navigate(R.id.action_mainFragment_to_loginFragment);
                break;

            default:
                break;
        }
    }
}

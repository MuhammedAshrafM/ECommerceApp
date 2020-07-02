package com.example.ecommerceapp.data;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.databinding.LayoutFilterBinding;
import com.example.ecommerceapp.pojo.BrandModel;
import com.example.ecommerceapp.ui.search.FilterDialogAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

public class FilterBrandDialog extends Dialog implements View.OnClickListener {

    private LayoutFilterBinding bindingFilter;
    private ArrayList<BrandModel> brands;
    private FilterDialogAdapter adapter;
    private View view;
    private Activity activity;
    private Context context;
    private ItemDialogClickListener listener;
    private static FilterBrandDialog INSTANCE;

    private FilterBrandDialog(@NonNull Context context, Activity activity, int themeResId,
                             ItemDialogClickListener listener, ArrayList<BrandModel> brandModels) {
        super(context, themeResId);
        this.context = context;
        this.activity = activity;
        this.listener = listener;

        this.brands = brandModels;
    }

    public synchronized static FilterBrandDialog getINSTANCE(Context context, Activity activity, int themeResId,
                                                ItemDialogClickListener listener, ArrayList<BrandModel> brandModels){

            INSTANCE = new FilterBrandDialog(context, activity, themeResId, listener, brandModels);
            INSTANCE.setCancelable(true);
            INSTANCE.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            INSTANCE.getWindow().setGravity(Gravity.BOTTOM);
            INSTANCE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return INSTANCE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view = activity.getLayoutInflater().inflate(R.layout.layout_filter, null);
        bindingFilter = LayoutFilterBinding.bind(view);
        setContentView(view);
        adapter = new FilterDialogAdapter(context, R.string.brand);
//        bindingFilter.recyclerViewSubCategories.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        bindingFilter.recyclerViewSubCategories.setLayoutManager(linearLayoutManager);
        bindingFilter.recyclerViewSubCategories.setAdapter(adapter);

        bindingFilter.doneBt.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        bindingFilter.typeFilterTv.setText(context.getString(R.string.brand));
        adapter.setBrandList(brands);
    }

    public ArrayList<String> getBrandsSelected() {
        return adapter.getBrandsSelected();
    }

    private void setListen(View view, int id){
        dismiss();
        listener.onItemDialogClick(view, id);

    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.done_bt){
            setListen(view, R.string.brand);
        }
    }
}

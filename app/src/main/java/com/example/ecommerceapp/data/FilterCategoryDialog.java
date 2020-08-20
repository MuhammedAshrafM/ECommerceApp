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
import com.example.ecommerceapp.pojo.SubCategoryModel;
import com.example.ecommerceapp.ui.search.FilterDialogAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

public class FilterCategoryDialog extends Dialog implements View.OnClickListener {

    private LayoutFilterBinding bindingFilter;
    private ArrayList<SubCategoryModel> categories;
    private FilterDialogAdapter adapter;
    private View view;
    private Activity activity;
    private Context context;
    private ItemDialogClickListener listener;
    private static FilterCategoryDialog INSTANCE;

    private FilterCategoryDialog(@NonNull Context context, Activity activity, int themeResId,
                                ItemDialogClickListener listener, ArrayList<SubCategoryModel> subCategoryModels) {
        super(context, themeResId);
        this.context = context;
        this.activity = activity;
        this.listener = listener;

        this.categories = subCategoryModels;
    }

    public synchronized static FilterCategoryDialog getINSTANCE(Context context, Activity activity, int themeResId,
                                                   ItemDialogClickListener listener, ArrayList<SubCategoryModel> subCategoryModels){

            INSTANCE = new FilterCategoryDialog(context, activity, themeResId, listener, subCategoryModels);
            INSTANCE.setCancelable(true);
            INSTANCE.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            INSTANCE.getWindow().setGravity(Gravity.BOTTOM);
            INSTANCE.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            INSTANCE.setCanceledOnTouchOutside(false);

        return INSTANCE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = activity.getLayoutInflater().inflate(R.layout.layout_filter, null);
        bindingFilter = LayoutFilterBinding.bind(view);
        setContentView(view);
        adapter = new FilterDialogAdapter(context, R.string.category);
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

        bindingFilter.typeFilterTv.setText(context.getString(R.string.category));
        adapter.setSubCategoryList(categories);
    }


    public ArrayList<String> getSubCategoriesSelected() {
        return adapter.getSubCategoriesSelected();
    }

    private void setListen(View view, int id){
        dismiss();
        listener.onItemDialogClick(view, id);
    }
    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.done_bt){
            setListen(view, R.string.category);
        }
    }
}

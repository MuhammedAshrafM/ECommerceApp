package com.example.ecommerceapp.data;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.databinding.LayoutFilterPriceBinding;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.Map;

import androidx.annotation.NonNull;

public class FilterPriceDialog extends Dialog implements View.OnClickListener, TextWatcher {

    private LayoutFilterPriceBinding binding;
    private Map<String, Double> infoProducts;
    private Number minValue, maxValue;
    private Double minPrice, maxPrice;
    private String minPriceKey, maxPriceKey;
    private View view;
    private Activity activity;
    private Context context;
    private ItemDialogClickListener listener;
    private static FilterPriceDialog INSTANCE;
    private boolean editable;

    private FilterPriceDialog(@NonNull Context context, Activity activity, int themeResId,
                             ItemDialogClickListener listener, Map<String, Double> infoProducts) {
        super(context, themeResId);
        this.context = context;
        this.activity = activity;
        this.listener = listener;

        this.infoProducts = infoProducts;
    }

    public synchronized static FilterPriceDialog getINSTANCE(Context context, Activity activity, int themeResId,
                                                ItemDialogClickListener listener, Map<String, Double> infoProducts){

            INSTANCE = new FilterPriceDialog(context, activity, themeResId, listener, infoProducts);
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

        view = activity.getLayoutInflater().inflate(R.layout.layout_filter_price, null);
        binding = LayoutFilterPriceBinding.bind(view);
        setContentView(view);

        minPriceKey = context.getString(R.string.minPriceKey);
        maxPriceKey = context.getString(R.string.maxPriceKey);

        minPrice = infoProducts.get(minPriceKey);
        maxPrice = infoProducts.get(maxPriceKey);

        minValue = (Double) minPrice;
        maxValue = (Double) maxPrice;

        binding.priceRangeRsb.setRangeValues(minPrice, maxPrice);
        binding.priceRangeRsb.setTextAboveThumbsColorResource(R.color.blackColor);
        binding.minPriceEt.setText(String.valueOf(minPrice));
        binding.maxPriceEt.setText(String.valueOf(maxPrice));
    }

    @Override
    protected void onStart() {
        super.onStart();

        editable = false;


        binding.priceRangeRsb.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar rangeSeekBar, Object min, Object max) {
                editable = false;
                minValue = (Double) min;
                maxValue = (Double) max;
                binding.minPriceEt.setText(String.valueOf(minValue));
                binding.maxPriceEt.setText(String.valueOf(maxValue));
            }
        });

        binding.minPriceEt.addTextChangedListener(this);
        binding.maxPriceEt.addTextChangedListener(this);
        binding.doneBt.setOnClickListener(this);
    }

    public Map<String, Double> getPriceRangeSelected() {
        infoProducts.put(minPriceKey, (Double) minValue);
        infoProducts.put(maxPriceKey, (Double) maxValue);
        return infoProducts;
    }

    private void validateData(){
        String min = binding.minPriceEt.getText().toString().trim();
        String max = binding.maxPriceEt.getText().toString().trim();
        if(min.equals("") || Double.valueOf(min) < minPrice || Double.valueOf(min) > maxPrice){
            min = String.valueOf(minPrice);
            binding.minPriceEt.setText(min);

        }else if(max.equals("") || Double.valueOf(max) > maxPrice || Double.valueOf(max) < minPrice){
            max = String.valueOf(maxPrice);
            binding.maxPriceEt.setText(max);

        }
        minValue = Double.valueOf(min);
        maxValue = Double.valueOf(max);

        setListen(view, R.string.priceRange);
    }
    private void setListen(View view, int id){
        dismiss();
        listener.onItemDialogClick(view, id);

    }
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.done_bt){
            if(editable){
                validateData();
            }else {
                setListen(view, R.string.priceRange);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        this.editable = true;
    }
}

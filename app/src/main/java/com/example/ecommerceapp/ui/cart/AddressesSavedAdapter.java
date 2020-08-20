package com.example.ecommerceapp.ui.cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.data.ItemClickListener;
import com.example.ecommerceapp.data.Preferences;
import com.example.ecommerceapp.pojo.AddressModel;
import com.example.ecommerceapp.pojo.ProductModel;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class AddressesSavedAdapter extends RecyclerView.Adapter<AddressesSavedAdapter.AddressesSavedViewHolder>
    implements View.OnClickListener {

    private ArrayList<AddressModel> addresses = new ArrayList<>();
    private Context context;
    private ItemClickListener listener;
    private AddressModel addressModel;
    private static final String PREFERENCES_ADDRESSES_SAVED = "ADDRESSES_SAVED";

    public AddressesSavedAdapter(Context context, ItemClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressesSavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressesSavedViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_address, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull AddressesSavedViewHolder holder, int position) {


        holder.addressTv.setText(addresses.get(position).getAddress());
        holder.mobileNumberTv.setText(addresses.get(position).getMobileNumber());
        holder.keyCountryCp.setCountryForNameCode(addresses.get(position).getCountryCodeName());

        if(addressModel.getId().equals(addresses.get(position).getId())){
            holder.addressSelectedIbt.setVisibility(View.VISIBLE);
        }else {
            holder.addressSelectedIbt.setVisibility(View.INVISIBLE);
        }

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).selectAddressSaved(position);

                listener.onItemClick(view, position);
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
        });
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public void setList(ArrayList<AddressModel> addressModels) {
        addressModel = Preferences.getINSTANCE(context, PREFERENCES_ADDRESSES_SAVED).getAddressSavedSelected();
        this.addresses = addressModels;
        notifyDataSetChanged();
    }

    public void removeItem(int position){
        addresses.remove(position);
        notifyDataSetChanged();
    }

    public void restoreItem(AddressModel addressModel, int position){
        addresses.add(position, addressModel);
        notifyDataSetChanged();
    }
    @Override
    public void onClick(View view) {

    }

    public class AddressesSavedViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView addressTv, mobileNumberTv;
        private CountryCodePicker keyCountryCp;
        private ImageButton addressSelectedIbt;
        public ConstraintLayout foregroundCola;

        private ItemClickListener itemClickListener;

        private AddressesSavedViewHolder(@NonNull View itemView) {
            super(itemView);

            addressTv = itemView.findViewById(R.id.address_tv);
            mobileNumberTv = itemView.findViewById(R.id.mobile_number_tv);
            keyCountryCp = itemView.findViewById(R.id.key_country_cp);
            addressSelectedIbt = itemView.findViewById(R.id.address_selected_ibt);
            foregroundCola = itemView.findViewById(R.id.foreground_cola);

            itemView.setOnClickListener(this);
        }


        private void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener=itemClickListener;
        }

        @Override
        public void onClick(View view) {
            this.itemClickListener.onItemClick(view, this.getAdapterPosition());
        }
    }
}

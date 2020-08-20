package com.example.ecommerceapp.data;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchListener {

    void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
}

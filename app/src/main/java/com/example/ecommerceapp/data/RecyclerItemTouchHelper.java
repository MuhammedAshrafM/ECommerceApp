package com.example.ecommerceapp.data;

import android.graphics.Canvas;
import android.view.View;

import com.example.ecommerceapp.R;
import com.example.ecommerceapp.ui.cart.AddressesSavedAdapter;
import com.example.ecommerceapp.ui.notifications.NotificationsAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private ItemTouchListener listener;
    private int recyclerViewId;

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, int recyclerViewId, ItemTouchListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
        this.recyclerViewId = recyclerViewId;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        if(listener != null){
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        getDefaultUIUtil().clearView(selectViewHolder(viewHolder));
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        if(viewHolder != null){
            getDefaultUIUtil().onSelected(selectViewHolder(viewHolder));
        }
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        getDefaultUIUtil().onDraw(c, recyclerView, selectViewHolder(viewHolder), dX, dY, actionState, isCurrentlyActive);
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c, @NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        getDefaultUIUtil().onDrawOver(c, recyclerView, selectViewHolder(viewHolder), dX, dY, actionState, isCurrentlyActive);
    }

    private View selectViewHolder(RecyclerView.ViewHolder viewHolder){
        View foregroundView = null;
        if(recyclerViewId == R.id.recyclerViewNotifications){
            foregroundView = ((NotificationsAdapter.NotificationsViewHolder)viewHolder).foregroundCola;
        }else if(recyclerViewId == R.id.recyclerViewAddressesSaved){
            foregroundView = ((AddressesSavedAdapter.AddressesSavedViewHolder)viewHolder).foregroundCola;
        }
        return foregroundView;
    }
}

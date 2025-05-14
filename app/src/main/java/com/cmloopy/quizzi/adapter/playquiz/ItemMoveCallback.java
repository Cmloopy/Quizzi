package com.cmloopy.quizzi.adapter.playquiz;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ItemMoveCallback extends ItemTouchHelper.Callback {

    public interface ItemTouchHelperContract {
        void onRowMoved(int fromPosition, int toPosition);
    }

    private final ItemTouchHelperContract adapter;

    public ItemMoveCallback(ItemTouchHelperContract adapter) {
        this.adapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true; // Cho phép người dùng kéo bằng nhấn giữ
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false; // Không hỗ trợ vuốt trái/phải
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // Cho phép kéo lên và xuống
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        adapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // Không cần xử lý vuốt
    }
}

package com.cmloopy.quizzi.utils.QuestionCreate.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class QCGenericSelectionDialog {
    private final Context context;
    private final OnItemSelectedListener listener;
    private final List<Integer> items;
    private final String headerTitle;
    private final String unit;
    private final int defaultSelectedValue;

    public interface OnItemSelectedListener {
        void onItemSelected(int selectedItem);
    }

    private QCGenericSelectionDialog(Builder builder) {
        this.context = builder.context;
        this.listener = builder.listener;
        this.items = builder.items;
        this.headerTitle = builder.headerTitle;
        this.unit = builder.unit;
        this.defaultSelectedValue = builder.defaultSelectedValue;
    }

    public void show() {
        View dialogView = createDialogView();
        Dialog dialog = createDialog(dialogView);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(R.drawable.ui_qc_rounded_dialog);
        window.setLayout(
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.75),
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        setupDialogInteractions(dialogView, dialog);
        dialog.show();
    }

    private View createDialogView() {
        View dialogView = LayoutInflater.from(context).inflate(R.layout.ui_qc_dialog_generic_selection, null);

        TextView headerTextView = dialogView.findViewById(R.id.text_header);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewSelection);

        headerTextView.setText(headerTitle);

        setupRecyclerView(recyclerView);

        return dialogView;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        SelectionAdapter adapter = new SelectionAdapter(items, unit, defaultSelectedValue);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, 30, false));
    }

    private Dialog createDialog(View dialogView) {
        return new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create();
    }

    private void setupDialogInteractions(View dialogView, Dialog dialog) {
        MaterialButton btnOk = dialogView.findViewById(R.id.btnOk);
        RecyclerView recyclerView = dialogView.findViewById(R.id.recyclerViewSelection);
        SelectionAdapter adapter = (SelectionAdapter) recyclerView.getAdapter();

        btnOk.setOnClickListener(v -> {
            if (adapter != null) {
                int selectedItem = adapter.getSelectedItem();
                if (selectedItem != -1 && listener != null) {
                    listener.onItemSelected(selectedItem);
                    dialog.dismiss();
                }
            }
        });
    }

    public static class Builder {
        private final Context context;
        private OnItemSelectedListener listener;
        private List<Integer> items;
        private String headerTitle = "Select";
        private String unit = "sec";
        private int defaultSelectedValue = 20;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setItems(List<Integer> items) {
            this.items = items;
            return this;
        }

        public Builder setHeaderTitle(String headerTitle) {
            this.headerTitle = headerTitle;
            return this;
        }

        public Builder setUnit(String unit) {
            this.unit = unit;
            return this;
        }

        public Builder setDefaultSelectedValue(int defaultValue) {
            this.defaultSelectedValue = defaultValue;
            return this;
        }

        public Builder setOnItemSelectedListener(OnItemSelectedListener listener) {
            this.listener = listener;
            return this;
        }

        public QCGenericSelectionDialog build() {
            return new QCGenericSelectionDialog(this);
        }
    }

    private class SelectionAdapter extends RecyclerView.Adapter<SelectionAdapter.SelectionViewHolder> {
        private final List<Integer> items;
        private int selectedPosition = RecyclerView.NO_POSITION;
        private final String unit;

        public SelectionAdapter(List<Integer> items, String unit, int defaultSelectedValue) {
            this.items = items;
            this.unit = unit;

            // Find and set the default selected position
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i) == defaultSelectedValue) {
                    selectedPosition = i;
                    break;
                }
            }
        }

        public int getSelectedItem() {
            return selectedPosition != RecyclerView.NO_POSITION ? items.get(selectedPosition) : -1;
        }

        @NonNull
        @Override
        public SelectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.ui_qc_item_generic_selection, parent, false);
            return new SelectionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SelectionViewHolder holder, int position) {
            int item = items.get(position);
            holder.button.setText(String.format("%d %s", item, unit));

            // Set text size to 20sp

            // Update background based on selection state
            updateItemAppearance(holder, position);

            holder.itemView.setOnClickListener(v -> {
                if (selectedPosition == position) {
                    return;
                } else {
                    // Otherwise, select the new item
                    selectedPosition = position;
                }

                // Notify the entire adapter to refresh
                notifyDataSetChanged();
            });


        }

        private void updateItemAppearance(@NonNull SelectionViewHolder holder, int position) {
            if (position == selectedPosition) {
                holder.itemView.setBackgroundResource(R.drawable.ui_qc_bg_elevation_purple);
                holder.button.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                holder.itemView.setBackgroundResource(R.drawable.ui_qc_bg_elevation_white);
                holder.button.setTextColor(ContextCompat.getColor(context, R.color.black));
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class SelectionViewHolder extends RecyclerView.ViewHolder {
            MaterialButton button;

            public SelectionViewHolder(@NonNull View itemView) {
                super(itemView);
                button = itemView.findViewById(R.id.buttonItem);
            }
        }
    }

    private static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;
        private final boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position >= 0) {
                int column = position % spanCount;

                if (includeEdge) {
                    outRect.left = spacing - column * spacing / spanCount;
                    outRect.right = (column + 1) * spacing / spanCount;

                    if (position < spanCount) {
                        outRect.top = spacing;
                    }
                    outRect.bottom = spacing;
                } else {
                    outRect.left = column == 0 ? 0 : spacing / 2;
                    outRect.right = column == spanCount - 1 ? 0 : spacing / 2;
                    outRect.top = position < spanCount ? 0 : spacing;
                }
            }
        }
    }
}
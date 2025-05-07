package com.cmloopy.quizzi.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.UI_44_Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UI_44_WordAdapter extends RecyclerView.Adapter<UI_44_WordAdapter.WordViewHolder> {

    private List<UI_44_Word> wordList;
    private ItemTouchHelperAdapter touchHelperAdapter;
    private int[] cardColors = {
            R.color.option_blue,    // Blue for "This"
            R.color.option_green,   // Green for "Calendar"
            R.color.option_orange,  // Orange for "Is"
            R.color.option_red      // Red for "A"
    };

    public interface ItemTouchHelperAdapter {
        void onItemMove(int fromPosition, int toPosition);
        void onItemDismiss(int position);
    }

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    private final OnStartDragListener dragListener;

    public UI_44_WordAdapter(List<UI_44_Word> wordList, OnStartDragListener dragListener) {
        this.wordList = wordList;
        this.dragListener = dragListener;
        this.touchHelperAdapter = new ItemTouchHelperAdapter() {
            @Override
            public void onItemMove(int fromPosition, int toPosition) {
                Collections.swap(wordList, fromPosition, toPosition);
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onItemDismiss(int position) {
                // We don't want to dismiss items in this case
            }
        };
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ui_44_item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        UI_44_Word word = wordList.get(position);
        Context context = holder.itemView.getContext();

        // Set word text
        holder.tvWord.setText(word.getText());

        // Set card color based on original position (to maintain colors even when reordered)
        holder.cardWord.setCardBackgroundColor(context.getResources().getColor(cardColors[word.getOriginalPosition()]));

        // Set up drag handle touch listener
        holder.ivHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(holder);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return wordList.size();
    }

    // Method to get current word order
    public List<String> getCurrentWordOrder() {
        List<String> currentOrder = new ArrayList<>();
        for (UI_44_Word word : wordList) {
            currentOrder.add(word.getText());
        }
        return currentOrder;
    }

    public ItemTouchHelperAdapter getItemTouchHelperAdapter() {
        return touchHelperAdapter;
    }

    public static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView tvWord;
        ImageView ivHandle;
        CardView cardWord;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWord = itemView.findViewById(R.id.tvWord);
            ivHandle = itemView.findViewById(R.id.ivHandle);
            cardWord = itemView.findViewById(R.id.cardWord);
        }
    }
}
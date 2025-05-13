package com.cmloopy.quizzi.adapter.playquiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.question.puzzle.PuzzlePiece;

import java.util.List;

public class PuzzleAdapter extends RecyclerView.Adapter<PuzzleAdapter.PuzzleViewHolder>
        implements ItemMoveCallback.ItemTouchHelperContract {

    public List<PuzzlePiece> answerList;
    private Context context;

    public PuzzleAdapter(Context context, List<PuzzlePiece> answerList){
        this.context = context;
        this.answerList = answerList;
    }

    @NonNull
    @Override
    public PuzzleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_puzzle, parent, false);
        return new PuzzleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PuzzleViewHolder holder, int position) {
        holder.txtPuzzle.setText(answerList.get(position).text);
    }

    @Override
    public int getItemCount() {
        return answerList.size();
    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        PuzzlePiece movedItem = answerList.remove(fromPosition);
        answerList.add(toPosition, movedItem);
        notifyItemMoved(fromPosition, toPosition);
    }

    public static class PuzzleViewHolder extends RecyclerView.ViewHolder {
        TextView txtPuzzle;

        public PuzzleViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPuzzle = itemView.findViewById(R.id.txt_title_puzzle_piece);
        }
    }
}

package com.cmloopy.quizzi.adapter.QuestionCreate;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuestionCreate.Option.PuzzleOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QCPuzzleOptionAdapter extends RecyclerView.Adapter<QCPuzzleOptionAdapter.PuzzlePieceViewHolder> {
    private Context context;
    private List<PuzzleOption> puzzlePieces;
    private List<PuzzleOption> originalPuzzlePieces;
    private OnPuzzlePieceClickListener listener;

    public interface OnPuzzlePieceClickListener {
        void onPuzzlePieceClick(int position);
        void onPuzzlePieceLongClick(int position);
        void onChangePuzzlePiecePosition(List<PuzzleOption> originalPuzzlePieces, List<PuzzleOption> newAnswers);
    }

    public QCPuzzleOptionAdapter(Context context, List<PuzzleOption> puzzlePieces, OnPuzzlePieceClickListener listener) {
        this.context = context;
        this.puzzlePieces = puzzlePieces;
        this.originalPuzzlePieces = this.puzzlePieces;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PuzzlePieceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ui_qc_item_answer_puzzle_type, parent, false);
        return new PuzzlePieceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PuzzlePieceViewHolder holder, int position) {
        PuzzleOption puzzlePiece = puzzlePieces.get(position);
        holder.cardView.setBackgroundResource(puzzlePiece.getBackground());
        holder.btnAnswer.setText(puzzlePiece.getTextOrDefault());
        holder.btnAnswer.setMovementMethod(new ScrollingMovementMethod());
        holder.btnAnswer.setVerticalScrollBarEnabled(true);

        holder.btnAnswer.setClickable(true);
        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
//        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
////                v.getParent().requestDisallowInterceptTouchEvent(true);
////                return false;
//                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    Button button = (Button) v;
//                    // Check if we're at the top or bottom edge of the button's text
//                    int scrollY = button.getScrollY();
//                    int maxScroll = button.getLayout().getHeight() - button.getHeight();
//
//                    // If we're not at an edge or if we're scrolling away from an edge, disallow parent interception
//                    if ((scrollY > 0 && scrollY < maxScroll) ||
//                            (scrollY == 0 && event.getY() > 0) ||
//                            (scrollY == maxScroll && event.getY() < 0)) {
//                        v.getParent().requestDisallowInterceptTouchEvent(true);
//                    } else {
//                        // At the edge and trying to scroll further, allow parent to intercept
//                        v.getParent().requestDisallowInterceptTouchEvent(false);
//                    }
//                }
//                return false;
//            }
//        });

//        holder.itemView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
//                    v.getParent().requestDisallowInterceptTouchEvent(true);
//                } else if (event.getAction() == MotionEvent.ACTION_UP) {
//                    v.getParent().requestDisallowInterceptTouchEvent(false);
//                }
//                return false;
//            }
//        });


        holder.btnAnswer.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPuzzlePieceClick(position);
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onPuzzlePieceLongClick(position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return puzzlePieces.size();
    }

    public void updatePuzzlePiece(int position, PuzzleOption newPuzzlePiece) {
        if (position >= 0 && position < puzzlePieces.size()) {
            puzzlePieces.set(position, newPuzzlePiece);
            notifyItemChanged(position);
        }
    }

    public ItemTouchHelper createItemTouchHelper() {
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                if (originalPuzzlePieces == null) {
                    originalPuzzlePieces = new ArrayList<>(puzzlePieces);
                }

                Collections.swap(puzzlePieces, fromPosition, toPosition);
                notifyItemMoved(fromPosition, toPosition);

                return true;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);

                if (listener != null && originalPuzzlePieces != null) {
                    // Xác nhận vị trí đã thay đổi
                    listener.onChangePuzzlePiecePosition(originalPuzzlePieces, puzzlePieces);
                    originalPuzzlePieces = null; // Đặt lại danh sách gốc
                }
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // No swipe action needed

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState,
                                    boolean isCurrentlyActive) {
                // Add custom drawing if needed
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });
    }

    // Animate the move of items
    private void animateMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                             int fromPosition, int toPosition) {
        final View view = viewHolder.itemView;
        final RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManager != null) {
            float startTranslationY = view.getTranslationY();
            float endTranslationY = toPosition > fromPosition ?
                    view.getHeight() : -view.getHeight();

            // Animate the card being moved
            ObjectAnimator translateY = ObjectAnimator.ofFloat(view, "translationY",
                    startTranslationY, endTranslationY);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.9f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 0.9f, 1f);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(translateY, scaleX, scaleY);
            animatorSet.setDuration(300);
            animatorSet.start();
        }
    }

    public static class PuzzlePieceViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        Button btnAnswer;
        ImageView checkIcon;


        public PuzzlePieceViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_container);
            btnAnswer = itemView.findViewById(R.id.text_answer);
            checkIcon = itemView.findViewById(R.id.drag_icon_btn);
        }
    }
}
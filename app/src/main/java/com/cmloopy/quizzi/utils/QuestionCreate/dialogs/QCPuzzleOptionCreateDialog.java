package com.cmloopy.quizzi.utils.QuestionCreate.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuestionCreate.Option.PuzzleOption;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class QCPuzzleOptionCreateDialog {
    private final Context context;
    private final OnItemSelectedListener listener;
    private final String headerTitle;
    private Dialog dialog;
    private EditText etAnswer;
    private RecyclerView recyclerView;
    private PuzzleOption answer;
    private List<PuzzleOption> answers;
    private CardView editWrap;
    private int position;
    private boolean isMultipleChoiceQuestion;
    private int textAlignment;
    private int answerMaxlines;
    private int answerMaxLength;


    public QCPuzzleOptionCreateDialog(Builder builder) {
        this.context = builder.context;
        this.listener = builder.listener;
        this.headerTitle = builder.headerTitle;
        this.answer = builder.answer;
        this.answers = builder.answers;
        this.position = builder.position;
        this.textAlignment = builder.textAlignment;
        this.answerMaxlines = builder.answerMaxlines;
        this.answerMaxLength = builder.answerMaxLength;
        this.isMultipleChoiceQuestion = builder.isMultipleChoiceQuestion;
    }

    public void show() {
        View dialogView = createDialogView();
        dialog = createDialog(dialogView);
        setupDialogInteractions(dialogView, dialog);
        dialog.show();
    }

    private Dialog createDialog(View dialogView) {
        Dialog dialog = new MaterialAlertDialogBuilder(context)
                .setView(dialogView)
                .create();
        dialog.setOnShowListener(d -> {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(
                        (int) (context.getResources().getDisplayMetrics().widthPixels * 0.7), // 70% of screen width
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                window.setBackgroundDrawableResource(R.drawable.ui_qc_rounded_dialog);
            }
        });
        return dialog;
    }

    private void setupDialogInteractions(View dialogView, Dialog dialog) {
        etAnswer = dialogView.findViewById(R.id.et_answer);
        recyclerView = dialogView.findViewById(R.id.recyclerViewSelection);
        editWrap = dialogView.findViewById(R.id.edit_wrap);

        TextView headerTextView = dialogView.findViewById(R.id.text_header);
        if (!TextUtils.isEmpty(headerTitle)) {
            headerTextView.setText(headerTitle);
        }

        etAnswer.setTextAlignment(textAlignment);
        etAnswer.setMaxLines(answerMaxlines);
        etAnswer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(answerMaxLength)});

        etAnswer.setText(answer.getText());
        editWrap.setBackgroundResource(answer.getBackground());

        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                String newText = s.toString().trim();
                if (!TextUtils.isEmpty(newText) && !newText.equals(answer.getText())) {
                    answer.setText(newText);
                    if (listener != null) {
                        listener.onItemSelected(position, answer);
                    }
                }
            }
        });

    }



    private View createDialogView() {
        return LayoutInflater.from(context).inflate(R.layout.ui_qc_dialog_puzzle_option_create, null);
    }

    public static class Builder {
        private final Context context;
        private OnItemSelectedListener listener;
        private String headerTitle;
        private PuzzleOption answer;
        private int position = -1;
        private List<PuzzleOption> answers;
        private boolean isMultipleChoiceQuestion = false;
        private int textAlignment = View.TEXT_ALIGNMENT_CENTER;
        private int answerMaxlines = 100;
        private int answerMaxLength = 1000;


        public Builder(Context context) {
            this.context = context;
        }

        public Builder setListener(OnItemSelectedListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setHeaderTitle(String headerTitle) {
            this.headerTitle = headerTitle;
            return this;
        }

        public Builder setAnswer(PuzzleOption answer) {
            this.answer = answer;
            return this;
        }

        public Builder setPosition(int position) {
            this.position = position;
            return this;
        }

        public Builder setAnswers(List<PuzzleOption> answers) {
            this.answers = answers;
            return this;
        }

        public Builder setMultipleChoiceQuestion(boolean isMultipleChoiceQuestion) {
            this.isMultipleChoiceQuestion = isMultipleChoiceQuestion;
            return this;
        }

        public Builder setTextAlignment(int textAlignment) {
            this.textAlignment = textAlignment;
            return this;
        }

        public Builder setAnswerMaxlines(int answerMaxlines) {
            this.answerMaxlines = answerMaxlines;
            return this;
        }

        public Builder setAnswerMaxLength(int answerMaxLength) {
            this.answerMaxLength = answerMaxLength;
            return this;
        }

        public QCPuzzleOptionCreateDialog build() {
            return new QCPuzzleOptionCreateDialog(this);
        }
    }

    private ArrayList<Integer> getLayerListFromAnswerBackground(Context context, int background) {
        ArrayList<Integer> result = new ArrayList<>();
        Drawable drawable = ContextCompat.getDrawable(context, background);
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;

            Drawable firstLayer = layerDrawable.getDrawable(0);
            if (firstLayer instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) firstLayer;
                int firstColor = getGradientDrawableColor(gradientDrawable);
                result.add(firstColor);
            }

            Drawable secondLayer = layerDrawable.getDrawable(1);
            if (secondLayer instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) secondLayer;
                int secondColor = getGradientDrawableColor(gradientDrawable);
                result.add(secondColor);
            }
        }
        return result;
    }

    private int getGradientDrawableColor(GradientDrawable drawable) {
        try {
            Field colorField = GradientDrawable.class.getDeclaredField("mColorStateList");
            colorField.setAccessible(true);
            ColorStateList colorStateList = (ColorStateList) colorField.get(drawable);
            return (colorStateList != null) ? colorStateList.getDefaultColor() : Color.TRANSPARENT;
        } catch (Exception e) {
            e.printStackTrace();
            return Color.TRANSPARENT;
        }
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position, PuzzleOption answer);
//        void onItemSelectedUpdateAll(List<PuzzleOption> answer);
    }
}
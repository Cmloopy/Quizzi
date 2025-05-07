package com.cmloopy.quizzi.fragment.QuizCreate.before;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuizCreate.before.Question;
import com.cmloopy.quizzi.models.QuizCreate.before.SliderQuestion;
import com.cmloopy.quizzi.utils.QuizCreate.slider.QCCustomSlider;

import java.util.Arrays;
import java.util.List;

public class QCSliderQuestionTypeFragment extends QCBaseQuestionFragment {
    private static final String ARG_MIN_VALUE = "min_value";
    private static final String ARG_MAX_VALUE = "max_value";
    private static final String ARG_CORRECT_VALUE = "correct_value";
    private static final String ARG_LAMBDA = "lambda";

    private SliderQuestion sliderQuestion;
    private QCCustomSlider customSlider;
    private EditText minValueEdit, maxValueEdit, answerEdit;
    private Spinner marginSpinner;

    public static QCSliderQuestionTypeFragment newInstance(SliderQuestion question) {
        QCSliderQuestionTypeFragment fragment = new QCSliderQuestionTypeFragment();
        Bundle args = createBaseBundle(question);
        args.putInt(ARG_MIN_VALUE, question.getMinValue());
        args.putInt(ARG_MAX_VALUE, question.getMaxValue());
        args.putInt(ARG_CORRECT_VALUE, question.getCorrectValue());
        args.putString(ARG_LAMBDA, question.getLambda());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sliderQuestion = new SliderQuestion();
        if (getArguments() != null) {
            initializeSliderQuestion(getArguments());
            if(sliderQuestion.isDefaultInstance()) {
                setDefaultSliderQuestion();
            }
        } else {
            setDefaultSliderQuestion();
        }
    }

    private void initializeSliderQuestion(Bundle args) {
        super.initializeBaseQuestion(sliderQuestion, args);
        sliderQuestion.setMinValue(args.getInt(ARG_MIN_VALUE, 0));
        sliderQuestion.setMaxValue(args.getInt(ARG_MAX_VALUE, 100));
        sliderQuestion.setCorrectValue(args.getInt(ARG_CORRECT_VALUE, 0));
        sliderQuestion.setLambda(args.getString(ARG_LAMBDA, "Default"));
    }

    private void setDefaultSliderQuestion() {
        super.setDefaultBaseQuestion(sliderQuestion);
        sliderQuestion.setMinValue(0);
        sliderQuestion.setMaxValue(100);
        sliderQuestion.setCorrectValue(50);
        sliderQuestion.setLambda("Default");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_slider, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(sliderQuestion);
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        customSlider = view.findViewById(R.id.custom_slider);
        minValueEdit = view.findViewById(R.id.min_value_edit);
        maxValueEdit = view.findViewById(R.id.max_value_edit);
        answerEdit = view.findViewById(R.id.answer_edit);
        marginSpinner = view.findViewById(R.id.margin_spinner);

        customSlider.setMinValue(sliderQuestion.getMinValue());
        customSlider.setMaxValue(sliderQuestion.getMaxValue());
        customSlider.setCurrentValue(Math.round((sliderQuestion.getMinValue() + sliderQuestion.getMaxValue()) / 2));
        setListeners();
    }

    private void setListeners() {
        minValueEdit.setText(String.valueOf(sliderQuestion.getMinValue()));
        maxValueEdit.setText(String.valueOf(sliderQuestion.getMaxValue()));
        answerEdit.setText(String.valueOf(sliderQuestion.getCorrectValue()));

        minValueEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    sliderQuestion.setMinValue(Integer.parseInt(s.toString()));
                    customSlider.setMinValue(sliderQuestion.getMinValue());
                    notifyQuestionUpdated2();
                } catch (NumberFormatException ignored) {}
            }
        });

        maxValueEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    sliderQuestion.setMaxValue(Integer.parseInt(s.toString()));
                    customSlider.setMaxValue(sliderQuestion.getMaxValue());
                    notifyQuestionUpdated2();
                } catch (NumberFormatException ignored) {}
            }
        });

        answerEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    sliderQuestion.setCorrectValue(Integer.parseInt(s.toString()));
                    notifyQuestionUpdated2();
                } catch (NumberFormatException ignored) {}
            }
        });

        List<String> sliderItems = Arrays.asList("Default", "Small", "Medium", "Large");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sliderItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        marginSpinner.setAdapter(adapter);
        marginSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sliderQuestion.setLambda(parent.getItemAtPosition(position).toString());
                notifyQuestionUpdated2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void notifyQuestionUpdated2() {
        if (listener != null) {
            listener.onUpdateQuestion(sliderQuestion.getPosition(), sliderQuestion);
        }
    }

    @Override
    protected void onCoverImageClicked() {
        // Implement as needed
    }

    @Override
    public Question getCurrentQuestion() {
        return sliderQuestion;
    }
}

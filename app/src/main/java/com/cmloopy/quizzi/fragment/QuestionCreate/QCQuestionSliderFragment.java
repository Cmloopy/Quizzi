package com.cmloopy.quizzi.fragment.QuestionCreate;

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
import com.cmloopy.quizzi.adapter.QuestionCreate.QCChoiceOptionAdapter;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateSlider;
import com.cmloopy.quizzi.utils.QuestionCreate.slider.QCCustomSlider;

import java.util.Arrays;
import java.util.List;

public class QCQuestionSliderFragment extends QCBaseQuestionFragment {
    private static final String ARG_RESPONSE_CHOICE = "response_slider";
    private QuestionCreateSlider questionSlider;
    private QCChoiceOptionAdapter answerAdapter;
    private QCCustomSlider customSlider;
    private EditText minValueEdit, maxValueEdit, answerEdit;
    private Spinner colorSpinner;
    public static QCQuestionSliderFragment newInstance(QuestionCreateSlider responseChoice) {
        QCQuestionSliderFragment fragment = new QCQuestionSliderFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_RESPONSE_CHOICE, responseChoice);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questionSlider = (QuestionCreateSlider) getArguments().getSerializable(ARG_RESPONSE_CHOICE);
            if (questionSlider == null) {
                questionSlider = new QuestionCreateSlider();
            }
        } else {
            questionSlider = new QuestionCreateSlider();
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_slider, container, false);
        super.onCreateBaseView(view);
        super.setUpBaseView(getCurrentQuestion());
        setupViews(view);
        return view;
    }

    private void setupViews(View view) {
        customSlider = view.findViewById(R.id.custom_slider);
        minValueEdit = view.findViewById(R.id.min_value_edit);
        maxValueEdit = view.findViewById(R.id.max_value_edit);
        answerEdit = view.findViewById(R.id.answer_edit);
        colorSpinner = view.findViewById(R.id.margin_spinner);

        customSlider.setMinValue(questionSlider.getMinValue());
        customSlider.setMaxValue(questionSlider.getMaxValue());
        customSlider.setCurrentValue(Math.round((questionSlider.getMinValue() + questionSlider.getMaxValue()) / 2));
        setListeners();
    }

    private void setListeners() {
        minValueEdit.setText(String.valueOf(questionSlider.getMinValue()));
        maxValueEdit.setText(String.valueOf(questionSlider.getMaxValue()));
        answerEdit.setText(String.valueOf(questionSlider.getCorrectAnswer()));

        minValueEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    questionSlider.setMinValue(Integer.parseInt(s.toString()));
                    customSlider.setMinValue(questionSlider.getMinValue());
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
                    questionSlider.setMaxValue(Integer.parseInt(s.toString()));
                    customSlider.setMaxValue(questionSlider.getMaxValue());
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
                    questionSlider.setCorrectAnswer(Integer.parseInt(s.toString()));
                    notifyQuestionUpdated2();
                } catch (NumberFormatException ignored) {}
            }
        });

        List<String> sliderItems = Arrays.asList("Orange", "Green", "Blue", "Primary");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, sliderItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);
        int initPosition = sliderItems.indexOf(questionSlider.getColor());
        if(initPosition != -1) {
            colorSpinner.setSelection(initPosition);
        }
        colorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionSlider.setColor(parent.getItemAtPosition(position).toString());
                notifyQuestionUpdated2();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void notifyQuestionUpdated2() {
        if (listener != null) {
            listener.onUpdateQuestion(questionSlider.getPosition(), questionSlider);
        }
    }

    @Override
    protected void onCoverImageClicked() {

    }

    @Override
    public QuestionCreate getCurrentQuestion() {
        return questionSlider;
    }
}

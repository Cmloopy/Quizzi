package com.cmloopy.quizzi.utils.QuizCreate.dialogs;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.fragment.QuizCreate.before.QCCheckboxQuestionTypeFragment;
import com.cmloopy.quizzi.fragment.QuizCreate.before.QCPuzzleQuestionTypeFragment;
import com.cmloopy.quizzi.fragment.QuizCreate.before.QCQuizQuestionTypeFragment;
import com.cmloopy.quizzi.fragment.QuizCreate.before.QCQuizAudioQuestionTypeFragment;
import com.cmloopy.quizzi.fragment.QuizCreate.before.QCSayWordQuestionTypeFragment;
import com.cmloopy.quizzi.fragment.QuizCreate.before.QCSliderQuestionTypeFragment;
import com.cmloopy.quizzi.fragment.QuizCreate.before.QCTrueFalseQuestionTypeFragment;
import com.cmloopy.quizzi.fragment.QuizCreate.before.QCTypeQuestionTypeFragment;
import com.cmloopy.quizzi.models.QuizCreate.before.QCQuestionType;
import com.cmloopy.quizzi.models.QuizCreate.before.CheckboxQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.PuzzleQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.Question;
import com.cmloopy.quizzi.models.QuizCreate.before.QuizAudioQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.QuizQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.SayWordQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.SliderQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.TrueFalseQuestion;
import com.cmloopy.quizzi.models.QuizCreate.before.TypeQuestion;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionType;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionChoice;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionPuzzle;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionSayWord;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionSlider;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionTrueFalse;
import com.cmloopy.quizzi.models.QuizCreate.after.QuestionTypeText;
import com.cmloopy.quizzi.utils.QuizCreate.sheet.after.QuestionTypeBottomSheetFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QCHelper {
    public static void showQuestionTypeBottomSheet(
            @NonNull Context context,
            @NonNull FragmentManager fragmentManager,
            @NonNull QuestionTypeBottomSheetFragment.OnQuestionTypeSelectedListener onQuestionTypeSelectedListener
    ) {
        QuestionTypeBottomSheetFragment bottomSheetFragment = new QuestionTypeBottomSheetFragment();
        bottomSheetFragment.setOnQuestionTypeSelectedListener(questionType -> {
            onQuestionTypeSelectedListener.onQuestionTypeSelected(questionType);
        });
        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
    }

    public static void showQuestionTypeBottomSheet2(
            @NonNull Context context,
            @NonNull FragmentManager fragmentManager,
            @NonNull com.cmloopy.quizzi.utils.QuizCreate.sheet.before.QuestionTypeBottomSheetFragment.OnQuestionTypeSelectedListener onQuestionTypeSelectedListener
    ) {
        com.cmloopy.quizzi.utils.QuizCreate.sheet.before.QuestionTypeBottomSheetFragment bottomSheetFragment = new com.cmloopy.quizzi.utils.QuizCreate.sheet.before.QuestionTypeBottomSheetFragment();
        bottomSheetFragment.setOnQuestionTypeSelectedListener(questionType -> {
            onQuestionTypeSelectedListener.onQuestionTypeSelected(questionType);
        });
        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
    }

    public static void navigateToQuestionCreation(
            @NonNull FragmentManager fragmentManager,
            @NonNull QCQuestionType questionType
    ) {
        Fragment fragment = null;
        switch (questionType.getName()) {
            case "Quiz":
                fragment = new QCQuizQuestionTypeFragment();
                break;
            case "True or False":
                fragment = new QCTrueFalseQuestionTypeFragment();
                break;
            case "Puzzle":
                fragment = new QCPuzzleQuestionTypeFragment();
                break;
            case "Checkbox":
                fragment = new QCCheckboxQuestionTypeFragment();
                break;
            case "Type Answer":
                fragment = new QCTypeQuestionTypeFragment();
                break;
            case "Slider":
                fragment = new QCSliderQuestionTypeFragment();
                break;
            case "Quiz Audio":
                fragment = new QCQuizAudioQuestionTypeFragment();
                break;
            case "Say Word":
                fragment = new QCSayWordQuestionTypeFragment();
                break;
        }

        if (fragment != null) {
            loadQuestionTypeFrame(fragmentManager, fragment);
        }
    }


    //        Map<String, Integer> mp = new HashMap<>();
//        mp.put("Slider", TYPE_SLIDER);
//        mp.put("Quiz", TYPE_QUIZ);
//        mp.put("Checkbox", TYPE_CHECKBOX);
//        mp.put("Puzzle", TYPE_PUZZLE);
//        mp.put("Text", TYPE_TEXT);
//        mp.put("Quiz Audio", TYPE_QUIZ_AUDIO);
//        mp.put("True/False", TYPE_TRUE_FALSE);
//        mp.put("Say Word", TYPE_SAY_WORD);
    public static void navigateToQuestionCreation2(
            @NonNull FragmentManager fragmentManager,
            @NonNull QuestionType questionType
    ) {
        Fragment fragment = null;
        switch (questionType.getName()) {
            case "Quiz":
                fragment = new QCQuizQuestionTypeFragment();
                break;
            case "True/False":
                fragment = new QCTrueFalseQuestionTypeFragment();
                break;
            case "Puzzle":
                fragment = new QCPuzzleQuestionTypeFragment();
                break;
            case "Checkbox":
                fragment = new QCCheckboxQuestionTypeFragment();
                break;
            case "Text":
                fragment = new QCTypeQuestionTypeFragment();
                break;
            case "Slider":
                fragment = new QCSliderQuestionTypeFragment();
                break;
            case "Quiz Audio":
                fragment = new QCQuizAudioQuestionTypeFragment();
                break;
            case "Say Word":
                fragment = new QCSayWordQuestionTypeFragment();
                break;
        }

        if (fragment != null) {
            loadQuestionTypeFrame(fragmentManager, fragment);
        }
    }

    private static void loadQuestionTypeFrame(
            @NonNull FragmentManager fragmentManager,
            @NonNull Fragment fragment
    ) {
        fragmentManager
                .beginTransaction()
                .replace(R.id.question_type_frame_container, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }
    public static void applyLayerColorToIcon(Context context, int backgroundRes, ImageView iconView) {
        if (iconView == null) return;

        ArrayList<Integer> colors = getLayerListFromAnswerBackground(context, backgroundRes);
        if (!colors.isEmpty()) {
            int color = colors.get(0);

            Drawable drawable = iconView.getDrawable();
            if (drawable != null) {
                drawable = DrawableCompat.wrap(drawable.mutate());
                DrawableCompat.setTint(drawable, color);
                iconView.setImageDrawable(drawable);
            }
        }
    }

    public static ArrayList<Integer> getLayerListFromAnswerBackground(Context context, int background) {
        ArrayList<Integer> result = new ArrayList<>();
        Drawable drawable = context.getDrawable(background);

        if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;

            for (int i = 0; i < layerDrawable.getNumberOfLayers(); i++) {
                Drawable layer = layerDrawable.getDrawable(i);
                if (layer instanceof GradientDrawable) {
                    int color = getGradientDrawableColor((GradientDrawable) layer);
                    if (color != android.graphics.Color.TRANSPARENT) {
                        result.add(color);
                    }
                }
            }
        }
        return result;
    }

    private static int getGradientDrawableColor(GradientDrawable drawable) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return (drawable.getColor() != null) ? drawable.getColor().getDefaultColor() : android.graphics.Color.TRANSPARENT;
        }
        return android.graphics.Color.TRANSPARENT;
    }
    public static class QuestionTypeMapper {

        private static final Map<String, Class<?>> questionTypeMap = new HashMap<>();

        static {
            questionTypeMap.put("Quiz", QuizQuestion.class);
            questionTypeMap.put("True or False", TrueFalseQuestion.class);
            questionTypeMap.put("Puzzle", PuzzleQuestion.class);
            questionTypeMap.put("Type Answer", TypeQuestion.class);
            questionTypeMap.put("Quiz Audio", QuizAudioQuestion.class);
            questionTypeMap.put("Slider", SliderQuestion.class);
            questionTypeMap.put("Checkbox", CheckboxQuestion.class);
            questionTypeMap.put("Say Word", SayWordQuestion.class);
        }


//        public static Question createQuestionInstance(List<Question> questions, String type) {
//            Class<?> clazz = questionTypeMap.get(type);
//            Question question;
//            switch (type) {
//                case "Quiz":
//                    question = new QuizQuestion();
//                    question.setPosition(questions.size());
//                    break;
////                    ((QuizQuestion) question).setOptions(new ArrayList<>());
////                    ((QuizQuestion) question).setOptions(new ArrayList<>());
//                case "True or False":
//                    question = new TrueFalseQuestion();
//                    question.setPosition(questions.size());
////                    ((TrueFalseQuestion) question).setCorrectAnswer(true);
//                    break;
//                case "Puzzle":
//                    question = new PuzzleQuestion();
//                    question.setPosition(questions.size());
////                    ((PuzzleQuestion) question).setPuzzleData(new ArrayList<>());
////                    ((PuzzleQuestion) question).setSolution("");
//                    break;
//                case "Type Answer":
//                    question = new TypeQuestion();
//                    question.setPosition(questions.size());
////                    ((TypeQuestion) question).setCorrectAnswer(new ArrayList<>());
//                    break;
//                case "Quiz Audio":
//                    question = new QuizAudioQuestion();
//                    question.setPosition(questions.size());
////                    ((QuizAudioQuestion) question).setOptions(new ArrayList<>());
////                    ((QuizAudioQuestion) question).setCorrectOptionIndex(1);
//                    break;
//                case "Slider":
//                    question = new SliderQuestion();
//                    question.setPosition(questions.size());
////                    ((SliderQuestion) question).setLambda("Default");
////                    ((SliderQuestion) question).setCorrectValue(50);
////                    ((SliderQuestion) question).setMinValue(0);
////                    ((SliderQuestion) question).setMaxValue(100);
//                    break;
//                case "Checkbox":
//                    question = new CheckboxQuestion();
//                    question.setPosition(questions.size());
////                    ((CheckboxQuestion) question).setOptions(new ArrayList<>());
////                    ((CheckboxQuestion) question).setCorrectAnswers(new ArrayList<>());
//                    break;
//                case "Say Word":
//                    question = new SayWordQuestion();
//                    question.setPosition(questions.size());
////                    ((SayWordQuestion) question).setPronunciation("");
////                    ((SayWordQuestion) question).setWord("");
//                    break;
//                default:
//                    throw new IllegalArgumentException("Unknown question type: " + type);
//            }
//            return question;
//
////            if (clazz != null) {
////                try {
////                    QuizQuestion x  = new questionTypeMap.get("Quiz");
////                    return clazz.getDeclaredConstructor().newInstance();
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////            }
////            return null;
//        }
    public static Question createQuestionInstance(List<Question> questions, String type) {
        Question question;

        switch (type) {
            case "Quiz":
                question = new QuizQuestion();
                break;
            case "True or False":
                question = new TrueFalseQuestion();
                break;
            case "Puzzle":
                question = new PuzzleQuestion();
                break;
            case "Type Answer":
                question = new TypeQuestion();
                break;
            case "Quiz Audio":
                question = new QuizAudioQuestion();
                break;
            case "Slider":
                question = new SliderQuestion();
                break;
            case "Checkbox":
                question = new CheckboxQuestion();
                break;
            case "Say Word":
                question = new SayWordQuestion();
                break;
            default:
                throw new IllegalArgumentException("Unknown question type: " + type);
        }

        question.setPosition(questions.size());

        return question;
    }

        public static com.cmloopy.quizzi.models.QuizCreate.after.Question createQuestionInstance2(List<com.cmloopy.quizzi.models.QuizCreate.after.Question> questions, String type) {
            com.cmloopy.quizzi.models.QuizCreate.after.Question question;

            switch (type) {
                case "Quiz":
                    question = new QuestionChoice();
                    break;
                case "True or False":
                    question = new QuestionTrueFalse();
                    break;
                case "Puzzle":
                    question = new QuestionPuzzle();
                    break;
                case "Type Answer":
                    question = new QuestionTypeText();
                    break;
                case "Quiz Audio":
                    question = new QuestionChoice();
                    break;
                case "Slider":
                    question = new QuestionSlider();
                    break;
                case "Checkbox":
                    question = new QuestionChoice();
                    break;
                case "Say Word":
                    question = new QuestionSayWord();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown question type: " + type);
            }


            return question;
        }

        public static void main(String[] args) {
//            Object question = createQuestionInstance("Quiz");
//            if (question != null) {
//                System.out.println("Created instance of: " + question.getClass().getSimpleName());
//            } else {
//                System.out.println("Question type not found.");
//            }
        }
    }

    public static class GridItemDecoration extends RecyclerView.ItemDecoration {
        private final int spanCount;
        private final int spacing;

        public GridItemDecoration(int spanCount, int spacing) {
            this.spanCount = spanCount;
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position < 0) return;

            int column = position % spanCount;
            outRect.left = column == 0 ? 0 : spacing / 2;
            outRect.right = column == spanCount - 1 ? 0 : spacing / 2;
            outRect.top = position < spanCount ? 0 : spacing;
        }
    }

    public static class LinearItemDecoration extends RecyclerView.ItemDecoration {
        private final int spacing;

        public LinearItemDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if (position > 0) {
                outRect.top = spacing;
            }
        }
    }

}

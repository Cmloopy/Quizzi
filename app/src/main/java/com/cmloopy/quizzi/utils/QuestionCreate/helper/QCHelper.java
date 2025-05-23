package com.cmloopy.quizzi.utils.QuestionCreate.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
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
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionCheckboxFragment;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionPuzzleFragment;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionQuizFragment;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionQuizAudioFragment;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionSayWordFragment;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionSliderFragment;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionTrueFalseFragment;
import com.cmloopy.quizzi.fragment.QuestionCreate.QCQuestionTypeTextFragment;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateChoice;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreatePuzzle;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateSayWord;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTrueFalse;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateTypeText;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionType;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreate;
import com.cmloopy.quizzi.models.QuestionCreate.QuestionCreateSlider;
import com.cmloopy.quizzi.utils.QuestionCreate.sheet.QCQuestionTypeBottomSheetFragment;
import com.squareup.picasso.Transformation;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QCHelper {
    public static void showQuestionTypeBottomSheet(
            @NonNull Context context,
            @NonNull FragmentManager fragmentManager,
            @NonNull QCQuestionTypeBottomSheetFragment.OnQuestionTypeSelectedListener onQuestionTypeSelectedListener
    ) {
        QCQuestionTypeBottomSheetFragment bottomSheetFragment = new QCQuestionTypeBottomSheetFragment();
        bottomSheetFragment.setOnQuestionTypeSelectedListener(questionType -> {
            onQuestionTypeSelectedListener.onQuestionTypeSelected(questionType);
        });
        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.getTag());
    }

    public static void navigateToQuestionCreation(
            @NonNull FragmentManager fragmentManager,
            @NonNull QuestionType questionType
    ) {
        Fragment fragment = null;
        switch (questionType.getName()) {
            case "SINGLE_CHOICE":
                fragment = new QCQuestionQuizFragment();
                break;
            case "TRUE_FALSE":
                fragment = new QCQuestionTrueFalseFragment();
                break;
            case "PUZZLE":
                fragment = new QCQuestionPuzzleFragment();
                break;
            case "MULTI_CHOICE":
                fragment = new QCQuestionCheckboxFragment();
                break;
            case "TEXT":
                fragment = new QCQuestionTypeTextFragment();
                break;
            case "SLIDER":
                fragment = new QCQuestionSliderFragment();
                break;
            case "AUDIO_SINGLE_CHOICE":
                fragment = new QCQuestionQuizAudioFragment();
                break;
            case "SAY_WORD":
                fragment = new QCQuestionSayWordFragment();
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


        public static QuestionCreate createQuestionInstance(List<QuestionCreate> questionCreates, String type) {
            QuestionCreate questionCreate;

            switch (type) {
                case "SINGLE_CHOICE":
                    questionCreate = new QuestionCreateChoice();
                    break;
                case "TRUE_FALSE":
                    questionCreate = new QuestionCreateTrueFalse();
                    break;
                case "PUZZLE":
                    questionCreate = new QuestionCreatePuzzle();
                    break;
                case "TEXT":
                    questionCreate = new QuestionCreateTypeText();
                    break;
                case "AUDIO_SINGLE_CHOICE":
                    questionCreate = new QuestionCreateChoice();
                    break;
                case "SLIDER":
                    questionCreate = new QuestionCreateSlider();
                    break;
                case "MULTI_CHOICE":
                    questionCreate = new QuestionCreateChoice();
                    break;
                case "SAY_WORD":
                    questionCreate = new QuestionCreateSayWord();
                    break;
                default:
                    throw new IllegalArgumentException("Unknown question type: " + type);
            }


            return questionCreate;
        }

        public static void main(String[] args) {}
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
    public static class RoundedTransformation implements Transformation {

        private final float radius;
        private final int margin;

        public RoundedTransformation(float radius, int margin) {
            this.radius = radius;
            this.margin = margin;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            RectF rect = new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin);
            paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(rect, radius, radius, paint);

            source.recycle();
            return output;
        }

        @Override
        public String key() {
            return "smooth_rounded(radius=" + radius + ", margin=" + margin + ")";
        }
    }

    public static Date parseIsoDate(String dateStr) {
        try {
            if (dateStr.contains(".")) {
                int dotIndex = dateStr.indexOf(".");
                int endIndex = Math.min(dateStr.length(), dotIndex + 7); // dot + 6
                dateStr = dateStr.substring(0, endIndex);
                LocalDateTime dateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
                return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
            } else {
                LocalDateTime dateTime = LocalDateTime.parse(dateStr);
                return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
            }
        } catch (Exception e) {
            System.err.println("Failed to parse date: " + dateStr);
            return new Date();
        }
    }


}

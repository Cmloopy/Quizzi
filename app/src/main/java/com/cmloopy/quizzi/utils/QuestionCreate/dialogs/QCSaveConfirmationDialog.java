package com.cmloopy.quizzi.utils.QuestionCreate.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.cmloopy.quizzi.R;

public class QCSaveConfirmationDialog extends DialogFragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_MESSAGE = "message";
    private static final String ARG_POSITIVE_BUTTON_TEXT = "positive_button_text";
    private static final String ARG_NEGATIVE_BUTTON_TEXT = "negative_button_text";
    private static final String ARG_NEUTRAL_BUTTON_TEXT = "neutral_button_text";

    private Button positiveButton;
    private Button negativeButton;
    private Button neutralButton;
    private TextView titleTextView;
    private TextView messageTextView;

    private SaveConfirmationListener listener;

    public interface SaveConfirmationListener {
        void onPositiveButtonClicked();
        void onNegativeButtonClicked();
        void onNeutralButtonClicked();
    }

    public static QCSaveConfirmationDialog newInstance(
            String title,
            String message,
            String positiveButtonText,
            String negativeButtonText,
            String neutralButtonText) {

        QCSaveConfirmationDialog dialog = new QCSaveConfirmationDialog();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_MESSAGE, message);
        args.putString(ARG_POSITIVE_BUTTON_TEXT, positiveButtonText);
        args.putString(ARG_NEGATIVE_BUTTON_TEXT, negativeButtonText);
        args.putString(ARG_NEUTRAL_BUTTON_TEXT, neutralButtonText);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Verify that the host activity implements the callback interface
        try {
            if (getParentFragment() != null) {
                listener = (SaveConfirmationListener) getParentFragment();
            } else {
                listener = (SaveConfirmationListener) context;
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Host must implement SaveConfirmationListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ui_qc_dialog_save_information, container, false);

        // Initialize views
        titleTextView = view.findViewById(R.id.dialog_title);
        messageTextView = view.findViewById(R.id.dialog_message);
        positiveButton = view.findViewById(R.id.positive_button);
        negativeButton = view.findViewById(R.id.negative_button);
        neutralButton = view.findViewById(R.id.neutral_button);

        // Set text from arguments
        Bundle args = getArguments();
        if (args != null) {
            titleTextView.setText(args.getString(ARG_TITLE, "Confirm"));
            messageTextView.setText(args.getString(ARG_MESSAGE, "Do you want to save changes?"));

            String positiveText = args.getString(ARG_POSITIVE_BUTTON_TEXT, "Save");
            String negativeText = args.getString(ARG_NEGATIVE_BUTTON_TEXT, "Discard");
            String neutralText = args.getString(ARG_NEUTRAL_BUTTON_TEXT, "Cancel");

            positiveButton.setText(positiveText);
            negativeButton.setText(negativeText);
            neutralButton.setText(neutralText);

            // Show or hide neutral button based on whether text is provided
            if (neutralText == null || neutralText.isEmpty()) {
                neutralButton.setVisibility(View.GONE);
            } else {
                neutralButton.setVisibility(View.VISIBLE);
            }
        }

        // Set button click listeners
        positiveButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPositiveButtonClicked();
            }
            dismiss();
        });

        negativeButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNegativeButtonClicked();
            }
            dismiss();
        });

        neutralButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNeutralButtonClicked();
            }
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Make dialog full width
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);
        // Treat cancel (e.g., back button) as neutral action
        if (listener != null) {
            listener.onNeutralButtonClicked();
        }
    }

    /**
     * Builder class for easy creation of the dialog
     */
    public static class Builder {
        private final String title;
        private final String message;
        private String positiveButtonText = "Save";
        private String negativeButtonText = "Discard";
        private String neutralButtonText = "Cancel";

        public Builder(String title, String message) {
            this.title = title;
            this.message = message;
        }

        public Builder setPositiveButtonText(String text) {
            this.positiveButtonText = text;
            return this;
        }

        public Builder setNegativeButtonText(String text) {
            this.negativeButtonText = text;
            return this;
        }

        public Builder setNeutralButtonText(String text) {
            this.neutralButtonText = text;
            return this;
        }

        public QCSaveConfirmationDialog build() {
            return QCSaveConfirmationDialog.newInstance(
                    title,
                    message,
                    positiveButtonText,
                    negativeButtonText,
                    neutralButtonText
            );
        }
    }
}
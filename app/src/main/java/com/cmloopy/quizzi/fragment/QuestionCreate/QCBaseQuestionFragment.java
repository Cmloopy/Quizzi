package com.cmloopy.quizzi.fragment.QuestionCreate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.cmloopy.quizzi.R;
import com.cmloopy.quizzi.models.QuestionCreate.Question;
import com.cmloopy.quizzi.utils.QuestionCreate.dialogs.QCGenericSelectionDialog;
import com.cmloopy.quizzi.utils.QuestionCreate.helper.QCHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public abstract class QCBaseQuestionFragment extends Fragment {
    protected FrameLayout mediaFrameContainer;
    protected Button timeLimitButton;
    protected Button pointButton;
    protected Button questionTypeButton;
    protected EditText questionEditText;

    // UI components from the merged layout
    protected CardView mediaContainer;
    protected RelativeLayout coverImageContainer;
    protected ImageView coverImageView;
    protected LinearLayout audioContentContainer;
    protected ImageView audioIcon;
    protected TextView audioFileName;
    protected ImageView playAudioButton;
    protected LinearLayout imagePlaceholder;
    protected LinearLayout audioPlaceholder;
    protected ImageButton deleteMediaButton;

    // Flag to determine media type
    protected boolean isAudioMode = false;

    protected int defaultTimeLimit;
    protected int defaultPoint;

    protected OnChangeListener listener;

    // Audio playback
    protected MediaPlayer mediaPlayer;
    private boolean isPlaying = false;



    protected SeekBar audioSeekBar;
    protected TextView currentTimeText;
    protected TextView totalTimeText;
    protected ImageButton rewindButton;
    protected ImageButton fastForwardButton;
    protected Button playbackSpeedButton;

    // Playback control variables
    private boolean isPlaybackInitialized = false;
    private android.os.Handler handler = new android.os.Handler();
    private Runnable updateSeekBar;
    private float currentPlaybackSpeed = 1.0f;
    private final float[] playbackSpeeds = {0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f};
    private int currentSpeedIndex = 2; // Start with 1.0x (index 2)

    public interface OnChangeListener {
        void onUpdateQuestion(int position, Question question);
        void onDeleteQuestion(int position, Question question);
    }

    public void setListener(OnChangeListener listener) {
        this.listener = listener;
    }

    protected static Bundle createBaseBundle(Question question) {
        Bundle args = new Bundle();
        return args;
    }

    protected void setUpBaseView(Question question) {
        timeLimitButton.setText(String.format("%s sec", String.valueOf(question.getTimeLimit())));
        pointButton.setText(String.format("%s pt", String.valueOf(question.getPoint())));
        timeLimitButton.setAllCaps(false);
        pointButton.setAllCaps(false);
        questionEditText.setText(question.getContent());

        setupMediaMode(isAudioMode);

        if (isAudioMode) {
            if (question.getAudio() != null && !question.getAudioUri().isEmpty()) {
                setQuestionMedia(question);
            }
        } else {
            if (question.getImage() != null && !question.getImage().isEmpty()) {
                setQuestionMedia(question);
            }
        }
    }

    protected final List<Integer> TIME_LIMITS = Arrays.asList(5, 10, 20, 30, 45, 60, 90, 120);
    protected final List<Integer> POINTS = Arrays.asList(50, 100, 200, 250, 500, 750, 1000, 2000);

    public void onCreateBaseView(View view) {
        initializeViews(view);
        setupCommonListeners();

        if (getActivity() != null) {
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    private void initializeViews(View view) {
        mediaFrameContainer = view.findViewById(R.id.cover_image_frame);
        timeLimitButton = view.findViewById(R.id.btn_time_limit);
        pointButton = view.findViewById(R.id.btn_points);
        questionTypeButton = view.findViewById(R.id.btn_question_type);
        questionEditText = view.findViewById(R.id.question_edit_text);

        mediaContainer = view.findViewById(R.id.media_container);
        coverImageContainer = view.findViewById(R.id.cover_image_container);
        coverImageView = view.findViewById(R.id.cover_image_view);
        audioContentContainer = view.findViewById(R.id.audio_content_container);
//        audioIcon = view.findViewById(R.id.audio_icon);
        audioFileName = view.findViewById(R.id.audio_file_name);
        playAudioButton = view.findViewById(R.id.play_audio_button);
        imagePlaceholder = view.findViewById(R.id.image_placeholder);
        audioPlaceholder = view.findViewById(R.id.audio_placeholder);
        deleteMediaButton = view.findViewById(R.id.delete_media_button);

        // Initialize new audio player components
        audioSeekBar = view.findViewById(R.id.audio_seek_bar);
        currentTimeText = view.findViewById(R.id.current_time_text);
        totalTimeText = view.findViewById(R.id.total_time_text);
        rewindButton = view.findViewById(R.id.rewind_button);
        fastForwardButton = view.findViewById(R.id.fast_forward_button);
        playbackSpeedButton = view.findViewById(R.id.playback_speed_button);
    }

    protected void setupCommonListeners() {
        setupQuestionEditText();
        mediaFrameContainer.setOnClickListener(v -> openMediaPicker());
        setupTimeLimitButton();
        setupPointsButton();
        setupQuestionTypeButton();
        setupDeleteMediaButton();
        setupAudioControls();
    }

    protected void setupMediaMode(boolean isAudio) {
        this.isAudioMode = isAudio;

        if (isAudio) {
            if (audioPlaceholder != null) audioPlaceholder.setVisibility(View.VISIBLE);
            if (imagePlaceholder != null) imagePlaceholder.setVisibility(View.GONE);

            if (coverImageContainer != null) coverImageContainer.setVisibility(View.GONE);
            if (audioContentContainer != null) audioContentContainer.setVisibility(View.GONE);
        } else {
            if (imagePlaceholder != null) imagePlaceholder.setVisibility(View.VISIBLE);
            if (audioPlaceholder != null) audioPlaceholder.setVisibility(View.GONE);

            if (coverImageContainer != null) coverImageContainer.setVisibility(View.GONE);
            if (audioContentContainer != null) audioContentContainer.setVisibility(View.GONE);
        }

        if (deleteMediaButton != null) deleteMediaButton.setVisibility(View.GONE);
    }

    private void setupDeleteMediaButton() {
        if (deleteMediaButton != null) {
            deleteMediaButton.setOnClickListener(v -> {
                clearMedia();
            });
        } else {
            Log.e("QCBaseQuestionFragment", "deleteMediaButton is null");
        }
    }

    private void clearMedia() {
        mediaUri = null;

        releaseMediaPlayer();

        if (isAudioMode) {
            getCurrentQuestion().setAudioUri(null);

            if (audioPlaceholder != null) audioPlaceholder.setVisibility(View.VISIBLE);
            if (audioContentContainer != null) audioContentContainer.setVisibility(View.GONE);
        } else {
            getCurrentQuestion().setImageUri(null);

            if (imagePlaceholder != null) imagePlaceholder.setVisibility(View.VISIBLE);
            if (coverImageContainer != null) coverImageContainer.setVisibility(View.GONE);
        }

        if (deleteMediaButton != null) {
            deleteMediaButton.setVisibility(View.GONE);
        }

        notifyQuestionUpdated();
    }

    private void setupQuestionEditText() {
        questionEditText.setOnFocusChangeListener((v, hasFocus) -> {
            int backgroundRes = hasFocus
                    ? R.drawable.ui_qc_bg_custom_edit_text_onfocus
                    : R.drawable.ui_qc_bg_custom_edit_text;
            questionEditText.setBackgroundResource(backgroundRes);
            updateQuestionTitle();
        });
    }

    private void setupQuestionTypeButton() {
//        questionTypeButton.setOnClickListener(v -> {
//            QCHelper.showQuestionTypeBottomSheet(
//                    getContext(),
//                    getActivity().getSupportFragmentManager(),
//                    questionType -> {
//
//                        String name = questionType.getName();
//                        List<Question> questions = ((QCQuestionBNVFragment) getParentFragmentManager()
//                                .findFragmentById(R.id.bottom_navigation_frame_container))
//                                .getQuestions();
//
//                        Question newQuestion = QCHelper.QuestionTypeMapper.createQuestionInstance(questions, name);
//                        newQuestion.setQuestion(getCurrentQuestion());
//                        newQuestion.setQuestionType(questionType);
//
//                        if (listener != null) {
//                            listener.onUpdateQuestion(getCurrentQuestion().getPosition(), newQuestion);
//
//                            if (getActivity() instanceof QuestionCreateActivity) {
//                                QuestionCreateActivity activity = (QuestionCreateActivity) getActivity();
//                                activity.onClickListener(newQuestion);
//                            }
//                        }
//
//                        Log.d("QCBaseQuestionFragment", "Question type changed to: " + name + " at position: " + getCurrentQuestion().getPosition());
//                    }
//            );
//        });
    }

    protected void setupTimeLimitButton() {
        timeLimitButton.setText(getCurrentQuestion().getTimeLimit() + " sec");

        timeLimitButton.setOnClickListener(v -> {
            new QCGenericSelectionDialog.Builder(getContext())
                    .setItems(TIME_LIMITS)
                    .setHeaderTitle("Time Limit")
                    .setUnit("sec")
                    .setDefaultSelectedValue(getCurrentQuestion().getTimeLimit())
                    .setOnItemSelectedListener(selectedTime -> {
                        timeLimitButton.setText(selectedTime + " sec");
                        updateQuestionTime();
                    })
                    .build()
                    .show();
        });
    }

    protected void setupPointsButton() {
        pointButton.setText(getCurrentQuestion().getPoint() + " pt");

        pointButton.setOnClickListener(v -> {
            new QCGenericSelectionDialog.Builder(getContext())
                    .setItems(POINTS)
                    .setHeaderTitle("Points")
                    .setUnit("pt")
                    .setDefaultSelectedValue(getCurrentQuestion().getPoint())
                    .setOnItemSelectedListener(selectedPoints -> {
                        pointButton.setText(selectedPoints + " pt");
                        updateQuestionPoint();
                    })
                    .build()
                    .show();
        });
    }

    private String filterDigit(String source) {
        StringBuilder target = new StringBuilder();
        for(int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            if(Character.isDigit(c)) {
                target.append(c);
            }
        }
        return  target.toString();
    }

    protected void updateQuestionTitle() {
        getCurrentQuestion().setContent(questionEditText.getText().toString());
        notifyQuestionUpdated();
    }

    protected void updateQuestionPoint() {
        getCurrentQuestion().setPoint(Integer.parseInt(filterDigit(pointButton.getText().toString())));
        notifyQuestionUpdated();
    }

    protected void updateQuestionTime() {
        getCurrentQuestion().setTimeLimit(Integer.parseInt(filterDigit(timeLimitButton.getText().toString())));
        notifyQuestionUpdated();
    }

    protected void notifyQuestionUpdated() {
        if (listener != null) {
            listener.onUpdateQuestion(getCurrentQuestion().getPosition(), getCurrentQuestion());
        }
    }

    protected void onMediaClicked() {
        releaseMediaPlayer();
    }

    private void setupAudioControls() {
        if (playAudioButton != null) {
            playAudioButton.setOnClickListener(v -> playAudio());
        }

        if (rewindButton != null) {
            rewindButton.setOnClickListener(v -> {
                if (mediaPlayer != null && isPlaybackInitialized) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int newPosition = Math.max(0, currentPosition - 10000);
                    mediaPlayer.seekTo(newPosition);
                    updateSeekBarProgress();
                }
            });
        }

        if (fastForwardButton != null) {
            fastForwardButton.setOnClickListener(v -> {
                if (mediaPlayer != null && isPlaybackInitialized) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    int newPosition = Math.min(duration, currentPosition + 10000); // Forward 10 seconds
                    mediaPlayer.seekTo(newPosition);
                    updateSeekBarProgress();
                }
            });
        }

        if (playbackSpeedButton != null) {
            playbackSpeedButton.setOnClickListener(v -> {
                changePlaybackSpeed();
            });
        }

        if (audioSeekBar != null) {
            audioSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser && mediaPlayer != null && isPlaybackInitialized) {
                        mediaPlayer.seekTo(progress);
                        updateTimeText(progress, mediaPlayer.getDuration());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    handler.removeCallbacks(updateSeekBar);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    if (mediaPlayer != null && isPlaybackInitialized && isPlaying) {
                        handler.postDelayed(updateSeekBar, 100);
                    }
                }
            });
        }
    }

    private void changePlaybackSpeed() {
        if (mediaPlayer != null && isPlaybackInitialized) {
            currentSpeedIndex = (currentSpeedIndex + 1) % playbackSpeeds.length;
            currentPlaybackSpeed = playbackSpeeds[currentSpeedIndex];

            playbackSpeedButton.setText(String.format("%.1fx", currentPlaybackSpeed));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.media.PlaybackParams params = mediaPlayer.getPlaybackParams();
                params.setSpeed(currentPlaybackSpeed);
                try {
                    boolean wasPlaying = isPlaying;
                    mediaPlayer.setPlaybackParams(params);

                    if (!wasPlaying && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                } catch (Exception e) {
                    Log.e("QCBaseQuestionFragment", "Failed to set playback speed: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed to change playback speed", Toast.LENGTH_SHORT).show();
                }
            } else {
                // For older Android versions, just show a toast
                Toast.makeText(getContext(),
                        "Playback speed change requires Android 6.0+",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String formatTime(int milliseconds) {
        int seconds = milliseconds / 1000;
        int minutes = seconds / 60;
        seconds %= 60;
        return String.format(java.util.Locale.getDefault(), "%d:%02d", minutes, seconds);
    }

    private void updateTimeText(int currentMs, int totalMs) {
        if (currentTimeText != null && totalTimeText != null) {
            currentTimeText.setText(formatTime(currentMs));
            totalTimeText.setText(formatTime(totalMs));
        }
    }

    // Update the seek bar progress
    private void updateSeekBarProgress() {
        if (mediaPlayer != null && isPlaybackInitialized) {
            int currentPosition = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();

            audioSeekBar.setProgress(currentPosition);
            updateTimeText(currentPosition, duration);
        }
    }

    protected void playAudio() {
        if (mediaUri == null) {
            Log.e("QCBaseQuestionFragment", "No audio URI available");
            Toast.makeText(getContext(), "No audio file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (isPlaying && mediaPlayer != null) {
                mediaPlayer.pause();
                isPlaying = false;
                playAudioButton.setImageResource(R.drawable.ic_qc_gr_play);
                handler.removeCallbacks(updateSeekBar);
                return;
            }

            if (mediaPlayer != null && isPlaybackInitialized) {
                mediaPlayer.start();
                isPlaying = true;
                playAudioButton.setImageResource(R.drawable.ic_qc_gr_pause);
                handler.postDelayed(updateSeekBar, 100);
                return;
            }

            releaseMediaPlayer();

            mediaPlayer = new MediaPlayer();

            updateSeekBar = new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null && isPlaying) {
                        updateSeekBarProgress();
                        handler.postDelayed(this, 100);
                    }
                }
            };

            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("QCBaseQuestionFragment", "MediaPlayer error: what=" + what + ", extra=" + extra);
                Toast.makeText(getContext(), "Audio playback error", Toast.LENGTH_SHORT).show();
                releaseMediaPlayer();
                return true;
            });

            mediaPlayer.setOnPreparedListener(mp -> {
                int duration = mp.getDuration();
                audioSeekBar.setMax(duration);
                updateTimeText(0, duration);

                mp.start();
                isPlaying = true;
                isPlaybackInitialized = true;
                playAudioButton.setImageResource(R.drawable.ic_qc_gr_pause);

                mp.setVolume(1.0f, 1.0f);

                // Set playback speed if not default
                if (currentPlaybackSpeed != 1.0f && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        android.media.PlaybackParams params = mp.getPlaybackParams();
                        params.setSpeed(currentPlaybackSpeed);
                        mp.setPlaybackParams(params);
                    } catch (Exception e) {
                        Log.e("QCBaseQuestionFragment", "Failed to restore playback speed: " + e.getMessage());
                    }
                }

                // Start updating seekbar
                handler.postDelayed(updateSeekBar, 100);

                // Log successful playback start
                Log.d("QCBaseQuestionFragment", "Audio playback started, duration: " + duration + "ms");
            });

            // Set completion listener
            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                playAudioButton.setImageResource(R.drawable.ic_qc_gr_play);
                audioSeekBar.setProgress(0);
                updateTimeText(0, mp.getDuration());
                handler.removeCallbacks(updateSeekBar);
                Log.d("QCBaseQuestionFragment", "Audio playback completed");
            });

            // Set audio attributes
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());

            Log.d("QCBaseQuestionFragment", "Attempting to play audio from URI: " + mediaUri);

            // Get file permissions if needed
            if (getContext() != null) {
                getContext().getContentResolver().takePersistableUriPermission(
                        mediaUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                );
            }

            // Prepare asynchronously
            mediaPlayer.setDataSource(getContext(), mediaUri);
            mediaPlayer.prepareAsync();

            // Show toast that audio is loading
            Toast.makeText(getContext(), "Loading audio...", Toast.LENGTH_SHORT).show();

        } catch (SecurityException e) {
            Log.e("QCBaseQuestionFragment", "Security exception playing audio: " + e.getMessage());
            Toast.makeText(getContext(), "Permission denied to access audio file", Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        } catch (IOException e) {
            Log.e("QCBaseQuestionFragment", "Error playing audio: " + e.getMessage());
            Toast.makeText(getContext(), "Failed to load audio file", Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        } catch (IllegalStateException e) {
            Log.e("QCBaseQuestionFragment", "MediaPlayer in illegal state: " + e.getMessage());
            Toast.makeText(getContext(), "Error in playback", Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        } catch (Exception e) {
            Log.e("QCBaseQuestionFragment", "Unexpected error in audio playback: " + e.getMessage());
            Toast.makeText(getContext(), "Unexpected error playing audio", Toast.LENGTH_SHORT).show();
            releaseMediaPlayer();
        }
    }

    // Enhanced method to release MediaPlayer resources
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
            } catch (IllegalStateException e) {
                // Ignore if already stopped
            }
            mediaPlayer.release();
            mediaPlayer = null;
            isPlaying = false;
            isPlaybackInitialized = false;

            // Stop seekbar updates
            handler.removeCallbacks(updateSeekBar);

            if (playAudioButton != null) {
                playAudioButton.setImageResource(R.drawable.ic_qc_gr_play);
            }

            // Reset seek bar
            if (audioSeekBar != null) {
                audioSeekBar.setProgress(0);
            }

            // Reset time displays
            if (currentTimeText != null && totalTimeText != null) {
                currentTimeText.setText("0:00");
                totalTimeText.setText("0:00");
            }
        }
    }

    public abstract Question getCurrentQuestion();

    private static final int REQUEST_MEDIA_PICK = 1001;
    protected Uri mediaUri;

    private void openMediaPicker() {
        Intent intent;
        if (isAudioMode) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("audio/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(Intent.createChooser(intent, "Select Audio"), REQUEST_MEDIA_PICK);
        } else {
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_MEDIA_PICK);
        }
        onMediaClicked();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MEDIA_PICK && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedUri = data.getData();
            if (selectedUri != null) {
                // Take persistent permissions for the URI if it's from content provider
                if (isAudioMode && selectedUri.getScheme() != null &&
                        selectedUri.getScheme().equals("content")) {
                    try {
                        getContext().getContentResolver().takePersistableUriPermission(
                                selectedUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                        Log.d("QCBaseQuestionFragment", "Took persistent permission for URI: " + selectedUri);
                    } catch (SecurityException e) {
                        Log.e("QCBaseQuestionFragment", "Failed to take persistent permission: " + e.getMessage());
                    }
                }

                mediaUri = selectedUri;
                Log.d("QCBaseQuestionFragment", "Selected media URI: " + selectedUri);
                displayMedia(selectedUri);
                updateQuestionMedia();
            }
        }
    }

    private void displayMedia(Uri mediaUri) {
        if (isAudioMode) {
            if (audioPlaceholder != null) audioPlaceholder.setVisibility(View.GONE);
            if (imagePlaceholder != null) imagePlaceholder.setVisibility(View.GONE);
            if (audioContentContainer != null) audioContentContainer.setVisibility(View.VISIBLE);
            if (coverImageContainer != null) coverImageContainer.setVisibility(View.GONE);

            if (audioFileName != null) {
                String filename = getMediaFileName(mediaUri);
                audioFileName.setText(filename);
            }
        } else {
            if (imagePlaceholder != null) imagePlaceholder.setVisibility(View.GONE);
            if (audioPlaceholder != null) audioPlaceholder.setVisibility(View.GONE);
            if (coverImageContainer != null) coverImageContainer.setVisibility(View.VISIBLE);
            if (audioContentContainer != null) audioContentContainer.setVisibility(View.GONE);

            if (mediaUri != null) {

                Picasso.get()
                        .load(mediaUri)
                        .resize(1080, 720)
                        .centerCrop()
                        .into(coverImageView);
            }
        }

        // Show delete button
        if (deleteMediaButton != null) {
            deleteMediaButton.setVisibility(View.VISIBLE);
        }
    }

    private String getMediaFileName(Uri uri) {
        String fileName = isAudioMode ? "audio file" : "image file";
        try {
            if (uri.getScheme().equals("file")) {
                File file = new File(uri.getPath());
                fileName = file.getName();
            } else {
                String[] projection = {MediaStore.MediaColumns.DISPLAY_NAME};
                if (getContext() != null && getContext().getContentResolver() != null) {
                    try (android.database.Cursor cursor = getContext().getContentResolver().query(
                            uri, projection, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst()) {
                            fileName = cursor.getString(0);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("QCBaseQuestionFragment", "Error getting media file name: " + e.getMessage());
        }
        return fileName;
    }

    protected void updateQuestionMedia() {
        if (mediaUri != null) {
            if (isAudioMode) {
                Log.d("QCBaseQuestionFragment", "Setting audio URI: " + mediaUri.toString());

                // Check if we can read from this URI
                try {
                    if (getContext() != null) {
                        getContext().getContentResolver().openInputStream(mediaUri).close();
                        Log.d("QCBaseQuestionFragment", "Successfully verified read access to audio URI");
                    }
                } catch (Exception e) {
                    Log.e("QCBaseQuestionFragment", "Cannot read from URI: " + e.getMessage());
                    Toast.makeText(getContext(), "Cannot access audio file", Toast.LENGTH_SHORT).show();
                }

                getCurrentQuestion().setAudioUri(mediaUri.toString());
            } else {
                getCurrentQuestion().setImageUri(mediaUri.toString());
            }
            notifyQuestionUpdated();
        }
    }

    protected void setQuestionMedia(Question question) {
        String mediaPath = isAudioMode ? question.getAudio() : question.getImage();
        if (mediaPath != null && !mediaPath.isEmpty()) {
            try {
                Uri uri = Uri.parse(mediaPath);
                mediaUri = uri;
                displayMedia(uri);
            } catch (Exception e) {
                Log.e("QCBaseQuestionFragment", "Error setting media: " + e.getMessage());
            }
        } else {
            setupMediaMode(isAudioMode);
        }
    }

    protected void setAudioMode(boolean isAudio) {
        this.isAudioMode = isAudio;
        setupMediaMode(isAudio);

        // Release media player when changing modes
        releaseMediaPlayer();
    }

    // Override lifecycle methods to properly handle MediaPlayer
    @Override
    public void onPause() {
        super.onPause();
        releaseMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    protected abstract void onCoverImageClicked();
}
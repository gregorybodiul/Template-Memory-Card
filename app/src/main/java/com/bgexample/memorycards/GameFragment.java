package com.bgexample.memorycards;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Random;

public class GameFragment extends Fragment {
    private static final int MAX_LEVEL = 20;
    private static final int START_COUNT_COLUMNS = 3;
    private static final int START_COUNT_ROWS = 2;
    private static final int POINTS_WIN = 100;
    private static final int FLIP_DURATION = 200;
    private static final int FLIP_LEVEL_DURATION = 800;
    private static final float FLIP_START_ROTATION = 0f;
    private static final float FLIP_END_ROTATION = 180f;
    private static final int CARD_IMAGE_PLACE = R.drawable.place;
    private static final int CARD_PADDING = 6;
    public static final int OPEN_CARDS_TIME_BASE = 2000;
    public static final int OPEN_CARDS_TIME_ADD_OF_LEVEL = 1000;
    public static final int OPEN_CARDS_TIME_OFFSET = 200;
    private static MediaPlayer soundBg;
    private final Cards cards = new Cards();
    private View view;
    private TextView textPoints, textLevel, textLevelName, bgLevelDown, bgLevelUp;
    private FrameLayout frameLayout;
    private ConstraintLayout pointsLayout;
    private ArrayList<ImageView> arrayOpenCard = new ArrayList<>();
    private int marginLeftFirstCard = -1;
    private int marginTopFirstCard = -1;
    private int countColumns = START_COUNT_COLUMNS;
    private int countRows = START_COUNT_ROWS;
    private int points = 0;
    private int level = 1;
    private float volumeBg = 0.5f;
    private MediaPlayer mediaPlayer;
    private TextView dialogPoints;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_game, container, false);
        frameLayout = view.findViewById(R.id.game_layout);
        pointsLayout = view.findViewById(R.id.points_layout);
        textPoints = view.findViewById(R.id.points_text);
        textLevel = view.findViewById(R.id.level_text);
        textLevelName = view.findViewById(R.id.text_level);
        bgLevelDown = view.findViewById(R.id.level_down);
        bgLevelUp = view.findViewById(R.id.level_up);

        updateGameData();
        createCards(cards);
        Animation animationUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        frameLayout.startAnimation(animationUp);
        bgLevelDown.setOnClickListener(v -> {
            if(volumeBg > 0)
                volumeBg-=0.25f;
            changeSoundBgLevel();
            new MyAnimation(bgLevelDown, "rotation", FLIP_START_ROTATION, -360, 200);
        });
        bgLevelUp.setOnClickListener(v -> {
            if(volumeBg < 1)
                volumeBg+=0.25f;
            changeSoundBgLevel();
            new MyAnimation(bgLevelUp, "rotation", FLIP_START_ROTATION, 360, 200);
        });
        updateUI();
        playSoundBg(this.getActivity());
        return view;
    }

    class FlipNewCards implements Runnable{
        @Override
        public void run() {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                playSound(R.raw.snd_ok);
                MyAnimation myAnimation = new MyAnimation(frameLayout, "rotationX", (int)-(FLIP_END_ROTATION / 2), (int)FLIP_START_ROTATION, FLIP_LEVEL_DURATION);
                myAnimation.getObjectAnimator().addListener(new animListenerNextLevelSecond());
            });
        }
    }
    class animListenerNextLevelFirst implements Animator.AnimatorListener{
        @Override
        public void onAnimationStart(Animator animator) {}

        @Override
        public void onAnimationEnd(Animator animator) {
            frameLayout.removeAllViews();
            createCards(cards);
            new Thread(new FlipNewCards()).start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {}

        @Override
        public void onAnimationRepeat(Animator animator) {}
    }
    class animListenerNextLevelSecond implements Animator.AnimatorListener{

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            level++;
            updateUI();
            if(level == MAX_LEVEL)
                level = 0;
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }

    private void createNextLevel() {
        MyAnimation myAnimation = new MyAnimation(frameLayout, "rotationX", FLIP_START_ROTATION, FLIP_END_ROTATION / 2, FLIP_LEVEL_DURATION);
        myAnimation.getObjectAnimator().addListener(new animListenerNextLevelFirst());
    }

    private void createCards(Cards cards) {
        if(marginLeftFirstCard != -1 && marginTopFirstCard != -1){
            if(marginLeftFirstCard > marginTopFirstCard)
                countColumns++;
            else
                countRows++;
        }
        if(level == 0){
            countRows = START_COUNT_ROWS;
            countColumns = START_COUNT_COLUMNS;
        }
        int heightWindow = getActivity().getWindow().getWindowManager().getDefaultDisplay().getHeight();
        int widthWindow = getActivity().getWindow().getWindowManager().getDefaultDisplay().getWidth();
        int heightPointsLayout = pointsLayout.getLayoutParams().height;
        int imagesHeightAndWidth = Math.min(widthWindow / countColumns, (heightWindow - heightPointsLayout)/ countRows);
        marginLeftFirstCard = (widthWindow - (imagesHeightAndWidth * countColumns)) / 2;
        marginTopFirstCard = ((heightWindow - heightPointsLayout) - (imagesHeightAndWidth * countRows)) / 2;
        int indexCard = 0;
        for (int row = 0; row < countRows; row++) {
            for (int column = 0; column < countColumns; column++) {
                indexCard++;
                ImageView imageView = new ImageView(this.getContext());
                imageView.setImageResource(R.drawable.place);
                imageView.setPadding(CARD_PADDING,CARD_PADDING,CARD_PADDING,CARD_PADDING);
                imageView.setTag(getRandomSrc(cards));
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(imagesHeightAndWidth, imagesHeightAndWidth);
                layoutParams.leftMargin = (imagesHeightAndWidth * column) + marginLeftFirstCard;
                layoutParams.topMargin = (imagesHeightAndWidth * row) + marginTopFirstCard;
                imageView.setLayoutParams(layoutParams);
                frameLayout.addView(imageView);
                card_flip(imageView, Integer.parseInt(imageView.getTag().toString()), FLIP_START_ROTATION, FLIP_END_ROTATION, false, false);
                int finalIndexCard = indexCard;
                new Thread(() -> {
                    try {
                        Thread.sleep((OPEN_CARDS_TIME_BASE + OPEN_CARDS_TIME_ADD_OF_LEVEL * level) + (OPEN_CARDS_TIME_OFFSET * finalIndexCard));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new Handler(Looper.getMainLooper()).post(() -> card_flip(imageView, CARD_IMAGE_PLACE, FLIP_END_ROTATION, FLIP_START_ROTATION, false, false));
                }).start();
                imageView.setOnClickListener(v -> {
                    float viewRotationY = imageView.getRotationY();
                    if(viewRotationY == FLIP_START_ROTATION){
                        card_flip(imageView, Integer.parseInt(imageView.getTag().toString()), FLIP_START_ROTATION, FLIP_END_ROTATION, false, true);
                    }
                });
            }
        }
        dialogPoints = new TextView(getContext());
        dialogPoints.setGravity(Gravity.CENTER);
        dialogPoints.setTypeface(ResourcesCompat.getFont(getContext(), R.font.berlin_sans));
        dialogPoints.setTextSize(TypedValue.COMPLEX_UNIT_SP, 100);
        dialogPoints.setElevation(10);
        dialogPoints.setTextColor(Color.GREEN);
        dialogPoints.setAlpha(0f);
        frameLayout.addView(dialogPoints);

        checkFinishLevel("");
    }


    private void card_flip(ImageView image, int visibleImage, float startValue, float endValue, boolean nextLevel, boolean checkWin) {
        playSound(R.raw.snd_move);
        float average = Math.max(startValue, endValue) / 2f;
        MyAnimation myAnimation = new MyAnimation(image, "rotationY", startValue, average, FLIP_DURATION);
        myAnimation.getObjectAnimator().addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                image.setImageDrawable(getActivity().getDrawable(visibleImage));
                MyAnimation myAnimation1 = new MyAnimation(image, "rotationY", average, endValue, FLIP_DURATION);
                myAnimation1.getObjectAnimator().addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if(endValue > startValue && !nextLevel && checkWin){
                            if(arrayOpenCard.size() == 0){
                                arrayOpenCard.add(image);
                            }else{
                                if(arrayOpenCard.get(0).getTag().toString().equals(image.getTag().toString())){
                                    points+=POINTS_WIN * arrayOpenCard.size();
                                    updateUI();
                                    arrayOpenCard.add(image);
                                    System.out.println("+1 point");
                                    dialogPoints.setText("+" + POINTS_WIN * (arrayOpenCard.size() - 1));
                                    ObjectAnimator alpha = ObjectAnimator.ofFloat(dialogPoints, "alpha", 1f, 0f);
                                    alpha.setDuration(1000);
                                    alpha.start();
                                    playSound(R.raw.snd_info);
                                    for (int i = 0; i < frameLayout.getChildCount(); i++) {
                                        View card = frameLayout.getChildAt(i);
                                        if(card.getRotationY() == FLIP_END_ROTATION)
                                            card.setAlpha(0.5f);
                                    }
                                }else{
                                    for (int i = 0; i < frameLayout.getChildCount(); i++) {
                                        View card = frameLayout.getChildAt(i);
                                        if(card.getRotationY() == FLIP_END_ROTATION && card.getAlpha() == 1)
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        Thread.sleep(200);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            card_flip((ImageView) card, CARD_IMAGE_PLACE, FLIP_END_ROTATION, FLIP_START_ROTATION, false, false);
                                                        }
                                                    });
                                                }
                                            }).start();
                                    }
                                    arrayOpenCard.clear();
                                }
                                if(arrayOpenCard.size() > 0)
                                    checkFinishLevel(arrayOpenCard.get(0).getTag().toString());
                                else
                                    checkFinishLevel("");
                            }
                        }
                        if(nextLevel){
                            startNextLevel();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void checkFinishLevel(String tagCurrent) {
        ArrayList<String> tagsCard = new ArrayList<>();
        boolean toGame = false;
        System.out.println("Count child: " + frameLayout.getChildCount());
        for (int i = 0; i < frameLayout.getChildCount(); i++) {
            View card = frameLayout.getChildAt(i);
            if(card.getAlpha() == 1 && card.getTag() != null){
                if(tagsCard.size() > 0){
                    for(String tag : tagsCard){
                        System.out.println("Equals: " + tagCurrent + " / " + tag);
                        boolean isDoubleCard_1 = tagCurrent.equals(card.getTag().toString());
                        boolean isDoubleCard_2 = tagCurrent.equals(tag);
                        boolean isDoubleCard_3 = tag.equals(card.getTag().toString());
                        if(isDoubleCard_1 || isDoubleCard_2 || isDoubleCard_3){
                            toGame = true;
                        }
                    }
                }
                tagsCard.add(card.getTag().toString());
            }
        }
        System.out.println("tagSize: " + tagsCard.size());
        if(tagsCard.size() == 1) toGame = false;
        if(!toGame){
            boolean isCustomChangeLevel = true;
            for (int i = 0; i < frameLayout.getChildCount(); i++) {
                View card = frameLayout.getChildAt(i);
                System.out.println("Go to next level");
                if((card.getRotationY() == FLIP_START_ROTATION) && (card.getAlpha() == 1) && card.getTag() != null){
                    card_flip((ImageView) card, Integer.parseInt(card.getTag().toString()), FLIP_START_ROTATION, FLIP_END_ROTATION, true, false);
                    isCustomChangeLevel = false;
                }
            }
            arrayOpenCard.clear();
            if(isCustomChangeLevel) {
                startNextLevel();
            }

            System.out.println("Go to next level");
        }
    }

    private void startNextLevel() {
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new Handler(Looper.getMainLooper()).post(() -> createNextLevel());
        }).start();
    }

    private int getRandomSrc(Cards cards){
        Random random = new Random();
        int nextInt = random.nextInt(Math.min(level + 1, cards.getSize()));
        return cards.getSrc(nextInt);
    }

    private void updateGameData() {
        points = Database.readPoints(getActivity());
        level = Database.readLevel(getActivity());
        countColumns = Database.readColumns(getActivity());
        countRows = Database.readRows(getActivity());
    }

    private void updateUI(){
        if(points != Integer.parseInt(textPoints.getText().toString())){
            animateTextView(textPoints, points);
        }
        if(level != Integer.parseInt(textLevel.getText().toString())){
            animateTextView(textLevel, level);
            animateTextView(textLevelName, -1);
        }
        Database.writeData(getActivity(), points, level, countColumns, countRows);
    }

    private void animateTextView(TextView textView, int value) {
        ObjectAnimator animation_90 = ObjectAnimator.ofFloat(textView, "rotationX", FLIP_START_ROTATION, FLIP_END_ROTATION / 2);
        animation_90.setDuration(GameFragment.FLIP_DURATION);
        animation_90.start();
        animation_90.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(value != -1)
                    textView.setText(String.valueOf(value));
                ObjectAnimator animation_180 = ObjectAnimator.ofFloat(textView, "rotationX", -(FLIP_END_ROTATION / 2), FLIP_START_ROTATION);
                animation_180.setDuration(GameFragment.FLIP_DURATION);
                animation_180.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void playSoundBg(Context context) {
        new Thread(() -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (soundBg != null) {
                    try {
                        soundBg.release();
                        soundBg = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                soundBg = MediaPlayer.create(context, R.raw.bg_loop);
                soundBg.setLooping(true);
                soundBg.setVolume(.5f, .5f);
                soundBg.start();
            });
        }).start();
    }


    private void changeSoundBgLevel(){
        if(soundBg != null)
            soundBg.setVolume(volumeBg, volumeBg);
    }

    private void playSound(int file) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mediaPlayer = MediaPlayer.create(getContext(), file);
        mediaPlayer.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (soundBg != null) {
            try {
                soundBg.release();
                soundBg = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static int getStartCountColumns() {
        return START_COUNT_COLUMNS;
    }

    public static int getStartCountRows() {
        return START_COUNT_ROWS;
    }
}
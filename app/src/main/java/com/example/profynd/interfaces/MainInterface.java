package com.example.profynd.interfaces;

import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public interface MainInterface {
    void onPictureClick(int position);
    void onNameClick(int position);
    void onShareClick(int position);
    void onLikeClick(int position, LottieAnimationView lottieAnimationView, TextView likesTxt, boolean isAnswer);
}

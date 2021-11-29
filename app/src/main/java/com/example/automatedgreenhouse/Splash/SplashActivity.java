package com.example.automatedgreenhouse.Splash;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.automatedgreenhouse.MainActivity;
import com.example.automatedgreenhouse.R;

public class SplashActivity extends AppCompatActivity {

    //Variables
    Animation topAnimation;
    Animation midAnimation;
    Animation bottomAnimation;
    ImageView image;
    TextView title;
    TextView slogan;
    private static int SPLASH_SCREEN = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        //Animations
        topAnimation = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        midAnimation = AnimationUtils.loadAnimation(this, R.anim.middle_animation);
        bottomAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //Hooks
        image = findViewById(R.id.ivLogo);
        image.setAnimation(topAnimation);

        title = findViewById(R.id.tvTitle);
        title.setAnimation(midAnimation);

        slogan = findViewById(R.id.tvSlogan);
        slogan.setAnimation(bottomAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                Pair pair = new Pair(image, "logo_image");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashActivity.this, pair);
                startActivity(intent, options.toBundle());
            }
        }, SPLASH_SCREEN);

        getSupportActionBar().hide();
    }
}
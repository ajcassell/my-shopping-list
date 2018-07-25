package ait.android.shoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.facebook.shimmer.ShimmerFrameLayout;

public class SplashActivity extends AppCompatActivity{

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    private ImageView iv1;
    private ImageView iv2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        ShimmerFrameLayout shimmer = findViewById(R.id.shimmer);
        shimmer.startShimmerAnimation();

        iv1 = findViewById(R.id.cart1);
        iv2 = findViewById(R.id.cart2);

        final Animation animCart1 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.cart1_anim);
        final Animation animCart2 = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.cart2_anim);

        iv1.startAnimation(animCart1);
        iv2.startAnimation(animCart2);

        startMainActivity();

    }

    public void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

}

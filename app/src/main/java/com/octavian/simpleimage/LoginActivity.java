package com.octavian.simpleimage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import com.dropbox.core.android.Auth;

public class LoginActivity extends AppCompatActivity {

    String ACCESS_TOKEN = null;
    String USER_ID = null;
    static boolean save_feature = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView tv = findViewById(R.id.title);
        Animation tanimation = new AlphaAnimation(0.0f, 1.0f);
        tanimation.setDuration(2000);
        tv.startAnimation(tanimation);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ACCESS_TOKEN = Auth.getOAuth2Token();
        USER_ID = Auth.getUid();

        if (ACCESS_TOKEN != null && !save_feature) {
            save_feature = true;
            Intent intent = new Intent(this, ShowImagesActivity.class);
            intent.putExtra("token", ACCESS_TOKEN);
            intent.putExtra("id", USER_ID);
            startActivity(intent);
        } else {
            USER_ID = null;
        }
    }

    public void loginClick(View view) {
        Auth.startOAuth2Authentication(this, getString(R.string.app_key));
        save_feature = false;
    }
}

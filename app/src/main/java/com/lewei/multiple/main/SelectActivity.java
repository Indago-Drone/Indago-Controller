package com.lewei.multiple.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.media.TransportMediator;
import android.view.View;
import com.lewei.multiple.app.Applications;
import com.lewei.multiple.fydrone.R;

public class SelectActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(TransportMediator.FLAG_KEY_MEDIA_NEXT);
        setContentView(R.layout.activity_select);
    }

    public void onBackPressed(View pressed) {
        onBackPressed();
    }

    public void onPhotoPressed(View pressed) {
        startIntent(PlaybackActivity.class, 1);
    }

    public void onVideoPressed(View pressed) {
        startIntent(PlaybackActivity.class, 2);
    }

    public void onCardVideoPressed(View pressed) {
        if ((Applications.mRunLeweiLibType & 1) > 0) {
            startIntent(CardVideo63Activity.class, 0);
        } else {
            startIntent(CardVideoActivity.class, 0);
        }
    }

    private void startIntent(Class<?> cls, int mediaType) {
        finish();
        Intent intent = new Intent(this, cls);
        if (mediaType > 0) {
            intent.putExtra("MediaType", mediaType);
        }
        startActivity(intent);
    }

    public void onBackPressed() {
        super.onBackPressed();
        startIntent(LW93MainActivity.class, 0);
    }
}

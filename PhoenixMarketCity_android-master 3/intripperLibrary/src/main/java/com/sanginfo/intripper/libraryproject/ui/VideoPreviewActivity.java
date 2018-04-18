package com.sanginfo.intripper.libraryproject.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.sanginfo.intripper.libraryproject.R;

public class VideoPreviewActivity extends Activity {

    VideoView vv;

    Button wayFinderButton;

    TextView wayFinderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_preview);

        wayFinderTextView = (TextView)findViewById(R.id.wayFinderTextView);

        wayFinderTextView.setText("W\nA\nY\nF\nI\nN\nD\nE\nR");

        wayFinderButton = (Button)findViewById(R.id.wayFinderButtonID);

         vv = (VideoView)findViewById(R.id.myVideoView);

        //Video Loop
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                vv.start(); //need to make transition seamless.
            }
        });

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.moses);

        vv.setVideoURI(uri);
        vv.requestFocus();
        vv.start();

        vv.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
             //   mp.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT);
             //   mp.setScreenOnWhilePlaying(false);
                mp.setLooping(true);
            }
        });

        wayFinderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{

                    vv.pause();

                    Intent intent;
                    intent = new Intent( getApplicationContext(), MapActivity.class );
                    startActivity( intent);
                   // overridePendingTransition(R.anim.open_next,R.anim.close_main);
                   // finish();
                }
                catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // ... your own onResume implementation
        //checkForCrashes();

        vv.start();
    }
}

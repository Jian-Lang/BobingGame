package cn.edu.deut.bobinggame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button button,button2;
    private ImageButton imageButton;
    Intent musicService;
    private volatile boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicService = new Intent(getApplicationContext(),MusicService.class);
        startService(musicService);
        button = findViewById(R.id.start_btn);
        button.setOnClickListener(this);
        button2 = findViewById(R.id.rule);
        button2.setOnClickListener(this);
        imageButton = findViewById(R.id.btn_music);
        imageButton.setOnClickListener(this);
//        mediaPlayer = MediaPlayer.create(this,R.raw.moon);
//        mediaPlayer.start();
//        mediaPlayer.setLooping(true);
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.start_btn:
                Bundle music_mes = new Bundle();
                music_mes.putBoolean("music",flag);
                Intent intent = new Intent(this,LoginActivity.class);
                intent.putExtras(music_mes);
                startActivity(intent);
                break;
            case R.id.btn_music:
                if(flag){
                    //mediaPlayer.pause();
                    flag = false;
                    stopService(musicService);
                    imageButton.setImageResource(R.drawable.ic_baseline_music_off_24);
                }
                else{
                    //mediaPlayer.start();
                    flag = true;
                    startService(musicService);
                    imageButton.setImageResource(R.drawable.ic_baseline_music_note_24);
                }
                break;
            case R.id.rule:
                Intent intent1 = new Intent(this,RulesActivity.class);
                startActivity(intent1);
                break;
        }



    }
}
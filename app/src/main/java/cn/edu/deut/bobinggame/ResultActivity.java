package cn.edu.deut.bobinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener{
    TextView textView_player[];
    private volatile boolean flag = true;
    Intent musicService;
    private ImageButton imageButton;
    Button button1,button2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        musicService = new Intent(getApplicationContext(),ResultMusicService.class);
        startService(musicService);
        imageButton = findViewById(R.id.btn_music);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });

        Bundle bundle = getIntent().getExtras();
        int num = bundle.getInt("num_people");
        String keys[] = {"p1","p2","p3","p4","p5","p6"};
        int txt_id[] = {R.id.player1,R.id.player2,R.id.player3,R.id.player4,R.id.player5,R.id.player6};
        textView_player = new TextView[num];
        for(int i = 0;i<num;i++){
            textView_player[i] = findViewById(txt_id[i]);
            textView_player[i].setText("                      "+bundle.getString(keys[i])+":"+bundle.getString(bundle.getString(keys[i])));
        }
        button1 = findViewById(R.id.next_turn);
        button2 = findViewById(R.id.restart_game);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next_turn:
                stopService(musicService);
                //startService(musicService2);
                Bundle bundle = new Bundle();
                bundle.putBoolean("res",true);
                Intent intent = new Intent(ResultActivity.this,GameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.restart_game:
                stopService(musicService);
                Intent intent1 = new Intent(ResultActivity.this,MainActivity.class);
                startActivity(intent1);
                break;
        }
    }
}
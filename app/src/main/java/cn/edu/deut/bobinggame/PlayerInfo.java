package cn.edu.deut.bobinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerInfo extends AppCompatActivity{
    EditText editText[];
    Button button;
    Bundle bundle;
    private volatile boolean flag = true;
    Intent musicService;
    private ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_info);
        Bundle bundle_rec = this.getIntent().getExtras();
        flag = bundle_rec.getBoolean("music");
        imageButton = findViewById(R.id.btn_music);
        if(!flag){
            imageButton.setImageResource(R.drawable.ic_baseline_music_off_24);
        }
        musicService = new Intent(getApplicationContext(),MusicService.class);
        //startService(musicService);

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

        int num = bundle_rec.getInt("num_people");
        //Toast.makeText(PlayerInfo.this,""+num,Toast.LENGTH_SHORT).show();

        editText = new EditText[6];
        editText[0] = findViewById(R.id.text1);
        editText[1] = findViewById(R.id.text2);
        editText[2] = findViewById(R.id.text3);
        editText[3] = findViewById(R.id.text4);
        editText[4] = findViewById(R.id.text5);
        editText[5] = findViewById(R.id.text6);
        if(num < 6){
            for(int i = num;i<6;i++){
                editText[i].setVisibility(View.INVISIBLE);
            }
        }
        button = findViewById(R.id.btn_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle = new Bundle();
                String keys[] = {"p1","p2","p3","p4","p5","p6"};
                for(int i = 0; i < num;i++){
                    bundle.putString(keys[i],editText[i].getText().toString());
                }
                bundle.putInt("num_people",num);
                bundle.putBoolean("music",flag);
                Intent intent = new Intent(PlayerInfo.this,GameActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }


}
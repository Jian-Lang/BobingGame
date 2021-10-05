package cn.edu.deut.bobinggame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{
    private Button button_add,button_min,button_confirm;
    private TextView textView;

    private volatile boolean flag = true;
    Intent musicService;
    private ImageButton imageButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Bundle music_rec = getIntent().getExtras();
        flag = music_rec.getBoolean("music");
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

        button_add = findViewById(R.id.btn_add);
        button_min = findViewById(R.id.btn_min);
        textView = findViewById(R.id.num_txt);
        button_min.setOnClickListener(this);
        button_add.setOnClickListener(this);
        button_confirm = findViewById(R.id.btn_confirm);
        button_confirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int tmp = Integer.parseInt(textView.getText().toString());
        switch(v.getId()){
            case R.id.btn_add:
                if(tmp < 6){
                    tmp++;
                    textView.setText(""+tmp+"");
                }
                else{
                    Toast.makeText(SettingActivity.this,"Max amount of people:6",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_min:
                if(tmp > 1){
                    tmp--;
                    textView.setText(""+tmp+"");
                }
                else{
                    Toast.makeText(SettingActivity.this,"Min amount of people:1",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_confirm:
                Intent intent = new Intent(SettingActivity.this,PlayerInfo.class);
                Bundle bundle = new Bundle();
                bundle.putInt("num_people",tmp);
                bundle.putBoolean("music",flag);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
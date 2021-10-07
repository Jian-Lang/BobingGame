package cn.edu.deut.bobinggame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.SecureRandom;

public class GameActivity extends AppCompatActivity {
    private ImageView imageView1,imageView2,imageView3,imageView4,imageView5,imageView6;
    private Button button1,button2,button3;
    private ImageView[] imageViews;
    private Handler handler;
    private TextView textView;
    private volatile boolean flag_mus = true;
    Intent musicService,tossService;
    private ImageButton imageButton;
    //子线程
    private Thread thread;
    //记录每个骰子的点数
    private int number[];
    //骰子的总点数
    private int count;
    //volatile修饰符用来保证其它线程读取的总是该变量的最新的值
    public volatile boolean isStop = false;
    public volatile boolean flag = true;
    public volatile boolean hasStarted = false;
    public volatile int current_player = 1;
    public volatile int current_score = -1;
    public volatile boolean isRecoverd = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Bundle bundle = this.getIntent().getExtras();
        musicService = new Intent(getApplicationContext(),MusicService.class);
        tossService = new Intent(getApplicationContext(),TossBGMService.class);
        flag_mus = bundle.getBoolean("music");
        //Toast.makeText(this,""+flag_res,Toast.LENGTH_SHORT).show();
        imageButton = findViewById(R.id.btn_music);
        if(!flag_mus){
            imageButton.setImageResource(R.drawable.ic_baseline_music_off_24);
        }
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag_mus){
                    //mediaPlayer.pause();
                    flag_mus = false;
                    stopService(musicService);
                    imageButton.setImageResource(R.drawable.ic_baseline_music_off_24);
                }
                else{
                    //mediaPlayer.start();
                    flag_mus = true;
                    startService(musicService);
                    imageButton.setImageResource(R.drawable.ic_baseline_music_note_24);
                }
            }
        });
        number = new int[6];
        imageView1 = findViewById(R.id.imageView1);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        imageViews = new ImageView[]{imageView1,imageView2,imageView3,imageView4,imageView5,imageView6};

        int num = bundle.getInt("num_people");
        int score[][] = new int[num][6];
        String name[] = new String[num];
        String keys[] = {"p1","p2","p3","p4","p5","p6"};
        for(int i = 0;i<num;i++){
            name[i] = bundle.getString(keys[i]);
        }
        SecureRandom secureRandom = new SecureRandom();
       // Toast.makeText(GameActivity.this,name[0],Toast.LENGTH_SHORT).show();
        textView = findViewById(R.id.txt_player);
        textView.setText(name[0]+"的回合");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断线程是否存在
                if(isRecoverd && current_player <= num){
                    if(flag){
                        startService(tossService);
                        flag = false;
                        hasStarted = true;
                        current_score ++;
                        if (thread != null&&isStop == true){
                            isStop = false;
                        }
                        handler = new Handler(){
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                super.handleMessage(msg);
                                for(int i = 0;i < 6;i++) {
                                    switch (number[i]) {
                                        case 1:
                                            imageViews[i].setImageResource(R.drawable.t1);
                                            break;
                                        case 2:
                                            imageViews[i].setImageResource(R.drawable.t2);
                                            break;
                                        case 3:
                                            imageViews[i].setImageResource(R.drawable.t3);
                                            break;
                                        case 4:
                                            imageViews[i].setImageResource(R.drawable.t4);
                                            break;
                                        case 5:
                                            imageViews[i].setImageResource(R.drawable.t5);
                                            break;
                                        case 6:
                                            imageViews[i].setImageResource(R.drawable.t6);
                                            break;
                                    }
                                }
                            }
                        };
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                while (!isStop) {
                                    Message message = handler.obtainMessage();
                                    //总点数归零
                                    count = 0;
                                    for (int i = 0; i < 6; i++) {
                                        try {
                                            Thread.sleep(5);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        //生成随机数
                                        int random = secureRandom.nextInt(6) + 1;
                                        if(current_score <= num-1)
                                        score[current_score][i] = random;
                                        number[i] = random;
                                        count += random;
                                    }
                                    handler.sendMessage(message);
                                }
                            }
                        });
                        thread.start();
                    }
                    else{
                        Toast.makeText(GameActivity.this,"请勿重复点击！",Toast.LENGTH_SHORT).show();
                    }
                }
                else if(!isRecoverd && current_player <= num){
                    Toast.makeText(GameActivity.this,"请先点击【下一位玩家】！",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(GameActivity.this,"所有玩家已经完成本轮游戏!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        //停止掷筛子
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                isRecoverd = false;
                if(isStop == false) {
                    stopService(tossService);
                    isStop = true;
                    try {
                        //当子线程执行完以后才继续执行主线程
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //显示掷出的点数
                    if(current_player < num){
                        Toast.makeText(GameActivity.this, name[current_player-1]+": " + count+" "+number[0]+" "+number[1]+" "+number[2]+" "+number[3]+" "+number[4]+" "+number[5], Toast.LENGTH_SHORT).show();
                    }
                    else if(current_player == num){
                        Toast.makeText(GameActivity.this, name[current_player-1]+": " + count+" "+number[0]+" "+number[1]+" "+number[2]+" "+number[3]+" "+number[4]+" "+number[5], Toast.LENGTH_SHORT).show();
                        button2.setText("查看结果");
                        current_player++;
                    }
                }else {
                    if(button2.getText().equals("查看结果")){
                        stopService(tossService);
                        Intent intent = new Intent(GameActivity.this,ResultActivity.class);
                        Bundle messenger = new Bundle();
                        String str[] = getResult(score,num);
                        for(int i = 0;i<num;i++) {
                            messenger.putString(keys[i], name[i]);
                            messenger.putString(name[i], str[i]);
                        }
                        if(flag_mus)
                        stopService(musicService);
                        messenger.putInt("num_people",num);
                        intent.putExtras(messenger);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(GameActivity.this,"请按开始按钮！",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //将色子还原到初始状态
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecoverd = true;
                if(current_player <= num){
                    if(isStop == true && hasStarted) {
                        hasStarted = false;
                        for (int i = 0; i < 6; i++) {
                            imageViews[i].setImageResource(R.drawable.t1);
                        }
                        if(current_player < num)
                        textView.setText(name[current_player]+"的回合");
                        current_player++;
                    }else if(isStop != true && hasStarted){
                        Toast.makeText(GameActivity.this, "请先停止游戏，再重新开始！", Toast.LENGTH_SHORT).show();
                    }
                    else if(!hasStarted){
                        Toast.makeText(GameActivity.this, "本轮还未点击开始，请点击开始游戏！", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(GameActivity.this,"所有玩家已经完成本轮游戏!",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
    public String[] getResult(int arr[][],int num){
        int res[][] = new int[num][6];
        for(int i = 0;i<num;i++){
            for(int j = 0;j<6;j++){
                res[i][arr[i][j]-1]++;
            }
        }
        String str[] = new String[num];
        for(int i = 0;i<num;i++){
             if(res[i][3] == 4 && res[i][0] == 2){
                str[i] = "【状元】状元插金花";
             }
             else if(res[i][3] == 6){
                 str[i] = "【状元】六勃红";
             }
             else if(res[i][0] == 6){
                 str[i] = "【状元】遍地锦";
             }
             else if(res[i][1] == 6 || res[i][2] ==6 || res[i][4] == 6 || res[i][5] == 6){
                 str[i] = "【状元】黑六勃";
             }
             else if(res[i][3] == 5){
                 str[i] = "【状元】五红";
             }
             else if(res[i][0] == 5 || res[i][1] == 5 || res[i][2] == 5 || res[i][4] == 5 || res[i][5] == 5){
                 str[i] = "【状元】五子登科";
             }
             else if(res[i][3] == 4 && res[i][0] != 2){
                 str[i] = "【状元】四点红";
             }
             else if(res[i][0] == 1 && res[i][1] == 1 && res[i][2] == 1 && res[i][3] == 1 && res[i][4] == 1 && res[i][5] == 1){
                 str[i] = "【榜眼】对堂";
             }
             else if(res[i][3] == 3){
                 str[i] = "【探花】三红";
             }
             else if(res[i][0] == 4 || res[i][1] == 4 || res[i][2] == 4 || res[i][4] == 4 || res[i][5] == 4){
                 str[i] = "【进士】四进";
             }
             else if(res[i][3] == 2){
                 str[i] = "【举人】二举";
             }
             else if(res[i][3] == 1){
                 str[i] = "【秀才】一秀";
             }
             else{
                 str[i] = "本轮未获奖";
             }
        }
        return str;
    }
}
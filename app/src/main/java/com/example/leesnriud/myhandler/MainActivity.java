package com.example.leesnriud.myhandler;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * android handler
 * <p>
 * UI线程 就是主线程，系统在创建UI线程的时候会初始化一个looper对象，同时也会创建一个与之关联的MassageQueue
 * handler 就是发送和处理消息，Handler想正常工作，当前线程一定要有一个Looper
 * message Handler接收和处理的消息对象
 * messageQueue 消息队列,先进先出管理Message,在初始化Looper对象时会创建一个与之关联的MessageQueue
 * looper 每个线程只能够有一个Looper,管理MessageQueue,不断地从中取出Message分发给对应的Handler处理
 * <p>
 * void handleMessage(Message msg):处理消息的方法,通常是用于被重写!
 * sendEmptyMessage(int what):发送空消息
 * sendEmptyMessageDelayed(int what,long delayMillis):指定延时多少毫秒后发送空信息
 * sendMessage(Message msg):立即发送信息
 * sendMessageDelayed(Message msg):指定延时多少毫秒后发送信息
 * final boolean hasMessage(int what):检查消息队列中是否包含what属性为指定值的消息 如果是参数为(int what,Object object):
 * 除了判断what属性,还需要判断Object属性是否为指定对象的消息
 */
public class MainActivity extends AppCompatActivity {

    int num[] = new int[]{
            1, 2, 3, 4, 5
    };

    int numstart = 0;

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                tvNum.setText(num[numstart++ % 5] + "");
            }else if(msg.what == 0x122){
                tvHandler.setText(nums.toString());
            }
        }
    };

    //handler使用在子线程
    static final String UPPER_NUM = "upper";
    CalThread calThread;
    private List<Integer> nums;

    @BindView(R.id.tv_num)
    TextView tvNum;
    @BindView(R.id.et_handler)
    EditText etHandler;
    @BindView(R.id.tv_handler)
    TextView tvHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //使用定时器每1000ms发送一个空消息
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);
            }
        }, 0, 1000);

        //handler使用在子线程
        calThread = new CalThread();
        // 启动新线程
        calThread.start();
    }

    @OnClick(R.id.bt_handler)
    public void onViewClicked() {
        // 创建消息
        Message msg = new Message();
        msg.what = 0x111;
        Bundle bundle = new Bundle();
        bundle.putInt(UPPER_NUM ,
                Integer.parseInt(etHandler.getText().toString()));
        msg.setData(bundle);
        // 向新线程中的Handler发送消息
        calThread.mHandler.sendMessage(msg);
    }

    // 定义一个线程类
    class CalThread extends Thread {
        public Handler mHandler;

        public void run() {
            Looper.prepare();
            mHandler = new Handler() {
                // 定义处理消息的方法
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 0x111) {
                        int upper = msg.getData().getInt(UPPER_NUM);
                        nums = new ArrayList<Integer>();
                        // 计算从2开始、到upper的所有质数
                        outer:
                        for (int i = 2; i <= upper; i++) {
                            // 用i处于从2开始、到i的平方根的所有数
                            for (int j = 2; j <= Math.sqrt(i); j++) {
                                // 如果可以整除，表明这个数不是质数
                                if (i != 2 && i % j == 0) {
                                    continue outer;
                                }
                            }
                            nums.add(i);
                        }
                        //使用handler更新UI线程
                        handler.sendEmptyMessage(0x122);
                    }
                }
            };
            Looper.loop();
        }
    }

}

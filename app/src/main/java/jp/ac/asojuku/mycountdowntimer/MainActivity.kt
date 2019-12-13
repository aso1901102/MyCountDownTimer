package jp.ac.asojuku.mycountdowntimer

import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    //初期設定時間
    //val defaultTime = this.timerText

    //SoundPool型のインスタンス変数のフィールドプロパティを宣言
    private lateinit var soundPool: SoundPool;
    //効果音の音源(Sound)のリソースID
    //仮の初期値で初期化
    private  var soundResId = 0;

    //内部クラスとしてカウントダウンタイマー継承クラスを定義する
    //inner classで内部クラスを宣言
    inner class  MyCountDownTimer(
        //親クラスにコンストラクタを引き渡す(残り時間：ミリ秒)
        millisInFuture: Long,

        //親クラスのコンストラクタに引き渡す(チック刻み間隔)
        countDownInterval:Long
    ):CountDownTimer(millisInFuture,countDownInterval)/*引き渡す親クラスの指定*/{
        //タイマーが動いているかを判定するフラグ
        var isRunning = false;

        //カウントダウン終了後のイベントで呼ばれるコールバックメソッド
        override fun onFinish() {
            timerText.text = "0:00";

            //音源を鳴らす
            soundPool.play(
                soundResId,            //サウンドリソースID（鳴らす音)を指定する
                1.0f,       //左ボリューム
                1.0f,      //右ボリューム
                0,             //優先度
                0,               //ループする（１）、ループしない（０）
                1.0f              //再生スピード（0.5　～　2.0）
            )

        }

        //チックイベント毎に呼ばれるコールバックメソッド
        override fun onTick(millisUntilFinished: Long) {
            //残り時間表示を書き換える（残り時間：millisUntilFinished:ミリ秒)
            //millisUntilFinishedの分の部分を計算
            val minute = millisUntilFinished/1000L/60L  //1000で割って60で割ると分単位
            val second = millisUntilFinished/1000L%60L  //1000で割って60で割った余りが秒単位

            //残り時間を書き換える
            //文字列テンプレートに値を引き渡す
            timerText.text = "%1d:%2$02d".format(minute,second)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //timerの時間表示を初期設定

        timerText.text = "0:05";

        //カウントダウンタイマーの継承クラスインスタンスを生成する（設定時間カウントダウン、100ミリ秒毎にチック）
        //val timer = MyCountDownTimer(3*60*1000,100);
        val timer = MyCountDownTimer(5*1000,100);
        //FAVがクリックされた時のコールバックメソッド
        this.playStop.setOnClickListener{
            timer.isRunning = when(timer.isRunning){
                true -> {
                    //タイマーをストップ
                    //タイマーインスタンスの動きを止める
                    timer.cancel();

                    //FAVボタンの画像を切り替える
                    playStop.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                    false;
                }
                false -> {
                    //タイマーをスタート
                    //タイマーインスタンスの動きをスタートする
                    timer.start();

                    //FAVボタンの画像を切り替える
                    playStop.setImageResource(R.drawable.ic_stop_black_24dp)
                    true;
                }
            }
        }
    }

    //画面が表示・再表示されるイベントのコールバックメソッド
    override fun onResume() {
        super.onResume()
        //画面が表示されている間だけインスタンスをメモリに保持する
        //SoundPoolクラスのインスタンスを変数に代入
        this.soundPool =
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                //4.4 LOLLIPOPより古い端末(非推奨のメソッドを利用）
                SoundPool(     //SoundPoolクラスのコンストラクタ
                    2,                  //同時にならせる音源の数
                    AudioManager.STREAM_ALARM,  //オーディオの種類
                    0                 //音源の音質。
                );
            }else{
                val audioAttributes = AudioAttributes.Builder()
                                                        .setUsage(AudioAttributes.USAGE_ALARM).build();
                //オーディオ設定を使ってSoundPoolのインスタンスを作る
                SoundPool.Builder().setMaxStreams(1)                //同時音源数
                    .setAudioAttributes(audioAttributes).build();   //オーディオ設定を登録してビルド
            }


        //鳴らすサウンドファイルのリソースIDを設定
        this.soundResId = soundPool.load(
            this,       //アクティビティのインスタンス
            R.raw.bellsound,    //音源のリソースID
            1           //音の優先順位　現在未使用
        )
    }

    //画面が非表示の時のコールバックメソッド
    override fun onPause() {
        super.onPause()

        //SoundPoolインスタンスをメモリから解放
        this.soundPool.release();
    }
}

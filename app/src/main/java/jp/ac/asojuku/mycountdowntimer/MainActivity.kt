package jp.ac.asojuku.mycountdowntimer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.CompletableFuture
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {

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

        //タイマーの表示時間を初期設定
        timerText.text = "3:00";

        //カウントダウンタイマーの継承クラスインスタンスを生成する（設定時間カウントダウン、100ミリ秒毎にチック）
        val timer = MyCountDownTimer(3*60*1000,100);
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
}

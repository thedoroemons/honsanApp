package jp.co.spajam.honsenapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class SasubeActivity extends ActionBarActivity {

    Handler mHandler = new Handler();           //UI Threadへのpost用ハンドラ
    Timer mTimer   = new Timer(true);         //onClickメソッドでインスタンス生成

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sasube);
        VoiceManager voiceManager = VoiceManager.getInstance();
        voiceManager.startRecording();
        PostTimerTask timerTask = new PostTimerTask();
        mTimer.schedule(timerTask,50,1000);


    }

    class PostTimerTask extends TimerTask {

        @Override
        public void run() {
            // mHandlerを通じてUI Threadへ処理をキューイング
            mHandler.post( new Runnable() {
                public void run() {


                    VoiceManager voiceManager = VoiceManager.getInstance();
                    float[] healtz = voiceManager.getAndRemoveHealtzList();
                    float[] volume = voiceManager.getAndRemoveVolumeList();
                    String str = "healtz";
                    for (int n = 0; n < healtz.length; n++){
                        str += healtz[n] + ", ";
                    }
                    str += "\nvolume:";
                    for (int n = 0; n < volume.length; n++){
                        str += volume[n] + ", ";
                    }
                    ((TextView)findViewById(R.id.textView)).setText(str);


                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sasube, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoiceManager voiceManager = VoiceManager.getInstance();
        voiceManager.stopRecoding();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

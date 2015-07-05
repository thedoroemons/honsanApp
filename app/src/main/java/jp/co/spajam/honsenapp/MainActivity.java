package jp.co.spajam.honsenapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @Bind(R.id.yell_button)
    TextView mYellButton;

    @Bind(R.id.edit_nickname)
    EditText mEditNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setTitleColor(getResources().getColor(R.color.main_color));
        ButterKnife.bind(this);

        // 緯度経度を一度だけ取りに行く。SharedPrefに保存する。
        YellLocationManager locationManager = YellLocationManager.getInstance();
        locationManager.startGetCurrentLocation(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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


    @OnClick(R.id.yell_button)
    public void openYellActivity(Button button) {
        Editable nickNameEditable = mEditNickname.getText();
        if (nickNameEditable != null) {
            String nickname = nickNameEditable.toString();
            YellApplication.saveNickname(nickname);
        }
        Intent intent = new Intent(this, YellActivity.class);
        startActivity(intent);
    }
}

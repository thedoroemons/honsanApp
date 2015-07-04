package jp.co.spajam.honsenapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {
//    public static final String PREFERENCES_FILE_NAME = "YELL";
//    public static final String PREF_NICKNAME_KEY = "NICKNAME";

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

//    /**
//     * ニックネームをプリファレンスに保存する
//     * @param nickname
//     */
//    private void saveNickname(String nickname) {
//        SharedPreferences pref = getSharedPreferences(PREFERENCES_FILE_NAME, MODE_PRIVATE);
//        SharedPreferences.Editor editor = pref.edit();
//        editor.putString(PREF_NICKNAME_KEY, nickname);
//        editor.commit();
//    }
}

package jp.co.spajam.honsenapp;

import android.content.Context;
import android.media.Image;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by kenjendo on 2015/07/05.
 */
public class TamaHelper {

	private static final String TAG = TamaHelper.class.getSimpleName();
	private ImageView mTama;
	private static int mTamaSize = 50;
	private ViewGroup mParent; //たまの親のレイアウト
	private List<TextView> mNicknameList = new ArrayList();
	private static final int MAX_NICKNAME_NUM = 100;

	public TamaHelper(ImageView tama,ViewGroup parent) {
		mTama = tama;
		mParent = parent;
        // 大きさの初期化（共有データの分）
        tamaBig(0);
	}

	// たまを大きくする
	public void tamaBig(int vol) {
		ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)mTama.getLayoutParams();
		int currentHeight = mTama.getLayoutParams().height;

        // 引き継ぎ用球サイズ
        setTamaSize(currentHeight + vol);

		int afterWidth = mTamaSize * 2;
		int afterHeight = mTamaSize;
		int afterMarginTop = (int)(afterHeight * -0.5);


		mTama.getLayoutParams().width = afterWidth;
		mTama.getLayoutParams().height = afterHeight;
		mlp.topMargin = afterMarginTop;
		mTama.setLayoutParams(mlp);
		mTama.requestLayout();
	}

	// たまに打ち上げたyellのニックネームを表示
	public void showNameInTama(Context context,String nickname,int type) {
		Pair<Integer,Integer> topLeft = getRandomInTamaTopLeft();

		// nicknameを指定された色に
		int color = YellApplication.getColor(R.color.yell); //default
		if (type==1) {
			color = YellApplication.getColor(R.color.yell);
		} else if (type==2) {
			color = YellApplication.getColor(R.color.yell2);
		} else if (type==3) {
			color = YellApplication.getColor(R.color.yell3);
		} else if (type==4) {
			color = YellApplication.getColor(R.color.yell4);
		} else if (type==5) {
			color = YellApplication.getColor(R.color.yell5);
		}

		// nicknameを表示
		int width = YellApplication.dp2int(30); //　適当
		int height = YellApplication.dp2int(0);//　適当
		final TextView nicknameText = new TextView(context);
		nicknameText.setText(nickname);
		nicknameText.setTextSize(12);//sp
		nicknameText.setTextColor(color);
		RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		int top = topLeft.first - height; //位置調整
		int left = topLeft.second - width;
		rlp.topMargin = top;
		rlp.leftMargin = left;
		mParent.addView(nicknameText, rlp);
		mNicknameList.add(nicknameText);

		if (mNicknameList.size()>MAX_NICKNAME_NUM) {
			int removeNum = mNicknameList.size() - MAX_NICKNAME_NUM;
			for (int i=0;i<removeNum;i++) {
				TextView removeNickname = mNicknameList.get(i);
				mParent.removeView(removeNickname);
			}
		}
	}

	// たまのなかの適当な座標をランダムで返す
	private Pair<Integer,Integer> getRandomInTamaTopLeft() {
		int rLeft;
		int rTop;

		while (true) {
			// 楕円を内包する矩形領域内でランダムな座標(rLeft,rTop)を発行する
			int width = mTama.getLayoutParams().width;
			int height = (int) (mTama.getLayoutParams().height * 0.5);
			int leftStart = mTama.getLeft();
			int leftEnd = leftStart + width;
			int topStart = 0;
			int topEnd = topStart + height;

			Random r = new Random();
			int rWidth = r.nextInt(width + 1); //widht10とすると 0〜10のランダムな数
			int rHeight = r.nextInt(height + 1);
			rLeft = leftStart + rWidth;
			rTop = topStart + rHeight;

			// 発行したランダムな座標が楕円内か判定
			// X^2*B^2 + Y^2*A^2 ≦ A^2*B^2
			boolean isInner;
			int b = (int) (height * 0.5); ///楕円の高さ/2
			int a = (int) (width * 0.5); //楕円の幅/2
			int centerX = leftStart + a; //楕円の中心座標x
			int centerY = topStart + b;
			int rX = rLeft - centerX; // ランダムな座標を原点中心に表現
			int rY = rTop - centerY;
			if (rX * rX * b * b + rY * rY * a * a <= a * a * b * b) { // ランダムな点が楕円の内側か TODO はみ出してる..
				isInner = true;
				break;
			} else {
				isInner = false;
			}
		}

		return Pair.create(rTop,rLeft);
	}

	public void setTamaSize(int tamasize) {
        Log.d("TEST",  mTamaSize + ":" + tamasize);
		if(mTamaSize < tamasize){
			mTamaSize = tamasize;
		}
	}

    public int getTamaSize() {
        return  Math.max(mTamaSize, mTama.getLayoutParams().height);
	}
}

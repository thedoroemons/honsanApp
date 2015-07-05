package jp.co.spajam.honsenapp;

import android.support.v4.util.Pair;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by kenjendo on 2015/07/05.
 */
public class TamaHelper {

	private ImageView mTama;
	private static int mTamaSize = 50;

	public TamaHelper(ImageView tama) {
		mTama = tama;
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
	public void showNameInTama(String nickname,int type) {
		//TODO
	}

	// たまのなかの適当な座標をランダムで返す
	private Pair<Integer,Integer> getRandomInTamaTopLeft() {
		//TODO
		return null;
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

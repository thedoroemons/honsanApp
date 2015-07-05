package jp.co.spajam.honsenapp;

import android.media.Image;
import android.support.v4.util.Pair;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by kenjendo on 2015/07/05.
 */
public class TamaHelper {

	private ImageView mTama;

	public TamaHelper(ImageView tama) {
		mTama = tama;
	}

	// たまを大きくする
	public void tamaBig(int vol) {
		ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)mTama.getLayoutParams();
		int currentWidth = mTama.getLayoutParams().width;
		int currentHeight = mTama.getLayoutParams().height;
		int afterWidth = currentWidth + vol * 2;
		int afterHeight = currentHeight + vol;
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
}

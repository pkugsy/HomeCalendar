package pku.gsy.app.view.wrapper;

import android.view.View;
import android.widget.TextView;

public class ButtonWrapper extends BaseWrapper {

	protected TextView mButton = null;
	
	public ButtonWrapper(View base) {
		super(base);
		this.mButton = (TextView) base;
	}
	
	public TextView getButton() {
		return (this.mButton);
	}
	
	public void setText(CharSequence text) {
		mButton.setText(text);
	}
	
	public CharSequence getText() {
		return mButton.getText();
	}

}

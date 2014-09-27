package pku.gsy.app;

import pku.gsy.app.util.content.Key;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class PasswordActivity extends FullScreenBaseActivity implements 
    OnClickListener {
	
	public static String ActionSet = "pku.gsy.Password.Set";
	public static String ActionEntry = "pku.gsy.Password.Entry";
	
	/*
	 * PasswordActivity的输入：（通过intent）
	 * 1，action：表示是设置口令，输入口令
	 * 2，hash(password)：要比对口令的hash值。
	 * 
	 * PasswordActivity的返回：（通过finish完成）
	 * 1，当action=“设置口令”时，返回设置口令的hash值；
	 * 2，当action=“输入口令”时，返回是否输入正确口令。
	 */
	
	// 0,1,2分别表示原有，输入和确认口令
	private View RowPass0 = null;
	private View RowPass1 = null;
	private View RowPass2 = null;
	private int mPwdHash = 0;
	private TextView[] mTextView = {null, null, null};
	private String[] mPassword = {Key.BLANK, Key.BLANK, Key.BLANK};
	private int mCurTextIndex = -1;
	private String mAction = null;
	private int mCount = 2;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        
        RowPass0 = findViewById(R.id.row_pass0);
        RowPass1 = findViewById(R.id.row_pass1);
        RowPass2 = findViewById(R.id.row_pass2);
        mTextView[0] = (TextView) findViewById(R.id.pass0);
        mTextView[1] = (TextView) findViewById(R.id.pass1);
        mTextView[2] = (TextView) findViewById(R.id.pass2);
        mTextView[0].setOnClickListener(this);
        mTextView[1].setOnClickListener(this);
        mTextView[2].setOnClickListener(this);
        bindContent();
        mCount = 2;
        
        Intent intent = getIntent();
        mAction = intent.getAction();
        mPwdHash = intent.getIntExtra(Key.PasswordHash, 0);
        if (ActionSet.equals(mAction)) {
        	// 显示口令输入框和口令确认框
        	RowPass1.setVisibility(View.VISIBLE);
        	RowPass2.setVisibility(View.VISIBLE);
        	setTextIndex(1);
        	
        	// 判断是否有旧口令
        	if (mPwdHash == 0) {
        		// 显示原有口令输入框
        		RowPass0.setVisibility(View.VISIBLE);
        		setTextIndex(0);
        	}
        	else {
        		// 隐藏原有口令输入框
        		RowPass0.setVisibility(View.GONE);
        	}
        }
        else if (ActionEntry.equals(mAction)) {
        	// 隐藏原有口令输入框
        	RowPass0.setVisibility(View.GONE);
        	// 隐藏口令确认框
        	RowPass2.setVisibility(View.GONE);
        	// 显示口令输入框
        	RowPass1.setVisibility(View.VISIBLE);
        	setTextIndex(1);
        }
        else {
        	// 暂不做处理
        	setTextIndex(0);
        }
	}
	
	@TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (hasFocus && android.os.Build.VERSION.SDK_INT > 18) {
            getWindow().getDecorView().setSystemUiVisibility(
                      View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode==KeyEvent.KEYCODE_BACK){
    		cancel();
    		return true;
    	}
    	
    	return super.onKeyDown(keyCode, event);
	}
	
	private void setTextIndex(int index) {
		if (mCurTextIndex == index) return;
		
		mTextView[0].setBackgroundColor(0xFF606060);
		mTextView[1].setBackgroundColor(0xFF606060);
		mTextView[2].setBackgroundColor(0xFF606060);
		mCurTextIndex = index;
		mTextView[mCurTextIndex].setBackgroundColor(0xFFE6E6F6);
	}
	
	private void addText(int num) {
		// if (mCurTextIndex < 0 || mCurTextIndex > 2) return;
		mPassword[mCurTextIndex] += num;
		final TextView tpTextView = mTextView[mCurTextIndex];
		tpTextView.setText(tpTextView.getText().toString() + '*');
	}
	
	private void deleteText() {
		// if (mCurTextIndex < 0 || mCurTextIndex > 2) return;
		final int len = mPassword[mCurTextIndex].length();
		if (0 == len) return;
		mPassword[mCurTextIndex] = mPassword[mCurTextIndex].substring(0, len - 1);
		final TextView tpTextView = mTextView[mCurTextIndex];
		final CharSequence tpSeq = tpTextView.getText();
		tpTextView.setText(tpSeq.subSequence(0, tpSeq.length() - 1));
	}
	
	private void resetText() {
		// if (mCurTextIndex < 0 || mCurTextIndex > 2) return;
		mPassword[mCurTextIndex] = Key.BLANK;
		mTextView[mCurTextIndex].setText(Key.CBLANK);
	}
	
	private void okText() {
		int status = 0;
		int hash = 0;
		if (ActionSet.equals(mAction)) {
			if (mPwdHash == 0) {
				status = 1;
			}
			else {
				if (mPassword[0].hashCode() == mPwdHash) {
					status = 1;
				}
			}
			if (status == 1) {
				if (mPassword[1].equals(mPassword[2])) {
					hash = mPassword[1].hashCode();
				}
				else {
					Toast.makeText(this, "口令与确认不一致，请重新输入。", Toast.LENGTH_SHORT).show();
					return;
				}
			}
		}
		else if (ActionEntry.equals(mAction)) {
			hash = mPassword[1].hashCode();
			if (hash == mPwdHash) {
				status = 1;
			} else {
				if (mCount > 0) {
					resetText();
					Toast.makeText(this, "还有" + mCount + "次输入机会。", Toast.LENGTH_SHORT).show();
					--mCount;
					return;
				}
			}
		}
		Intent data = new Intent();
		data.putExtra(Key.PasswordResult, status);
		data.putExtra(Key.PasswordHash, hash);
		setResult(Activity.RESULT_OK, data);
		finish();
	}
	
	private void cancel() {
		Intent data = new Intent();
		data.putExtra(Key.PasswordResult, 0);
		setResult(Activity.RESULT_CANCELED, data);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pass0:
			setTextIndex(0);
			break;
		case R.id.pass1:
			setTextIndex(1);
			break;
		case R.id.pass2:
			setTextIndex(2);
			break;
		}
	}
	
	private void bindContent() {
		OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.num0: addText(0); break;
				case R.id.num1: addText(1); break;
				case R.id.num2: addText(2); break;
				case R.id.num3: addText(3); break;
				case R.id.num4: addText(4); break;
				case R.id.num5: addText(5); break;
				case R.id.num6: addText(6); break;
				case R.id.num7: addText(7); break;
				case R.id.num8: addText(8); break;
				case R.id.num9: addText(9); break;
				case R.id.delete: deleteText(); break;
				case R.id.reset: resetText(); break;
				case R.id.ok: okText(); break;
				case R.id.cancel: cancel(); break;
				}
			}
		};
		findViewById(R.id.num0).setOnClickListener(listener);
		findViewById(R.id.num1).setOnClickListener(listener);
		findViewById(R.id.num2).setOnClickListener(listener);
		findViewById(R.id.num3).setOnClickListener(listener);
		findViewById(R.id.num4).setOnClickListener(listener);
		findViewById(R.id.num5).setOnClickListener(listener);
		findViewById(R.id.num6).setOnClickListener(listener);
		findViewById(R.id.num7).setOnClickListener(listener);
		findViewById(R.id.num8).setOnClickListener(listener);
		findViewById(R.id.num9).setOnClickListener(listener);
		findViewById(R.id.delete).setOnClickListener(listener);
		findViewById(R.id.reset).setOnClickListener(listener);
		findViewById(R.id.ok).setOnClickListener(listener);
		findViewById(R.id.cancel).setOnClickListener(listener);
	}
	
}

/**
 * 
 */
package com.criteo.deeplinktester;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * @author alix mougenot
 *
 */
public class MainActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainactivity);

		TextView deeplinkLabel = (TextView) findViewById(R.id.deeplink);
		TextView sourceLabel = (TextView) findViewById(R.id.caller);
		EditText input = (EditText) findViewById(R.id.deeplinkinput);
		final TextView okFeedback = (TextView) findViewById(R.id.thumbup);

		deeplinkLabel.setText("N/A");
		sourceLabel.setText("User Launch");

		Intent current_intent =  getIntent();
		Uri deeplink_uri = current_intent.getData();
		if(deeplink_uri != null){
			String deeplink = deeplink_uri.toString();
			deeplinkLabel.setText(deeplink);
			sourceLabel.setText("An App");
		}

		input.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == EditorInfo.IME_ACTION_SEND || arg1 == EditorInfo.IME_ACTION_UNSPECIFIED) {
					tryDeeplink(arg0.getText().toString());
					return true;
				}

				return true;
			}
		});

		input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void afterTextChanged(Editable s) {
				if(canOpenURL(s.toString())){
					okFeedback.setVisibility(View.VISIBLE);
				}else{
					okFeedback.setVisibility(View.INVISIBLE);
				}
			}
		});


	}

	public boolean canOpenURL(String url){
		boolean answer = false;
		Uri target = Uri.parse(url);
		if(target != null){
			Intent tester = new Intent(Intent.ACTION_VIEW,target);
			PackageManager currentM = getPackageManager();
			List<ResolveInfo> acts = currentM.queryIntentActivities(tester, 0);
			if(acts.size() > 0){
				return true;
			}
		}

		return answer;
	}

	public void tryDeeplink(String url){

		Uri target = Uri.parse(url);
		//check that this is parsable
		if(target == null){
			Toast.makeText(this.getApplicationContext(), "This is not a valid Uri", Toast.LENGTH_SHORT).show();
			return;
		}

		//Check that one activity can open it
		Intent tester = new Intent(Intent.ACTION_VIEW,target);
		PackageManager currentM = getPackageManager();
		List<ResolveInfo> acts = currentM.queryIntentActivities(tester, 0);
		if(acts.size() <= 0){
			Toast toast = Toast.makeText(this.getApplicationContext(), "No activity was found", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			return;
		}

		this.startActivity(tester);

	}


}

package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.model.User;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ForumActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temp);
		((Button) findViewById(R.id.btnLogin)).setText("forum page");
		
		// disable the button for this footer menu item
		findViewById(R.id.footer_forum).setEnabled(false);

	}

	public void onBtnClick(View view) {
		int id = view.getId();
		switch(id){
			case R.id.btnLogin:
				//userMustClickOkay("Forum", "This is the forum page. Still to be implemented.");
				mSettings.loadSettingsFromSharedPreferences(this);
				User user = mSettings.getUser();
				if (user==null) {
					user = new User();
				}
				user.setLogin(false);
				mSettings.setUser(user);
				mSettings.saveSettingsToSharedPreferences(this);
				Intent backToLogin = new Intent(ForumActivity.this,LoginActivity.class);
				startActivity(backToLogin);
				finish();
			break;
			default:
				super.onBtnClick(view);
		}
	}
}

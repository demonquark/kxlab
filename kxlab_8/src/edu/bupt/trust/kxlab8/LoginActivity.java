package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class LoginActivity extends BaseActivity {

	DialogFragmentBasic x; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		((Button) findViewById(R.id.btnLogin)).setText("Go to My services");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void onBtnClick(View view) {
		int id = view.getId();
		switch(id){
			case R.id.btnLogin:
				openActivity(new Intent(this, MyServicesListActivity.class));
			break;
			default:
				super.onBtnClick(view);
		}
	}
	
	public void setupDummy(){
		PreferenceManager.getDefaultSharedPreferences(this)
			.edit().putInt(Gegevens.PREF_LANGUAGE, mSettings.getLanguageSelectionId()).commit();

	}
}

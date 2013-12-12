package edu.bupt.trust.kxlab8;

import android.os.Bundle;
import android.view.View;

public class MyInformationActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temp);

		// disable the button for this footer menu item
		findViewById(R.id.footer_myinformation).setEnabled(false);


	}

	public void onBtnClick(View view) {
		int id = view.getId();
		switch(id){
			case R.id.btnLogin:
				userMustClickOkay("Forum", "This is the forum page. Still to be implemented.");
			break;
			default:
				super.onBtnClick(view);
		}
	}
}

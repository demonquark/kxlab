package edu.bupt.trust.kxlab8;

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
				userMustClickOkay("Forum", "This is the forum page. Still to be implemented.");
			break;
			default:
				super.onBtnClick(view);
		}
	}
}

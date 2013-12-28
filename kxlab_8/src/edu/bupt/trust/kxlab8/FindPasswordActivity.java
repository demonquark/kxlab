package edu.bupt.trust.kxlab8;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class FindPasswordActivity extends Activity {

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //不能比requestWindowFeature更早调用 否则报requestFeature() must be called before adding content
        setContentView(R.layout.findpassword);
        
     
   }
    public void onClick(View v){
  	  	int flag = v.getId();
  	  	switch (flag) {
		case R.id.btn_findpwd_submit:
			Intent back = new Intent(FindPasswordActivity.this,LoginActivity.class);
			startActivity(back);
			finish();
			break;

		default:
			break;
		}
    }
}
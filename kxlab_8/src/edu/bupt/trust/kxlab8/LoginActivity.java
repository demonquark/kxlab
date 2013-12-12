package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class LoginActivity extends BaseActivity {

	
	
	DialogFragmentBasic x;
	/**
	 * �û� �����û���������
	 */
	private User user;
	
	EditText editAccount;
	EditText editPassword;

	CheckBox boxRemember;
	ImageView imageviewFace;
	/**
	 * SharedPreferences �ļ��ӿ�
	 */
	private SharedPreferences userPreferences;
	/**
	 * ���SharedPreferences�ļ�д����
	 */
	private SharedPreferences.Editor userEditor;
	/**
	 * �ж��û��Ƿ��Ѿ���¼
	 */
	private boolean islogin; 
	/**
	 * ����û�������ݵ�һ����
	 */
	//private UserFetcher userFetcher;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		//((Button) findViewById(R.id.login_btn_login)).setText("Go to My services");
		//getActionBar().hide();

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
			case R.id.login_btn_login:
				openActivity(new Intent(this, MyServicesListActivity.class));
			break;
			default:
				super.onBtnClick(view);
		}
	}
}

package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.ProfileDAO;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Loggen;
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
		mSettings.setUser(DaoFactory.getInstance().setProfileDAO(this, null).generateUser());
		Loggen.d(this,"The user email is " + mSettings.getUser().getEmail());
		
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
	

    public boolean isValidEmail(String email){
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(".+@.+\\.[a-z]+");
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}
}

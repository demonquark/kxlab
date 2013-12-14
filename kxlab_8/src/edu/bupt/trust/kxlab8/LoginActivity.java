package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.ProfileDAO;
import edu.bupt.trust.kxlab.data.ProfileDAO.LoginListener;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Tools;
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
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements LoginListener{

	
	
	DialogFragmentBasic x;
	/**
	 * �û� �����û���������
	 */
	private User user;
	
	EditText editAccount;
	EditText editPassword;

	CheckBox boxRemember;
	ImageView imageviewFace;
    boolean isRemmber;
    int userFace;

	/**
	 * ����û�������ݵ�һ����
	 */
	private ProfileDAO pDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		//((Button) findViewById(R.id.login_btn_login)).setText("Go to My services");
		//getActionBar().hide();

		 
	     pDao = DaoFactory.getInstance().setProfileDAO(this,this);
	     //���һ��ֻ�ܱ������д��SharedPreferences����
	     editAccount = (EditText)findViewById(R.id.login_edit_account);
	     editPassword = (EditText)findViewById(R.id.login_edit_pwd);
	     boxRemember = (CheckBox)findViewById(R.id.login_cb_savepwd);
	     imageviewFace = (ImageView)findViewById(R.id.login_img_face);
	     
	     mSettings.loadSettingsFromSharedPreferences(this);
	     user=mSettings.getUser();
	     isRemmber=mSettings.isRemmber();
	     userFace=mSettings.getUserFace();
	     if (user==null) {
	    	 user = new User();
		}
	    
	     validateUserIsLogin();
	     recoveryUserInfo();
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
			login(true);
			break;
		case R.id.btn_login_guest:
			login(false);
			break;
		case R.id.btn_login_findPassword:
			findPassword();
			break;
		case R.id.login_cb_savepwd:
			if(boxRemember.isChecked()){
			Toast.makeText(this, getString(R.string.strRemmenberTips), Toast.LENGTH_SHORT).show();
				isRemmber=true;
			}else {
				isRemmber=false;
			}
			break;
		default :
			super.onBtnClick(view);
			Toast.makeText(this, getString(R.string.no_suchbtn), Toast.LENGTH_SHORT).show();
			break;
		}
	}

	 /**
     * ��sharedpreference �м���Ƿ����û���½�� ��ʾ�û������û�ͼƬ
     */
    private void recoveryUserInfo() {
    	editAccount.setText(user.getEmail());
    	boxRemember.setChecked(isRemmber);
    	imageviewFace.setImageResource(userFace);
    	if (boxRemember.isChecked()) {
    		editPassword.setText(user.getPassword());
		}
	}
	
	/**
     * �û��Ƿ��ѵ�¼
     */
    private void validateUserIsLogin() {
    	
		//����Ѿ���¼��ֱ����ת���û�����
		if(user.isLogin()){
			Intent startmain = new Intent(LoginActivity.this,MyServicesListActivity.class);
			startActivity(startmain);
			finish();
		}
	}
    
    
    /**
	 * ����ʵ���û����ο͵ĵ�¼
	 * @param isUser
	 */
	private void login(boolean isUser) {
		
		if(isUser){
			
			String mUsername = editAccount.getText().toString().trim();
			String mPassword = editPassword.getText().toString().trim();
			if(mUsername == null || mPassword == null 
					|| mUsername.equals("") || mPassword.equals("") ){
				//Toast �û������벻��Ϊ��
				Toast.makeText(this, getString(R.string.strLoginReplyAmpty), Toast.LENGTH_LONG).show();
				return ;
			}
			/*if(!Tools.isEmail(mUsername)){
				//Toast �û������벻��Ϊ��
				Toast.makeText(this, getString(R.string.strLoginReplyNotMail), Toast.LENGTH_LONG).show();
				return ;
			}*/
			user.setEmail(mUsername);
			user.setPassword(mPassword);
			pDao.login(mUsername, mPassword);
			System.out.println("success do login");
			/*if(mUsername == null || mPassword == null 
					|| mUsername.equals("") || mPassword.equals("") ){
				//Toast �û������벻��Ϊ��
				Toast.makeText(this, getString(R.string.strLoginReplyAmpty), Toast.LENGTH_LONG).show();
			}else if(mUsername.equals(user.getFullName())&& mPassword.equals(user.getPassword())){
				//���Գɹ���¼
				islogin = true;
				Intent startmain = new Intent(LoginActivity.this,MainActivity.class);
				startActivity(startmain);
				//�����û�
				saveUser(user);
				//��¼�ɹ��Ϳ������ٵ�loginҳ���� �ο;Ͳ���
				finish();
			}else{//Toast �û����������
				Toast.makeText(this,getString(R.string.strLoginReplyErr),Toast.LENGTH_LONG).show();
			}*/
		}else{//��ʱΪ�ο͵�¼
			Toast.makeText(this,"you are a guest",Toast.LENGTH_LONG).show();
			Intent startmain = new Intent(LoginActivity.this,MyServicesListActivity.class);
			startActivity(startmain);
			
		}
		
		
	}
    
	/**
	 * ���û��һ�����
	 */
	private void findPassword() {
		Intent toFindPassword = new Intent(LoginActivity.this,FindPasswordActivity.class);
		startActivity(toFindPassword);
	}
    
	@Override
	public void onLogin(boolean success, String errorMessage) {
		System.out.println(" do onlogin"+success);
		if (success) {
				//���Գɹ���¼
				user.setLogin(true);
				userFace=R.drawable.tom_head;
				Intent startmain = new Intent(LoginActivity.this,MyServicesListActivity.class);
				startActivity(startmain);
				//��������
				save();
				//��¼�ɹ��Ϳ������ٵ�loginҳ���� �ο;Ͳ���
				finish();
			}else {
				Toast.makeText(this,errorMessage,Toast.LENGTH_LONG).show();
			}
		}
		
		/**
		 * ���û���Ϣ���浽sharedpreference
		 * @param person
		 */
		public void save(){
			mSettings.setUser(user);
			mSettings.setRemmber(isRemmber);
			mSettings.setUserFace(userFace);
			mSettings.saveSettingsToSharedPreferences(this);
		}
		
	}


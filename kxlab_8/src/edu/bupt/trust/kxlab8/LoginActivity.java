package edu.bupt.trust.kxlab8;

import java.io.File;
import java.util.List;

import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.model.JsonActivityRecord;
import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.utils.Tools;
import edu.bupt.trust.kxlab.widgets.DialogFragmentEditText;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements ProfileListener{

	/**
	 * 用户 保存用户名和密码
	 */
	private User mUser;
	CheckBox boxRemember;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// enable back stack navigation
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		// load views
		boxRemember = (CheckBox) findViewById(R.id.login_checkbox_savepassword);
		
		// get the user
		mSettings.loadSettingsFromSharedPreferences(this);
		mUser = mSettings.getUser();
		
		// determine what to do
		if(mUser.isRemember()){
			
			if(mUser.isLogin()){
				// Case 1 - The user wants to be remembered and has not explicitly logged out.
				// Give the user immediate access.
				onLogin(mUser.isLogin(), null);
			} else {
				// Case 2 - The user wants to be remembered, but has explicitly logged out.
				// Show his information. We need not worry about rotation (see Manifest) 
				showUserInfo();
			}
		} else {
			// Case 3 - The user does not want to be remembered.
			mUser.setLogin(false);
		}
	}



	public void onBtnClick(View view) {
		int id = view.getId();
		switch(id){
		case R.id.login_btn_login:
			login(true);
			break;
		case R.id.login_btn_guest:
			login(false);
			break;
		case R.id.login_txt_forgot:
			findPassword();
			break;
		case R.id.login_checkbox_savepassword:
			mUser.setRemember(boxRemember.isChecked());
			mSettings.saveSettingsToSharedPreferences(this);
			if(mUser.isRemember()) { postToast(getString(R.string.strRemmenberTips)); }
			break;
		default :
			super.onBtnClick(view);
			break;
		}
	}

    private void showUserInfo() {
		// get the email box
		EditText editTxtEmail = ((EditText) findViewById(R.id.login_edit_account));
		EditText editTxtPassword = ((EditText) findViewById(R.id.login_edit_password));
		ImageView imgView = (ImageView)findViewById(R.id.login_img_face);
		
		editTxtEmail.setText(String.valueOf(mUser.getEmail()));
		editTxtPassword.setText(String.valueOf(mUser.getPassword()));
		boxRemember.setChecked(mUser.isRemember());
		File imgFile = new File(mUser.getLocalPhoto());
		if(imgFile.exists()){
			imgView.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
		    		imgFile.getAbsolutePath(),
		    		imgView.getLayoutParams().width, 
		    		imgView.getLayoutParams().height));
		}
	}
    /**
	 * 用来实现用户和游客的登录
	 * @param isUser
	 */
	private void login(boolean isUser) {
		
		if(isUser){
			
			// get the user name and password
			String mUsername = ((EditText) findViewById(R.id.login_edit_account)).getText().toString().trim();
			String mPassword = ((EditText) findViewById(R.id.login_edit_password)).getText().toString().trim();
			
			// make sure they are valid entries
			if(mUsername == null || mPassword == null || mUsername.equals("") || mPassword.equals("") ){
				Toast.makeText(this, getString(R.string.strLoginReplyAmpty), Toast.LENGTH_LONG).show();
				return ;
			}
			
			// update the user 
			mUser.setEmail(mUsername);
			mUser.setPassword(mPassword);

			// log in
			DaoFactory.getInstance().setProfileDAO(this,this).login(Source.WEB, mUsername, mPassword);
			
		}else{
			
			// login as guest
			mUser = new User();
			mUser.setLogin(false);
			mSettings.saveSettingsToSharedPreferences(this);
			
			// inform the user
			postToast(getString(R.string.login_post_guest));
			
			// open the Services activity
			Bundle b = new Bundle();
			b.putSerializable(Gegevens.EXTRA_FLAVOR, ServiceFlavor.SERVICE);
			Intent intent = new Intent(this, ServicesListActivity.class);
			intent.putExtra(Gegevens.EXTRA_MSG, b);
			openActivity(intent);
		}
	}
    
	/**
	 * 供用户找回密码
	 */
	private void findPassword() {
		DialogFragmentEditText.newInstance(true, null, R.layout.dialog_email)
				.setTitle(getString(R.string.login_dialog_recover_title))
				.setMessage(getString(R.string.login_dialog_recover_text))
				.setPositiveButtonText(getString(R.string.submit))
				.setNegativeButtonText(getString(R.string.cancel))
				.setCancelableAndReturnSelf(false)
				.show(getSupportFragmentManager(), Gegevens.FRAG_FORGOTPASSWORD);
	}
    
	@Override public void onBasicPositiveButtonClicked(String tag, Object o) { 
		if(o instanceof String){
			String email = (String) o;
			if(Tools.isEmail(email)){
				Loggen.e(this, "I have no idea what to do with this: " + email);
			} else {
				this.postToast("You did not enter a valid email address");
			}
			
		}
	}

	
	@Override
	public void onLogin(boolean success, String errorMessage) {
		if (success) {
			
			// update and save the user
			mUser.setLogin(true);
			mSettings.saveSettingsToSharedPreferences(this);
			
			// launch the my services activity.
			Bundle b = new Bundle();
			b.putSerializable(Gegevens.EXTRA_FLAVOR, ServiceFlavor.MYSERVICE);
			Intent intent = new Intent(this, ServicesListActivity.class);
			intent.putExtra(Gegevens.EXTRA_MSG, b);
			openActivity(intent);

		}else if(errorMessage != null) {
			this.userMustClickOkay(getString(R.string.login_dialog_failure_title), getString(R.string.login_dialog_failure_text));
		}
	}

	@Override public void onReadUserList(List<User> users) {}
	@Override public void onReadUserInformation(User user) { }
	@Override public void onReadActivityHistory(List<JsonActivityRecord> records) { }
	@Override public void onChangeUser(User newUser, String errorMessage) { }
}
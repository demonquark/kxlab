package edu.bupt.trust.kxlab8;

import java.io.File;
import java.util.List;

import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.ProfileDAO;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.model.ActivityHistory;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.DialogFragmentEditText;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;

public class MyInformationEditFragment extends BaseDetailFragment implements ProfileListener {
	
	private User oldUser;
	private User newUser;
	private View mRootView;
	private boolean allowPasswordEditing;
	private Uri mCapturedImageURI;
	
	public MyInformationEditFragment(){
		// Empty constructor required for MyInformationFragment
	}
	
	/** Set the options menu and load defaults for the class variables that require a context */
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        // set up the options menu
        setHasOptionsMenu(true);
        
        // make a default URI for the capture images
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, Gegevens.EXTRA_CAMERAIMG);
		mCapturedImageURI = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);

      if(v.getId() == R.id.myinfo_btn_img && getActivity() != null){
          MenuInflater inflater = getActivity().getMenuInflater();
          inflater.inflate(R.menu.context_myinfo_img, menu);
      }
    }
	
    /**
     *  Handles the context menu requests. 
     *  Currently there is just one context menu namely the context menu for the profile image. <br />
     *  - if from gallery, forward an intent call to the gallery <br />
     *  - if from camera, forward an intent call to the camera <br />
     *  The results are handled in onActivityResult()
     */
	@Override public boolean onContextItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch(id){
		case R.id.context_img_from_gallery:
			Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(i, Gegevens.CODE_GALLERY);
			break;
		case R.id.context_img_from_camera:
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE); 
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
	        startActivityForResult(cameraIntent, Gegevens.CODE_CAMERA); 
			break;
		default:
			return super.onContextItemSelected(item);	
		}
		
		return true;
	}

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Loggen.v(this, getTag() + " - Creating a new ServiceDetailView instance.");
		
		// Assign values to the class variables (This avoids facing null exceptions when creating / saving a Parcelable)
		// convert the saved state to an empty bundle to avoid errors later on
		Bundle savedstate = (savedInstanceState != null) ? savedInstanceState : new Bundle();
		Bundle arguments = (getArguments() != null) ? getArguments() : new Bundle();
		
		// load the user (Note: user remains null if it is neither in the saved state nor the arguments)
		oldUser = savedstate.getParcelable(Gegevens.EXTRA_USER); 							
		if(oldUser == null){ 
			oldUser = arguments.getParcelable(Gegevens.EXTRA_USER);
			if(oldUser == null){ oldUser = new User();}
		} 
		newUser = savedstate.getParcelable(Gegevens.EXTRA_USER2); 							
		if(newUser == null){ 
			newUser = arguments.getParcelable(Gegevens.EXTRA_USER2);
			if(newUser == null){ newUser = new User(oldUser);}
		}
		
		// load whether or not the user has changed the password
		allowPasswordEditing = savedstate.getBoolean(Gegevens.EXTRA_STATE);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the MyInformation view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_myinformation_edit, container, false);
		
		// set the on click listener for the log out button
		((Button) mRootView.findViewById(R.id.myinfo_btn_save)).setOnClickListener(this);
		((ImageView) mRootView.findViewById(R.id.myinfo_btn_img)).setOnClickListener(this);

		// If we already have a user, just show the user information
		enablePasswordEditing(allowPasswordEditing);
		showUserInformation(true);

		return mRootView;
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving MyInformation instance state.");
		// save the list of comments to the instance state
		outState.putParcelable(Gegevens.EXTRA_USER, oldUser);
		outState.putParcelable(Gegevens.EXTRA_USER2, newUser);
		outState.putBoolean(Gegevens.EXTRA_STATE, allowPasswordEditing);
	}
	
	/** Handles results from other activities.
	 *  There are only two results worth considering: <br />
	 *  - CODE_CAMERA: The user has taken a picture with a camera. 
	 *  - CODE_GALERY: The user has selected a picture from the gallery. 
	 */
	@Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == BaseActivity.RESULT_OK){
            if (requestCode == Gegevens.CODE_GALLERY && null != data) {
            	setProfileImageFromUri(data.getData());
            } else if (requestCode == Gegevens.CODE_CAMERA){
            	setProfileImageFromUri(mCapturedImageURI);
            }
    	} else if (getActivity() != null){
    		((BaseActivity) getActivity() ).postToast("Failed");
    	}
	}

	/** Loads the newUser object's variables to the screen. */
	private void showUserInformation(boolean showinfo) {
		if(mRootView != null){
			
			// show or hide the information
			((ScrollView) mRootView.findViewById(R.id.myinfo_container))
				.setVisibility((showinfo) ? View.VISIBLE : View.GONE);
			((ProgressBar) mRootView.findViewById(R.id.myinfo_progress_bar))
				.setVisibility((showinfo) ? View.GONE : View.VISIBLE);

			if(newUser != null){
				// set the image
				setProfileImage(newUser.getPhotoLocation());
				
				// set the text
				((TextView) mRootView.findViewById(R.id.myinfo_email)).setText(newUser.getEmail());
				((EditText) mRootView.findViewById(R.id.myinfo_edit_name)).setText(newUser.getUserName());
				((EditText) mRootView.findViewById(R.id.myinfo_edit_phone)).setText(newUser.getPhoneNumber());
				((EditText) mRootView.findViewById(R.id.myinfo_edit_source)).setText(newUser.getSource());
				((EditText) mRootView.findViewById(R.id.myinfo_edit_password)).setText(newUser.getPassword());
				((EditText) mRootView.findViewById(R.id.myinfo_edit_confirm_password)).setText(newUser.getPassword());
				
				// set the profile text watchers
				((EditText) mRootView.findViewById(R.id.myinfo_edit_name))
					.addTextChangedListener(new UserProfileWatcher(R.id.myinfo_edit_name));
				((EditText) mRootView.findViewById(R.id.myinfo_edit_phone))
					.addTextChangedListener(new UserProfileWatcher(R.id.myinfo_edit_phone));
				((EditText) mRootView.findViewById(R.id.myinfo_edit_source))
					.addTextChangedListener(new UserProfileWatcher(R.id.myinfo_edit_source));
				((EditText) mRootView.findViewById(R.id.myinfo_edit_password))
					.addTextChangedListener(new UserProfileWatcher(R.id.myinfo_edit_password));
			}
		}
	}
	
	/** Shows a dialog that asks the user to verify his identity by entering his current password.
	 *  The reply to this question is handled in the onBasicPositiveButtonClicked()
	 */
	private void verifyPassword() {
		DialogFragmentEditText.newInstance(true, this, R.layout.dialog_password)
			.setTitle(getString(R.string.myinfo_edit_confirm_password_title))
			.setMessage(getString(R.string.myinfo_edit_confirm_password_text))
			.setPositiveButtonText(getString(R.string.submit))
			.setNegativeButtonText(getString(R.string.cancel))
			.setCancelableAndReturnSelf(false)
			.show(getFragmentManager(), Gegevens.FRAG_PASSWORD);
	}
	
	/** Shows a dialog that asks the user if he wants to save the changes he has made to the user.
	 *  If the user has made no changes, the method does nothing and returns true.
	 *  @return true if new user equals old users, false otherwise.
	 */
	private boolean verifySaveUser() {
		// Check if anything has changed
		boolean allow = (oldUser != null && oldUser.equals(newUser));

		// If the user has changed anything, ask for confirmation
		if(!allow){ 
			userMustConfirm(getString(R.string.myinfo_edit_confirm_title),
							getString(R.string.myinfo_edit_confirm_text));
		}
		
		return allow;
	}
	
	/** Saves the user to the web server. The request is only sent if: <br />
	 *  - The user has changed anything in the profile <br />
	 *  - The entered password matches the confirmation text (Note: the user is required to enter his password twice) <br /> 
	 *  The reply to this question is handled in the various callback methods of the profile DAO
	 */
	private void saveUser(){
		
		// get the saved password was repeated correctly
		String password2 = oldUser.getPassword();
		if(mRootView != null){
			((EditText) mRootView.findViewById(R.id.myinfo_edit_confirm_password)).getText().toString();
		}
		
		// make sure the password was repeated correctly
		if( "".equals(newUser.getPassword()) || !password2.equals(newUser.getPassword()) ) {
			// give an alert if the values do not match
			userMustClickOkay(	getString(R.string.myinfo_passwords_dont_match_title), 
								getString(R.string.myinfo_passwords_dont_match_text)); 
		} else if (getActivity() != null) { 
			ProfileDAO profileDAO = DaoFactory.getInstance().setProfileDAO(getActivity(),this);
			profileDAO.updateUser(oldUser, newUser);
			showUserInformation(false);
		} else {
			// Inform the user that we could not save the information.
			userMustClickOkay(getString(R.string.myinfo_update_fail_title), getString(R.string.myinfo_update_fail_text));
		}
	}
	
	/** Makes the password field editable and makes the confirm password row visible */
	private void enablePasswordEditing(boolean enableEditing){
		allowPasswordEditing = enableEditing;

		if(mRootView != null){
			EditText edittext = (EditText) mRootView.findViewById(R.id.myinfo_edit_password);
			edittext.setFocusable(allowPasswordEditing); 
			edittext.setFocusableInTouchMode(allowPasswordEditing);
			edittext.setOnClickListener((allowPasswordEditing) ? null : this);
			if(allowPasswordEditing) { 
				((TableRow) mRootView.findViewById(R.id.myinfo_row_confirm_password)).setVisibility(View.VISIBLE);
				edittext.setText("");
				edittext.requestFocus();
				if(getActivity() != null){
					getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
				}
			} else {
				((TableRow) mRootView.findViewById(R.id.myinfo_row_confirm_password)).setVisibility(View.GONE);
			}
		}
	}
	
	/** Loads the profile image from the provided URI. */
	private void setProfileImageFromUri(Uri imageUri){
		Cursor cursor = null;
		String picturePath = imageUri.getPath();
        try{
            
        	// Get the image path
			Loggen.v(this, getTag() + " - Called captured image with: " + mCapturedImageURI.toString());
        	String[] filePathColumn = { MediaStore.Images.Media.DATA };
            cursor = getActivity().getContentResolver().query(imageUri,filePathColumn, null, null, null);
            cursor.moveToFirst();
            picturePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
            cursor.close();
            
        } catch (Exception e) {
        	 Loggen.e(this, "Error occurred during getting image." + e.toString());
        } finally {
        	if(cursor != null) { cursor.close(); }
        }

        // set the image path and update the user profile picture
        setProfileImage(picturePath);
	}
	
	/** Loads the profile image from the provided path. */
	private void setProfileImage(String imageLocation){
		Loggen.v(this, getTag() + " - Getting image at: " + imageLocation );
		// Set the image
		File imgFile = new File(imageLocation);
		ImageView avatar = (ImageView) mRootView.findViewById(R.id.myinfo_img);
		if(imgFile.exists()){
			// update the image and the new user variable
			avatar.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
		    		imgFile.getAbsolutePath(),
		    		avatar.getLayoutParams().width, 
		    		avatar.getLayoutParams().height));
	        newUser.setPhotoLocation(imageLocation);
		} else {
			// Inform the user that no image was found
			userMustClickOkay(getString(R.string.myinfo_img_not_found_title), getString(R.string.myinfo_img_not_found_text));
		}
	}

	@Override public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.myinfo_btn_save:
				saveUser();
			break;
			case R.id.myinfo_edit_password:
				verifyPassword();
			break;
			case R.id.myinfo_btn_img:
				registerForContextMenu(v); 
			    v.showContextMenu();
			    unregisterForContextMenu(v);
			break;
		}
	}
	
	@Override public boolean onNavigateUp(){ return verifySaveUser(); }
	
	@Override public boolean allowBackPressed(){ return verifySaveUser(); }

	/**	Default callback from all dialog fragments.  
	 * There are two possible dialogs worth considering: <br />
	 *  - Fragment Confirm: Only one possibility, namely the user wants to save his changes
	 *  - Fragment Password: Only one possibility, namely the user wants to allow password editing 
	 */
	@Override public void onBasicPositiveButtonClicked(String tag, Object o) {
		if(Gegevens.FRAG_PASSWORD.equals(tag)){
			if(o instanceof String && (allowPasswordEditing = ((String)o).equals(oldUser.getPassword()))){
				// check if the entered password matches the current password
				enablePasswordEditing(allowPasswordEditing);
			} else { 
				// give an alert if the values do not match
				userMustClickOkay( 	getString(R.string.myinfo_invalid_password_title), 
									getString(R.string.myinfo_invalid_password_text)); 
			}
		}else if (Gegevens.FRAG_CONFIRM.equals(tag)){
			saveUser();
		}
	}
	
	/**	Default callback from all dialog fragments. 
	 *  There is only one possible dialog worth considering: <br />
	 *  - Fragment Confirm: Only one possibility, namely the user wants to go back
	 */
	@Override public void onBasicNegativeButtonClicked(String tag, Object o) { 
		if(Gegevens.FRAG_CONFIRM.equals(tag) && mListener != null){
			mListener.performBackPress();
		}
	}
	


	@Override public void onChangeUser(User updatedUser, String errorMessage) {
		
		// hide the progress bar (i.e. show the user information)
		showUserInformation(true);
		
		if(updatedUser == null || !updatedUser.equals(newUser)){
			// the update failed. Inform the user.
			
			// Build an error text with the error message from the server
			String errorTxt = (errorMessage != null && !errorMessage.equals("")) ? errorMessage + "\n" : "";
			
			// Add the fields that were not updated
			errorTxt += getString(R.string.myinfo_update_failures_text) + ":\n";
			if(!newUser.getUserName().equals(updatedUser.getUserName())){
				errorTxt +=  "- " + getString(R.string.myinfo_name_title) + "\n";
			}
			if(!newUser.getPhoneNumber().equals(updatedUser.getPhoneNumber())){
				errorTxt +=  "- " + getString(R.string.myinfo_phone_title) + "\n";
			}
			if(!newUser.getSource().equals(updatedUser.getSource())){
				errorTxt +=  "- " + getString(R.string.myinfo_source_title) + "\n";
			}
			if(!newUser.getPassword().equals(updatedUser.getPassword())){
				errorTxt +=  "- " + getString(R.string.myinfo_password_title) + "\n";
			}
			
			// Add the generic failure text
			errorTxt += "\n" + getString(R.string.myinfo_update_fail_text);
			
			// inform the user of the failure
			userMustClickOkay(getString(R.string.myinfo_update_fail_title), errorTxt);
		} else {
			oldUser.setFromUser(newUser);
			mListener.performBackPress();
		}
		
	}

	
	@Override public void onReadUserInformation(User user) { }
	@Override public void onReadActivityHistory(ActivityHistory history) { }
	@Override public void onChangePhoto(boolean success, String errorMessage) { }
	@Override public void onChangePassword(boolean success, String errorMessage) { }
	@Override public void onChangePhonenumber(boolean success, String errorMessage) { }
	@Override public void onChangeSource(boolean success, String errorMessage) { }

	@Override public void onReadUserList(List<User> users) { }
	@Override public void onLocalFallback() { }
	
	
	/** This class handles all the EditText fields. 
	 *  It just updates the corresponding variable in the new user. 
	 * @author Krishna
	 *
	 */
	private class UserProfileWatcher implements TextWatcher{

	    private int viewId;
	    private UserProfileWatcher(int id) { this.viewId = id; }
	    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

	    public void afterTextChanged(Editable editable) {
	        switch(viewId){
            case R.id.myinfo_edit_name:
                newUser.setUserName(editable.toString());
                break;
            case R.id.myinfo_edit_phone:
                newUser.setPhoneNumber(editable.toString());
                break;
            case R.id.myinfo_edit_source:
                newUser.setSource(editable.toString());
                break;
            case R.id.myinfo_edit_password:
    			newUser.setPassword(editable.toString());
                break;
	        }
	    }
	}

}
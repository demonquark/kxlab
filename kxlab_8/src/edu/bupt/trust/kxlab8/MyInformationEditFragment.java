package edu.bupt.trust.kxlab8;

import java.io.File;

import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MyInformationEditFragment extends BaseDetailFragment {
	
	private User oldUser;
	private User newUser;
	private boolean allowPasswordEditing;
	private View mRootView;
	
	public MyInformationEditFragment(){
		// Empty constructor required for MyInformationFragment
	}
	
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);

      if(v.getId() == R.id.myinfo_btn_img && getActivity() != null){
          MenuInflater inflater = getActivity().getMenuInflater();
          inflater.inflate(R.menu.context_myinfo_img, menu);
      }
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
			if(newUser == null){ newUser = oldUser;}
		}
		
		// load wether or not the user has changed the password
		allowPasswordEditing = savedstate.getBoolean(Gegevens.EXTRA_STATE);
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the MyInformation view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_myinformation_edit, container, false);
		
		// set the on click listener for the log out button
		((Button) mRootView.findViewById(R.id.myinfo_btn_save)).setOnClickListener(this);
		((ImageView) mRootView.findViewById(R.id.myinfo_btn_img)).setOnClickListener(this);
		EditText edittext = (EditText) mRootView.findViewById(R.id.myinfo_edit_password);
		if(!allowPasswordEditing){
			edittext.setFocusable(false); edittext.setFocusableInTouchMode(false);
			edittext.setOnClickListener(this);
		}

		// If we already have a user, just show the user information
		showUserInformation();

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
	
	private void showUserInformation() {
		if(mRootView != null && newUser != null){
			// Set the image
			File imgFile = new File(newUser.getPhotoLocation());
			ImageView avatar = (ImageView) mRootView.findViewById(R.id.myinfo_img);
			if(imgFile.exists()){
				avatar.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
			    		imgFile.getAbsolutePath(),
			    		avatar.getLayoutParams().width, 
			    		avatar.getLayoutParams().height));
			}
			
			// set the text
			((TextView) mRootView.findViewById(R.id.myinfo_email)).setText(newUser.getEmail());
			((EditText) mRootView.findViewById(R.id.myinfo_edit_name)).setText(newUser.getUserName());
			((EditText) mRootView.findViewById(R.id.myinfo_edit_phone)).setText(newUser.getPhoneNumber());
			((EditText) mRootView.findViewById(R.id.myinfo_edit_source)).setText(newUser.getSource());
			((EditText) mRootView.findViewById(R.id.myinfo_edit_password)).setText(newUser.getPassword());
		}
	}

	private void verifyPassword() {
		DialogFragmentBasic.newInstance(true)
			.setTitle(getString(R.string.myinfo_edit_confirm_password_title))
			.setMessage(getString(R.string.myinfo_edit_confirm_password_text))
			.setPositiveButtonText(getString(R.string.submit))
			.setNegativeButtonText(getString(R.string.cancel))
			.setCancelableAndReturnSelf(false)
			.show(getFragmentManager(), Gegevens.FRAG_PASSWORD);
	}
	
	private void enablePasswordEditing(){
		
	}

	@Override public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.myinfo_btn_save:
				
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
	
	@Override public void onBasicPositiveButtonClicked(String tag, Object o) {
		if(Gegevens.FRAG_PASSWORD.equals(tag)){
			
		}else if (Gegevens.FRAG_CONFIRM.equals(tag)){
			mListener.performBackPress();	
		}
	}
	
	@Override public void onBasicNegativeButtonClicked(String tag, Object o) { }
	
	@Override public boolean onNavigateUp(){ return allowBackPressed(); }
	
	@Override public boolean allowBackPressed(){
		
		// Check if anything has changed
		boolean allow = (oldUser != null && oldUser.equals(newUser));
		
		// If the user has changed anything, ask for confirmation
		if(!allow){
			DialogFragmentBasic.newInstance(true)
				.setTitle(getString(R.string.myinfo_edit_confirm_title))
				.setMessage(getString(R.string.myinfo_edit_confirm_text))
				.setPositiveButtonText(getString(R.string.yes))
				.setNegativeButtonText(getString(R.string.no))
				.show(getFragmentManager(), Gegevens.FRAG_CONFIRM);
		}
		
		return allow;
	}
}
package edu.bupt.trust.kxlab8;

import java.util.List;

import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.ServicesDAO;
import edu.bupt.trust.kxlab.data.ServicesDAO.ServicesListener;
import edu.bupt.trust.kxlab.model.JsonComment;
import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.ServiceType;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

/**
 *  This fragment lets you edit a service.
 *  The fragment expects the following items in its arguments: <br />
 *  - EXTRA_SERVICE: A service object. Cannot be null. <br />
 *  - EXTRA_SERVICETYPE: The type identifies the service type (COMMUNITY, RECOMMEND, APPLY). Defaults to COMMUNITY.
 *  Service type is really only useful when creating a new service. (Note: a service object knows its own type) <br />
 *  The fragment supplements those arguments with the following items (added to saved state): <br />
 *  - EXTRA_SERVICE2: A temporary holder for changes made to the EXTRA_SERVICE object. <br />
 */
public class ServiceDetailEditFragment extends BaseDetailFragment implements ServicesListener {
	
	private TrustService oldService;
	private TrustService newService;
	private User mUser;
	private View mRootView;
	private Uri mCapturedImageURI;
	
	public ServiceDetailEditFragment(){
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
		
		// create a copy of the user
		if(mUser == null){ mUser = ((BaseActivity) getActivity()).mSettings.getUser(); }

    }

    @Override public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
      super.onCreateContextMenu(menu, v, menuInfo);

      if(v.getId() == R.id.details_btn_img && getActivity() != null){
          MenuInflater inflater = getActivity().getMenuInflater();
          inflater.inflate(R.menu.context_details_img, menu);
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
		
		// load the service as it is now (Note: oldService remains null if it is neither in the saved state nor the arguments)
		oldService = savedstate.getParcelable(Gegevens.EXTRA_SERVICE); 							
		if(oldService == null){ oldService = arguments.getParcelable(Gegevens.EXTRA_SERVICE); }
		
		// load the edited service (Note: newService should not be null)
		newService = savedstate.getParcelable(Gegevens.EXTRA_SERVICE2);		
		if(newService == null){ newService = arguments.getParcelable(Gegevens.EXTRA_SERVICE2); }
		if(newService == null){
			if(oldService != null){
				newService = new TrustService(oldService); 
			} else {
				
				// create a new trust service object 
				newService = new TrustService();

				// get the user and type
				ServiceType type = (ServiceType) arguments.getSerializable(Gegevens.EXTRA_SERVICETYPE);
				if(mUser == null && getActivity() != null){ 
					mUser = ((BaseActivity) getActivity()).mSettings.getUser(); 
				}

				if(type != null){ newService.setType(type); }
				if(mUser != null){ newService.setUseremail(mUser.getEmail()); }
			}
		}
		
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the DetailEdit view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_service_detail_edit, container, false);
		
		// set the on click listener for the log out button
		((Button) mRootView.findViewById(R.id.details_btn_save)).setOnClickListener(this);
		((ImageView) mRootView.findViewById(R.id.details_btn_img)).setOnClickListener(this);

		if(oldService == null){
			((Button) mRootView.findViewById(R.id.details_btn_save)).setText(getString(R.string.details_edit_new_title));
			((TextView) mRootView.findViewById(R.id.details_title)).setText(getString(R.string.details_edit_new_title));
		}
		// Just show the service information
		showUserInformation(true);

		return mRootView;
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving DetailEdit instance state.");
		// save the service to the instance state
		if(oldService != null) { outState.putParcelable(Gegevens.EXTRA_SERVICE, oldService); }
		outState.putParcelable(Gegevens.EXTRA_SERVICE2, newService);
	}
	
	/** Handles results from other activities.
	 *  There are only two results worth considering: <br />
	 *  - CODE_CAMERA: The user has taken a picture with a camera. 
	 *  - CODE_GALERY: The user has selected a picture from the gallery. 
	 */
	@Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(resultCode == BaseActivity.RESULT_OK){
            if (requestCode == Gegevens.CODE_GALLERY && null != data) {
            	setServiceImageFromUri(data.getData());
            } else if (requestCode == Gegevens.CODE_CAMERA){
            	setServiceImageFromUri(mCapturedImageURI);
            }
    	} else if (getActivity() != null){
    		((BaseActivity) getActivity() ).postToast(getString(R.string.details_error_captured_img));
    	}
	}

	/** Loads the newUser object's variables to the screen. */
	private void showUserInformation(boolean showinfo) {
		if(mRootView != null){
			
			// show or hide the information
			((ScrollView) mRootView.findViewById(R.id.details_container))
				.setVisibility((showinfo) ? View.VISIBLE : View.GONE);
			((ProgressBar) mRootView.findViewById(R.id.details_progress_bar))
				.setVisibility((showinfo) ? View.GONE : View.VISIBLE);

			if(newService != null){
				// set the image
				setServiceImage(newService.getLocalPhoto());
				
				// set the text
				((TextView) mRootView.findViewById(R.id.details_user_email)).setText(newService.getUseremail());
				((EditText) mRootView.findViewById(R.id.details_edit_title)).setText(newService.getServicetitle());
				((EditText) mRootView.findViewById(R.id.details_edit_description)).setText(newService.getServicedetail());
				
				// set the service details text watchers
				((EditText) mRootView.findViewById(R.id.details_edit_title))
					.addTextChangedListener(new ServiceDetailsWatcher(R.id.details_edit_title));
				((EditText) mRootView.findViewById(R.id.details_edit_description))
					.addTextChangedListener(new ServiceDetailsWatcher(R.id.details_edit_description));
				
				// set the service types
				Spinner typeSpinner = (Spinner) mRootView.findViewById(R.id.details_spin_type);
				if(typeSpinner.getAdapter() == null && getActivity() != null){ 
					// create the adapter for the service type spinner
					String [] tabTitles = getResources().getStringArray(R.array.services_tab_titles);
					ArrayAdapter <String> typeAdapter = new ArrayAdapter <String>(getActivity(),
							android.R.layout.simple_spinner_item, android.R.id.text1, tabTitles);
					typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					
					// The service types have not been loaded to the spinner.
					typeSpinner.setAdapter(typeAdapter);

					// set the listener 
					typeSpinner.setOnItemSelectedListener(new ServiceDetailsWatcher(R.id.details_spin_type));
				}
				
				// set the selection to the selected service type
				int position = newService.getType().getIndex(); 
				typeSpinner.setSelection(position);
			}
		}
	}
	
	/** Shows a dialog that asks the user if he wants to save the changes he has made to the service.
	 *  If the user has made no changes, the method does nothing and returns true.
	 *  @return true if new user equals old users, false otherwise.
	 */
	private boolean verifySaveService() {
		// Check if anything has changed
		boolean allow = (oldService != null && oldService.equals(newService));

		// If the user has changed anything, ask for confirmation
		if(!allow){ 
			userMustConfirm(getString(R.string.details_edit_confirm_title),
							getString(R.string.details_edit_confirm_text));
		}
		
		return allow;
	}
	
	/** Saves the service to the web server. The request is only sent if: <br />
	 *  - The user has changed anything in the service <br />
	 *  The reply to this question is handled in the various callback methods of the profile DAO
	 */
	private void saveService(){
		
		if (getActivity() != null) { 
			ServicesDAO servicesDAO = DaoFactory.getInstance().setServicesDAO(getActivity(), this, ServiceType.COMMUNITY, ServiceFlavor.MYSERVICE);

			// get the new service information
			String title = newService.getServicetitle();
			String detail = newService.getServicedetail();
			String photo = newService.getLocalPhoto();
			ServiceType type = newService.getType();
			
			if(oldService == null){
				// create a service
				servicesDAO.createService(DaoFactory.Source.DUMMY, type, 
						new String [] {newService.getUseremail(), title, detail});
			} else {
				// get the parameters 
				String [] parameters = new String [3];
				parameters[0] = (!oldService.getServicetitle().equals(title)) ? title : null;  
				parameters[1] = (!oldService.getServicedetail().equals(detail)) ? detail : null;  
				parameters[2] = (!oldService.getLocalPhoto().equals(photo)) ? photo : null;  
				
				// update the service
				servicesDAO.editService(DaoFactory.Source.DUMMY, newService.getId(), parameters);
			}
			
			// hide the user information until we get a response
			showUserInformation(false);
		} else {
			// Inform the user that we could not save the information.
			userMustClickOkay(getString(R.string.details_update_fail_title), 
					getString(R.string.details_update_fail_text));
		}
	}
	
	/** Loads the profile image from the provided URI. */
	private void setServiceImageFromUri(Uri imageUri){
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
        setServiceImage(picturePath);
	}
	
	/** Loads the service image from the provided path. */
	private void setServiceImage(String imageLocation){
		Loggen.v(this, getTag() + " - Getting image at: " + imageLocation );

		// Set the image
		if(setImageView(imageLocation, (ImageView) mRootView.findViewById(R.id.details_img))){
			// the image was successfully changed
			newService.setLocalPhoto(imageLocation);
		} else if (oldService != null){
			userMustClickOkay(getString(R.string.details_img_not_found_title), 
					getString(R.string.details_img_not_found_text));
		}
	}

	@Override public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.details_btn_save:
				saveService();
			break;
			case R.id.details_btn_img:
				registerForContextMenu(v); 
			    v.showContextMenu();
			    unregisterForContextMenu(v);
			break;
		}
	}
	
	@Override public boolean onNavigateUp(){ return verifySaveService(); }
	
	@Override public boolean allowBackPressed(){ return verifySaveService(); }

	/**	Default callback from all dialog fragments.  
	 * There is only one possible dialog worth considering: <br />
	 *  - Fragment Confirm: Only one possibility, namely the user wants to save his changes
	 */
	@Override public void onBasicPositiveButtonClicked(String tag, Object o) {
		if (Gegevens.FRAG_CONFIRM.equals(tag)){
			saveService();
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
	

	@Override public void onReadService(TrustService service, int numberOfUsers, List<JsonComment> comments) { }
	@Override public void writeServiceScore(boolean success) { }
	@Override public void writeServiceComment(boolean success) { }

	@Override public void onCreateService(boolean success) { 
		Loggen.v(this," Received onCreateService from dao: " + success);
		
		if(success){
			oldService = new TrustService(newService);
			oldService.setLocalPhoto("");
			if(newService.getLocalPhoto() == null || newService.getLocalPhoto().equals("")){
				mListener.performBackPress();
			} else {
				saveService();
			}
		} else {
			// the update failed. Inform the user.
			userMustClickOkay(getString(R.string.details_update_fail_title), 
					getString(R.string.details_update_fail_text));
			showUserInformation(true);
		}
	}

	@Override public void onEditService(boolean success) { 
		Loggen.v(this," Received onEditService from dao: " + success);
		
		// hide the progress bar (i.e. show the user information)
		showUserInformation(true);
		
		if(success){
			if(oldService == null) { new TrustService(newService); }
			oldService.setFromService(newService);
			mListener.performBackPress();
		} else {
			// the update failed. Inform the user.
			userMustClickOkay(getString(R.string.details_update_fail_title), 
					getString(R.string.details_update_fail_text));
		}
	}

	/** This class handles all the EditText fields. 
	 *  It just updates the corresponding variable in the new user. 
	 * @author Krishna
	 *
	 */
	private class ServiceDetailsWatcher implements TextWatcher, OnItemSelectedListener{

	    private int viewId;
	    private ServiceDetailsWatcher(int id) { this.viewId = id; }
	    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

	    public void afterTextChanged(Editable editable) {
	        switch(viewId){
            case R.id.details_edit_title:
                newService.setServicetitle(editable.toString());
                break;
            case R.id.details_edit_description:
                newService.setServicedetail(editable.toString());
                break;
	        }
	    }
	    
		@Override public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) { 
			if(viewId == R.id.details_spin_type){
				newService.setType(ServiceType.fromIndex(position));
			}
		}
		@Override public void onNothingSelected(AdapterView<?> arg0) { }
	}

	@Override
	public void onDeleteService(boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReadServices(List<TrustService> services) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSearchService(List<TrustService> services) {
		// TODO Auto-generated method stub
		
	}
}
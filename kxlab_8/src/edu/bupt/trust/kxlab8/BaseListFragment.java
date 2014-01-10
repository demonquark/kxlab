package edu.bupt.trust.kxlab8;

import java.io.File;

import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic.BasicDialogListener;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

public class BaseListFragment extends Fragment implements BasicDialogListener {

	OnActionSelectedListener mListener;

	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);

		Loggen.v(this, "Attaching.");
		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof OnActionSelectedListener)) {
			throw new IllegalStateException( "Activity must implement fragment's callbacks.");
		}
		mListener = (OnActionSelectedListener) activity;
	}
	
	@Override public void onDetach() {
		super.onDetach();
		Loggen.d(this, "Detaching.");
		mListener = null;
	}
	
	/** Launch a confirmation dialog from the activity context */
	protected void userMustClickOkay(String title, String message){
		DialogFragmentBasic.newInstance(false).setTitle(title).setMessage(message)
			.setPositiveButtonText(getString(R.string.ok))
			.show(getFragmentManager(), Gegevens.FRAG_DIALOG);
	}

	/** Launch a confirmation dialog from the activity context */
	protected void userMustConfirm(String title, String message){
		DialogFragmentBasic.newInstance(true)
			.setTitle(title)
			.setMessage(message)
			.setPositiveButtonText(getString(R.string.yes))
			.setNegativeButtonText(getString(R.string.no))
			.show(getFragmentManager(), Gegevens.FRAG_CONFIRM);
	}
	
	/** 
	 * Sets the bitmap of an image view
	 * @return successful True if the bitmap was set. False if not.  
	 * */ 
	protected boolean setImageView(String fileLocation, ImageView imgView){
		File imgFile = new File(fileLocation);
		if(imgFile.exists()){
			imgView.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
		    		imgFile.getAbsolutePath(),
		    		imgView.getLayoutParams().width, 
		    		imgView.getLayoutParams().height));
		}
		
		return imgFile.exists();
	}

	public interface OnActionSelectedListener {
		public void onActionSelected(String tag, String goal, Object o);
	}

	@Override public void onBasicPositiveButtonClicked(String tag, Object o) { }
	@Override public void onBasicNegativeButtonClicked(String tag, Object o) { }

}

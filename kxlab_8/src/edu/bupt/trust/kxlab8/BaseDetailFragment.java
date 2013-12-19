package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic.BasicDialogListener;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;

public class BaseDetailFragment extends Fragment  implements OnClickListener, BasicDialogListener {

	OnActionSelectedListener mListener;

	@Override public void onClick(View v) {
		// This is clearly an unknown button. Perhaps the activity can recognize the button.
		if(getActivity() != null){ ((BaseActivity) getActivity()).onBtnClick(v); }
	}

	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof OnActionSelectedListener)) {
			throw new IllegalStateException( "Activity must implement fragment's callbacks.");
		}
		mListener = (OnActionSelectedListener) activity;
	}
	
	@Override public void onDetach() {
		super.onDetach();
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
			.setTitle(getString(R.string.myinfo_edit_confirm_title))
			.setMessage(getString(R.string.myinfo_edit_confirm_text))
			.setPositiveButtonText(getString(R.string.yes))
			.setNegativeButtonText(getString(R.string.no))
			.show(getFragmentManager(), Gegevens.FRAG_CONFIRM);
	}

	/**
	 * This method allows you to implement a filter before the navigate up demand. <br />
	 * Note: This method is not automatically called.
	 * If you wish to use this method, you need to overwrite the onSupportNavigateUp() in your activity 
	 * and fire this method manually.
	 * @return true
	 */
	public boolean onNavigateUp(){
		return true;
	}
	
	/**
	 * This method allows you to implement a filter before the back pressed event. <br />
	 * Note: This method is not automatically called.
	 * If you wish to use this method, you need to overwrite the onBackPressed() in your activity 
	 * and fire this method manually.
	 * @return true
	 */
	public boolean allowBackPressed(){
		return true;
	}

	public interface OnActionSelectedListener{
		public void onActionSelected(String tag, Object o);
		public void performBackPress();
	}

	@Override public void onBasicPositiveButtonClicked(String tag, Object o) { }
	@Override public void onBasicNegativeButtonClicked(String tag, Object o) { }
	
}

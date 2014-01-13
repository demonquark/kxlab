package edu.bupt.trust.kxlab.widgets;

import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class DialogFragmentBasic extends DialogFragment {
    
	BasicDialogListener mListener;
	String [] mDialogStrings;
	String mListenerFragmentTag;
	Object mGenericObject;
	
	public DialogFragmentBasic() {
        // Empty constructor required for DialogFragment
    }
    
	/** <p>Create a new instance of a basic dialog fragment. <br />
	 *  The fragment shows a dialog box with a title and single message.
	 *  Note: DialogFragmentBasic automatically assign the associated activity as the listener. 
     * 	Hence if the associated activity wants to do something after the user has clicked a dialog button
     *  it should just implement the BasicDialogListener. </p>
     */
    public static DialogFragmentBasic newInstance(boolean hasNegativeButton) {
        return newInstance(hasNegativeButton, null);
    }
    

	/** <p>Create a new instance of a basic dialog fragment. <br />
	 *  The fragment shows a dialog box with a title and single message.
	 *  Note: DialogFragmentBasic automatically assign the provided listenerFragment as the listener. 
     * 	The listenerFragment must implement the BasicDialogListener. If the fragment has not implemented the
     *  BasicDialogListener, the class assigns the associated Activity as the listener </p>
     */
    public static DialogFragmentBasic newInstance(boolean hasNegativeButton, Fragment listenerFragment) {
    	DialogFragmentBasic f = new DialogFragmentBasic();
    	f.mListener = null;
    	f.mDialogStrings = new String [4];
    	f.mListenerFragmentTag = (listenerFragment != null) ? listenerFragment.getTag() : null; 
    	
        // Supply style input as an argument.
        Bundle args = new Bundle();
        args.putBoolean(Gegevens.EXTRA_HASNEGATIVE, hasNegativeButton);
        if(f.mListenerFragmentTag != null){ args.putString(Gegevens.EXTRA_TAG, f.mListenerFragmentTag); }
        f.setArguments(args);

        return f;
    }
    
    @Override public void onAttach(Activity activity) {
        if(mListener == null){
        	
        	// try to assign the listener fragment as listener
        	if(mListenerFragmentTag != null){
        		Loggen.d(this, "Got request to set listener fragment to " + mListenerFragmentTag);
        		Fragment listenerFrag = getActivity().getSupportFragmentManager().findFragmentByTag(mListenerFragmentTag);
        		if(listenerFrag != null && listenerFrag instanceof BasicDialogListener){
            		mListener = (BasicDialogListener) listenerFrag;
        		}
        	}
        	
        	// if that didn't work, try to assign the activity as listener
        	if( mListener == null && activity instanceof BasicDialogListener){ 
            	mListener = (BasicDialogListener) activity; 
            }
        } 
        super.onAttach(activity);
    }

    @Override public void onDetach() {
        mListener = null;
        super.onDetach();
    }
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	
    	// Save UI state changes to the savedInstanceState.
    	// This bundle will be passed to onCreate if the process is killed and restarted.
    	savedInstanceState.putStringArray(Gegevens.EXTRA_STRINGS, mDialogStrings);
    	savedInstanceState.putBoolean(Gegevens.EXTRA_HASNEGATIVE, getArguments().getBoolean(Gegevens.EXTRA_HASNEGATIVE, true));
    	savedInstanceState.putString(Gegevens.EXTRA_TAG, mListenerFragmentTag);
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	// return the dialog
        return buildDialog(savedInstanceState).create();
    }
    
    protected AlertDialog.Builder buildDialog(Bundle savedInstanceState){
    	// Make sure we have a valid string
    	if(savedInstanceState == null) { savedInstanceState = getArguments();}
    	if( mDialogStrings == null) { mDialogStrings = savedInstanceState.getStringArray(Gegevens.EXTRA_STRINGS); }
    	if( mDialogStrings == null) { mDialogStrings = new String [4]; }
    	
    	// Build the dialog (Set title and message)
    	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    	builder.setTitle((mDialogStrings[0] != null ) ?  mDialogStrings[0] : getString(android.R.string.dialog_alert_title));
    	builder.setMessage((mDialogStrings[1] != null ) ?  mDialogStrings[1] : getString(android.R.string.untitled));
    	
    	// Build the dialog (Set positive button)
    	builder.setPositiveButton((mDialogStrings[2] != null ) ?  mDialogStrings[2] : getString(android.R.string.ok), 
    			new DialogInterface.OnClickListener() {	
    				@Override public void onClick(DialogInterface dialog, int which) { 
    					if(mListener != null){ mListener.onBasicPositiveButtonClicked(getTag(), mGenericObject); }
    					dialog.dismiss(); 
    				}
        		});

    	// Build the dialog (Set negative button)
    	if(savedInstanceState.getBoolean(Gegevens.EXTRA_HASNEGATIVE, true)){
			builder.setNegativeButton((mDialogStrings[3] != null ) ?  mDialogStrings[3] : getString(android.R.string.cancel), 
					new DialogInterface.OnClickListener() {	
						@Override public void onClick(DialogInterface dialog, int which) { 
							if(mListener != null){ mListener.onBasicNegativeButtonClicked(getTag(), mGenericObject); }
							dialog.dismiss(); 
						}
		    		});
    	}
    	
    	return builder;
    }
    
   
	public DialogFragmentBasic setHasNegativeButton(boolean hasNegativeButton){
    	return this;
    }

    public DialogFragmentBasic setTitle(String title){
    	mDialogStrings[0] = title;
    	return this;
    }

    public DialogFragmentBasic setMessage(String message){
    	mDialogStrings[1] = message;
    	return this;
    }

    public DialogFragmentBasic setPositiveButtonText(String positiveButtonText){
    	mDialogStrings[2] = positiveButtonText;
    	return this;
    }

    public DialogFragmentBasic setNegativeButtonText(String negativeButtonText){
    	mDialogStrings[3] = negativeButtonText;
    	return this;
    }

    public DialogFragmentBasic setObject(Object o){
    	mGenericObject = o;
    	return this;
    }
    
    public DialogFragmentBasic setCancelableAndReturnSelf(boolean cancelable){
    	setCancelable(cancelable);
    	return this;
    }

    /** listener interface for DialogFragmentBasic */
    public interface BasicDialogListener {
		public void onBasicPositiveButtonClicked(String tag, Object o);
		public void onBasicNegativeButtonClicked(String tag, Object o);
    }
}

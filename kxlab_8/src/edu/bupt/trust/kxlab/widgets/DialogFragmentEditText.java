package edu.bupt.trust.kxlab.widgets;

import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab8.R;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

public class DialogFragmentEditText extends DialogFragmentBasic implements TextWatcher {
    
	RadioGroup mEditText;
	int mlayoutResource;
	
	public DialogFragmentEditText() {
        // Empty constructor required for DialogFragment
    }
	
	/** <p>Create a new instance of a basic dialog fragment. <br />
	 *  The fragment shows a dialog box with a title and single message.
	 *  Note: DialogFragmentBasic automatically assign the associated activity as the listener. 
     * 	Hence if the associated activity wants to do something after the user has clicked a dialog button
     *  it should just implement the BasicDialogListener. </p>
     */
    public static DialogFragmentEditText newInstance(boolean hasNegativeButton) {
        return newInstance(hasNegativeButton, null, 0);
    }
    

	/** <p>Create a new instance of a basic dialog fragment. <br />
	 *  The fragment shows a dialog box with a title and single message.
	 *  Note: DialogFragmentBasic automatically assign the provided listenerFragment as the listener. 
     * 	The listenerFragment must implement the BasicDialogListener. If the fragment has not implemented the
     *  BasicDialogListener, the class assigns the associated Activity as the listener </p>
     */
    public static DialogFragmentEditText newInstance(boolean hasNegativeButton, Fragment listenerFragment, 
    		int layoutResource) {
    	DialogFragmentEditText f = new DialogFragmentEditText();
    	f.mListener = null;
    	f.mDialogStrings = new String [4];
    	f.mListenerFragmentTag = (listenerFragment != null) ? listenerFragment.getTag() : null;
    	f.mlayoutResource = layoutResource;
    	
        // Supply style input as an argument.
        Bundle args = new Bundle();
        args.putBoolean(Gegevens.EXTRA_HASNEGATIVE, hasNegativeButton);
        if(f.mListenerFragmentTag != null){ args.putString(Gegevens.EXTRA_TAG, f.mListenerFragmentTag); }
        f.setArguments(args);

        return f;
    }

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	// make the dialog builder
    	AlertDialog.Builder builder = buildDialog(savedInstanceState);
    	
    	// inflate the custom view into dialog
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	View view = inflater.inflate(mlayoutResource == 0 ? R.layout.dialog_comment : mlayoutResource, null);
    	((EditText) view.findViewById(R.id.dialog_edit_text)).addTextChangedListener(this);
    	builder.setView(view);
    	
    	// return the dialog
        return builder.create();
    }
    
    @Override
    public DialogFragmentBasic setObject(Object o){
    	mGenericObject = new Pair<Object, String>(o, "");
    	return this;
    }

	@Override public void afterTextChanged(Editable s) { 
		String text = s.toString();
		
		// Save the score as an element of the generic object pair (note the first element is the original object)
		if(mGenericObject instanceof Pair<?, ?>){
			mGenericObject = new Pair<Object, String>(((Pair<?,?>) mGenericObject).first, text);
    	} else {
    		mGenericObject = new Pair<Object, String>(mGenericObject != null ? mGenericObject : new Object (), text);
    	}

	}

	@Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
	@Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
}

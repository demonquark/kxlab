package edu.bupt.trust.kxlab.widgets;

import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab8.R;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

public class DialogFragmentComment extends DialogFragmentBasic implements OnCheckedChangeListener {
    
	RadioGroup scRadioGroup;
	RadioButton scBtn01;
	RadioButton scBtn02;
	RadioButton scBtn03;
	
	public DialogFragmentComment() {
        // Empty constructor required for DialogFragment
    }
	
	/** <p>Create a new instance of a basic dialog fragment. <br />
	 *  The fragment shows a dialog box with a title and single message.
	 *  Note: DialogFragmentBasic automatically assign the associated activity as the listener. 
     * 	Hence if the associated activity wants to do something after the user has clicked a dialog button
     *  it should just implement the BasicDialogListener. </p>
     */
    public static DialogFragmentComment newInstance(boolean hasNegativeButton) {
        return newInstance(hasNegativeButton, null);
    }
    

	/** <p>Create a new instance of a basic dialog fragment. <br />
	 *  The fragment shows a dialog box with a title and single message.
	 *  Note: DialogFragmentBasic automatically assign the provided listenerFragment as the listener. 
     * 	The listenerFragment must implement the BasicDialogListener. If the fragment has not implemented the
     *  BasicDialogListener, the class assigns the associated Activity as the listener </p>
     */
    public static DialogFragmentComment newInstance(boolean hasNegativeButton, Fragment listenerFragment) {
    	DialogFragmentComment f = new DialogFragmentComment();
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

    @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    	
    	// make the dialog builder
    	AlertDialog.Builder builder = buildDialog(savedInstanceState);
    	
    	// inflate the custom view into dialog
    	LayoutInflater inflater = getActivity().getLayoutInflater();
    	View view = inflater.inflate(R.layout.dialog_score, null);
    	builder.setView(view);
    	
    	// return the dialog
        return builder.create();
    }
   

	@Override
	public void onCheckedChanged(RadioGroup arg0, int checkedId) {
		// TODO Auto-generated method stub
		if(checkedId == R.id.score01){
			mGenericObject = Integer.valueOf(-1);
		}
		else if(checkedId == R.id.score02){
			mGenericObject = Integer.valueOf(0);
		}
		else{
			mGenericObject = Integer.valueOf(1);
		}
	}
}

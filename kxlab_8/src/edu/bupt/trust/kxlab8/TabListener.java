package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar.Tab;

public class TabListener<T extends Fragment> implements android.support.v7.app.ActionBar.TabListener {
    private Fragment mFragment;
    private final ActionBarActivity mActivity;
    private final String mTag;
    private final Class<? extends Fragment> mClass;

    /** Constructor used each time a new tab is created.
      * @param activity  The host Activity, used to instantiate the fragment
      * @param tag  The identifier tag for the fragment
      * @param clz  The fragment's Class, used to instantiate the fragment
      */
    public TabListener(ActionBarActivity activity, String tag, Class<? extends Fragment> clz) {
    	this(activity, tag, clz, null);
        Loggen.v(this, "Created tablistener for a new fragment: " + clz.getName());
    }

    /** Constructor used each time a new tab is created.
     * @param activity  The host Activity, used to instantiate the fragment
     * @param tag  The identifier tag for the fragment
     * @param fragment  The fragment used as the tab content
     */
   public TabListener(ActionBarActivity activity, String tag, Fragment fragment) {
   	this(activity, tag, fragment.getClass(), fragment);
    Loggen.v(this, "Created tablistener for existing fragment: " + fragment.getClass().getName());
   }

   private TabListener(ActionBarActivity activity, String tag, Class<? extends Fragment> clz, Fragment fragment) {
       mActivity = activity;
       mTag = tag;
       mClass = clz;
       mFragment = fragment;
   }

/** The following are each of the ActionBar.TabListener call backs */
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
    	FragmentTransaction localft = mActivity.getSupportFragmentManager().beginTransaction();
        // Check if the fragment is already initialized
        if (mFragment == null) {
            Loggen.v(this, "Fragment does not exist. creating a new one." );

            // If not, instantiate and add it to the activity
    		Bundle arguments = new Bundle();
    		arguments.putString(Gegevens.EXTRA_TAG, mTag);

    		// If not, instantiate and add it to the activity
            mFragment = Fragment.instantiate(mActivity, mClass.getName(), arguments);
            localft.add(android.R.id.content, mFragment, mTag);
        } else {
            // If it exists, simply attach it in order to show it
            Loggen.v(this, "Fragment does exist. adding an existing one." );
            localft.attach(mFragment);
        }
        localft.commit();
    }

    @Override public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    	FragmentTransaction localft = mActivity.getSupportFragmentManager().beginTransaction();
    	if (mFragment != null) {
            // Detach the fragment, because another one is being attached
    		localft.detach(mFragment);
            //ft.remove(mFragment);
            //kris you forgot commit
    		localft.commit();
        }
    }

    @Override public void onTabReselected(Tab tab, FragmentTransaction ft) {
        // User selected the already selected tab. Usually do nothing.
    }

}
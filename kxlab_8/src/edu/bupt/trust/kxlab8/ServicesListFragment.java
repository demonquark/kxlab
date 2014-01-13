package edu.bupt.trust.kxlab8;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.ServicesArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.ServicesDAO;
import edu.bupt.trust.kxlab.data.ServicesDAO.ServicesListener;
import edu.bupt.trust.kxlab.model.JsonComment;
import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.ServiceType;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic;
import edu.bupt.trust.kxlab.widgets.XListView;
import edu.bupt.trust.kxlab.widgets.XListView.IXListViewListener;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.RawResponse.Page;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ServicesListFragment extends BaseListFragment 
						implements ServicesListener, OnItemClickListener, IXListViewListener{
	
	private enum State { DELETE, LOADING, IDLE };
	
	private SearchView mSearchView;
	private ActionMode mActionMode;
	ArrayList<TrustService> mServices;
	ServiceType mType;
	ServiceFlavor mFlavor;
	private State state;
	private View mRootView;
	private XListView mListView;
	private String mSearchTerm;
	
	public ServicesListFragment() {
        // Empty constructor required for ServicesListFragment
	}
	
    @Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);
    }
    
	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		// add the services menu
			inflater.inflate(R.menu.services, menu);
		if (mFlavor == ServiceFlavor.MYSERVICE)
			inflater.inflate(R.menu.myservices, menu);
	    
		// set up the search view
		MenuItem searchItem = menu.findItem(R.id.action_search);
		MenuItemCompat.setOnActionExpandListener(searchItem, this);
	    mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    setupSearchView();
	}

    @Override public boolean onOptionsItemSelected(MenuItem item) {

    	// Only process requests if we we've enabled interaction 
    	int itemId = item.getItemId();
    	if(state != State.LOADING){
            switch (itemId) {
            	case R.id.action_delete:
            		if (mActionMode == null) { changeToDeleteListView(); }
                break;
            	case R.id.action_create:
            		startServicesDetailActivity(null);
                break;
                default:
                	return super.onOptionsItemSelected(item);
            }
    	} else if (itemId == android.R.id.home) {
    		// make an exception for the home button
    		return super.onOptionsItemSelected(item);
    	}
    	
    	return true;
    }
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Loggen.v(this, getTag() + " - Creating a new ServicesList instance.");
		
		// Assign values to the class variables (This avoids facing null exceptions when creating / saving a Parcelable)
		// convert the saved state to an empty bundle to avoid errors later on
		Bundle savedstate = (savedInstanceState != null) ? savedInstanceState : new Bundle();
		Bundle arguments = (getArguments() != null) ? getArguments() : new Bundle();
		
		// Use the tag to determine the service type
		String tag = getTag();
		if(Gegevens.FRAG_RECOMMEND.equals(tag)){ mType = ServiceType.RECOMMENDED;
		} else if(Gegevens.FRAG_APPLY.equals(tag)){ mType = ServiceType.APPLY;
		} else { mType = ServiceType.COMMUNITY; }
		
		// load the services (Note: services remains null if it is neither in the saved state nor the arguments)
		mServices = savedstate.getParcelableArrayList(Gegevens.EXTRA_SERVICES); 							
		if(mServices == null){ mServices = arguments.getParcelableArrayList(Gegevens.EXTRA_SERVICES); } 	
		
		// load the service flavor. This determines what the user can do while viewing the service details.
		mFlavor = (ServiceFlavor) savedstate.getSerializable(Gegevens.EXTRA_FLAVOR);
		if(mFlavor == null) { mFlavor = (ServiceFlavor) arguments.getSerializable(Gegevens.EXTRA_FLAVOR); }
		if(mFlavor == null && getActivity() instanceof ServicesListActivity) { 
			mFlavor = ((ServicesListActivity) getActivity()).getFlavor(); }
		if(mFlavor == null) { mFlavor = ServiceFlavor.SERVICE; }
		
		Loggen.v(this, getTag() + " - Flavor is " + mFlavor + " | services exist?" + (mServices != null));

		// load the state
		state = (State) savedstate.getSerializable(Gegevens.EXTRA_STATE);
		if(state == null){ state = State.IDLE; }
		
		// load the search term
		mSearchTerm = savedstate.getString(Gegevens.EXTRA_SEARCHTERM); 							
		if(mSearchTerm == null){ mSearchTerm = arguments.getString(Gegevens.EXTRA_SEARCHTERM); } 	
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the ServicesList view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_generic_xlist, container, false);

		// add some padding for the footer 
		int bottomPadding = (int) getResources().getDimension(R.dimen.footer_height);
		mRootView.findViewById(R.id.list_top_container).setPadding(0,0,0,bottomPadding);

		// set this fragment as the listener for the xlist 
		mListView = (XListView) mRootView.findViewById(android.R.id.list);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		return mRootView;
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring ServiceList instance state.");

		// load the posts list if requested (generally only if we just created the fragment)
		if(mServices == null){
			// Load the services from the DAO
			getData(Source.WEB, Page.CURRENT);
		}

		// show or hide the list
		showList(mServices != null);
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving ServiceList instance state.");
		// save the list of services to the instance state
		outState.putParcelableArrayList(Gegevens.EXTRA_SERVICES, mServices);
		outState.putSerializable(Gegevens.EXTRA_FLAVOR, mFlavor);
		outState.putSerializable(Gegevens.EXTRA_STATE, state);
		if(mSearchTerm != null && !mSearchTerm.equals("")){
			outState.putString(Gegevens.EXTRA_SEARCHTERM, mSearchTerm);
		}
	}

    private void setupSearchView() {
    	
    	if(getActivity() != null){
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            if (searchManager != null) {
                SearchableInfo info = searchManager.getSearchableInfo(getActivity().getComponentName());
                mSearchView.setSearchableInfo(info);
            }
    	}
        mSearchView.setOnQueryTextListener(this);
    }

	private void showList(boolean showlist) {
		
		if(mListView != null){ 
			if(mListView.isPullLoading()){
				mListView.stopLoadMore();
			}
			if(mListView.isPullRefreshing()){
				mListView.stopRefresh(); 
				mListView.updateHeaderTime();
			}
		}

		// show or hide the progress bar
		mListView.setVisibility((showlist) ? View.VISIBLE : View.GONE);
		((ProgressBar) mRootView.findViewById(R.id.progress_bar))
			.setVisibility((showlist) ? View.GONE : View.VISIBLE);
		
		// TODO: update the state
		if(!showlist) { state = State.LOADING; } else { state = State.IDLE; }
		
		// load the services list
		if(mServices != null && mListView != null && showlist){
			Loggen.v(this, getTag() + " - Loading the adapter with " + mServices.size() + " items.");
			
			if(mListView.getAdapter() == null ){ 
				// The comments have not been loaded to the list view. Do that now
				if(getActivity() != null){
					ServicesArrayAdapter a = new ServicesArrayAdapter(getActivity(), 
							R.layout.list_item_services, android.R.id.text1, mServices);
					mListView.setAdapter(a);
					mListView.setOnItemClickListener(this);
				}
			} else {
				// The comments are already loaded to the list view.
				((BaseAdapter)((HeaderViewListAdapter)mListView.getAdapter())
						.getWrappedAdapter()).notifyDataSetChanged();
			}
			
			// set the choice mode and reaction to the choices 
			mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			
			if(mServices.size() == 0){
				((TextView) mRootView.findViewById(android.R.id.empty)).setVisibility(View.VISIBLE);	
			}
		}
	}
	
	private void changeToDeleteListView() {
		Loggen.v(this, getTag() + " - Changing to delete mode.");
		if(getActivity() != null){
			// set the choice mode and reaction to the choices 
			state = State.DELETE;
			mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
			mActionMode = getActivity().startActionMode(new DeleteServicesMode ());
		}
	}
	
	private void startServicesDetailActivity(TrustService service){
		if(getActivity() != null){
			// Bundle the post and send it off to the detail activity
			Bundle b = new Bundle();
			
			if(service != null) { 
				b.putParcelable(Gegevens.EXTRA_SERVICE, service); 
			} else {
				b.putSerializable(Gegevens.EXTRA_SERVICETYPE, mType);
			}
			b.putSerializable(Gegevens.EXTRA_FLAVOR, mFlavor);
			
			Intent intent = new Intent(getActivity(), ServiceDetailActivity.class);
			intent.putExtra(Gegevens.EXTRA_MSG, b);
			this.startActivity(intent);
		}
	}

	private void getData(Source source, Page page) {
		if(getActivity() != null){
			Loggen.v(this, "We are asked to load services with searchterm = " + mSearchTerm);
			ServicesDAO servicesDAO = DaoFactory.getInstance().setServicesDAO(getActivity(), this, mType, mFlavor);
			
			// add the email for my services
			String email = null;
			if(mFlavor == ServiceFlavor.MYSERVICE){
				email = ((BaseActivity) getActivity()).mSettings.getUser().getEmail();
			}
			
			// add the search term
			String searchterm = null;
			if(mSearchTerm != null && !mSearchTerm.equals("")){
				searchterm = mSearchTerm;
			}
			
			servicesDAO.readServices(source, mType, mFlavor, email, searchterm, mServices, page);	
		}
	}
	
	private void deleteServices(List <Integer> serviceIds){
		if(getActivity() != null){
			ServicesDAO servicesDAO = DaoFactory.getInstance().setServicesDAO(getActivity(), this, mType, mFlavor);
			servicesDAO.deleteServices(serviceIds);
		}
	}
	
	@Override public void onRefresh() {
		Loggen.v(this, " called onRefresh.");
		getData(Source.WEB, Page.LATEST);
	}
	
	@Override
	public void onLoadMore() {
		Loggen.v(this, " called onLoadMore.");
		getData(Source.WEB, Page.PREVIOUS);
	}

	@Override public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
		
		// only keep the items activated if we're in the delete state
		if(state != State.DELETE){ mListView.setItemChecked(position, false); }

		// only show service details if we're in the IDLE state
		if(state == State.IDLE){
			// Pass the service we have now. Which might be out of date or incomplete.
			if(position > 0 && position <= mServices.size()){
				// TODO: Figure out why the method is returning the wrong position
				position--; 
				startServicesDetailActivity(mServices.get(position));
			}
		}
	}
	
	@Override public void onReadServices(List<TrustService> services) {
		Loggen.v(this, "Got a response onReadservices. services exist? " + (services != null));
		
		if(getActivity() != null){
			if(services != null && mServices != null){
				// We got a response and are updating an existing list
				mServices.clear();
				mServices.addAll(services);
			} else if(services == null && mServices == null){
				// We got no response and have no existing list
				userMustClickOkay(getString(R.string.forum_error_update_title), getString(R.string.forum_error_update_text)); 
				mServices = new ArrayList<TrustService> ();
				getData(Source.LOCAL, Page.CURRENT);
			} else if (mServices == null) {
				// We got a response, but have no existing list 
				mServices = (ArrayList<TrustService>) services;			
			}
			
			for(TrustService service : mServices){
				service.setFlavor(mFlavor);
			}
		}
		showList(true);	
	}

 	@Override public void onDeleteService(int itemsDeleted) {
 		
 		if(mListView != null){
 			mListView.clearChoices();
 		}
 		
 		if(itemsDeleted > 0){
 			userMustClickOkay(getString(R.string.services_delete_success_title), getString(R.string.services_delete_success_text));
 			getData(Source.WEB, Page.LATEST);
 		} else {
 			userMustClickOkay(getString(R.string.services_delete_failure_title), getString(R.string.services_delete_failure_text));
 			showList(true);
 		}
	}

	@Override public void onSearchService(List<TrustService> services) {
		// TODO Auto-generated method stub
		
	}	

	@Override public void onBasicPositiveButtonClicked(String tag, Object o) {
		if(Gegevens.FRAG_DELETE.equals(tag) && o instanceof List<?>){
			List <?> list = (List<?>) o;
			List <Integer> deletionQuery = new ArrayList<Integer> ();
			for(Object i : list){
				if(i instanceof Integer){
					deletionQuery.add((Integer) i);
				}
			}
			
			// handle the query
			deleteServices(deletionQuery);
			showList(false);
		} else {
			showList(mServices != null);	
		}
	}

	@Override public void onBasicNegativeButtonClicked(String tag, Object o) {showList(mServices != null); }

	// Contact the DAO only if the user has submitted a full request
	@Override public boolean onQueryTextSubmit(String arg0) { 
		Loggen.v(this, getTag() + " - text submitted: " + arg0);
	    
		// set the search term
		mSearchTerm = arg0;

		// clear the search view focus
		mSearchView.clearFocus();
		showList(false);
	
		// process search
		getData(Source.WEB, Page.CURRENT);
		return true; 
	}
	
	/**
	 * DeleteServicesMode allows you to pick items from the list and delete them.<br />
	 * Note: Rather than adding menu items to the context menu, I've decided to just process the request 
	 * @author Krishna
	 */
	private class DeleteServicesMode implements ActionMode.Callback{

		/** Provide the user with a hint, so he knows what to do. */
		@Override public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			((BaseActivity) getActivity()).postToast(getString(R.string.services_delete_hint));
			return true; 
		}

		/** Delete the selected items. */
		@Override public void onDestroyActionMode(ActionMode mode) {
			boolean willdelete = (mListView != null && mServices != null && mListView.getCheckedItemCount() > 0);
			
			// Make sure that the user has selected valid items
	        if(willdelete){
	        	
	        	// Get and list the selected items
	        	SparseBooleanArray checkedItems = mListView.getCheckedItemPositions();
	        	String confirmationText = getString(R.string.services_delete_confirm_text);
	        	List <Integer> deletionList = new ArrayList<Integer> ();
	        	int listSize = mServices.size();
	        	for(int i = 0; i < listSize; i++){
	        		if(checkedItems.get(i)){
	        			confirmationText += "\n" + mServices.get(i).getServicetitle();
	        			// TODO: put logic to build deletion query (now it just adds the serviceIds)
	        			deletionList.add(mServices.get(i).getId());
	        		}
	        	}
	        	
	        	// Ask the user to confirm his choice
	        	DialogFragmentBasic deletedialog = DialogFragmentBasic.newInstance(true, ServicesListFragment.this)
	        		.setTitle(getString(R.string.services_delete_confirm_title)).setMessage(confirmationText)
	        		.setObject(deletionList)
	        		.setPositiveButtonText(getString(R.string.ok))
	        		.setNegativeButtonText(getString(R.string.cancel));
	        		
	        	// note: the user cannot cancel this confirmation 
	        	deletedialog.setCancelable(false);
	        	deletedialog.show(getActivity().getSupportFragmentManager(), Gegevens.FRAG_DELETE);
	        	
	        } else {
	        	// Inform the user that nothing was deleted
	        	DialogFragmentBasic confirmdialog = DialogFragmentBasic.newInstance(false, ServicesListFragment.this)
	        		.setTitle(getString(R.string.services_delete_empty_title))
        			.setMessage(getString(R.string.services_delete_empty_text))
        			.setPositiveButtonText(getString(R.string.ok));

	        	// note: the user cannot cancel this notification 
	        	confirmdialog.setCancelable(false);
	        	confirmdialog.show(getActivity().getSupportFragmentManager(), Gegevens.FRAG_CONFIRM);
	        }

			mActionMode = null;
		}
		
		@Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) { return true; }
		@Override public boolean onPrepareActionMode(ActionMode mode, Menu menu) { return false; }

	}

	@Override public void onReadService(TrustService service, List<JsonComment> comments) { }
	@Override public void onUpdateServiceScore(boolean success) { }
	@Override public void onCreateComment(boolean success) { }
	@Override public void onEditService(boolean success) { }
	@Override public void onCreateService(boolean success) { }
	
	@Override public boolean onMenuItemActionCollapse(MenuItem item) { 
		mSearchTerm = null;
		getData(Source.WEB, Page.CURRENT);
		showList(false);
		return true; }

}
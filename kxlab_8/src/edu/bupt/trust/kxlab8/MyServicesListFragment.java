package edu.bupt.trust.kxlab8;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.ServicesArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.MyServicesDAO;
import edu.bupt.trust.kxlab.data.ServicesDAO;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesListListener;
import edu.bupt.trust.kxlab.model.Settings;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic;
import edu.bupt.trust.kxlab.widgets.XListView;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic.BasicDialogListener;
import edu.bupt.trust.kxlab.widgets.XListView.IXListViewListener;
import edu.bupt.trust.kxlab.data.RawResponse.Page;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MyServicesListFragment extends ListFragment 
						implements MyServicesListListener, BasicDialogListener, OnQueryTextListener,IXListViewListener,OnCloseListener{

	
	private enum State { DELETE, LOADING, IDLE };
	
	private boolean mLoadServices;
	private OnServiceSelectedListener mListener;
	private SearchView mSearchView;
	private LinearLayout mProgressContainer;
	private ActionMode mActionMode;
	ArrayList<TrustService> services;
	MyServicesDAO.Type servicesType;
	private State state;
	private int requestType=0;
	private User user;
	private XListView mListView;
	
	public MyServicesListFragment() {
        // Empty constructor required for ServicesListFragment
	}
	
    @Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);
    }
    
	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		// add the services menu
		inflater.inflate(R.menu.myservices, menu);
	    
		// set up the search view
		MenuItem searchItem = menu.findItem(R.id.action_search);
	    mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	    mSearchView.setOnCloseListener(this);

	    setupSearchView();
	}

    @Override public boolean onOptionsItemSelected(MenuItem item) {

    	// Only process requests if we we've enabled interaction 
    	int itemId = item.getItemId();
    	if(state != State.LOADING){
            switch (itemId) {
            	case R.id.action_create:
            		if(mListener != null) { mListener.onCreateService(getTag()); }
                break;
            	case R.id.action_delete:
            		if (mActionMode == null) { changeToDeleteListView(); }
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
		Settings.getInstance(getActivity()).loadSettingsFromSharedPreferences(getActivity());
		user=Settings.getInstance(getActivity()).getUser();
		if (user==null) {
			user=new User();
		}
		
		// Use the tag to determine the service type
		String tag = getTag();
		if(Gegevens.FRAG_RECOMMEND.equals(tag)){ servicesType = MyServicesDAO.Type.RECOMMENDED;
		} else if(Gegevens.FRAG_APPLY.equals(tag)){ servicesType = MyServicesDAO.Type.APPLY;
		} else { servicesType = MyServicesDAO.Type.COMMUNITY; }
		
		// load the services (Note: services remains null if it is neither in the saved state nor the arguments)
		services = savedstate.getParcelableArrayList(Gegevens.EXTRA_SERVICES); 							
		if(services == null){ services = arguments.getParcelableArrayList(Gegevens.EXTRA_SERVICES); } 	
		
		// load the state
		state = (State) savedstate.getSerializable(Gegevens.EXTRA_STATE);
		if(state == null){ state = State.IDLE; }
		
		// We just created a fragment. So reset the list on restore
		mLoadServices = true;
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the ServicesList view. ");

		// Inflate the root view and save references to useful views as class variables
		View rootView = inflater.inflate(R.layout.frag_serviceslist, container, false);
		mListView = (XListView) rootView.findViewById(android.R.id.list);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
		mProgressContainer = (LinearLayout) rootView.findViewById(R.id.progress_container);

		return rootView;
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring ServiceList instance state.");

		// load the services list if requested (generally only if we just created the fragment)
		if(mLoadServices){
			if(services == null){
				// Load the services from the DAO
				showList(false);
				geneData(requestType);
				Loggen.v(this, "Restoring saved Instancestate: Hide the list");
			}else{
				// If we already have a list of services, just show those services
				initListView();
			}
		}
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving ServiceList instance state.");
		// save the list of services to the instance state
		outState.putParcelableArrayList(Gegevens.EXTRA_SERVICES, services);
		outState.putSerializable(Gegevens.EXTRA_STATE, state);
	}
	
	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof OnServiceSelectedListener)) {
			throw new IllegalStateException( "Activity must implement fragment's callbacks.");
		}
		mListener = (OnServiceSelectedListener) activity;
	}
	
	@Override public void onDetach() {
		super.onDetach();
		mListener = null;
	}
	
	@Override public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		// only keep the items activated if we're in the delete state
		if(state != State.DELETE){ listView.setItemChecked(position, false); }

		// only show service details if we're in the IDLE state
		if(state == State.IDLE){
			// TODO: edit this to get the service details from the server (?)
			// Now it just passes the service we have now. Which might be out of date or incomplete.
			mListener.onItemSelected(getTag(), position, services.get(position)); 
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
		mListView.setVisibility((showlist) ? View.VISIBLE : View.GONE);
		mProgressContainer.setVisibility( (!showlist) ? View.VISIBLE : View.GONE);
		if(!showlist) { state = State.LOADING; } else { state = State.IDLE; }
	}

	private void initListView() {
		Loggen.v(this, getTag() + " - initlistview: Create and set a list adapter for the listview.");
		if(getActivity() != null){
			// load a new adapter
			ServicesArrayAdapter a = new ServicesArrayAdapter(getActivity(), 
					R.layout.list_item_services, android.R.id.text1, services);
			
			// set the adapter
			setListAdapter(a);
			
			// set the choice mode and reaction to the choices 
			mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			
			showList(true);
			mLoadServices = false;
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
	
	@Override public void onReadServices(List<TrustService> services) {
		Loggen.i(this, getTag() + " - Returned from onReadservices. ");

		if(mListView != null){ 
			if(mListView.isPullLoading()){
				mListView.stopLoadMore();
			}
			if(mListView.isPullRefreshing()){
				mListView.stopRefresh(); 
				mListView.updateHeaderTime();
			}
		}
		
		ArrayList<TrustService> servicesTemp = new ArrayList <TrustService> ();
		// update the services
		servicesTemp = (ArrayList<TrustService>) ((services != null) ? services : new ArrayList <TrustService> ());
		//System.out.println("tempServices :"+requestType+services.toString());
		if (requestType==0) {
			//System.out.println("tempServices :"+requestType+servicesTemp.toString());
			this.services=servicesTemp;
			//System.out.println("this.Services :"+requestType+this.services.get(0).getServiceid());
		}else {
			for (TrustService s:servicesTemp) {
				this.services.add(s);
			}
		}
		
		// update the UI
		initListView();
	}

  /**
   * 
   * @param flag 0 means latest services,1 means previous services
   */
	private void geneData(int flag) {
		if(getActivity() != null){
			// TODO: process refresh request (for now it just reloads the list)
			MyServicesDAO myServicesDAO = DaoFactory.getInstance().setMyServicesDAO(getActivity(), this, servicesType);
			switch (flag) {
			case 0:
				myServicesDAO.readServices(servicesType,Page.LATEST, DaoFactory.Source.DUMMY, new String [] {user.getEmail()});
				break;
			case 1:
				myServicesDAO.readServices(servicesType,Page.PREVIOUS, DaoFactory.Source.DUMMY, new String [] {user.getEmail()});
				break;
			default:
				myServicesDAO.readServices(servicesType,Page.LATEST, DaoFactory.Source.DUMMY, new String [] {user.getEmail()});
				break;
			}

		}
	}


	@Override public void onBasicPositiveButtonClicked(String tag, Object o) {
		if(Gegevens.FRAG_DELETE.equals(tag)){
			// TODO: process deletion (for now it does the same as non delete)
			ArrayList<Integer> deleteQuery = (ArrayList<Integer>) o;
			for(Integer i : deleteQuery){
				int serviceSize = services.size();
				for(int j = 0; j < serviceSize; j++){
					if(services.get(j).getId() == i){
						services.remove(j);
						break;
					}
				}
			}
			
			initListView();
		} else {
			initListView();
		}
	}

	@Override public void onBasicNegativeButtonClicked(String tag, Object o) { initListView(); }

	// Do NOT search if the user just changed the text
	@Override public boolean onQueryTextChange(String arg0) { return false; }
	// Contact the DAO only if the user has submitted a full request
	@Override public boolean onQueryTextSubmit(String arg0) { 
		Loggen.v(this, getTag() + " - text submitted. ");
	    mSearchView.clearFocus();
		showList(false);
	    // TODO: process search (for now it just reloads the list)
		geneData(requestType);
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
			boolean willdelete = (mListView != null && services != null && mListView.getCheckedItemCount() > 0);
			
			// Make sure that the user has selected valid items
	        if(willdelete){
	        	
	        	// Get and list the selected items
	        	SparseBooleanArray checkedItems = getListView().getCheckedItemPositions();
	        	String confirmationText = getString(R.string.services_delete_confirm_text);
	        	ArrayList<Integer> deleteQuery = new ArrayList<Integer> ();
	        	int listSize = services.size();
	        	for(int i = 0; i < listSize; i++){
	        		if(checkedItems.get(i)){
	        			confirmationText += "\n" + services.get(i).getServicetitle();
	        			// TODO: put logic to build deletion query (now it just adds the serviceIds)
	        			deleteQuery.add(services.get(i).getId());
	        		}
	        	}
	        	
	        	// Ask the user to confirm his choice
	        	DialogFragmentBasic deletedialog = DialogFragmentBasic.newInstance(true, MyServicesListFragment.this)
	        		.setTitle(getString(R.string.services_delete_confirm_title)).setMessage(confirmationText)
	        		.setObject(deleteQuery)
	        		.setPositiveButtonText(getString(R.string.ok))
	        		.setNegativeButtonText(getString(R.string.cancel));
	        		
	        	// note: the user cannot cancel this confirmation 
	        	deletedialog.setCancelable(false);
	        	deletedialog.show(getActivity().getSupportFragmentManager(), Gegevens.FRAG_DELETE);
	        	
	        } else {
	        	// Inform the user that nothing was deleted
	        	DialogFragmentBasic confirmdialog = DialogFragmentBasic.newInstance(false, MyServicesListFragment.this)
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
	
	public interface OnServiceSelectedListener{
		public void onItemSelected(String tag, int position, TrustService service);
		public void onCreateService(String tag);
	}

	@Override public void onRefresh() {
		requestType=0;
		geneData(requestType);	
	}
	
	@Override
	public void onLoadMore() {
		requestType=1;
		geneData(requestType);	
	}

	@Override
	public void onCreateService(boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDeleteService(boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEditService(boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSearchService(List<TrustService> services) {
		mListView.hideFooter();
		mListView.hideHeader();
		this.services = (ArrayList<TrustService>) ((services != null) ? services : new ArrayList <TrustService> ()); 

		// update the UI
		initListView();
	}
	
	@Override
	public boolean onClose() {
		mListView.showFooter();
		mListView.showHeader();
		onRefresh();
		return false;
	}
}
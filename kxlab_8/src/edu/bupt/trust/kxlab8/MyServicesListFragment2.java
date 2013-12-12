package edu.bupt.trust.kxlab8;

import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.text.AndroidCharacter;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import edu.bupt.trust.kxlab.adapters.ServicesArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.MyServicesDAO;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesListListener;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.XListView;
import edu.bupt.trust.kxlab.widgets.XListView.IXListViewListener;


public class MyServicesListFragment2 extends ListFragment 
						implements MyServicesListListener,IXListViewListener{
	
	private enum State { DELETE, LOADING, IDLE };
	
	private boolean mLoadServices;
	private OnServiceSelectedListener mListener;
	private SearchView mSearchView;
	private LinearLayout mProgressContainer;
	private ActionMode mActionMode;
	ArrayList<TrustService> services;
	MyServicesDAO.Type servicesType;
	private State state;
	
	
	private XListView mListView;
	private Handler mHandler;

	
	
	public MyServicesListFragment2() {
        // Empty constructor required for ServicesListFragment
	}
	
    @Override public void onActivityCreated(Bundle savedInstanceState) {
    	System.out.println("onActivityCreated");
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);
    }
    
	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		System.out.println("onCreateOptionsMenu");
		super.onCreateOptionsMenu(menu, inflater);
        
		// add the services menu
		inflater.inflate(R.menu.myservices, menu);
	    
		// set up the search view
		MenuItem searchItem = menu.findItem(R.id.action_search);
	    mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
	}

    @Override public boolean onOptionsItemSelected(MenuItem item) {
    	System.out.println("onOptionsItemSelected");
    	// Only process requests if we we've enabled interaction 
    	int itemId = item.getItemId();
    	if(state != State.LOADING){
            switch (itemId) {
            	case R.id.action_create:
            		mListener.onCreateService(getTag());
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
	
    
	private void showList(boolean showlist) {
		System.out.println("showList");
		mListView.setVisibility((showlist) ? View.VISIBLE : View.GONE);
		mProgressContainer.setVisibility( (!showlist) ? View.VISIBLE : View.GONE);
		if(!showlist) { state = State.LOADING; } else { state = State.IDLE; }
	}
	
	@Override public void onCreate(Bundle savedInstanceState) {
		System.out.println("onCreate");
		super.onCreate(savedInstanceState);
		Loggen.v(this, getTag() + " - Creating a new ServicesList instance.");
		
		// Assign values to the class variables (This avoids facing null exceptions when creating / saving a Parcelable)
		// convert the saved state to an empty bundle to avoid errors later on
		Bundle savedstate = (savedInstanceState != null) ? savedInstanceState : new Bundle();
		Bundle arguments = (getArguments() != null) ? getArguments() : new Bundle();
		
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
		System.out.println("onCreate end");
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the ServicesList view. ");
		System.out.println("onCreateView");
		// Inflate the root view and save references to useful views as class variables
		View rootView = inflater.inflate(R.layout.main, container, false);
		mListView = (XListView) rootView.findViewById(android.R.id.list);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);
		mProgressContainer = (LinearLayout) rootView.findViewById(R.id.progress_container);
		//mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, items);
		mHandler = new Handler();
		return rootView;
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring ServiceList instance state.");

		// load the services list if requested (generally only if we just created the fragment)
		if(mLoadServices){
			if(services == null){
				showList(false);
				// Load the services from the DAO
				MyServicesDAO myServicesDAO = DaoFactory.getInstance().setMyServicesDAO(getActivity(), this, servicesType);
				myServicesDAO.readServices(servicesType, DaoFactory.Source.DUMMY, new String [] {});
				Loggen.v(this, "Restoring saved Instancestate: Hide the list");
			}else{
				// If we already have a list of services, just show those services
				initListView();
			}
		}
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		System.out.println("onSaveInstanceState");
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
	
   

	private void initListView() {
		System.out.println("initListView");
		Loggen.v(this, getTag() + " - initlistview: Create and set a list adapter for the listview.");

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

	private void changeToDeleteListView() {
		Loggen.v(this, getTag() + " - Changing to delete mode.");

		// set the choice mode and reaction to the choices 
		state = State.DELETE;
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}
	
	
	
	@Override public void onReadServices(List<TrustService> services) {
		Loggen.i(this, getTag() + " - Returned from onReadservices. ");

		
		// update the services
		this.services = (ArrayList<TrustService>) ((services != null) ? services : new ArrayList <TrustService> ());
		
		// update the UI
		initListView();
	}


	
	public interface OnServiceSelectedListener{
		public void onItemSelected(String tag, int position, TrustService service);
		public void onCreateService(String tag);
	}



	
	private void geneData() {
		MyServicesDAO myServicesDAO = DaoFactory.getInstance().setMyServicesDAO(getActivity(), this, servicesType);
		myServicesDAO.readServices(servicesType, DaoFactory.Source.DEFAULT, new String [] {});
	}


	
	@Override
	public void onRefresh() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
				geneData();
				mListView.stopRefresh();
				mListView.updateHeaderTime();
			}
		}, 2000);
	}

	@Override
	public void onLoadMore() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				geneData();
				mListView.stopLoadMore();
			}
		}, 500);
	}

	@Override
	public void onReadService(TrustService service) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}
	
}
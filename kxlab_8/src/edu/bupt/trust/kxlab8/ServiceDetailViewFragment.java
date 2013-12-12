package edu.bupt.trust.kxlab8;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.ServicesArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.MyServicesDAO;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesListListener;
import edu.bupt.trust.kxlab.data.MyServicesDAO.Type;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ServiceDetailViewFragment extends ListFragment implements MyServicesListListener {
	
	boolean mLoadComments;
	OnActionSelectedListener mListener;
	private ListView mServiceList;
	private LinearLayout mProgressContainer;
	private LinearLayout mListHolder;
	TrustService mService;
	MyServicesDAO.Type servicesType;
	ArrayList<TrustService> comments;
	
	public ServiceDetailViewFragment (){
		// Empty constructor required for ServiceDetailViewFragment
	}
	
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	
		// add the services menu
		inflater.inflate(R.menu.service_detail_view, menu);
	}
	
    @Override public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
        switch (itemId) {
        	case R.id.action_edit:
        		mListener.onActionSelected(getTag(), mService);
            break;
            default:
            	return super.onOptionsItemSelected(item);
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
		
		// load the service (Note: service remains null if it is neither in the saved state nor the arguments)
		mService = savedstate.getParcelable(Gegevens.EXTRA_SERVICE); 							
		if(mService == null){ mService = arguments.getParcelable(Gegevens.EXTRA_SERVICE); } 
		
		// load the comments (Note: comments remains null if it is neither in the saved state nor the arguments)
		comments = savedstate.getParcelableArrayList(Gegevens.EXTRA_COMMENTS); 							
		if(comments == null){ comments = arguments.getParcelableArrayList(Gegevens.EXTRA_COMMENTS); } 	

		// We just created a fragment. So reset the list on restore
		mLoadComments = true;

	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the ServiceDetailView view. ");

		// Inflate the root view and save references to useful views as class variables
		View rootView = inflater.inflate(R.layout.frag_services_details_view, container, false);
		mListHolder = (LinearLayout) rootView.findViewById(R.id.list_holder);
		mProgressContainer = (LinearLayout) rootView.findViewById(R.id.progress_container);
		mServiceList = (ListView) rootView.findViewById(android.R.id.list);
		mServiceList.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.list_header_service_details, null));
		loadHeaderContent(rootView);
		
		return rootView;
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring ServiceDetailView instance state.");

		// load the services list if requested (generally only if we just created the fragment)
		if(mLoadComments){
			if(comments == null){
				// TODO: implement the comments DAO (not sure where this is coming from, so I did it with services)
				showList(false);
				MyServicesDAO myServicesDAO = DaoFactory.getInstance().setMyServicesDAO(getActivity(), this, servicesType);
				myServicesDAO.readServices(MyServicesDAO.Type.APPLY, DaoFactory.Source.DUMMY, new String [] {});
				Loggen.v(this, "Restoring saved Instancestate: Hide the list");
			}else{
				// If we already have a list of comments, just show those comments
				initListView();
			}
		}
	}
	
	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving ServiceDetailView instance state.");
		// save the list of comments to the instance state
		outState.putParcelableArrayList(Gegevens.EXTRA_COMMENTS, comments);
		outState.putParcelable(Gegevens.EXTRA_SERVICE, mService);
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

	private void showList(boolean showlist) {
		mListHolder.setVisibility((showlist) ? View.VISIBLE : View.GONE);
		mProgressContainer.setVisibility( (!showlist) ? View.VISIBLE : View.GONE);
	}

	private void initListView() {
		Loggen.v(this, getTag() + " - initlistview: Create and set a list adapter for the listview.");

		// load a new adapter
		ServicesArrayAdapter a = new ServicesArrayAdapter(getActivity(), 
				R.layout.list_item_services, android.R.id.text1, comments);
		
		
		// set the adapter
		setListAdapter(a);

		// set the choice mode and reaction to the choices 
		mServiceList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		showList(true);
		mLoadComments = false;
	}

	private void loadHeaderContent(View rootView) {
		
		//Set the image
		File imgFile = new File(mService.getServicephoto());
		ImageView serviceImg = (ImageView) rootView.findViewById(R.id.details_service_img);
		if(imgFile.exists()){
			serviceImg.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
		}
		
		// Set the text
		((TextView) rootView.findViewById(R.id.details_service_title)).setText(mService.getServicetitle());
		((TextView) rootView.findViewById(R.id.details_service_description)).setText(mService.getServicedetail());

	}

	@Override
	public void onReadServices(List<TrustService> services) {
		Loggen.i(this, getTag() + " - Returned from onReadservices. ");

		
		// update the services
		this.comments = (ArrayList<TrustService>) ((services != null) ? services : new ArrayList <TrustService> ());
		
		// update the UI
		initListView();
		
	}


	public interface OnActionSelectedListener{
		public void onActionSelected(String tag, TrustService service);
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

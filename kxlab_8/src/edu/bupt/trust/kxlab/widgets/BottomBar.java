package edu.bupt.trust.kxlab.widgets;

import edu.bupt.trust.kxlab.utils.Loggen;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

/**
 * 将状态条单独封装起来
 * 
 * 
 * 这里与布局文件相结合。通过inflater布局文件，来得到每个Item。
 * 
 * @author DemonTF
 * @since 2013-10-5
 * */

public class BottomBar extends LinearLayout implements OnClickListener {

	private OnClickListener btnClickedListener;
	private View lastClicked;
	private Context mContext;
	
	public BottomBar(Context context) {
		super(context);
		mContext = context;
	}
	
	//构造方法1 使用AttributeSet 自定义控件
	public BottomBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override public void onClick(View v) {
		this.onFinishInflate();
		Loggen.i(this, "Clicked: " + v.getId());
		if(lastClicked != null){ lastClicked.setActivated(false); }
		v.setActivated(true);
		lastClicked = v;
		if(btnClickedListener != null) { btnClickedListener.onClick(v); } 
	}
	
	@Override protected void onFinishInflate () {
		super.onFinishInflate();
		// Set the onclick listener if available
		if(mContext != null && mContext instanceof OnClickListener){
			setOnClickListener((OnClickListener) mContext);			
		}
		mContext = null;
	}
	
	private void setListeners(){
		// Set the listeners to this
		int children = this.getChildCount();
		for(int i = 0; i < children; i++){
			if(getChildAt(i) instanceof Button || getChildAt(i) instanceof ImageButton){
				getChildAt(i).setOnClickListener(this);
			}
		}
	}
	
	@Override public void setOnClickListener(OnClickListener l) {
		btnClickedListener = l;
		setListeners();
	}
	
	public void setActivatedView(int viewResourceId) {
		int children = this.getChildCount();
		for(int i = 0; i < children; i++){
			if(getChildAt(i).getId() == viewResourceId){
				lastClicked = getChildAt(i);
				lastClicked.setActivated(true);
			} else {
				getChildAt(i).setActivated(false);
			}
		}
	}
}

package pku.gsy.app.view.adapter;

import java.util.ArrayList;
import java.util.List;

import pku.gsy.app.view.wrapper.BaseWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class CalBaseAdapter<T extends BaseWrapper> extends BaseAdapter {
	
	private LayoutInflater mInflater = null;
	private ViewGroup mParent = null;
	private int mLayoutId = 0;
	private List<T> mWrappers = null;

	public CalBaseAdapter(ViewGroup parent, int layoutId, int capacity) {
		super();
		mWrappers = new ArrayList<T>(capacity);
		mInflater = LayoutInflater.from(parent.getContext());
		mParent = parent;
		mLayoutId = layoutId;
	}
	
	public final List<T> getWrappers() {
		return mWrappers;
	}
	
	public final void addToWrappers(T wrapper) {
		mWrappers.add(wrapper);
	}
	
	public final T getWrapper(int pos) {
		return mWrappers.get(pos);
	}
	
//	protected View newOneView(int layoutId) {
//		return mInflater.inflate(layoutId, mParent, false);
//	}
	
	protected final View newOneView(boolean attachToRoot) {
		return attachToRoot ? mInflater.inflate(mLayoutId, mParent, false) : mInflater.inflate(mLayoutId, null);
	}
	
	@Override
	public int getCount() {
		return mWrappers.size();
	}

	@Override
	public Object getItem(int pos) {
		return mWrappers.get(pos);
	}

	@Override
	public long getItemId(int pos) {
		return pos;
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		return mWrappers.get(pos).mBase;
	}

}

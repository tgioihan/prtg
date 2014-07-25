package com.bestfunforever.thichngamgaixinhvn;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MenuFragment extends Fragment{
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.menu_fragment, null);
		ListView mListView = (ListView) view.findViewById(R.id.menulist);
		String[] menuStrings = getResources().getStringArray(R.array.pages);
		ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, menuStrings);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				((MainActivity)getActivity()).setPositionPage(arg2);
				((MainActivity)getActivity()).toogleDrawer();
			}
		});
		return view;
	}

}

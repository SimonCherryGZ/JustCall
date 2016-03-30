package com.simoncherry.justcall.Adapter;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.simoncherry.justcall.Bean.ContactBean;
import com.simoncherry.justcall.Custom.QuickAlphabeticBar;
import com.simoncherry.justcall.R;

public class ContactListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<ContactBean> list;
	private HashMap<String, Integer> alphaIndexer;
	private String[] sections;
	private Context ctx;
	private int heartPos = -1;
	
	//add by simon 2016.02.16
	HashMap<String,Boolean> heart_states = new HashMap<String,Boolean>();
	private boolean isInitHeart = false;

	public ContactListAdapter(Context context, List<ContactBean> list,
			QuickAlphabeticBar alpha) {
		this.ctx = context;
		this.inflater = LayoutInflater.from(context);
		this.list = list;
		this.alphaIndexer = new HashMap<String, Integer>();
		this.sections = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			String name = getAlpha(list.get(i).getSortKey());
			if (!alphaIndexer.containsKey(name)) {
				alphaIndexer.put(name, i);
			}
		}

		Set<String> sectionLetters = alphaIndexer.keySet();
		ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
		Collections.sort(sectionList);
		sections = new String[sectionList.size()];
		sectionList.toArray(sections);

		alpha.setAlphaIndexer(alphaIndexer);

	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void remove(int position) {
		list.remove(position);
	}

	public int getHeartPos(){
		return heartPos;
	}
	
	public void setHeartPos(int pos){
		heartPos = pos;
	}
	

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.contact_list_item, null);
			holder = new ViewHolder();
			holder.quickContactBadge = (QuickContactBadge) convertView
					.findViewById(R.id.qcb);
			holder.alpha = (TextView) convertView.findViewById(R.id.alpha);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.number = (TextView) convertView.findViewById(R.id.number);
			holder.heart = (ImageView) convertView.findViewById(R.id.heart);
			
			convertView.setTag(holder);
			
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final ContactBean contact = list.get(position);
		String name = contact.getDisplayName();
		String number = contact.getPhoneNum();
		holder.name.setText(name);
		holder.number.setText(number);
		holder.quickContactBadge.assignContactUri(Contacts.getLookupUri(
				contact.getContactId(), contact.getLookUpKey()));
		if (0 == contact.getPhotoId()) {
			holder.quickContactBadge.setImageResource(R.drawable.touxiang);
		} else {
			Uri uri = ContentUris.withAppendedId(
					ContactsContract.Contacts.CONTENT_URI,
					contact.getContactId());
			InputStream input = ContactsContract.Contacts
					.openContactPhotoInputStream(ctx.getContentResolver(), uri);
			Bitmap contactPhoto = BitmapFactory.decodeStream(input);
			holder.quickContactBadge.setImageBitmap(contactPhoto);
		}

		String currentStr = getAlpha(contact.getSortKey());

		String previewStr = (position - 1) >= 0 ? getAlpha(list.get(
				position - 1).getSortKey()) : " ";

		if (!previewStr.equals(currentStr)) {
			holder.alpha.setVisibility(View.VISIBLE);
			holder.alpha.setText(currentStr);
		} else {
			holder.alpha.setVisibility(View.GONE);
		}
		
		if(isInitHeart == false){
			if(contact.getHeart() == true){
				heart_states.put(String.valueOf(position), true);
				isInitHeart = true;
			}else{
				heart_states.put(String.valueOf(position), false);
			}
		}
		
		//*
		holder.heart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				/*
				if(heartState == false){
					if(contact.getHeart() == false){
						holder.heart.setImageResource(R.drawable.icon_heart_active);
						contact.setHeart(true);
						heartPos = position;
						heartState = true;
						Log.v("clickPos", String.valueOf(heartPos));
					}
				}else{
					if(contact.getHeart() == true){
						holder.heart.setImageResource(R.drawable.icon_heart_default);
						contact.setHeart(false);
						heartPos = -1;
						heartState = false;
					}
					//add by simon 2016.02.15
					else{
						holder.heart.setImageResource(R.drawable.icon_heart_active);
						heartPos = position;
					}
				}
				*/
				
				//add by simon 2016.02.16
				for(String key:heart_states.keySet()){  
						heart_states.put(key, false);    
	            } 
				heart_states.put(String.valueOf(position), true);
				heartPos = position;
				
				Log.v("clickPos", String.valueOf(heartPos));
				ContactListAdapter.this.notifyDataSetChanged();
			}	
		});
		//*/
		
		if(heart_states.get(String.valueOf(position)) == null || 
				heart_states.get(String.valueOf(position)) == false){
			
			heart_states.put(String.valueOf(position), false);
			holder.heart.setImageResource(R.drawable.icon_heart_default);	
		}else{
			holder.heart.setImageResource(R.drawable.icon_heart_active);
		}
		
		return convertView;
	}

	private static class ViewHolder {
		QuickContactBadge quickContactBadge;
		TextView alpha;
		TextView name;
		TextView number;
		ImageView heart;
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		if (str == null) {
			return "#";
		}
		if (str.trim().length() == 0) {
			return "#";
		}
		char c = str.trim().substring(0, 1).charAt(0);

		Pattern pattern = Pattern.compile("^[A-Za-z]+$");
		if (pattern.matcher(c + "").matches()) {
			return (c + "").toUpperCase();
		} else {
			return "#";
		}
	}
}

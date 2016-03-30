package com.simoncherry.justcall.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simoncherry.justcall.Adapter.ContactListAdapter;
import com.simoncherry.justcall.Bean.ContactBean;
import com.simoncherry.justcall.Custom.QuickAlphabeticBar;
import com.simoncherry.justcall.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 
 * @author Administrator
 * 
 */
public class ContactListActivity extends Activity {

	private ContactListAdapter adapter;
	private ListView contactList;
	private List<ContactBean> list;
	private AsyncQueryHandler asyncQueryHandler;
	private QuickAlphabeticBar alphabeticBar;

	private Map<Integer, ContactBean> contactIdMap = null;
	private Map<String, ContactBean> contactNumberMap = null;
	
	private int heartPos = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contact_list_view);
		
		contactList = (ListView) findViewById(R.id.contact_list);
		alphabeticBar = (QuickAlphabeticBar) findViewById(R.id.fast_scroller);

		asyncQueryHandler = new MyAsyncQueryHandler(getContentResolver());
		init();
		setTitle(R.string.contact_title);
		//setTitle(getResources().getString(R.string.contact_title));
	}
	
	@Override
	protected void onPause(){
		super.onPause();

		SharedPreferences preference = getSharedPreferences(
				"justcall", Context.MODE_PRIVATE);
		
		Editor edit = preference.edit();
		/*
		edit.remove("heart_pos");
		edit.remove("name");
		edit.remove("number");
		edit.commit();
		*/
		
		heartPos = adapter.getHeartPos();
		Log.v("savePos", String.valueOf(heartPos));
		//*
		edit.putInt("heart_pos", heartPos);
		edit.putString("name", list.get(heartPos).getDisplayName());
		edit.putString("number", list.get(heartPos).getPhoneNum());
		edit.commit();
		
		Log.v("savePos", String.valueOf(preference.getInt("heart_pos", -1)));
		Log.v("saveName", preference.getString("name", "China Mobile"));
		Log.v("saveNumber", preference.getString("number", "10086"));
		//*/
	}
	/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event){
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
			startActivity(new Intent(ContactListActivity.this, MainActivity.class));
			ContactListActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	*/
	private void init() {
		SharedPreferences preference = getSharedPreferences("justcall",
				Context.MODE_PRIVATE);
		heartPos = preference.getInt("heart_pos", 0);
		Log.v("loadPos", String.valueOf(heartPos));
		
		Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

		String[] projection = { 
					ContactsContract.CommonDataKinds.Phone._ID,
					ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
					ContactsContract.CommonDataKinds.Phone.DATA1, "sort_key",
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
					ContactsContract.CommonDataKinds.Phone.PHOTO_ID,
					ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY, 
				};

		asyncQueryHandler.startQuery(0, null, uri, projection, null, null,
				"sort_key COLLATE LOCALIZED asc");

	}

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyAsyncQueryHandler extends AsyncQueryHandler {

		public MyAsyncQueryHandler(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			if (cursor != null && cursor.getCount() > 0) {
				contactIdMap = new HashMap<Integer, ContactBean>();
				contactNumberMap = new HashMap<String, ContactBean>();
				list = new ArrayList<ContactBean>();
				cursor.moveToFirst();
				
				//Log.v("get_count", String.valueOf(cursor.getCount()));
				
				for (int i = 0; i < cursor.getCount(); i++) {
					cursor.moveToPosition(i);
					String name = cursor.getString(1);
					String number = cursor.getString(2);
					String sortKey = cursor.getString(3);
					int contactId = cursor.getInt(4);
					Long photoId = cursor.getLong(5);
					String lookUpKey = cursor.getString(6);
					
					//if (contactIdMap.containsKey(contactId) == false) {
					if (contactNumberMap.containsKey(number) == false) {
						
						ContactBean contact = new ContactBean();
						contact.setDesplayName(name);
						contact.setPhoneNum(number);
						contact.setSortKey(sortKey);
						contact.setContactId(contactId);  //fix by simon
						contact.setPhotoId(photoId);
						contact.setLookUpKey(lookUpKey);
//						if(i == heartPos){
//							contact.setHeart(true);
//							Log.v("pos_match", String.valueOf(i));
//							SharedPreferences preference = getSharedPreferences(
//									"justcall", Context.MODE_PRIVATE);
//							Editor edit = preference.edit();
//							edit.putString("name", name);
//							edit.putString("number", number);
//							edit.commit();
//							saveName = name;
//							saveNumber =number;
//							
//						}else{
//							contact.setHeart(false);
//						}
						list.add(contact);
						//contactIdMap.put(contactId, contact);
						contactNumberMap.put(number, contact);
					}
						
				}
				if (list.size() > 0) {
					// TODO
					for(int i=0; i<list.size(); i++){
						if(i == heartPos){
							list.get(i).setHeart(true);
							SharedPreferences preference = getSharedPreferences(
									"justcall", Context.MODE_MULTI_PROCESS);
							Editor edit = preference.edit();
							edit.putString("name", list.get(i).getDisplayName());
							edit.putString("number", list.get(i).getPhoneNum());
							edit.commit();
							
						}else{
							list.get(i).setHeart(false);
						}						
					}
					
					setAdapter(list);
				}
			}

			super.onQueryComplete(token, cookie, cursor);
		}

	}

	private void setAdapter(List<ContactBean> list) {
		adapter = new ContactListAdapter(this, list, alphabeticBar);
		
		adapter.setHeartPos(heartPos);

		contactList.setAdapter(adapter);
		alphabeticBar.init(ContactListActivity.this);
		alphabeticBar.setListView(contactList);
		alphabeticBar.setHight(alphabeticBar.getHeight());
		alphabeticBar.setVisibility(View.VISIBLE);
		
		contactList.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ContactBean cb = (ContactBean) adapter.getItem(position);
				showContactDialog(OptionStr, cb, position);
			}
		});
		
	}
	
	private String[] OptionStr = new String[] { 
			"想打给TA", 
			"查看详情",
			"删掉TA" };
	
		private void showContactDialog(final String[] arg ,final ContactBean cb, final int position){
			new AlertDialog.Builder(this).setTitle(cb.getDisplayName()).setItems(
					arg, new DialogInterface.OnClickListener(){
				public void onClick(DialogInterface dialog, int which){

					Uri uri = null;

					switch(which){

					case 0:
						
						Bundle mybundle = new Bundle();
						String contact_name = cb.getDisplayName();
						mybundle.putString("name", contact_name);
						String contact_number = cb.getPhoneNum();
						mybundle.putString("number", contact_number);
						
						Intent intent1 = new Intent();
						intent1.setClass(ContactListActivity.this, TimingDialActivity.class);
						intent1.putExtras(mybundle);
						startActivity(intent1);
						
						break;

					case 1:

						uri = ContactsContract.Contacts.CONTENT_URI;
						Uri personUri = ContentUris.withAppendedId(uri, cb.getContactId());
						Intent intent2 = new Intent();
						intent2.setAction(Intent.ACTION_VIEW);
						intent2.setData(personUri);
						startActivity(intent2);
						break;

					case 2:
						showDelete(cb.getContactId(), position);
						break;
					}
				}
			}).show();
		}
		
		private void showDelete(final int contactsID, final int position) {
			new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle(R.string.contact_delete_title)
			.setPositiveButton(R.string.contact_delete_positive, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

					Uri deleteUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactsID);
					Uri lookupUri = ContactsContract.Contacts.getLookupUri(ContactListActivity.this.getContentResolver(), deleteUri);
					if(lookupUri != Uri.EMPTY){
						ContactListActivity.this.getContentResolver().delete(deleteUri, null, null);
					}
					adapter.remove(position);
					adapter.notifyDataSetChanged();
					Toast.makeText(ContactListActivity.this, R.string.contact_delete_toast, Toast.LENGTH_SHORT).show();
				}
			})
			.setNegativeButton(R.string.contact_delete_negative, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {

				}
			}).show();
		}
}

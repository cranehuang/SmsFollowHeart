package followheart.entities;

import java.io.InputStream;
import java.io.Serializable;



import followheart.activities.R;
import followheart.utils.PinyinUtils;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long contactID;
	private String contactName;
	private Long photoID;
	private String phoneNumber;
	private boolean hasBeSelected;
	private transient Bitmap contactPhoto = null;
	private transient Context context;
	
	public ContactInfo(Context context, String contactName,String phoneNumber ,Long photoID,Long contactID)
	{
		this.context = context;
		this.contactName = contactName;
		this.phoneNumber = phoneNumber;
		this.photoID = photoID;
		this.contactID = contactID;
		this.hasBeSelected = false;
	}
	
//	public void setContactName(String contactName)
//	{
//		this.contactName = contactName;
//	}
	public String getContactName()
	{
		return this.contactName;
	}
//	public void setPhoneNumber(String phoneNumber)
//	{
//		this.phoneNumber = phoneNumber;
//	}
	public String getPhoneNumber()
	{
		return this.phoneNumber;
	}
	public Long getContactID()
	{
		return this.contactID;
	}
	

	public Bitmap getContactPhoto()
	{
		ContentResolver resolver = context.getContentResolver();
		//photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
		if(this.photoID > 0 ) {
		    Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactID);
		    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
		    contactPhoto = BitmapFactory.decodeStream(input);
		}else {
		    contactPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_contact_photo);
		}
		return contactPhoto;
	}

	public String getPY()
	{
		return PinyinUtils.getPingYin(this.contactName);
	}
	
	public boolean hasBeSelected()
	{
		return this.hasBeSelected;
	}
	
	public void setHasBeSelected(boolean hasBeSelected)
	{
		this.hasBeSelected = hasBeSelected;
	}
	
}

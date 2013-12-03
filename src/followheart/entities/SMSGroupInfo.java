package followheart.entities;

import java.io.InputStream;
import java.util.ArrayList;

import followheart.activities.R;



import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;


public class SMSGroupInfo {
	private String groupId;
	private String date;
	private String body;
	private String msg_count;
	private String read;
	private String type;
	private ArrayList<String> contactNames;
	private ArrayList<String> contactNumbers;
	private Long photoID;
	private Context context;
	private Long contactID;
	private Bitmap contactPhoto;
	
	public SMSGroupInfo(Context context, String groupId , String date ,String body, String msg_count ,String read ,String type )
	{
		this.contactNames = new ArrayList<String>();
		this.contactNumbers = new ArrayList<String>();
		this.groupId = groupId;
		this.date = date;
		this.body = body;
		this.msg_count = msg_count;
		this.read = read;
		this.type = type;
		this.context = context;
		this.photoID = (long)0;
	}
	
	public String getGroupId()
	{
		return this.groupId;
	}
	public String getDate()
	{
		return this.date;
	}

	public String getBody()
	{
		return this.body;
	}
	public int getRead()
	{
		return Integer.parseInt(this.read) ;
	}
	public String getMsg_count()
	{
		return this.msg_count;
	}
	public int getType()
	{
		return Integer.parseInt(this.type);
	}
	public ArrayList<String> getContactNames()
	{
		return this.contactNames;
	}
	
	public void setContactNames(ArrayList<String> contactNames)
	{
		this.contactNames = contactNames;
	}
	
	public ArrayList<String> getContactNumbers()
	{
		return this.contactNumbers;
	}
	
	public void setContactID(Long contactID)
	{
		this.contactID = contactID;
	}
	
	public void setPhotoId(Long photoID)
	{
		this.photoID = photoID;
	}
	
	public Bitmap getContactPhoto()
	{
		ContentResolver resolver = context.getContentResolver();
		//photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
		System.out.println("------------>photoID = " + photoID);
		if(this.photoID > 0 ) {
		    Uri uri =ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,contactID);
		    InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(resolver, uri);
		    contactPhoto = BitmapFactory.decodeStream(input);
		}else {
		    contactPhoto = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		}
		return contactPhoto;
	}
}

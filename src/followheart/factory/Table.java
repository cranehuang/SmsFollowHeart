package followheart.factory;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

public class Table {
	private String tableName, tableNum, createTableTime;
	private String[] name, phoneNumber, isAttended, note;
	private String pathString = "/sdcard/data/data/mesapp/";
	private List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();

	public Table(String tableName, String tableNum, String createTableTime,
			String[] name, String[] phoneNumber, String[] isAttended, String[] note) throws IOException {
		this.tableName = tableName;
		this.tableNum = tableNum;
		this.createTableTime = createTableTime;
		this.name = name;
		
		this.phoneNumber = phoneNumber;
		this.isAttended = isAttended;
		this.note = note;
		for(int i = 0;i<name.length;i++){
			Log.d("A new table!!!!!",name[i]);
		}
		
		writeIn(tableName, tableNum, createTableTime);
	}

	public Table(String tableName, String tableNum, String createTableTime) throws IOException {
		this.tableName = tableName;
		this.tableNum = tableNum;
		this.createTableTime = createTableTime;
		setList(initContent(readOut()));
	}
	//���������Ĳ���д�뵽�ı��м�¼
	public void writeIn(String tableName, String tableNum,
			String createTableTime) throws IOException {
		Log.d("wwwwwwwwww","writeIn");
		FileWriter fw;
		BufferedWriter bw;
		File file = new File(pathString);
		if (!file.exists()) {
			file.mkdirs();
		}
		File dirFile = new File(pathString + tableName);
		if (dirFile.exists()) {
			
				dirFile.delete();
			}
		dirFile.createNewFile();
				// ��tableName����Ϊ�ļ���
				fw = new FileWriter(pathString + tableName, true);
				//fw.write("");
				bw = new BufferedWriter(fw);
			
				bw.write(tableName + "*" + createTableTime);
				if(name!=null){
				for (int i = 0; i < name.length; i++) {
					bw.write("*" + name[i] + "!" + phoneNumber[i] + "!"
							+ isAttended[i] + "!" + note[i]);
					
				}
				}else {
					bw.write("");
				}
				bw.flush(); // ˢ�¸����Ļ���  
	            bw.close();  
	            fw.close(); 
		

	}

	public String readOut() throws IOException {
		File file = new File(pathString + tableName);
		FileInputStream fin = null;
		ByteArrayOutputStream baArrayOutputStream = null;
		//Log.d("read!!!","!!!!");
		
		if(!file.exists()){
			file.createNewFile();
		}
		String content = "";
		try {
			fin = new FileInputStream(file);
			int lenth = fin.available();

			byte b[] = new byte[1024];
			baArrayOutputStream = new ByteArrayOutputStream();
			while (fin.read(b) != -1) {
				baArrayOutputStream.write(b, 0, lenth);
			}
			
			baArrayOutputStream.close();
			content = new String(baArrayOutputStream.toByteArray());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(fin!=null)
				baArrayOutputStream.close();
				fin.close();
		}
		//file.delete();
		return content;
	}

	// ����ı��е�*�ͣ������ֶν���
	// list�д�ŵ�map�а���id,phoneNumber,isAttended,note
	@SuppressLint("NewApi")
	public List<Map<String, Object>> initContent(String content) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		HashMap<String, Object> hashMap;
		Log.d("init!!!", "==========");
		if(content.isEmpty()){
			return list;
		}else {
			content.replaceAll(" ","");
		String a[] = content.split("\\*");
		
		if (a.length >= 3) {
			Log.d("����init",a[2]);
			for (int i = 2; i < a.length; i++) {
				Log.d("�ٴ�init","========");
				String b[] = a[i].split("!");
				//Log.d("aaa",a[i]);
				Log.d("aaa",String.valueOf(b.length));
				for (int j = 0; j < b.length; j++) { 
					System.out.println("----------> b[" + j + "] = " + b[j]);
				}
				
				hashMap = new HashMap<String, Object>();
				hashMap.put("name", b[0]);
				hashMap.put("phoneNumber", b[1]);
				hashMap.put("isAttended", b[2]);
				hashMap.put("note", b[3]);
				list.add(hashMap);
			
			}
		}
		return list;
		}
		//return list;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		Log.d("set!!!!","setle=============");
		this.list = list;
	}

}

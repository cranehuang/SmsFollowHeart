package followheart.utils;

import java.text.Collator;
import java.util.Comparator;

import followheart.entities.ContactInfo;






//通讯社按中文拼音排序
public class MyComparator implements Comparator<ContactInfo>{ 
     public int compare(ContactInfo o1,ContactInfo o2) { 
         ContactInfo c1=(ContactInfo)o1; 
         ContactInfo c2=(ContactInfo)o2; 
         String str1 = PinyinUtils.getPingYin(c1.getContactName());
         String str2 = PinyinUtils.getPingYin(c2.getContactName());
         Comparator<Object> cmp = Collator.getInstance(java.util.Locale.CHINA);
         return cmp.compare(str1, str2); 
     } 

}


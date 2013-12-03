package followheart.entities;

public class GroupInfo {
	
	private String groupTitle;
	private String groupId;
	
	public GroupInfo()
	{
		
	}
	public GroupInfo(String groupTitle , String groupId)
	{
		this.groupTitle = groupTitle;
		this.groupId = groupId;
	}

	public void setTitle(String groupTitle)
	{
		this.groupTitle = groupTitle;
	}
	public String getTitle()
	{
		return this.groupTitle;
	}
	
	public void setId(String groupId)
	{
		this.groupId =groupId;
	}
	public String getId()
	{
		return this.groupId;
	}
}

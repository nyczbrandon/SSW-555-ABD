package gedcom;

import java.util.Date;
import java.util.List;

public class Family {
	private String id;
	private Date married;
	private Date divorced;
	private String husbandId;
	private String husbandName;
	private String wifeId;
	private String wifeName;
	private List<String> children;

	public Family() {
	
	}
	
	public Family( String id, Date married, Date divorced, String husbandId, String husbandName, String wifeId, String wifeName, List<String> children) {
		this.id = id;
		this.married = married;
		this.divorced = divorced;
		this.husbandId = husbandId;
		this.husbandName = husbandName;
		this.wifeId = wifeId;
		this.wifeName = wifeName;
		this.children = children;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getMarried() {
		return married;
	}

	public void setMarried(Date married) {
		this.married = married;
	}

	public Date getDivorced() {
		return divorced;
	}

	public void setDivorced(Date divorced) {
		this.divorced = divorced;
	}

	public String getHusbandId() {
		return husbandId;
	}

	public void setHusbandId(String husbandId) {
		this.husbandId = husbandId;
	}

	public String getHusbandName() {
		return husbandName;
	}

	public void setHusbandName(String husbandName) {
		this.husbandName = husbandName;
	}

	public String getWifeId() {
		return wifeId;
	}

	public void setWifeId(String wifeId) {
		this.wifeId = wifeId;
	}

	public String getWifeName() {
		return wifeName;
	}

	public void setWifeName(String wifeName) {
		this.wifeName = wifeName;
	}

	public List<String> getChildren() {
		return children;
	}

	public void setChildren(List<String> children) {
		this.children = children;
	}
}

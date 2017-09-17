package gedcom;

import java.util.Date;
import java.util.List;

public class Individual {
	private String id;
	private String name;
	private String gender;
	private Date birthday;
	private int age;
	private boolean alive;
	private Date death;
	private List<String> children;
	private List<String> spouses;
	
	public Individual() {
		
	}
	
	public Individual( String id, String name, String gender, Date birthday, int age, boolean alive, Date death, List<String> children, List<String> spouses) {
		this.id = id;
		this.name = name;
		this.gender = gender;
		this.birthday = birthday;
		this.age = age;
		this.alive = alive;
		this.death = death;
		this.children = children;
		this.spouses = spouses;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	public Date getDeath() {
		return death;
	}

	public void setDeath(Date death) {
		this.death = death;
	}

	public List<String> getChildren() {
		return children;
	}

	public void setChildren(List<String> children) {
		this.children = children;
	}

	public List<String> getSpouses() {
		return spouses;
	}

	public void setSpouses(List<String> spouses) {
		this.spouses = spouses;
	}
}

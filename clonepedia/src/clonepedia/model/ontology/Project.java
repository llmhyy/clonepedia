package clonepedia.model.ontology;

import java.io.Serializable;

public class Project implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4598279253698163747L;
	private String projectName;
	private String projectFilePath;
	private String programmingLanguage;
	private CloneSets sets;

	public Project(String projectName, String programmingLanguage, String projectFilePath) {
		super();
		this.projectName = projectName;
		this.projectFilePath = projectFilePath;
		this.programmingLanguage = programmingLanguage;
	}
	
	public Project(String projectName, String projectFilePath,
			String programmingLanguage, CloneSets sets) {
		super();
		this.projectName = projectName;
		this.projectFilePath = projectFilePath;
		this.programmingLanguage = programmingLanguage;
		this.setSets(sets);
	}

	public boolean equals(Object obj){
		if(obj instanceof Project){
			Project prj = (Project)obj;
			if(prj.getProjectName().equals(this.projectName) && 
					prj.getProgrammingLanguage().equals(this.programmingLanguage))
				return true;
			else 
				return false;
		}
		else
			return false;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProgrammingLanguage() {
		return programmingLanguage;
	}

	public void setProgrammingLanguage(String programmingLanguage) {
		this.programmingLanguage = programmingLanguage;
	}

	public String getProjectFilePath() {
		return projectFilePath;
	}

	public void setProjectFilePath(String projectFilePath) {
		this.projectFilePath = projectFilePath;
	}

	public CloneSets getSets() {
		return sets;
	}

	public void setSets(CloneSets sets) {
		this.sets = sets;
	}

}

package clonepedia.model.semantic;

public class Word {
	private String name;
	public int count = 0;
	
	public Word(String name) {
		super();
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int hashCode(){
		return name.hashCode();
	}
	
	public String toString(){
		return name;
	}
	
	public boolean equals(Object obj){
		return (name.equals(obj.toString()));
	}
}

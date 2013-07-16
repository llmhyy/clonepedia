package clonepedia.model.viewer;

import java.util.ArrayList;
import java.util.List;

import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.wizard.SkeletonGenerationWizard;

/**
 * 
 * @author linyun
 *
 */
public class PathPatternGroupWrapper extends PatternGroupWrapper{
	
	public class PotentialLocation{
		
		public String packageName;
		
		public String className;
		public String superClassName;
		public List<String> interfaceNames = new ArrayList<String>();
		
		public String methodName;
		public String methodReturnTypeName;
		public List<String> methodParameterNames = new ArrayList<String>();
		
		public PotentialLocation(String packageName, String className, String superClassName,
				List<String> interfaceNames, String methodName,
				String methodReturnTypeName, List<String> methodParameterNames) {
			super();
			this.packageName = packageName;
			this.className = className;
			this.superClassName = superClassName;
			this.interfaceNames = interfaceNames;
			this.methodName = methodName;
			this.methodReturnTypeName = methodReturnTypeName;
			this.methodParameterNames = methodParameterNames;
		}
	}
	
	private PathPatternGroup pathPattern;
	private SkeletonGenerationWizard wizard;
	private PotentialLocation location;
	
	public PathPatternGroupWrapper(PathPatternGroup ppg){
		this.setPathPattern(ppg);
		this.pathSequence = ppg.getAbstractPathSequence();
	}

	public PathPatternGroup getPathPattern() {
		return pathPattern;
	}

	public void setPathPattern(PathPatternGroup pathPattern) {
		this.pathPattern = pathPattern;
	}

	public SkeletonGenerationWizard getWizard() {
		return wizard;
	}

	public void setWizard(SkeletonGenerationWizard wizard) {
		this.wizard = wizard;
	}

	public PotentialLocation getLocation() {
		return location;
	}

	public void setLocation(PotentialLocation location) {
		this.location = location;
	}
}

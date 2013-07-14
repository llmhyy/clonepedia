package clonepedia.model.viewer;

import clonepedia.model.syntactic.PathPatternGroup;
import clonepedia.wizard.SkeletonGenerationWizard;

public class PathPatternGroupWrapper extends PatternGroupWrapper{
	
	private PathPatternGroup pathPattern;
	private SkeletonGenerationWizard wizard;
	
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
}

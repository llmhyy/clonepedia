package clonepedia.model.viewer;

import clonepedia.model.syntactic.PathPatternGroup;

public class PathPatternGroupWrapper extends PatternGroupWrapper{
	
	private PathPatternGroup pathPattern;
	
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
}

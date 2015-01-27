package ccdemon.evaluation.handler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

import ccdemon.evaluation.model.DataRecord;
import ccdemon.util.CCDemonUtil;

public class RecordBehaviorHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IProject project = CCDemonUtil.retrieveWorkingJavaProject().getProject();
		IFile file = project.getFile("/record_data" + (++DataRecord.recordTime));
		String content = "Behavior Record: \n" + 
				"Next Time: " + DataRecord.toNextTime + "\n" +
				"Prev Time: " + DataRecord.toPrevTime + "\n" +
				"ManualEdit Time: " + DataRecord.manualEditTime + "\n";
		content += "Each Configuration Time: " + "\n";
		for(Long interval : DataRecord.focusIntervals){
			content += interval + "ms,";
		}
		
		InputStream source = new ByteArrayInputStream(content.getBytes());
		try {
			file.create(source, false, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		DataRecord.clear();
		return null;
	}

}

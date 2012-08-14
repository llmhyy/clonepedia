package clonepedia.actions;

import java.io.File;
import java.io.IOException;

import jxl.write.Number;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import clonepedia.Activator;
import clonepedia.model.viewer.ClonePatternGroupWrapper;
import clonepedia.model.viewer.ClonePatternGroupWrapperList;
import clonepedia.model.viewer.TopicWrapper;
import clonepedia.model.viewer.TopicWrapperList;
import clonepedia.summary.SummaryUtil;

public class ExportOverallDetailAction implements
		IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
		dialog.setText("Save");
		dialog.setFilterPath("C:/");
		dialog.setFilterExtensions(new String[]{"*.xls", "*.*"});
        String path = dialog.open();
        if(path != null){
        	try {
				createExcelFile(path);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

	}
	
	private void createExcelFile(String path) throws IOException, RowsExceededException, WriteException{
		File file = new File(path);
		WritableWorkbook workbook = Workbook.createWorkbook(file);
		WritableSheet sheet = workbook.createSheet("Report", 0);
		writeInfo(sheet);
		workbook.write();
		workbook.close();
	}
	
	private void writeInfo(WritableSheet sheet) throws RowsExceededException, WriteException{
		Label synTitleLabel = new Label(0, 0, "Syntactic Pattern");
		Label semTitleLabel = new Label(1, 0, "Semantic Topic");
		Label synNumLablel = new Label(2, 0, "Synatactic Contained Clone Set Number");
		Label semNumLablel = new Label(3, 0, "Semantic Contained Clone Set Number");
		Label numTitleLabel = new Label(4, 0, "Intersection Number");
		
		sheet.addCell(synTitleLabel);
		sheet.addCell(semTitleLabel);
		sheet.addCell(synNumLablel);
		sheet.addCell(semNumLablel);
		sheet.addCell(numTitleLabel);
		
		ClonePatternGroupWrapperList clonePatterns = SummaryUtil.generateClonePatternOrientedComplexTree(Activator.sets.getCloneList());
		//ClonePatternGroupWrapper[] clonePatternList = clonePatterns.toArray(new ClonePatternGroupWrapper[0]);
		
		TopicWrapperList topics = SummaryUtil.generateTopicOrientedSimpleTree(Activator.sets.getCloneList());
		//TopicWrapper[] topicList = topics.toArray(new TopicWrapper[0]);
		int count = 1;
		
		for(ClonePatternGroupWrapper clonePattern: clonePatterns)
			for(TopicWrapper topic: topics){
				Label synLabel = new Label(0, count, clonePattern.toString());
				Label semLabel = new Label(1, count, topic.getTopic().getTopicString());
				Number synNum = new Number(2, count, Integer.valueOf(clonePattern.getAllContainedCloneSet().size()));
				Number semNum = new Number(3, count, Integer.valueOf(topic.getAllContainedCloneSet().size()));
				Number numLabel = new Number(4, count, Integer.valueOf(clonePattern.getIntersectionWith(topic).size()));
				
				sheet.addCell(synLabel);
				sheet.addCell(semLabel);
				sheet.addCell(synNum);
				sheet.addCell(semNum);
				sheet.addCell(numLabel);
				
				count++;
			}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}

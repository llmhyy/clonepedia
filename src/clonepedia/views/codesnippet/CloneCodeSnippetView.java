package clonepedia.views.codesnippet;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CounterRelationGroup;
import clonepedia.model.ontology.InstanceElementRelation;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.util.MinerUtil;
import clonepedia.views.util.ViewUtil;

public class CloneCodeSnippetView extends ViewPart {
	private FormToolkit toolkit;
	private ScrolledForm form;
	
	public CloneCodeSnippetView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		form.setText("Relavent Codes");
		TableWrapLayout tableLayout = new TableWrapLayout();
		tableLayout.numColumns = 1;
		form.getBody().setLayout(tableLayout);
		
		parent.redraw();
		//createCodeSection(form.getBody(), "test");
	}
	
	public void createCodeSections(CloneInstance instance){
		ArrayList<SnippetInstanceRelation> codes = generateCodeSnippets(instance);
		//codes.add(new SnippetInstanceRelation(instance, getCodeSnippetOfCloneInstance(instance)));
		appendCounterRelationTag(codes);
		
		clearForm(form);
		
		for(SnippetInstanceRelation code: codes)
			createCodeSection(form.getBody(), code);
		
		//form.getBody().redraw();
	}
	
	private void createCodeSection(Composite parent, SnippetInstanceRelation code) {
		Section section = toolkit.createSection(parent, Section.TWISTIE|Section.EXPANDED|Section.TITLE_BAR);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		section.setExpanded(true);
		section.setLayout(new TableWrapLayout());
		section.setText(titleFormatting(code.instance));
		
		FormText text = toolkit.createFormText(section, true);
		
		FormColors colors = toolkit.getColors();
		colors.createColor("CounterRelation", colors.getSystemColor(SWT.COLOR_RED));
		text.setColor("CounterRelation", colors.getColor("CounterRelation"));
		
		colors.createColor("Distant", colors.getSystemColor(SWT.COLOR_DARK_BLUE));
		text.setColor("Distant", colors.getColor("Distant"));
		//setFormTextColorAndFont(text);
		text.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		text.setWhitespaceNormalized(false);
		try{
			text.setText(codeFormatting(code), true, false);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		
		section.setClient(text);
	}
	
	private String titleFormatting(CloneInstance instance){
		String location = instance.getFileLocation();
		String simpleFileName = location.substring(location.lastIndexOf("\\")+1, location.lastIndexOf("."));
		String methodName = instance.getResidingMethod().toString();
		methodName = methodName.substring(methodName.indexOf(" ")+1);
		String title = simpleFileName + "." + methodName + ":" + instance.getResidingMethod().getReturnType();
		return title;
	}
	
	private String codeFormatting(SnippetInstanceRelation code){
		String codeString;
		
		String upperBoundString = "<span color=\"Distant\">" + code.upperDistant + " lines above in a method.</span><br/>";
		String lowerBoundString = "<span color=\"Distant\">" + code.lowerDistant + " lines under in a method.</span><br/>";
		
		codeString = code.snippet.replace("\n", "<br/>");
		codeString = "<form><p>" + upperBoundString + codeString + lowerBoundString + "</p></form>";
		
		
		return codeString;
	}
	
	private void appendCounterRelationTag(ArrayList<SnippetInstanceRelation> list){
		
		CloneSet set = list.get(0).instance.getCloneSet();
		for(SnippetInstanceRelation r: list){
			for(CounterRelationGroup group: set.getCounterRelationGroups())
				for(InstanceElementRelation relation: group.getRelationList())
					if(relation.getCloneInstance().getId().equals(r.instance.getId())){
						String name = relation.getElement().getSimpleElementName();
						name = MinerUtil.xmlStringProcess(name);
						r.snippet = attachTagForSingleCodeFragment(r.snippet, name);
						//System.out.println();
					}
			//System.out.println();
		}
	}
	
	private String attachTagForSingleCodeFragment(String code, String targetWord){
		ArrayList<String> list = divideCode(code);
		StringBuffer buffer = new StringBuffer();
		for(String subCode: list){
			String str = "";
			if (!subCode.contains("</span>")
					&& !subCode.contains("<span color=\"CounterRelation\">"+ targetWord + "</span>")) {
				//str = subCode.replace(targetWord, "<span color=\"CounterRelation\">" + targetWord + "</span>");
				str = highlightReleventCounterRelationCode(targetWord, subCode);
				//System.out.println();
			}
			else
				str = subCode;
			buffer.append(str);
		}
			
		return buffer.toString();
	}
	
	private String highlightReleventCounterRelationCode(String targetWord, String code){
		
		String replacingWord = "<span color=\"CounterRelation\">" + targetWord + "</span>";
		
		String returnCode = "";
		
		String[] strs = code.split("(?<![\\w])" + MinerUtil.changeWordBeforeRegularExpression(targetWord) + "(?![\\w])");
		
		if(strs.length == 1)
			return code;
		
		for(int i=0; i<strs.length-1; i++){
			returnCode += strs[i];
			returnCode += replacingWord;
		}
		returnCode += strs[strs.length-1];
		return returnCode;
	}
	
	

	private ArrayList<String> divideCode(String code){
		ArrayList<String> list = new ArrayList<String>();
		int index = 0;
		while(index < code.length()){
			int delimit = code.indexOf("<span", index);
			if(delimit == -1){
				list.add(code.substring(index, code.length()));
				return list;
			}
			else{
				list.add(code.substring(index, delimit));
				index = delimit;
				delimit = code.indexOf("</span>", index) + 7;
				//delimit = code.indexOf("</span>", delimit+1) + 7;
				list.add(code.substring(index, delimit));
				index = delimit;
			}
		}
		return list;
	}
	

	private void clearForm(ScrolledForm form){
		for(Control control: form.getBody().getChildren()){
			if(control != null)
				control.dispose();
		}
	}
	
	
	private ArrayList<SnippetInstanceRelation> generateCodeSnippets(CloneInstance instance){
		ArrayList<SnippetInstanceRelation> codeList = new ArrayList<SnippetInstanceRelation>();
		for(CloneInstance ins: instance.getCloneSet()){
			if(ins != instance){
				SnippetInstanceRelation relation = ViewUtil.getCodeSnippetForCloneInstance(ins);
				relation.snippet = MinerUtil.xmlStringProcess(relation.snippet);
				codeList.add(relation);
			}
		}
		SnippetInstanceRelation relation = ViewUtil.getCodeSnippetForCloneInstance(instance);
		relation.snippet = MinerUtil.xmlStringProcess(relation.snippet);
		codeList.add(relation);
		
		return codeList;
	} 
	
	/*private String getCodeSnippetOfCloneInstance(CloneInstance instance){
		return MinerUtil.xmlStringProcess(ViewUtil.getCodeSnippetForCloneInstance(instance));
	}*/
	
	//private void createSectionsForCode

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}

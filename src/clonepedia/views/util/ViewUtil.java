package clonepedia.views.util;

import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.internal.corext.refactoring.structure.ASTNodeSearchUtil;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;


import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.OntologicalRelationType;
import clonepedia.model.viewer.CloneSetWrapper;
import clonepedia.model.viewer.IContainer;
import clonepedia.model.viewer.IContent;
import clonepedia.util.MinerProperties;
import clonepedia.util.Settings;
import clonepedia.views.codesnippet.CloneCodeSnippetView;
import clonepedia.views.codesnippet.SnippetInstanceRelation;

public class ViewUtil {
	
	public static ICompilationUnit getCorrespondingCompliationUnit(CloneInstance instance){
		//instance.getResidingMethod().getOwner().getFullName();

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject proj = root.getProject(Settings.projectName);
		try {
			if (proj.isNatureEnabled(MinerProperties.javaNatureName)) {
				IJavaProject javaProject = JavaCore.create(proj);

				IPackageFragment[] packages = javaProject.getPackageFragments();
				for (IPackageFragment pack : packages) {
					if (pack.getKind() == IPackageFragmentRoot.K_SOURCE) {
						for (ICompilationUnit unit : pack.getCompilationUnits()) {
							String packageName = unit.getParent().getElementName();
							/*String unitName = unit.getElementName();
							String fullName = packageName + "." + unitName;
							fullName = fullName.substring(0, fullName.length() - 5);*/
							
							String instanceResidingClassFullName;
							ComplexType type = instance.getResidingMethod().getOwner();
							if(type instanceof Interface){
								instanceResidingClassFullName = type.getFullName();
							}
							else{
								Class clas = (Class)type;
								while(clas.getOuterClass() != null)
									clas = clas.getOuterClass();
								
								instanceResidingClassFullName = clas.getFullName();
							}
							
							String instanceResidingPackageName = instanceResidingClassFullName.substring(0, 
									instanceResidingClassFullName.lastIndexOf("."));
							String instanceResidingTypeName = instanceResidingClassFullName.substring(
									instanceResidingClassFullName.lastIndexOf(".")+1, instanceResidingClassFullName.length());
							if (instanceResidingPackageName.equals(packageName) &&
									isContainedInCompilationUnit(unit, instanceResidingTypeName)) {
								return unit;
							}
						}
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static ICompilationUnit getCorrespondingCompliationUnit(String typeFullName){
		//instance.getResidingMethod().getOwner().getFullName();

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject proj = root.getProject(Settings.projectName);
		try {
			if (proj.isNatureEnabled(MinerProperties.javaNatureName)) {
				IJavaProject javaProject = JavaCore.create(proj);

				IPackageFragment[] packages = javaProject.getPackageFragments();
				for (IPackageFragment pack : packages) {
					if (pack.getKind() == IPackageFragmentRoot.K_SOURCE) {
						for (ICompilationUnit unit : pack.getCompilationUnits()) {
							String packageName = unit.getParent().getElementName();
							String unitName = unit.getElementName();
							String fullName = packageName + "." + unitName;
							fullName = fullName.substring(0, fullName.length() - 5);
							
							if(fullName.equals(typeFullName)){
								return unit;
							}
							
						}
					}
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static boolean isContainedInCompilationUnit(ICompilationUnit unit, String typeName){
		IType[] typeList;
		try {
			typeList = unit.getTypes();
			for(IType type: typeList){
				if(type.getElementName().equals(typeName)){
					return true;
				}
			}
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static SnippetInstanceRelation getCodeSnippetForCloneInstance(CloneInstance instance){
		ICompilationUnit iunit = getCorrespondingCompliationUnit(instance);
		
		if(iunit != null){
			ASTParser parser = ASTParser.newParser(AST.JLS3);
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setSource(iunit);
			parser.setResolveBindings(true);
			CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			
			
			int startPosition = cu.getPosition(instance.getStartLine(), 0);
			int endPosition = cu.getPosition(instance.getEndLine()+1, 0);
			
			int upperDistant = 0;
			int lowerDistant = 0;
			
			try {
				IJavaElement element = iunit.getElementAt(startPosition-1);
				IMethod method = (IMethod)element;
				
				
				@SuppressWarnings("restriction")
				MethodDeclaration node = ASTNodeSearchUtil.getMethodDeclarationNode(method, cu);
				int methodStartPosition = node.getStartPosition();
				int methodEndPosition = methodStartPosition + node.getLength();
				methodStartPosition = methodEndPosition - node.getBody().getLength();
				
				int methodStartLine = cu.getLineNumber(methodStartPosition);
				int methodEndLine = cu.getLineNumber(methodEndPosition);
				
				upperDistant = instance.getStartLine() - (methodStartLine + 1);
				lowerDistant = methodEndLine - (instance.getEndLine() + 1);
				//System.out.println(methodStartLine + " " + methodEndLine);
			} catch (JavaModelException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			String code = "";
			try {
				code = iunit.getSource();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			code = code.substring(startPosition, endPosition);
			return new SnippetInstanceRelation(instance, code, upperDistant, lowerDistant);
		}
		return null;
	}
	
	public static void openJavaEditorForCloneInstance(CloneInstance instance) {
		
		ICompilationUnit unit = getCorrespondingCompliationUnit(instance);
		if(unit != null){
			IEditorPart javaEditor;
			try {
				javaEditor = JavaUI.openInEditor(unit);
				JavaUI.revealInEditor(javaEditor,
						(IJavaElement) unit);
				goToLine(javaEditor, instance.getStartLine(), instance.getEndLine());
			} catch (PartInitException e) {
				e.printStackTrace();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			
		}
		
		//CompilationUnitEditor editor = (CompilationUnitEditor)javaEditor;
		
		/*IResource resource= unit.getResource();
		IEditorInput input = javaEditor.getEditorInput();
		
		FileEditorInput fei = (FileEditorInput)input;
		IJavaElement cu = JavaCore.create((IFile)resource);
		IEditorPart editor = JavaUI.openInEditor(cu);*/
	}
	
	public static void openJavaEditorForCloneInstace(ICompilationUnit unit, int startLine, int endLine) {
		if(unit != null){
			IEditorPart javaEditor;
			try {
				javaEditor = JavaUI.openInEditor(unit);
				JavaUI.revealInEditor(javaEditor,
						(IJavaElement) unit);
				goToLine(javaEditor, startLine, endLine);
			} catch (PartInitException e) {
				e.printStackTrace();
			} catch (JavaModelException e) {
				e.printStackTrace();
			}
			
		}
	}

	private static void goToLine(IEditorPart editorPart, int startLine, int endLine) {

		if (!(editorPart instanceof ITextEditor)) {
			return;
		}

		ITextEditor editor = (ITextEditor) editorPart;
		IDocument document = editor.getDocumentProvider().getDocument(
				editor.getEditorInput());
		if (document != null) {
			
			IRegion startLineInfo = null;
			IRegion endLineInfo = null;
			try {
				// line count internally starts with 0, and not with 1 like in
				// GUI
				startLineInfo = document.getLineInformation(startLine - 1);
				endLineInfo = document.getLineInformation(endLine - 1);
			} catch (BadLocationException e) {
				// ignored because line number may not really exist in document,
				// we guess this...
				e.printStackTrace();
			}
			if (startLineInfo != null && endLineInfo != null) {
				editor.selectAndReveal(startLineInfo.getOffset(),
						(endLineInfo.getOffset() - startLineInfo.getOffset() + endLineInfo.getLength()));
			}
		}
	}
	
	public static String getSimplifiedNote(OntologicalRelationType relationType){
		if((relationType.equals(OntologicalRelationType.call)) ||
				(relationType.equals(OntologicalRelationType.access))||
				(relationType.equals(OntologicalRelationType.define))||
						(relationType.equals(OntologicalRelationType.refer))||
						(relationType.equals(OntologicalRelationType.useConstant))||
						(relationType.equals(OntologicalRelationType.useClass))||
						(relationType.equals(OntologicalRelationType.useInterface)) ||
						(relationType.equals(OntologicalRelationType.use)))
						return "-}";
		else if((relationType.equals(OntologicalRelationType.hasType))||
				(relationType.equals(OntologicalRelationType.extendClass))||
				(relationType.equals(OntologicalRelationType.extendInterface)))
			return ":";
		else if((relationType.equals(OntologicalRelationType.implement)))
			return "~";
		else if((relationType.equals(OntologicalRelationType.isInnerClassOf))||
				(relationType.equals(OntologicalRelationType.declaredIn))||
				(relationType.equals(OntologicalRelationType.resideIn)))
			return "@";
		else if((relationType.equals(OntologicalRelationType.hasParameterType)))
			return "$";
		return null;
	}
}

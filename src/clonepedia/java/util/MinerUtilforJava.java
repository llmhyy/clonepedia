package clonepedia.java.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;

import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.internal.compiler.ast.ConstructorDeclaration;
import org.eclipse.jdt.internal.corext.callhierarchy.CallHierarchy;
import org.eclipse.jdt.internal.corext.callhierarchy.MethodWrapper;

import clonepedia.businessdata.OntologicalDBDataFetcher;
import clonepedia.businessdata.OntologicalDataFetcher;
import clonepedia.businessdata.OntologicalModelDataFetcher;
import clonepedia.java.ASTStatementSimilarityComparator;
import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.EnumType;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.PrimiType;
import clonepedia.model.ontology.ProgrammingElement;
import clonepedia.model.ontology.Project;
import clonepedia.model.ontology.RegionalOwner;
import clonepedia.model.ontology.TypeVariableType;
import clonepedia.model.ontology.VarType;
import clonepedia.model.ontology.Variable;
import clonepedia.model.ontology.VariableUseType;
import clonepedia.util.Settings;

public class MinerUtilforJava {
	/**
	 * Currently, the concerned AST node type are: simple name, literal(constants such as character, number, string, type and boolean)
	 * and primitive type(such as float, int, double, char and so on).
	 * @param node
	 * @return
	 */
	public static boolean isConcernedType(ASTNode node) {
		int type = node.getNodeType();
		if (type == ASTNode.SIMPLE_NAME 
				|| type == ASTNode.CHARACTER_LITERAL
				|| type == ASTNode.NUMBER_LITERAL
				|| type == ASTNode.STRING_LITERAL
				|| type == ASTNode.TYPE_LITERAL
				|| type == ASTNode.BOOLEAN_LITERAL
				|| type == ASTNode.PRIMITIVE_TYPE
				)
			return true;
		else
			return false;
	}

	public static ITypeBinding getBinding(ASTNode node) {
		if (!isConcernedType(node))
			return null;

		if (node instanceof SimpleName)
			return ((SimpleName) node).resolveTypeBinding();
		else if (node instanceof CharacterLiteral)
			return ((CharacterLiteral) node).resolveTypeBinding();
		else if (node instanceof NumberLiteral)
			return ((NumberLiteral) node).resolveTypeBinding();
		else if (node instanceof StringLiteral)
			return ((StringLiteral) node).resolveTypeBinding();
		else if (node instanceof TypeLiteral)
			return ((TypeLiteral) node).resolveTypeBinding();
		else if (node instanceof BooleanLiteral)
			return ((BooleanLiteral) node).resolveTypeBinding();
		else if (node instanceof PrimitiveType)
			return ((PrimitiveType) node).resolveBinding();

		return null;
	}

	public static String getConcernedASTNodeName(ASTNode node) {
		if (!isConcernedType(node))
			return null;

		if (node instanceof SimpleName)
			return ((SimpleName) node).getIdentifier();
		else if (node instanceof CharacterLiteral)
			return String.valueOf(((CharacterLiteral) node).charValue());
		else if (node instanceof NumberLiteral)
			return ((NumberLiteral) node).getToken();
		else if (node instanceof StringLiteral)
			return ((StringLiteral) node).getLiteralValue();
		else if (node instanceof TypeLiteral)
			return ((TypeLiteral) node).toString();
		else if (node instanceof BooleanLiteral)
			return ((BooleanLiteral) node).toString();
		else if (node instanceof PrimitiveType)
			return ((PrimitiveType) node).getPrimitiveTypeCode().toString();

		return null;
	}

	/**
	 * As some key words(while, if, for, instanceof, return, switch, this,
	 * throw, try, catch, continue, do) should be reserved in clone fragments so
	 * that they can be used as "landmark" to distinguish the different part of
	 * clone code fragments. The more "context" in code code, the better effect
	 * my diff-algorithm has.
	 */
	public static boolean isBenchMarkType(ASTNode node) {
		/*
		 * if(node.getNodeType() == ASTNode.CATCH_CLAUSE || node.getNodeType()
		 * == ASTNode.CONTINUE_STATEMENT || node.getNodeType() ==
		 * ASTNode.DO_STATEMENT || node.getNodeType() == ASTNode.FOR_STATEMENT
		 * || node.getNodeType() == ASTNode.IF_STATEMENT || node.getNodeType()
		 * == ASTNode.INSTANCEOF_EXPRESSION || node.getNodeType() ==
		 * ASTNode.RETURN_STATEMENT || node.getNodeType() ==
		 * ASTNode.SWITCH_STATEMENT || node.getNodeType() ==
		 * ASTNode.THIS_EXPRESSION || node.getNodeType() ==
		 * ASTNode.THROW_STATEMENT || node.getNodeType() ==
		 * ASTNode.TRY_STATEMENT || node.getNodeType() ==
		 * ASTNode.WHILE_STATEMENT) return true; return false;
		 */
		return !isConcernedType(node);
	}

	public static boolean isTheBindingsofTheSameType(ITypeBinding b1, ITypeBinding b2) {
		boolean flag = true;

		// flag = flag && (b1.isAnnotation() == b2.isAnnotation());
		// flag = flag && (b1.isAnonymous() == b2.isAnonymous());
		flag = flag && (b1.isArray() == b2.isArray());
		// flag = flag && (b1.isCapture() == b2.isCapture());
		// flag = flag && (b1.isClass() == b2.isClass());
		flag = flag && (b1.isEnum() == b2.isEnum());
		// flag = flag && (b1.isInterface() == b2.isInterface());
		// flag = flag && (b1.isLocal() == b2.isLocal());
		// flag = flag && (b1.isMember() == b2.isMember());
		flag = flag && (b1.isNullType() == b2.isNullType());
		flag = flag && (b1.isPrimitive() == b2.isPrimitive());
		// flag = flag && (b1.isTypeVariable() == b2.isTypeVariable());

		return flag;
	}

	/**
	 * Currently, I just handle the case of SimpleName, because it is mainly
	 * used in the comparison algorithm. The Method
	 * <code>isTheBindingsofTheSameType</code> could be used to find out the
	 * return value, type of the method and variable, however, it is incapable
	 * of handling the case like "String string = new String()" where both the
	 * identifiers "String" and "string" are of the same ITypeBinding value but
	 * they must be distinguished.
	 * 
	 * @param n1
	 * @param n2
	 * @return
	 */
	public static boolean isASTNodesofTheSameType(ASTNode n1, ASTNode n2) {
		
		
		if (n1.getNodeType() == n2.getNodeType()) {
			if (n1.getNodeType() == ASTNode.SIMPLE_NAME) {
				SimpleName name1 = (SimpleName) n1;
				SimpleName name2 = (SimpleName) n2;
				
				IBinding bind1 = name1.resolveBinding();
				IBinding bind2 = name2.resolveBinding();
				
				if(bind1 != null && bind2 != null){
					if(bind1.getKind() != bind2.getKind()){
						return false;
					}
				}

				if (name1.getParent().getNodeType() == ASTNode.SIMPLE_TYPE
						|| name2.getParent().getNodeType() == ASTNode.SIMPLE_TYPE) {
					return (name1.getParent().getNodeType() == ASTNode.SIMPLE_TYPE && 
							name2.getParent().getNodeType() == ASTNode.SIMPLE_TYPE);
				} else{
					/**
					 * The reason why the AST nodes other than SimpleName will be true is because the cases involving SimpleName
					 * are too complicated. Therefore, a further comparison is needed to conducted to judge whether two AST nodes
					 * are of the same type.
					 */
					return true;
				}	
			} else{
				return true;
			}
		} else {
			return false;
		}
			
	}
	
	public static boolean isTheASTNodesBelongToSimilarStatement(ASTNode node1, ASTNode node2){
		ASTNode stat1 = node1.getParent();
		ASTNode stat2 = node2.getParent();
		while(!(stat1 instanceof Statement)){
			stat1 = stat1.getParent();
		}
		while(!(stat2 instanceof Statement)){
			stat2 = stat2.getParent();
		}
		
		Statement s1 = (Statement)stat1;
		Statement s2 = (Statement)stat2;
		ASTStatementSimilarityComparator comparator = new ASTStatementSimilarityComparator();
		try {
			return (s1.getNodeType() == s2.getNodeType()) && (comparator.computeCost(s1, s2)<Settings.thresholdForStatementDifference);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static VarType getVariableType(Type type, Project project, OntologicalDataFetcher fetcher) throws Exception {
		if (type == null) {
			return null;
		} else if (type.isPrimitiveType()) {
			return new PrimiType(type.toString());
		} else if (type.isSimpleType() || type.isQualifiedType()) {
			if (type.resolveBinding().isClass()) {
				return (clonepedia.model.ontology.Class) transferTypeToComplexType(type,
						project, (CompilationUnit)type.getRoot(), fetcher);
			} else if (type.resolveBinding().isInterface()) {
				return (clonepedia.model.ontology.Interface) transferTypeToComplexType(
						type, project, (CompilationUnit)type.getRoot(), fetcher);
			} else if (type.resolveBinding().isEnum()){
				return new EnumType(type.toString());
			} else if (type.resolveBinding().isTypeVariable()){
				return new TypeVariableType(type.toString());
			} else
				throw new Exception("The type " + type.toString() + " cannot be handled");
		}
		/**
		 * The array type is simplified as its element type because of the
		 * following reasons 1) The coding time is limited 2) In clone code,
		 * there should be some relationship between a type and a list of its
		 * type.
		 * 
		 * I will come back to investigate or refine the problem in future work.
		 */
		else if (type.isArrayType()) {
			ArrayType arrayType = (ArrayType) type;
			return getVariableType(arrayType.getElementType(), project, fetcher);
		} else if (type.isParameterizedType()) {
			ParameterizedType paramType = (ParameterizedType) type;
			return getVariableType(paramType.getType(), project, fetcher);
		} else {
			throw new Exception("The type " + type.toString() + " cannot be handled");
		}
	}

	public static VarType getVariableType(ITypeBinding type, Project project, CompilationUnit compilationUnit, OntologicalDataFetcher fetcher) throws Exception {
		if (type == null) {
			return null;
		} else if (type.isPrimitive()) {
			return new PrimiType(type.getName());
		} else if (type.isClass()) {
			return (clonepedia.model.ontology.Class) transferTypeToComplexType(type, project, compilationUnit, fetcher);
		} else if (type.isInterface()) {
			return (clonepedia.model.ontology.Interface) transferTypeToComplexType(type, project, compilationUnit, fetcher);
		} else if (type.isEnum()) {
			return new EnumType(type.getName());
		} else if (type.isTypeVariable()){
			return new TypeVariableType(type.getName());
		}
		/**
		 * The array type is simplified as its element type because of the
		 * following reasons 1) The coding time is limited 2) In clone code,
		 * there should be some relationship between a type and a list of its
		 * type.
		 * 
		 * I will come back to investigate or refine the problem in future work.
		 */
		else if (type.isArray()) {
			return getVariableType(type.getElementType(), project, compilationUnit, fetcher);
		} else if (type.isParameterizedType()) {
			return getVariableType(type.getTypeDeclaration(), project, compilationUnit, fetcher);
		} else if (type.isCapture()){
			ITypeBinding b = type.getWildcard().getGenericTypeOfWildcardType();
			
			System.out.print("");
			return getVariableType(b, project, compilationUnit, fetcher);
		} else {
			throw new Exception("The type " + type.getName() + " cannot be handled");
		}
	}

	public static ComplexType transferTypeToComplexType(Type type, Project project, CompilationUnit cu, OntologicalDataFetcher fetcher) {
		
		return transferTypeToComplexType(type.resolveBinding(), project, cu, fetcher);
		
		/*OntologicalDataFetcher fetcher = new OntologicalDataFetcher();
		String fullName = type.toString();
		if (!fullName.contains("."))
			fullName = type.resolveBinding().getPackage().getName() + "."
					+ fullName;

		if (type.resolveBinding().isClass())
			return (ComplexType) fetcher.getTheExistingClassorCreateOne(fullName,
					project);
		else if (type.resolveBinding().isInterface())
			return (ComplexType) fetcher.getTheExistingInterfaceorCreateOne(fullName,
					project);
		else
			return null;*/
	}

	/**
	 * Given a type binding, the method return a complexType(class or interface).
	 * The parameter cu is used only when the type binding is correspond to an
	 * anonymous class.
	 * 
	 * Whenever a class/interface is transferred, the transferred type will also
	 * be stored in ontology. 
	 * @param typeBinding
	 * @param project
	 * @param cu
	 * @return
	 */
	public static ComplexType transferTypeToComplexType(ITypeBinding typeBinding,
			Project project, CompilationUnit cu, OntologicalDataFetcher fetcher) {
		
		if(null == typeBinding) return null;
		//String fullName = getSimpleClassNameFromTypeBinding(typeBinding, cu);
		
		/**
		 * The type is an anonymous class.
		 */
		/*if(fullName.equals("")){
			Class anonymousClass = extractAndStoreClassInformation(typeBinding, true, 
					new OntologicalDataFetcher(), cu, project);
			return anonymousClass;
		}
		
		if (!fullName.contains(".")){
			fullName = typeBinding.getPackage().getName() + "." + fullName;
		}*/
		
		if (typeBinding.isClass()){
			String classFullName = getClassFullName(typeBinding, cu);
			return fetcher.getTheExistingClassorCreateOne(classFullName, project);
		}
		else if (typeBinding.isInterface()){
			String interfaceFullName = typeBinding.getPackage().getName() + "." + typeBinding.getName();
			return fetcher.getTheExistingInterfaceorCreateOne(interfaceFullName, project);
		}
		else
			return null;
	}

	@SuppressWarnings("rawtypes")
	public static Method getMethodfromASTNode(MethodDeclaration md, Project project, OntologicalDataFetcher fetcher) throws Exception {
		
		ASTNode node = md.getParent();
		while(!(node instanceof TypeDeclaration)){
			node = node.getParent();
		}
		TypeDeclaration declaringType = (TypeDeclaration)node;
		
		ComplexType owner = transferTypeToComplexType(declaringType.resolveBinding(), project, (CompilationUnit)md.getRoot(), fetcher);

		String methodName = md.getName().getIdentifier();
		
		VarType returnType = getVariableType(md.getReturnType2(), project, fetcher);
		ArrayList<Variable> parameterList = new ArrayList<Variable>();
		List paramList = md.parameters();

		for (int j = 0; j < paramList.size(); j++) {
			SingleVariableDeclaration svd = (SingleVariableDeclaration) paramList
					.get(j);

			String paramName = svd.getName().getIdentifier();
			VarType paramType = getVariableType(svd.getType(), project, fetcher);

			Variable variable = new Variable(paramName, paramType, false);
			parameterList.add(variable);
		}

		return new Method(owner, methodName, returnType, parameterList);
	}

	public static Method getMethodfromBinding(IMethodBinding methodBinding,
			Project project, CompilationUnit cu, OntologicalDataFetcher fetcher) throws Exception {

		String methodName = methodBinding.getName();
		ComplexType methodOwner = transferTypeToComplexType(methodBinding.getDeclaringClass(), project, cu, fetcher);
		VarType returnType = getVariableType(methodBinding.getReturnType(), project, cu, fetcher);
		System.out.print("");
		ITypeBinding[] paramList = methodBinding.getParameterTypes();

		ArrayList<Variable> parameters = new ArrayList<Variable>();
		for (int i = 0; i < paramList.length; i++) {
			VarType paramType = getVariableType(paramList[i], project, cu, fetcher);
			parameters.add(new Variable("", paramType, false));
		}
		Method method = new Method(methodOwner, methodName, returnType, parameters);
		
		ASTNode node = cu.findDeclaringNode(methodBinding);
		if(null != node){
			MethodDeclaration md = (MethodDeclaration)node;
			int startLine = cu.getLineNumber(md.getStartPosition());
			int endLine = cu.getLineNumber(md.getStartPosition() + md.getLength());
			
			method.setLength(endLine - startLine + 1);
			
		}
		return method;
	}
	
	public static Field getFieldfromBinding(IVariableBinding variableBinding, Project project, CompilationUnit cu, OntologicalDataFetcher fetcher) throws Exception{
		String fieldName = variableBinding.getName();
		ComplexType type = transferTypeToComplexType(
				variableBinding.getDeclaringClass(), project, cu, fetcher);
		VarType fieldType = getVariableType(variableBinding.getType(), project, cu, fetcher);
		return new Field(fieldName, type, fieldType);
	}
	
	public static Variable getVariablefromBinding(SimpleName name, IVariableBinding variableBinding, Project project, CompilationUnit cu, OntologicalDataFetcher fetcher) throws Exception{
		String variableName = variableBinding.getName();
		VarType variableType = getVariableType(variableBinding.getType(), project, cu, fetcher);
		VariableUseType useType;
		if(name.isDeclaration())
			useType = VariableUseType.DEFINE;
		else
			useType = VariableUseType.REFER;
		return new Variable(variableName, variableType, useType);
	}
	
	

	public static String getSimpleClassNameFromTypeBinding(ITypeBinding binding, CompilationUnit compilationUnit){
		String className;
		ASTNode node = compilationUnit.findDeclaringNode(binding);
		/**
		 * The type is an anonymous class
		 */
		if(node instanceof AnonymousClassDeclaration){
			int lineNumber = compilationUnit.getLineNumber(node.getStartPosition());
			className = "Anonymous" + lineNumber;
		}
		else{
			className = binding.getName();
		}
		return className;
	}
	
	private static String getClassFullName(ITypeBinding binding, CompilationUnit compilationUnit){
		String longestPrefix = getTheLongestPrefixOfFullName(binding, compilationUnit);
		return longestPrefix + "." + getSimpleClassNameFromTypeBinding(binding, compilationUnit);
	}
	
	/**
	 * The longest prefix of full name means its substring which contain characters from the first to the one before last dot character.
	 * For example, the longest prefix of full name "org.eclipse.jdt.dom.ASTNode.ASTInnerClass" is "org.eclipse.jdt.dom.ASTNode", and the
	 * longest prefix of full name "org.eclipse.jdt.dom.ASTNode" is "org.eclipse.jdt.dom".
	 * 
	 * @param binding
	 * @param compilationUnit
	 * @return
	 */
	public static String getTheLongestPrefixOfFullName(ITypeBinding binding, CompilationUnit compilationUnit){
		ITypeBinding declaringClassBinding = binding.getDeclaringClass();
		if(declaringClassBinding != null){
			String outerClassFullName = getSimpleClassNameFromTypeBinding(declaringClassBinding, compilationUnit);
			
			/**
			 * Iteratively find the declaring class path.
			 */
			ITypeBinding parentDeclaringClassBinding = declaringClassBinding.getDeclaringClass();
			while(parentDeclaringClassBinding != null){
				String parentClassName = getSimpleClassNameFromTypeBinding(parentDeclaringClassBinding, compilationUnit);
				outerClassFullName = parentClassName + "." + outerClassFullName;
				parentDeclaringClassBinding = parentDeclaringClassBinding.getDeclaringClass();
				//System.out.println("in declaring class loop");
			}
			
			outerClassFullName = declaringClassBinding.getPackage().getName() + "." + outerClassFullName;
			return outerClassFullName;
		}
		else{
			return binding.getPackage().getName();
		}
	}

	/*
	 * public cloneminer.model.Class transferTypeToClass(Type type, Project
	 * project) { String classFullName = type.toString(); if
	 * (!classFullName.contains(".")) classFullName =
	 * type.resolveBinding().getPackage().getName() + "." + classFullName; else
	 * { System.out.print("qua"); } return
	 * getTheExistingClassorCreateOne(classFullName, project); }
	 * 
	 * public cloneminer.model.Interface transferTypeToInterface(Type type,
	 * Project project) { String interfaceFullName = type.toString(); if
	 * (!interfaceFullName.contains(".")) interfaceFullName =
	 * type.resolveBinding().getPackage().getName() + "." + interfaceFullName;
	 * else { System.out.print("qua"); } return
	 * getTheExistingInterfaceorCreateOne(interfaceFullName, project); }
	 */

	public static ASTNode[] convertASTNode(Object[] objectList) {
		ASTNode[] nodeList = new ASTNode[objectList.length];
		for (int i = 0; i < objectList.length; i++)
			nodeList[i] = (ASTNode) objectList[i];

		return nodeList;
	}
	
	public static Statement[] convertStatement(Object[] objectList){
		Statement[] statList = new Statement[objectList.length];
		for (int i = 0; i < objectList.length; i++)
			statList[i] = (Statement) objectList[i];

		return statList;
	}
	
	public static boolean isComplexStatement(ASTNode node){
		switch(node.getNodeType()){
		case ASTNode.BLOCK: 
			return true;
		case ASTNode.IF_STATEMENT: 
			return true;
		case ASTNode.FOR_STATEMENT:
			return true;
		case ASTNode.ENHANCED_FOR_STATEMENT:
			return true;
		case ASTNode.WHILE_STATEMENT:
			return true;
		case ASTNode.DO_STATEMENT:
			return true;
		case ASTNode.TRY_STATEMENT:
			return true;
		case ASTNode.SYNCHRONIZED_STATEMENT:
			return true;
		case ASTNode.SWITCH_STATEMENT:
			return true;
		}
		
		return false;
	}
	
	public static ComplexType getComplexTypeWithBasicInfoByBinding(ITypeBinding typeBinding, CompilationUnit compilationUnit, Project project){
		
		if(typeBinding == null) return null;
		
		if(typeBinding.isClass()){
			String classFullName = getClassFullName(typeBinding, compilationUnit);
			return new Class(null, project, classFullName);
			//return MinerUtilforJava.transferTypeToComplexType(type.getType(), project, (CompilationUnit)node.getRoot());	
			
		}
		else if (typeBinding.isInterface()){
			String interfaceFullName = typeBinding.getPackage().getName() + "." + typeBinding.getName();
			return new Interface(null, project, interfaceFullName);
			//return MinerUtilforJava.transferTypeToComplexType(type.getType(), project, (CompilationUnit)node.getRoot());	
		}
		else return null;
	}
	
	public static VarType getVariableTypeWithBasicInfoByBinding(ITypeBinding type, Project project, CompilationUnit compilationUnit) throws Exception {
		if (type == null) {
			return null;
		} else if (type.isPrimitive()) {
			return new PrimiType(type.getName());
		} else if (type.isClass()) {
			return (clonepedia.model.ontology.Class) getComplexTypeWithBasicInfoByBinding(type, compilationUnit, project);
		} else if (type.isInterface()) {
			return (clonepedia.model.ontology.Interface) getComplexTypeWithBasicInfoByBinding(type, compilationUnit, project);
		} else if (type.isEnum()) {
			return new EnumType(type.getName());
		} else if (type.isTypeVariable()){
			return new TypeVariableType(type.getName());
		}
		/**
		 * The array type is simplified as its element type because of the
		 * following reasons 1) The coding time is limited 2) In clone code,
		 * there should be some relationship between a type and a list of its
		 * type.
		 * 
		 * I will come back to investigate or refine the problem in future work.
		 */
		else if (type.isArray()) {
			return getVariableTypeWithBasicInfoByBinding(type.getElementType(), project, compilationUnit);
		} else if (type.isParameterizedType()) {
			return getVariableTypeWithBasicInfoByBinding(type.getTypeDeclaration(), project, compilationUnit);
		} else if (type.isCapture()){
			ITypeBinding b = type.getWildcard().getGenericTypeOfWildcardType();
			
			System.out.print("");
			return getVariableTypeWithBasicInfoByBinding(b, project, compilationUnit);
		} else {
			throw new Exception("The type " + type.getName() + " cannot be handled");
		}
	}
	
	public static ProgrammingElement transferASTNodesToProgrammingElementType(ASTNode node) throws Exception{
		
		Project project = new Project(Settings.projectName, "java", "");
		CompilationUnit cu = (CompilationUnit)node.getRoot();
		
		if(MinerUtilforJava.isConcernedType(node)){
			if(node.getNodeType() == ASTNode.PRIMITIVE_TYPE){
				PrimitiveType primitiveType = (PrimitiveType)node;
				PrimiType primiType = new PrimiType(primitiveType.toString());
				return primiType;
			}
			else if(node.getNodeType() == ASTNode.STRING_LITERAL ||
					node.getNodeType() == ASTNode.NUMBER_LITERAL ||
					node.getNodeType() == ASTNode.CHARACTER_LITERAL ||
					node.getNodeType() == ASTNode.BOOLEAN_LITERAL){
				
				String typeName = ASTNode.nodeClassForType(node.getNodeType()).getSimpleName();
				typeName = typeName.substring(0, typeName.length()-7);
				Constant constant = new Constant(MinerUtilforJava.getConcernedASTNodeName(node), new PrimiType(typeName), false);
				return constant;
			}
			else if(node.getNodeType() == ASTNode.TYPE_LITERAL){
				TypeLiteral type = (TypeLiteral)node;
				ITypeBinding typeBinding = type.getType().resolveBinding();
				return getComplexTypeWithBasicInfoByBinding(typeBinding, cu, project);
			}
			else {
				SimpleName name = (SimpleName)node;
				
				if(name.resolveBinding() == null)
					return null;
				
				if(name.resolveBinding().getKind() == IBinding.TYPE){
					ITypeBinding typeBinding = (ITypeBinding)name.resolveBinding();
					return getComplexTypeWithBasicInfoByBinding(typeBinding, cu, project);
				}
				else if(name.resolveBinding().getKind() == IBinding.METHOD){
					IMethodBinding methodBinding = (IMethodBinding) name.resolveBinding();
					
					String methodName = methodBinding.getName();
					ComplexType methodOwner = getComplexTypeWithBasicInfoByBinding(methodBinding.getDeclaringClass(), cu, project);
					VarType returnType = getVariableTypeWithBasicInfoByBinding(methodBinding.getReturnType(), project, cu);
					
					ITypeBinding[] paramList = methodBinding.getParameterTypes();

					ArrayList<Variable> parameters = new ArrayList<Variable>();
					for (int i = 0; i < paramList.length; i++) {
						VarType paramType = getVariableTypeWithBasicInfoByBinding(paramList[i], project, cu);
						parameters.add(new Variable("", paramType, false));
					}

					return new Method(methodOwner, methodName, returnType, parameters);
				}
				else if(name.resolveBinding().getKind() == IBinding.VARIABLE){
					IVariableBinding variableBinding = (IVariableBinding) name.resolveBinding();
					String variableName = variableBinding.getName();
					VarType variableType = getVariableTypeWithBasicInfoByBinding(variableBinding.getType(), project, cu);
					if(variableBinding.isField()){
						ComplexType type = getComplexTypeWithBasicInfoByBinding(
								variableBinding.getDeclaringClass(), cu, project);
						
						return new Field(variableName, type, variableType);
					}
					else {
						//Variable v = MinerUtilforJava.getVariablefromBinding(name, variableBinding, project, (CompilationUnit)node.getRoot());
						//return v;
						return new Variable(variableName, variableType, VariableUseType.REFER);
					}
				}
				return null;
			}
		}
		else return null;
	}
	
	public static CompilationUnit getCompilationUnit(ASTNode astNode){
		CompilationUnit unit = null;
		ASTNode node = astNode.getParent();
		while(!(node instanceof CompilationUnit)){
			node = node.getParent();
		}
		unit = (CompilationUnit)node;
		
		return unit;
	}
	
	public static HashSet<IMethod> getCallerForMethod(IMethod methodElement){
		CallHierarchy callHierarchy = CallHierarchy.getDefault();

		IMember[] members = { methodElement };
		MethodWrapper[] methodWrappers = callHierarchy.getCallerRoots(members);
		HashSet<IMethod> callers = new HashSet<IMethod>();
		for (MethodWrapper mw : methodWrappers) {
			
			try{
				MethodWrapper[] mw2 = mw.getCalls(new NullProgressMonitor());
				HashSet<IMethod> temp = getIMethods(mw2);
				callers.addAll(temp);				
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		return callers;
	}

	private static HashSet<IMethod> getIMethods(MethodWrapper[] methodWrappers) {
		HashSet<IMethod> c = new HashSet<IMethod>();
		for (MethodWrapper m : methodWrappers) {
			IMethod im = getIMethodFromMethodWrapper(m);
			if (im != null) {
				c.add(im);
			}
		}
		return c;
	}

	private static IMethod getIMethodFromMethodWrapper(MethodWrapper m) {
		try {
			IMember im = m.getMember();
			if (im.getElementType() == IJavaElement.METHOD) {
				return (IMethod) m.getMember();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

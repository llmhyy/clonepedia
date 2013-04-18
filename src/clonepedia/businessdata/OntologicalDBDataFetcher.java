package clonepedia.businessdata;

import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

import clonepedia.db.DBOperator;
import clonepedia.db.schema.DBTable;
import clonepedia.db.schema.Entity;
import clonepedia.db.schema.Relation;
import clonepedia.model.db.DataRecord;
import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
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
import clonepedia.model.semantic.Word;
import clonepedia.model.semantic.WordBag;
import clonepedia.syntactic.pools.ClassPool;
import clonepedia.syntactic.pools.InterfacePool;
import clonepedia.util.MinerUtil;

public class OntologicalDBDataFetcher implements OntologicalDataFetcher{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 916566187626592609L;
	
	//private DBOperator dbOperator = new DBOperator();
	
	public ArrayList<String> getExtendedInterfaceIds(String interfaceId){
		Properties properties = new Properties();
		properties.put("subInterfaceId", interfaceId);
		ArrayList<String> idList = new ArrayList<String>();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Relation(Relation.InterfaceExtendRelation), properties);
		for(DataRecord record: records){
			String id = record.getProperty("superInterfaceId");
			idList.add(id);
		}
		
		return idList;
	}
	
	public ArrayList<String> getImplementedInterfaceIds(String classId){
		Properties properties = new Properties();
		properties.put("classId", classId);
		ArrayList<String> idList = new ArrayList<String>();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Relation(Relation.ImplementRelation), properties);
		for(DataRecord record: records){
			String id = record.getProperty("interfaceId");
			idList.add(id);
		}

		return idList;
	}
	
	public ArrayList<String> getMethodIds(String cloneSetId){
		Properties properties = new Properties();
		properties.put("cloneSetId", cloneSetId);
		ArrayList<String> idList = new ArrayList<String>();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Relation(Relation.CommonPartCall), properties);
		for(DataRecord record: records){
			String id = record.getProperty("methodId");
			idList.add(id);
		}

		return idList;
	}
	
	public ArrayList<Field> getFields(String cloneSetId){
		Properties properties = new Properties();
		properties.put("cloneSetId", cloneSetId);
		ArrayList<Field> fields = new ArrayList<Field>();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Relation(Relation.CommonPartAccess), properties);
		for(DataRecord record: records){
			String fieldName = record.getProperty("fieldName");
			String ownerId = record.getProperty("ownerId");
			
			ComplexType type = getClassorInterfacebyId(ownerId);
			Field field = new Field(fieldName, type, null);
			fields.add(field);
		}

		return fields;
	}
	
	public ArrayList<ComplexType> getTypes(String cloneSetId){
		
		Properties properties = new Properties();
		properties.put("cloneSetId", cloneSetId);
		ArrayList<ComplexType> types = new ArrayList<ComplexType>();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Relation(Relation.CommonPartUseType), properties);
		for(DataRecord record: records){
			String typeId = record.getProperty("typeId");
			ComplexType type = getClassorInterfacebyId(typeId);
			types.add(type);
		}

		return types;
	}
	
	public String getMethodId(CloneInstance instance, String counterRelationId){
		
		Properties properties = new Properties();
		properties.put("cloneInstanceId", instance.getId());
		properties.put("counterRelationId", counterRelationId);
		
		String methodId = null;
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Relation(Relation.DiffPartCall), properties);
		for(DataRecord record: records){
			methodId = record.getProperty("methodId");
			return methodId;
		}

		return methodId;
	}
	
	public Field getField(CloneInstance instance, String counterRelationId){
		
		Properties properties = new Properties();
		properties.put("cloneInstanceId", instance.getId());
		properties.put("counterRelationId", counterRelationId);
		
		Field field = null;
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Relation(Relation.DiffPartAccess), properties);
		for(DataRecord record: records){
			String fieldName = record.getProperty("fieldName");
			String ownerId = record.getProperty("ownerId");
			
			ComplexType type = getClassorInterfacebyId(ownerId);
			
			field = new Field(fieldName, type, null);
			
			return field;
		}

		return field;
	}
	
	public ComplexType getType(CloneInstance instance, String counterRelationId){
		
		Properties properties = new Properties();
		properties.put("cloneInstanceId", instance.getId());
		properties.put("counterRelationId", counterRelationId);
		
		ComplexType type = null;
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Relation(Relation.DiffPartUseType), properties);
		for(DataRecord record: records){
			String typeId = record.getProperty("typeId");
			type = getClassorInterfacebyId(typeId);
			
			return type;
		}

		return type;
	}
	
	public PrimiType getPrimiType(CloneInstance instance, String counterRelationId){
		Properties properties = new Properties();
		properties.put("cloneInstanceId", instance.getId());
		properties.put("counterRelationId", counterRelationId);
		
		PrimiType primiType = null;
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Relation(Relation.DiffPartKeyword), properties);
		for(DataRecord record: records){
			String primiTypeName = record.getProperty("keyword");
			primiType = new PrimiType(primiTypeName);
			
			return primiType;
		}
		
		return primiType;
	}
	
	public ArrayList<String> getCounterRelationIds(CloneSet set){
		ArrayList<String> idList = new ArrayList<String>();
		for(CloneInstance instance: set)
			collectCounterRelationIds(instance, idList);
		return idList;
	}
	
	/**
	 * This method is to gain all the counter relation identifiers in a clone instance.
	 * 
	 * According to the DB design, counter relations of access, call and use_type is stored in
	 * a separate relation table while counter relations of refer, define is stored together
	 * with variable and constant table. Therefore, there are two different ways to handle them.
	 * @param instance
	 * @param idList
	 */
	private void collectCounterRelationIds(CloneInstance instance, ArrayList<String> idList){
		Relation[] relations = {
				new Relation(Relation.DiffPartAccess), 
				new Relation(Relation.DiffPartCall), 
				new Relation(Relation.DiffPartUseType),
				new Relation(Relation.DiffPartKeyword)
				};
		Entity[] entities = {
				new Entity(Entity.Variable), 
				new Entity(Entity.Constant)
				};
		
		Properties properties = new Properties();
		properties.put("cloneInstanceId", instance.getId());
		for(int i=0; i<relations.length; i++){
			
			ArrayList<DataRecord> relationRecords = DBOperator.getDataRecords(relations[i], properties);
			for(DataRecord relationRecord: relationRecords){
				String id = relationRecord.getProperty("counterRelationId");
				if(!idList.contains(id) && null != id)
					idList.add(id);
			}
		}
		
		Properties ownerProperties = new Properties();
		ownerProperties.put("ownerId", instance.getId());
		for(int i=0; i<entities.length; i++){
			
			ArrayList<DataRecord> entityRecords = DBOperator.getDataRecords(entities[i], ownerProperties);
			for(DataRecord entityRecord: entityRecords){
				String id = entityRecord.getProperty("counterRelationId");
				if(!idList.contains(id) && null != id)
					idList.add(id);
			}
		}
	}
	
	public WordBag getWordsOfCloneSet(String cloneSetId){
		WordBag wordBag = new WordBag();
		wordBag.addAll(getWordsOfClassInCommonUseType(cloneSetId));
		wordBag.addAll(getWordsOfInterfaceInCommonUseType(cloneSetId));
		wordBag.addAll(getWordsOfFieldsInCommonPartAccess(cloneSetId));
		wordBag.addAll(getWordsOfVariableInCommonPart(cloneSetId));
		wordBag.addAll(getWordsOfConstantInCommonPart(cloneSetId));
		wordBag.addAll(getWordsOfMethodInCommonPartCalls(cloneSetId));
		
		return wordBag;
	}

	public WordBag getWordsOfClassInCommonUseType(String cloneSetId){
		DBTable[] tables = {  new Entity(Entity.CloneSet),
				new Entity(Entity.Class), new Relation(Relation.CommonPartUseType)};
		Properties properties = new Properties();
		properties.put(cloneSetId, "CloneSet.cloneSetId");
		properties.put("CloneSet.cloneSetId", "CommonPartUseType.cloneSetId");
		properties.put("Class.classId", "CommonPartUseType.typeId");
		
		return getSpecificEntityName(tables, "classFullName", properties);
	}
	
	public WordBag getWordsOfInterfaceInCommonUseType(String cloneSetId){
		DBTable[] tables = {  new Entity(Entity.CloneSet),
				new Entity(Entity.Interface), new Relation(Relation.CommonPartUseType)};
		Properties properties = new Properties();
		properties.put(cloneSetId, "CloneSet.cloneSetId");
		properties.put("CloneSet.cloneSetId", "CommonPartUseType.cloneSetId");
		properties.put("Interface.interfaceId", "CommonPartUseType.typeId");
		
		return getSpecificEntityName(tables, "interfaceFullName", properties);
	}
	
	public WordBag getWordsOfFieldsInCommonPartAccess(String cloneSetId){
		DBTable[] tables = { new Entity(Entity.CloneSet),
				new Entity(Entity.Field), new Relation(Relation.CommonPartAccess) };
		Properties properties = new Properties();
		properties.put(cloneSetId, "CloneSet.cloneSetId");
		properties.put("CloneSet.cloneSetId", "CommonPartAccess.cloneSetId");
		properties.put("Field.ownerId", "CommonPartAccess.ownerId");
		properties.put("Field.fieldName", "CommonPartAccess.fieldName");
		
		return getSpecificEntityName(tables, "fieldName", properties);
	}
	
	public WordBag getWordsOfVariableInCommonPart(String cloneSetId){
		DBTable[] tables = { new Entity(Entity.CloneSet),
				new Entity(Entity.Variable) };
		Properties properties = new Properties();
		properties.put(cloneSetId, "CloneSet.cloneSetId");
		properties.put("CloneSet.cloneSetId", "Variable.ownerId");
		
		return getSpecificEntityName(tables, "variableName", properties);
	}
	
	public WordBag getWordsOfConstantInCommonPart(String cloneSetId){
		DBTable[] tables = { new Entity(Entity.CloneSet),
				new Entity(Entity.Constant) };
		Properties properties = new Properties();
		properties.put(cloneSetId, "CloneSet.cloneSetId");
		properties.put("CloneSet.cloneSetId", "Constant.ownerId");
		properties.put("Constant.constantType", "'String'");
		
		return getSpecificEntityName(tables, "constantName", properties);
	}
	
	public WordBag getWordsOfMethodInCommonPartCalls(String cloneSetId) {
		

		DBTable[] tables = { new Entity(Entity.CloneSet),
				new Entity(Entity.Method), new Relation(Relation.CommonPartCall) };
		Properties properties = new Properties();
		
		properties.put(cloneSetId, "CloneSet.cloneSetId");
		properties.put("CloneSet.cloneSetId", "CommonPartCall.cloneSetId");
		properties.put("Method.methodId", "CommonPartCall.methodId");
		return getSpecificEntityName(tables, "methodName", properties);
	}
	
	private WordBag getSpecificEntityName(DBTable[] tables, String columnName, Properties properties){
		WordBag wordBag = new WordBag();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(tables, properties);
		
		for(DataRecord record: records){
			String name = record.getProperty(columnName);
			
			name = name.substring(name.lastIndexOf(".")+1);
			
			String[] words = MinerUtil.splitCamelString(name);
			for (int i = 0; i < words.length; i++) {
				if(words[i].length() > 2)
					wordBag.add(new Word(words[i]));
			}
		}
		
		return wordBag;
	}

	/*public Project getProject(String projectName){
		
		Project project = null;
		
		Properties properties = new Properties();
		properties.put("projectName", projectName);
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Project), properties);
		for(DataRecord record: records){
			String projName = record.getProperty("projectName");
			String programmingLanguage = record.getProperty("programmingLanguage");
			String projectFilePath = record.getProperty("projectFilePath");
			
			project = new Project(projectName, programmingLanguage, projectFilePath);
			
			return project;
		}
		
		return project;
	}*/

	/**
	 * Get a project according to its name.
	 * 
	 * @param projectName
	 * @return
	 */
	private Project getProject(String projectName) {
		Project project = null;
		
		Properties properties = new Properties();
		properties.put("projectName", projectName);
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Project), properties);
		for(DataRecord record: records){
			String projName = record.getProperty("projectName");
			String programmingLanguage = record.getProperty("programmingLanguage");
			String projectFilePath = record.getProperty("projectFilePath");
			
			project = new Project(projName, programmingLanguage, projectFilePath);
			
			return project;
		}
		
		return project;
	}

	/**
	 * Get all the clone sets contained in a project.
	 * 
	 * @param project
	 * @return
	 */
	public CloneSets getPlainCloneSets(Project project) {
		
		CloneSets sets = new CloneSets();
		
		Properties prop = new Properties();
		prop.put("projectName", project.getProjectName());
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.CloneSet), prop);
		for(DataRecord record: records){
			String cloneSetId = record.getProperty("cloneSetId");
			CloneSet set = new CloneSet(cloneSetId, project);
			sets.add(set);
		}
		
		return sets;
	}

	public ArrayList<CloneInstance> getPlainCloneInstances(String cloneSetId) {
		
		Properties properties = new Properties();
		properties.put("cloneSetId", cloneSetId);
		
		ArrayList<CloneInstance> instances = new ArrayList<CloneInstance>();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.CloneInstance), properties);
		for(DataRecord record: records){
			String cloneInstanceId = record.getProperty("cloneInstanceId");
			String residingMethodId = record.getProperty("residingMethodId");
			String fileLocation = record.getProperty("fileLocation");
			String startLineString = record.getProperty("startLine");
			String endLineString = record.getProperty("endLine");
			
			CloneInstance instance = new CloneInstance(cloneInstanceId,
					null, fileLocation, Integer.valueOf(startLineString),
					Integer.valueOf(endLineString));
			instance.setResidingMethod(new Method(residingMethodId));

			instances.add(instance);
		}
		
		return instances;
	}

	/**
	 * Get all the plain classes according to a project.
	 * 
	 * @param id
	 * @param project
	 * @return
	 * @throws Exception 
	 */
	public ClassPool getPlainClasses(Project project) throws Exception {
		
		Properties properties = new Properties();
		//properties.put("classId", id);
		properties.put("projectName", project.getProjectName());
		
		ClassPool classPool = new ClassPool();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Class), properties);
		for(DataRecord record: records){
			String classFullName = record.getProperty("classFullName");
			String classId = record.getProperty("classId");
			String superClassId = record.getProperty("superClassId");
			String outerClassId = record.getProperty("outerClassId");
			
			Class clas = new Class(classId, project, classFullName);
			clas.setSuperClass(new Class(superClassId));
			clas.setOuterClass(new Class(outerClassId));
			
			ArrayList<Method> methods = getPlainMethods(clas, project);
			clas.setMethods(methods);
			ArrayList<Field> fields = getPlainFields(clas, project);
			clas.setFields(fields);
			
			classPool.add(clas);
		}

		return classPool;
	}

	/**
	 * Get all the interfaces according to a project.
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public InterfacePool getPlainInterfaces(Project project)
			throws Exception {
		
		Properties properties = new Properties();
		//properties.put("interfaceId", id);
		properties.put("projectName", project.getProjectName());
		InterfacePool interfacePool = new InterfacePool();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Interface), properties);
		for(DataRecord record: records){
			String interfaceId = record.getProperty("interfaceId");
			String interfaceFullName = record.getProperty("interfaceFullName");

			Interface interf = new Interface(interfaceId, project, interfaceFullName);
			ArrayList<Method> methods = getPlainMethods(interf, project);
			interf.setMethods(methods);
			ArrayList<Field> fields = getPlainFields(interf, project);
			interf.setFields(fields);
			
			interfacePool.add(interf);
		}
		
		return interfacePool;
	}

	private ArrayList<Method> getPlainMethods(ComplexType owner, Project project)
			throws Exception {
		
		Properties properties = new Properties();
		properties.put("ownerId", owner.getId());
		
		ArrayList<Method> methodList = new ArrayList<Method>();
		
		ArrayList<DataRecord> methodRecords = DBOperator.getDataRecords(new Entity(Entity.Method), properties);
		for(DataRecord methodRecord: methodRecords){
			String methodName = methodRecord.getProperty("methodName");
			String methodId = methodRecord.getProperty("methodId");
			String returnType = methodRecord.getProperty("returnType");
			String returnTypeCategory = methodRecord.getProperty("returnTypeCategory");

			VarType vType = transferToVarType(returnType,
					returnTypeCategory, project);

			ArrayList<Variable> parameters = new ArrayList<Variable>();
			Properties paramProperties = new Properties();
			paramProperties.put("methodId", methodId);
			
			ArrayList<DataRecord> paramRecords = DBOperator.getDataRecords(new Entity(Entity.MethodParameter), 
					paramProperties, new String[]{"parameterOrder"});
			
			for(DataRecord paramRecord: paramRecords){
				String parameterType = paramRecord.getProperty("parameterType");
				String parameterName = paramRecord.getProperty("parameterName");
				String parameterTypeCategory = paramRecord.getProperty("parameterTypeCategory");

				VarType paramType = transferToVarType(parameterType,
						parameterTypeCategory, project);
				Variable variable = new Variable(parameterName, paramType, false);
				parameters.add(variable);
			}
			
			Method method = new Method(methodId, owner, methodName, vType,
					parameters);
			methodList.add(method);
		}
		
		return methodList;
	}

	private ArrayList<Field> getPlainFields(ComplexType owner, Project project)
			throws Exception {
		
		Properties properties = new Properties();
		properties.put("ownerId", owner.getId());

		ArrayList<Field> fields = new ArrayList<Field>();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Field), properties);
		for(DataRecord record: records){
			String fieldName = record.getProperty("fieldName");
			String fieldType = record.getProperty("fieldType");
			String fieldTypeCategory = record.getProperty("fieldTypeCategory");

			VarType fType = transferToVarType(fieldType, fieldTypeCategory,
					project);
			// ComplexType owner = getClassorInterfacebyId(ownerId);

			Field field = new Field(fieldName, owner, fType);
			fields.add(field);
		}

		return fields;
	}

	public ArrayList<Variable> getVariables(CloneSet owner, Project project) throws Exception {
		
		Properties properties = new Properties();
		properties.put("ownerId", owner.getId());
		ArrayList<Variable> variables = new ArrayList<Variable>();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Variable), properties);
		for(DataRecord record: records){
			String variableName = record.getProperty("variableName");
			String variableType = record.getProperty("variableType");
			String variableTypeCategory = record.getProperty("variableTypeCategory");
			String useType = record.getProperty("useType");
			
			VarType vaType = transferToVarType(variableType, variableTypeCategory, project);
			VariableUseType useT = VariableUseType.valueOf(useType);
			Variable variable = new Variable(variableName, vaType, useT);
			
			variables.add(variable);
		}

		return variables;
	}
	
	public Variable getVariable(CloneInstance owner, String counterRelationId, Project project) throws Exception{
		
		Properties properties = new Properties();
		properties.put("ownerId", owner.getId());
		properties.put("counterRelationId", counterRelationId);
		Variable variable = null;
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Variable), properties);
		for(DataRecord record: records){
			String variableName = record.getProperty("variableName");
			String variableType = record.getProperty("variableType");
			String variableTypeCategory = record.getProperty("variableTypeCategory");
			String useType = record.getProperty("useType");
			
			VarType vaType = transferToVarType(variableType, variableTypeCategory, project);
			VariableUseType useT = VariableUseType.valueOf(useType);
			variable = new Variable(variableName, vaType, useT);
			
			return variable;
		}
		return variable;
	}
	
	public ArrayList<Constant> getConstants(CloneSet owner){
		
		Properties properties = new Properties();
		properties.put("ownerId", owner.getId());
		ArrayList<Constant> constants = new ArrayList<Constant>();
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Constant), properties);
		for(DataRecord record: records){
			String constantName = record.getProperty("constantName");
			String constantType = record.getProperty("constantType");
			
			Constant constant = new Constant(constantName, new PrimiType(constantType), false);
			constants.add(constant);
		}
		
		return constants;
	}
	
	public Constant getConstant(CloneInstance owner, String counterRelationId){
		
		Properties properties = new Properties();
		properties.put("ownerId", owner.getId());
		properties.put("counterRelationId", counterRelationId);
		Constant constant = null;
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Constant), properties);
		for(DataRecord record: records){
			String constantName = record.getProperty("constantName");
			String constantType = record.getProperty("constantType");
			
			constant = new Constant(constantName, new PrimiType(constantType), false);
			return constant;
		}
		
		return constant;
	}

	private ComplexType getClassorInterfacebyId(String ownerId) {
		ComplexType owner;
		String inclType = ownerId.substring(0, 2);

		if (inclType.equals("cl"))
			owner = new Class(ownerId);
		else
			owner = new Interface(ownerId);

		return owner;
	}

	private VarType transferToVarType(String typeName, String typeCategory,
			Project project) throws Exception {

		VarType vType = null;
		if(null == typeName || null == typeCategory)
			return null;

		if (typeCategory.equals("EnumType"))
			vType = new EnumType(typeName);
		else if (typeCategory.equals("PrimiType"))
			vType = new PrimiType(typeName);
		else if (typeCategory.equals("TypeVariableType"))
			vType = new TypeVariableType(typeName);
		else if (typeCategory.equals("Class"))
			vType = getTheExistingClassorCreateOne(typeName, project);
		else if (typeCategory.equals("Interface"))
			vType = getTheExistingClassorCreateOne(typeName, project);

		if (null == vType)
			throw new Exception("unsupported returnTypeCategory "
					+ typeCategory + " in method transferToVarType");

		return vType;
	}

	/*
	 * public Class getSimpleClass(Class clas) { 
	 * 
	 * try{ Properties classProperties = new Properties(); } }
	 */

	private void storeCloneSet(CloneSet set) {
		Properties setProperties = new Properties();
		setProperties.put("cloneSetId", set.getId());
		setProperties.put("projectName", set.getProject().getProjectName());

		if (!DBOperator.checkIfanObjectExist(new Entity(Entity.CloneSet), setProperties)) {
			DBOperator.insertDataRecord(new Entity(Entity.CloneSet), setProperties);
		}
	}

	public void storeCommonRelation(CloneSet set, ProgrammingElement element)
			throws Exception {
		if (element instanceof Method) {
			Method method = (Method) element;
			storeCommonPartCallRelation(set, method);
		} else if (element instanceof Field) {
			Field field = (Field) element;
			storeCommonPartAccessRelation(set, field);
		} else if (element instanceof Variable) {
			Variable variable = (Variable) element;
			storeVariable(set, variable, null);
		} else if (element instanceof Constant) {
			Constant constant = (Constant) element;
			storeConstant(set, constant, null);
		} else if (element instanceof ComplexType) {
			ComplexType type = (ComplexType) element;
			storeCommonPartUseTypeRelation(set, type);
		} else if (element instanceof PrimiType){
			/**
			 * do nothing
			 */
		} else
			throw new Exception(
					"unexpected programming element is transfered into"
							+ " method storeCommonRelation");

	}

	public void storeDiffRelation(String counterRelationId,
			CloneInstance instance, ProgrammingElement element)
			throws Exception {
		if (element instanceof Method) {
			Method method = (Method) element;
			
			/*if(method.getOwner().getSimpleName().length() == 0)
				System.out.print("");*/
			
			storeDiffPartCallRelation(counterRelationId, instance, method);
		} else if (element instanceof Field) {
			Field field = (Field) element;
			storeDiffPartAccessRelation(counterRelationId, instance, field);
		} else if (element instanceof Variable) {
			Variable variable = (Variable) element;
			storeVariable(instance, variable, counterRelationId);
		} else if (element instanceof Constant) {
			Constant constant = (Constant) element;
			storeConstant(instance, constant, counterRelationId);
		} else if (element instanceof ComplexType) {
			ComplexType type = (ComplexType) element;
			storeDiffPartUseTypeRelation(counterRelationId, instance, type);
		} else if (element instanceof PrimiType) {
			PrimiType type = (PrimiType) element;
			storeDiffPartKeywordRelation(counterRelationId, instance, type);
		} else
			throw new Exception(
					"unexpected programming element is transfered into"
							+ " method storeDiffRelation");
	}

	private void storeDiffPartKeywordRelation(String counterRelationId,
			CloneInstance instance, PrimiType type)  throws Exception {
		
		storeCloneInstanceWithDependency(instance);
		
		Properties relationProperties = new Properties();
		relationProperties.put("cloneInstanceId", instance.getId());
		relationProperties.put("keyword", type.getFullName());
		relationProperties.put("counterRelationId", counterRelationId);

		DBOperator.insertDataRecord(new Relation(Relation.DiffPartKeyword), relationProperties);
		
	}

	private void storeDiffPartCallRelation(String counterRelationId,
			CloneInstance instance, Method method) throws Exception {

		getTheExistingMethodorCreateOne(method);
		storeCloneInstanceWithDependency(instance);

		Properties relationProperties = new Properties();
		relationProperties.put("cloneInstanceId", instance.getId());
		relationProperties.put("methodId", method.getMethodId());
		relationProperties.put("counterRelationId", counterRelationId);

		DBOperator.insertDataRecord(new Relation(Relation.DiffPartCall), relationProperties);
	}

	private void storeDiffPartAccessRelation(String counterRelationId,
			CloneInstance instance, Field field) throws Exception {

		getTheExistingFieldorCreateOne(field);
		storeCloneInstanceWithDependency(instance);

		Properties relationProperties = new Properties();
		relationProperties.put("cloneInstanceId", instance.getId());
		relationProperties.put("fieldName", field.getFieldName());
		relationProperties.put("ownerId", field.getOwnerType().getId());
		relationProperties.put("counterRelationId", counterRelationId);

		DBOperator.insertDataRecord(new Relation(Relation.DiffPartAccess), relationProperties);
	}

	private void storeDiffPartUseTypeRelation(String counterRelationId,
			CloneInstance instance, ComplexType type) throws Exception {
		if (type instanceof Class) {
			Class clas = (Class) type;
			getTheExistingClassorCreateOne(clas.getFullName(),
					clas.getProject());
		} else if (type instanceof Interface) {
			Interface interf = (Interface) type;
			getTheExistingInterfaceorCreateOne(interf.getFullName(),
					interf.getProject());
		} else
			throw new Exception("unexpected complex type " + type.getFullName()
					+ " when calling " + "the storeDiffPartUseTypeRelation");
		storeCloneInstanceWithDependency(instance);

		Properties relationProperties = new Properties();
		relationProperties.put("cloneInstanceId", instance.getId());
		relationProperties.put("typeId", type.getId());
		relationProperties.put("counterRelationId", counterRelationId);

		DBOperator.insertDataRecord(new Relation(Relation.DiffPartUseType), relationProperties);
	}

	private void storeCommonPartCallRelation(CloneSet set, Method method)
			throws Exception {
		storeCloneSet(set);
		getTheExistingMethodorCreateOne(method);

		Properties relationProperties = new Properties();
		relationProperties.put("cloneSetId", set.getId());
		relationProperties.put("methodId", method.getMethodId());

		DBOperator.insertDataRecord(new Relation(Relation.CommonPartCall), relationProperties);
	}

	private void storeCommonPartAccessRelation(CloneSet set, Field field)
			throws Exception {
		storeCloneSet(set);
		getTheExistingFieldorCreateOne(field);

		Properties relationProperties = new Properties();
		relationProperties.put("cloneSetId", set.getId());
		relationProperties.put("fieldName", field.getFieldName());
		relationProperties.put("ownerId", field.getOwnerType().getId());

		DBOperator.insertDataRecord(new Relation(Relation.CommonPartAccess), relationProperties);
	}

	private void storeVariable(RegionalOwner owner, Variable variable,
			String counterRelationId) throws Exception {
		if (owner instanceof CloneSet)
			storeCloneSet((CloneSet) owner);
		else
			// case for clone instance
			storeCloneInstanceWithDependency((CloneInstance) owner);

		Properties variableProperties = new Properties();
		variableProperties.put("variableName", variable.getVariableName());
		variableProperties = putTypeInformation("variable", variableProperties,
				variable.getVariableType());
		variableProperties.put("useType", variable.getUseType().toString());
		variableProperties.put("ownerId", owner.getId());

		if (owner instanceof CloneInstance)
			variableProperties.put("counterRelationId", counterRelationId);
		
		DBOperator.insertDataRecord(new Entity(Entity.Variable), variableProperties);
	}

	private void storeConstant(RegionalOwner owner, Constant constant,
			String counterRelationId) throws Exception {
		if (owner instanceof CloneSet)
			storeCloneSet((CloneSet) owner);
		else
			// case for clone instance
			storeCloneInstanceWithDependency((CloneInstance) owner);

		Properties consProperties = new Properties();
		/*if (constant.getConstantType().toString().equals("String")) {
			constant.setConstantName(constant.getConstantName());
		}*/
		consProperties.put("constantName", constant.getConstantName());
		consProperties.put("constantType", constant.getConstantType()
				.toString());
		consProperties.put("ownerId", owner.getId());

		if (owner instanceof CloneInstance)
			consProperties.put("counterRelationId", counterRelationId);

		DBOperator.insertDataRecord(new Entity(Entity.Constant), consProperties);
	}

	private void storeCommonPartUseTypeRelation(CloneSet set, ComplexType type)
			throws Exception {
		if (type instanceof Class) {
			Class clas = (Class) type;
			getTheExistingClassorCreateOne(clas.getFullName(),
					clas.getProject());
		} else if (type instanceof Interface) {
			Interface interf = (Interface) type;
			getTheExistingInterfaceorCreateOne(interf.getFullName(),
					interf.getProject());
		} else
			throw new Exception("unexpected complex type " + type.getFullName()
					+ " when calling " + "the storeCommonPartUseTypeRelation");

		storeCloneSet(set);

		Properties relationProperties = new Properties();
		relationProperties.put("cloneSetId", set.getId());
		relationProperties.put("typeId", type.getId());

		DBOperator.insertDataRecord(new Relation(Relation.CommonPartUseType), relationProperties);
	}

	private String getCloneInstanceId(Properties instanceProperties)
			throws Exception {
		
		String instanceId = null;
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.CloneInstance), instanceProperties);
		for(DataRecord record: records){
			instanceId = record.getProperty("cloneInstanceId");
			return instanceId;
		}
		
		return instanceId;
	}

	public void storeCloneInstanceWithDependency(CloneInstance instance)
			throws Exception {

		storeCloneSet(instance.getCloneSet());
		getTheExistingMethodorCreateOne(instance.getResidingMethod());

		Properties instanceConProperties = new Properties();
		// instanceConProperties.put("cloneInstanceId",
		// instance.getCloneInstanceId());
		instanceConProperties.put("cloneSetId", instance.getCloneSet().getId());
		instanceConProperties.put("startLine",
				String.valueOf(instance.getStartLine()));
		instanceConProperties.put("endLine",
				String.valueOf(instance.getEndLine()));
		instanceConProperties.put("fileLocation", instance.getFileLocation());

		;

		if (!DBOperator.checkIfanObjectExist(new Entity(Entity.CloneInstance), instanceConProperties)) {
			Properties instanceValProperties = new Properties();
			String instanceId = "ci" + UUID.randomUUID();

			instance.setId(instanceId);
			instanceValProperties.put("cloneInstanceId", instanceId);
			instanceValProperties.put("residingMethodId", instance
					.getResidingMethod().getMethodId());

			instanceConProperties.putAll(instanceValProperties);
			
			DBOperator.insertDataRecord(new Entity(Entity.CloneInstance), instanceConProperties);
		} else {
			String instanceId = getCloneInstanceId(instanceConProperties);
			instance.setId(instanceId);
		}

	}

	public void storeProject(Project project) {
		Properties conProperties = new Properties();
		conProperties.put("projectName", project.getProjectName());

		Properties valProperties = new Properties();
		valProperties.put("programmingLanguage",
				project.getProgrammingLanguage());
		valProperties.put("projectFilePath", project.getProjectFilePath());

		if (DBOperator.checkIfanObjectExist(new Entity(Entity.Project), conProperties))
			DBOperator.updateDataRecord(new Entity(Entity.Project), valProperties, conProperties);
		else {
			conProperties.putAll(valProperties);
			DBOperator.insertDataRecord(new Entity(Entity.Project), conProperties);
		}
	}

	public void storeImplementRelation(clonepedia.model.ontology.Class clas,
			clonepedia.model.ontology.Interface interf) {
		// storeClass(clas);
		// storeInterface(interf);
		getTheExistingClassorCreateOne(clas.getFullName(), clas.getProject());
		getTheExistingInterfaceorCreateOne(interf.getFullName(),
				interf.getProject());
		Properties relationProperties = new Properties();
		relationProperties.put("classId", clas.getId());
		relationProperties.put("interfaceId", interf.getId());

		if (DBOperator.checkIfaRelationExist(new Relation(Relation.ImplementRelation),
				relationProperties))
			DBOperator.updateDataRecord(new Relation(Relation.ImplementRelation), relationProperties, relationProperties);
		else {
			DBOperator.insertDataRecord(new Relation(Relation.ImplementRelation), relationProperties);
		}

	}

	public void storeInterfaceExtendRelation(
			clonepedia.model.ontology.Interface subInterface,
			clonepedia.model.ontology.Interface superInterface) {
		/*
		 * storeInterface(subInterface); storeInterface(superInterface);
		 */
		getTheExistingInterfaceorCreateOne(subInterface.getFullName(),
				subInterface.getProject());
		getTheExistingInterfaceorCreateOne(superInterface.getFullName(),
				superInterface.getProject());

		Properties relationProperties = new Properties();
		relationProperties.put("subInterfaceId", subInterface.getId());
		relationProperties.put("superInterfaceId", superInterface.getId());

		if (DBOperator.checkIfaRelationExist(new Relation(Relation.InterfaceExtendRelation),
				relationProperties))
			DBOperator.updateDataRecord(new Relation(Relation.InterfaceExtendRelation), relationProperties, relationProperties);
		else {
			DBOperator.insertDataRecord(new Relation(Relation.InterfaceExtendRelation), relationProperties);
		}

	}

	public void storeInterfaceWithDependency(clonepedia.model.ontology.Interface interf) {

		getTheExistingInterfaceorCreateOne(interf.getFullName(),
				interf.getProject());

		if (0 != interf.getSuperInterfaces().size()) {
			for (clonepedia.model.ontology.Interface in : interf.getSuperInterfaces()) {
				getTheExistingInterfaceorCreateOne(in.getFullName(),
						in.getProject());
				storeInterfaceExtendRelation(interf, in);
			}
		}

	}

	public void storeClasswithDependency(clonepedia.model.ontology.Class clas) {
		Project project = clas.getProject();

		Properties conClassProperties = new Properties();
		conClassProperties.put("classFullName", clas.getFullName());
		conClassProperties.put("projectName", project.getProjectName());

		Properties valClassProperties = new Properties();
		// valClassProperties.put("classId", clas.getId());

		clonepedia.model.ontology.Class superClass = clas.getSuperClass();
		clonepedia.model.ontology.Class outerClass = clas.getOuterClass();
		if (null != superClass)
			valClassProperties.put("superClassId", superClass.getId());
		if (null != outerClass)
			valClassProperties.put("outerClassId", outerClass.getId());

		if (null != superClass) {
			getTheExistingClassorCreateOne(superClass.getFullName(),
					superClass.getProject());
		}

		if (null != outerClass) {
			getTheExistingClassorCreateOne(outerClass.getFullName(),
					outerClass.getProject());
		}
		
		if (DBOperator.checkIfanObjectExist(new Entity(Entity.Class), conClassProperties)) {
			valClassProperties.putAll(conClassProperties);
			DBOperator.updateDataRecord(new Entity(Entity.Class), valClassProperties, conClassProperties);
		} else {
			String classId = "cl" + UUID.randomUUID();
			valClassProperties.put("classId", classId);
			clas.setId(classId);
			conClassProperties.putAll(valClassProperties);
			DBOperator.insertDataRecord(new Entity(Entity.Class), conClassProperties);
		}

		if (clas.getImplementedInterfaces().size() != 0) {
			for (clonepedia.model.ontology.Interface interf : clas
					.getImplementedInterfaces()) {
				Interface in = getTheExistingInterfaceorCreateOne(
						interf.getFullName(), interf.getProject());
				storeImplementRelation(clas, in);
			}
		}
	}

	public clonepedia.model.ontology.Class getTheExistingClassorCreateOne(
			String classFullName, Project project) {
		Properties conditionalProperties = new Properties();
		conditionalProperties.put("projectName", project.getProjectName());
		conditionalProperties.put("classFullName", classFullName);

		ArrayList<clonepedia.model.ontology.Class> classes = getClass(conditionalProperties);

		if (classes.size() == 0) {
			String classId = "cl" + UUID.randomUUID();
			Class cl = new Class(classId, project, classFullName);
			storeClass(cl);
			return cl;
		}
		else
			return classes.get(0);
	}

	public clonepedia.model.ontology.Interface getTheExistingInterfaceorCreateOne(
			String interfaceFullName, Project project) {
		Properties conditionalProperties = new Properties();
		conditionalProperties.put("projectName", project.getProjectName());
		conditionalProperties.put("interfaceFullName", interfaceFullName);

		ArrayList<Interface> interfaceList = getInterface(conditionalProperties);

		if (interfaceList.size() == 0) {
			String interfaceId = "in" + UUID.randomUUID();
			Interface interf = new Interface(interfaceId, project, interfaceFullName);
			storeInterface(interf);
			return interf;
		}
		else
			return interfaceList.get(0);
	}

	public Method getTheExistingMethodorCreateOne(Method m) throws Exception {
		Method method = getMethod(m);
		if (null == method) {
			String methodId = "me" + UUID.randomUUID();
			m.setMethodId(methodId);
			storeMethodwithDependency(m);
			System.out.print("");
			return m;
		} else
			return method;
	}

	public Field getTheExistingFieldorCreateOne(Field f) throws Exception {
		Field field = getField(f);
		if (null == field) {
			storeFieldwithDependency(f);
		}
		return f;
	}

	private ArrayList<clonepedia.model.ontology.Class> getClass(Properties properties) {
		
		ArrayList<clonepedia.model.ontology.Class> classes = new ArrayList<clonepedia.model.ontology.Class>();
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Class), properties);
		
		for(DataRecord record: records){
			String projectName = record.getProperty("projectName");
			String classFullName = record.getProperty("classFullName");
			String classId = record.getProperty("classId");

			
			clonepedia.model.ontology.Class cl = new clonepedia.model.ontology.Class(classId,
					getProject(projectName), classFullName);
			
			classes.add(cl);
		}
		
		return classes;
	}

	private ArrayList<Interface> getInterface(Properties properties) {
		
		ArrayList<Interface> interfaces = new ArrayList<Interface>();
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Interface), properties);
		
		for(DataRecord record: records){
			String projectName = record.getProperty("projectName");

			String interfaceFullName = record.getProperty("interfaceFullName");
			String interfaceId = record.getProperty("interfaceId");

			clonepedia.model.ontology.Interface interf = new clonepedia.model.ontology.Interface(
					interfaceId, getProject(projectName), interfaceFullName);
			
			interfaces.add(interf);
		}
		
		return interfaces;
	}

	private Field getField(Field field) {
		Properties fieldProperties = new Properties();
		fieldProperties.put("fieldName", field.getFieldName());
		fieldProperties.put("ownerId", field.getOwnerType().getId());
		
		ArrayList<DataRecord> records = DBOperator.getDataRecords(new Entity(Entity.Field), fieldProperties);
		if(records.size() == 0)
			return null;
		else
			return field;
	}

	private Method getMethod(Method method) {
		
		Properties methodProperties = new Properties();
		methodProperties.put("ownerId", method.getOwner().getId());
		methodProperties.put("methodName", method.getMethodName());
		if (method.getReturnType() != null)
			methodProperties.put("returnType", method.getReturnType().toString());

		ArrayList<DataRecord> methodRecords = DBOperator.getDataRecords(new Entity(Entity.Method), methodProperties);
		for(DataRecord methodRecord: methodRecords){
			String methodId = methodRecord.getProperty("methodId");

			Properties paramProperties = new Properties();
			paramProperties.put("methodId", methodId);
			ArrayList<DataRecord> paramRecords = DBOperator.getDataRecords(new Entity(Entity.MethodParameter), paramProperties);
			if (hasTheCorrespondingParameters(method, paramRecords)) {
				method.setMethodId(methodId);
				return method;
			} 
		}
		
		return null;
	}
	
	private boolean hasTheCorrespondingParameters(Method method,
			ArrayList<DataRecord> paramRecords){
		
		if (paramRecords.size() != method.getParameters().size()) {
			return false;
		} else if (paramRecords.size() == 0)
			return true;
		else {
			Variable[] params = method.getParameters().toArray(new Variable[0]);
			
			for(DataRecord paramRecord: paramRecords){
				int parameterOrder = Integer.valueOf(paramRecord.getProperty("parameterOrder"));
				String parameterType = paramRecord.getProperty("parameterType");

				if (!params[parameterOrder - 1].getVariableType().toString()
						.equals(parameterType))
					return false;
			}
			
			return true;
		}
	}

	/**
	 * This method can only be called by
	 * <code>getTheExistingFieldorCreateOne</code>. It is used to create a brand
	 * new field.
	 * 
	 * @param field
	 * @throws Exception
	 */
	private void storeFieldwithDependency(Field field) throws Exception {

		if (field.getOwnerType().isClass())
			storeClasswithDependency((clonepedia.model.ontology.Class) field
					.getOwnerType());
		else
			storeInterfaceWithDependency((Interface) field.getOwnerType());

		Properties fieldProperties = new Properties();
		fieldProperties.put("fieldName", field.getFieldName());
		fieldProperties.put("ownerId", field.getOwnerType().getId());

		Properties typeProperties = new Properties();
		VarType varType = field.getFieldType();
		typeProperties = putTypeInformation("field", typeProperties, varType);
		if (typeProperties == null)
			return;

		fieldProperties.putAll(typeProperties);

		DBOperator.insertDataRecord(new Entity(Entity.Field), fieldProperties);
	}

	/**
	 * This method can only be called by
	 * <code>getTheExistingMethodorCreateOne</code>. It is used to create a
	 * brand new method.
	 * 
	 * @param method
	 * @throws Exception
	 */
	private void storeMethodwithDependency(Method method) throws Exception {

		ComplexType type = method.getOwner();
		
		/*if(type.getSimpleName().length() == 0)
			System.out.print("");*/
		
		if (type.isClass())
			storeClasswithDependency((Class) type);
		else
			storeInterfaceWithDependency((Interface) type);

		Properties methodProperties = new Properties();
		// conMethodProperties.put("methodId", method.getMethodId());

		// Properties valMethodProperties = new Properties();
		methodProperties.put("ownerId", method.getOwner().getId());
		methodProperties.put("methodName", method.getMethodName());

		if (method.getReturnType() != null) {
			methodProperties = putTypeInformation("return", methodProperties,
					method.getReturnType());
			if (methodProperties == null)
				return;
		}

		methodProperties.put("methodId", method.getMethodId());
		// conMethodProperties.putAll(valMethodProperties);
		// System.out.print("");
		DBOperator.insertDataRecord(new Entity(Entity.Method), methodProperties);
		
		storeMethodParameter(method);
	}

	private void storeMethodParameter(Method method) throws Exception {
		Integer order = 1;
		for (Variable parameter : method.getParameters()) {
			Properties paramProperties = new Properties();
			paramProperties.put("methodId", method.getMethodId());
			paramProperties.put("parameterOrder", order.toString());
			paramProperties = putTypeInformation("parameter", paramProperties,
					parameter.getVariableType());

			Properties paramValProperties = new Properties();
			paramValProperties
					.put("parameterName", parameter.getVariableName());

			if (null == paramProperties)
				return;
			
			/**
			 * There is no need to update the record if the record has already
			 * existed.
			 */
			if (!DBOperator.checkIfanObjectExist(new Entity(Entity.MethodParameter), paramProperties)) {
				paramProperties.putAll(paramValProperties);
				DBOperator.insertDataRecord(new Entity(Entity.MethodParameter), paramProperties);
			}
			
			order++;
		}
	}

	/**
	 * This method can only be called by
	 * <code>getTheExistingClassorCreateOne</code> method. It is used to insert
	 * a brand new class.
	 * 
	 * @param clas
	 */
	private void storeClass(clonepedia.model.ontology.Class clas) {
		Project project = clas.getProject();

		Properties conClassProperties = new Properties();
		conClassProperties.put("classFullName", clas.getFullName());
		conClassProperties.put("projectName", project.getProjectName());
		conClassProperties.put("classId", clas.getId());

		DBOperator.insertDataRecord(new Entity(Entity.Class), conClassProperties);
	}

	/**
	 * This method can only be called by
	 * <code>getTheExistingInterfaceorCreateOne</code> method. It is used to
	 * insert a brand new interface.
	 * 
	 * @param interf
	 */
	private void storeInterface(clonepedia.model.ontology.Interface interf) {
		Project project = interf.getProject();

		Properties conInterfaceProperties = new Properties();
		conInterfaceProperties.put("interfaceFullName",
				interf.getFullName());
		conInterfaceProperties.put("projectName", project.getProjectName());
		conInterfaceProperties.put("interfaceId", interf.getId());

		DBOperator.insertDataRecord(new Entity(Entity.Interface), conInterfaceProperties);
	}

	private Properties putTypeInformation(String commonString,
			Properties originalProperties, VarType varType) throws Exception {

		String type = commonString + "Type";
		String typeCategory = commonString + "TypeCategory";

		if (varType instanceof PrimiType) {
			PrimiType primiType = (PrimiType) varType;
			originalProperties.put(type, primiType.getTypeName());
			originalProperties.put(typeCategory, "PrimiType");
		} else if (varType instanceof clonepedia.model.ontology.Class) {
			clonepedia.model.ontology.Class clazz = (clonepedia.model.ontology.Class) varType;
			getTheExistingClassorCreateOne(clazz.getFullName(),
					clazz.getProject());
			originalProperties.put(type, clazz.getFullName());
			originalProperties.put(typeCategory, "Class");
		} else if (varType instanceof clonepedia.model.ontology.Interface) {
			clonepedia.model.ontology.Interface interf = (clonepedia.model.ontology.Interface) varType;
			getTheExistingInterfaceorCreateOne(interf.getFullName(),
					interf.getProject());
			originalProperties.put(type, interf.getFullName());
			originalProperties.put(typeCategory, "Interface");
		} else if (varType instanceof EnumType) {
			EnumType enumType = (EnumType) varType;
			originalProperties.put(type, enumType.getTypeName());
			originalProperties.put(typeCategory, "EnumType");
		} else if (varType instanceof TypeVariableType) {
			TypeVariableType tvType = (TypeVariableType) varType;
			originalProperties.put(type, tvType.getTypeName());
			originalProperties.put(typeCategory, "TypeVariableType");
		} else
			throw new Exception("Unrecoginzed vartype" + varType.toString());

		return originalProperties;
	}
}

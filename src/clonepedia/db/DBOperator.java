package clonepedia.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import clonepedia.db.schema.DBTable;
import clonepedia.db.schema.Entity;
import clonepedia.db.schema.Relation;
import clonepedia.model.db.DataRecord;
import clonepedia.util.MinerUtil;


public class DBOperator{

	/**
	 * 
	 */
	//private static final long serialVersionUID = 6481596782788399487L;
	private static Connection conn = DBManager.getConnection();
	private static PreparedStatement stat = null;

	// private ResultSet rs = null;

	
	


	/**
	 * The objectName is an entity in database
	 * 
	 * @param objectName
	 * @param properties
	 * @return
	 */
	public static boolean checkIfanObjectExist(Entity objectName,
			Properties properties) {
		return checkIfaRecordExistInDB(objectName, properties);
	}

	public static boolean checkIfaRelationExist(Relation relationType,
			Properties properties) {
		return checkIfaRecordExistInDB(relationType, properties);
	}

	public static boolean checkIfaRecordExistInDB(DBTable table, Properties properties) {
		ResultSet rs = null;
		try {
			String sql = generateSelectFromTemplateSQL(new DBTable[]{table}, properties, null);
			stat = conn.prepareStatement(sql);
			rs = stat.executeQuery();

			boolean flag = rs.next();
			rs.close();
			stat.close();

			return flag;

		} catch (SQLException e) {
			closeResultSet(rs);
			closePreparedStatement(stat);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	
	
	public static ArrayList<DataRecord> getDataRecords(DBTable table, Properties conditionProperties){
		return getDataRecords(new DBTable[]{table}, conditionProperties, null);
	}
	
	public static ArrayList<DataRecord> getDataRecords(DBTable table, Properties conditionProperties, String[] orderedColumns){
		return getDataRecords(new DBTable[]{table}, conditionProperties, orderedColumns);
	}
	
	public static ArrayList<DataRecord> getDataRecords(DBTable[] tables, Properties conditionProperties){
		return getDataRecords(tables, conditionProperties, null);
	}
	
	//@SuppressWarnings("unchecked")
	public static ArrayList<DataRecord> getDataRecords(DBTable[] tables, Properties conditionProperties, String[] orderedColumns){
		ArrayList<DataRecord> records = new ArrayList<DataRecord>();
		ResultSet rs = null;
		try {
			String sql;
			sql = generateSelectFromTemplateSQL(tables, conditionProperties, orderedColumns);
	
			stat = conn.prepareStatement(sql);
			rs = stat.executeQuery();
			
			while (rs.next()) {
				DataRecord record = new DataRecord();
				
				ResultSetMetaData rsmd = rs.getMetaData();
				for(int i=1; i<=rsmd.getColumnCount(); i++){
					String columnName = rsmd.getColumnName(i);
					String value = rs.getString(columnName);
					
					if(null != value){
						record.put(columnName, value);
					}
					
				}
				
				records.add(record);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeResultSet(rs);
			closePreparedStatement(stat);
		}
		
		return records;
	}
	
	public static void insertDataRecord(DBTable table, Properties valueProperties){
		String sql = "";
		try {
			sql = generateInsertFromTemplateSQL(table,
					valueProperties);
	
			/**
			 * The following commented code is used to debug.
			 */
			/*if(table.getName().equals("Class")){
				String fullName = valueProperties.getProperty("classFullName");
				if(fullName.equals("org.qii.weiciyuan.ui.actionmenu.FollowTask")){
					System.out.println("here");
				}
			}*/
			
			stat = conn.prepareStatement(sql);
			stat.executeUpdate();
			stat.close();
	
		} catch (SQLException e) {
			closePreparedStatement(stat);
			System.out.println(sql);
			e.printStackTrace();
		}
	}
	
	public static void updateDataRecord(DBTable table, Properties valueProperties, Properties conditionProperites){
		try {
			String sql = generateUpdateFromTemplateSQL(table, valueProperties, conditionProperites);
	
			stat = conn.prepareStatement(sql);
			stat.executeUpdate();
			stat.close();
	
		} catch (SQLException e) {
			closePreparedStatement(stat);
			e.printStackTrace();
		}
	}
	
	public static void deleteDataRecord(DBTable table, Properties properties) {
		try {
			String sql = generateDeleteFromTemplateSQL(table, properties);
			stat = conn.prepareStatement(sql);
			stat.executeUpdate();
			stat.close();
		} catch (SQLException e) {
			closePreparedStatement(stat);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	private static String generateSelectFromTemplateSQL(DBTable[] tables,
			Properties properties, String[] orderedColumns) {
		String sql = "select * from ";
	
		for (int i = 0; i < tables.length; i++) {
			sql += tables[i].getName() + ",";
		}
	
		sql = sql.substring(0, sql.length() - 1);
		sql += " where ";
	
		Enumeration<String> en = (Enumeration<String>) properties.propertyNames();
	
		while (en.hasMoreElements()) {
			String property = en.nextElement();
			String value = properties.getProperty(property);
			
			value = MinerUtil.switchSpecialCharacterForSQL(value);
	
			sql += property + "='" + value + "' and ";
		}
	
		sql = sql.substring(0, sql.length() - 5);
	
		if(null != orderedColumns){
			sql += " order by ";
			for(String orderedColumn: orderedColumns){
				sql += orderedColumn + ", ";
			}
			sql = sql.substring(0, sql.length()-2);
		}
		
		return sql;
	}

	@SuppressWarnings("unchecked")
	private static String generateDeleteFromTemplateSQL(DBTable table,
			Properties properties) {
		String sql = "delete * from ";
		
		sql += table.getName() + " where ";
		Enumeration<String> en = (Enumeration<String>) properties
				.propertyNames();

		while (en.hasMoreElements()) {
			String property = en.nextElement();
			String value = properties.getProperty(property);
			
			value = MinerUtil.switchSpecialCharacterForSQL(value);

			sql += property + "='" + value + "' and ";
		}

		sql = sql.substring(0, sql.length() - 5);

		return sql;
	}

	@SuppressWarnings("unchecked")
	private static String generateInsertFromTemplateSQL(DBTable table,
			Properties valueProperties) {
		String sql1 = "insert into " + table.getName() + "(";
		String sql2 = ") value(";
		String sql3 = ")";
		Enumeration<String> valueEn = (Enumeration<String>) valueProperties
				.propertyNames();
		while (valueEn.hasMoreElements()) {
			String valueProp = valueEn.nextElement();
			String valueVal = valueProperties.getProperty(valueProp);
			
			valueVal = MinerUtil.switchSpecialCharacterForSQL(valueVal);

			sql1 += valueProp + ", ";
			sql2 += "'" + valueVal + "', ";
		}

		sql1 = sql1.substring(0, sql1.length() - 2);
		sql2 = sql2.substring(0, sql2.length() - 2);

		return sql1 + sql2 + sql3;
	}

	@SuppressWarnings("unchecked")
	private static String generateUpdateFromTemplateSQL(DBTable table,
			Properties valueProperties, Properties conditionalProperties) {
		String sql = "update " + table.toString() + " set ";
		Enumeration<String> valueEn = (Enumeration<String>) valueProperties
				.propertyNames();
		while (valueEn.hasMoreElements()) {
			String valueProp = valueEn.nextElement();
			String valueVal = valueProperties.getProperty(valueProp);
			
			valueVal = MinerUtil.switchSpecialCharacterForSQL(valueVal);
			
			sql += valueProp + "='" + valueVal + "', ";
		}

		sql = sql.substring(0, sql.length() - 2);
		sql += " where ";

		Enumeration<String> conditionalEn = (Enumeration<String>) conditionalProperties
				.propertyNames();
		while (conditionalEn.hasMoreElements()) {
			String conditionalProp = conditionalEn.nextElement();
			String conditionalVal = conditionalProperties
					.getProperty(conditionalProp);
			sql += conditionalProp + "='" + conditionalVal + "' and ";
		}

		sql = sql.substring(0, sql.length() - 5);

		return sql;
	}

	

	private static void closePreparedStatement(PreparedStatement stat) {
		if (null != stat) {
			try {
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private static void closeResultSet(ResultSet rs) {
		if (null != rs) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*public boolean checkIfaRecordExistInDB(Relation r, Properties properties) {
	ResultSet rs = null;
	try {
		String sql = generateSelectfromTemplateSQL(r, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();

		boolean flag = rs.next();
		rs.close();
		stat.close();

		return flag;

	} catch (SQLException e) {
		closeResultSet(rs);
		closePreparedStatement(stat);
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	}

	return false;
}*/

// private ResultSet rs = null;

/*
 * public void delete(Entity en, Properties properties) { try { String sql =
 * generateSelorDelfromTemplateSQL(DELETE, en, properties); stat =
 * conn.prepareStatement(sql); stat.executeUpdate(); stat.close(); } catch
 * (SQLException e) { closePreparedStatement(stat); e.printStackTrace(); }
 * catch (Exception e) { e.printStackTrace(); }
 * 
 * }
 */

/*public ArrayList<String> getExtendedInterfaceIds(String interfaceId){
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("subInterfaceId", interfaceId);
	ArrayList<String> idList = new ArrayList<String>();
	try{
		String sql = generateSelectfromTemplateSQL(Relation.InterfaceExtendRelation, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		while(rs.next()){
			String id = rs.getString("superInterfaceId");
			idList.add(id);
		}
		rs.close();
		stat.close();
		return idList;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public ArrayList<String> getImplementedInterfaceIds(String classId){
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("classId", classId);
	ArrayList<String> idList = new ArrayList<String>();
	try{
		String sql = generateSelectfromTemplateSQL(Relation.ImplementRelation, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		while(rs.next()){
			String id = rs.getString("interfaceId");
			idList.add(id);
		}
		rs.close();
		stat.close();
		return idList;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public ArrayList<String> getMethodIds(String cloneSetId){
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("cloneSetId", cloneSetId);
	ArrayList<String> idList = new ArrayList<String>();
	try{
		String sql = generateSelectfromTemplateSQL(Relation.CommonPartCall, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		while(rs.next()){
			String methodId = rs.getString("methodId");
			idList.add(methodId);
		}
		
		rs.close();
		stat.close();
		return idList;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public ArrayList<Field> getFields(String cloneSetId){
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("cloneSetId", cloneSetId);
	ArrayList<Field> fields = new ArrayList<Field>();
	try{
		String sql = generateSelectfromTemplateSQL(Relation.CommonPartAccess, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		while(rs.next()){
			String fieldName = rs.getString("fieldName");
			String ownerId = rs.getString("ownerId");
			
			ComplexType type = getClassorInterfacebyId(ownerId);
			Field field = new Field(fieldName, type, null);
			fields.add(field);
		}
		
		rs.close();
		stat.close();
		return fields;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public ArrayList<ComplexType> getTypes(String cloneSetId){
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("cloneSetId", cloneSetId);
	ArrayList<ComplexType> types = new ArrayList<ComplexType>();
	try{
		String sql = generateSelectfromTemplateSQL(Relation.CommonPartUseType, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		while(rs.next()){
			
			String typeId = rs.getString("typeId");
			
			ComplexType type = getClassorInterfacebyId(typeId);
			types.add(type);
		}
		
		rs.close();
		stat.close();
		return types;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public String getMethodId(CloneInstance instance, String counterRelationId){
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("cloneInstanceId", instance.getId());
	properties.put("counterRelationId", counterRelationId);
	try{
		String sql = generateSelectfromTemplateSQL(Relation.DiffPartCall, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		String methodId = null;
		if(rs.next()){
			methodId = rs.getString("methodId");
		}
		rs.close();
		stat.close();
		return methodId;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public Field getField(CloneInstance instance, String counterRelationId){
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("cloneInstanceId", instance.getId());
	properties.put("counterRelationId", counterRelationId);
	try{
		String sql = generateSelectfromTemplateSQL(Relation.DiffPartAccess, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		Field field = null;
		if(rs.next()){
			String fieldName = rs.getString("fieldName");
			String ownerId = rs.getString("ownerId");
			
			ComplexType type = getClassorInterfacebyId(ownerId);
			
			field = new Field(fieldName, type, null);
		}
		rs.close();
		stat.close();
		return field;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public ComplexType getType(CloneInstance instance, String counterRelationId){
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("cloneInstanceId", instance.getId());
	properties.put("counterRelationId", counterRelationId);
	try{
		String sql = generateSelectfromTemplateSQL(Relation.DiffPartUseType, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		ComplexType type = null;
		if(rs.next()){
			
			String typeId = rs.getString("typeId");
			
			type = getClassorInterfacebyId(typeId);
		}
		rs.close();
		stat.close();
		return type;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public ArrayList<String> getCounterRelationIds(CloneSet set){
	ArrayList<String> idList = new ArrayList<String>();
	for(CloneInstance instance: set)
		collectCounterRelationIds(instance, idList);
	return idList;
}

private void collectCounterRelationIds(CloneInstance instance, ArrayList<String> idList){
	ResultSet relationRs = null;
	ResultSet entityRs = null;
	
	
	Relation[] relations = {Relation.DiffPartAccess, Relation.DiffPartCall, Relation.DiffPartUseType};
	Entity[] entities = {Entity.Variable, Entity.Constant};
	
	try{
		Properties properties = new Properties();
		properties.put("cloneInstanceId", instance.getId());
		for(int i=0; i<relations.length; i++){
			String relationSQL = generateSelectfromTemplateSQL(relations[i], properties);
			stat = conn.prepareStatement(relationSQL);
			relationRs = stat.executeQuery();
			while(relationRs.next()){
				String id = relationRs.getString("counterRelationId");
				if(!idList.contains(id) && null != id)
					idList.add(id);
			}
			
			relationRs.close();
		}
		
		Properties ownerProperties = new Properties();
		ownerProperties.put("ownerId", instance.getId());
		for(int i=0; i<entities.length; i++){
			String entitySQL = generateSelectfromTemplateSQL(entities[i], ownerProperties);
			stat = conn.prepareStatement(entitySQL);
			entityRs = stat.executeQuery();
			while(entityRs.next()){
				String id = entityRs.getString("counterRelationId");
				if(null==id){
					System.out.print("");
				}
				
				if(!idList.contains(id))
					idList.add(id);
			}
			
			entityRs.close();
		}
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(relationRs);
		closeResultSet(entityRs);
		closePreparedStatement(stat);
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
	String[] tables = { Entity.CloneSet.toString(),
			Entity.Class.toString(), Relation.CommonPartUseType.toString() };
	Properties properties = new Properties();
	properties.put(cloneSetId, "CloneSet.cloneSetId");
	properties.put("CloneSet.cloneSetId", "CommonPartUseType.cloneSetId");
	properties.put("Class.classId", "CommonPartUseType.typeId");
	
	return getSpecificEntityName(tables, "classFullName", properties);
}

public WordBag getWordsOfInterfaceInCommonUseType(String cloneSetId){
	String[] tables = { Entity.CloneSet.toString(),
			Entity.Interface.toString(), Relation.CommonPartUseType.toString() };
	Properties properties = new Properties();
	properties.put(cloneSetId, "CloneSet.cloneSetId");
	properties.put("CloneSet.cloneSetId", "CommonPartUseType.cloneSetId");
	properties.put("Interface.interfaceId", "CommonPartUseType.typeId");
	
	return getSpecificEntityName(tables, "interfaceFullName", properties);
}

public WordBag getWordsOfFieldsInCommonPartAccess(String cloneSetId){
	String[] tables = { Entity.CloneSet.toString(),
			Entity.Field.toString(), Relation.CommonPartAccess.toString() };
	Properties properties = new Properties();
	properties.put(cloneSetId, "CloneSet.cloneSetId");
	properties.put("CloneSet.cloneSetId", "CommonPartAccess.cloneSetId");
	properties.put("Field.ownerId", "CommonPartAccess.ownerId");
	properties.put("Field.fieldName", "CommonPartAccess.fieldName");
	
	return getSpecificEntityName(tables, "fieldName", properties);
}

public WordBag getWordsOfVariableInCommonPart(String cloneSetId){
	String[] tables = { Entity.CloneSet.toString(),
			Entity.Variable.toString() };
	Properties properties = new Properties();
	properties.put(cloneSetId, "CloneSet.cloneSetId");
	properties.put("CloneSet.cloneSetId", "Variable.ownerId");
	
	return getSpecificEntityName(tables, "variableName", properties);
}

public WordBag getWordsOfConstantInCommonPart(String cloneSetId){
	String[] tables = { Entity.CloneSet.toString(),
			Entity.Constant.toString() };
	Properties properties = new Properties();
	properties.put(cloneSetId, "CloneSet.cloneSetId");
	properties.put("CloneSet.cloneSetId", "Constant.ownerId");
	properties.put("Constant.constantType", "'String'");
	
	return getSpecificEntityName(tables, "constantName", properties);
}

public WordBag getWordsOfMethodInCommonPartCalls(String cloneSetId) {
	

	String[] tables = { Entity.CloneSet.toString(),
			Entity.Method.toString(), Relation.CommonPartCall.toString() };
	Properties properties = new Properties();
	
	properties.put(cloneSetId, "CloneSet.cloneSetId");
	properties.put("CloneSet.cloneSetId", "CommonPartCall.cloneSetId");
	properties.put("Method.methodId", "CommonPartCall.methodId");

	

	return getSpecificEntityName(tables, "methodName", properties);
}

private WordBag getSpecificEntityName(String[] tables, String columnName, Properties properties){
	WordBag wordBag = new WordBag();
	ResultSet rs = null;
	
	String sql = generateCascadeSelectfromTemplateSQL(tables, properties);
	try {
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		while (rs.next()) {
			String name = rs.getString(columnName);
			
			name = name.substring(name.lastIndexOf(".")+1);
			
			String[] words = MinerUtil.splitCamelString(name);
			for (int i = 0; i < words.length; i++) {
				if(words[i].length() > 2)
					wordBag.add(new Word(words[i]));
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return wordBag;
}

public Project getProject(Properties properties) throws Exception {
	ResultSet rs = null;
	try {
		String sql = generateSelectfromTemplateSQL(Entity.Project,
				properties);

		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();

		if (rs.next()) {
			Project project = new Project(rs.getString("projectName"),
					rs.getString("programmingLanguage"),
					rs.getString("projectFilePath"));

			closeResultSet(rs);
			closePreparedStatement(stat);
			return project;
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

*//**
 * Get a project according to its name.
 * 
 * @param projectName
 * @return
 *//*
public Project getProject(String projectName) {
	ResultSet rs = null;
	Properties prop = new Properties();
	prop.put("projectName", projectName);
	try {
		Project project = null;

		String sql = generateSelectfromTemplateSQL(Entity.Project, prop);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		if (rs.next()) {
			String pName = rs.getString("projectName");
			String programmingLanguage = rs
					.getString("programmingLanguage");
			String projectFilePath = rs.getString("projectFilePath");

			project = new Project(pName, programmingLanguage,
					projectFilePath);
		}

		rs.close();
		stat.close();
		return project;
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

*//**
 * Get all the clone sets contained in a project.
 * 
 * @param project
 * @return
 *//*
public CloneSets getPlainCloneSets(Project project) {
	ResultSet rs = null;
	Properties prop = new Properties();
	prop.put("projectName", project.getProjectName());
	try {
		CloneSets sets = new CloneSets();
		String sql = generateSelectfromTemplateSQL(Entity.CloneSet, prop);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		while (rs.next()) {
			String cloneSetId = rs.getString("cloneSetId");
			CloneSet set = new CloneSet(cloneSetId, project);
			sets.add(set);
		}

		rs.close();
		stat.close();
		return sets;
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public ArrayList<CloneInstance> getPlainCloneInstances(String cloneSetId) {
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("cloneSetId", cloneSetId);
	try {
		ArrayList<CloneInstance> instances = new ArrayList<CloneInstance>();

		String sql = generateSelectfromTemplateSQL(Entity.CloneInstance,
				properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		while (rs.next()) {
			String cloneInstanceId = rs.getString("cloneInstanceId");
			String residingMethodId = rs.getString("residingMethodId");
			String fileLocation = rs.getString("fileLocation");
			String startLineString = rs.getString("startLine");
			String endLineString = rs.getString("endLine");

			CloneInstance instance = new CloneInstance(cloneInstanceId,
					null, fileLocation, Integer.valueOf(startLineString),
					Integer.valueOf(endLineString));
			instance.setResidingMethod(new Method(residingMethodId));

			instances.add(instance);
		}

		rs.close();
		stat.close();
		return instances;
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

*//**
 * Get all the plain classes according to a project.
 * 
 * @param id
 * @param project
 * @return
 * @throws Exception 
 *//*
public ClassPool getPlainClasses(Project project) throws Exception {
	ResultSet rs = null;
	Properties properties = new Properties();
	//properties.put("classId", id);
	properties.put("projectName", project.getProjectName());
	
	ClassPool classPool = new ClassPool();
	try {
		String sql = generateSelectfromTemplateSQL(Entity.Class, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();

		while (rs.next()) {
			String classFullName = rs.getString("classFullName");
			String classId = rs.getString("classId");
			String superClassId = rs.getString("superClassId");
			String outerClassId = rs.getString("outerClassId");
			
			Class clas = new Class(classId, project, classFullName);
			clas.setSuperClass(new Class(superClassId));
			clas.setOuterClass(new Class(outerClassId));
			
			ArrayList<Method> methods = getPlainMethods(clas, project);
			clas.setMethods(methods);
			ArrayList<Field> fields = getPlainFields(clas, project);
			clas.setFields(fields);
			
			classPool.add(clas);
		}

		rs.close();
		stat.close();
		return classPool;
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;

}

*//**
 * Get all the interfaces according to a project.
 * 
 * @param id
 * @return
 * @throws Exception
 *//*
public InterfacePool getPlainInterfaces(Project project)
		throws Exception {
	ResultSet rs = null;
	Properties properties = new Properties();
	//properties.put("interfaceId", id);
	properties.put("projectName", project.getProjectName());
	InterfacePool interfacePool = new InterfacePool();
	try {

		String sql = generateSelectfromTemplateSQL(Entity.Interface,
				properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		while (rs.next()) {
			String interfaceId = rs.getString("interfaceId");
			String interfaceFullName = rs.getString("interfaceFullName");

			Interface interf = new Interface(interfaceId, project, interfaceFullName);
			ArrayList<Method> methods = getPlainMethods(interf, project);
			interf.setMethods(methods);
			ArrayList<Field> fields = getPlainFields(interf, project);
			interf.setFields(fields);
			
			interfacePool.add(interf);
		}
		rs.close();
		stat.close();
		return interfacePool;
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

private ArrayList<Method> getPlainMethods(ComplexType owner, Project project)
		throws Exception {
	ResultSet rs = null;
	ResultSet paramRs = null;
	Properties properties = new Properties();
	properties.put("ownerId", owner.getId());
	try {
		String methodSQL = generateSelectfromTemplateSQL(Entity.Method,
				properties);
		stat = conn.prepareStatement(methodSQL);
		rs = stat.executeQuery();

		ArrayList<Method> methodList = new ArrayList<Method>();

		while (rs.next()) {
			String methodName = rs.getString("methodName");
			String methodId = rs.getString("methodId");
			String returnType = rs.getString("returnType");
			String returnTypeCategory = rs.getString("returnTypeCategory");

			VarType vType = transferToVarType(returnType,
					returnTypeCategory, project);

			ArrayList<Variable> parameters = new ArrayList<Variable>();
			
			Properties paramProperties = new Properties();
			paramProperties.put("methodId", methodId);
			
			String paramSQL = generateSelectfromTemplateSQL(
					Entity.MethodParameter, paramProperties);
			paramSQL += " order by parameterOrder";
			stat = conn.prepareStatement(paramSQL);
			paramRs = stat.executeQuery();
			while (paramRs.next()) {
				String parameterType = paramRs.getString("parameterType");
				String parameterName = paramRs.getString("parameterName");
				String parameterTypeCategory = paramRs
						.getString("parameterTypeCategory");

				VarType paramType = transferToVarType(parameterType,
						parameterTypeCategory, project);
				Variable variable = new Variable(parameterName, paramType, false);
				parameters.add(variable);
			}

			// ComplexType owner = getClassorInterfacebyId(ownerId);

			Method method = new Method(methodId, owner, methodName, vType,
					parameters);
			methodList.add(method);
			
			paramRs.close();
		}
		
		rs.close();
		stat.close();

		return methodList;
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

private ArrayList<Field> getPlainFields(ComplexType owner, Project project)
		throws Exception {
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("ownerId", owner.getId());

	ArrayList<Field> fields = new ArrayList<Field>();

	try {
		String sql = generateSelectfromTemplateSQL(Entity.Field, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();

		while (rs.next()) {
			String fieldName = rs.getString("fieldName");
			String fieldType = rs.getString("fieldType");
			String fieldTypeCategory = rs.getString("fieldTypeCategory");

			VarType fType = transferToVarType(fieldType, fieldTypeCategory,
					project);
			// ComplexType owner = getClassorInterfacebyId(ownerId);

			Field field = new Field(fieldName, owner, fType);
			fields.add(field);
		}

		rs.close();
		stat.close();
		return fields;
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}

	return null;
}

public ArrayList<Variable> getVariables(CloneSet owner, Project project) throws Exception {
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("ownerId", owner.getId());
	ArrayList<Variable> variables = new ArrayList<Variable>();
	try{
		String sql = generateSelectfromTemplateSQL(Entity.Variable, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		
		while(rs.next()){
			String variableName = rs.getString("variableName");
			String variableType = rs.getString("variableType");
			String variableTypeCategory = rs.getString("variableTypeCategory");
			String useType = rs.getString("useType");
			
			VarType vaType = transferToVarType(variableType, variableTypeCategory, project);
			VariableUseType useT = VariableUseType.valueOf(useType);
			Variable variable = new Variable(variableName, vaType, useT);
			
			variables.add(variable);
		}
		
		rs.close();
		stat.close();
		return variables;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public Variable getVariable(CloneInstance owner, String counterRelationId, Project project) throws Exception{
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("ownerId", owner.getId());
	properties.put("counterRelationId", counterRelationId);
	Variable variable = null;
	try{
		String sql = generateSelectfromTemplateSQL(Entity.Variable, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		if(rs.next()){
			String variableName = rs.getString("variableName");
			String variableType = rs.getString("variableType");
			String variableTypeCategory = rs.getString("variableTypeCategory");
			String useType = rs.getString("useType");
			
			VarType vaType = transferToVarType(variableType, variableTypeCategory, project);
			VariableUseType useT = VariableUseType.valueOf(useType);
			variable = new Variable(variableName, vaType, useT);
			
		}
		rs.close();
		stat.close();
		return variable;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public ArrayList<Constant> getConstants(CloneSet owner){
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("ownerId", owner.getId());
	ArrayList<Constant> constants = new ArrayList<Constant>();
	try{
		String sql = generateSelectfromTemplateSQL(Entity.Constant, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		
		while(rs.next()){
			String constantName = rs.getString("constantName");
			String constantType = rs.getString("constantType");
			
			Constant constant = new Constant(constantName, new PrimiType(constantType), false);
			constants.add(constant);
		}
		
		rs.close();
		stat.close();
		return constants;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

public Constant getConstant(CloneInstance owner, String counterRelationId){
	ResultSet rs = null;
	Properties properties = new Properties();
	properties.put("ownerId", owner.getId());
	properties.put("counterRelationId", counterRelationId);
	Constant constant = null;
	try{
		String sql = generateSelectfromTemplateSQL(Entity.Constant, properties);
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		
		while(rs.next()){
			String constantName = rs.getString("constantName");
			String constantType = rs.getString("constantType");
			
			constant = new Constant(constantName, new PrimiType(constantType), false);
			
		}
		
		rs.close();
		stat.close();
		return constant;
	}
	catch(SQLException e){
		e.printStackTrace();
	}
	finally{
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
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


 * public Class getSimpleClass(Class clas) { ResultSet rs = null;
 * 
 * try{ Properties classProperties = new Properties(); } }


private void storeCloneSet(CloneSet set) {
	Properties setProperties = new Properties();
	setProperties.put("cloneSetId", set.getId());
	setProperties.put("projectName", set.getProject().getProjectName());

	String sql = "";

	if (!checkIfanObjectExist(Entity.CloneSet, setProperties)) {
		sql = generateInsertfromTemplateSQL(Entity.CloneSet, setProperties);
		try {
			stat = conn.prepareStatement(sql);
			stat.executeUpdate();

			stat.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closePreparedStatement(stat);
		}
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
		
		if(method.getOwner().getSimpleName().length() == 0)
			System.out.print("");
		
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
	} else
		throw new Exception(
				"unexpected programming element is transfered into"
						+ " method storeDiffRelation");
}

private void storeDiffPartCallRelation(String counterRelationId,
		CloneInstance instance, Method method) throws Exception {

	getTheExistingMethodorCreateOne(method);
	storeCloneInstanceWithDependency(instance);

	Properties relationProperties = new Properties();
	relationProperties.put("cloneInstanceId", instance.getId());
	relationProperties.put("methodId", method.getMethodId());
	relationProperties.put("counterRelationId", counterRelationId);

	String sql = generateInsertfromTemplateSQL(Relation.DiffPartCall,
			relationProperties);
	try {
		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closePreparedStatement(stat);
	}
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

	String sql = generateInsertfromTemplateSQL(Relation.DiffPartAccess,
			relationProperties);
	try {
		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closePreparedStatement(stat);
	}
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

	String sql = generateInsertfromTemplateSQL(Relation.DiffPartUseType,
			relationProperties);
	try {
		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closePreparedStatement(stat);
	}
}

private void storeCommonPartCallRelation(CloneSet set, Method method)
		throws Exception {
	storeCloneSet(set);
	getTheExistingMethodorCreateOne(method);

	Properties relationProperties = new Properties();
	relationProperties.put("cloneSetId", set.getId());
	relationProperties.put("methodId", method.getMethodId());

	String sql = generateInsertfromTemplateSQL(Relation.CommonPartCall,
			relationProperties);

	try {
		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closePreparedStatement(stat);
	}
}

private void storeCommonPartAccessRelation(CloneSet set, Field field)
		throws Exception {
	storeCloneSet(set);
	getTheExistingFieldorCreateOne(field);

	Properties relationProperties = new Properties();
	relationProperties.put("cloneSetId", set.getId());
	relationProperties.put("fieldName", field.getFieldName());
	relationProperties.put("ownerId", field.getOwnerType().getId());

	String sql = generateInsertfromTemplateSQL(Relation.CommonPartAccess,
			relationProperties);

	try {
		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closePreparedStatement(stat);
	}
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

	String sql = generateInsertfromTemplateSQL(Entity.Variable,
			variableProperties);
	try {
		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();
	} catch (SQLException ex) {
		ex.printStackTrace();
	} finally {
		closePreparedStatement(stat);
	}

}

private void storeConstant(RegionalOwner owner, Constant constant,
		String counterRelationId) throws Exception {
	if (owner instanceof CloneSet)
		storeCloneSet((CloneSet) owner);
	else
		// case for clone instance
		storeCloneInstanceWithDependency((CloneInstance) owner);

	Properties consProperties = new Properties();
	if (constant.getConstantType().toString().equals("String")) {
		constant.setConstantName(MinerUtil
				.filterSpecialCharacter(constant.getConstantName()));
	}
	consProperties.put("constantName", constant.getConstantName());
	consProperties.put("constantType", constant.getConstantType()
			.toString());
	consProperties.put("ownerId", owner.getId());

	if (owner instanceof CloneInstance)
		consProperties.put("counterRelationId", counterRelationId);

	String sql = generateInsertfromTemplateSQL(Entity.Constant,
			consProperties);
	try {
		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();
	} catch (SQLException ex) {
		ex.printStackTrace();
	} finally {
		closePreparedStatement(stat);
	}
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

	String sql = generateInsertfromTemplateSQL(Relation.CommonPartUseType,
			relationProperties);

	try {
		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closePreparedStatement(stat);
	}
}

private String getCloneInstanceId(Properties instanceProperties)
		throws Exception {
	ResultSet rs = null;
	String sql = generateSelectfromTemplateSQL(Entity.CloneInstance,
			instanceProperties);
	try {
		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		if (rs.next()) {
			String id = rs.getString("cloneInstanceId");
			rs.close();
			stat.close();
			return id;
		} else {
			rs.close();
			stat.close();
			return null;
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
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
	instanceConProperties.put("fileLocation", instance.getFileLocation()
			.replace("\\", "\\\\"));

	;

	String sql = "";
	if (!checkIfanObjectExist(Entity.CloneInstance, instanceConProperties)) {
		Properties instanceValProperties = new Properties();
		String instanceId = "ci" + UUID.randomUUID();

		instance.setId(instanceId);
		instanceValProperties.put("cloneInstanceId", instanceId);
		instanceValProperties.put("residingMethodId", instance
				.getResidingMethod().getMethodId());

		instanceConProperties.putAll(instanceValProperties);
		sql = generateInsertfromTemplateSQL(Entity.CloneInstance,
				instanceConProperties);

		try {
			stat = conn.prepareStatement(sql);
			stat.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closePreparedStatement(stat);
		}
	} else {
		String instanceId = getCloneInstanceId(instanceConProperties);
		instance.setId(instanceId);
	}

}


 * public Class getSimpleClass(Class clas) { ResultSet rs = null;
 * 
 * try{ Properties classProperties = new Properties(); } }


public void storeProject(Project project) {
	try {
		Properties conProperties = new Properties();
		conProperties.put("projectName", project.getProjectName());

		Properties valProperties = new Properties();
		valProperties.put("programmingLanguage",
				project.getProgrammingLanguage());
		valProperties.put("projectFilePath", project.getProjectFilePath());

		String sql = "";

		if (checkIfanObjectExist(Entity.Project, conProperties))
			sql = generateUpdatefromTemplateSQL(Entity.Project,
					valProperties, conProperties);
		else {
			conProperties.putAll(valProperties);
			sql = generateInsertfromTemplateSQL(Entity.Project,
					conProperties);
		}

		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();

	} catch (SQLException e) {
		closePreparedStatement(stat);
		e.printStackTrace();
	}
}

public void storeImplementRelation(clonepedia.model.ontology.Class clas,
		clonepedia.model.ontology.Interface interf) {
	// storeClass(clas);
	// storeInterface(interf);
	getTheExistingClassorCreateOne(clas.getFullName(), clas.getProject());
	getTheExistingInterfaceorCreateOne(interf.getFullName(),
			interf.getProject());
	try {
		Properties relationProperties = new Properties();
		relationProperties.put("classId", clas.getId());
		relationProperties.put("interfaceId", interf.getId());

		String sql = "";

		if (checkIfaRelationExist(Relation.ImplementRelation,
				relationProperties))
			sql = generateUpdatefromTemplateSQL(Relation.ImplementRelation,
					relationProperties, relationProperties);
		else {
			sql = generateInsertfromTemplateSQL(Relation.ImplementRelation,
					relationProperties);
		}

		stat = conn.prepareStatement(sql);

		stat.executeUpdate();
		stat.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}

	closePreparedStatement(stat);

}

public void storeInterfaceExtendRelation(
		clonepedia.model.ontology.Interface subInterface,
		clonepedia.model.ontology.Interface superInterface) {
	
	 * storeInterface(subInterface); storeInterface(superInterface);
	 
	getTheExistingInterfaceorCreateOne(subInterface.getFullName(),
			subInterface.getProject());
	getTheExistingInterfaceorCreateOne(superInterface.getFullName(),
			superInterface.getProject());

	try {
		Properties relationProperties = new Properties();
		relationProperties.put("subInterfaceId", subInterface.getId());
		relationProperties.put("superInterfaceId", superInterface.getId());

		String sql = "";

		if (checkIfaRelationExist(Relation.InterfaceExtendRelation,
				relationProperties))
			sql = generateUpdatefromTemplateSQL(
					Relation.InterfaceExtendRelation, relationProperties,
					relationProperties);
		else {
			sql = generateInsertfromTemplateSQL(
					Relation.InterfaceExtendRelation, relationProperties);
		}

		stat = conn.prepareStatement(sql);

		stat.executeUpdate();
		stat.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}

	closePreparedStatement(stat);

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

	try {
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

		String sql = "";

		if (checkIfanObjectExist(Entity.Class, conClassProperties)) {
			valClassProperties.putAll(conClassProperties);
			sql = generateUpdatefromTemplateSQL(Entity.Class,
					valClassProperties, conClassProperties);
		} else {
			String classId = "cl" + UUID.randomUUID();
			valClassProperties.put("classId", classId);
			clas.setId(classId);
			conClassProperties.putAll(valClassProperties);
			sql = generateInsertfromTemplateSQL(Entity.Class,
					conClassProperties);
		}

		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();

		if (clas.getImplementedInterfaces().size() != 0) {
			for (clonepedia.model.ontology.Interface interf : clas
					.getImplementedInterfaces()) {
				Interface in = getTheExistingInterfaceorCreateOne(
						interf.getFullName(), interf.getProject());
				storeImplementRelation(clas, in);
			}
		}

	} catch (SQLException e) {
		closePreparedStatement(stat);
		e.printStackTrace();
	}
}

public clonepedia.model.ontology.Class getTheExistingClassorCreateOne(
		String classFullName, Project project) {
	Properties conditionalProperties = new Properties();
	conditionalProperties.put("projectName", project.getProjectName());
	conditionalProperties.put("classFullName", classFullName);

	clonepedia.model.ontology.Class cl = getClass(conditionalProperties);

	if (null == cl) {
		String classId = "cl" + UUID.randomUUID();
		cl = new clonepedia.model.ontology.Class(classId, project, classFullName);
		storeClass(cl);
	}

	System.out.print("");

	return cl;
}

public clonepedia.model.ontology.Interface getTheExistingInterfaceorCreateOne(
		String interfaceFullName, Project project) {
	Properties conditionalProperties = new Properties();
	conditionalProperties.put("projectName", project.getProjectName());
	conditionalProperties.put("interfaceFullName", interfaceFullName);

	clonepedia.model.ontology.Interface interf = null;
	interf = getInterface(conditionalProperties);

	if (null == interf) {
		String interfaceId = "in" + UUID.randomUUID();
		interf = new clonepedia.model.ontology.Interface(interfaceId, project,
				interfaceFullName);
		storeInterface(interf);
	}

	return interf;
}

public Method getTheExistingMethodorCreateOne(Method m) throws Exception {
	Method method = new DBOperator().getMethod(m);
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

private clonepedia.model.ontology.Class getClass(Properties properties) {
	ResultSet rs = null;
	try {
		String sql;
		sql = generateSelectfromTemplateSQL(Entity.Class, properties);

		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();

		if (rs.next()) {
			String projectName = rs.getString("projectName");
			Properties proper = new Properties();
			proper.put("projectName", projectName);

			String classFullName = rs.getString("classFullName");
			String classId = rs.getString("classId");

			clonepedia.model.ontology.Class cl = new clonepedia.model.ontology.Class(classId,
					getProject(proper), classFullName);

			closeResultSet(rs);
			closePreparedStatement(stat);
			return cl;
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

private clonepedia.model.ontology.Interface getInterface(Properties properties) {
	ResultSet rs = null;
	try {
		String sql;
		sql = generateSelectfromTemplateSQL(Entity.Interface, properties);

		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();

		if (rs.next()) {
			String projectName = rs.getString("projectName");
			Properties proper = new Properties();
			proper.put("projectName", projectName);

			String interfaceFullName = rs.getString("interfaceFullName");
			String interfaceId = rs.getString("interfaceId");

			clonepedia.model.ontology.Interface interf = new clonepedia.model.ontology.Interface(
					interfaceId, getProject(proper), interfaceFullName);

			closeResultSet(rs);
			closePreparedStatement(stat);
			return interf;
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}


 * public Class getSimpleClass(Class clas) { ResultSet rs = null;
 * 
 * try{ Properties classProperties = new Properties(); } }


private Field getField(Field field) {
	ResultSet rs = null;

	try {
		Properties fieldProperties = new Properties();
		fieldProperties.put("fieldName", field.getFieldName());
		fieldProperties.put("ownerId", field.getOwnerType().getId());
		String sql = generateSelectfromTemplateSQL(Entity.Field,
				fieldProperties);

		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();

		if (rs.next()) {
			rs.close();
			stat.close();
			return field;
		} else {
			rs.close();
			stat.close();
			return null;
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}

private Method getMethod(Method method) {
	ResultSet rs = null;
	ResultSet paramRs = null;
	try {

		Properties methodProperties = new Properties();
		methodProperties.put("ownerId", method.getOwner().getId());
		methodProperties.put("methodName", method.getMethodName());
		if (method.getReturnType() != null)
			methodProperties.put("returnType", method.getReturnType()
					.toString());

		String sql;
		sql = generateSelectfromTemplateSQL(Entity.Method, methodProperties);

		stat = conn.prepareStatement(sql);
		rs = stat.executeQuery();
		// System.out.println(rs.getRow());

		while (rs.next()) {

			String methodId = rs.getString("methodId");

			Properties paramProperties = new Properties();
			paramProperties.put("methodId", methodId);
			String paramSQL = generateSelectfromTemplateSQL(
					Entity.MethodParameter, paramProperties);

			stat = conn.prepareStatement(paramSQL,
					ResultSet.TYPE_SCROLL_INSENSITIVE);
			// System.out.println(paramSQL);
			paramRs = stat.executeQuery();
			if (hasTheCorrespondingParameters(method, paramRs)) {
				method.setMethodId(methodId);
				paramRs.close();
				rs.close();
				stat.close();
				return method;
			} else {
				paramRs.close();
			}
		}
	} catch (SQLException e) {
		e.printStackTrace();
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		closeResultSet(paramRs);
		closeResultSet(rs);
		closePreparedStatement(stat);
	}
	return null;
}


 * private String getTypeIdbyProperties(Entity e, Properties properties){
 * ResultSet rs = null; try { String sql =
 * generateSelorDelfromTemplateSQL(SELECT, e, properties); stat =
 * conn.prepareStatement(sql); rs = stat.executeQuery(); if(rs.next()){
 * String idName = e.toString().toLowerCase() + "Id"; String id =
 * rs.getString(idName); return id; } rs.close(); stat.close(); } catch
 * (Exception ex) { ex.printStackTrace(); } finally { closeResultSet(rs);
 * closePreparedStatement(stat); } return null; }


private boolean hasTheCorrespondingParameters(Method method,
		ResultSet paramRs) throws SQLException {
	paramRs.last();
	int parameterNum = paramRs.getRow();
	if (parameterNum != method.getParameters().size()) {
		return false;
	} else if (parameterNum == 0)
		return true;
	else {
		Variable[] params = method.getParameters().toArray(new Variable[0]);
		paramRs.first();
		// System.out.println(paramRs.getRow());
		do {
			// System.out.println(paramRs.getRow());
			int parameterOrder = paramRs.getInt("parameterOrder");
			String parameterType = paramRs.getString("parameterType");

			if (!params[parameterOrder - 1].getVariableType().toString()
					.equals(parameterType))
				return false;
		} while (paramRs.next());
		return true;
	}
}

*//**
 * This method can only be called by
 * <code>getTheExistingFieldorCreateOne</code>. It is used to create a brand
 * new field.
 * 
 * @param field
 * @throws Exception
 *//*
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

	String sql = generateInsertfromTemplateSQL(Entity.Field,
			fieldProperties);

	try {
		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}

	closePreparedStatement(stat);

}

*//**
 * This method can only be called by
 * <code>getTheExistingMethodorCreateOne</code>. It is used to create a
 * brand new method.
 * 
 * @param method
 * @throws Exception
 *//*
private void storeMethodwithDependency(Method method) throws Exception {

	ComplexType type = method.getOwner();
	
	if(type.getSimpleName().length() == 0)
		System.out.print("");
	
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
	String sql = generateInsertfromTemplateSQL(Entity.Method,
			methodProperties);

	try {
		stat = conn.prepareStatement(sql);
		stat.executeUpdate();
		stat.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}
	closePreparedStatement(stat);
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

		String sql = "";
		*//**
		 * There is no need to update the record if the record has already
		 * existed.
		 *//*
		if (!checkIfanObjectExist(Entity.MethodParameter, paramProperties)) {
			paramProperties.putAll(paramValProperties);
			sql = generateInsertfromTemplateSQL(Entity.MethodParameter,
					paramProperties);

			try {
				stat = conn.prepareStatement(sql);
				stat.executeUpdate();
				stat.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		closePreparedStatement(stat);
		order++;
	}
}

*//**
 * This method can only be called by
 * <code>getTheExistingClassorCreateOne</code> method. It is used to insert
 * a brand new class.
 * 
 * @param clas
 *//*
private void storeClass(clonepedia.model.ontology.Class clas) {
	try {
		Project project = clas.getProject();

		Properties conClassProperties = new Properties();
		conClassProperties.put("classFullName", clas.getFullName());
		conClassProperties.put("projectName", project.getProjectName());
		conClassProperties.put("classId", clas.getId());

		String sql = generateInsertfromTemplateSQL(Entity.Class,
				conClassProperties);

		stat = conn.prepareStatement(sql);
		stat.executeUpdate();

		stat.close();

	} catch (SQLException e) {
		closePreparedStatement(stat);
		e.printStackTrace();
	}
}

private void storeInterface(clonepedia.model.ontology.Interface interf) {
	try {
		Project project = interf.getProject();

		Properties conInterfaceProperties = new Properties();
		conInterfaceProperties.put("interfaceFullName",
				interf.getFullName());
		conInterfaceProperties.put("projectName", project.getProjectName());
		conInterfaceProperties.put("interfaceId", interf.getId());

		String sql = generateInsertfromTemplateSQL(Entity.Interface,
				conInterfaceProperties);

		stat = conn.prepareStatement(sql);
		stat.executeUpdate();

		stat.close();

	} catch (SQLException e) {
		closePreparedStatement(stat);
		e.printStackTrace();
	}
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
}*/
	
	/*@SuppressWarnings("unchecked")
	private String generateSelectfromTemplateSQL(DBTable table, Properties properties) {
		String sql = "select * from ";
	
		sql += table.toString() + " where ";
		Enumeration<String> en = (Enumeration<String>) properties
				.propertyNames();
	
		while (en.hasMoreElements()) {
			String property = en.nextElement();
			String value = properties.getProperty(property);
	
			sql += property + "='" + value + "' and ";
		}
	
		sql = sql.substring(0, sql.length() - 5);
	
		return sql;
	}*/
	
	/*@SuppressWarnings("unchecked")
	private String generateUpdatefromTemplateSQL(Entity e,
			Properties valueProperties, Properties conditionalProperties) {
		String sql = "update " + e.toString() + " set ";
		Enumeration<String> valueEn = (Enumeration<String>) valueProperties
				.propertyNames();
		while (valueEn.hasMoreElements()) {
			String valueProp = valueEn.nextElement();
			String valueVal = valueProperties.getProperty(valueProp);
			sql += valueProp + "='" + valueVal + "', ";
		}

		sql = sql.substring(0, sql.length() - 2);
		sql += " where ";

		Enumeration<String> conditionalEn = (Enumeration<String>) conditionalProperties
				.propertyNames();
		while (conditionalEn.hasMoreElements()) {
			String conditionalProp = conditionalEn.nextElement();
			String conditionalVal = conditionalProperties
					.getProperty(conditionalProp);
			sql += conditionalProp + "='" + conditionalVal + "' and ";
		}

		sql = sql.substring(0, sql.length() - 5);

		return sql;
	}*/

	/*public boolean checkIfaRecordExistInDB(Relation r, Properties properties) {
		ResultSet rs = null;
		try {
			String sql = generateSelectfromTemplateSQL(r, properties);
			stat = conn.prepareStatement(sql);
			rs = stat.executeQuery();
	
			boolean flag = rs.next();
			rs.close();
			stat.close();
	
			return flag;
	
		} catch (SQLException e) {
			closeResultSet(rs);
			closePreparedStatement(stat);
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return false;
	}*/
	
	/*@SuppressWarnings("unchecked")
	private String generateInsertfromTemplateSQL(Relation r,
			Properties valueProperties) {
		String sql1 = "insert into " + r.toString() + "(";
		String sql2 = ") value(";
		String sql3 = ")";
		Enumeration<String> valueEn = (Enumeration<String>) valueProperties
				.propertyNames();
		while (valueEn.hasMoreElements()) {
			String valueProp = valueEn.nextElement();
			String valueVal = valueProperties.getProperty(valueProp);

			sql1 += valueProp + ", ";
			sql2 += "'" + valueVal + "', ";
		}

		sql1 = sql1.substring(0, sql1.length() - 2);
		sql2 = sql2.substring(0, sql2.length() - 2);

		return sql1 + sql2 + sql3;
	}*/
	
	/*@SuppressWarnings("unchecked")
	private String generateSelectfromTemplateSQL(Relation r,
			Properties properties)  throws Exception {
		String sql = "select * from ";
		
		 * if (type.equals(SELECT)) sql = "select * from "; else if
		 * (type.equals(DELETE)) sql = "delete from "; else throw new
		 * Exception();
		 

		sql += r.toString() + " where ";
		Enumeration<String> en = (Enumeration<String>) properties
				.propertyNames();

		while (en.hasMoreElements()) {
			String property = en.nextElement();
			String value = properties.getProperty(property);

			sql += property + "='" + value + "' and ";
		}

		sql = sql.substring(0, sql.length() - 5);

		return sql;
	}*/
}

package clonepedia.syntactic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import clonepedia.businessdata.OntologicalDataFetcher;
import clonepedia.model.ontology.Class;
import clonepedia.model.ontology.CloneInstance;
import clonepedia.model.ontology.CloneSet;
import clonepedia.model.ontology.CloneSets;
import clonepedia.model.ontology.ComplexType;
import clonepedia.model.ontology.Constant;
import clonepedia.model.ontology.CounterRelationGroup;
import clonepedia.model.ontology.Field;
import clonepedia.model.ontology.InstanceElementRelation;
import clonepedia.model.ontology.Interface;
import clonepedia.model.ontology.Method;
import clonepedia.model.ontology.PrimiType;
import clonepedia.model.ontology.Project;
import clonepedia.model.ontology.VarType;
import clonepedia.model.ontology.Variable;
import clonepedia.model.ontology.VariableUseType;
import clonepedia.syntactic.pools.ClassPool;
import clonepedia.syntactic.pools.FieldPool;
import clonepedia.syntactic.pools.InterfacePool;
import clonepedia.syntactic.pools.MethodPool;
import clonepedia.util.MinerUtil;

public class OntologicalModelGenerator implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4884220405975527491L;
	private Project project;
	private CloneSets sets;
	private HashMap<String, Long> setTimeMap = new HashMap<String, Long>();
	private OntologicalDataFetcher fetcher = new OntologicalDataFetcher();

	public void setSets(CloneSets sets) {
		this.sets = sets;
	}

	private ClassPool classPool;
	//private CloneInstancePool cloneInstancePool;
	private InterfacePool interfacePool;
	private MethodPool methodPool;
	private FieldPool fieldPool;
	
	public OntologicalModelGenerator(Project project) {
		super();
		this.project = project;
	}

	public void reconstructOntologicalModel() throws Exception{
		initializePools();
		buildRelationsforCommonPart();
		buildRelationsforDiffPart();
		buildRelationsforClass();
		buildRelationsforInterface();
		buildRelationsforMethod();
		buildRelationsforField();
		
		/*FileOutputStream fos = new FileOutputStream("ontological_model"); 
		ObjectOutputStream oos = new ObjectOutputStream(fos); 
		oos.writeObject(sets); 
		oos.flush(); 
		oos.close(); */
		MinerUtil.serialize(sets, "ontological_model");
		
		//buildPatternforCloneSets();
	}
	
	
	public void buildPatternforCloneSets() throws Exception{
		//int processed = 0;
		for(CloneSet set: sets.getCloneList()){
			long start = System.currentTimeMillis();
			//if(set.getId().equals("52028"))
				set.buildPatterns();
			//processed++;
			long end = System.currentTimeMillis();
			long time = end - start;
			setTimeMap.put(set.getId(), time);
			//System.out.println("Time used in buildPatterns:" + time);
			//System.out.print("");
		}
		System.out.print("");
	}
	
	private void buildRelationsforField() {
		for(Field field: fieldPool){
			VarType vType = field.getFieldType();
			ComplexType fieldType = transferVarTypeToComplexType(vType);
			if(fieldType != null){
				if(fieldType instanceof Class)
					field.setFieldType((Class)fieldType);
				else if(fieldType instanceof Interface)
					field.setFieldType((Interface)fieldType);
			}
		}
		
	}

	private void buildRelationsforMethod() {
		for(Method method: methodPool){
			VarType vType = method.getReturnType();
			ComplexType returnType = transferVarTypeToComplexType(vType);
			if(returnType != null){
				if(returnType instanceof Class)
					method.setReturnType((Class)returnType);
				else if(returnType instanceof Interface)
					method.setReturnType((Interface)returnType);
			}
			
			for(Variable parameter: method.getParameters()){
				buildRelationforVariable(parameter);
				/*VarType pType = parameter.getVariableType();
				ComplexType parameterType = transferVarTypeToComplexType(pType);
				if(parameterType != null){
					if(parameterType instanceof Class)
						parameter.setVariableType((Class)parameterType);
					else if(parameterType instanceof Interface)
						parameter.setVariableType((Interface)parameterType);
				}*/
			}
		}
		
	}
	
	private void buildRelationforVariable(Variable variable){
		
		VarType vType = variable.getVariableType();
		ComplexType variableType = transferVarTypeToComplexType(vType);
		if(variableType != null){
			if(variableType instanceof Class)
				variable.setVariableType((Class)variableType);
			else if(variableType instanceof Interface)
				variable.setVariableType((Interface)variableType);
		}
	}
	
	private ComplexType transferVarTypeToComplexType(VarType vType){
		
		if(vType instanceof Class){
			Class returnClass = (Class)vType;
			String returnClassId = returnClass.getId();
			Class clas = classPool.getClass(returnClassId);
			return clas;
		}
		else if(vType instanceof Interface){
			Interface returnInterface = (Interface)vType;
			String returnInterfaceId = returnInterface.getId();
			Interface interf = interfacePool.getInterface(returnInterfaceId);
			return interf;
		}
		else
			return null;
	}

	private void buildRelationsforInterface() {
		for(Interface interf: interfacePool){
			ArrayList<String> superInterfaceIds = fetcher.getExtendedInterfaceIds(interf.getId());
			for(String id: superInterfaceIds){
				Interface superInterf = interfacePool.getInterface(id);
				interf.addSuperInterface(superInterf);
			}
		}
	}

	private void buildRelationsforClass() {
		for(Class clas: classPool){
			try{
				String superClassId = clas.getSuperClass().getId();
				String outerClassId = clas.getOuterClass().getId();
				
				/*if("cla205bcb6-cedb-4c94-bb8c-b32700c44195".equals(superClassId))
					System.out.print("");*/
				
				Class superClass = classPool.getClass(superClassId);
				Class outerClass = classPool.getClass(outerClassId);
				
				clas.setSuperClass(superClass);
				clas.setOuterClass(outerClass);
				
				ArrayList<String> interfaceIds = fetcher.getImplementedInterfaceIds(clas.getId());
				for(String id: interfaceIds){
					Interface interf = interfacePool.getInterface(id);
					clas.getImplementedInterfaces().add(interf);
				}
			}
			catch(NullPointerException e){
				e.printStackTrace();
			}
		}
		
	}

	private void buildRelationsforCommonPart() throws Exception {
		for(CloneSet set: sets.getCloneList()){
			ArrayList<String> methodIds = fetcher.getMethodIds(set.getId());
			ArrayList<Field> fields = fetcher.getFields(set.getId());
			ArrayList<ComplexType> types = fetcher.getTypes(set.getId());
			ArrayList<Variable> variables = fetcher.getVariables(set, set.getProject());
			ArrayList<Constant> constants = fetcher.getConstants(set);
			
			for(String methodId: methodIds){
				Method method = methodPool.getMethod(methodId);
				set.getCallingMethods().add(method);
			}
			
			for(Field f: fields){
				Field field = fieldPool.getField(f.getFieldName(), f.getOwnerType().getId());
				set.getAccessingFields().add(field);
			}
			
			for(ComplexType type: types){
				if(type instanceof Class){
					Class clas = classPool.getClass(type.getId());
					set.getUsingClasses().add(clas);
				}
				if(type instanceof Interface){
					Interface interf = interfacePool.getInterface(type.getId());
					set.getUsingInterfaces().add(interf);
				}
			}
			
			for(Variable variable: variables){
				variable.setOwner(set);
				buildRelationforVariable(variable);
				
				if(variable.getUseType().equals(VariableUseType.DEFINE))
					set.getDefiningVariables().add(variable);
				if(variable.getUseType().equals(VariableUseType.REFER))
					set.getReferingVariables().add(variable);
			}
			
			for(Constant constant: constants){
				constant.setOwner(set);
				set.getUsingConstants().add(constant);
			}
		}
	}

	private void buildRelationsforDiffPart() throws Exception {
		for(CloneSet set: sets.getCloneList()){
			ArrayList<CloneInstance> instances = fetcher.getPlainCloneInstances(set.getId());
			for(CloneInstance instance: instances){
				
				String mId = instance.getResidingMethod().getMethodId();
				Method method = methodPool.getMethod(mId);
				instance.setResidingMethod(method);
				
				set.add(instance);
				instance.setCloneSet(set);
				//cloneInstancePool.add(instance);
			}
			
			ArrayList<String> counterRelationIds = fetcher.getCounterRelationIds(set);
			for(String counterRelationId: counterRelationIds){
				CounterRelationGroup group = new CounterRelationGroup(counterRelationId);
				set.addCounterRelationGroup(group);
			}	
			
			for(CounterRelationGroup group: set.getCounterRelationGroups()){
				for(CloneInstance instance: set){
					String methodId = fetcher.getMethodId(instance, group.getCounterRelationsId());
					Field field = fetcher.getField(instance, group.getCounterRelationsId());
					ComplexType type = fetcher.getType(instance, group.getCounterRelationsId());
					Variable variable = fetcher.getVariable(instance, group.getCounterRelationsId(), set.getProject());
					Constant constant = fetcher.getConstant(instance, group.getCounterRelationsId());
					PrimiType primiType = fetcher.getPrimiType(instance, group.getCounterRelationsId());
					
					if(null != methodId){
						Method method = methodPool.getMethod(methodId);
						group.add(new InstanceElementRelation(instance, method));
					}
					else if(null != field){
						Field f = fieldPool.getField(field.getFieldName(), field.getOwnerType().getId());
						group.add(new InstanceElementRelation(instance, f));
					}
					else if(null != type){
						if(type instanceof Class){
							Class clas = classPool.getClass(type.getId());
							group.add(new InstanceElementRelation(instance, clas));
						}
						if(type instanceof Interface){
							Interface interf = interfacePool.getInterface(type.getId());
							group.add(new InstanceElementRelation(instance, interf));
						}
					}
					else if(null != variable){
						variable.setOwner(instance);
						buildRelationforVariable(variable);
						group.add(new InstanceElementRelation(instance, variable));
					}
					else if(null != constant){
						constant.setOwner(instance);
						group.add(new InstanceElementRelation(instance, constant));
					}
					else if(null != primiType){
						group.add(new InstanceElementRelation(instance, primiType));
					}
				}
			}
		}
		
	}

	private void initializePools() throws Exception{
		
		sets = fetcher.getPlainCloneSets(project);
		
		for(CloneSet set: sets.getCloneList())
			set.setCloneSets(sets);
		
		classPool = fetcher.getPlainClasses(project);
		interfacePool = fetcher.getPlainInterfaces(project);
		
		
		methodPool = new MethodPool();
		fieldPool = new FieldPool();
		
		for(Class clas: classPool){
			ArrayList<Method> methods = clas.getMethods();
			for(Method method: methods)
				methodPool.add(method);
			
			ArrayList<Field> fields = clas.getFields();
			for(Field field: fields)
				fieldPool.add(field);
		}
		
		for(Interface interf: interfacePool){
			ArrayList<Method> methods = interf.getMethods();
			for(Method method: methods)
				methodPool.add(method);
			
			ArrayList<Field> fields = interf.getFields();
			for(Field field: fields)
				fieldPool.add(field);
		}
	}
	
	public CloneSets getSets() {
		return sets;
	}
}

package analysis;

import minijava.ast.*;

import java.util.*;

import static minijava.ast.MJ.TypeClass;


public class Analysis {

    public enum Type { INT, BOOL, CLASS, ARRAY}
    public ArrayList<MJTypeClass> classes;
    Map<String, MJClassDecl> classTable=new HashMap<>();
    // class, extends
    Map<MJClassDecl, MJClassDecl> superClasses=new HashMap<>();
    private final MJProgram prog;
    private List<TypeError> typeErrors = new ArrayList<>();

    public Analysis(MJProgram prog) {
        this.prog = prog;
    }

    void addError(MJElement element, String message) {
        typeErrors.add(new TypeError(element, message));
    }

    public List<TypeError> getTypeErrors() {
        return new ArrayList<>(typeErrors);
    }
    public void check() {
        buildClassTable();
        buildSuperClass();
       //1 a: error massage for extending main class missing
       checkExtends();
       //1 c
       checkNames();
       typeChecking();
       System.out.println(getTypeErrors());



    }


    private void buildClassTable(){
        for(int i=0;i<prog.getClassDecls().size();i++){
            if(classTable.get(prog.getClassDecls().get(i).getName())!=null || prog.getClassDecls().get(i).getName().equals(prog.getMainClass().getName())){
                addError(prog.getClassDecls().get(i),"Duplicate class "+prog.getClassDecls().get(i).getName());
            }
            classTable.put(prog.getClassDecls().get(i).getName(),prog.getClassDecls().get(i));
        }

    }
    private void buildSuperClass(){
        for(MJClassDecl classDecl : classTable.values()){
            if(classDecl.getExtended() instanceof MJExtendsClass) {
                superClasses.put(classDecl, classTable.get(((MJExtendsClass) classDecl.getExtended()).getName()));
            }
        }
    }
    private void typeChecking(){
        //new Object
        for(MJStatement statement : prog.getMainClass().getMainBody()){
            if(statement instanceof MJStmtExpr) {
                if (((MJStmtExpr) statement).getExpr() instanceof MJNewObject) {
                    if (classTable.get(((MJNewObject) ((MJStmtExpr) statement).getExpr()).getClassName()) == null) {
                        addError(statement, "Invalid object type " + ((MJNewObject) ((MJStmtExpr) statement).getExpr()).getClassName());
                    }
                }
            }
        }
    }

    private Type checkType(MJType type){
        if (type instanceof MJTypeInt ){
            return  Type.INT;
        }
        if (type instanceof MJTypeBool ){
            return  Type.BOOL;
        }
        if (type instanceof MJTypeClass ){
            //classes.add((MJTypeClass) type);
            return  Type.CLASS;
        }
        if (type instanceof MJTypeArray ){
            return  Type.ARRAY;
        }
        return  null;
    }
    private Boolean checkTypes(MJType type1,MJType type2){
        if (type1 instanceof MJTypeInt && type2 instanceof MJTypeInt ){
            return  true;
        }
        if (type1 instanceof MJTypeBool && type2 instanceof MJTypeBool){
            return  true;
        }
        if (type1 instanceof MJTypeClass && type2 instanceof MJTypeClass){
            if(((MJTypeClass) type1).getName().equals(((MJTypeClass) type2).getName())){
                return true;
            }
        }
        if (type1 instanceof MJTypeArray && type2 instanceof MJTypeArray ){
            if(((MJTypeArray) type1).getComponentType().equals(((MJTypeArray) type2).getComponentType())){
                return true;
            }
        }
        return  false;
    }
    private boolean isSubtype(MJType type1, MJType type2){
        if(checkType(type1) ==Type.CLASS && checkType(type2) == Type.CLASS){
            if (((MJTypeClass) type1).getName().equals(((MJTypeClass) type2).getName())){
                //Same type
                return true;
            }
            for (MJClassDecl cl : prog.getClassDecls() ){

                    if (cl.getName().equals(((MJTypeClass) type1).getName()) && cl.getExtended() instanceof MJExtendsClass ){

                        //direct subtype
                        if ( ((MJExtendsClass) cl.getExtended()).getName().equals(((MJTypeClass) type2).getName())){
                            return true;
                        }

                        //recursive subtype
                        else{

                            for (MJClassDecl sup : prog.getClassDecls() ){
                                if (((MJExtendsClass) cl.getExtended()).getName().equals(sup.getName())){
                                    return isSubtype(TypeClass(sup.getName()),type2);
                                }
                            }

                        }

                    }

            }
        }
        else{
            if(checkType(type1) == checkType(type2)){
                return true;
            }
        }
        return false;

    }


    private void checkExtends(){
        for(Map.Entry<MJClassDecl, MJClassDecl> entry : superClasses.entrySet()){
            if(entry.getValue()==null){
                addError(entry.getKey(),"Extended class "+((MJExtendsClass)entry.getKey().getExtended()).getName()+" does not exist!");
            }
        }
        MJClassDecl currentClassDecl;
        //check for cycles
        for(MJClassDecl classDecl : classTable.values()) {
            List<String> temp=new ArrayList();
            temp.add(classDecl.getName());
            currentClassDecl=classDecl;
            while(superClasses.get(currentClassDecl)!=null) {
                if(temp.contains(superClasses.get(currentClassDecl).getName())){
                    if(superClasses.get(currentClassDecl).getName().equals(classDecl.getName())) {
                        String message="Cyclic inheritance between classes ";
                        for(String t : temp){
                            message+=t+" ";
                        }
                        addError(classDecl, "Cyclic inheritance between classes " + message);
                        return;
                    }else{break;}
                }
                else {
                    //check Override
                    for (MJMethodDecl clmethod: classDecl.getMethods()) {
                            for (MJMethodDecl supmethod : superClasses.get(currentClassDecl).getMethods()) {
                                //selber return type && selber name && gleiche Parameteranzahl
                                if(clmethod.getName().equals(supmethod.getName()) && clmethod.getFormalParameters().size()==supmethod.getFormalParameters().size() ){
                                   if(!(isSubtype(clmethod.getReturnType(),supmethod.getReturnType()))){
                                           addError(clmethod,"Incorrect return type of method "+clmethod.getName()+". Expected Type "+((MJTypeClass) supmethod.getReturnType()).getName()+ ", but was "+((MJTypeClass) clmethod.getReturnType()).getName() );
                                    }
                                    //vergleiche Parameter
                                    int amountOfParameters = clmethod.getFormalParameters().size();
                                    for (int i =0; i<amountOfParameters; i++) {
                                        if (!checkTypes(clmethod.getFormalParameters().get(i).getType(),(supmethod.getFormalParameters().get(i).getType()))) {
                                            String msg ="Incorrect Type of parameter " + clmethod.getFormalParameters().get(i).getName() + " in method: " + clmethod.getName() + " in class " + classDecl.getName();
                                            msg = msg + ". Expected Type: " + supmethod.getFormalParameters().get(i).getType().toString()+  " but was Type: " + clmethod.getFormalParameters().get(i).getType().toString() ;
                                            addError(clmethod,msg);
                                        }
                                    }
                                }
                        }
                    }


                    temp.add(superClasses.get(currentClassDecl).getName());
                    currentClassDecl=classTable.get(superClasses.get(currentClassDecl).getName());
                }
            }
        }
    }
    private void checkNames(){
        MJVarDeclList fields;
        String parameterName;
        MJMethodDeclList m;
        for(int i=0;i<prog.getClassDecls().size();i++){
            //////methods
            m=prog.getClassDecls().get(i).getMethods();
            for(int j=0;j<m.size();j++){
                //parameter names
                for(int k=0;k<m.get(j).getFormalParameters().size();k++) {
                   parameterName = m.get(j).getFormalParameters().get(k).getName();
                    for(int l=0;l<m.get(j).getFormalParameters().size();l++) {
                        if(parameterName.equals(m.get(j).getFormalParameters().get(l).getName()) && l!=k){
                            addError(m.get(j).getFormalParameters().get(l),"Dupilicate parameter "+parameterName+" in methdod "+m.get(j).getName()+" in class "+prog.getClassDecls().get(i).getName());
                            return;
                        }
                    }
                }
                //method names
              String methodName= m.get(j).getName();
                for(int k=0;k<m.size();k++) {
                    //two methods with the same name?
                    if(methodName.equals(m.get(k).getName()) && j!=k){
                        addError(m.get(j),"Dupilicate method "+m.get(j).getName()+" in class "+prog.getClassDecls().get(i).getName());
                        return;
                    }
                }
            }
            //////field names
            fields=prog.getClassDecls().get(i).getFields();
            for(int j=0;j<fields.size();j++){
                String fieldName= fields.get(j).getName();
                for(int k=0;k<fields.size();k++) {
                    //two methods with the same name?
                    if(fieldName.equals(fields.get(k).getName()) && j!=k){
                        addError(fields.get(j),"Dupilicate field "+fields.get(j).getName()+" in class "+prog.getClassDecls().get(i).getName());
                        return;
                    }
                }
            }
        }
    }
}

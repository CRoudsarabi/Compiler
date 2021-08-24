package analysis;

import minijava.ast.*;

import java.util.*;

import static minijava.ast.MJ.*;


public class Analysis {

    public enum Type { INT, BOOL, CLASS, ARRAY}

    Map<String, MJClassDecl> classTable=new HashMap<>();
    // class, extends
    Map<MJClassDecl, MJClassDecl> superClasses=new HashMap<>();
    private final MJProgram prog;
    private List<TypeError> typeErrors = new ArrayList<>();
    private SymbolTable s=new SymbolTable();
    public Analysis(MJProgram prog) {
        this.prog = prog;
    }

    void addError(MJElement element, String message) {
        typeErrors.add(new TypeError(element, message));
    }

    public MJProgram getProg() {
        return prog;
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

        s.enter();
        //classList
        for(MJClassDecl classDecl: prog.getClassDecls()){
            s.enter();
            String classname = classDecl.getName();
            for(MJVarDecl varDecl : classDecl.getFields()) {
                String fieldname = varDecl.getName();
                MJType type = varDecl.getType();
                s.addFields(classname,fieldname,type);
                s.peek().put(varDecl.getName(), varDecl);

            }
            for(MJMethodDecl methodDecl: classDecl.getMethods()) {
                s.peek().put(methodDecl.getName(), methodDecl);
                String methodname = methodDecl.getName();
                MJType retype = methodDecl.getReturnType();
                s.addMethod(classname,methodname,retype);
                s.enter();
                for (MJVarDecl varDecl : methodDecl.getFormalParameters()) {
                    s.peek().put(varDecl.getName(), varDecl);
                    //TODO add formal parameters of subclass method
                    if (classDecl.getExtended() instanceof MJExtended){

                    }
                }
                for(MJStatement statement : methodDecl.getMethodBody()) {
                    checkStmt(statement);
                    if(statement instanceof MJVarDecl) {
                        MJVarDecl varDecl=((MJVarDecl) statement);
                        s.peek().put( varDecl.getName(),varDecl);
                    }
                }
                s.exit();
            }
            s.exit();
        }
        //mainClass

        //args
        //s.peek().put( prog.getMainClass().getArgsName(), TypeArray(TypeInt()));

        //main body
        for(MJStatement stmt : prog.getMainClass().getMainBody()){
            checkStmt(stmt);
        }
        s.exit();
    }

    private void checkStmt(MJStatement e){
        e.match(new MJStatement.MatcherVoid() {

            @Override
            public void case_Block(MJBlock block) {
                s.enter();
                for (MJStatement stmt: (MJBlock) e) {
                    checkStmt(stmt);
                }
                s.exit();
            }

            @Override
            public void case_StmtAssign(MJStmtAssign stmtAssign) {
                if( isSubtypeOf(checkExpr(stmtAssign.getRight()), checkExprL(stmtAssign.getAddress() )   )){
                    //return checkType2(((MJStmtAssign) e).getAddress() );
                }
                else{
                    addError(e, "Assign: Type error between  " + stmtAssign.getAddress() +  " and " + stmtAssign.getRight());
                    System.out.println("Assign Address: " + checkExprL(stmtAssign.getAddress()));
                    System.out.println("Assign Right: " + checkExpr(stmtAssign.getRight()));
                }
            }

            @Override
            public void case_StmtExpr(MJStmtExpr stmtExpr) {
                checkExpr(stmtExpr.getExpr());
            }

            @Override
            public void case_StmtIf(MJStmtIf stmtIf) {
                if(!(checkExpr(stmtIf.getCondition()) instanceof MJTypeBool)){
                    addError(e, "StmtIF: Cond not of type boolean. Type " + ((MJStmtIf) e).getCondition());
                }
                else {
                    checkStmt(stmtIf.getIfTrue());
                    checkStmt(stmtIf.getIfFalse());
                }
            }

            @Override
            public void case_StmtPrint(MJStmtPrint stmtPrint) {
                if (checkExpr(stmtPrint.getPrinted()) instanceof MJTypeInt){
                    //return TypeInt();
                }
                else{
                    addError(e, "StmtPrint: Argument not of type int. Type " + ((MJStmtPrint) e).getPrinted());
                }
            }

            @Override
            public void case_StmtReturn(MJStmtReturn stmtReturn) {

            }

            @Override
            public void case_StmtWhile(MJStmtWhile stmtWhile) {
                if(!(checkExpr(stmtWhile.getCondition()) instanceof MJTypeBool)){
                    addError(e, "StmtWhile: Cond not of type boolean. Type " + ((MJStmtWhile) e).getCondition());
                }
                else {
                    checkStmt(stmtWhile.getLoopBody());
                }
            }

            @Override
            public void case_VarDecl(MJVarDecl varDecl) {
                if (e instanceof MJVarDecl ){
                    if(varDecl.getName().equals("args")){
                        addError(e, "Use of protected name args");

                    }
                    if (varDecl.getType() instanceof MJTypeClass  ){
                        if (classTable.get(((MJTypeClass) varDecl.getType()).getName()) == null) {
                            addError(e, "Class does not exist " + varDecl.getType());

                        }
                    }
                    if(varDecl.getType() instanceof MJTypeArray){

                    }
                    s.peek().put(varDecl.getName(), e);
                    //return((MJVarDecl) e).getType();
                }
            }

        });

    }

    private MJType checkExpr(MJExpr e){
        return e.match(new MJExpr.Matcher<MJType>() {

            @Override
            public MJType case_ArrayLength(MJArrayLength arrayLength) {
                MJExpr expr = arrayLength.getArrayExpr();
                if(!(checkExpr(expr) instanceof MJTypeArray)){
                    addError(e, "ArrayLength: ArrayExpr not of type array. Type: " + checkExpr(expr));
                }
                return TypeInt();
            }

            @Override
            public MJType case_Number(MJNumber number) {
                return TypeInt();
            }

            @Override
            public MJType case_NewArray(MJNewArray newArray) {
                MJType type = newArray.getBaseType();

                MJExpr expr = newArray.getArraySize();
                if(!(checkExpr(expr) instanceof MJTypeInt)){
                    addError(e, "NewArray: ArraySize not of type int. Type: " + checkExpr(expr));
                }
                if (type instanceof MJTypeInt){
                    return TypeArray(TypeInt());
                }
                if (type instanceof MJTypeBool){
                    return TypeArray(TypeBool());
                }
                if (type instanceof MJTypeClass){
                    return TypeArray(TypeClass(((MJTypeClass) newArray.getBaseType()).getName()));
                }
                if (type instanceof MJTypeArray){
                    //System.out.println("new array type : " + TypeArray(checkType2(type)));
                    return TypeArray(checkType2(type));
                }
                else {
                    addError(e, "Incorrect type for creating Array " + newArray.getBaseType());
                    return null;
                }
            }

            @Override
            public MJType case_BoolConst(MJBoolConst boolConst) {
                return TypeBool();
            }

            @Override
            public MJType case_Read(MJRead read) {
                return checkExprL(read.getAddress());
            }

            @Override
            public MJType case_ExprBinary(MJExprBinary exprBinary) {
                MJType typeLeft = checkExpr(exprBinary.getLeft());
                MJType typeRight = checkExpr(exprBinary.getRight());
                MJOperator op = exprBinary.getOperator();

                if (op instanceof MJEquals){
                    if(typeLeft instanceof MJExprNull || typeRight instanceof MJExprNull ){
                         return TypeBool();
                    }
                    if(isSubtypeOf(typeLeft,typeRight) || isSubtypeOf(typeRight,typeLeft) ){
                        return TypeBool();
                    }
                    else{
                        addError(e, "BinaryExpr with == : Type error between  " + typeLeft +  " and " + typeRight);
                        return null;
                    }
                }

                if (op instanceof MJPlus || op instanceof MJTimes || op instanceof MJDiv || op instanceof MJMinus ){
                    if(typeLeft instanceof MJTypeInt && typeRight instanceof MJTypeInt ){
                        return TypeInt();
                    }
                    else {
                        addError(e, "BinaryExpr with +-*/ : Type error between  " + typeLeft +  " and " + typeRight);
                        return null;
                    }
                }

                if (op instanceof MJLess ){
                    if(typeLeft instanceof MJTypeInt && typeRight instanceof MJTypeInt ){
                        return TypeBool();
                    }
                    else {
                        addError(e, "BinaryExpr with < : Type error between  " + typeLeft +  " and " + typeRight);
                        return null;
                    }
                }

               if (op instanceof MJAnd){
                   if(typeLeft instanceof MJTypeBool && typeRight instanceof MJTypeBool ){
                       return TypeBool();
                   }
                   else {
                       addError(e, "BinaryExpr with && : Type error between  " + typeLeft +  " and " + typeRight);
                       return null;
                   }
                }
                else{
                    addError(e, "BinaryExpr: Type error between  " + (((MJExprBinary) e).getLeft()) +  " and " + ((MJExprBinary) e).getRight());
                    return null;
                }
            }

            @Override
            public MJType case_ExprThis(MJExprThis exprThis) {
                return MJ.TypeClass("this");
            }

            @Override
            public MJType case_ExprUnary(MJExprUnary exprUnary) {
                MJExpr expr = exprUnary.getExpr();
                MJType exprType = checkExpr(expr);
                if (exprUnary.getUnaryOperator() instanceof MJNegate){
                    if(!(exprType instanceof MJTypeBool)){
                        addError(e, "ExprUnary: Expr not of type bool. Type: " + exprType);
                    }
                    return TypeBool();
                }
                if (exprUnary.getUnaryOperator() instanceof MJUnaryMinus){
                    if(!(exprType instanceof MJTypeInt)){
                        addError(e, "ExprUnary: Expr not of type int. Type: " + exprType);
                    }
                    return TypeInt();
                }
                else{
                    addError(e, "Error with Expr Unary");
                    return null;
                }
            }

            @Override
            public MJType case_MethodCall(MJMethodCall methodCall) {

                for (MJExpr expr : methodCall.getArguments()) {
                    checkExpr(expr);
                }
                MJType type =checkExpr(methodCall.getReceiver());


                if (type instanceof MJTypeClass){

                    if (findMethod(((MJTypeClass) type).getName(),methodCall.getMethodName()) == null){
                        addError(e, "Cannot find method  " + (methodCall.getMethodName()));
                        return null;
                    }

                    else {
                        return findMethod(((MJTypeClass) type).getName(),methodCall.getMethodName());
                    }

                }
                else{
                    addError(e, "MethodCall: Caller not of type class.  Type: " + type + " .");
                    return null;
                }

            }

            @Override
            public MJType case_NewObject(MJNewObject newObject) {
                if(classTable.get(((MJNewObject) e).getClassName()) == null){
                    addError(e, "Class does not exist " + ((MJNewObject) e).getClassName());
                    return null;
                }
                return TypeClass(((MJNewObject) e).getClassName()) ;
            }

            @Override
            public MJType case_ExprNull(MJExprNull exprNull) {
                return null;
            }
        });
    }

    private MJType checkExprL(MJExprL e){
        return e.match(new MJExprL.Matcher<MJType>() {
            @Override
            public MJType case_FieldAccess(MJFieldAccess fieldAccess) {
                MJType type =checkExpr(fieldAccess.getReceiver());
                if (type instanceof MJTypeClass){
                    if(((MJTypeClass) type).getName().equals("this")){
                        return s.lookupType(((MJFieldAccess) e).getFieldName());

                    }
                    return findField(((MJTypeClass) type).getName(),((MJFieldAccess) e).getFieldName());

                }
                else {
                    addError(e, "Field Access: Caller not of type class. Type: " + type + " .");
                    return null;
                }

            }

            @Override
            public MJType case_ArrayLookup(MJArrayLookup arrayLookup) {
                MJType indexType = checkExpr(arrayLookup.getArrayIndex());
                MJType exprType = checkExpr(arrayLookup.getArrayExpr());
                if(!(indexType instanceof MJTypeInt)){
                    addError(e, "ArrayLookup: Index not of type int ");

                }

                if(exprType instanceof MJTypeArray){

                    return (((MJTypeArray) exprType).getComponentType());

                }
                else {
                    addError(e, "ArrayLookup: Caller not of type array. Type: " + exprType + " .");
                    return null;
                }
            }

            @Override
            public MJType case_VarUse(MJVarUse varUse) {
                MJType varUseType = s.lookupType(varUse.getVarName());
                if (varUseType == null){
                    addError(e, "Variable does not exist " + (varUse.getVarName()));
                }
                return varUseType;
            }
        });
    }

    private MJType checkType2(MJType e){

        return e.match(new MJType.Matcher<MJType>() {
            @Override
            public MJType case_TypeClass(MJTypeClass typeClass) {
                return MJ.TypeClass(typeClass.getName());
            }

            @Override
            public MJType case_TypeArray(MJTypeArray typeArray) {
                //System.out.println("checktype Array : " + MJ.TypeArray(checkType2(typeArray.getComponentType())));
                return MJ.TypeArray(checkType2(typeArray.getComponentType()));
            }

            @Override
            public MJType case_TypeBool(MJTypeBool typeBool) {
                return MJ.TypeBool();
            }

            @Override
            public MJType case_TypeInt(MJTypeInt typeInt) {
                return MJ.TypeInt();
            }
        });


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
            if(checkTypes(((MJTypeArray) type1).getComponentType(),((MJTypeArray) type2).getComponentType())){
                return true;
            }
        }
        return  false;
    }


    private boolean isSubtypeOf(MJType type1, MJType type2){
        if(type1 instanceof MJTypeClass && type2 instanceof MJTypeClass){
            String class1name = ((MJTypeClass) type1).getName();
            String class2name =((MJTypeClass) type2).getName() ;

            //Same type
            if (class1name.equals(class2name)){
                return true;
            }
            //possible Subtype
            MJClassDecl class1 = classTable.get(class1name);
            MJClassDecl class2 = classTable.get(class2name);
            if(class1.getExtended() instanceof MJExtendsClass ){
                if (superClasses.get(class1).equals(class2)){
                    return true;
                }
                else return isSubtypeOf(TypeClass(superClasses.get(class1).getName()),type2);
            }

        }
        else{
            if(checkTypes(type1,type2)){
                return true;
            }
        }
        if (type1 == null &&(type2 instanceof MJTypeClass || type2 instanceof MJTypeArray ) ){
            return true;
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
                                   if(!(isSubtypeOf(clmethod.getReturnType(),supmethod.getReturnType()))){
                                           addError(clmethod,"Incorrect return type of method "+clmethod.getName());
                                           //addError(clmethod,"Incorrect return type of method "+clmethod.getName()+". Expected Type "+((MJTypeClass) supmethod.getReturnType()).getName()+ ", but was "+((MJTypeClass) clmethod.getReturnType()).getName() );
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

    MJType findField(String classname,String fieldname){
        if (s.getFieldType(classname,fieldname) == null){
            if(classTable.get(classname).getExtended() instanceof MJExtendsNothing){
                return null;
            }
            if(classTable.get(classname).getExtended() instanceof MJExtended){
                return findField(superClasses.get(classTable.get(classname)).getName(),fieldname);
            }
            else return null;
        }

        else return s.getFieldType(classname,fieldname) ;
    }

    MJType findMethod(String classname,String methodname){

        if (s.getMethodType(classname,methodname) == null){
            if(classTable.get(classname).getExtended() instanceof MJExtendsNothing){
                return null;
            }

            if(classTable.get(classname).getExtended() instanceof MJExtended){
                return findMethod(superClasses.get(classTable.get(classname)).getName(),methodname);
            }
            else{

                return null;
            }
        }

        else return s.getMethodType(classname,methodname) ;
    }

    public SymbolTable getSymbolTable(){
        return this.s;
    }
}

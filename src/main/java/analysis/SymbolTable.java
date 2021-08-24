package analysis;

import minijava.ast.*;

import java.util.*;

import static minijava.ast.MJ.MethodDecl;
import static minijava.ast.MJ.TypeClass;

public class SymbolTable{
    private Stack<Map<String,MJElement>> symbolTable;
    private Map<String,Map<String,MJType>> fields;
    private Map<String,Map<String,MJType>> methods;

    public SymbolTable(){
        symbolTable=new Stack<>();
        fields =new HashMap<>();
        methods = new HashMap<>();
    }
    public void addMethod(String classname, String methodname, MJType returntype){
        if(methods.get(classname) == null){
            Map<String,MJType> temp =new HashMap<>();
            temp.put(methodname,returntype);
            methods.put(classname,temp);
        }
        else{
            methods.get(classname).put(methodname,returntype);
        }

    }
    public MJType getMethodType (String classname, String methodname){
        if (methods.get(classname) == null){
            return null;
        }
        return methods.get(classname).get(methodname);
    }

    public void addFields(String classname,String paramname,MJType type ){
        if(fields.get(classname) == null){
            Map<String,MJType> temp =new HashMap<>();
            temp.put(paramname,type);
            fields.put(classname,temp);
        }
        else{
            fields.get(classname).put(paramname,type);
        }
    }

    public MJType getFieldType (String classname, String fieldname){
        if (fields.get(classname) == null){
            return null;
        }
        return fields.get(classname).get(fieldname);
    }

    public void enter(){
        symbolTable.push(new HashMap<>());
    }
    public void exit(){
        symbolTable.pop();
    }

    public MJType lookupType(String id){

        Iterator<Map<String,MJElement>> mappingIterator = symbolTable.iterator();
        while (mappingIterator.hasNext()) {

            Map<String, MJElement> mapping =mappingIterator.next();
            MJElement element = mapping.get(id);

            if(element != null){
                if(element instanceof MJVarDecl){
                    return ((MJVarDecl) element).getType();
                }
                if(element instanceof MJMethodDecl){
                    return ((MJMethodDecl) element).getReturnType();
                }
                if(element instanceof MJClassDecl){
                    return TypeClass(id);
                }
            }
        }
        return null;
    }

    public MJElement lookup(String id){

        Iterator<Map<String,MJElement>> mappingIterator = symbolTable.iterator();
        while (mappingIterator.hasNext()) {

            Map<String, MJElement> mapping =mappingIterator.next();
            MJElement element = mapping.get(id);

            if(element != null){
                if(element instanceof MJVarDecl){
                    return element;
                }
                if(element instanceof MJMethodDecl){
                    return element;
                }
                if(element instanceof MJClassDecl){
                    return element;
                }
            }
        }
        return null;
    }

    public Map<String, MJElement> peek(){
       return symbolTable.peek();
    }
    public Stack<Map<String, MJElement>> getSymbolTable() {
        return symbolTable;
    }
}

package frontend;


import minijava.ast.*;

import java.util.List;

public class AstHelper {

    public static void splitList(List<MJMemberDecl> memberDeclList,MJVarDeclList varDeclList,MJMethodDeclList methodDeclList ){
    //    MJVarDeclList varDeclList = MJ.VarDeclList();
    //    MJMethodDeclList methodDeclList = MJ.MethodDeclList();
        for(int i=0;i<memberDeclList.size();i++){
            if(memberDeclList.get(i) instanceof MJVarDecl){
                varDeclList.add((MJVarDecl) memberDeclList.get(i));
            }
            if(memberDeclList.get(i) instanceof MJMethodDecl){
                methodDeclList.add((MJMethodDecl) memberDeclList.get(i));
            }
        }
    }
}

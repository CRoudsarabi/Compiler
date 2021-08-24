package translation;

import analysis.SymbolTable;
import minijava.ast.*;
import minillvm.ast.*;

import java.util.*;
public class Translator {

    private MJProgram javaProg;
    private SymbolTable symbolTable;

    public Translator(MJProgram javaProg, SymbolTable symbolTable) {
        this.javaProg = javaProg;
        this.symbolTable = symbolTable;
    }

    GlobalList globals;
    ProcList proc;
    TypeStructList structs;
    BasicBlockList b;
    //BasicBlock block;
    Map<String, TemporaryVar> locals=new HashMap<>();

    public Prog translate() {
        globals=Ast.GlobalList();
        proc = Ast.ProcList();
        structs = Ast.TypeStructList();
        b= Ast.BasicBlockList();
        BasicBlock block = Ast.BasicBlock();
        b.add(block);
        ///STATEMENTS
        for(MJStatement statement : javaProg.getMainClass().getMainBody()) {
            ///// StmtExpr
           block =translateStatement(statement,block);

        }
        for(BasicBlock basicBlock : b){
            if(!basicBlock.getTerminatingInstruction().isPresent()) {
                basicBlock.add(Ast.ReturnExpr(Ast.ConstInt(0)));
            }
        }
        Proc p= Ast.Proc("main",Ast.TypeInt(),Ast.ParameterList(),b);
        proc.add(p);
    return Ast.Prog(structs,globals,proc);
    }

    // return type vielleicht BasicBlock oder Instruction?
    public BasicBlock translateStatement(MJStatement statement, BasicBlock basicBlock){

        return statement.match(new MJStatement.Matcher<BasicBlock>() {

            @Override
            public BasicBlock case_Block(MJBlock block) {
                BasicBlock b = basicBlock;
                for (MJStatement stmt: block) {
                    b = translateStatement(stmt,b);

                }
                return b;
            }

            @Override
            public BasicBlock case_StmtAssign(MJStmtAssign stmtAssign) {
                        //sollte eigentlich zu exprL
                        if(stmtAssign.getAddress() instanceof MJArrayLookup){
                            MJArrayLookup arrayLookup = (MJArrayLookup) stmtAssign.getAddress();
                            TemporaryVar size = Ast.TemporaryVar("size");
                            TemporaryVar sol = Ast.TemporaryVar("sol");
                            TemporaryVar binary = Ast.TemporaryVar("binary");
                            TemporaryVar index = Ast.TemporaryVar("index");
                            basicBlock.add(Ast.GetElementPtr(size,expr(arrayLookup.getArrayExpr(),basicBlock),Ast.OperandList(Ast.ConstInt(0))));
                            basicBlock.add(Ast.Load(sol,Ast.VarRef(size)));
                            //Zugriffswert < size?
                            basicBlock.add(Ast.BinaryOperation(index,expr(arrayLookup.getArrayIndex(),basicBlock),Ast.Slt(),Ast.VarRef(sol)));

                            basicBlock.add(Ast.BinaryOperation(binary,expr(arrayLookup.getArrayIndex(),basicBlock),Ast.Slt(),Ast.VarRef(sol)));
                            BasicBlock btrue = Ast.BasicBlock();
                            btrue.setName("true");
                            BasicBlock bfalse = Ast.BasicBlock();
                            bfalse.setName("false");
                            //IF
                            basicBlock.add(Ast.Branch(Ast.VarRef(binary),btrue,bfalse));
                            BasicBlock b2 =Ast.BasicBlock();
                            b2.setName("endIF");
                            btrue.add(Ast.Store(getExprL(stmtAssign.getAddress(),btrue),(expr(stmtAssign.getRight(),btrue))));
                            btrue.add(Ast.Jump(b2));
                            bfalse.add(Ast.HaltWithError("Index out of Bounds"));
                            b.add(btrue);
                            b.add(bfalse);
                            b.add(b2);
                            return b2;
                        }
                        else {
                            //basicBlock.add(Ast.GetElementPtr(temporaryVar,getExprL(stmtAssign.getAddress(),basicBlock),Ast.OperandList((expr(stmtAssign.getRight(),basicBlock)))));
                            basicBlock.add(Ast.Store(getExprL(stmtAssign.getAddress(), basicBlock), (expr(stmtAssign.getRight(), basicBlock))));
                            return basicBlock;
                        }

       /*             @Override
                    public BasicBlock case_FieldAccess(MJFieldAccess fieldAccess) {
                        //TODO

                       String v = fieldAccess.getReceiver().toString();
                        fieldAccess.getFieldName();
                        basicBlock.add(Ast.Store(Ast.VarRef(locals.get(v)),(expr(stmtAssign.getRight(),basicBlock))));
                        return basicBlock;
                    }

                    @Override
                    public BasicBlock case_VarUse(MJVarUse varUse) {

                        String v=varUse.getVarName();
                        basicBlock.add(Ast.Store(Ast.VarRef(locals.get(v)),(expr(stmtAssign.getRight(),basicBlock))));
                        return basicBlock;
                    }*/

            }

            @Override
            public BasicBlock case_VarDecl(MJVarDecl varDecl) {
                TemporaryVar t=Ast.TemporaryVar("exp");
                basicBlock.add(Ast.Alloca(t,getType(varDecl.getType())));
                locals.put(varDecl.getName(),t);
                return basicBlock;
            }

            @Override
            public BasicBlock case_StmtExpr(MJStmtExpr stmtExpr) {
                MJExpr expr =stmtExpr.getExpr();
                expr(expr,basicBlock);
                return basicBlock;
            }

            @Override
            public BasicBlock case_StmtIf(MJStmtIf stmtIf) {
                MJExpr expr =stmtIf.getCondition();
                Operand ref = expr(expr,basicBlock);

                MJStatement stmtTrue =stmtIf.getIfTrue();
                MJStatement stmtFalse =stmtIf.getIfFalse();
                BasicBlock btrue = Ast.BasicBlock();
                btrue.setName("true");
                BasicBlock bfalse = Ast.BasicBlock();
                bfalse.setName("false");
                basicBlock.add(Ast.Branch(ref,btrue,bfalse));
                BasicBlock b2 =Ast.BasicBlock();
                b2.setName("endIF");

                b.add(b2);
                b.add(btrue);
                BasicBlock btemp =translateStatement(stmtTrue,btrue);
                if(!btemp.getTerminatingInstruction().isPresent()) {
                    btemp.add(Ast.Jump(b2));
                }
                b.add(bfalse);
                BasicBlock btempFalse = translateStatement(stmtFalse,bfalse);
                if(!btempFalse.getTerminatingInstruction().isPresent()) {
                    btempFalse.add(Ast.Jump(b2));
                }
                return b2;
            }

            @Override
            public BasicBlock case_StmtPrint(MJStmtPrint stmtPrint) {
                basicBlock.add(Ast.Print(expr(stmtPrint.getPrinted(),basicBlock)));
                return basicBlock;
            }

            @Override
            public BasicBlock case_StmtReturn(MJStmtReturn stmtReturn) {
                MJExpr expr =stmtReturn.getResult();
                basicBlock.add(Ast.ReturnExpr(expr(expr,basicBlock)));
                return basicBlock;
            }

            @Override
            public BasicBlock case_StmtWhile(MJStmtWhile stmtWhile) {
                MJExpr expr = stmtWhile.getCondition();
                BasicBlock wh = Ast.BasicBlock();
                wh.setName("while");
                Operand ref = expr(expr,wh);
                //zum while Anfang springen
                basicBlock.add(Ast.Jump(wh));

                //neuer Block
                BasicBlock b2 =Ast.BasicBlock();
                b2.setName("endWhile");

                b.add(b2);

                //loop-body
                MJStatement stmtLoopBody = stmtWhile.getLoopBody();
                BasicBlock loopBody = Ast.BasicBlock();
                loopBody.setName("LoopBody");
                BasicBlock btemp = translateStatement(stmtLoopBody,loopBody);
                System.out.println(btemp.getTerminatingInstruction());

                //zu while-Anfang springen
                if(!btemp.getTerminatingInstruction().isPresent()) {
                    btemp.add(Ast.Jump(wh));
                }
                b.add(loopBody);
                ////while-Anfang
                b.add(wh);



                //wenn true -> loopbody, ansonsten in neuem Block weiter
                wh.add(Ast.Branch(ref,loopBody,b2));
                return b2;
            }

        });

    }
    public Type getType(MJType type) {
        return type.match(new MJType.Matcher<Type>() {

            @Override
            public Type case_TypeArray(MJTypeArray typeArray) {
                typeArray.getComponentType();
                return Ast.TypePointer(getType(typeArray.getComponentType()));
            }

            @Override
            public Type case_TypeBool(MJTypeBool typeBool) {
                return Ast.TypeBool();
            }

            @Override
            public Type case_TypeInt(MJTypeInt typeInt) {
                return Ast.TypeInt();
            }

            @Override
            public Type case_TypeClass(MJTypeClass typeClass) {
                return null;
            }
        });
    }
    public Operand expr(MJExpr expr, BasicBlock b){
        return expr.match(new MJExpr.Matcher<Operand>() {
            @Override
            public Operand case_Number(MJNumber number) {
                return Ast.ConstInt(number.getIntValue());
            }

            @Override
            public Operand case_ExprNull(MJExprNull exprNull) {
                return Ast.Nullpointer();
            }

            @Override
            public Operand case_ArrayLength(MJArrayLength arrayLength) {
               TemporaryVar t = Ast.TemporaryVar("t");
                TemporaryVar sol = Ast.TemporaryVar("sol");
                b.add(Ast.GetElementPtr(t,expr(arrayLength.getArrayExpr(),b),Ast.OperandList(Ast.ConstInt(0))));
                b.add(Ast.Load(sol,Ast.VarRef(t)));
                return Ast.VarRef(sol);
            }

            @Override
            public Operand case_BoolConst(MJBoolConst boolConst) {
                return Ast.ConstBool(boolConst.getBoolValue());
            }

            @Override
            public Operand case_ExprUnary(MJExprUnary exprUnary) {
                return  exprUnary(exprUnary,b);
            }

            @Override
            public Operand case_NewObject(MJNewObject newObject) {
                return null;
            }

            @Override
            public Operand case_ExprBinary(MJExprBinary exprBinary) {
                return exprBinary(exprBinary,b);
            }

            @Override
            public Operand case_MethodCall(MJMethodCall methodCall) {
                return null;
            }

            @Override
            public Operand case_NewArray(MJNewArray newArray) {
                //nur für int-arrays
                //System.out.println(size);
                //TypeArray type=Ast.TypeArray(getType(newArray.getBaseType()),size);
                TemporaryVar array = Ast.TemporaryVar("array");
                TemporaryVar ptr = Ast.TemporaryVar("ptr");
                TemporaryVar casted = Ast.TemporaryVar("casted");
                TemporaryVar binary = Ast.TemporaryVar("binary");
                TemporaryVar binary2 = Ast.TemporaryVar("binary2");
                b.add(Ast.BinaryOperation(binary,expr(newArray.getArraySize(),b),Ast.Mul(),Ast.ConstInt(4)));
                b.add(Ast.BinaryOperation(binary2,Ast.VarRef(binary),Ast.Add(),Ast.ConstInt(4)));
                b.add(Ast.Alloc(array,Ast.VarRef(binary2)));
                b.add(Ast.Bitcast(casted,Ast.TypePointer((Ast.TypeInt())),Ast.VarRef(array)));
                //erstes Element nehmen

                b.add(Ast.GetElementPtr(ptr,Ast.VarRef(casted),Ast.OperandList(Ast.ConstInt(0))));
                //Groesse im ersten Element speichern
                b.add(Ast.Store(Ast.VarRef(ptr),(expr(newArray.getArraySize(),b))));
                //TODO array initialisieren mit while Schleife
                return Ast.VarRef(casted);
            }

            @Override
            public Operand case_ExprThis(MJExprThis exprThis) {
                return null;
            }

            @Override
            public Operand case_Read(MJRead read) {
                TemporaryVar var = Ast.TemporaryVar("var");
                b.add(Ast.Load(var,getExprL(read.getAddress(),b)));
                return Ast.VarRef(var);
            }
        });
    }
    public Operand getExprL(MJExprL exprL, BasicBlock basicBlock){
        return exprL.match(new MJExprL.Matcher<Operand>() {
            @Override
            public Operand case_ArrayLookup(MJArrayLookup arrayLookup) {
                TemporaryVar var = Ast.TemporaryVar("temp");
                TemporaryVar binary = Ast.TemporaryVar("binary");
                basicBlock.add(Ast.BinaryOperation(binary,expr(arrayLookup.getArrayIndex(),basicBlock),Ast.Add(),Ast.ConstInt(1)));
                basicBlock.add(Ast.GetElementPtr(var,expr(arrayLookup.getArrayExpr(),basicBlock),Ast.OperandList(Ast.VarRef(binary))));

                return Ast.VarRef(var);
            }

            @Override
            public Operand case_FieldAccess(MJFieldAccess fieldAccess) {
                //TODO
                //in globals suchen?
                fieldAccess.getFieldName();
                fieldAccess.getReceiver();
                return null;
            }

            @Override
            public Operand case_VarUse(MJVarUse varUse) {
                       // b.add(Ast.Load(var,Ast.VarRef(locals.get(varUse.getVarName()))));
                        return Ast.VarRef(locals.get(varUse.getVarName()));
                //b.add(Ast.Store(Ast.VarRef(locals.get(v)),(expr(stmtAssign.getRight(),basicBlock))));

            }
        });
    }
    public Operator getOp(MJOperator operand){
        Operator o;
        switch(operand.toString()){
            case "Plus" : o=Ast.Add();break;
            case "And" : o=Ast.And();break;
            case "EQ" : o=Ast.Eq();break;
            case "Minus" : o=Ast.Sub();break;
            case "Times" : o=Ast.Mul();break;
            case "Div" : o=Ast.Sdiv();break;
            default : o=Ast.Slt();break;//less
        }
        return o;
    }

    public VarRef exprBinary(MJExprBinary binary, BasicBlock b){
        TemporaryVar sol= Ast.TemporaryVar("sol");
        Operand l = expr(binary.getLeft(),b);
        Operand r = expr(binary.getRight(),b);

        //not like this
        if(getOp(binary.getOperator()).structuralEquals(Ast.Sdiv()) && r.structuralEquals(Ast.ConstInt(0))){

            BasicBlock b2 =Ast.BasicBlock();
            b2.setName("DivByZero");
            this.b.add(b2);
            b.add(Ast.Jump(b2));
           b2.add(Ast.HaltWithError("Division by zero"));

        }else {
            b.add(Ast.BinaryOperation(sol, l, getOp(binary.getOperator()), r));
        }
        return Ast.VarRef(sol);

    }
    public Operand exprUnary(MJExprUnary unary, BasicBlock b){
        TemporaryVar sol= Ast.TemporaryVar("sol");
        if(unary.getUnaryOperator().toString().equals("UnaryMinus")){
            b.add(Ast.BinaryOperation(sol,Ast.ConstInt(0),Ast.Sub(), expr(unary.getExpr(),b)));
        }
        else if(unary.getUnaryOperator().toString().equals("Negate")){
            b.add(Ast.BinaryOperation(sol, expr(unary.getExpr(),b), Ast.Xor(), Ast.ConstBool(true)));
            }

        else{
            b.add(Ast.HaltWithError("unaryOp not valid"));
        }
        return Ast.VarRef(sol);
    }
}

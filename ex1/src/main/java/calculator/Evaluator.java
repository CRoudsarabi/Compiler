package calculator;

import calculator.ast.*;

public class Evaluator {
    private int solution;

    /**
     * Starting method to evaluate the expression
     * and to assign the evaluation to the variable solution
     * @param e the expression
     */
    public void run(Expr e){

      solution=calc(e);

    }

    /**
     *  Evaluates the expression and returns the solution.
     *  Returns 0 if
     * @param e the expression
     * @return the solution of the evaluation of the expression as int
     */
    private int calc(Expr e){
        if(e instanceof ExprNumber){
            return ((ExprNumber) e).getValue();
        }
        else if(e instanceof Paren){
            return calc(((Paren) e).getC());
        }
        else if(e instanceof ExprPLUS){
            return calc(((ExprPLUS) e).getLeft())+calc(((ExprPLUS) e).getRight());
        }
        else if(e instanceof ExprMinus){
            return calc(((ExprMinus) e).getLeft())-calc(((ExprMinus) e).getRight());
        }
        else if(e instanceof ExprMUL){
            return calc(((ExprMUL) e).getLeft())*calc(((ExprMUL) e).getRight());
        }
        else if(e instanceof ExprDiv){
            return calc(((ExprDiv) e).getLeft())/calc(((ExprDiv) e).getRight());
        }
        else if(e instanceof ExprLess){
            if(calc(((ExprDiv) e).getLeft())<calc(((ExprDiv) e).getRight())){
                return 1;
            }
            return 0;
        }
        else if(e instanceof ExprEquals){
            if(calc(((ExprEquals) e).getLeft())==calc(((ExprEquals) e).getRight())){
                return 1;
            }
            return 0;
        }
        else if(e instanceof ExprIf){
            if(calc(((ExprIf) e).getLeft())==1){return calc(((ExprIf) e).getMiddle());}
            else{
                return calc(((ExprIf) e).getRight());
            }

        }
        return 0;
    }


    public int getSolution() {
        return solution;
    }
}

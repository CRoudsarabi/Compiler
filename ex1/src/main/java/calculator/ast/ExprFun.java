package calculator.ast;

public class ExprFun extends CExpr{
    private AExpr left;
    private Expr right;

    public ExprFun(ExprID left, Expr right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + "fun" + " " + left + " -> "+ right + ")";
    }

    public AExpr getLeft() {
        return left;
    }

    public Expr getRight() {
        return right;
    }
}

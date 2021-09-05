package calculator.ast;

public class ExprLet extends Expr {
    private ExprID left;
    private Expr middle;
    private Expr right;

    public ExprLet(ExprID left,Expr middle, Expr right) {
        this.left = left;
        this.middle= middle;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + "let" + " " + left + " = "+ middle + " in " + right + ")";
    }

    public AExpr getLeft() {
        return left;
    }

    public Expr getMiddle() {
        return middle;
    }

    public Expr getRight() {
        return right;
    }

}

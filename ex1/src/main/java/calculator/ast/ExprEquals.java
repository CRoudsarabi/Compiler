package calculator.ast;

public class ExprEquals extends Comp {
    private Comp left;
    private CExpr right;
    public ExprEquals(Comp left, CExpr right) {
        this.left = left;
        this.right = right;

    }

    @Override
    public String toString() {
        return "(" + left + " " + "==" + " " + right + ")";
    }

    public Comp getLeft() {
        return left;
    }

    public CExpr getRight() {
        return right;
    }
}

package calculator.ast;

public class ExprLess extends Comp {
    private Comp left;
    private CExpr right;
    public ExprLess(Comp left, CExpr right) {
        this.left = left;
        this.right = right;

    }

    @Override
    public String toString() {
        return "(" + left + " " + "<" + " " + right + ")";
    }

    public Comp getLeft() {
        return left;
    }

    public CExpr getRight() {
        return right;
    }
}

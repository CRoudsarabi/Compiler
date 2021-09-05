package calculator.ast;

public class ExprMinus extends CExpr {
    private CExpr left;
    private Term right;

    public ExprMinus(CExpr left, Term right) {
        this.left = left;
        this.right = right;

    }

    @Override
    public String toString() {
        return "(" + left + " " + "-" + " " + right + ")";
    }

    public CExpr getLeft() {
        return left;
    }

    public Term getRight() {
        return right;
    }
}

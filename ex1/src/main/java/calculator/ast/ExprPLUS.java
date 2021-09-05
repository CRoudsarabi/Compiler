package calculator.ast;

public class ExprPLUS extends CExpr {
    private CExpr left;
    private Term right;

    public ExprPLUS(CExpr left, Term right) {
        this.left = left;
        this.right = right;

    }

    @Override
    public String toString() {
        return "(" + left + " " + "+" + " " + right + ")";
    }

    public CExpr getLeft() {
        return left;
    }

    public Term getRight() {
        return right;
    }

}

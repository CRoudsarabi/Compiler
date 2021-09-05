package calculator.ast;

public class ExprIf extends Expr {
    private Expr left;
    private Expr middle;
    private Expr right;

    public ExprIf(Expr left, Expr middle, Expr right) {
        this.left = left;
        this.middle=middle;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + "if" + " " + left + " then "+ middle+ " else " + right + ")";
    }

    public Expr getLeft() {
        return left;
    }

    public Expr getMiddle() {
        return middle;
    }

    public Expr getRight() {
        return right;
    }

}

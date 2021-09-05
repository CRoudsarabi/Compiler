package calculator.ast;

public class ExprDiv extends Term {
    private Term left;
    private Funct right;

    public ExprDiv(Term left, Funct right) {
        this.left = left;
        this.right = right;

    }

    @Override
    public String toString() {
        return "(" + left + " " + "/" + " " + right + ")";
    }

    public Term getLeft() {
        return left;
    }

    public Funct getRight() {
        return right;
    }
}

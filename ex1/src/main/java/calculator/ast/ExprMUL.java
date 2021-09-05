package calculator.ast;

public class ExprMUL extends Term {
    private Term left;
    private Funct right;

    public ExprMUL(Term left, Funct right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "(" + left + " " + "*" + " " + right + ")";
    }

    public Term getLeft() {
        return left;
    }

    public Funct getRight() {
        return right;
    }

}



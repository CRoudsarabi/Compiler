package calculator.ast;

public class Paren extends AExpr {
    private Expr c;

    public Paren(Expr c){
        this.c =c;
    }

    public String toString() {
        return c.toString();
    }

    public Expr getC() {
        return c;
    }
}

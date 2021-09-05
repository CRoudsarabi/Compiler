package calculator.ast;

public class ExprID extends AExpr{
    private String value;

    public ExprID(String value) {
        super();
        this.value = value;
    }
    @Override
    public String toString() {
        return value;
    }

}

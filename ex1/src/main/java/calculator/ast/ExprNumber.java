package calculator.ast;

public class ExprNumber extends AExpr {
	private int value;

	public ExprNumber(int value) {
		super();
		this.value = value;
	}

	public ExprNumber(String value) {
	    this.value = Integer.parseInt(value);
    }

	@Override
	public String toString() {
		return "" + value;
	}

	public int getValue() {
		return value;
	}
}

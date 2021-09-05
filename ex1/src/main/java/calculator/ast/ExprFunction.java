package calculator.ast;

public class ExprFunction extends Funct{
        private Funct left;
        private AExpr right;

        public ExprFunction(Funct left, AExpr right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "(" + left + " "+ right + ")";
        }

        public Funct getLeft() {
            return left;
        }

        public AExpr getRight() {
            return right;
        }
}

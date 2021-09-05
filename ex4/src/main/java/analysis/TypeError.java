package analysis;

import frontend.SourcePosition;
import frontend.SyntaxError;
import minijava.ast.MJElement;

public class TypeError extends SyntaxError {

    public TypeError(String message, int line, int column) {
        super(message, line, column);
    }

    public TypeError(MJElement element, String message) {
        super(element, message);
    }
}
package minijava.parser;


import frontend.AstPrinter;
import frontend.MJFrontend;
import frontend.SyntaxError;
import minijava.ast.MJElement;
import minijava.ast.MJMethodCall;
import minijava.ast.MJProgram;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class OurTests {


    @Test
    public void myTestPrint() throws Exception {
        String input = "class Test { public static void main(String[] args) { System.out.println(42); }}";
        MJProgram ast = new MJFrontend().parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(printed);
        Assert.assertThat(printed, CoreMatchers.containsString("System.out.println(42);"));
    }

    @Test
    public void myTestIF() throws Exception {
        String input = "class Test { public static void main(String[] args) { if (true) test.length; else test.length; }}";
        MJProgram ast = new MJFrontend().parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(printed);
        Assert.assertThat(printed, CoreMatchers.containsString("if (true) test.length;"));
    }

    @Test
    public void myTestWhile() throws Exception {
        String input = "class Test { public static void main(String[] args) { while (true) {x=1;} }}";
        MJProgram ast = new MJFrontend().parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(printed);
        Assert.assertThat(printed, CoreMatchers.containsString("while (true) x = 1;"));
    }

    @Test
    public void myTestVarDecl() throws Exception {
        String input = "class Test { public static void main(String[] args) { int x; }}";
        MJProgram ast = new MJFrontend().parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(printed);
        Assert.assertThat(printed, CoreMatchers.containsString("int x;"));
    }

    @Test
    public void myTestAddition() throws Exception {
        String input = "class Test { public static void main(String[] args) { x= 2+2; }}";
        MJProgram ast = new MJFrontend().parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(printed);
        Assert.assertThat(printed, CoreMatchers.containsString("x = (2 + 2);"));
    }

    @Test
    public void myTestComplexAddition() throws Exception {
        String input = "class Test { public static void main(String[] args) { x= 2+2 +5 +6; }}";
        MJProgram ast = new MJFrontend().parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(printed);
        Assert.assertThat(printed, CoreMatchers.containsString("x = (((2 + 2) + 5) + 6);"));
    }

    @Test
    public void multipleClasses() throws Exception {
        String input = "class Test { public static void main(String[] args) {}} class Banana { }";
        MJProgram ast = new MJFrontend().parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(printed);
        Assert.assertThat(printed, CoreMatchers.containsString(" Banana"));
    }

    @Test
    public void thisTestNeedsToPrintNull() throws Exception {
        String input = "nonsense";
        MJProgram ast = new MJFrontend().parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(printed);
    }

    @Test
    public void arrayAccess() throws Exception {
        String input = "class Main { public static void main(String[] args) { int x; new int[5][2]; }}";
        MJProgram ast = new MJFrontend().parseString(input);
        MJFrontend frontend = new MJFrontend();
        frontend.parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(frontend.getSyntaxErrors());
        System.out.println(printed);
        assertFalse(frontend.getSyntaxErrors().isEmpty());
    }

    @Test
    public void testMultipleArgumetns() throws Exception {
        String input = "class Main{public static void main(String[] a){x=a.s(1,2,(f+g));}}";
        MJProgram ast = new MJFrontend().parseString(input);
        MJFrontend frontend = new MJFrontend();
        frontend.parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(frontend.getSyntaxErrors());
        System.out.println(printed);
        Assert.assertThat(printed, CoreMatchers.containsString("x = a.s(1, 2, (f + g));"));
    }

    @Test
    public void testMultipleParameters() throws Exception {
        String input = "class Main{ public static void main(String[] a){}} class A{ int m(int a, boolean b, int c){return 0;}}";
        MJProgram ast = new MJFrontend().parseString(input);
        MJFrontend frontend = new MJFrontend();
        frontend.parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(frontend.getSyntaxErrors());
        System.out.println(printed);
        Assert.assertThat(printed, CoreMatchers.containsString("int m(int a, boolean b, int c)"));
    }

    @Test
    public void operators() throws Exception {
        String input = "class Main { public static void main(String[] args) { boolean x; x = ((((3 * 4) + 5) < 2) && (1 < 3)); }}";
        MJProgram ast = new MJFrontend().parseString(input);
        MJFrontend frontend = new MJFrontend();
        frontend.parseString(input);
        String printed = AstPrinter.print(ast);
        System.out.println(frontend.getSyntaxErrors());
        System.out.println(printed);
        Assert.assertThat(printed, CoreMatchers.containsString("x = ((((3 * 4) + 5) < 2) && (1 < 3))"));
    }
}

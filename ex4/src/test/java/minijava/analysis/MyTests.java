package minijava.analysis;

import analysis.Analysis;
import frontend.MJFrontend;
import frontend.SyntaxError;
import minijava.ast.MJProgram;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.fail;

public class MyTests {

    @Rule
    public Timeout globalTimeout = Timeout.seconds(2);



    @Test
    public void lookup() {
        expectOk(
                "class Main { public static void main(String[] args) { int a; int b; a = b; } }"

        );
    }

    @Test
    public void lookupError() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) { int a; boolean b; a = b; } }"

        );
    }

    @Test
    public void lookupError2() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) { int a; int b; boolean b;   a = b; } }"

        );
    }

    @Test
    public void bool() {
        expectOk(
                "class Main { public static void main(String[] args) {  boolean a; boolean b;   b = false; a = true; a = b;} }"

        );
    }

    @Test
    public void print() {
        expectOk(
                "class Main { public static void main(String[] args) {  System.out.println(2); } }"

        );
    }

    @Test
    public void printError() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) {  System.out.println(true); } }"

        );
    }


    @Test
    public void addition() {
        expectOk(
                "class Main { public static void main(String[] args) {int a; int b; int c; c = a + b; } }"

        );
    }

    @Test
    public void additionError() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) {int a; int b; boolean c; c = a + b; } }"

        );
    }

    @Test
    public void complexAddition() {
        expectOk(
                "class Main { public static void main(String[] args) {int a; int b; int c; c = a + b - 3 * 2; } }"

        );
    }

    @Test
    public void methodCall() {
        expectOk(
                "class Main { public static void main(String[] args) {A a; int b; b = a.foo(); } }",
                "class A {",
                "int foo() { return 0; }",
                "}"

        );
    }

    @Test
    public void methodCallArguments() {
        expectOk(
                "class Main { public static void main(String[] args) {A a; int b; int c; int d; b = a.foo(c,d); } }",
                "class A {",
                "int foo(int a, int b) { return 0; }",
                "}"

        );
    }

    @Test
    public void methodCallError() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) {int b; b = a.foo(); } }"

        );
    }

    @Test
    public void methodCallError2() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) {int b; int a; b = a.foo(); } }"

        );
    }

    @Test
    public void methodCallArgumentsError() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) {A a; int b; int c; int d; b = a.foo(c,!2); } }",
                "class A {",
                "int foo(int a, int b) { return 0; }",
                "}"

        );
    }

    @Test
    public void methodCallArgumentsError2() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) {A a; int b; boolean c; int d; b = a.foo(c,d); } }",
                "class A {",
                "int foo(int a, int b) { return 0; }",
                "}"

        );
    }



    @Test
    public void newObjectError() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) {A a; } }"

        );
    }


    @Test
    public void newObject() {
        expectOk(
                "class Main { public static void main(String[] args) {A a; } }"," class A {}"


        );
    }

    @Test
    public void arrayError() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) { boolean[] ar; ar = new int[20]; } }"


        );
    }

    @Test
    public void array() {
        expectOk(
                "class Main { public static void main(String[] args) { int[] ar; ar = new int[20]; } }"


        );
    }

    @Test
    public void array2() {
        expectOk(
                "class Main { public static void main(String[] args) { int[] ar; ar = new int[20]; ar[8] = 5; } }"


        );
    }

    @Test
    public void newArrayError() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) { boolean banana; int[] ar; ar = new int[banana]; ar[8] = 5; } }"


        );
    }

    @Test
    public void arrayLength() {
        expectOk(
                "class Main { public static void main(String[] args) { int[] ar; ar = new int[20]; ar[8] = 5; ar.length; } }"


        );
    }

    @Test
    public void arrayLengthError() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) { boolean banana; int[] ar; ar = new int[20]; ar[8] = 5; banana.length; } }"


        );
    }

    @Test
    public void boolBinop() {
        expectOk(
                "class Main { public static void main(String[] args) { boolean a; boolean b; boolean c; c = a && b; } }"


        );
    }

    @Test
    public void boolBinop2() {
        expectOk(
                "class Main { public static void main(String[] args) { boolean a;  boolean c; c = a && false; } }"


        );
    }


    @Test
    public void boolBinop3() {
        expectOk(
                "class Main { public static void main(String[] args) { boolean a;  boolean c; c = (1==1); } }"


        );
    }


    @Test
    public void boolBinopError() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) { boolean a;  boolean c; c = 3 && 4; } }"


        );
    }

    @Test
    public void boolBinopError2() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) { boolean a;  boolean c; c = (false==2); } }"


        );
    }

    @Test
    public void boolUnop() {
        expectOk(
                "class Main { public static void main(String[] args) { boolean a; boolean b; boolean c; c = !b; } }"


        );
    }

    @Test
    public void boolUnopError() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) { boolean a; boolean b; boolean c; c = -b; } }"


        );
    }

    @Test
    public void boolUnopError2() {
        expectTypeErrors(
                "class Main { public static void main(String[] args) { boolean a; boolean b; int c; c = -b; } }"


        );
    }


    private void expectTypeErrors(String... inputLines) {
        test(true, inputLines);
    }

    private void expectOk(String... inputLines) {
        test(false, inputLines);
    }

    private void test(boolean expectError, String ... inputLines) {
        try {
            String input = String.join("\n", inputLines);
            MJFrontend frontend = new MJFrontend();
            MJProgram program = frontend.parseString(input);
            if (!frontend.getSyntaxErrors().isEmpty()) {
                SyntaxError syntaxError = frontend.getSyntaxErrors().get(0);
                fail("Unexpected syntax error in line " + syntaxError.getLine() + ")\n" + syntaxError.getMessage());
            }

            Analysis analysis = new Analysis(program);
            analysis.check();


            if (expectError) {
                assertFalse("There should be type errors.", analysis.getTypeErrors().isEmpty());
            } else {
                if (!analysis.getTypeErrors().isEmpty()) {
                    throw analysis.getTypeErrors().get(0);
                }
                assertTrue("There should be no type errors.", analysis.getTypeErrors().isEmpty());
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package calculator;

import calculator.ast.Expr;
import exprs.ExprParser.ParserError;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class tests the parser with some input strings.
 * <p>
 * Before you run this test you have to make the method Main.parseToAST public.
 **/
public class ParserTest {



    @Test
    public void testOk1() throws Exception {
        String input = "((5*3) + 4)";
        Expr e = Main.parseString(input);
        assertEquals("((5 * 3) + 4)", e.toString());
    }

    @Test
    public void testOk2() throws Exception {
        String input = "let x  = 5 in (x*3)";
        Expr e = Main.parseString(input);
        assertEquals("(let x = 5 in (x * 3))", e.toString());
    }
    @Test
    public void testOk3() throws Exception {
        String input = "let y  = 2 in if (y==2) then (y + 3) else (y-1)";
        Expr e = Main.parseString(input);
        assertEquals("(let y = 2 in (if (y == 2) then (y + 3) else (y - 1)))", e.toString());
    }

    @Test
    public void testOk4() throws Exception {
        String input = "let y1 = 2 in ((fun x1 -> (x1*3))  y1)";
        Expr e = Main.parseString(input);
        assertEquals("(let y1 = 2 in ((fun x1 -> (x1 * 3)) y1))", e.toString());
    }

    @Test(expected = ParserError.class)
    public void testFail1() throws Exception {
        String input = "((5*3) + 4";
        Main.parseString(input);
    }

    @Test(expected = ParserError.class)
    public void testFail2() throws Exception {
        String input = "if (3==4) then 0";
        Main.parseString(input);
    }

    @Test
    public void lambdaWithSpace() throws Exception {
        String input = "(fun  x -> x)";
        Expr e = Main.parseString(input);
        assertEquals("(fun x -> x)", e.toString());
    }


    @Test
    public void testLet2() throws Exception {
        String input = "5 * (let x = 1 in x)";
        Expr e = Main.parseString(input);
        assertEquals("(5 * (let x = 1 in x))", e.toString());
    }
    @Test
    public void functionCall() throws Exception {
        String input = "5 * f 1";
        Expr e = Main.parseString(input);
        assertEquals("(5 * (f 1))", e.toString());
    }

    @Test
    public void functionCall2() throws Exception {
        String input = "f x y z";
        Expr e = Main.parseString(input);
        assertEquals("(((f x) y) z)", e.toString());
    }

    //EIGENE TESTS

    @Test
    public void testAddition() throws Exception {
        String input = "5+5";
        Expr e = Main.parseString(input);
        assertEquals("(5 + 5)", e.toString());
    }

    @Test
    public void testMultiplication() throws Exception {
        String input = "5*5";
        Expr e = Main.parseString(input);
        assertEquals("(5 * 5)", e.toString());
    }

    @Test
    public void testLess() throws Exception {
        String input = "5 < 4";
        Expr e = Main.parseString(input);
        assertEquals("(5 < 4)", e.toString());
    }


    @Test
    public void testSimpleFunction() throws Exception {
        String input = "f 5";
        Expr e = Main.parseString(input);
        assertEquals("(f 5)", e.toString());
    }

    @Test
    public void testComplexFunction() throws Exception {
        String input = "f 0 + g 1 2";
        Expr e = Main.parseString(input);
        assertEquals("((f 0) + ((g 1) 2))", e.toString());
    }


    @Test
    public void testOperatorOrder() throws Exception {
        String input = "5*4+3+2*2";
        Expr e = Main.parseString(input);
        assertEquals("(((5 * 4) + 3) + (2 * 2))", e.toString());
        //System.out.println(5*4+3+2*2);
        //System.out.println((((5 * 4) + 3) + (2 * 2)));
    }

}

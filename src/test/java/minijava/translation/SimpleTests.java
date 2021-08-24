package minijava.translation;


import org.junit.Test;

public class SimpleTests {

	@Test
	public void println() throws Exception {
		testStatements(
				"System.out.println(42);"
		);
	}
	@Test
	public void ret() throws Exception {
		testStatements(
				"boolean x;",
						" x = !true;"
		);
	}
	@Test
	public void array() throws Exception {
		testStatements(
				"int[] a;"
		);
	}

    @Test
    public void array2() throws Exception {
        testStatements(
                "int[] a;",
                " a = new int[10];",
				" a[1] = 3;",
				" a[2] = 3;",
				" a[4] = 3;",
				" a[5] = 3;",
				" a[6] = 3;",
				" a[7] = 3;",
				" a[8] = 3;",
				" a[9] = 3;",
				"System.out.println(a[3]);"

        );
    }

	@Test
	public void math() throws Exception {
		testStatements(
				"int x;",
				" x = 12;",
				"x=x-1;",
				"System.out.println(x);"
		);
	}
	@Test
	public void testIf() throws Exception {
		testStatements(
				"int x;",
				"x = 42;",
				"if (43 < x) {",
				"	System.out.println(11);",
				"}",
				"else {",
				"	System.out.println(10);",
				"}",
				"System.out.println(22);"
		);
	}
	@Test
	public void testIfIf() throws Exception {
		testStatements(
				"int x;",
				"x = 42;",
				"if (0 < x) {",
				"if (0 < x) {",
				"	x = x - 1;",
				"	System.out.println(x);",
				"}",
				"else {",
				"	System.out.println(10);",
				"}",
				"}",
				"else {",
				"	System.out.println(10);",
				"}",
				"System.out.println(x);"


		);
	}
	@Test
	public void testIfWhile() throws Exception {
		testStatements(
				"int x;",
				"x = 42;",
				"if (0 < x) {",
				"	System.out.println(x);",
				"while (0 < x) {",
				"	System.out.println(x);",
				"	x = x - 1;",
				"}",
				"int y;",
				"}",
				"else {",
				"	System.out.println(10);",
				"}",
				"System.out.println(22);"


		);
	}
	@Test
	public void testWhileIf() throws Exception {
		testStatements(
				"int x;",
				"x = 42;",
				"while (0 < x) {",
				"	System.out.println(x);",
				"	x = x - 1;",
				"if (0 < x) {",
				"	System.out.println(x);",
				"}",
				"else {",
				"	System.out.println(10);",
				"}",
				"}",
				"int y;",
				"System.out.println(22);"


		);
	}
	@Test
	public void test0() throws Exception {
		testStatements(
				"System.out.println(42 * 7 + 3);"
		);
	}

	@Test
	public void test1() throws Exception {
		testStatements(
				"int x;",
				"x = 42;",
				"x = x + 1;",
				"System.out.println(x);"
		);
	}


	@Test
	public void test2() throws Exception {
		testStatements(
				"int x;",
				"x = 2;",
				"while (0 < x) {",
				"	System.out.println(x);",
				"	x = x - 1;",
				"}",
				"int y;"
		);
	}

	private void testStatements(String...inputLines) throws Exception {
		String input = "class Main { public static void main(String[] args) {\n"
				+ String.join("\n", inputLines)
				+ "\n}}\n";
		TranslationTestHelper.testLLVMTranslation("Test.java", input);
	}


}

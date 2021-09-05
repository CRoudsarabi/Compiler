A1 (Steffen)

Multi-line comments must start must start with "{-" and end with "-}. In between all characters are allowed so we used [^]* for them.
The curly braces must be written in quotes so that they are interpreted correctly. Comments shall be eliminated by the lexer, so nothing is returned.
"iff" is recognized as an identifier because of the "longest match"-rule.
"if" is recognized as a keyword because of the rule priority. The rule for the keyword "if" is written before the rule for identifiers.

A2 (Steffen and Constantin)

We defined six non-terminals in our grammar. One for each type of operation (analogous to the exercise session). The right side of each operation will always use a different non terminal. 
This way we can remove the ambiguity in the grammar and enforce the correct operator order. On the top level we have expr, which we use for the let, lamda and if statements.Comp is used for comparisons (<,==). 
Cexpr for plus and minus. Term for multiplication and division. Funct for functions and Aexpr for Numbers and IDs. We can convert a AExpr back into an Expr by adding Parenthesis. 
We add a corresponding class for each non-termial and operation in our AST.

A3 (Steffen and Constantin)

We created an Evaluator-class with a run-method to start the evaluation and a method calc where the evaluation actually happens.
The methods use the expression as parameter. In multiple if-then-else statements we check which kind of expression we have and return
the value of the number if we have an ExprNumber. Otherwise we return the function-call of the same method with the sub expressions and the
the corresponding operator. The solution of the evaluation is assigned to the variable solution which is returned in the evaluate-method in
the Main-class.
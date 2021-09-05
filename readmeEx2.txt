Lab 2 

Changes to the Grammar:

Every time a production had an optional parameter, we added a new non terminal symbol that can reproduce itself multiple times or become epsilon. This way we create a variable length list of this parameter.
(For example the non terminal ClassDeclList used to create a List of ClassDecl).

To split our MemberDecl into the MethodDecl and VarDecl we created a class AstHelper that uses a static method to filter a List of MemberDecl for VarDec and MethodDecl.

We do not use the Op non-terminal symbol and have instead a unique production for each mathematic expression in the Expr non-terminal.
We handeled operator associativity with the "left precedence" keyword below our terminal symbols.

To fix the Array access we have split the Expr non-terminal into two different non-terminals (Expr2 and Expr). Expr2 contains only array creation or can be made into a Expr. 
Expr2 is used for most productions but during Array access we only allow Expr so the Array creation is avoided.

MultiArray tests:
We use a new non-terminal called Brackets to read multiple empty bracktes after an Array creation but it does not create anything in the ast. Therefore in the two multiarray tests can understand the programm without Syntax errors but do not print the correct solution.

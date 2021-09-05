Lab 4

We use our Method translateStatement on each statement in the main body. Using a matcher we then recursively go through the ast calling a method for each non terminal. 
We translate each statement into MiniLLVM and return the modified basicblock.
Method Sideeffects: If we declare a variable we put it in our locals.

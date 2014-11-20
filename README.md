# PLD Lab #

## Lab 6 - 7 ##

Can parse all expressions.

### Run the test program ###

	$ cd Clite-student
	$ java Parser ../parser-test/exprTest.cpp

Indentation is implemented by passing a primitive int as a parameter to `display()`. The int is the indentation level. Terminals print at the given indentation level, non-terminals call their children with `indent+1`.

# PLD Lab #

## Lab 5 ##

Implement the display methods.

### Run the test program ###

	$ cd Clite-student
	$ java Parser ../parser-test/prog2.cpp

Indentation is implemented by passing a primitive int as a parameter to `display()`. The int is the indentation level. Terminals print at the given indentation level, non-terminals call their children with `indent+1`.

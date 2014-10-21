# PLD Lab 3 #

## Compile the Lexer
	$ cd Clite-student
	$ javac Lexer.java

## Run the test cases
From inside `project_root/Clite-student` directory:

    $ java Lexer ../ambiguous.txt | diff ../ambiguous.expected.txt -
    $ java Lexer ../single-char-lex.txt | diff ../single-char-lex.expected.txt -
    $ java Lexer ../lexeme_list.expected.txt | diff ../lexeme_list.expected.txt -

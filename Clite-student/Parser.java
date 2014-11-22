import java.util.*;

public class Parser {
    // Recursive descent parser that inputs a C++Lite program and 
    // generates its abstract syntax.  Each method corresponds to
    // a concrete syntax grammar rule, which appears as a comment
    // at the beginning of the method.
  
    Token token;          // current token from the input stream
    Lexer lexer;
  
    public Parser(Lexer ts) { // Open the C++Lite source program
        lexer = ts;                          // as a token stream, and
        token = lexer.next();            // retrieve its first Token
    }
  
    private String match (TokenType t) {
        String value = token.value();
        if (token.type().equals(t))
            token = lexer.next();
        else
            error(t);
        return value;
    }

    private void error(TokenType tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    private void error(String tok) {
        System.err.println("Syntax error: expecting: " + tok 
                           + "; saw: " + token);
        System.exit(1);
    }
  
    public Program program() {
        // Program --> void main ( ) '{' Declarations Statements '}'
        TokenType[ ] header = {TokenType.Int, TokenType.Main,
                          TokenType.LeftParen, TokenType.RightParen};
        for (int i=0; i<header.length; i++)   // bypass "int main ( )"
            match(header[i]);
        
        match(TokenType.LeftBrace);
        // student exercise
        
        Declarations d = declarations();
        Block b = statements();
        
        match(TokenType.RightBrace);
        
        return new Program(d, b);
    }
  
    // braces = one or more
    private Declarations declarations() {
        // Declarations --> { Declaration }
    	Declarations ds = new Declarations();
    	while ( isType() ) {
    		declaration(ds);
    	}
    	return ds;
    }
    
    
    private void declaration (Declarations ds) {
        // Declaration  --> Type Identifier { , Identifier } ;
        // student exercise

    	Type t = type();
    	Variable v = new Variable( match(TokenType.Identifier) );
    	Declaration d = new Declaration(v, t);
    	ds.add(d);
    	while ( !token.type().equals(TokenType.Semicolon) ) {
        	match(TokenType.Comma);
        	v = new Variable( match(TokenType.Identifier) );
        	d = new Declaration(v, t);
        	ds.add(d);
    	}
    	match(TokenType.Semicolon);
    }
  
    private Type type () {
        // Type  -->  int | bool | float | char
        // student exercise
    	
    	match(TokenType.Int);
        return Type.INT;
    }
  
    private Statement statement() {
        // Statement --> ; | Block | Assignment | IfStatement | WhileStatement
        // student exercise
    	if ( token.type().equals(TokenType.Semicolon) ) {
    		match(TokenType.Semicolon);
    		return new Skip();
    	} else if ( token.type().equals(TokenType.LeftBrace) ) {
    		match(TokenType.LeftBrace);
    		Block b = statements();
    		match( TokenType.RightBrace );
    		return b;
    	} else if ( token.type().equals(TokenType.Identifier) ) {
    		return assignment();
    	} else if ( token.type().equals(TokenType.If) ) {
    		return ifStatement();
    	} else if ( token.type().equals(TokenType.While) ) {
    		return whileStatement();
    	} else {
    		throw new Error("in Parser.statement");
    	}
    }

    private Block statements () {
        // Block --> '{' Statements '}'
        // student exercise
        Block b = new Block();
        while ( !token.type().equals(TokenType.RightBrace) ) {
        	b.members.add( statement() );
        }
        return b;
    }

    private Assignment assignment () {
        // Assignment --> Identifier = Expression ;
        // student exercise
    	Variable v = new Variable(match(TokenType.Identifier));
    	match(TokenType.Assign);
    	Expression e = expression();
    	match(TokenType.Semicolon);
    	return new Assignment(v,e);
    }

    private Conditional ifStatement () {
        // IfStatement --> if ( Expression ) Statement [ else Statement ]
        // student exercise
    	match(TokenType.If);
    	match(TokenType.LeftParen);
    	Expression e = expression();
    	match(TokenType.RightParen);
    	Statement s = statement();
    	Statement elStat = null;
    	if ( token.type().equals(TokenType.Else) ) {
    		match(TokenType.Else);
    		elStat = statement();
    	}
    	return new Conditional(e, s, elStat);
    }
  
    private Loop whileStatement () {
        // WhileStatement --> while ( Expression ) Statement
        // student exercise
    	match(TokenType.While);
    	match(TokenType.LeftParen);
    	Expression e = expression();
    	match(TokenType.RightParen);
    	Statement s = statement();
    	return new Loop(e, s);
    }

    private Expression expression () {
        // Expression --> Conjunction { || Conjunction }
    	// student exercise
    	Expression e = conjunction();
        while ( token.type().equals(TokenType.Or) ) {
            Operator op = new Operator( match(token.type()) );
            Expression term2 = conjunction();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression conjunction () {
        // Conjunction --> Equality { && Equality }
    	// student exercise
    	Expression e = equality();
        while ( token.type().equals(TokenType.And) ) {
            Operator op = new Operator( match(token.type()) );
            Expression term2 = equality();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression equality () {
        // Equality --> Relation [ EquOp Relation ]
    	// student exercise
    	Expression e = relation();
        if ( isEqualityOp() ) {
            Operator op = new Operator( match(token.type()) );
            Expression term2 = relation();
            e = new Binary(op, e, term2);
        }
        return e;
    }

    private Expression relation (){
        // Relation --> Addition [RelOp Addition]
    	// student exercise
    	Expression e = addition();
        if ( isRelationalOp() ) {
            Operator op = new Operator( match(token.type()) );
            Expression term2 = addition();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression addition () {
        // Addition --> Term { AddOp Term }
        Expression e = term();
        while (isAddOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = term();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression term () {
        // Term --> Factor { MultiplyOp Factor }
        Expression e = factor();
        while (isMultiplyOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term2 = factor();
            e = new Binary(op, e, term2);
        }
        return e;
    }
  
    private Expression factor() {
        // Factor --> [ UnaryOp ] Primary 
        if (isUnaryOp()) {
            Operator op = new Operator(match(token.type()));
            Expression term = primary();
            return new Unary(op, term);
        }
        else return primary();
    }
  
    private Expression primary () {
        // Primary --> Identifier | Literal | ( Expression )
        //             | Type ( Expression )
        Expression e = null;
        if (token.type().equals(TokenType.Identifier)) {
            e = new Variable(match(TokenType.Identifier));
        } else if (isLiteral()) {
            e = literal();
        } else if (token.type().equals(TokenType.LeftParen)) {
            token = lexer.next();
            e = expression();
            match(TokenType.RightParen);
        } else if (isType( )) {
            Operator op = new Operator(match(token.type()));
            match(TokenType.LeftParen);
            Expression term = expression();
            match(TokenType.RightParen);
            e = new Unary(op, term);
        } else error("Identifier | Literal | ( | Type");
        return e;
    }

    private Value literal( ) {
    	// student exercise
    	Value lit = new IntValue( Integer.parseInt(token.value()) );
		token = lexer.next();
    	return lit;
    }
  

    private boolean isAddOp( ) {
        return token.type().equals(TokenType.Plus) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isMultiplyOp( ) {
        return token.type().equals(TokenType.Multiply) ||
               token.type().equals(TokenType.Divide);
    }
    
    private boolean isUnaryOp( ) {
        return token.type().equals(TokenType.Not) ||
               token.type().equals(TokenType.Minus);
    }
    
    private boolean isEqualityOp( ) {
        return token.type().equals(TokenType.Equals) ||
            token.type().equals(TokenType.NotEqual);
    }
    
    private boolean isRelationalOp( ) {
        return token.type().equals(TokenType.Less) ||
               token.type().equals(TokenType.LessEqual) || 
               token.type().equals(TokenType.Greater) ||
               token.type().equals(TokenType.GreaterEqual);
    }
    
    private boolean isType( ) {
        return token.type().equals(TokenType.Int)
            || token.type().equals(TokenType.Bool) 
            || token.type().equals(TokenType.Float)
            || token.type().equals(TokenType.Char);
    }
    
    private boolean isLiteral( ) {
        return token.type().equals(TokenType.IntLiteral) ||
            isBooleanLiteral() ||
            token.type().equals(TokenType.FloatLiteral) ||
            token.type().equals(TokenType.CharLiteral);
    }
    
    private boolean isBooleanLiteral( ) {
        return token.type().equals(TokenType.True) ||
            token.type().equals(TokenType.False);
    }
    
    private boolean isStatement() {
        return token.type().equals(TokenType.Semicolon) ||
               token.type().equals(TokenType.LeftBrace) || 
               token.type().equals(TokenType.Identifier) ||
               token.type().equals(TokenType.If) ||
               token.type().equals(TokenType.While);
    }
    
    public static void main(String args[]) {
        Parser parser  = new Parser(new Lexer(args[0]));
        Program prog = parser.program();
        prog.display();           // display abstract syntax tree
    } //main

} // Parser


// File:   Evaluator.java
// Date:   October 2015

// Java template file for typechecker component of Informatics 2A Assignment 1.
// Rudimentary runtime system for the MH language.
// Illustrates the ideas of small-step operational semantics (Lecture 27!).

import java.math.* ;
import java.io.* ;

class Evaluator {

    static boolean reducible (MH_EXP e) {
	return ! (e.isNUM () || e.isBOOLEAN() || e.isLAMBDA()) ;
    }

    static MH_EXP subst (MH_EXP e, String v1, MH_EXP e1) 
	throws RuntimeError {
	// returns e with e1 substituted for all free occurrences of
	// the variable v1
	if (e.isVAR()) {
	    if (e.value().equals(v1)) return e1 ;
	    else return e ;
	} else if (e.isNUM() || e.isBOOLEAN()) {
	    return e ;
	} else if (e.isAPP()) {
	    return new MH_Exp_Impl 
		(subst (e.first(),v1,e1), subst (e.second(),v1,e1)) ;
	} else if (e.isINFIX()) {
	    return new MH_Exp_Impl 
		(subst (e.first(),v1,e1), e.infixOp(), 
		 subst (e.second(),v1,e1)) ;
	} else if (e.isIF()) {
	    return new MH_Exp_Impl 
		(subst (e.first(),v1,e1), subst (e.second(),v1,e1),
		 subst (e.third(),v1,e1)) ;
	} else if (e.isLAMBDA()) {
	    String v2 = e.value() ;
	    if (v2.equals(v1)) return e ;
	    else return new MH_Exp_Impl (v2, subst (e.first(),v1,e1)) ;
	} else throw new RuntimeError() ;
    }

    static MH_EXP reduce (MH_EXP e, MH_Exp_Env env) 
	throws RuntimeError, UnknownVariable {
	if (e.isVAR()) {
	    return env.valueOf(e.value()) ;
	} else if (e.isINFIX()) {
	    MH_EXP e1 = e.first() ;
	    MH_EXP e2 = e.second() ;
	    String i = e.infixOp() ;
	    if (reducible(e1)) {
		return new MH_Exp_Impl (reduce(e1,env),i,e2) ;
	    } else if (reducible(e2)) {
		return new MH_Exp_Impl (e1,i,reduce(e2,env)) ;
	    } else {
		BigInteger v1 = new BigInteger (e1.value()) ;
		BigInteger v2 = new BigInteger (e2.value()) ;
		switch (i) {
		case "+": return new MH_Exp_Impl 
			("NUM", v1.add(v2).toString()) ;
		case "-": return new MH_Exp_Impl
 			("NUM", v1.subtract(v2).toString()) ;
		case "==": return new MH_Exp_Impl
			("BOOLEAN", (v1.equals(v2)?"True":"False")) ;
		case "<=": return new MH_Exp_Impl
			("BOOLEAN", (v1.compareTo(v2)<=0?"True":"False")) ;
		default: throw new RuntimeError() ;
		}
	    }
	} else if (e.isIF()) {
	    MH_EXP e1 = e.first() ;
	    MH_EXP e2 = e.second() ;
	    MH_EXP e3 = e.third() ;
	    if (reducible(e1)) {
		return new MH_Exp_Impl (reduce(e1,env),e2,e3) ;
	    } else if (e1.value().equals("True")) {
		return e2 ;
	    } else if (e1.value().equals("False")) {
		return e3 ;
	    } else throw new RuntimeError() ;
	} else if (e.isAPP()) {
	    MH_EXP e1 = e.first() ;
	    MH_EXP e2 = e.second() ;
	    if (reducible(e1)) {
		return new MH_Exp_Impl (reduce(e1,env),e2) ;
	    } else if (e1.isLAMBDA()) {  
		// N.B. call-by-name reduction
		String var = e1.value() ;
		MH_EXP body = e1.first() ;
		return subst (body,var,e2) ;
	    } else throw new RuntimeError() ;
	} else throw new RuntimeError() ;
    }

    static MH_EXP evaluate (MH_EXP e, MH_Exp_Env env) 
	throws RuntimeError, UnknownVariable {
	MH_EXP d = e ;
	while (reducible(d)) {
	    d = reduce(d,env) ;
	} ;
	return d ;
    }

    static String printForm (MH_EXP e) {
	if (e.isNUM() || e.isBOOLEAN()) return e.value() ;
	else return "-" ;
    }

    static class RuntimeError extends Exception {} ;

    public static void main (String[] args) throws Exception {
	// processes MH program from specified file and then enters
	// interactive read-eval loop.

	Reader fileReader = new BufferedReader (new FileReader (args[0])) ;
	MH_Typechecker.MH_Type_Env typeEnv = null ;
	MH_Exp_Env runEnv = null ;
	// load MH definitions from specified file
	try {
	    LEX_TOKEN_STREAM MH_Lexer = 
		new CheckedSymbolLexer (new MH_Lexer (fileReader)) ;
	    TREE prog = MH_Typechecker.MH_Parser.parseTokenStream (MH_Lexer) ;
	    typeEnv = MH_Typechecker.compileTypeEnv (prog) ;
	    runEnv = MH_Typechecker.typecheckProg (prog, typeEnv) ;
	} catch (Exception x) {
	    System.out.println ("MH Error: " + x.getMessage()) ;
	}
	if (runEnv != null) {
	    BufferedReader consoleReader = 
		new BufferedReader (new InputStreamReader (System.in)) ;
	    // Enter interactive read-eval loop
	    while (0==0) {
		System.out.print ("\nMH> ") ;
		String inputLine = "it = " + consoleReader.readLine() + ";" ;
		MH_EXP e = null ;
		MH_TYPE t = null ;
		// lex, parse and typecheck one line of console input
		try {
		    Reader lineReader = 
			new BufferedReader (new StringReader (inputLine)) ;
		    LEX_TOKEN_STREAM lineLexer =
			new CheckedSymbolLexer (new MH_Lexer (lineReader)) ;
		    TREE dec = MH_Typechecker.MH_Parser.parseTokenStreamAs
			(lineLexer, "#TermDecl");
		    e = MH_Exp_Impl.convertExp (dec.getChildren()[3]) ;
		    t = MH_Typechecker.computeType (e,typeEnv) ;
		} catch (Exception x) {
		    System.out.println ("MH Error: " + x.getMessage()) ;
		} ;
		if (t != null) {
		    // display type
		    System.out.println ("  it :: " + t.toString()) ;
		    // evaluate expression
		    MH_EXP e1 = evaluate (e,runEnv) ;
		    // display value
		    System.out.println ("  it  = " + printForm(e1)) ;
		}
	    }
	}
    }
}

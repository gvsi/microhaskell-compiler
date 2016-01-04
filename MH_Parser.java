
// File:   MH_Parser.java
// Date:   October 2015

// Java template file for parser component of Informatics 2A Assignment 2 (2015).
// Students should add a method body for the LL(1) parse table for Micro-Haskell.


import java.io.* ;

class MH_Parser extends GenParser implements PARSER {

  String startSymbol() {return "#Prog" ;}

  // Right hand sides of all productions in grammar:

  String[] epsilon              = new String[] { } ;
  String[] Decl_Prog            = new String[] {"#Decl", "#Prog"} ;
  String[] TypeDecl_TermDecl    = new String[] {"#TypeDecl", "#TermDecl"} ;
  String[] TypeDecl_rule        = new String[] {"VAR", "::", "#Type", ";"} ;
  String[] Type1_TypeOps        = new String[] {"#Type1", "#TypeOps"} ;
  String[] arrow_Type           = new String[] {"->", "#Type"} ;
  String[] Integer              = new String[] {"Integer"} ;
  String[] Bool                 = new String[] {"Bool"} ;
  String[] lbr_Type_rbr         = new String[] {"(", "#Type", ")"} ;
  String[] TermDecl_rule        = new String[] {"VAR", "#Args", "=",
  "#Exp", ";"} ;
  String[] VAR_Args             = new String[] {"VAR", "#Args"} ;
  String[] Exp1                 = new String[] {"#Exp1"} ;
  String[] if_then_else_rule    = new String[] {"if", "#Exp", "then",
  "#Exp", "else", "#Exp"} ;
  String[] Exp2_Op1             = new String[] {"#Exp2", "#Op1"} ;
  String[] eq_rule              = new String[] {"==", "#Exp2"} ;
  String[] lteq_le              = new String[] {"<=", "#Exp2"} ;
  String[] Exp3_Ops2            = new String[] {"#Exp3", "#Ops2"} ;
  String[] plus_rule            = new String[] {"+", "#Exp3", "#Ops2"} ;
  String[] minus_rule           = new String[] {"-", "#Exp3", "#Ops2"} ;
  String[] Exp4_Ops3            = new String[] {"#Exp4", "#Ops3"} ;
  String[] VAR                  = new String[] {"VAR"} ;
  String[] NUM                  = new String[] {"NUM"} ;
  String[] BOOLEAN              = new String[] {"BOOLEAN"} ;
  String[] lbr_Exp_rbr          = new String[] {"(", "#Exp", ")"} ;

  String[] tableEntry (String nonterm, String tokClass) {

    if (nonterm.equals("#Prog")) {
      if (tokClass == null)
        return epsilon;
      else if (tokClass.equals("VAR"))
        return Decl_Prog;
      else
        return null;
    } else if (nonterm.equals("#Decl")) {
      if (tokClass.equals("VAR"))
        return TypeDecl_TermDecl;
      else
        return null;
    } else if (nonterm.equals("#TypeDecl")) {
      if (tokClass.equals("VAR"))
        return TypeDecl_rule;
      else
        return null;
    } else if (nonterm.equals("#Type")) {
      if (tokClass.equals("Integer") || tokClass.equals("Bool") || tokClass.equals("("))
        return Type1_TypeOps;
      else
        return null;
    } else if (nonterm.equals("#TypeOps")) {
      if (tokClass.equals(";") || tokClass.equals(")"))
        return epsilon;
      else if (tokClass.equals("->"))
        return arrow_Type;
      else
        return null;
    } else if (nonterm.equals("#Type1")) {
      if (tokClass.equals("Integer"))
        return Integer;
      else if (tokClass.equals("Bool"))
        return Bool;
      else if (tokClass.equals("("))
        return lbr_Type_rbr;
      else
        return null;
    } else if (nonterm.equals("#TermDecl")) {
      if (tokClass.equals("VAR"))
        return TermDecl_rule;
      else
        return null;
    } else if (nonterm.equals("#Args")) {
      if (tokClass.equals("VAR"))
        return VAR_Args;
      else if (tokClass.equals("="))
        return epsilon;
      else
      return null;
    } else if (nonterm.equals("#Exp")) {
      if (tokClass.equals("VAR") || tokClass.equals("(") || tokClass.equals("NUM")
          || tokClass.equals("BOOLEAN"))
        return Exp1;
      else if (tokClass.equals("if"))
        return if_then_else_rule;
      else
        return null;
    } else if (nonterm.equals("#Exp1")) {
      if (tokClass.equals("VAR") || tokClass.equals("(") || tokClass.equals("NUM")
          || tokClass.equals("BOOLEAN"))
        return Exp2_Op1;
      else
        return null;
    } else if (nonterm.equals("#Op1")) {
      if (tokClass.equals(";") || tokClass.equals(")") || tokClass.equals("then") ||
          tokClass.equals("else"))
        return epsilon;
      else if (tokClass.equals("=="))
        return eq_rule;
      else if (tokClass.equals("<="))
        return lteq_le;
      else
        return null;
    } else if (nonterm.equals("#Exp2")) {
      if (tokClass.equals("VAR") || tokClass.equals("(") || tokClass.equals("NUM") ||
          tokClass.equals("BOOLEAN"))
        return Exp3_Ops2;
      else
        return null;
    } else if (nonterm.equals("#Ops2")) {
      if (tokClass.equals(";") || tokClass.equals(")") || tokClass.equals("then") ||
          tokClass.equals("else") || tokClass.equals("==") || tokClass.equals("<="))
        return epsilon;
      else if (tokClass.equals("+"))
        return plus_rule;
      else if (tokClass.equals("-"))
        return minus_rule;
      else
        return null;
    } else if (nonterm.equals("#Exp3")) {
      if (tokClass.equals("VAR") || tokClass.equals("(") || tokClass.equals("NUM") ||
          tokClass.equals("BOOLEAN"))
        return Exp4_Ops3;
      else
        return null;
    } else if (nonterm.equals("#Ops3")) {
      if (tokClass.equals("VAR") || tokClass.equals("(") || tokClass.equals("NUM") ||
          tokClass.equals("BOOLEAN"))
        return Exp4_Ops3;
      else if (tokClass.equals(";") || tokClass.equals(")") || tokClass.equals("then") ||
               tokClass.equals("else") || tokClass.equals("==") || tokClass.equals("<=") ||
               tokClass.equals("+") || tokClass.equals("-"))
        return epsilon;
      else
        return null;
    } else if (nonterm.equals("#Exp4")) {
      if (tokClass.equals("VAR"))
        return VAR;
      else if (tokClass.equals("("))
        return lbr_Exp_rbr;
      else if (tokClass.equals("NUM"))
        return NUM;
      else if (tokClass.equals("BOOLEAN"))
        return BOOLEAN;
      else
        return null;
    } else {
      return null;
    }
  }

}


// For testing

class MH_ParserDemo {

  static PARSER MH_Parser = new MH_Parser() ;

  public static void main (String[] args) throws Exception {
    Reader reader = new BufferedReader (new FileReader (args[0])) ;
    LEX_TOKEN_STREAM MH_Lexer =
    new CheckedSymbolLexer (new MH_Lexer (reader)) ;
    TREE theTree = MH_Parser.parseTokenStream (MH_Lexer) ;
  }
}

package listener;

import java.util.Hashtable;
import generated.*;

import generated.MiniCParser;
import generated.MiniCParser.ExprContext;
import generated.MiniCParser.Fun_declContext;
import generated.MiniCParser.If_stmtContext;
import generated.MiniCParser.Local_declContext;
import generated.MiniCParser.ParamContext;
import generated.MiniCParser.ParamsContext;
import generated.MiniCParser.Type_specContext;
import generated.MiniCParser.Var_declContext;
import listener.SymbolTable;
import listener.SymbolTable.VarInfo;

public class BytecodeGenListenerHelper {

	// <boolean functions>

	static boolean isFunDecl(MiniCParser.ProgramContext ctx, int i) {
		return ctx.getChild(i).getChild(0) instanceof MiniCParser.Fun_declContext;
	}

	// type_spec IDENT '[' ']'
	static boolean isArrayParamDecl(ParamContext param) {
		return param.getChildCount() == 4;
	}

	// global vars
	static int initVal(Var_declContext ctx) {
		return Integer.parseInt(ctx.LITERAL().getText());
	}

	// var_decl : type_spec IDENT '=' LITERAL ';
	static boolean isDeclWithInit(Var_declContext ctx) {
		return ctx.getChildCount() == 5;
	}

	// var_decl : type_spec IDENT '[' LITERAL ']' ';'
	static boolean isArrayDecl(Var_declContext ctx) {
		return ctx.getChildCount() == 6;
	}

	// <local vars>
	// local_decl : type_spec IDENT '[' LITERAL ']' ';'
	static int initVal(Local_declContext ctx) {
		return Integer.parseInt(ctx.LITERAL().getText());
	}

	static boolean isArrayDecl(Local_declContext ctx) {
		return ctx.getChildCount() == 6;
	}

	static boolean isDeclWithInit(Local_declContext ctx) {
		return ctx.getChildCount() == 5;
	}

	static boolean isVoidF(Fun_declContext ctx) {
		// <Fill in>
		return ctx.getChild(0).getText().equals("void");
	}

	static boolean isIntReturn(MiniCParser.Return_stmtContext ctx) {
		return ctx.getChildCount() == 3;
	}

	static boolean isVoidReturn(MiniCParser.Return_stmtContext ctx) {
		return ctx.getChildCount() == 2;
	}

	// <information extraction>
	static String getStackSize(Fun_declContext ctx) {
		return "32";
	}

	static String getLocalVarSize(Fun_declContext ctx) {
		return "32";
	}

	static String getTypeText(Type_specContext typespec) { 
		// <Fill in>
		/*
		 * type_spec : VOID | INT ;
		 */
		if (typespec.getChild(0).equals(typespec.VOID())) {
			return "V";
		}
		return "I";
	}

	// params
	static String getParamName(ParamContext param) { 
		// <Fill in>
//		param		: type_spec IDENT		
//		| type_spec IDENT '[' ']'	;
		return param.getChild(1).getText();
	}

	static String getParamTypesText(ParamsContext params) {
		String typeText = "";

		for (int i = 0; i < params.param().size(); i++) {
			MiniCParser.Type_specContext typespec = (MiniCParser.Type_specContext) params.param(i).getChild(0);
			typeText += getTypeText(typespec); // + ";";
		}
		return typeText;
	}

	static String getLocalVarName(Local_declContext local_decl) {
		// <Fill in>
//		local_decl	: type_spec IDENT ';'
//		| type_spec IDENT '=' LITERAL ';'	
//		| type_spec IDENT '[' LITERAL ']' ';'	;
		return local_decl.getChild(1).getText();
	}

	static String getFunName(Fun_declContext ctx) { 
		// <Fill in>
//		fun_decl	: type_spec IDENT '(' params ')' compound_stmt ;
		return ctx.getChild(1).getText();
	}

	static String getFunName(ExprContext ctx) { 
		// <Fill in>
//		expr	:  LITERAL				
//		| '(' expr ')'				 
//		| IDENT				 
//		| IDENT '[' expr ']'			 
//		| IDENT '(' args ')'			
//		| '-' expr				 
//		| '+' expr				 
//		| '--' expr				 
//		| '++' expr				 
//		| expr '*' expr				 
//		| expr '/' expr				 
//		| expr '%' expr				 
//		| expr '+' expr				 
//		| expr '-' expr				 
//		| expr EQ expr				
//		| expr NE expr				 
//		| expr LE expr				 
//		| expr '<' expr				 
//		| expr GE expr				 
//		| expr '>' expr				 
//		| '!' expr					 
//		| expr AND expr				 
//		| expr OR expr				
//		| IDENT '=' expr			
//		| IDENT '[' expr ']' '=' expr		;
		return ctx.IDENT().getText();
	}

	static boolean noElse(If_stmtContext ctx) {
		return ctx.getChildCount() < 5;
	}

	static String getFunProlog() {
		return ".class public Test\n" + ".super java/lang/Object\n" + "; standard initializer\n"
				+ ".method public <init>()V\n" + "aload_0\n" + "invokenonvirtual " 
				+ "java/lang/Object/<init>()V\n"
				+ "return\n" + ".end method\n";
//		 ".class public Test .....
//		 ...
//		 invokenonvirtual java/lang/Object/<init>()
//		 return
//		 .end method"
	}

	static String getCurrentClassName() {
		return "Test";
	}
}

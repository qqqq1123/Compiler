package print;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;

import org.antlr.v4.runtime.tree.ParseTreeProperty;
import generated.*;

public class MiniCPrintListener extends MiniCBaseListener {
	ParseTreeProperty<String> newTexts = new ParseTreeProperty<>();
	/*
	 * program에서 시작해서 decl로 들어가고 .... 를 terminal까지 반복. 아래 쪽으로 들어가는게 enter(), 다시 나오는게
	 * exit()
	 */

	@Override
	public void exitType_spec(MiniCParser.Type_specContext ctx) { // type_spec
		/*
		 * type_spec : VOID | INT ;
		 */
		String s = "";
		if (ctx.getChild(0).equals(ctx.VOID())) { // VOID일 때,
			newTexts.put(ctx, ctx.VOID() + s); // VOID를 newTexts에 put 한다.
		} else { // INT일 때,
			newTexts.put(ctx, ctx.INT() + s); // INT를 newTexts에 put 한다.
		}

	}

	@Override
	public void exitVar_decl(MiniCParser.Var_declContext ctx) { // var_decl
		/*
		 * var_decl : type_spec IDENT ';' //첫 번째 | type_spec IDENT '=' LITERAL ';' //두
		 * 번째 | type_spec IDENT '[' LITERAL ']' ';' //세 번째 ;
		 */
		String getS = ctx.getChild(2).getText(); // 위 문법에서 첫 번째인지, 두 번째인지, 세 번째인지 판가름하기 위한 문자
		String whiteSpace = " "; // space

		if (getS.equals(";")) { // 첫 번째 문법 일 때
			newTexts.put(ctx, newTexts.get(ctx.type_spec()) + whiteSpace + ctx.IDENT() + ";\n"); // 그대로 newTexts.put함
		} else if (getS.equals("=")) { // 두 번째 문법 일 때
			newTexts.put(ctx, newTexts.get(ctx.type_spec()) + whiteSpace + ctx.IDENT() + " = " + ctx.LITERAL() + ";\n");
		} else if (getS.equals("[")) { // 세 번째 문법 일 때
			newTexts.put(ctx, newTexts.get(ctx.type_spec()) + whiteSpace + ctx.IDENT() + "[" + ctx.LITERAL() + "];\n");
		}

	}

	@Override
	public void exitFun_decl(MiniCParser.Fun_declContext ctx) { // fun_decl
		/*
		 * fun_decl : type_spec IDENT '(' params ')' compound_stmt ;
		 */
		String whiteSpace = " ";

		newTexts.put(ctx, newTexts.get(ctx.type_spec()) + whiteSpace + ctx.IDENT() + "(" + newTexts.get(ctx.params())
				+ ")" + newTexts.get(ctx.compound_stmt())); // 위의 문법을 newTexts에 put함
	}

	@Override
	public void exitParam(MiniCParser.ParamContext ctx) { // param
		/*
		 * param : type_spec IDENT //첫 번째 | type_spec IDENT '[' ']' ; //두 번째
		 */
		int cnt = ctx.getChildCount(); // 자식의 수로 경우를 따짐
		String whiteSpace = " ";

		if (cnt == 2) { // 첫 번째 문법 일 때
			newTexts.put(ctx, newTexts.get(ctx.type_spec()) + whiteSpace + ctx.IDENT());
		} else { // 두 번째 문법 일 때
			newTexts.put(ctx, newTexts.get(ctx.type_spec()) + whiteSpace + ctx.IDENT() + "[" + "]");
		}

	}

	@Override
	public void exitParams(MiniCParser.ParamsContext ctx) { // params
		/*
		 * params : param (',' param)* | VOID | ;
		 */

		String s = "";
		StringBuilder output = new StringBuilder();

		if (ctx.param().size() == 0 && ctx.getChildCount() == 1) { // VOID일 때
			newTexts.put(ctx, ctx.VOID() + s);
		} else if (ctx.getChildCount() == 0) { // blank일 때
			newTexts.put(ctx, s);
		} else {
			if (ctx.getChildCount() == 1) { // param일 때
				newTexts.put(ctx, newTexts.get(ctx.param(0)) + s);
			} else { // param (',' param)*일 때
				output.append(newTexts.get(ctx.param(0)) + s); //param 붙이고 
				for (int i = 0; i < ctx.getChildCount() - 2; i++) { // (',' param)* 붙여줌
					output.append("," + newTexts.get(ctx.param(i)) + s);
				}
				newTexts.put(ctx, output + s);
				output.setLength(0); // StringBuilder 초기화
			}
		}
	}

	public static int comStmtCNT = 0; // compound_stmt에 들어온 횟수를 저장하는 변수 

	@Override
	public void enterCompound_stmt(MiniCParser.Compound_stmtContext ctx) { // compound_stmt
		++comStmtCNT; // 들어올 때 마다 증가
	}

	@Override
	public void exitCompound_stmt(MiniCParser.Compound_stmtContext ctx) { // compound_stmt
		/*
		 * compound_stmt: '{' local_decl* stmt* '}' ;
		 */
		StringBuilder ex = new StringBuilder(); // 시작 블록
		StringBuilder in = new StringBuilder(); // 중첩 블록

		for (int i = 0; i < comStmtCNT; i++) { // 중첩 블록에 들어온 만큼 "...."을 붙여준다.
			in.append("....");
		}

		for (int i = 0; i < comStmtCNT - 1; i++) { // 중첩 블록의 바깥 블록이므로 중첩블록보다 -1번 "...."을 붙여준다.
			ex.append("....");
		}

		newTexts.put(ctx, "\n" + ex + "{\n"); // 개행하고 "...." 붙이고 "{" 들여쓰기 시작
		for (int i = 0; i < ctx.local_decl().size(); i++) // local_decl*에 대해 "...." 붙여줌
			newTexts.put(ctx, newTexts.get(ctx) + in + newTexts.get(ctx.local_decl(i)) + "\n");

		for (int i = 0; i < ctx.stmt().size(); i++) { // stmt*에 대해 "...." 붙여줌
			newTexts.put(ctx, newTexts.get(ctx) + in + newTexts.get(ctx.stmt(i)) + "\n");
		}
		newTexts.put(ctx, newTexts.get(ctx) + ex + "}"); // "...." 찍고 "}" 들여쓰기 종료
		--comStmtCNT; // 끝났으니까 count 빼줌

		in.setLength(0); // StringBuilder 초기화
		ex.setLength(0); // StringBuilder 초기화
	}

	@Override
	public void exitStmt(MiniCParser.StmtContext ctx) { // stmt
		/*
		 * stmt : expr_stmt | compound_stmt | if_stmt | while_stmt | return_stmt ;
		 */
		String s = "";

		if (ctx.getChild(0) == ctx.expr_stmt()) { // expr_stmt일 때
			newTexts.put(ctx, newTexts.get(ctx.expr_stmt()) + s);

		} else if (ctx.getChild(0) == ctx.compound_stmt()) { // compound_stmt일 때
			newTexts.put(ctx, newTexts.get(ctx.compound_stmt()) + s);

		} else if (ctx.getChild(0) == ctx.if_stmt()) { // if_stmt일 때
			newTexts.put(ctx, newTexts.get(ctx.if_stmt()) + s);

		} else if (ctx.getChild(0) == ctx.while_stmt()) { // while_stmt일 때
			newTexts.put(ctx, newTexts.get(ctx.while_stmt()) + s);

		} else if (ctx.getChild(0) == ctx.return_stmt()) { // return_stmt일 때
			newTexts.put(ctx, newTexts.get(ctx.return_stmt()) + s);

		}

	}

	@Override
	public void exitExpr_stmt(MiniCParser.Expr_stmtContext ctx) { // expr_stmt
		/*
		 * expr_stmt : expr ';' ;
		 */
		newTexts.put(ctx, newTexts.get(ctx.expr()) + ";");
	}

	@Override
	public void exitWhile_stmt(MiniCParser.While_stmtContext ctx) { // while_stmt
		/*
		 * while_stmt : WHILE '(' expr ')' stmt ;
		 */
		StringBuilder ex = new StringBuilder(); // 시작 블록
		StringBuilder in = new StringBuilder(); // 중첩 블록

		for (int i = 0; i < comStmtCNT + 1; i++) { // 중첩 블록이므로 "...."을 그냥 블록보다 한 번 더 붙여준다.
			in.append("....");
		}

		for (int i = 0; i < comStmtCNT; i++) { // 시작 블록이므로 "...."을 들어온 만큼 붙여준다.
			ex.append("....");
		}

		newTexts.put(ctx, ctx.WHILE() + " (" + newTexts.get(ctx.expr()) + ") "); // WHILE '(' expr ')'
		newTexts.put(ctx, newTexts.get(ctx) + newTexts.get(ctx.stmt())); // while문에 stmt 붙여줌

		in.setLength(0); // StringBuilder 초기화
		ex.setLength(0); // StringBuilder 초기화
	}

	@Override
	public void exitLocal_decl(MiniCParser.Local_declContext ctx) { // local_decl
		/*
		 * local_decl : type_spec IDENT ';' //첫 번째 | type_spec IDENT '=' LITERAL ';' //두
		 * 번째 | type_spec IDENT '[' LITERAL ']' ';' //세 번째 ;
		 */
		String getS = ctx.getChild(2).getText(); // 위 문법에서 첫 번째인지, 두 번째인지, 세 번째인지 판가름하기 위한 문자
		String whiteSpace = " ";

		if (getS.equals(";")) { // type_spec IDENT ';'
			newTexts.put(ctx, newTexts.get(ctx.type_spec()) + whiteSpace + ctx.IDENT() + ";");
		} else if (getS.equals("=")) { // type_spec IDENT '=' LITERAL ';'
			newTexts.put(ctx, newTexts.get(ctx.type_spec()) + whiteSpace + ctx.IDENT() + " = " + ctx.LITERAL() + ";");
		} else if (getS.equals("[")) { // type_spec IDENT '[' LITERAL ']' ';'
			newTexts.put(ctx, newTexts.get(ctx.type_spec()) + whiteSpace + ctx.IDENT() + "[" + ctx.LITERAL() + "];");
		}

	}

	@Override
	public void exitIf_stmt(MiniCParser.If_stmtContext ctx) { // if_stmt

		/*
		 * if_stmt : IF '(' expr ')' stmt | IF '(' expr ')' stmt ELSE stmt ;
		 */

		StringBuilder ex = new StringBuilder(); // 시작 블록
		StringBuilder in = new StringBuilder(); // 중첩 블록

		for (int i = 0; i < comStmtCNT + 1; i++) { // 중첩 블록이므로 "...."을 그냥 블록보다 한 번 더 붙여준다.
			in.append("....");
		}

		for (int i = 0; i < comStmtCNT; i++) { // 시작 블록이므로 "...."을 들어온 만큼 붙여준다.
			ex.append("....");
		}

		if (ctx.getChildCount() == 5) { // if문일 때
			newTexts.put(ctx, ctx.IF() + " (" + newTexts.get(ctx.expr()) + ") "); // if(expr)
			newTexts.put(ctx, newTexts.get(ctx) + newTexts.get(ctx.stmt(0))); // if문에 stmt 붙여줌
		} else { // if-else문 일 때
			// if
			newTexts.put(ctx, ctx.IF() + " (" + newTexts.get(ctx.expr()) + ") "); // if(expr)
			newTexts.put(ctx, newTexts.get(ctx) + newTexts.get(ctx.stmt(0))); // if문에 stmt 붙여줌
			// else
			newTexts.put(ctx, ctx.ELSE() + " "); // else stmt
			newTexts.put(ctx, newTexts.get(ctx) + newTexts.get(ctx.stmt(1))); // else문에 stmt 붙여줌
		}

		in.setLength(0); // StringBuilder 초기화
		ex.setLength(0); // StringBuilder 초기화

	}

	@Override
	public void exitReturn_stmt(MiniCParser.Return_stmtContext ctx) { // return_stmt
		/*
		 * return_stmt : RETURN ';' //첫 번째 | RETURN expr ';' //두 번째 ;
		 */
		int cnt = ctx.getChildCount(); // 자식 수로 첫 번째 문법인지 두 번째 문법인지 구분하기 위한 변수

		if (cnt == 1) { // RETURN ';'
			newTexts.put(ctx, ctx.RETURN() + ";");
		} else { // RETURN expr ';'
			newTexts.put(ctx, ctx.RETURN() + newTexts.get(ctx.expr()) + ";");
		}

	}

	@Override
	public void exitDecl(MiniCParser.DeclContext ctx) { // decl
		/*
		 * decl : var_decl | fun_decl ;
		 */
		String s = "";
		if (ctx.getChild(0) == ctx.var_decl()) { // var_decl
			newTexts.put(ctx, newTexts.get(ctx.var_decl()) + s);
		} else { // fun_decl
			newTexts.put(ctx, newTexts.get(ctx.fun_decl()) + s);
		}

	}

	@Override
	public void exitProgram(MiniCParser.ProgramContext ctx) {
		/*
		 * program	: decl+			;
		 * */
		String program = "";

		for (int i = 0; i < ctx.getChildCount(); i++) {
			newTexts.put(ctx, ctx.decl(i).getText()); // ParseTree인 newText에 decl을 넣음
			program += newTexts.get(ctx.getChild(i)); // ctx의 child에 들어갔다가 나오면서 출력
		}

		System.out.println(program);
		File file = new File(String.format("[HW3]201802157.c")); // 201802157

		try {
			FileWriter fw = new FileWriter(file);
			fw.write(program);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	boolean isBinaryOperation(MiniCParser.ExprContext ctx) { // 이항연산인지 판별하는 함수 
		return ctx.getChildCount() == 3 && ctx.getChild(1) != ctx.expr(); // expr op expr 형태이면 return true 
	}

	boolean isUnaryOperation(MiniCParser.ExprContext ctx) { //단항연산인지 판별하는 함수 
		return ctx.getChildCount() == 2 && ctx.getChild(0) != ctx.expr(); // op expr 형태이면 return true 
	}

	@Override
	public void exitExpr(MiniCParser.ExprContext ctx) {
		/*
		 * expr	:  LITERAL				
	| '(' expr ')'				 
	| IDENT				 
	| IDENT '[' expr ']'			 
	| IDENT '(' args ')'			
	| '-' expr				 
	| '+' expr				 
	| '--' expr				 
	| '++' expr				 
	| expr '*' expr				 
	| expr '/' expr				 
	| expr '%' expr				 
	| expr '+' expr				 
	| expr '-' expr				 
	| expr EQ expr				
	| expr NE expr				 
	| expr LE expr				 
	| expr '<' expr				 
	| expr GE expr				 
	| expr '>' expr				 
	| '!' expr					 
	| expr AND expr				 
	| expr OR expr				
	| IDENT '=' expr			
	| IDENT '[' expr ']' '=' expr		;
	*/
		String s1 = "", s2 = "", op = "";
		if (ctx.getChild(0) == ctx.LITERAL()) { // LITERAL
			newTexts.put(ctx, ctx.LITERAL() + s1);
		} else if (ctx.getChild(0).getText().equals("(")) { // '(' expr ')'
			newTexts.put(ctx, "(" + newTexts.get(ctx.expr(0)) + ")");
		} else if (ctx.getChild(0) == ctx.IDENT()) { // IDENT
			if (ctx.getChildCount() == 1) { // IDENT
				newTexts.put(ctx, ctx.IDENT() + s1);

			} else if (ctx.getChild(1).getText().equals("[") && ctx.getChildCount() == 4) { // IDENT '[' expr ']'
				newTexts.put(ctx, ctx.IDENT() + "[" + newTexts.get(ctx.expr(0)) + "]");

			} else if (ctx.getChild(1).getText().equals("(") && ctx.getChildCount() == 4) { // IDENT '(' args ')'
				newTexts.put(ctx, ctx.IDENT() + "(" + newTexts.get(ctx.args()) + ")");

			} else if (ctx.getChild(1).getText().equals("=")) { // IDENT '=' expr
				newTexts.put(ctx, ctx.IDENT() + " = " + newTexts.get(ctx.expr(0)));

			} else if (ctx.getChildCount() == 6) { // IDENT '[' expr ']' '=' expr
				newTexts.put(ctx, ctx.IDENT() + "[" + newTexts.get(ctx.expr(0)) + "] = " + newTexts.get(ctx.expr(1)));
			}
		} else if (isBinaryOperation(ctx)) { // expr 'op' expr
			s1 = newTexts.get(ctx.expr(0));
			s2 = newTexts.get(ctx.expr(1));
			op = ctx.getChild(1).getText();

			newTexts.put(ctx, s1 + " " + op + " " + s2);
		} else if (isUnaryOperation(ctx)) { // 'op' expr
			s1 = newTexts.get(ctx.expr(0));
			op = ctx.getChild(0).getText();

			newTexts.put(ctx, op + s1);
		}

	}

	@Override
	public void exitArgs(MiniCParser.ArgsContext ctx) { // args
		/*
		 * args	: expr (',' expr)*			 
	|					 ;
		 */
		String s = "";
		StringBuilder output = new StringBuilder();

		if (ctx.getChildCount() == 0) { // "" //blank
			newTexts.put(ctx, s);
		} else { // expr (',' expr)*
			if (ctx.getChildCount() == 1) { // expr
				newTexts.put(ctx, newTexts.get(ctx.expr(0)));
			} else { // expr (',' expr)*
				output.append(newTexts.get(ctx.expr(0)) + s); //expr 붙이고 
				for (int i = 0; i < ctx.getChildCount() - 2; i++) { //(',' expr)* 붙여줌 
					output.append("," + newTexts.get(ctx.expr(i)) + s);
				}
				newTexts.put(ctx, output + s);
				output.setLength(0); // StringBuilder 초기화
			}
		}
	}

}


class LookAhead  {
	/* Simulating a reader class for a stream of Token */

	private Token current;
	private Lexer lexer;

	public LookAhead(Lexer l) throws Exception {
		lexer=l;
		current=lexer.yylex();
	}

	public boolean check(Sym s) throws Exception {
		/* check whether the first character is of type s*/
		return (current.symbol() == s); 
	}

	public void eat(Sym s) throws Exception {
		/* consumes a token of type s from the stream,
	   exception when the contents does not start on s.   */
		if (!check(s)) {
			throw new Exception("\n" + lexer.getPosition()+": Can't eat "+s+" current being "+current);
		}
		//for debug
		System.out.println(current);

		current=lexer.yylex();
	}


}
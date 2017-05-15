import java.io.*;

class TestLexer {

	public static void main(String[] args) throws Exception {
		if (args.length <1) {
			System.out.println("java TestLexer <filename>");
			System.exit(1);
		}

		File input = new File(args[0]);
		Reader reader = new FileReader(input);
		Lexer lexer = new Lexer(reader);
		Token t=null;
		do{
			t=lexer.yylex();
			System.out.println(t);
		}
		while(t.symbole!=Sym.EOF);  
	}
}

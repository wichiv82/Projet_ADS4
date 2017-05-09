import java.io.*;

class Main {

	public static void main(String[] args) throws Exception {
		if (args.length <1) {
			System.out.println("java Main <namefile>");
			System.exit(1);
		}
		
		File input = new File(args[0]);
		Reader reader = new FileReader(input);
		Lexer lexer = new Lexer(reader);
		LookAhead look = new LookAhead(lexer);

		Parser parser = new Parser(look);
		try {
			Arbre a  = parser.nonterm_Corps();
			System.out.println("The file is correct");
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("output.html")));
			out.print(a.toString());
			out.close();
		} catch (IOException e) {
			System.out.println(e);
		} catch (Exception e){
			System.out.println("The file is not correct");
			System.out.println(e);
		}
	}
}

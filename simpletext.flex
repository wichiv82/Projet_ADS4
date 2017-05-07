

%%

%public

%class Lexer

%unicode

%type Token

%line

%column



%{
   
	public String getPosition(){
     
	 return "Reading at line "+yyline+", column "+yycolumn;
   
	}


%}





%yylexthrow Exception



blank = [\n\r \t] 

string = \"[^\"]*\"

mot = [a-zA-Z0-9]+



%%

"\\begindoc"	 {return new Token(Sym.DEBUTDOC);}

"\\enddoc" {return new Token(Sym.FINDOC);}


"\\linebreak" {return new Token(Sym.LINEBREAK);}

"\\bf" {return new Token(Sym.BF);}

"\\it" {return new Token(Sym.IT);}

"{" {return new Token(Sym.DEBUTACCOLADE);}

"}" {return new Token(Sym.FINACCOLADE);}

"\\beginenum" {return new Token(Sym.DEBUTENUM);}

"\\endenum" {return new Token(Sym.FINENUM);}

"\\item" {return new Token(Sym.ITEM);}


{mot}		{return new Token(Sym.MOT);}

{blank}		{}
 
 
[^]		{throw new Exception("Lexer error at line "+ yyline + "column " + yycolumn);}


<<EOF>>    	{return new Token(Sym.EOF);}  

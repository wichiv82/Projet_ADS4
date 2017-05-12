

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
mot = [a-zA-Z0-9:;,.?!]+
ID = ¤[0-9]+
CONSTANTE_COULEUR = \#[A-F0-9][A-F0-9][A-F0-9][A-F0-9][A-F0-9][A-F0-9]
   

%%

"\\begindoc"	 	{return new Token(Sym.DEBUTDOC);}

"\\enddoc" 		{return new Token(Sym.FINDOC);}


"\\linebreak" 		{return new Token(Sym.LINEBREAK);}

"\\bf" 			{return new Token(Sym.BF);}

"\\it" 			{return new Token(Sym.IT);}

"{" 			{return new Token(Sym.DEBUTACCOLADE);}

"}" 			{return new Token(Sym.FINACCOLADE);}

"\\beginenum" 		{return new Token(Sym.DEBUTENUM);}

"\\endenum" 		{return new Token(Sym.FINENUM);}

"\\item" 		{return new Token(Sym.ITEM);}


{CONSTANTE_COULEUR}     {return new ColorToken(Sym.CONSTANTE_COULEUR, yytext());}
{mot}			{return new WordToken(Sym.MOT, yytext());}

{blank}			{}
 
 
[^]			{throw new Exception("Erreur du Lexeur ligne "+ yyline + "column " + yycolumn);}


//Partie couleur
"\\set" 		{return new Token(Sym.SET);}

"\\couleur" 		{return new Token(Sym.COULEUR);}

{ID}     		{return new IdToken(Sym.ID, yytext());}

<<EOF>>    	{return new Token(Sym.EOF);}  


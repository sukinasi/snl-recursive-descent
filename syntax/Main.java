package snl.syntax;


import snl.lexer.Token;

public class Main {
    public static void main(String[] args) {
        SyntaxParser syntaxParser=new SyntaxParser("p.snl");
        syntaxParser.parser();
        for(Token token:syntaxParser.tokenList){
            System.out.println(token);
        }
        syntaxParser.tree.print(syntaxParser.tree.head,0);
        System.out.close();
        if(!syntaxParser.errors.isEmpty()){
            for(String s:syntaxParser.errors){
                System.err.println(s);
            }
        }
    }

}

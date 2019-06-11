package snl.lexer;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    LineNumberReader bufferedReader= null;
    public List<Token> tokenList=new ArrayList<>();
    List<String> errorList=new ArrayList<>();
    char character = ' ';
    boolean isEnd=false;
    public Lexer(String fileName){
        File file=new File(fileName);
        try {
            FileReader fileReader=new FileReader(file);
            bufferedReader=new LineNumberReader(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public void getToken() throws IOException {

        while (!isEnd){
                while (character==' '||character=='\n'||character=='\r'){
                    readch();
                }
                if(character=='{'){
                    commit();
                }else if(isDegit()){
                    number();
                }else if(isAlpha()){
                    id();
                }else if (character == '+') {
                    Token token = new Token(bufferedReader.getLineNumber(), TokenType.PLUS, String.valueOf(character));
                    tokenList.add(token);readch();
                } else if (character == '-') {
                   Token token = new Token(bufferedReader.getLineNumber(),  TokenType.MINUS, String.valueOf(character));
                    tokenList.add(token);readch();
                } else if (character == '*') {
                   Token token = new Token(bufferedReader.getLineNumber(), TokenType.TIMES, String.valueOf(character));
                    tokenList.add(token);readch();
                } else if (character == '/') {
                   Token token = new Token(bufferedReader.getLineNumber(), TokenType.OVER, String.valueOf(character));
                    tokenList.add(token);readch();
                } else if (character == '(') {
                   Token token = new Token(bufferedReader.getLineNumber(),TokenType.LPAREN, String.valueOf(character));
                    tokenList.add(token);readch();
                } else if (character == ')') {
                   Token token = new Token(bufferedReader.getLineNumber(), TokenType.RPAREN, String.valueOf(character));
                    tokenList.add(token);readch();
                } else if (character == '[') {
                   Token token = new Token(bufferedReader.getLineNumber(),TokenType.LMIDPAREN, String.valueOf(character));
                    tokenList.add(token);readch();
                } else if (character == ']') {
                   Token token = new Token(bufferedReader.getLineNumber(),  TokenType.RMIDPAREN, String.valueOf(character));
                    tokenList.add(token);readch();
                } else if (character == ';') {
                   Token token = new Token(bufferedReader.getLineNumber(),  TokenType.SEMI, String.valueOf(character));
                    tokenList.add(token);readch();
                } else if (character == ',') {
                   Token token = new Token(bufferedReader.getLineNumber(), TokenType.COMMA, String.valueOf(character));
                    tokenList.add(token);readch();
                } else if (character == '=') {
                   Token token = new Token(bufferedReader.getLineNumber(), TokenType.EQ, String.valueOf(character));
                    tokenList.add(token);
                    readch();
                } else if (character == '<') {
                   Token token = new Token(bufferedReader.getLineNumber(),  TokenType.LT, String.valueOf(character));
                    tokenList.add(token);readch();
                }else if(character==':'){
                    assign();
                }else if(character=='.'){
                    dot();
                }else if(character=='\''){
                    ch();
                }
            }
        }


    private void ch() throws IOException {
        readch();
        String s;
        if(isAlpha()||isDegit()){
            s=String.valueOf(character);
            readch();
            if (character=='\''){
                Token token = new Token(bufferedReader.getLineNumber(),  TokenType.CHARACTER, s);
                tokenList.add(token);
            }else error();
        }
        else error();
    }

    private void dot() throws IOException {
        readch();
        if(character=='.'){
            readch();
            if(isDegit()){
                Token token = new Token(bufferedReader.getLineNumber(),  TokenType.UNDERRANGE, "..");
                tokenList.add(token);
            }else error();
        }else if (isAlpha()){
            Token token = new Token(bufferedReader.getLineNumber(),TokenType.DOT, ".");
            tokenList.add(token);
        }else {
            if (character == ' ') {
                while (character == ' ') readch();
            }
            if (isEnd){
                Token token = new Token(bufferedReader.getLineNumber(),TokenType.EOF, ".");
                tokenList.add(token);
            }
        }
    }

    private void assign() throws IOException {
        readch();
        if(character=='='){
            Token token = new Token(bufferedReader.getLineNumber(), TokenType.ASSIGN, ":=");
            tokenList.add(token);
        }else {
            error();
        }
        readch();
    }

    private void id() throws IOException {

        String s=""+character;
        readch();
        while (isDegit()||isAlpha()){
            s+=character;
            readch();
        }
        Token token=new Token(bufferedReader.getLineNumber(),TokenType.ID,s);
        token.checkKeyWords();
        tokenList.add(token);
    }

    public boolean isAlpha(){
        return (character>='a'&&character<='z')||(character>='A'&&character<='Z');
    }
    public boolean isDegit(){
        return character>='0'&&character<='9';
    }
    public void number() throws IOException {
        String s=""+character;
        readch();
        while (isDegit()){
            s+=character;
            readch();
        }
        Token token=new Token(bufferedReader.getLineNumber(),TokenType.INTC,s);
        tokenList.add(token);
    }
    public void commit() throws IOException {
        while(character!='}'&&!isEnd){
          readch();
        }
        if(character!='}'){
            error();
        }
        readch();

    }
    public void error(){
        errorList.add("错误在"+bufferedReader.getLineNumber()+"行");
    }
    public void readch()throws IOException {
        character=(char)bufferedReader.read();
        //System.out.println(character);
        if ((int) character == 0xffff) {
            this.isEnd = true;
        }
    }
    public static void main(String[] args) {
       Lexer lexer=new Lexer("a.snl");
        try {
            lexer.getToken();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Token token:lexer.tokenList){
            System.out.printf("%5d|%10s|%10s\n",token.line,token.type,token.value);
        }

    }
}

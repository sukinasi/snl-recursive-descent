package snl.lexer;


public class Token {
    int line ;      //行
    TokenType type;        //token类型
    String value;       //token值

    public Token() {
    }

    public Token(int line,  TokenType tokenType, String value) {
        this.line = line;

        this.type = tokenType;
        this.value = value;

    }
    void checkKeyWords() {        if (type == TokenType.ID) {
            if (value.equals(TokenType.PROGRAM.getStr())) {
                type = TokenType.PROGRAM;
            } else if (value.equals(TokenType.PROCEDURE.getStr())) {
                type = TokenType.PROCEDURE;
            } else if (value.equals(TokenType.TYPE.getStr())) {
                type = TokenType.TYPE;
            } else if (value.equals(TokenType.VAR.getStr())) {
                type = TokenType.VAR;
            } else if (value.equals(TokenType.IF.getStr())) {
                type = TokenType.IF;
            } else if (value.equals(TokenType.THEN.getStr())) {
                type = TokenType.THEN;
            } else if (value.equals(TokenType.ELSE.getStr())) {
                type = TokenType.ELSE;
            } else if (value.equals(TokenType.FI.getStr())) {
                type = TokenType.FI;
            } else if (value.equals(TokenType.WHILE.getStr())) {
                type = TokenType.WHILE;
            } else if (value.equals(TokenType.DO.getStr())) {
                type = TokenType.DO;
            } else if (value.equals(TokenType.ENDWH.getStr())) {
                type = TokenType.ENDWH;
            } else if (value.equals(TokenType.DO.getStr())) {
                type = TokenType.DO;
            } else if (value.equals(TokenType.BEGIN.getStr())) {
                type = TokenType.BEGIN;
            } else if (value.equals(TokenType.END.getStr())) {
                type = TokenType.END;
            } else if (value.equals(TokenType.READ.getStr())) {
                type = TokenType.READ;
            } else if (value.equals(TokenType.WRITE.getStr())) {
                type = TokenType.WRITE;
            } else if (value.equals(TokenType.ARRAY.getStr())) {
                type = TokenType.ARRAY;
            } else if (value.equals(TokenType.OF.getStr())) {
                type = TokenType.OF;
            } else if (value.equals(TokenType.RECORD.getStr())) {
                type = TokenType.RECORD;
            } else if (value.equals(TokenType.RETURN.getStr())) {
                type = TokenType.RETURN;
            } else if (value.equals(TokenType.INTEGER.getStr())) {
                type = TokenType.INTEGER;
            } else if (value.equals(TokenType.CHAR.getStr())) {
                type = TokenType.CHAR;
            }
        }
    }
    public String toString() {
//        return "[line=" + line + ",column=" + column + ",type=" + type + ",value=" + value + "]";

        return "`" + value + "|" + type + "|" + line + ":" ;
    }
    public int getLine() {
        return line;
    }




    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}

package snl.syntax;

import snl.lexer.Lexer;
import snl.lexer.Token;
import snl.lexer.TokenType;

import static snl.lexer.TokenType.*;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SyntaxParser {
    public List<Token> tokenList;
    public List<String> errors=new ArrayList<>();
    public  int positon=0;
    public  Tree tree=new Tree();

    public SyntaxParser(String fileName){
        Lexer lexer=new Lexer(fileName);
        try {
            lexer.getToken();
            tokenList=lexer.tokenList;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void parser(){
        tree.setHead(program());
    }
    TreeNode  program(){
        TreeNode root = node("Program");
        root.setChildren(programHead(), declarePart(), programBody(), match(EOF));
        return root;
    }
    private TreeNode programHead() {
        TreeNode pHead = node("ProgramHead");
        pHead.setChildren(match(PROGRAM), programName());
        return pHead;
    }
    private TreeNode declarePart() {
        TreeNode node = node("DeclarePart");
        node.setChildren(typeDecPart(), varDecPart(), procDecpart());
        return node;
    }
    private TreeNode procDecpart() {
        TreeNode node = node("ProcDecpart");
        switch (peekToken().getType()) {
            case BEGIN:
                node.setChildren(node());
                break;
            case PROCEDURE:
                node.setChildren(procDec());
                break;
            default:
                error(BEGIN, PROCEDURE);
        }
        return node;
    }
    private TreeNode procDec() {
        TreeNode node = node("ProcDec");
        node.setChildren(match(PROCEDURE), procName(),
                match(LPAREN), paramList(), match(RPAREN), match(SEMI),
                procDecPart(), procBody(), procDecpart());
        return node;
    }
    private TreeNode procDecPart() {
        TreeNode node = node("ProcDecPart");
        node.setChildren(declarePart());
        return node;
    }
    private TreeNode procBody() {
        TreeNode node = node("ProcBody");
        node.setChildren(programBody());
        return node;
    }
    private TreeNode programBody() {
        TreeNode node = node("ProgramBody");
        node.setChildren(match(BEGIN), stmList(), match(END));
        return node;
    }
    private TreeNode stmList() {
        TreeNode node = node("StmList");
        node.setChildren(stm(), stmMore());
        return node;
    }
    private TreeNode stmMore() {
        TreeNode node = node("StmMore");
        switch (peekToken().getType()) {
            case ELSE:
            case FI:
            case END:
            case ENDWH:
                node.setChildren(node());
                break;
            case SEMI:
                node.setChildren(match(SEMI), stmList());
                break;
            default:
                error(ELSE, FI, END, ENDWH, SEMI);
        }
        return node;
    }
    private TreeNode stm() {
        TreeNode node = node("Stm");
        switch (peekToken().getType()) {
            case IF:
                node.setChildren(conditionalStm());
                break;
            case WHILE:
                node.setChildren(loopStm());
                break;
            case READ:
                node.setChildren(inputStm());
                break;
            case WRITE:
                node.setChildren(outputStm());
                break;
            case RETURN:
                node.setChildren(returnStm());
                break;
            case ID:
                node.setChildren(match(ID), assCall());
                break;
            default:
                error(IF, WHILE, READ,
                        WRITE, RETURN, ID);
        }
        return node;
    }
    private TreeNode outputStm() {
        TreeNode node = node("OutputStm");
        node.setChildren(match(WRITE), match(LPAREN), exp(), match(RPAREN));
        return node;
    }
    private TreeNode returnStm() {
        TreeNode node = node("ReturnStm");
        node.setChildren(match(RETURN));
        return node;
    }
    private TreeNode assCall() {
        TreeNode node = node("AssCall");
        switch (peekToken().getType()) {
            case ASSIGN:
            case LMIDPAREN:
            case DOT:
                node.setChildren(assignmentRest());
                break;
            case LPAREN:
                node.setChildren(callStmRest());
                break;
            default:
                error(ASSIGN, LPAREN);
        }
        return node;
    }
    private TreeNode assignmentRest() {
        TreeNode node = node("AssignmentRest");
        node.setChildren(variMore(), match(ASSIGN), exp());
        return node;
    }
    private TreeNode callStmRest() {
        TreeNode node = node("CallStmRest");
        node.setChildren(match(LPAREN), actParamList(), match(RPAREN));
        return node;
    }
    private TreeNode actParamList() {
        TreeNode node = node("ActParamList");
        switch (peekToken().getType()) {
            case RPAREN:
                node.setChildren(node());
                break;
            case LPAREN:
            case INTC:
            case ID:
                node.setChildren(exp(), actParamMore());
                break;
            default:
                error(RPAREN, LPAREN, INTC, ID);
        }
        return node;
    }
    private TreeNode actParamMore() {
        TreeNode node = node("FiledVar");
        switch (peekToken().getType()) {
            case RPAREN:
                node.setChildren(node());
                break;
            case COMMA:
                node.setChildren(match(COMMA), actParamList());
                break;
            default:
                error(RPAREN, COMMA);
        }
        return node;
    }
    private TreeNode loopStm() {
        TreeNode node = node("LoopStm");
        node.setChildren(match(WHILE), relExp(), match(DO), stmList(), match(ENDWH));
        return node;
    }
    private TreeNode inputStm() {
        TreeNode node = node("InputStm");
        node.setChildren(match(READ), match(LPAREN), invar(), match(RPAREN));
        return node;
    }
    private TreeNode invar() {
        TreeNode node = node("Invar");
        node.setChildren(match(ID));
        return node;
    }
    private TreeNode conditionalStm() {
        TreeNode node = node("ConditionalStm");
        node.setChildren(match(IF), relExp(), match(THEN),
                stmList(), match(ELSE), stmList(), match(FI));
        return node;
    }
    private TreeNode relExp() {
        TreeNode node = node("RelExp");
        node.setChildren(exp(), otherRelE());
        return node;
    }
    private TreeNode otherRelE() {
        TreeNode node = node("OtherRelE");
        node.setChildren(cmpOp(), exp());
        return node;
    }
    private TreeNode cmpOp() {
        TreeNode node = node("CmpOp");
        switch (peekToken().getType()) {
            case LT:
                node.setChildren(match(LT));
                break;
            case EQ:
                node.setChildren(match(EQ));
                break;
            default:
                error(LT, EQ);
        }
        return node;
    }
    private TreeNode otherTerm() {
        TreeNode node = node("OtherTerm");
        switch (peekToken().getType()) {
            case LT:
            case EQ:
            case RMIDPAREN:
            case THEN:
            case ELSE:
            case FI:
            case DO:
            case ENDWH:
            case RPAREN:
            case END:
            case SEMI:
            case COMMA:
                node.setChildren(node());
                break;
            case PLUS:
            case MINUS:
                node.setChildren(addOp(), exp());
                break;
            default:
                error(LT, EQ, RMIDPAREN, THEN,
                        ELSE, FI, DO, ENDWH,
                        RPAREN, END, SEMI, COMMA,
                        PLUS, MINUS);
        }
        return node;
    }
    private TreeNode addOp() {
        TreeNode node = node("AddOp");
        switch (peekToken().getType()) {
            case PLUS:
                node.setChildren(match(PLUS));
                break;
            case MINUS:
                node.setChildren(match(MINUS));
                break;
            default:
                error(PLUS, MINUS);
        }
        return node;
    }
    private TreeNode exp() {
        TreeNode node = node("Exp");
        node.setChildren(term(), otherTerm());
        return node;
    }
    private TreeNode factor() {
        TreeNode node = node("Factor");
        switch (peekToken().getType()) {
            case LPAREN:
                node.setChildren(match(LPAREN), exp(), match(RPAREN));
                break;
            case INTC:
                node.setChildren(match(INTC));
                break;
            case ID:
                node.setChildren(variable());
                break;
            default:
                error(LPAREN, INTC, ID);
        }
        return node;
    }
    private TreeNode variable() {
        TreeNode node = node("Variable");
        node.setChildren(match(ID), variMore());
        return node;
    }
    private TreeNode variMore() {
        TreeNode node = node("VariMore");
        switch (peekToken().getType()) {
            case ASSIGN:
            case TIMES:
            case OVER:
            case PLUS:
            case MINUS:
            case LT:
            case EQ:
            case THEN:
            case ELSE:
            case FI:
            case DO:
            case ENDWH:
            case RPAREN:
            case END:
            case SEMI:
            case COMMA:
            case RMIDPAREN:
                node.setChildren(node());
                break;
            case LMIDPAREN:
                node.setChildren(match(LMIDPAREN), exp(), match(RMIDPAREN));
                break;
            case DOT:
                node.setChildren(match(DOT), filedVar());
                break;
            default:
                error(ASSIGN, TIMES, OVER, PLUS, MINUS, LT, EQ, THEN, ELSE, FI,
                        DO, ENDWH, RPAREN, END, SEMI, COMMA, RMIDPAREN, DOT);
        }
        return node;
    }
    private TreeNode filedVar() {
        TreeNode node = node("FiledVar");
        node.setChildren(match(ID), filedVarMore());
        return node;
    }
    private TreeNode filedVarMore() {
        TreeNode node = node("FiledVarMore");
        switch (peekToken().getType()) {
            case ASSIGN:
            case TIMES:
            case OVER:
            case PLUS:
            case MINUS:
            case LT:
            case EQ:
            case THEN:
            case ELSE:
            case FI:
            case DO:
            case ENDWH:
            case RPAREN:
            case END:
            case SEMI:
            case COMMA:
                node.setChildren(node());
                break;
            case LMIDPAREN:
                node.setChildren(match(LMIDPAREN), exp(), match(RMIDPAREN));
                break;
            default:
                error(ASSIGN, TIMES, OVER, PLUS, MINUS, LT, EQ, THEN,
                        ELSE, FI, DO, ENDWH, RPAREN, END, SEMI, COMMA, LMIDPAREN);
        }
        return node;
    }
    private TreeNode otherFactor() {
        TreeNode node = node("OtherFactor");
        switch (peekToken().getType()) {
            case PLUS:
            case MINUS:
            case LT:
            case EQ:
            case RMIDPAREN:
            case THEN:
            case ELSE:
            case FI:
            case DO:
            case ENDWH:
            case RPAREN:
            case END:
            case SEMI:
            case COMMA:
                node.setChildren(node());
                break;
            case TIMES:
            case OVER:
                node.setChildren(multiOp(), term());
                break;
            default:
                error(PLUS, MINUS, LT, EQ, RMIDPAREN, THEN, ELSE, FI, DO,
                        ENDWH, RPAREN, END, SEMI, COMMA, TIMES, OVER);
        }
        return node;
    }
    private TreeNode multiOp() {
        TreeNode node = node("MultiOp");
        switch (peekToken().getType()) {
            case TIMES:
                node.setChildren(match(TIMES));
                break;
            case OVER:
                node.setChildren(match(OVER));
                break;
            default:
                error(TIMES, OVER);
        }
        return node;
    }
    private TreeNode term() {
        TreeNode node = node("Term");
        node.setChildren(factor(), otherFactor());
        return node;
    }
    private TreeNode paramList() {
        TreeNode node = node("ParamList");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case RPAREN:
                node.setChildren(node());
                break;
            case INTEGER:
            case CHAR:
            case ARRAY:
            case RECORD:
            case ID:
            case VAR:
                node.setChildren(paramDecList());
                break;
            default:
                error(RPAREN, INTEGER, CHAR, ARRAY,
                        RECORD, ID, VAR);
        }
        return node;
    }
    private TreeNode param() {
        TreeNode node = node("Param");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case INTEGER:
            case CHAR:
            case ARRAY:
            case RECORD:
            case ID:
                node.setChildren(typeDef(), formList());
                break;
            case VAR:
                node.setChildren(match(VAR), typeDef(), formList());
                break;
            default:
                error(INTEGER, CHAR, ARRAY,
                        RECORD, ID, VAR);
        }
        return node;
    }
    private TreeNode formList() {
        TreeNode node = node("FormList");
        node.setChildren(match(ID), fidMore());
        return node;
    }
    private TreeNode paramDecList() {
        TreeNode node = node("ParamDecList");
        node.setChildren(param(), paramMore());
        return node;
    }
    private TreeNode paramMore() {
        TreeNode node = node("ParamMore");
        switch (peekToken().getType()) {
            case RPAREN:
                node.setChildren(node());
                break;
            case SEMI:
                node.setChildren(match(SEMI), paramDecList());
                break;
            default:
                error(RPAREN, SEMI);
        }
        return node;
    }
    private TreeNode fidMore() {
        TreeNode node = node("FidMore");
        switch (peekToken().getType()) {
            case SEMI:
            case RPAREN:
                node.setChildren(node());
                break;
            case COMMA:
                node.setChildren(match(COMMA), formList());
                break;
            default:
                error(SEMI, RPAREN, COMMA);
        }
        return node;
    }
    private TreeNode procName() {
        TreeNode node = node("ProcName");
        node.setChildren(match(ID));
        return node;
    }
    private TreeNode varDecPart() {
        TreeNode node = node("VarDecPart");

        switch (peekToken().getType()) {
            case PROCEDURE:
            case BEGIN:
                node.setChildren(node());
                break;
            case VAR:
                node.setChildren(varDec());
                break;
            default:
                error(PROCEDURE, BEGIN, VAR);
        }

        return node;
    }

    private TreeNode varDec() {
        TreeNode node = node("VarDec");
        node.setChildren(match(VAR), varDecList());
        return node;
    }

    private TreeNode varDecList() {
        TreeNode node = node("varIdList");
        node.setChildren(typeDef(), varIdList(), match(SEMI), varDecMore());
        return node;
    }
    private TreeNode varDecMore() {
        TreeNode node = node("varDecMore");
        switch (peekToken().getType()) {
            case PROCEDURE:
            case BEGIN:
                node.setChildren(node());
                break;
            case INTEGER:
            case CHAR:
            case ARRAY:
            case RECORD:
            case ID:
                node.setChildren(varDecList());
                break;
            default:
                error(PROCEDURE, BEGIN, INTEGER, CHAR, ARRAY, RECORD, ID);
        }
        return node;
    }
    private TreeNode varIdMore() {
        TreeNode node = node("VarIdMore");

        switch (peekToken().getType()) {
            case SEMI:
                node.setChildren(node());
                break;
            case COMMA:
                node.setChildren(match(COMMA), varIdList());
                break;
            default:
                error(SEMI, COMMA);
        }
        return node;
    }
    private TreeNode varIdList() {
        TreeNode node = node("varIdList");
        node.setChildren(match(ID), varIdMore());
        return node;
    }
    private TreeNode typeDecPart() {
        TreeNode node = node("TypeDecPart");
        switch (peekToken().getType()) {
            case VAR:
            case PROCEDURE:
            case BEGIN:
                node.setChildren(node());
                break;
            case TYPE:
                node.setChildren(typeDec());
                break;
            default:
                error(VAR, PROCEDURE, BEGIN, TYPE);
        }
        return node;
    }

    private TreeNode typeDec() {
        TreeNode node = node("TypeDec");
        node.setChildren(match(TYPE), typeDecList());
        return node;
    }

    private TreeNode typeDecList() {
        TreeNode node = node("TypeDecList");
        node.setChildren(typeId(), match(EQ), typeDef(), match(SEMI), typeDecMore());
        return node;
    }
    private TreeNode typeDecMore() {
        TreeNode node = node("typeDefMore");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case VAR:
            case PROCEDURE:
            case BEGIN:
                node.setChildren(node());
                break;
            case ID:
                node.setChildren(typeDecList());
                break;
            default:
                error(VAR, PROCEDURE, BEGIN, ID);
        }
        return node;
    }
    private TreeNode typeDef() {
        TreeNode node = node("TypeDef");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case INTEGER:
            case CHAR:
                node.setChildren(baseType());
                break;
            case ARRAY:
            case RECORD:
                node.setChildren(structureType());
                break;
            case ID:
                node.setChildren(match(ID));
                break;
            default:
                error(INTEGER, CHAR, ARRAY, RECORD, ID);
        }

        return node;
    }
    private TreeNode structureType() {
        TreeNode node = node("StructureType");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case ARRAY:
                node.setChildren(arrayType());
                break;
            case RECORD:
                node.setChildren(recType());
                break;
            default:
                error(ARRAY, RECORD);
        }
        return node;
    }
    private TreeNode low() {
        TreeNode node = node("Low");
        node.setChildren(match(INTC));
        return node;
    }
    private TreeNode recType() {
        TreeNode node = node("RecType");
        node.setChildren(match(RECORD), filedDecList(), match(END));
        return node;
    }
    private TreeNode idMore() {
        TreeNode node = node("IdMore");
        switch (peekToken().getType()) {
            case SEMI:
                node.setChildren(node());
                break;
            case COMMA:
                node.setChildren(match(COMMA), idList());
                break;
            default:
                error(SEMI, COMMA);
        }
        return node;
    }
    private TreeNode idList() {
        TreeNode node = node("IdList");
        node.setChildren(match(ID), idMore());
        return node;
    }
    private TreeNode filedDecList() {
        TreeNode node = node("FiledDecList");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case INTEGER:
            case CHAR:
                node.setChildren(baseType(), idList(), match(SEMI), filedDecMore());
                break;
            case ARRAY:
                node.setChildren(arrayType(), idList(), match(SEMI), filedDecMore());
                break;
            default:
                error(INTEGER, CHAR, ARRAY);
        }
        return node;
    }
    private TreeNode filedDecMore() {
        TreeNode node = node("FiledDecMore");
        switch (peekToken().getType()) {
            case END:
                node.setChildren(node());
                break;
            case INTEGER:
            case CHAR:
            case ARRAY:
                node.setChildren(filedDecList());
                break;
            default:
                error(END, INTEGER, CHAR, ARRAY);
        }
        return node;
    }
    private TreeNode arrayType() {
        TreeNode node = node("ArrayType");
        node.setChildren(match(ARRAY), match(LMIDPAREN),
                low(), match(UNDERRANGE), top(),
                match(RMIDPAREN), match(OF), baseType());
        return node;
    }
    private TreeNode top() {
        TreeNode node = node("Top");
        node.setChildren(match(INTC));
        return node;
    }
    private TreeNode baseType() {
        TreeNode node = node("BaseType");
        Token token = peekToken();
        TokenType type = token.getType();
        switch (type) {
            case INTEGER:
                node.setChildren(match(INTEGER));
                break;
            case CHAR:
                node.setChildren(match(CHAR));
                break;
            default:
                error(INTEGER, CHAR);
        }
        return node;
    }
    private TreeNode typeId() {
        TreeNode node = node("TypeID");
        node.setChildren(match(ID));
        return node;
    }



    private TreeNode programName() {
        TreeNode node = node("ProgramName");
        node.setChildren(match(ID));
        return node;
    }

    private TreeNode match(TokenType type) {
        Token token=getToken();
        TreeNode node=node();
        if(token!=null){
            if (token.getType().equals(type)){
                node=node(type.getStr());
                switch (type){
                    case ID:
                    case INTC:
                    case CHARACTER:
                        node=node(token.getValue());
                }
            }else
                errors.add("Unexpected token near `" + token.getValue() + "`. `"
                        + type.getStr() + "` expected. "
                        + " at line[" + token.getLine()  + "]");
        } else errors.add("Unexpected EOF. No more tokens at input stream.");
        return node;
    }

    private TreeNode node() {
        return new TreeNode("绌�");
    }

    private TreeNode node(String value) {
        return new TreeNode(value);
    }

    Token getToken(){
       if (positon<tokenList.size()){
           return tokenList.get(positon++);
       }
       return null;
    }
    protected Token peekToken() {
        if (positon<tokenList.size()){
            return tokenList.get(positon);
        }
        return null;
    }
    protected void error(TokenType... types) {
        StringBuilder sb = new StringBuilder("Unexpected token near `" + peekToken().getValue() + "`. |");
        for (TokenType t : types) {
            sb.append(t.getStr());
            sb.append("|");
        }
        sb.append(" expected. ");
        sb.append(" at line[");
        sb.append(peekToken().getLine());
        sb.append("]");
        errors.add(sb.toString());
    }
}

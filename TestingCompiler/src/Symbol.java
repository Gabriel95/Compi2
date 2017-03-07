/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jpaz
 */
public class Symbol {

    private  Object value;
    private int type;
    private int yyline;
    private int yycolumn;

    Symbol(int type, int yyline, int yycolumn) {
        this.type = type;
        this.yyline = yyline;
        this.yycolumn = yycolumn;
    }

    public int getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public int getYyline() {
        return yyline;
    }

    public int getYycolumn() {
        return yycolumn;
    }

    Symbol(int type, int yyline, int yycolumn, Object value) {
        this.type = type;
        this.yyline = yyline;
        this.yycolumn = yycolumn;
        this.value = value;
    }
    
}

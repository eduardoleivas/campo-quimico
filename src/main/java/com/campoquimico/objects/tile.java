package com.campoquimico.objects;

public class tile {
    private int id;
    private String symbol;
    private String name;
    private String tip1;
    private String tip2;
    private String tip3;
    private String tip4;
    private String tip5;
    private String tip6;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getSymbol() {
        return symbol;
    }
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getTip1() {
        return tip1;
    }
    public void setTip1(String tip1) {
        this.tip1 = tip1;
    }
    public String getTip2() {
        return tip2;
    }
    public void setTip2(String tip2) {
        this.tip2 = tip2;
    }
    public String getTip3() {
        return tip3;
    }
    public void setTip3(String tip3) {
        this.tip3 = tip3;
    }
    public String getTip4() {
        return tip4;
    }
    public void setTip4(String tip4) {
        this.tip4 = tip4;
    }
    public String getTip5() {
        return tip5;
    }
    public void setTip5(String tip5) {
        this.tip5 = tip5;
    }
    public String getTip6() {
        return tip6;
    }
    public void setTip6(String tip6) {
        this.tip6 = tip6;
    }

    //CONSTRUCTOR
    public tile(int id, String symbol, String name, String tip1, String tip2, String tip3, String tip4, String tip5, String tip6) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.tip1 = tip1;
        this.tip2 = tip2;
        this.tip3 = tip3;
        this.tip4 = tip4;
        this.tip5 = tip5;
        this.tip6 = tip6;
    }
}

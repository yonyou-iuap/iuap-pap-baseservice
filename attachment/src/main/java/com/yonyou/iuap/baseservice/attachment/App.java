package com.yonyou.iuap.baseservice.attachment;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    private String name;

    public App(String name) {
        super();
        this.name = name;
    }

    public static void main(String[] args) {
        System.out.println("Hello World!");

        List<App> ls = new ArrayList();
        ls.add(new App("aa"));
        ls.add(new App("bb"));

        for (App app : appendList(ls)) {

            System.out.println("return:"+app.name);
        }

        for (App app : ls) {

            System.out.println("inout:"+app.name);
        }

    }


    private static List<App> appendList(List<App> ls) {
        for (App app : ls) {
            app.name = app.name + "append";
        }
        return ls;
    }

    ;
}

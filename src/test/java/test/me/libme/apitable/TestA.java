package test.me.libme.apitable;

/**
 * Created by J on 2018/2/10.
 */
public class TestA {

    public static void main(String[] args) {

        String a="aaaa${param}bbb${ param }cc";

        System.out.println(a.replaceAll("([$][{]\\s*param\\s*[}])","0"));



    }

}

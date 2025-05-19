package dz.alaabo;

import dz.alaabo.core.Cache;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Cache cache = new Cache(100); // max 100 entries

        cache.put("session", "A1B2C3", 5000);
        System.out.println("Get session: " + cache.get("session"));

        Thread.sleep(6000);
        System.out.println("Get session after 6 seconds: " + cache.get("session"));
    }
}
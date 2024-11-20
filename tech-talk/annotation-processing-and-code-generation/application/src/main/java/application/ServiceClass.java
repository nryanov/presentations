package application;


import annotations.ExecutionTimer;

import java.util.concurrent.ThreadLocalRandom;

@ExecutionTimer
public class ServiceClass implements Logic, AnotherLogic {
    @Override
    public void foo() {
        System.out.println("Some logic");
    }

    @Override
    public int boo(String a) {
        System.out.println("Another logic");
        return ThreadLocalRandom.current().nextInt();
    }
}

package application;

import metered.MeteredServiceClass;

public class Main {
    public static void main(String[] args) {
        var delegate = new ServiceClass();
        var meteredServiceClass = new MeteredServiceClass(delegate);

        meteredServiceClass.foo();
        meteredServiceClass.boo("test");
    }
}

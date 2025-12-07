package net.fina.server.util;

import jakarta.ejb.Asynchronous;
import jakarta.ejb.Stateless;

@Stateless
public class ExecutorBean {
    @Asynchronous
    public void execute(Runnable command) {
        command.run();
    }
}
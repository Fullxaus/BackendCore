package ru.mentee.power.crm;

import ru.mentee.power.crm.web.HelloCrmServer;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        HelloCrmServer server = new HelloCrmServer(port);

        // Добавляем shutdown hook для корректной остановки сервера при Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Stopping server...");
            server.stop();
        }));

        server.start();

        // Добавляем бесконечное ожидание, чтобы main не завершился
        Thread.currentThread().join();
    }
}


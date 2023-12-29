package com.luv2code.doan.singleton;

public class LoggingService {
    private static LoggingService instance;

    private LoggingService() {
        // Ẩn constructor để ngăn việc tạo đối tượng LoggingService từ bên ngoài
    }

    public static synchronized LoggingService getInstance() {
        if (instance == null) {
            instance = new LoggingService();
        }
        return instance;
    }

    public void info(String message) {
        System.out.println("[INFO] " + message);
    }

    public void error(String message) {
        System.out.println("[ERROR] " + message);
    }
}

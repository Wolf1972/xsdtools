package ru.bis.datadic;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.nio.file.Files;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) {
        System.out.println("XSD tools.");

        // Looking for log4j
        String log4JPropertyFile = "log4j2.xml"; // Is Log4j configuration file in custom place?
        if (Files.isRegularFile(Paths.get(log4JPropertyFile))) {
            System.setProperty("log4j.configurationFile", log4JPropertyFile);
        }

        Logger logger = LogManager.getLogger(App.class);

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        TestApp testApp = context.getBean("testApp", TestApp.class);
//        testApp.test();
//        testApp.readDir("\\target\\test1\\"); // FNS 311-P
        testApp.readDir("\\target\\test2\\"); // UFEBS
//        testApp.readDir("\\target\\test3\\"); // ISO 20022
//        testApp.readDir("");

        context.close();
    }
}

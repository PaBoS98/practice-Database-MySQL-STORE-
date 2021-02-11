package com.service;

import com.service.enums.ProductStatus;

import java.util.Scanner;

public class Specify {

    private static Scanner scanner;

    public static String specifyString (String dialog) {
        scanner = new Scanner(System.in);
        System.out.println(dialog);
        return scanner.nextLine();
    }

    public static Integer specifyInt (String dialog) {
        scanner = new Scanner(System.in);
        while (true) {
            System.out.println(dialog);
            if (scanner.hasNextInt()) {
                return Integer.valueOf(scanner.next());
            } else {
                System.err.println("wrong number");
                scanner.next();
            }
        }
    }

    public static String specifyEnum (String dialog) {
        scanner = new Scanner(System.in);
        System.out.println(dialog);
        while (true) {
            String status = scanner.nextLine();
            if (status.equals("out_of_stock")) {
                return String.valueOf(ProductStatus.OUT_OF_STOCK);
            } else if (status.equals("in_stock")) {
                return String.valueOf(ProductStatus.IN_STOCK);
            } else if (status.equals("running_low")) {
                return String.valueOf(ProductStatus.RUNNING_LOW);
            } else {
                System.err.println(dialog);
            }
        }
    }
}

package com.company;
import java.util.Scanner;

public class Palindrome {
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String s = args[i];
        }
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine(); //ввод строки
        System.out.println(reverseString(s)); // вывод слова наоборот
        System.out.println(isPalindrome(s)); // вывод палиндрома
    }
    public static String reverseString(String s) {
        String s1 = "";
        for (int length = s.length() - 1; length >= 0; length--)
            s1 += s.charAt(length);
            return s1;
        }
        public static boolean isPalindrome(String s) {
            String s1 = reverseString(s); //вызов функции
            boolean palindrome = s.equals(s1); //проверка на палиндром
            return palindrome;
        }
    }


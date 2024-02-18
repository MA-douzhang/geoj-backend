package com.madou.geojcodesandbox;

/**
 * @author MA_dou
 * @version 1.0
 * @project geoj-backend-microservice
 * @description
 * @date 2024/2/17 10:52:23
 */

import java.util.Scanner;

public class test1 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String str = sc.nextLine();
        System.out.println(longestPalindrome(str));
    }
    public static String longestPalindrome(String s) {
        String ans = "";
        int max = 0;
        int len = s.length();
        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j <= len; j++) {
                String tmp = s.substring(i, j);
                if (isPalindromic(tmp) && tmp.length() > max) {
                    ans = tmp;
                    max = j - i;
                }
            }
        }
        return ans;
    }

    private static boolean isPalindromic(String s) {
        int len = s.length();
        for (int i = 0; i < len / 2; i++) {
            if (s.charAt(i) != s.charAt(len - i - 1)) {
                return false;
            }
        }
        return true;
    }
}

package com.tujuhsembilan.logic;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ConsoleUtil {

  public static final Scanner in = new Scanner(System.in);

  public static void printClear() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  public static void printDivider(Character character) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < 35; i++) {
      sb.append(character);
    }
    System.out.println(sb.toString());
  }

  public static void printDivider() {
    printDivider('=');
  }

  public static void delay(int seconds) {
    try {
      TimeUnit.SECONDS.sleep(seconds);
    } catch (InterruptedException e) {
      e.printStackTrace();
      Thread.currentThread().interrupt();
    }
  }

  public static void delay() {
    delay(3);
  }

}

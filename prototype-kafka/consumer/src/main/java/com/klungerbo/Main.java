package com.klungerbo;

public class Main {

  public static void main(String[] args) {
    int consumers = 10;
    for (int i = 0; i < consumers; i++) {
      Consumer consumer = new Consumer();
      consumer.start();
    }
  }
}

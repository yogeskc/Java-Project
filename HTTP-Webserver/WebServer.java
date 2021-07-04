import server.Server;

import java.io.FileNotFoundException;
import java.io.IOException;

public class WebServer {
  public static void main(String[] args) {
    // This file will be compiled by script and must be at 
    // the root of your project directory
  
    try {
      new Server().start();
    } catch (IOException e) {
      System.out.println("Error when creating server");
      e.printStackTrace();
    }
  }
}

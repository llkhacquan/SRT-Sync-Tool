package quannk.srtsynctool;

import java.io.File;

public class CheckSRTFiles {
  public static void main(String... args) {
    File folder = new File(".");
    System.out.println("Checking folder: " + folder.getAbsolutePath());
    checkSRTFiles(folder);
  }

  public static void checkSRTFiles(File folder) {
    File[] files = folder.listFiles();
    SRTFile.logToConsole = true;
    for (File f : files) {
      if (f.isFile()) {
        if (f.getName().contains(".srt"))
          checkFile(f);
      } else {
        checkSRTFiles(f);
      }
    }
  }

  public static void checkFile(File f) {
    SRTFile.parse(f.getPath());
  }
}

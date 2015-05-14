package quannk.srtsynctool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;

import javax.swing.JOptionPane;

public class SRTFile {
  public static final String UTF8_BOM = "\uFEFF";
  public Vector<Speech> speechs = new Vector<Speech>();
  public static SRTSyncTool gui = null;
  public static boolean logToConsole = true;

  private SRTFile() {
  }

  public void writeToFile(String fileName) {

    try {
      BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
      out.write(UTF8_BOM);
      for (int i = 0; i < speechs.size(); i++) {
        Speech s = speechs.elementAt(i);
        out.write((i + 1) + "\n");
        out.write(s.begin.toString() + " --> " + s.end.toString() + "\n");
        out.write(s.content.substring(0, s.content.length() - 1) + "\n\n");
      }
      out.close();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Error!");
      e.printStackTrace();
    }
  }

  public static void printError(String s) {
    if (gui != null)
      gui.createMessageBox(s);
    if (logToConsole)
      System.out.println(s);
  }

  public static SRTFile parse(String filePath) {
    SRTFile srtFile = new SRTFile();
    try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
      String line = "";
      try {
        while ((line = br.readLine()) != null) {
          line = line.trim();
          if (line.startsWith(UTF8_BOM)) {
            line = line.substring(1).trim();
          }
          int index = -1;
          if (line.compareTo("") == 0)
            continue;
          try {
            index = Integer.parseInt(line);
          } catch (NumberFormatException e) {
            printError("Parse file " + filePath + " error.\nCheck at line " + line);
          }
          if (index < 0)
            throw new Exception();

          // read the time
          line = br.readLine().trim();
          String time = line.replace("-->", "_");
          String s[] = time.split("_");
          if (s == null || s.length != 2) {
            throw new Exception("");
          }
          Speech currentSpeech = new Speech();
          currentSpeech.begin = new Speech.Time(s[0]);
          currentSpeech.end = new Speech.Time(s[1]);

          // read the content
          line = br.readLine();
          while (line != null && line.length() > 0) {
            line = line.trim();
            if (line.contains(" --> ")) {
              printError("Please check the file " + filePath + "\nat speech " + index);
            }
            if (currentSpeech.content == "")
              currentSpeech.content += line;
            else {
              if (currentSpeech.content.length() + line.length() >= 50)
                currentSpeech.content += "\n" + line;
              else
                currentSpeech.content += " " + line;
            }
            line = br.readLine();
          }
          currentSpeech.content = currentSpeech.content.trim();
          srtFile.speechs.add(currentSpeech);
        }
      } catch (Exception e) {
        printError("Catch exception at line " + line + "\n in file " + filePath);
      }
      br.close();
    } catch (FileNotFoundException e) {
      printError("File " + filePath + " not found");
      e.printStackTrace();
    } catch (IOException e) {
      printError("Error!");
      e.printStackTrace();
    }
    return srtFile;
  }

  public static void main(String... args) {
    SRTFile.parse("C:\\Users\\wind\\Desktop\\HIMYM.S02E11.vi.srt");
  }

  public SRTFile clone() {
    SRTFile result = new SRTFile();
    for (int i = 0; i < speechs.size(); i++) {
      result.speechs.add(speechs.elementAt(i).clone());
    }
    return null;
  }
}

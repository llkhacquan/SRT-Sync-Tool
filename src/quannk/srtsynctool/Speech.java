package quannk.srtsynctool;

public class Speech {
  String content = "";
  Time begin, end;

  public static class Time {
    int hh, mm, ss, ms;
    String st = "";

    public Time(String s) {
      String parts[] = s.trim().replace(".", ",").split(":|,");
      hh = Integer.parseInt(parts[0]);
      mm = Integer.parseInt(parts[1]);
      ss = Integer.parseInt(parts[2]);
      ms = Integer.parseInt(parts[3]);

      s = String.format("%02d", hh) + ":" + String.format("%02d", mm) + ":" + String.format("%02d", ss) + "," + String.format("%03d", ms);
      this.st = s;
    }

    public String toString() {
      return st;
    }

    public Time clone() {
      return new Time(st);
    }
  }

  public Speech clone() {
    Speech s = new Speech();
    s.content = content;
    s.begin = begin.clone();
    s.end = end.clone();
    return s;
  }
}

package quannk.srtsynctool;

public class Speech {
	String content = "";
	Time begin, end;
	
	public static class Time {
		int hh, mm, ss, ms;
		String st = "";
		public Time(String s){
			this.st = s;
			assert(s.length() == 12);
			hh = Integer.parseInt(s.substring(0, 2));
			mm = Integer.parseInt(s.substring(3, 5));
			ss = Integer.parseInt(s.substring(6, 8));
			ms = Integer.parseInt(s.substring(9, 12));
		}
		
		public String toString(){
			return st;
		}
		
		public Time clone(){
			return new Time(st);
		}
	}
	
	public Speech clone(){
		Speech s =new Speech();
		s.content = content;
		s.begin = begin.clone();
		s.end = end.clone();
		return s;
	}
}

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
	Vector<Speech> speechs = new Vector<Speech>();

	private SRTFile() {
		// TODO Auto-generated constructor stub
	}

	public void writeToFile(String fileName) {

		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName), "UTF-8"));
			out.write(UTF8_BOM);
			for (int i = 0; i < speechs.size(); i++) {
				Speech s = speechs.elementAt(i);
				out.write((i + 1) + "\n");
				out.write(s.begin.toString() + " --> " + s.end.toString()
						+ "\n");
				out.write(s.content.substring(0, s.content.length() - 1)
						+ "\n\n");
			}

			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static SRTFile parse(String fileName) {
		SRTFile srtFile = new SRTFile();
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith(UTF8_BOM)) {
					line = line.substring(1);
				}
				int index = -1;
				if (line.compareTo("") == 0)
					continue;
				try {
					index = Integer.parseInt(line);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				assert (index >= 0);

				// read the time
				line = br.readLine();
				Speech currentSpeech = new Speech();
				currentSpeech.begin = new Speech.Time(line.substring(0, 12));
				currentSpeech.end = new Speech.Time(line.substring(17));

				// read the content
				line = br.readLine();
				while (line.length() > 0) {
					if (line.contains(" --> ")) {
						JOptionPane.showMessageDialog(null, "Check file "
								+ fileName + " at speech " + index);
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

				srtFile.speechs.add(currentSpeech);
			}
			br.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File " + fileName
					+ " not found");
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return srtFile;
	}

	public static void main(String... args) {
		SRTFile.parse("test/HIMYM.S02E03.en.srt").writeToFile("test/out.srt");
	}

	public SRTFile clone() {
		SRTFile result = new SRTFile();
		for (int i = 0; i < speechs.size(); i++) {
			result.speechs.add(speechs.elementAt(i).clone());
		}

		return null;
	}
}

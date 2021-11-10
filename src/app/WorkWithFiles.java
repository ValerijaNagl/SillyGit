package app;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WorkWithFiles {

	public static File searchFile(File file, String search) {
		if (file.isDirectory()) {
			File[] arr = file.listFiles();
			for (File f : arr) {
				File found = searchFile(f, search);
				if (found != null)
					return found;
			}
		} else {
			if (file.getName().equals(search)) {
				return file;
			}
		}
		return null;
	}

	public static void writeToFile(String path, String content) {
		try {
			FileWriter myWriter = new FileWriter(path);
			myWriter.write(content);
			myWriter.close();

		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public static String relativePath(String file1, String file2) {
		if (file1.length() < file2.length()) {
			return file1.substring(file2.length());
		}
		return "";
	}

	public static String getContent(List<String> lines) {
		StringBuilder stringBuilder = new StringBuilder();
		for (String line : lines) {
			stringBuilder.append(line);
			stringBuilder.append(System.getProperty("line.separator"));
		}

		return stringBuilder.toString();
	}

	public static String getFileExtension(File file) {
		String fileName = file.getName();
		if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
			return fileName.substring(fileName.lastIndexOf(".") + 1);
		else
			return "";
	}

	public static String getFileWithoutVersion(String name) {
		int lastIndexOfUderscore = name.lastIndexOf("_");
		if (lastIndexOfUderscore == -1) {
			return name.substring(0, name.length());
		}
		return name.substring(0, lastIndexOfUderscore);
	}
	
	public String getFileWithoutExtension(File file) {
		String name = file.getName();
		int lastIndexOf = name.lastIndexOf(".");
		if (lastIndexOf == -1) {
			return file.getName();
		}
		return name.substring(0, lastIndexOf);
	}
	
}

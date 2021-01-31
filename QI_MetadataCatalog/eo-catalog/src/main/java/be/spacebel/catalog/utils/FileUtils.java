package be.spacebel.catalog.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class provide the utilities for file and directory processing
 *
 * @author tth
 *
 */
public class FileUtils {

	public static String SERIES_METADATA_ZIP_FILE = "series_metadata.zip";
	public static String SERIES_METADATA_DIRECTORY = "series_metadata";
	public static String DATASET_METADATA_ZIP_FILE = "dataset_metadata.zip";
	public static String DATASET_METADATA_DIRECTORY = "dataset_metadata";
	public static String CSV_METADATA_DIRECTORY = "csv_metadata";

	/**
	 * Get list of files in the inputed folder
	 *
	 * @param folder
	 *            folder to be processed
	 * @return list of file
	 */
	public static ArrayList<String> getFilesInFolder(File folder, String extension) {
		ArrayList<String> listFiles = new ArrayList<>();
		for (File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				ArrayList<String> fList = getFilesInFolder(fileEntry, extension);
				listFiles.addAll(fList);
			} else {
				String filePath = fileEntry.getAbsolutePath();
				if (filePath.endsWith(extension)) {
					listFiles.add(filePath);
				}

			}
		}
		return listFiles;
	}

	/**
	 * Read the content of a file
	 *
	 * @param fileName
	 *            : path to the file to be processed
	 * @return content of the inputed file
	 * @throws IOException
	 */
	public static String readFile(String fileName) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		try(FileReader f = new FileReader(fileName); BufferedReader reader = new BufferedReader(f)){
			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}
		}
		return stringBuilder.toString();
	}

	public static String readFileHeader(String fileName) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		try(FileReader f = new FileReader(fileName); BufferedReader reader = new BufferedReader(f)){
			String line;
			line = reader.readLine();
			stringBuilder.append(line);
		}
		return stringBuilder.toString();
	}

	public static void write2File(String data, String fileName, boolean mode) throws IOException {

		int idx = fileName.lastIndexOf("/");
		if (idx < 0) {
			idx = fileName.lastIndexOf("\\");
		}
		String location = fileName.substring(0, idx);
		File f = new File(location);
		if (!f.exists()) {
			f.mkdirs();
		}

		// Create file
		try(FileWriter fstream = new FileWriter(fileName, mode); BufferedWriter out = new BufferedWriter(fstream)){
			out.write(data + "\n");
		}
	}
}

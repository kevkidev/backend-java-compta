package kevkidev.compta.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CsvService {

	@FunctionalInterface
	public interface CSVToObjectConverter<T> {
		List<T> convertCSVToObject(final String line, final boolean verbose);
	}

	public static final boolean COMMENTED_CSV_LINE = true;
	public static final boolean VERBOSE = true;
	public static final boolean NO_VERBOSE = false;

	private String lastExportCsvFileName;

	public CsvService() {
		lastExportCsvFileName = "";
	}

	public void exportAllToCSV(List<String> data) throws FileNotFoundException, InterruptedException {
		var datetime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
		final var FILE_NAME = "compta-export-csv-" + datetime + ".csv";
		var csvOutputFile = new File(FILE_NAME);

		try (var writer = new PrintWriter(csvOutputFile)) {
			data.forEach(line -> {
				writer.println(line);
			});
			System.out.println("Exported to " + csvOutputFile.getAbsolutePath());
		}
		lastExportCsvFileName = FILE_NAME;
	}

	public List<String> findExistingCsvFile() {
		var folder = new File("./");
		return List.of(folder.list()).stream().filter(file -> file.matches("^(\\w*-)+\\d+-\\d+\\.csv"))
				.collect(Collectors.toList());
	}

	public void importCsv(final CSVToObjectConverter<?> converter) throws IOException, InterruptedException {
		importCsv(lastExportCsvFileName, converter);
	}

	public void importCsv(final String fileName, final CSVToObjectConverter<?> converter)
			throws IOException, InterruptedException {
		if (null == fileName || fileName.isBlank()) {
			System.out.println("Sorry: No file found.");
		} else {
			readCSV(fileName, VERBOSE, converter);
			lastExportCsvFileName = fileName;
		}
	}

	/**
	 * Read the last exported file
	 *
	 * @param verbose
	 * @throws FileNotFoundException
	 * @throws InterruptedException
	 */
	public void readCSV(final boolean verbose) throws FileNotFoundException, InterruptedException {
		readCSV(lastExportCsvFileName, verbose, null);
	}

	/**
	 * Read un CSV file. Make import if converter is not null
	 *
	 * @param fileName
	 * @param verbose
	 * @param converter
	 * @throws InterruptedException
	 */
	private void readCSV(final String fileName, final boolean verbose, final CSVToObjectConverter<?> converter)
			throws InterruptedException {

		System.out.println("Try reading file: " + fileName);
		try (Scanner sc = new Scanner(new File(fileName))) {
			System.out.println("CSV file reading ...");
			while (sc.hasNextLine()) {
				var currentLine = sc.nextLine();
				if (verbose) {
					System.out.println(currentLine);
				}
				if ('#' == currentLine.charAt(0)) {
					continue;
				}
				if (null != converter) {
					converter.convertCSVToObject(currentLine, verbose);
				}
			}
			System.out.println("#CSV file closed.");
		} catch (FileNotFoundException e) {
			System.out.println("File \"" + fileName + "\" not found.");
		}
	}

	public String selectExistingCsvFile(final List<String> fileNames, final BufferedReader input) throws IOException {
		System.out.println("Please selected a file from the list :");
		var count = 0;
		for (Iterator<String> iterator = fileNames.iterator(); iterator.hasNext();) {
			String filename = iterator.next();
			count++;
			System.out.println(count + " : " + filename);
		}

		System.out.print("Number ?> ");
		var selectedFileNumber = Integer.parseInt(input.readLine());
		if (selectedFileNumber < 0 || selectedFileNumber > count) {
			selectExistingCsvFile(fileNames, input);
		}
		return fileNames.get(selectedFileNumber - 1);

	}
}

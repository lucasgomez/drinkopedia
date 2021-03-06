package ch.lgo.drinks.simple.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;

import com.jumbletree.docx5j.xlsx.XLSXFile;
import com.jumbletree.docx5j.xlsx.builders.CellBuilder;
import com.jumbletree.docx5j.xlsx.builders.RowBuilder;
import com.jumbletree.docx5j.xlsx.builders.WorksheetBuilder;

import ch.lgo.drinks.simple.dao.DescriptiveLabel;
import ch.lgo.drinks.simple.entity.HasId;

public abstract class AbstractDocx5JHelper {

	protected class DocumentBuilder {
		private XLSXFile file;
		private boolean firstSheet;
		
		public DocumentBuilder() throws JAXBException, InvalidFormatException {
			file = new XLSXFile();
			firstSheet = true;
		}
		
		public DocumentBuilder appendSheet(String sheetTitle, String columnTitle, List<String> list) throws Exception {
			WorksheetBuilder sheet = firstSheet ? file.getWorkbookBuilder().getSheet(0)
												: file.getWorkbookBuilder().appendSheet();
			firstSheet = false;
			
			sheet.setName(sheetTitle);
			addContent(sheet, columnTitle);
			for (String value : list) {
				addContent(sheet, value);
			}
			return this;
		}
		//TODO Duplicated code
		//TODO Remove those Map parameters
		public DocumentBuilder appendSheet(String title, Map<String, List<String>> values) throws Exception {
			WorksheetBuilder sheet = firstSheet ? file.getWorkbookBuilder().getSheet(0)
					: file.getWorkbookBuilder().appendSheet();
			firstSheet = false;
			
			insertStrings(values, sheet, title);
			return this;
		}
		
		public DocumentBuilder appendSheet2(String sheetTitle, List<String> titles, List<List<String>> lists) throws Exception {
			WorksheetBuilder sheet = firstSheet ? file.getWorkbookBuilder().getSheet(0)
					: file.getWorkbookBuilder().appendSheet();
			firstSheet = false;
			
			sheet.setName(sheetTitle);
			addContent(sheet, titles);
			lists.forEach(list -> addContent(sheet, list));
			
			return this;
		}

		public File save(String fullName) throws IOException, Docx4JException {
			File out = new File(fullName);
			file.save(out);
			return out;
		}

		private void insertStrings(List<String> content, WorksheetBuilder sheet, String sheetTitle) throws Exception {
			sheet.setName(sheetTitle);
			for (String value : content) {
				addContent(sheet, value);
			}
		}
		
		private void insertStrings(Map<String, List<String>> content, WorksheetBuilder sheet, String sheetTitle) throws Docx4JException {
			sheet.setName(sheetTitle);
			for (Entry<String, List<String>> entry : content.entrySet()) {
				addContent(sheet, entry.getKey(), entry.getValue());
			}
		}
		
		private void insertStrings2(List<List<String>> content, WorksheetBuilder sheet, String sheetTitle) throws Docx4JException {
			sheet.setName(sheetTitle);
			content.stream()
				.forEachOrdered(line -> addContent(sheet, line));
		}
	}
	
	protected String buildFullName(String path, String baseFileName, String extension) {
		StringBuilder fullPath = new StringBuilder();
		if (StringUtils.isNotBlank(path)) {
			fullPath.append(path);
			if (!path.substring(path.length()-1, path.length()).contentEquals("/")) {
				fullPath.append("/");
			}
		}
		if (StringUtils.isNoneBlank(baseFileName)) {
			fullPath.append(baseFileName);
		} else {
			fullPath.append("phi");
		}
		fullPath.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("_yyyy-MM-dd_HH-mm-ss")));
		fullPath.append(extension);
		
		return fullPath.toString();
	}
	
	protected void addContent(WorksheetBuilder sheet, String value) {
		try {
			sheet.nextRow().nextCell().value(value);
		} catch (Docx4JException e) {
			//Yes, I know...
			throw new RuntimeException(e);
		}
	}
	
	protected void addContent(WorksheetBuilder sheet, String key, List<String> values) {
		try {
			RowBuilder builder = sheet.nextRow().nextCell().value(key).row();
			if (values != null) {
				for (String value : values) {
					builder = builder.nextCell().value(value).row();
				}
			}
		} catch (Docx4JException e) {
			//Yes, I know...
			throw new RuntimeException(e);
		}
	}
	
	protected void addContent(WorksheetBuilder sheet, List<String> values) {
		CellBuilder cell;
		try {
			cell = sheet.nextRow().nextCell();
			for (String value : values) {
				cell = cell.value(value).row().nextCell();
			}
		} catch (Docx4JException e) {
			//Yes, I know...
			throw new RuntimeException(e);
		}
	}
	
	protected String nullableToString(Object object) {
		if (object == null)
			return "";
		else
			return object.toString();
	}
	
	protected <T extends HasId & DescriptiveLabel> List<String> writeIdAndName(T entity) {
		return Arrays.asList(nullableToString(entity.getId()), entity.getName());
	}

}

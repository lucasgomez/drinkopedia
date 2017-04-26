package ch.lgo.drinks.simple.service;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.springframework.stereotype.Service;
import org.xlsx4j.sml.STHorizontalAlignment;

import com.jumbletree.docx5j.xlsx.XLSXFile;
import com.jumbletree.docx5j.xlsx.builders.CellBuilder;
import com.jumbletree.docx5j.xlsx.builders.RowBuilder;
import com.jumbletree.docx5j.xlsx.builders.WorksheetBuilder;

import ch.lgo.drinks.simple.dto.BottledBeerDetailedDto;

@Service
public class XlsxOutputService extends AbstractDocx5JHelper {

	private static final double FIRST_ROW_HEIGHT = 22.28;
	private static final double SECOND_ROW_HEIGHT = 15.27;
	private static final String BEER_STYLE_NAME = "Beername";
	private static final String PRICE_STYLE_NAME = "Price";
	private static final String INFOS_STYLE_NAME = "Infos";
	private static final String BREWERY_STYLE_NAME = "Brewery";
	private static final String VOLUME_STYLE_NAME = "Volume";
	private static final String DESCRIPTION_STYLE_NAME = "Description";
	private static final String FONT_NAME = "Calibri";
	private static final String EXTENSION = ".xlsx";
	
	private class DocumentBuilder {
		private XLSXFile file;
		private boolean firstSheet;
		
		public DocumentBuilder() throws JAXBException, InvalidFormatException {
			file = new XLSXFile();
			firstSheet = true;
		}
		
		public DocumentBuilder appendSheet(String title, Set<String> values) throws Exception {
			WorksheetBuilder sheet = firstSheet ? file.getWorkbookBuilder().getSheet(0)
												: file.getWorkbookBuilder().appendSheet();
			firstSheet = false;
			
			insertStrings(new ArrayList<>(values), sheet, title);
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
	}
	
	public File outputBeerStylesAndColors(Set<String> styleNames, Set<String> colorNames, String path, String baseFileName) throws Exception {
		return new DocumentBuilder()
				.appendSheet("Styles", styleNames)
				.appendSheet("Colors", colorNames)
				.save(buildFullName(path, baseFileName, EXTENSION));
	}
	
	public File outputBeersColors(Set<String> colorsNames, String path, String baseFileName) throws Exception {
		XLSXFile file = new XLSXFile();
		insertStrings(new ArrayList<>(colorsNames), file.getWorkbookBuilder().getSheet(0));
		
		File out = new File(buildFullName(path, baseFileName, EXTENSION));
		file.save(out);
		return out;
	}
	
	public File outputBottlesPriceLists(List<BottledBeerDetailedDto> list, String path, String baseFileName) throws Exception {
		XLSXFile file = new XLSXFile();
		
		createStyles(file);
		insertSummarizedList(list, file.getWorkbookBuilder().getSheet(0));
		insertDetailedList(list, file.getWorkbookBuilder().appendSheet());
		
		File out = new File(buildFullName(path, baseFileName, EXTENSION));
		file.save(out);
		return out;
	}
	
	public void insertStrings(List<String> content, WorksheetBuilder sheet) throws Exception {
		sheet.setName("BigList");
		for (String value : content) {
			addContent(sheet, value);
		}
	}
	
	public void insertSummarizedList(List<BottledBeerDetailedDto> list, WorksheetBuilder sheet) throws Exception {
		sheet.setName("BigList");
		
		prepareColumnsWidth(sheet);
		
		for (BottledBeerDetailedDto beer : list) {
			addBeerLines(sheet, beer);
		}
	}
	
	public void insertDetailedList(List<BottledBeerDetailedDto> list, WorksheetBuilder sheet) throws Exception {
		sheet.setName("DetailedList");
		
		prepareColumnsWidth(sheet);
		
		for (BottledBeerDetailedDto beer : list) {
			addDetailedBeerLines(sheet, beer);
		}
	}

	private void prepareColumnsWidth(WorksheetBuilder sheet) throws Docx4JException {
		List<Double> widths = Arrays.asList(0.47, 4.55, 5.57, 1.37, 1.37, 3.1);
		Double factor = 4.8565;
		
		for (Double width : widths) {
			sheet.addColumnDefinition(width*factor, false);
		}
	}

	private void createStyles(XLSXFile file) {
		createStyle(BEER_STYLE_NAME, file, 18, true);
		createStyle(INFOS_STYLE_NAME, file, 12, false);
		createStyle(BREWERY_STYLE_NAME, file, 12, true);
		createStyle(VOLUME_STYLE_NAME, file, 14, false, STHorizontalAlignment.RIGHT);
		createStyle(PRICE_STYLE_NAME, file, 20, true, STHorizontalAlignment.RIGHT);
		createStyle(DESCRIPTION_STYLE_NAME, file, 12, false, STHorizontalAlignment.JUSTIFY);
	}
	
	private void createStyle(String styleName, XLSXFile document, Integer fontSize, boolean isBold) {
		createStyle(styleName, document, fontSize, isBold, STHorizontalAlignment.LEFT);
	}
	
	private void createStyle(String styleName, XLSXFile document, Integer fontSize, boolean isBold, STHorizontalAlignment horizontal) {
		document.createStyle()
					.withFont(FONT_NAME, fontSize, Color.BLACK, isBold, false)
					.withAlignment(horizontal, null)
					.installAs(styleName);
		
	}
	
	private void addContent(WorksheetBuilder sheet, String value) throws Docx4JException {
		sheet.nextRow().nextCell().value(value);
	}
	
	private void addBeerLines(WorksheetBuilder sheet, BottledBeerDetailedDto beer) throws Docx4JException {
		
		sheet.nextRow()
				.sheet()
			 .nextRow()
				.height(FIRST_ROW_HEIGHT, true)
				.nextCell()
					.row()
				.nextCell()
					.style(BEER_STYLE_NAME)
					.value(beer.getName())
					.row()
				.nextCell()
					.row()
				.nextCell()
					.style(VOLUME_STYLE_NAME)
					.value(displayVolume(beer))
					.row()
				.nextCell()
					.style(VOLUME_STYLE_NAME)
					.value(displayABV(beer))
					.row()
				.nextCell()
					.style(PRICE_STYLE_NAME)
					.value(displayPrice(beer))
					.row()
				.sheet()
			.nextRow()
				.height(SECOND_ROW_HEIGHT, true)
				.nextCell()
					.row()
				.nextCell()
					.style(BREWERY_STYLE_NAME) //TODO Format & merge cells
					.value(String.format("%s (%s - %s)", beer.getProducerName(), beer.getProducerOriginShortName(), beer.getProducerOriginShortName()))
					.row()
				.nextCell()
					.style(INFOS_STYLE_NAME)
					.value(String.format("%s - %s", "lager", "noire")); //TODO Replace placeholders
	}
	
	private void addDetailedBeerLines(WorksheetBuilder sheet, BottledBeerDetailedDto beer) throws Docx4JException {
		addBeerLines(sheet, beer);
		
		//Add details
		RowBuilder row = sheet.nextRow().height(SECOND_ROW_HEIGHT);
		CellBuilder firstCell = row.nextCell();
		CellBuilder lastCell = firstCell.row().nextCell().row().nextCell().row().nextCell();
		
//		XLSXRange range = new XLSXRange(sheet, firstCell, lastCell);
//		firstCell.row()
//			.nextCell()
//				.style(DESCRIPTION_STYLE_NAME)
//				.value(beer.getDrinkComment())
//				.row()
//			.addExplicitSpan(1, 4);
		
		//TODO Merge 4 cells
		//TODO Autoheight
	}
	
	private String displayPrice(BottledBeerDetailedDto beer) {
		DecimalFormat format = new DecimalFormat("#.0");
		if (beer.getBottlePrice() != null)
			return format.format(beer.getBottlePrice()) + " .-";
		else
			return "";
	}

	private String displayVolume(BottledBeerDetailedDto beer) {
		if (beer.getBottleVolumeInCl() != null)
			return String.format("%s cl", beer.getBottleVolumeInCl());
		else
			return "";
	}

	private String displayABV(BottledBeerDetailedDto beer) {
		DecimalFormat format = new DecimalFormat("#.0");
		if (beer.getAbv() != null)
			return format.format(beer.getAbv())+"%";
		else
			return "";
	}
}

package ch.lgo.drinks.simple.service;

import java.awt.Color;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.stereotype.Service;
import org.xlsx4j.sml.STHorizontalAlignment;

import com.jumbletree.docx5j.xlsx.XLSXFile;
import com.jumbletree.docx5j.xlsx.builders.CellBuilder;
import com.jumbletree.docx5j.xlsx.builders.RowBuilder;
import com.jumbletree.docx5j.xlsx.builders.WorksheetBuilder;

import ch.lgo.drinks.simple.dto.DetailedPrintingDrinkDTO;

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
	
	public File outputBottlesPriceLists(List<DetailedPrintingDrinkDTO> list, String path, String baseFileName) throws Exception {
		XLSXFile file = new XLSXFile();
		
		createStyles(file);
		insertSummarizedList(list, file.getWorkbookBuilder().getSheet(0));
		insertDetailedList(list, file.getWorkbookBuilder().appendSheet());
		
		File out = new File(buildFullName(path, baseFileName, EXTENSION));
		file.save(out);
		return out;
	}
	
	public void insertSummarizedList(List<DetailedPrintingDrinkDTO> list, WorksheetBuilder sheet) throws Exception {
		sheet.setName("BigList");
		
		prepareColumnsWidth(sheet);
		
		for (DetailedPrintingDrinkDTO beer : list) {
			addBeerLines(sheet, beer);
		}
	}
	
	public void insertDetailedList(List<DetailedPrintingDrinkDTO> list, WorksheetBuilder sheet) throws Exception {
		sheet.setName("DetailedList");
		
		prepareColumnsWidth(sheet);
		
		for (DetailedPrintingDrinkDTO beer : list) {
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
	
	private void addBeerLines(WorksheetBuilder sheet, DetailedPrintingDrinkDTO beer) throws Docx4JException {
		
		sheet.nextRow()
				.sheet()
			 .nextRow()
				.height(FIRST_ROW_HEIGHT, true)
				.nextCell()
					.row()
				.nextCell()
					.style(BEER_STYLE_NAME)
					.value(beer.getDrinkName())
					.row()
				.nextCell()
					.row()
				.nextCell()
					.style(VOLUME_STYLE_NAME)
					.value(displayVolume(beer.getVolumeInCl()))
					.row()
				.nextCell()
					.style(VOLUME_STYLE_NAME)
					.value(displayABV(beer.getDrinkAbv()))
					.row()
				.nextCell()
					.style(PRICE_STYLE_NAME)
					.value(displayPrice(beer.getPrice()))
					.row()
				.sheet()
			.nextRow()
				.height(SECOND_ROW_HEIGHT, true)
				.nextCell()
					.row()
				.nextCell()
					.style(BREWERY_STYLE_NAME) //TODO Format & merge cells
					.value(String.format("%s (%s - %s)", beer.getDrinkProducerName(), beer.getDrinkProducerOriginShortName(), beer.getDrinkProducerOriginShortName()))
					.row()
				.nextCell()
					.style(INFOS_STYLE_NAME)
					.value(String.format("%s - %s", "lager", "noire")); //TODO Replace placeholders
	}
	
	private void addDetailedBeerLines(WorksheetBuilder sheet, DetailedPrintingDrinkDTO beer) throws Docx4JException {
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
	
	private String displayPrice(double price) {
		DecimalFormat format = new DecimalFormat("#.0");
		return format.format(price) + " .-";
	}

	private String displayVolume(int volume) {
		return String.format("%s cl", volume);
	}

	private String displayABV(double abv) {
		DecimalFormat format = new DecimalFormat("#.0");
		return format.format(abv)+"%";
	}
}

package ch.lgo.drinks.simple.service;

import java.awt.Color;
import java.io.File;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.stereotype.Service;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.CTRst;
import org.xlsx4j.sml.CTXstringWhitespace;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.STCellType;
import org.xlsx4j.sml.STHorizontalAlignment;

import com.jumbletree.docx5j.xlsx.XLSXFile;
import com.jumbletree.docx5j.xlsx.builders.WorksheetBuilder;

import ch.lgo.drinks.simple.dto.DetailedPrintingDrinkDTO;

@Service
public class XlsxOutputService {

	private static final double FIRST_ROW_HEIGHT = 22.28;
	private static final double SECOND_ROW_HEIGHT = 15.27;
	private static final String BEER_STYLE_NAME = "Beername";
	private static final String PRICE_STYLE_NAME = "Price";
	private static final String INFOS_STYLE_NAME = "Infos";
	private static final String BREWERY_STYLE_NAME = "Brewery";
	private static final String VOLUME_STYLE_NAME = "Volume";
	private static final String FONT_NAME = "Calibri";
	
	public File outputBottlesPriceList(List<DetailedPrintingDrinkDTO> list, String path, String baseFileName) throws Exception {
		XLSXFile file = new XLSXFile();
		
		createStyles(file);
		WorksheetBuilder sheet = file.getWorkbookBuilder().getSheet(0);
		sheet.setName("Beers");
		
		prepareColumnsWidth(sheet);
		
		for (DetailedPrintingDrinkDTO beer : list) {
			addBeerLines(sheet, beer);
		}
		
		File out = new File(buildFullName(path, baseFileName));
		file.save(out);
		return out;
	}
	
	private String buildFullName(String path, String baseFileName) {
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
		fullPath.append(".xlsx");
		
		return fullPath.toString();
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
				.height(FIRST_ROW_HEIGHT, true)
				.nextCell()
					.row()
				.nextCell()
					.style(BEER_STYLE_NAME)
					.value(beer.getBeerName())
					.row()
				.nextCell()
					.row()
				.nextCell()
					.style(VOLUME_STYLE_NAME)
					.value(displayVolume(beer.getVolumeInCl()))
					.row()
				.nextCell()
					.style(VOLUME_STYLE_NAME)
					.value(displayABV(beer.getBeerAbv()))
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
					.style(BREWERY_STYLE_NAME)
					.value(String.format("%s (%s)", beer.getBeerProducerName(), beer.getBeerProducerOriginShortName()))
					.row()
				.nextCell()
					.style(INFOS_STYLE_NAME)
					.value("Lager, Noire")
					.row()
				.sheet()
			.nextRow();
	}
	
	private Cell createCell(String content) {
		Cell cell = Context.getsmlObjectFactory().createCell();
		CTXstringWhitespace ctx = Context.getsmlObjectFactory().createCTXstringWhitespace();
		ctx.setValue(content);
		
		CTRst ctrst = new CTRst();
		ctrst.setT(ctx);

		cell.setT(STCellType.INLINE_STR);
		cell.setIs(ctrst); 
		return cell;
	}
	
	private void addNewCell(Row row, String styleName, String content) {
		row.getC().add(createCell(content));
	}
	
	private void addEmptyCell(Row row) {
		row.getC().add(Context.getsmlObjectFactory().createCell());
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

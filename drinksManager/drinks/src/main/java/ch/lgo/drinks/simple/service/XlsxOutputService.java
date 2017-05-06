package ch.lgo.drinks.simple.service;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.TapBeer;

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
	private static final double TAP_BEER_RATIO = 3L;

	//TODO Separate beer related processing with output itself
	public File outputBeersPricesWithDetails(List<Beer> beers, String path, String baseFileName) throws Exception {
		Map<String, List<Beer>> beersByType = beers.stream()
				.filter(beer -> beer.getTap() != null || beer.getBottle() != null)
				.collect(Collectors.groupingBy(beer -> beer.getTap() != null ? "tap" : "bottle"));

		//Start at 1 due to title line
		int rowNum = 1;
		List<List<String>> content = beersByType.get("tap").stream()
			.map(tapBeer -> tapBeerToTupple(tapBeer, 1))
			.collect(Collectors.toList());
		
		content.add(0, Arrays.asList("Id","ExternalId","Name","Style","Color","Abv","BuyingPricePerLiter","PriceBig","PriceSmall",
				"Ideal selling price big", "Ideal selling price small", "Price per alc Cl big", "Price per alc Cl small"));
		
		return new DocumentBuilder().appendSheet("Prices calc", content).save(buildFullName(path, baseFileName, EXTENSION));
	}

	/**
	 * [0] Id 
	 * [1] ExternalId
	 * [2] Name
	 * [3] Style
	 * [4] Color 
	 * [5] Abv 
	 * [6] BuyingPricePerLiter
	 * [7] PriceBig 
	 * [8] PriceSmall
	 * [9] Ideal selling price big
	 * [10] Ideal selling price small
	 * [11] Alcool price per Cl big
	 * [12] Alccol price per Cl small
	 * @param tapBeer
	 * @return
	 */
	private List<String> tapBeerToTupple(Beer tapBeer, int rowNum) {
		//Add fields from tapBeer
		List<String> result = new ArrayList<>();
		result.add(tapBeer.getId().toString());
		result.add(tapBeer.getExternalId());
		result.add(tapBeer.getName());
		result.add(tapBeer.getStyle() != null ? tapBeer.getStyle().getName() : "");
		result.add(tapBeer.getColor() != null ? tapBeer.getColor().getName() : "");
		result.add(tapBeer.getAbv() != null ? tapBeer.getAbv().toString() : "");
		result.add(tapBeer.getTap() != null ? tapBeer.getTap().getBuyingPricePerLiter().toString() : "");
		result.add(tapBeer.getTap() != null && tapBeer.getTap().getPriceBig() != null
				? tapBeer.getTap().getPriceBig().toString() : "");
		result.add(tapBeer.getTap() != null && tapBeer.getTap().getPriceSmall() != null
				? tapBeer.getTap().getPriceSmall().toString() : "");

		//Add calculated price
		DecimalFormat format = new DecimalFormat("#.000");
		Double pricePerCl = tapBeer.getTap().getBuyingPricePerLiter() / 100;
		String idealSellingPriceBig = format.format(pricePerCl * TapBeer.VOLUME_BIG_CL * TAP_BEER_RATIO);
		String idealSellingPriceSmall = format.format(pricePerCl * TapBeer.VOLUME_SMALL_CL * TAP_BEER_RATIO);
		result.add(idealSellingPriceBig); // [9]
		result.add(idealSellingPriceSmall); // [10]
		
		//Add formula to calculate price / cl of alcohol
		String cellAdressAbv = buildCellAdress(5, rowNum);
		String cellAdressPriceBig = buildCellAdress(7, rowNum);
		String cellAdressPriceSmall = buildCellAdress(8, rowNum);
		// Volume of Alc = Volume * ABV / 100
		// Price per Cl of alc = price / volume of alc
		String alcoholPriceFormulaBig = String.format("= %s / (%s * %s / 100)", cellAdressPriceBig, TapBeer.VOLUME_BIG_CL, cellAdressAbv);
		String alcoholPriceFormulaSmall = String.format("= %s / (%s * %s / 100)", cellAdressPriceSmall, TapBeer.VOLUME_SMALL_CL, cellAdressAbv);
		
		result.add(alcoholPriceFormulaBig); // [11]
		result.add(alcoholPriceFormulaSmall); // [12]
		
		return result;
	}

	public File outputBeerStylesAndColors(Set<String> styleNames, Set<String> colorNames, String path, String baseFileName) throws Exception {
		return new DocumentBuilder()
				.appendSheet("Styles", styleNames)
				.appendSheet("Colors", colorNames)
				.save(buildFullName(path, baseFileName, EXTENSION));
	}
	
	public File outputBeersAndCode(Map<String, List<String>> beersAndCode, String path, String baseFileName) throws InvalidFormatException, IOException, Docx4JException, JAXBException, Exception {
		return new DocumentBuilder()
				.appendSheet("Styles", beersAndCode)
				.save(buildFullName(path, baseFileName, EXTENSION));
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

	//TODO Merge with duplicate method in ImportDataService
	private static String buildCellAdress(int col, int row) {
		String str = getColumnId(col);
		return str + (row+1);
	}
	
	private static String getColumnId(int col) {
		if (col >= 26) {
			int colBit = col % 26;
			System.out.println("Bit: " + colBit);
			col = col - colBit;
			col = col / 26;
			col -= 1;
			System.out.println("Col: " + col);
			return getColumnId(col) + getColumnId(colBit);
		} else {
			return String.valueOf("ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(col));
		}
	}
}

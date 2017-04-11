package ch.lgo.drinks.simple.service;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfTextProperties;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.table.Cell;
import org.odftoolkit.simple.table.Row;
import org.odftoolkit.simple.table.Table;
import org.springframework.stereotype.Service;

import com.jumbletree.docx5j.docx.DOCXBuilder;
import com.jumbletree.docx5j.docx.Paragraph;

import ch.lgo.drinks.simple.dto.DetailedPrintingDrinkDTO;

@Service
public class DocxOutputService extends AbstractDocx5JHelper {

	private static final String BEER_STYLE_NAME = "Beername";
	private static final String INFOS_STYLE_NAME = "Infos";
	private static final String BREWERY_STYLE_NAME = "Brewery";
	private static final String VOLUME_STYLE_NAME = "Volume";
	private static final String EXTENSION = ".docx";

	public File outputBottlesPriceList(List<DetailedPrintingDrinkDTO> list, String path, String baseFileName) throws Exception {
		DOCXBuilder builder = new DOCXBuilder();
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

		for (DetailedPrintingDrinkDTO beer : list) {
			Paragraph paragraph = builder.createParagraph()
//			.addBoldText(beer.getDrinkName()).addTabDefinition(0, "left", );
			.addBoldText(beer.getDrinkName()).tab().addBoldText(displayPrice(beer.getPrice()));
			
			wordMLPackage.getMainDocumentPart().addParagraph(paragraph.toString());
			
//			paragraph.
		}
		
		//TODO Try tabs with rule
		//TODO Add text with bold/normal
		//TODO Add tabs
		//TODO Add styles
		//TODO Justify
		//TODO Refactor
		
//		Save saver = new Save(wordMLPackage);
//		saver.save(out);

//		createStyles(wordMLPackage);
//		P paragraph = wordMLPackage.getMainDocumentPart().addStyledParagraphOfText("Title", "Hello Maven Central");
//		wordMLPackage.getMainDocumentPart().addParagraphOfText("from docx4j!");
		// Now save it
		File file = new File(buildFullName(path, baseFileName, EXTENSION));
		wordMLPackage.save(file);
		return file;
	}

	private void prepareColumnsWidth(Table table) {
		setColumnWidth(table, 0, 67.6);
		setColumnWidth(table, 1, 38.2);
		setColumnWidth(table, 2, 10.2);
		setColumnWidth(table, 3, 10.2);
		setColumnWidth(table, 4, 10);
		setColumnWidth(table, 5, 11);
		setColumnWidth(table, 6, 18.1);
		setColumnWidth(table, 7, 8);
	}

	private void createStyles(SpreadsheetDocument document) {
		// Styles
		createStyle(BEER_STYLE_NAME, document, 18, true);
		createStyle(INFOS_STYLE_NAME, document, 12, false);
		createStyle(BREWERY_STYLE_NAME, document, 12, true);
		createStyle(VOLUME_STYLE_NAME, document, 14, false);
	}

	private void createStyle(String styleName, SpreadsheetDocument document, Integer fontSize, boolean isBold) {
		OdfStyle style = document.getDocumentStyles().newStyle(styleName, OdfStyleFamily.TableCell);
		if (isBold)
			style.setProperty(OdfTextProperties.FontWeight, "bold");
		style.setProperty(OdfTextProperties.FontSize, fontSize.toString());
	}

	/**
	 * @param beer
	 *            Item to output
	 * @param outputDocument
	 *            Destination document
	 * @param rowNumber
	 *            row id wher to start insertion
	 * @return Row number of the next available cell
	 */
	private void addBeerLines(DetailedPrintingDrinkDTO beer, Row firstRow, Row secondRow) {
		int colIndex = 0;

		// Setup format
		firstRow.setHeight(8, true);
		secondRow.setHeight(4.9, true);

		// 1st row
//		writeCell(firstRow, colIndex++, BEER_STYLE_NAME, beer.getDrinkName());
//		colIndex++; // empty cell
//		writeCell(firstRow, colIndex++, VOLUME_STYLE_NAME, displayVolume(beer.getVolumeInCl()));
//		writeCell(firstRow, colIndex++, VOLUME_STYLE_NAME, displayABV(beer.getBeerAbv()));
//		colIndex++;
//		writeCell(firstRow, colIndex++, BEER_STYLE_NAME, displayPrice(beer.getPrice()));
//
//		// 2nd row
//		colIndex = 0;
//		writeCell(secondRow, colIndex++, BREWERY_STYLE_NAME,
//				String.format("%s (%s)", beer.getBeerProducerName(), beer.getBeerProducerOriginShortName()));
//		writeCell(secondRow, colIndex++, BREWERY_STYLE_NAME, "Lager, Noire");
	}

	private void setColumnWidth(Table table, int columnIndex, double width) {
		table.getColumnByIndex(columnIndex).setWidth(width);
	}

	private void writeCell(Row row, int columnIndex, String styleName, String text) {
		Cell cell = row.getCellByIndex(columnIndex);
		cell.setStringValue(text);
		cell.setCellStyleName(styleName);
	}

	private String displayPrice(double price) {
		DecimalFormat format = new DecimalFormat("#.##");
		return format.format(price) + " .-";
	}

	private String displayVolume(int volume) {
		return String.format("%s cl", volume);
	}

	private String displayABV(double abv) {
		DecimalFormat format = new DecimalFormat("#.#%");
		return "'" + format.format(abv);
	}
}

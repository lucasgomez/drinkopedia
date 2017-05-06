package ch.lgo.drinks.simple.service;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorkbookPart;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xlsx4j.exceptions.Xlsx4jException;
import org.xlsx4j.org.apache.poi.ss.usermodel.DataFormatter;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;
import org.xlsx4j.sml.Worksheet;

import ch.lgo.drinks.simple.dao.BeerColorRepository;
import ch.lgo.drinks.simple.dao.BeerStylesRepository;
import ch.lgo.drinks.simple.dao.BeersRepository;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BeerColor;
import ch.lgo.drinks.simple.entity.BeerStyle;
import ch.lgo.drinks.simple.entity.BottledBeer;
import ch.lgo.drinks.simple.entity.TapBeer;

@Service
public class ImportDataService {
	
	@Autowired
	private BeersRepository beersRepository;
	@Autowired
	private BeerColorRepository colorsRepository;
	@Autowired
	private BeerStylesRepository stylesRepository;

	public Set<String> extractUnreferencedBeersColors(String pathAndFilename) throws Docx4JException, Xlsx4jException {
		Set<String> colorsToCreate = new HashSet<>();
		
		//Filers already existing colors
		readAndProcessContent(pathAndFilename, 0,
				colorName -> colorsRepository.findByName(colorName).isEmpty(), 
				colorName -> colorsToCreate.add(colorName),
				2);
		
		return colorsToCreate;
	}

	public Set<String> extractUnreferencedBeersStyles(String pathAndFilename) throws Docx4JException, Xlsx4jException {
		Set<String> stylesToCreate = new HashSet<>();
		//TODO Manage file not found
		//Filers already existing styles
		readAndProcessContent(pathAndFilename, 0,
				styleName -> stylesRepository.findByName(styleName).isEmpty(),
				styleName -> stylesToCreate.add(styleName), 
				5);
		
		stylesToCreate.forEach(createdStyle -> System.out.println(createdStyle));
		return stylesToCreate;
	}
	
	public Map<String, List<String>> extractUnreferencedBeersAndCode(String pathAndFilename) throws Docx4JException, Xlsx4jException {
		Map<String, List<String>> beersToCreate = new HashMap<>();
		//Filers already existing styles
		readAndProcessContent(pathAndFilename, 0,
				record -> beersRepository.loadByExternalCode(record.getKey()) == null,
				record -> beersToCreate.put(record.getKey(), record.getValue()),
				0, 1);
		
		return beersToCreate;
	}

	public Set<Beer> extractUnreferencedPricesAnServiceType(String pathAndFilename) throws Docx4JException, Xlsx4jException {
		Map<String, List<String>> beersToComplete = new HashMap<>();
		Map<STATUS_ENUM, List<String>> tapBeersToComplete = new HashMap<>();

		//Get all existing beer and map by ExternalId
		Map<String, Beer> beersByExternalId = beersRepository.findAll().stream()
				.collect(toMap(beer -> beer.getExternalId(), beer -> beer));
		
		//Retrieve data for existing beers only
		readAndProcessContent(pathAndFilename, 0,
				record -> beersByExternalId.get(record.getKey()) != null, 
				record -> beersToComplete.put(record.getKey(), record.getValue()),
				0, 3, 5);
		
		Predicate<Entry<String, List<String>>> tapBeersFilter = beerDetails -> beerDetails.getValue().get(0).contains("L");
		Function<Entry<String, List<String>>, TapBeer> tapBeersConverter = beerDetails -> tapBeerFromEntry(beerDetails, beersByExternalId.get(beerDetails.getKey()));
		Function<Entry<String, List<String>>, BottledBeer> bottledBeersConverter = beerDetails -> bottledBeerfromEntry(beerDetails, beersByExternalId.get(beerDetails.getKey()));
		
		//Use Collect instead
		Set<TapBeer> tapBeersToCreate = beersToComplete.entrySet().stream()
		    	.filter(tapBeersFilter)
		    	.map(tapBeersConverter)
		    	.collect(Collectors.toSet());
		Set<BottledBeer> bottledBeersToCreate = beersToComplete.entrySet().stream()
		    	.filter(tapBeersFilter.negate())
		    	.map(bottledBeersConverter)
		    	.collect(Collectors.toSet());
		
		Set<Beer> beerWithPrice = tapBeersToCreate.stream()
									.map(tapBeer -> beersRepository.addTapBeer(tapBeer))
									.collect(Collectors.toSet());
		beerWithPrice.addAll(bottledBeersToCreate.stream()
				.map(bottledBeer -> beersRepository.addBottledBeer(bottledBeer))
				.collect(Collectors.toSet()));
		
		return beerWithPrice;
	}
	
	public Set<Beer> importBeersDetails(String pathAndFilename) throws Xlsx4jException, Docx4JException {
		WorkbookPart workbook = openSpreadsheetFile(pathAndFilename);
		DataFormatter formatter = new DataFormatter();

		List<List<String>> content = readContent2(workbook.getWorksheet(0), formatter, Arrays.asList(0, 2, 3, 4, 5, 6));
		
		Map<String, Beer> beersByExtCode = beersRepository.findAll().stream()
				.collect(Collectors.toMap(beer -> beer.getExternalId(), beer -> beer));
		Map<String, BeerColor> colorsByName = colorsRepository.findAll().stream()
				.collect(Collectors.toMap(color -> color.getName(), color -> color));
		Map<String, BeerStyle> stylesByName = stylesRepository.findAll().stream()
				.collect(Collectors.toMap(style -> style.getName(), style -> style));
		
		return content.stream()
				.filter(row -> {
					String[] parts = row.get(0).split(" - ");
					return parts.length > 1 && beersByExtCode.get(parts[0]) != null;
				})
				.map(row -> {
					Beer beer = beersByExtCode.get(row.get(0).split(" - ")[0]);
					beer.setColor(colorsByName.get(row.get(1)));
//					beer.setPlato(Long.valueOf(row.get(2)));
					beer.setIbu(Long.valueOf(3));
					beer.setStyle(stylesByName.get(row.get(4)));
					try {
						beer.setAbv(Double.valueOf(row.get(5)));
					} catch (NumberFormatException ex) {}
					return beersRepository.save(beer);
				})
				.collect(Collectors.toSet());
	}

	private TapBeer tapBeerFromEntry(Entry<String, List<String>> entry, Beer beer) {
		TapBeer tap = new TapBeer();
		tap.setBeer(beer);
		Double price = Double.valueOf(entry.getValue().get(1));
		tap.setBuyingPricePerLiter(price);
		return tap;
	}
	
	private BottledBeer bottledBeerfromEntry(Entry<String, List<String>> entry, Beer beer) {
		BottledBeer tap = new BottledBeer();
		tap.setBeer(beer);
		Double price = Double.valueOf(entry.getValue().get(1));
		tap.setPrice(price);
		return tap;
	}
	
	private void readAndProcessContent(String path, int sheetId, Consumer<String> action, int columnId) throws Docx4JException, Xlsx4jException {
		readAndProcessContent(path, sheetId, item -> true, action, columnId);
	}
	
	private void readAndProcessContent(String path, int sheetId, Predicate<String> filter, Consumer<String> action, int columnId) throws Docx4JException, Xlsx4jException {
		WorkbookPart workbook = openSpreadsheetFile(path);
		DataFormatter formatter = new DataFormatter();

		Set<String> details = readContent(workbook.getWorksheet(0), formatter, columnId);
		
		//Create and persist beers
		details.stream().filter(filter).forEach(action);
	}
	
	//TODO Satan, please forbid those parameters type
	private void readAndProcessContent(String path, int sheetId, Predicate<Entry<String, List<String>>> filter, Consumer<Entry<String, List<String>>> action, int codeColumnId, int... columnsId) throws Docx4JException, Xlsx4jException {
		WorkbookPart workbook = openSpreadsheetFile(path);
		DataFormatter formatter = new DataFormatter();

		Map<String, List<String>> details = readContent(workbook.getWorksheet(0), formatter, codeColumnId, columnsId);
		
		//Create and persist beers
		details.entrySet().stream().filter(filter).forEach(action);
	}

	public Set<BeerStyle> importBeerStyles(String pathAndFilename, int sheetId) throws Docx4JException, Xlsx4jException {
		Set<BeerStyle> createdStyles = new HashSet<>();
		
		//Filers already existing colors
		readAndProcessContent(pathAndFilename, sheetId, 
				styleName -> createdStyles.add(stylesRepository.save(new BeerStyle(styleName))),
				0);
		
		return createdStyles;
	}

	public Set<BeerColor> importBeerColors(String path, int sheetId) throws Docx4JException, Xlsx4jException {
		WorkbookPart workbook = openSpreadsheetFile(path);
		DataFormatter formatter = new DataFormatter();
		
		Set<String> colors = readContent(workbook.getWorksheet(sheetId), formatter, 0);
		Set<BeerColor> createdColors = new HashSet<>();
		
		//Create and persist beers
		colors.forEach(colorName -> createdColors.add(colorsRepository.save(new BeerColor(colorName))));
		
		createdColors.forEach(color -> System.out.println(color.getId()+" : "+color.getName()));
		return createdColors;
	}

	public Set<Beer> importBeers(String pathAndFilename, int sheetId, int externalCodeColumnId, int nameColumnId) throws Docx4JException, Xlsx4jException {
		Set<Beer> createBeers = new HashSet<>();
		
		//Filers already existing colors
		readAndProcessContent(pathAndFilename, sheetId,
				mu -> true,
				newBeer -> createBeers.add(beersRepository.save(createBeer(newBeer.getKey(), newBeer.getValue().get(0)))),
				0, 1);
		
		return createBeers;
	}

	private Beer createBeer(String externalCode, String name) {
		Beer newBeer = new Beer();
		newBeer.setName(name);
		newBeer.setExternalId(externalCode);
		return newBeer;
	}

	private Beer addTap(Beer beerToTap) {
		TapBeer tap = new TapBeer();
		tap.setBeer(beerToTap);
		beerToTap.setTap(tap);
		return beerToTap;
	}
	
	private Set<Beer> addTap(Set<Beer> beersToTap) {
		Set<Beer> tapBeers = new HashSet<>();
		beersToTap.forEach(beerToTap -> tapBeers.add(addTap(beerToTap)));
		return tapBeers;
	}
	
	private Set<Beer> persistBeers(Set<Beer> beersToCreate) {
		Set<Beer> savedBeers = new HashSet<>();
		beersToCreate.forEach(beerToCreate -> savedBeers.add(beersRepository.save(beerToCreate)));
		return savedBeers;
	}

	private void printBeersReferencedOrNot(Map<STATUS_ENUM, Set<Beer>> tapResult, String title) {
		System.out.println(title+" :");
		System.out.println("   Existing :");
		tapResult.get(STATUS_ENUM.EXISTING).forEach(beer -> System.out.println(" - "+beer.getName()));
		System.out.println("   Not existing :");
		tapResult.get(STATUS_ENUM.NEW).forEach(beer -> System.out.println("    - "+beer.getName()));
	}

	private Map<STATUS_ENUM, Set<Beer>> sortBeersByReferencingStatus(Set<String> beersName) {
		List<Beer> beers = new ArrayList<>();
		Set<Beer> existingBeers = new HashSet<>();
		Set<Beer> beersToCreate = new HashSet<>();

		beersName.forEach(beerName -> beers.add(createBeerIfUnreferenced(beerName)));
		
		beers.stream().filter(beer -> beer.getId() == null).forEach(beer -> beersToCreate.add(beer));
		beers.stream().filter(beer -> beer.getId() != null).forEach(beer -> existingBeers.add(beer));

		Map<STATUS_ENUM, Set<Beer>> result = new HashMap<>();
		result.put(STATUS_ENUM.EXISTING, existingBeers);
		result.put(STATUS_ENUM.NEW, beersToCreate);
		
		return result;
	}

	private Beer createBeerIfUnreferenced(String beerName) {
		List<Beer> beerFound = beersRepository.findByName(beerName);
		if (beerFound == null || beerFound.isEmpty()) {
			Beer newBeer = new Beer();
			newBeer.setName(beerName);
			return newBeer;
		} else if (beerFound.size() == 1) {
			return beerFound.get(0);
		} else {
			StringBuilder existingNames = new StringBuilder();
			existingNames.append("Too many beers found for name '" +beerName +"' : ");
			beerFound.forEach(beer -> existingNames.append(beer.getName()+", "));
			throw new RuntimeException(existingNames.toString());
		}
	}
	
	private Set<String> readContent(WorksheetPart sheet, DataFormatter formatter, int columnId) {
		Set<String> result = new HashSet<>();
		String readValue;
		Worksheet ws = sheet.getJaxbElement();
		SheetData data = ws.getSheetData();

		for (Row r : data.getRow()) {
			if (!r.getC().isEmpty()) {
				Cell c = r.getC().get(columnId);
	
				readValue = formatter.formatCellValue(c);
				if (StringUtils.isNotBlank(readValue))
					result.add(readValue);
			}
		}
		return result;
	}
	
	private Map<String, List<String>> readContent(WorksheetPart sheet, DataFormatter formatter, int codeColumnId, int... columnsId) {
		Map<String, List<String>> result = new HashMap<>();
		String readValue;
		String readCode;
		Worksheet ws = sheet.getJaxbElement();
		SheetData data = ws.getSheetData();
		Cell c;
		
		for (Row r : data.getRow()) {
			if (!r.getC().isEmpty()) {
				List<String> line = new ArrayList<>();
				c = r.getC().get(codeColumnId);
				readCode = formatter.formatCellValue(c); 
				
				if (StringUtils.isNotBlank(readCode)) {
					for (int columnId : columnsId) {
						c = r.getC().get(columnId);
						
						readValue = formatter.formatCellValue(c);
						if (StringUtils.isNotBlank(readValue))
							line.add(readValue);
					}
					result.put(readCode, line);
				}
			}
		}
		return result;
	}

	private List<List<String>> readContent2(WorksheetPart sheet, DataFormatter formatter, List<Integer> columns) {
		Worksheet ws = sheet.getJaxbElement();
		SheetData data = ws.getSheetData();
		
		List<String> columnsToRead = columns.stream()
				.map(id -> getColumnId(id))
				.collect(Collectors.toList());
		
		return data.getRow().stream()
				.map(row -> row.getC().stream()
							.filter(cell -> columnsToRead.contains(cell.getR().substring(0, 1)))
							.map(cell -> formatter.formatCellValue(cell))
							.collect(Collectors.toList()))
				.collect(Collectors.toList());
	}
	
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

	private WorkbookPart openSpreadsheetFile(String inputfilepath) throws Docx4JException {
		// Open a document from the file system
		SpreadsheetMLPackage xlsxPkg = SpreadsheetMLPackage.load(new java.io.File(inputfilepath));
		return xlsxPkg.getWorkbookPart();
	}
	
	private enum STATUS_ENUM {
		EXISTING,
		NEW;
	}

	public void clearService() {
		beersRepository.clearService();
	}

}

package ch.lgo.drinks.simple.service;

import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

import ch.lgo.drinks.simple.dao.BarRepository;
import ch.lgo.drinks.simple.dao.BeerColorRepository;
import ch.lgo.drinks.simple.dao.BeerStylesRepository;
import ch.lgo.drinks.simple.dao.BeersRepository;
import ch.lgo.drinks.simple.dao.DescriptiveLabel;
import ch.lgo.drinks.simple.dao.ProducerRepository;
import ch.lgo.drinks.simple.entity.Bar;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BeerColor;
import ch.lgo.drinks.simple.entity.BeerStyle;
import ch.lgo.drinks.simple.entity.BottledBeer;
import ch.lgo.drinks.simple.entity.HasId;
import ch.lgo.drinks.simple.entity.Producer;
import ch.lgo.drinks.simple.entity.StrengthEnum;
import ch.lgo.drinks.simple.entity.TapBeer;

@Service
public class ImportDataService {
	
	@Autowired
	private BeersRepository beersRepository;
	@Autowired
	private BeerColorRepository colorsRepository;
	@Autowired
	private BeerStylesRepository stylesRepository;
	@Autowired
	private ProducerRepository producerRepository;
	@Autowired
	private BarRepository barsRepository;

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

	//TODO Split into methods
	public Set<Beer> readAndImportPricesAndServiceType(String pathAndFilename) throws Docx4JException, Xlsx4jException {
		//Get all existing beer and map by ExternalId
		Map<String, Beer> beersByExternalId = mapBeersByExternalId(beersRepository.findAll());
		Predicate<List<String>> tapBeersFilter = beerDetails -> beerDetails.get(1).contains("L");
		Map<String, List<String>> beersToComplete = new HashMap<>();

		//Read content of file and filters out non existing beers
		WorkbookPart workbook = openSpreadsheetFile(pathAndFilename);
		List<List<String>> content = readContent2(workbook.getWorksheet(0), Arrays.asList(1, 10, 14))
				.stream()
				.filter(record -> beersByExternalId.get(record.get(0)) != null)
				.collect(Collectors.toList());
		
		//From content create tap and bottle objects
		Set<TapBeer> tapBeersToCreate = content.stream()
		    	.filter(tapBeersFilter)
		    	.map(tapRecord -> {
		    		TapBeer tap = new TapBeer();
		    		Beer beer = beersByExternalId.get(tapRecord.get(0));
		    		tap.setBeer(beer);
		    		Double price = Double.valueOf(tapRecord.get(2));
		    		tap.setBuyingPricePerLiter(price);
		    		return tap;
		    	})
		    	.collect(Collectors.toSet());
		Set<BottledBeer> bottledBeersToCreate = content.stream()
		    	.filter(tapBeersFilter.negate())
		    	.map(tapRecord -> {
		    		BottledBeer bottle = new BottledBeer();
		    		Beer beer = beersByExternalId.get(tapRecord.get(0));
		    		bottle.setBeer(beer);
		    		Double price = Double.valueOf(tapRecord.get(2));
		    		bottle.setBuyingPrice(price);
		    		Long volume = Long.valueOf(tapRecord.get(1).split("/")[1]);
		    		bottle.setVolumeInCl(volume);
		    		return bottle;
		    	})
		    	.collect(Collectors.toSet());
		
		//From tap / bottles to create, save then store resulting Beer
		Set<Beer> beerWithPrice = tapBeersToCreate.stream()
									.map(tapBeer -> beersRepository.addTapBeer(tapBeer))
									.collect(Collectors.toSet());
		beerWithPrice.addAll(bottledBeersToCreate.stream()
				.map(bottledBeer -> beersRepository.addBottledBeer(bottledBeer))
				.collect(Collectors.toSet()));
		
		return beerWithPrice;
	}

	public Set<Beer> importSellingPrices(String pathAndFilename) throws Docx4JException, Xlsx4jException {
		Map<Long, Beer> beersById = mapEntitiesById(beersRepository.findAll());
		
		WorkbookPart workbook = openSpreadsheetFile(pathAndFilename);
		List<List<String>> tapContent = readContent2(workbook.getWorksheet(0), Arrays.asList(0, 7, 8));
		List<List<String>> bottledContent = readContent2(workbook.getWorksheet(1), Arrays.asList(0, 8));
		
		//TODO Fix stoopid comma format in xls file
		Function<List<String>, List<String>> commaTransformer = row -> row.stream()
				.map(cell -> cell != null ? cell.replace(",", ".") : "") 
				.collect(Collectors.toList());
		
		List<TapBeer> tapBeers = tapContent.stream()
			.filter(row -> row.size() == 3
						&& StringUtils.isNotBlank(row.get(0)) 
						&& StringUtils.isNotBlank(row.get(1)) 
						&& StringUtils.isNotBlank(row.get(2)))
			.map(commaTransformer)
			.filter(row -> {
				try {
					Long.valueOf(row.get(0));
					Double.valueOf(row.get(1));
					Double.valueOf(row.get(2));
					return true;
				} catch (NumberFormatException e) {
					return false;
				}
			})
			.filter(row -> beersById.get(Long.valueOf(row.get(0))) != null && beersById.get(Long.valueOf(row.get(0))).getTap() != null)
			.map(row -> {
				TapBeer tap = beersById.get(Long.valueOf(row.get(0))).getTap();
				tap.setPriceBig(Double.valueOf(row.get(1)));
				tap.setPriceSmall(Double.valueOf(row.get(2)));
				return tap;
			})
			.map(tap -> beersRepository.save(tap))
			.collect(Collectors.toList());
		
		List<BottledBeer> bottledBeers = bottledContent.stream()
				.filter(row -> row.size() == 2 
						&& StringUtils.isNotBlank(row.get(0)) 
						&& StringUtils.isNotBlank(row.get(1)))
				.map(commaTransformer)
				.filter(row -> {
					try {
						Long.valueOf(row.get(0));
						Double.valueOf(row.get(1));
						return true;
					} catch (NumberFormatException e) {
						return false;
					}
				})
				.filter(row -> beersById.get(Long.valueOf(row.get(0))) != null && beersById.get(Long.valueOf(row.get(0))).getBottle() != null)
				.map(row -> {
					BottledBeer bottle = beersById.get(Long.valueOf(row.get(0))).getBottle();
					bottle.setSellingPrice(Double.valueOf(row.get(1)));
					return bottle;
				})
				.map(bottle -> beersRepository.save(bottle))
				.collect(Collectors.toList());
		
		return null;
	}
	
	public Set<Beer> importBeersDetails(String pathAndFilename) throws Xlsx4jException, Docx4JException {
		WorkbookPart workbook = openSpreadsheetFile(pathAndFilename);

		List<List<String>> content = readContent2(workbook.getWorksheet(0), Arrays.asList(0, 2, 3, 4, 5, 6));
		
		Map<String, Beer> beersByExtCode = mapBeersByExternalId(beersRepository.findAll());
		Map<String, BeerColor> colorsByName = mapEntitiesByName(colorsRepository.findAll());
		Map<String, BeerStyle> stylesByName = mapEntitiesByName(stylesRepository.findAll());
		
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
					beer.setAbv(safeDoubleValueOf(row.get(5)));
					return beersRepository.save(beer);
				})
				.collect(Collectors.toSet());
	}

	private Double safeDoubleValueOf(String text) {
		try {
			return Double.valueOf(text);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private Long safeLongValueOf(String text) {
		if (StringUtils.isBlank(text))
			return null;
		try {
			return Long.valueOf(text);
		} catch (NumberFormatException ex) {
			return null;
		}	
	}
	
	private StrengthEnum safeStrengthValueOf(String text) {
		if (StringUtils.isNotBlank(text))
			return StrengthEnum.valueOf(text);
		else
			return null;
			
	}
	
	private TapBeer tapBeerFromEntry(List<String> entry, Beer beer) {
		TapBeer tap = new TapBeer();
		tap.setBeer(beer);
		Double price = Double.valueOf(entry.get(2));
		tap.setBuyingPricePerLiter(price);
		return tap;
	}
	
	private BottledBeer bottledBeerfromEntry(Entry<String, List<String>> entry, Beer beer) {
		BottledBeer tap = new BottledBeer();
		tap.setBeer(beer);
		Double price = Double.valueOf(entry.getValue().get(1));
		tap.setSellingPrice(price);
		return tap;
	}
	
	private void readAndProcessContent(String path, int sheetId, Consumer<String> action, int columnId) throws Docx4JException, Xlsx4jException {
		readAndProcessContent(path, sheetId, item -> true, action, columnId);
	}
	
	private void readAndProcessContent(String path, int sheetId, Predicate<String> filter, Consumer<String> action, int columnId) throws Docx4JException, Xlsx4jException {
		WorkbookPart workbook = openSpreadsheetFile(path);

		Set<String> details = readContent(workbook.getWorksheet(0), columnId);
		
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
		
		Set<String> colors = readContent(workbook.getWorksheet(sheetId), 0);
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
	
	private Set<String> readContent(WorksheetPart sheet, int columnId) {
		DataFormatter formatter = new DataFormatter();
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

	private List<List<String>> readContent2(WorksheetPart sheet, List<Integer> columns) {
		return readContent2(sheet, columns, false);
	}
	
	private List<List<String>> readContent2(WorksheetPart sheet, List<Integer> columns, boolean skipTitleRow) {
		DataFormatter formatter = new DataFormatter();
		Worksheet ws = sheet.getJaxbElement();
		SheetData data = ws.getSheetData();
		
		List<String> columnsToRead = columns.stream()
				.map(id -> getColumnId(id))
				.collect(Collectors.toList());
		
		return data.getRow().stream()
				.skip(skipTitleRow ? 1 : 0)
				.map(row -> {
						String[] rowContent = new String[columns.size()];
						row.getC().stream()
							.filter(cell -> columnsToRead.contains(cell.getR().substring(0, 1)))
							.forEach(cell -> rowContent[columnsToRead.indexOf(cell.getR().substring(0, 1))] = formatter.formatCellValue(cell));
						return Arrays.asList(rowContent);
						}
					)
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

	/**
	 * [0] Article
	 * [2] Acidité
	 * [3] Amertume
	 * [4] Aspect
	 * [5] Description courte
	 * [6] Douceur
	 * [8] Gout
	 * [9] Houblonnage
	 * [11] Mets
	 * [12] Nez
	 * [13] Remarque
	 * @param string
	 * @return
	 * @throws Xlsx4jException 
	 * @throws Docx4JException 
	 */
	public List<List<String>> extractRowsByExternalId(String pathAndFilename) throws Xlsx4jException, Docx4JException {
		Map<String, Beer> beersByExternalId = mapBeersByExternalId(beersRepository.findAll());
		
		WorkbookPart workbook = openSpreadsheetFile(pathAndFilename);
		return readContent2(workbook.getWorksheet(0), Arrays.asList(0, 2, 3, 4, 5, 6, 8, 9, 11, 12, 13))
				.stream()
			.filter(row -> beersByExternalId.keySet().contains(row.get(0).split(" - ")[0])
							|| row.get(0).startsWith("Article"))
			.collect(Collectors.toList());
	}

	public Set<Beer> importDescriptions(String pathAndFilename) throws Xlsx4jException, Docx4JException {
		Map<String, Beer> beersByExternalId = mapBeersByExternalId(beersRepository.findAll());
		
		WorkbookPart workbook = openSpreadsheetFile(pathAndFilename);
		List<List<String>> content = readContent2(workbook.getWorksheet(0), Arrays.asList(0, 1, 7, 8, 9, 10));
		content.remove(0);
		return content.stream()
				.filter(row -> beersByExternalId.keySet().contains(row.get(0).split(" - ")[0]))
				.map(row -> {
					Beer beer = beersByExternalId.get(row.get(0).split(" - ")[0]);
					beer.setComment(row.get(1));
					beer.setSourness(getStrengthFromWeirdNumber(row.get(2)));
					beer.setBitterness(getStrengthFromWeirdNumber(row.get(3)));
					beer.setSweetness(getStrengthFromWeirdNumber(row.get(4)));
					beer.setHopping(getStrengthFromWeirdNumber(row.get(5)));
					return beer;
				})
				.map(beerToUpdate -> beersRepository.save(beerToUpdate))
				.collect(Collectors.toSet());
	}
	
	public Set<Producer> importBeerProviders(String pathAndFilename) throws Docx4JException, Xlsx4jException {
		WorkbookPart workbook = openSpreadsheetFile(pathAndFilename);
		return readContent(workbook.getWorksheet(0), 1).stream()
			.collect(Collectors.toSet()).stream()
			.map(name -> new Producer(name))
			.map(producerRepository::save)
			.collect(Collectors.toSet());
	}
	
	public Set<Beer> updateBeersWithProducers(String pathAndFilename) throws Docx4JException, Xlsx4jException {
		WorkbookPart workbook = openSpreadsheetFile(pathAndFilename);

		Map<String, Beer> beersByExternalId = mapBeersByExternalId(beersRepository.findAll());
		Map<String, Producer> producersByName = mapEntitiesByName(producerRepository.findAll());
		
		return readContent2(workbook.getWorksheet(0), Arrays.asList(0, 1, 2), true).stream()
				.map(row -> beersByExternalId.get(row.get(0))
								.setProducer(producersByName.get(row.get(1)))
								.setName(row.get(2))
					)
				.map(beersRepository::save)
				.collect(Collectors.toSet());				
	}

	private Map<String, Beer> mapBeersByExternalId(Collection<Beer> beers) {
		return beers.stream()
				.collect(toMap(beer -> beer.getExternalId(), beer -> beer));
	}
	
	private <T extends DescriptiveLabel> Map<String, T> mapEntitiesByName(Collection<T> entities) {
		return entities.stream()
				.collect(toMap(entity -> entity.getName(), entity -> entity));
	}

	private <T extends HasId> Map<Long, T> mapEntitiesById(Collection<T> entities) {
		return entities.stream()
				.collect(toMap(entity -> entity.getId(), entity -> entity));
	}
	
	private StrengthEnum getStrengthFromWeirdNumber(String weirdNumber) {
		String rank = weirdNumber.substring(0, 1);
		return StrengthEnum.getStrengthByRank(Integer.valueOf(rank));
	}
	
	private Set<String> extractUnreferencedEntities(List<List<String>> content, int columnId, Map<String, ? extends DescriptiveLabel> entitiesByName) {
		return content.stream()
				.filter(row -> !row.isEmpty() && StringUtils.isNotBlank(row.get(columnId)))
				.map(row -> row.get(columnId))
				.filter(Objects::nonNull)
				.filter(entityName -> entitiesByName.get(entityName) == null)
				.collect(Collectors.toSet());
	}

	public Set<Beer> extractUnreferencedBeersFromStandardImporter(String pathAndFilename) throws Docx4JException, Xlsx4jException {
		Collection<Beer> beers = beersRepository.findAll();
		Map<String, Beer> beersByName = mapEntitiesByName(beers);
		Map<String, Producer> producersByName = mapEntitiesByName(producerRepository.findAll());
		Map<String, BeerColor> colorsByName = mapEntitiesByName(colorsRepository.findAll());
		Map<String, BeerStyle> stylesByName = mapEntitiesByName(stylesRepository.findAll());
		
		WorkbookPart workbook = openSpreadsheetFile(pathAndFilename);
		List<List<String>> content = readContent2(workbook.getWorksheet(0), IntStream.rangeClosed(0, 12).boxed().collect(Collectors.toList()), true);
		
//		content.remove(0);

		// [0] Beer
		// [1] Brewer
		// [2] Color
		// [3] Style
		// [4] Comment
		// [5] ABV
		// [6] Hopping
		// [7] Bitterness
		// [8] Sourness
		// [9] Sweetness
		// [10] Buying price bottle
		// [11] Bottle volume (cl)
		// [12] Buying price / L
		Map<String, Beer> newBeersByName = content.stream()
				.filter(row -> !row.isEmpty() && StringUtils.isNotBlank(row.get(0)))
				.filter(row -> beersByName.get(row.get(0)) == null)
				.map(row -> 
						(new Beer())
							.setName(row.get(0))
							.setProducer(producersByName.get(row.get(1)))
							.setColor(colorsByName.get(row.get(2)))
							.setStyle(stylesByName.get(row.get(3)))
							.setComment(row.get(4))
							.setAbv(safeDoubleValueOf(row.get(5)))
							.setHopping(safeStrengthValueOf(row.get(6)))
							.setBitterness(safeStrengthValueOf(row.get(7)))
							.setSourness(safeStrengthValueOf(row.get(8)))
							.setSweetness(safeStrengthValueOf(row.get(9)))
					)
				.map(beersRepository::save)
				.collect(Collectors.toMap(beer -> beer.getName(), beer -> beer));
		
		List<BottledBeer> newBottles = content.stream()
				.filter(row -> !row.isEmpty() && StringUtils.isNotBlank(row.get(0)))
				.filter(row -> newBeersByName.get(row.get(0)) != null)
				.filter(row -> StringUtils.isNotBlank(row.get(10)))
				.map(row -> (new BottledBeer())
						.setBeer(newBeersByName.get(row.get(0)))
						.setBuyingPrice(safeDoubleValueOf(row.get(10)))
						.setVolumeInCl(safeLongValueOf(row.get(11)))
					)
				.map(beersRepository::save)
				.collect(Collectors.toList());
		
		List<TapBeer> newTap = content.stream()
				.filter(row -> !row.isEmpty() && StringUtils.isNotBlank(row.get(0)))
				.filter(row -> newBeersByName.get(row.get(0)) != null)
				.filter(row -> StringUtils.isNotBlank(row.get(12)))
				.map(row -> (new TapBeer())
						.setBeer(newBeersByName.get(row.get(0)))
						.setBuyingPricePerLiter(safeDoubleValueOf(row.get(12)))
					)
				.map(beersRepository::save)
				.collect(Collectors.toList());
		
		return new HashSet<>(newBeersByName.values());
	}
	
	public Map<String, Set<String>> extractUnreferencedContentFromStandardImporter(String pathAndFilename) throws Docx4JException, Xlsx4jException {
		Map<String, Producer> producersByName = mapEntitiesByName(producerRepository.findAll());
		Map<String, BeerColor> colorsByName = mapEntitiesByName(colorsRepository.findAll());
		Map<String, BeerStyle> stylesByName = mapEntitiesByName(stylesRepository.findAll());
		
		WorkbookPart workbook = openSpreadsheetFile(pathAndFilename);
		List<List<String>> content = readContent2(workbook.getWorksheet(0), IntStream.rangeClosed(1, 3).boxed().collect(Collectors.toList()), true);

		Map<String, Set<String>> unreferencedStuff = new HashMap<>();
		unreferencedStuff.put(Producer.class.getName(), extractUnreferencedEntities(content, 0, producersByName));
		unreferencedStuff.put(BeerColor.class.getName(), extractUnreferencedEntities(content, 1, colorsByName));
		unreferencedStuff.put(BeerStyle.class.getName(), extractUnreferencedEntities(content, 2, stylesByName));
		return unreferencedStuff;
	}

	public void importBarsSelection(String pathAndFilename) throws Xlsx4jException, Docx4JException {
		Bar bottleBar = barsRepository.loadBottledById(667L);
		Set<BottledBeer> bottles = beersRepository.findAll().stream()
			.filter(beer -> beer.getBottle() != null)
			.map(Beer::getBottle)
			.collect(Collectors.toSet());
		
		bottleBar.setBottledBeer(bottles);
		barsRepository.save(bottleBar);
		
//		Map<String, Bar> barsByName = mapEntitiesByName(barsRepository.findAll());
//		List<Beer> beers = beersRepository.findAllWithServices();
//		
//		Map<Long, TapBeer> tapById = beers.stream()
//			.filter(beer -> beer.getTap() != null)
//			.map(Beer::getTap)
//			.collect(Collectors.toMap(TapBeer::getId, tap -> tap));
//		Map<Long, BottledBeer> bottledById = beers.stream()
//				.filter(beer -> beer.getBottle() != null)
//				.map(Beer::getBottle)
//				.collect(Collectors.toMap(BottledBeer::getId, bottle -> bottle));
//		
//		WorkbookPart workbook = openSpreadsheetFile(pathAndFilename);
//		List<List<String>> tapContent = readContent2(workbook.getWorksheet(0), IntStream.rangeClosed(0, 7).boxed().collect(Collectors.toList()));
//		List<List<String>> bottledContent = readContent2(workbook.getWorksheet(1), IntStream.rangeClosed(0, 7).boxed().collect(Collectors.toList()));
//		
//		List<String> tapTitles = tapContent.get(0).subList(3, tapContent.size()-1);
//		tapContent.remove(0);
//		List<String> bottledTitles = tapContent.get(0).subList(3, bottledContent.size()-1);
//		bottledContent.remove(0);
//		List<Bar> sortedBars = tapTitles.stream()
//			.map(barName -> barsByName.get(barName))
//			.collect(Collectors.toList());
//		
//		Map<TapBeer, List<String>> lineByTap = tapContent.stream()
//			.filter(line -> tapById.get(Long.valueOf(line.get(0))) != null)
//			.collect(Collectors.toMap(
//						line -> tapById.get(Long.valueOf(line.get(0))), 
//						line -> line.subList(3, line.size()-1))
//					);
		
//		lineByTap.entrySet().stream()
//			.forEach(entry -> );
			
		
		//TODO Complete method, doing inserts manually for now
	}
	
	public Map<String, Set<String>> importUnreferencedContentFromStandardImporter(String pathAndFilename) throws Docx4JException, Xlsx4jException {
		Map<String, Set<String>> stuff = extractUnreferencedContentFromStandardImporter(pathAndFilename);
		
		Set<BeerColor> createdColors = stuff.get(BeerColor.class.getName()).stream()
			.map(colorName -> colorsRepository.save(new BeerColor(colorName)))
			.collect(Collectors.toSet());
		
		Set<BeerStyle> createdStyles = stuff.get(BeerStyle.class.getName()).stream()
				.map(styleName -> stylesRepository.save(new BeerStyle(styleName)))
				.collect(Collectors.toSet());
		
		Set<Producer> createdProducer = stuff.get(Producer.class.getName()).stream()
				.map(producerName -> producerRepository.save(new Producer(producerName)))
				.collect(Collectors.toSet());
		
		Map<String, Set<String>> result = new HashMap<>();
		result.put(BeerColor.class.getName(), createdColors.stream()
				.map(BeerColor::getName)
				.collect(Collectors.toSet()));
		result.put(BeerStyle.class.getName(), createdStyles.stream()
				.map(BeerStyle::getName)
				.collect(Collectors.toSet()));
		result.put(Producer.class.getName(), createdProducer.stream()
				.map(Producer::getName)
				.collect(Collectors.toSet()));
		return result;
	}

}

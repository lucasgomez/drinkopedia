package ch.lgo.drinks.simple.service;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xlsx4j.sml.STHorizontalAlignment;

import com.jumbletree.docx5j.xlsx.XLSXFile;
import com.jumbletree.docx5j.xlsx.builders.CellBuilder;
import com.jumbletree.docx5j.xlsx.builders.RowBuilder;
import com.jumbletree.docx5j.xlsx.builders.WorksheetBuilder;

import ch.lgo.drinks.simple.dao.DescriptiveLabel;
import ch.lgo.drinks.simple.dto.BottledBeerDetailedDto;
import ch.lgo.drinks.simple.dto.DetailedBeerDto;
import ch.lgo.drinks.simple.dto.TapBeerDetailedDto;
import ch.lgo.drinks.simple.entity.Bar;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.BeerColor;
import ch.lgo.drinks.simple.entity.BeerStyle;
import ch.lgo.drinks.simple.entity.BottledBeer;
import ch.lgo.drinks.simple.entity.HasId;
import ch.lgo.drinks.simple.entity.Place;
import ch.lgo.drinks.simple.entity.Producer;
import ch.lgo.drinks.simple.entity.StrengthEnum;
import ch.lgo.drinks.simple.entity.TapBeer;

@Service
public class XlsxOutputService extends AbstractDocx5JHelper {

	@Autowired
	private ModelMapper modelMapper;
	
	private static final String ALC_CL_PRICE_FORMULA = "=%s/(%s*%s/100)";
	private static final double FIRST_ROW_HEIGHT = 22.28;
	private static final double SECOND_ROW_HEIGHT = 15.27;
	private static final String BEER_STYLE_NAME = "Beername";
	private static final String PRICE_STYLE_NAME = "Price";
	private static final String INFOS_STYLE_NAME = "Infos";
	private static final String BREWERY_STYLE_NAME = "Brewery";
	private static final String VOLUME_STYLE_NAME = "Volume";
	private static final String TAP_VOLUME_STYLE_NAME = "TapVolume";
	private static final String DESCRIPTION_STYLE_NAME = "Description";
	private static final String FONT_NAME = "Calibri";
	private static final String EXTENSION = ".xlsx";
	private static final double TAP_BEER_RATIO = 3.0;
	private static final double BOTTLED_BEER_RATIO = 2.3;
	private static final Double STOOPID_FACTOR = 4.8565;
	private static final DecimalFormat PRICES_CALCULATION_FORMAT = new DecimalFormat("#0.000");
	private static final DecimalFormat PRICES_DISPLAY_FORMAT = new DecimalFormat("#0.0");


	public File outputBeerByBarsImporter(Collection<Beer> beers, Collection<Bar> bars, String path, String baseFileName) throws InvalidFormatException, IOException, Docx4JException, JAXBException, Exception {
		Comparator<Beer> byProducer = Comparator.comparing(Beer::getProducer, Comparator.nullsLast(Comparator.naturalOrder()));

		List<String> titles = Arrays.asList("id", "Producer", "Name");
		List<String> barsNames = bars.stream().map(bar -> bar.getName()).collect(Collectors.toList());
		barsNames.addAll(0, titles);

		return new DocumentBuilder()
				.appendSheet2("Tap", barsNames,
					beers.stream()
						.filter(beer -> beer.getTap() != null)
						.sorted(byProducer)
						.map(beer -> Arrays.asList(
								nullableToString(beer.getTap().getId()),
								displayName(beer.getProducer()),
								beer.getName()
								))
						.collect(Collectors.toList()))
				.appendSheet2("Bottle", barsNames,
						beers.stream()
						.filter(beer -> beer.getBottle() != null)
						.sorted(byProducer)
						.map(beer -> Arrays.asList(
								nullableToString(beer.getBottle().getId()),
								displayName(beer.getProducer()),
								beer.getName()
								))
						.collect(Collectors.toList()))
				.save(buildFullName(path, baseFileName, EXTENSION));
	}
	
	public File theFullMonty(List<Beer> allBeers, String path, String baseFileName) throws Exception {
		Set<BeerColor> colors = allBeers.stream()
			.map(beer -> beer.getColor())
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
		
		Set<BeerStyle> styles = allBeers.stream()
			.map(beer -> beer.getStyle())
			.filter(Objects::nonNull)
			.collect(Collectors.toSet());
		
		Set<Producer> producers = allBeers.stream()
				.map(beer -> beer.getProducer())
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		
		Set<Place> places = producers.stream()
				.map(producer -> producer.getOrigin())
				.filter(Objects::nonNull)
				.collect(Collectors.toSet());
		
		return new DocumentBuilder()
				.appendSheet2("Colors", Arrays.asList("Id", "Color"),
						colors.stream()
						.map(color -> writeIdAndName(color))
						.collect(Collectors.toList()))
				.appendSheet2("Styles", Arrays.asList("Id", "Style"),
						styles.stream()
						.map(style -> writeIdAndName(style))
						.collect(Collectors.toList()))
				.appendSheet2("Places", Arrays.asList("Id", "Name", "Shortname"),
						places.stream()
						.map(place -> Arrays.asList(nullableToString(place.getId()), place.getName(), place.getShortName()))
						.collect(Collectors.toList()))
				.appendSheet2("Producers", Arrays.asList("Id", "Name", "Origin id", "Origin"),
						producers.stream()
						.map(producer -> Arrays.asList(
								nullableToString(producer.getId()),
								producer.getName(), 
								displayNullableToString(producer.getId()),
								displayName(producer.getOrigin())))
						.collect(Collectors.toList()))
				.appendSheet2("Beers", Arrays.asList("Id", "External Id", "Name",
				        "ProducerId", "Producer", "ColorId", "Color", "StyleId", "Style", 
						"ABV", "Hopping", "Bitterness", "Sourness", "Sweetness", "Comment"),
						allBeers.stream()
						.map(beer -> Arrays.asList(
								displayId(beer),
						        beer.getExternalId(),
								beer.getName(),
								displayId(beer.getProducer()),
								displayName(beer.getProducer()),
								displayId(beer.getColor()),
								displayName(beer.getColor()),
								displayId(beer.getStyle()),
								displayName(beer.getStyle()),
								formatForDisplay(beer.getAbv()),
								displayStrength(beer.getHopping()),
								displayStrength(beer.getBitterness()),
								displayStrength(beer.getSourness()),
								displayStrength(beer.getSweetness()),
								beer.getComment()
							))
						.collect(Collectors.toList()))
				.appendSheet2("Tap", Arrays.asList("Beer", "Buying price / L", "Price "+TapBeer.VOLUME_SMALL_CL, "Price "+TapBeer.VOLUME_BIG_CL),
						allBeers.stream()
						.map(beer -> beer.getTap())
						.filter(Objects::nonNull)
						.map(tap -> Arrays.asList(
								tap.getBeer().getName(),
								formatForDisplay(tap.getBuyingPricePerLiter()),
								formatForDisplay(tap.getPriceSmall()),
								formatForDisplay(tap.getPriceBig())
							))
						.collect(Collectors.toList()))
				.appendSheet2("Bottle", Arrays.asList("Beer", "Buying price", "Price", "Volume (cl)"),
						allBeers.stream()
						.map(beer -> beer.getBottle())
						.filter(Objects::nonNull)
						.map(bottle -> Arrays.asList(
								bottle.getBeer().getName(),
								formatForDisplay(bottle.getBuyingPrice()),
								formatForDisplay(bottle.getSellingPrice()),
								displayNullableToString(bottle.getVolumeInCl())
							))
						.collect(Collectors.toList())
						)
				.save(buildFullName(path, baseFileName, EXTENSION));
	}
	
	private String displayNullableToString(Object object) {
		if (object != null)
			return object.toString();
		else
			return "";
	}
	
	private String displayId(HasId entity) {
	    if (entity != null && entity.getId() != null)
	        return entity.getId().toString();
	    else
	        return "";
	}

	private String displayName(DescriptiveLabel entity) {
		if (entity == null)
			return "";
		return entity.getName();
			
	}
	
	private String displayStrength(StrengthEnum strength) {
		if (strength == null)
			return "";
		return strength.toString();
	}

	//TODO Separate beer related processing with output itself
	public File outputBeersPricesWithDetails(List<Beer> beers, String path, String baseFileName) throws Exception {
		List<Beer> tapBeers = beers.stream()
				.filter(beer -> beer.getTap() != null)
				.collect(Collectors.toList());
		List<Beer> bottledBeers = beers.stream()
				.filter(beer -> beer.getBottle() != null)
				.collect(Collectors.toList());
		

		//Start at 1 due to title line
		List<List<String>> tapContent = IntStream.range(0, tapBeers.size())
				.mapToObj(tapBeerId -> tapBeerToTupple(tapBeers.get(tapBeerId), tapBeerId+1))
				.collect(Collectors.toList());
		List<List<String>> bottleContent = IntStream.range(0, bottledBeers.size())
				.mapToObj(bottledBeerId -> bottledBeerToTupple(bottledBeers.get(bottledBeerId), bottledBeerId+1))
				.collect(Collectors.toList());

		List<String> tapTitles = Arrays.asList("Id","ExternalId","Name","Style","Color","Abv","BuyingPricePerLiter","PriceBig","PriceSmall",
				"Ideal selling price big", "Ideal selling price small", "Price per alc Cl big", "Price per alc Cl small");
		List<String> bottlesTitles = Arrays.asList("Id","ExternalId","Name","Style","Color","Abv","Volume (cl)","Buying Price",
				"Selling price", "Ideal selling price", "Price per alc Cl");
	
		return new DocumentBuilder()
				.appendSheet2("Tap calc", tapTitles, tapContent)
				.appendSheet2("Bottles calc", bottlesTitles, bottleContent)
				.save(buildFullName(path, baseFileName, EXTENSION));
	}
	
	public File outputContent(List<List<String>> content, String path, String baseFileName) throws Exception {
		return new DocumentBuilder()
				.appendSheet2("Data", Collections.singletonList("content"), content)
				.save(buildFullName(path, baseFileName, EXTENSION));
	}

	/**
	 * [0] Id
	 * [1] ExternalId
	 * [2] Name
	 * [3] Style
	 * [4] Color
	 * [5] Abv
	 * [6] Volume (cl)
	 * [7] Buying Price
	 * [8] Selling price
	 * [9] Ideal selling price
	 * [10] Price per alc Cl
	 * @param bottledBeer
	 * @param rowNum
	 * @return
	 */
	private List<String> bottledBeerToTupple(Beer bottledBeer, int rowNum) {
		//Add fields from tapBeer
		List<String> result = new ArrayList<>();
		result.add(bottledBeer.getId().toString());
		result.add(bottledBeer.getExternalId());
		result.add(bottledBeer.getName());
		result.add(displayName(bottledBeer.getStyle()));
		result.add(displayName(bottledBeer.getColor()));
		result.add(formatForDisplay(bottledBeer.getAbv())); // [5] 
		
		BottledBeer bottle = bottledBeer.getBottle();
		result.add(bottledBeer.getBottle() != null && bottle.getVolumeInCl() != null
				? bottle.getVolumeInCl().toString() : ""); // [6]
		result.add(formatForCalcul(bottle.getBuyingPrice())); // [7]
		result.add(formatForDisplay(bottle.getSellingPrice())); // [8]

		//Add calculated price
		String idealSellingPrice = formatForCalcul(bottle.getBuyingPrice() * BOTTLED_BEER_RATIO);
		result.add(idealSellingPrice); // [9]
		
		//Add formula to calculate price / cl of alcohol
		String abvCellAdress = buildCellAdress(5, rowNum);
		String volumeCellAdress = buildCellAdress(6, rowNum);
		String priceCellAdress = buildCellAdress(8, rowNum);
		// Volume of Alc = Volume * ABV / 100
		// Price per Cl of alc = price / volume of alc
		String alcoholPriceFormulaBig = String.format(ALC_CL_PRICE_FORMULA, priceCellAdress, volumeCellAdress, abvCellAdress);
		
		result.add(alcoholPriceFormulaBig); // [9]
		
		return result;
	}

	private String formatForDisplay(Double price) {
		if (price != null)
			return PRICES_DISPLAY_FORMAT.format(price).replace(".", ",");
		else
			return "";
	}
	
	private String formatForCalcul(Double price) {
		if (price != null)
			return PRICES_CALCULATION_FORMAT.format(price).replace(".", ",");
		else
			return "";
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
		result.add(formatForDisplay(tapBeer.getAbv()));
		result.add(formatForCalcul(tapBeer.getTap().getBuyingPricePerLiter()));
		result.add(formatForDisplay(tapBeer.getTap().getPriceBig()));
		result.add(formatForDisplay(tapBeer.getTap().getPriceSmall()));

		//Add calculated price
		Double pricePerCl = tapBeer.getTap().getBuyingPricePerLiter() / 100;
		String idealSellingPriceBig = PRICES_CALCULATION_FORMAT.format(pricePerCl * TapBeer.VOLUME_BIG_CL * TAP_BEER_RATIO);
		String idealSellingPriceSmall = PRICES_CALCULATION_FORMAT.format(pricePerCl * TapBeer.VOLUME_SMALL_CL * TAP_BEER_RATIO);
		result.add(idealSellingPriceBig); // [9]
		result.add(idealSellingPriceSmall); // [10]
		
		//Add formula to calculate price / cl of alcohol
		String cellAdressAbv = buildCellAdress(5, rowNum);
		String cellAdressPriceBig = buildCellAdress(7, rowNum);
		String cellAdressPriceSmall = buildCellAdress(8, rowNum);
		// Volume of Alc = Volume * ABV / 100
		// Price per Cl of alc = price / volume of alc
		String alcoholPriceFormulaBig = String.format(ALC_CL_PRICE_FORMULA, cellAdressPriceBig, TapBeer.VOLUME_BIG_CL, cellAdressAbv);
		String alcoholPriceFormulaSmall = String.format(ALC_CL_PRICE_FORMULA, cellAdressPriceSmall, TapBeer.VOLUME_SMALL_CL, cellAdressAbv);
		
		result.add(alcoholPriceFormulaBig); // [11]
		result.add(alcoholPriceFormulaSmall); // [12]
		
		return result;
	}

	public File outputBeerStylesAndColors(List<String> styleNames, List<String> colorNames, String path, String baseFileName) throws Exception {
		return new DocumentBuilder()
				.appendSheet("Styles", "Style", styleNames)
				.appendSheet("Colors", "Color", colorNames)
				.save(buildFullName(path, baseFileName, EXTENSION));
	}
	
	public File outputBeersAndCode(Map<String, List<String>> beersAndCode, String path, String baseFileName) throws InvalidFormatException, IOException, Docx4JException, JAXBException, Exception {
		return new DocumentBuilder()
				.appendSheet("Styles", beersAndCode)
				.save(buildFullName(path, baseFileName, EXTENSION));
	}
	
	public File outputBottledBarPricesLists(Bar bottledBar, String path, String baseFileName) throws Exception {
		List<BottledBeerDetailedDto> list = bottledBar.getBottledBeer().stream()
			.sorted(BottledBeer.byName())
			.sorted(BottledBeer.byPrice())
			.map(bottle -> bottle.getBeer())
			.map(beer -> modelMapper.map(beer, BottledBeerDetailedDto.class))
			.collect(Collectors.toList());
		
		XLSXFile file = new XLSXFile();
		
		createStyles(file);
		insertBottledPricesList(list, file.getWorkbookBuilder().getSheet(0), false);
		insertBottledPricesList(list, file.getWorkbookBuilder().appendSheet(), true);
		
		File out = new File(buildFullName(path, "bottle - "+baseFileName, EXTENSION));
		file.save(out);
		return out;
	}
	
	public File outputBottledDetailedMultiSortedAlphaPlusPlus(Bar bottledBar, String path, String baseFileName) throws Exception {
		Map<String, List<BottledBeer>> beersByStyle = bottledBar.getBottledBeer().stream()
				.sorted(BottledBeer.byName())
				.sorted(BottledBeer.byPrice())
				.sorted(BottledBeer.byStyle())
				.collect(Collectors.groupingBy(bottle -> bottle.getBeer().getStyle().getName()));
		
		Map<String, List<BottledBeer>> beersByColor = bottledBar.getBottledBeer().stream()
				.sorted(BottledBeer.byName())
				.sorted(BottledBeer.byPrice())
				.sorted(BottledBeer.byColor())
				.collect(Collectors.groupingBy(bottle -> bottle.getBeer().getColor().getName()));
		
		Map<String, List<BottledBeer>> beersByProducer = bottledBar.getBottledBeer().stream()
				.sorted(BottledBeer.byName())
				.sorted(BottledBeer.byPrice())
				.sorted(BottledBeer.byProducer())
				.collect(Collectors.groupingBy(bottle -> bottle.getBeer().getProducer().getName()));
			
//		beersByStyle.entrySet().stream()
//			.map(entry -> new Entry<String, List<List<String>>>(entry.getKey(),
//						entry.getValue().stream()
//							.map(bottle -> Arrays.asList(
//									bottle.getBeer().getProducer().getName()
//									+ " - " + bottle.getBeer().getName()
//									+ " (" + bottle.getBeer().getProducer().getOrigin().getShortName() + ")",
//									displayVolume(bottle.getVolumeInCl()) + " " + displayABV(bottle.getBeer().getAbv()),
//									displayPrice(bottle.getSellingPrice())
//									))
//							.collect(Collectors.toList())
//							)
////			.collect(Collectors.toList())
//			;
		//TODO Restore list
		
		List<List<String>> byStyle = bottledBar.getBottledBeer().stream()
			.sorted(BottledBeer.byName())
			.sorted(BottledBeer.byPrice())
			.sorted(BottledBeer.byStyle())
			.map(bottle -> bottleToList(bottle))
			.collect(Collectors.toList());
		List<List<String>> byName = bottledBar.getBottledBeer().stream()
				.sorted(BottledBeer.byName())
				.map(bottle -> bottleToList(bottle))
				.collect(Collectors.toList());
		List<List<String>> byProducer = bottledBar.getBottledBeer().stream()
				.sorted(BottledBeer.byName())
				.sorted(BottledBeer.byProducer())
				.map(bottle -> bottleToList(bottle))
				.collect(Collectors.toList());
		
		List<String> titles = Arrays.asList("Brewer", "Beer", "Style / color", "Details", "Price");
		return new DocumentBuilder()
				.appendSheet2("By style", titles, byStyle)
				.appendSheet2("By name", titles, byName)
				.appendSheet2("By producer", titles, byProducer)
				.save(buildFullName(path, baseFileName, EXTENSION));
	}

	private List<String> bottleToList(BottledBeer bottle) {
		return Arrays.asList(
				bottle.getBeer().getProducer().getName()
				+ " (" + bottle.getBeer().getProducer().getOrigin().getShortName() + ")",
				bottle.getBeer().getName(),
				bottle.getBeer().getStyle().getName() + " / " + bottle.getBeer().getColor().getName(),
				displayVolume(bottle.getVolumeInCl()) + " " + displayABV(bottle.getBeer().getAbv()),
				displayPrice(bottle.getSellingPrice())
				);
	}
	
	public File outputTapBarPricesLists(Bar tapBar, String path, String baseFileName) throws Exception {
		List<TapBeerDetailedDto> list = tapBar.getTapBeers().stream()
				.sorted(TapBeer.byName())
				.sorted(TapBeer.byPrice())
				.map(tap -> tap.getBeer())
				.map(beer -> modelMapper.map(beer, TapBeerDetailedDto.class))
				.collect(Collectors.toList());
		
		XLSXFile file = new XLSXFile();
		
		createStyles(file);
		insertTapPricesList(list, file.getWorkbookBuilder().getSheet(0), false);
		insertTapPricesList(list, file.getWorkbookBuilder().appendSheet(), true);
		
		File out = new File(buildFullName(path, "tap - "+baseFileName, EXTENSION));
		file.save(out);
		return out;
	}
	
	public void insertStrings(List<String> content, WorksheetBuilder sheet) throws Exception {
		sheet.setName("BigList");
		for (String value : content) {
			addContent(sheet, value);
		}
	}
	
	public void insertBottledPricesList(List<BottledBeerDetailedDto> list, WorksheetBuilder sheet, boolean withComment) throws Exception {
		if (withComment)
			sheet.setName("DetailedList");
		else
			sheet.setName("BigList");
			
		
		prepareBottlesColumnsWidth(sheet);
		
		for (BottledBeerDetailedDto beer : list) {
			addBottledBeerLines(sheet, beer);
			if (withComment)
				addBeerCommentLines(sheet, beer);
		}
	}
	
	public void insertTapPricesList(List<TapBeerDetailedDto> list, WorksheetBuilder sheet, boolean withComment) throws Exception {
		if (withComment)
			sheet.setName("DetailedList");
		else
			sheet.setName("BigList");
		
		prepareTapColumnsWidth(sheet);
		
		for (TapBeerDetailedDto beer : list) {
			addTapBeerLines(sheet, beer);
			if (withComment)
				addBeerCommentLines(sheet, beer);
		}
	}

	private void prepareBottlesColumnsWidth(WorksheetBuilder sheet) throws Docx4JException {
		List<Double> widths = Arrays.asList(0.5, 7.0, 4.0, 1.7, 1.4, 2.3);
		
		for (Double width : widths) {
			sheet.addColumnDefinition(width*STOOPID_FACTOR, false);
		}
	}
	
	private void prepareTapColumnsWidth(WorksheetBuilder sheet) throws Docx4JException {
		List<Double> widths = Arrays.asList(0.5, 5.9, 4.5, 1.3, 2.3, 2.1);
		
		for (Double width : widths) {
			sheet.addColumnDefinition(width*STOOPID_FACTOR, false);
		}
	}

	private void createStyles(XLSXFile file) {
		createStyle(BEER_STYLE_NAME, file, 18, true);
		createStyle(INFOS_STYLE_NAME, file, 12, false);
		createStyle(BREWERY_STYLE_NAME, file, 12, true);
		createStyle(VOLUME_STYLE_NAME, file, 14, false, STHorizontalAlignment.RIGHT);
		createStyle(TAP_VOLUME_STYLE_NAME, file, 11, false, STHorizontalAlignment.RIGHT);
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
	
	private void addBottledBeerLines(WorksheetBuilder sheet, DetailedBeerDto beer) throws Docx4JException {
		
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
					.value(displayVolume(beer.getBottleVolumeInCl()))
					.row()
				.nextCell()
					.style(VOLUME_STYLE_NAME)
					.value(displayABV(beer))
					.row()
				.nextCell()
					.style(PRICE_STYLE_NAME)
					.value(displayPrice(beer.getBottleSellingPrice()))
					.row()
				.sheet()
			.nextRow()
				.height(SECOND_ROW_HEIGHT, true)
				.nextCell()
					.row()
				.nextCell()
					.style(BREWERY_STYLE_NAME) //TODO Format & merge cells
					.value(String.format("%s (%s)", beer.getProducerName(), beer.getProducerOriginName()))
					.row()
				.nextCell()
					.style(INFOS_STYLE_NAME)
					.value(String.format("%s - %s", beer.getStyleName(), beer.getColorName()));
	}
	
	private void addTapBeerLines(WorksheetBuilder sheet, TapBeerDetailedDto beer) throws Docx4JException {
		
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
					.value(displayABV(beer))
					.row()
				.nextCell()
					.style(PRICE_STYLE_NAME)
					.value(displayPrice(beer.getTapPriceSmall()))
					.row()
				.nextCell()
					.style(PRICE_STYLE_NAME)
					.value(displayPrice(beer.getTapPriceBig()))
					.row()
				.sheet()
			.nextRow()
				.height(SECOND_ROW_HEIGHT, true)
				.nextCell()
					.row()
				.nextCell()
					.style(BREWERY_STYLE_NAME) //TODO Format & merge cells
					.value(String.format("%s (%s)", beer.getProducerName(), beer.getProducerOriginName()))
					.row()
				.nextCell()
					.style(INFOS_STYLE_NAME)
					.value(String.format("%s - %s", beer.getStyleName(), beer.getColorName()))
					.row()
				.nextCell()
					.row()
				.nextCell()
					.style(TAP_VOLUME_STYLE_NAME)
					.value(displayVolume(TapBeer.VOLUME_SMALL_CL))
					.row()
				.nextCell()
					.style(TAP_VOLUME_STYLE_NAME)
					.value(displayVolume(TapBeer.VOLUME_BIG_CL));
	}
	
	private void addBeerCommentLines(WorksheetBuilder sheet, DetailedBeerDto beer) throws Docx4JException {
		//Add details
		if (StringUtils.isNotBlank(beer.getComment())) {
			RowBuilder row = sheet.nextRow().height(SECOND_ROW_HEIGHT);
			CellBuilder firstCell = row
					.nextCell().row()
					.nextCell()
					.style(DESCRIPTION_STYLE_NAME)
					.value(beer.getComment());
//			CellBuilder lastCell = firstCell.row().nextCell().row().nextCell().row().nextCell();
		}
		
		//TODO Merge 4 cells
		//TODO Autoheight
	}
	
	private String displayPrice(Double price) {
		if (price != null)
			return PRICES_DISPLAY_FORMAT.format(price) + ".-";
		else
			return "";
	}

	private String displayVolume(Long volume) {
		if (volume != null)
			return String.format("%scl", volume);
		else
			return "";
	}

	private String displayABV(DetailedBeerDto beer) {
		if (beer.getAbv() != null)
			return PRICES_DISPLAY_FORMAT.format(beer.getAbv())+"%";
		else
			return "";
	}

	private String displayABV(Double abv) {
		if (abv != null)
			return PRICES_DISPLAY_FORMAT.format(abv)+"%";
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

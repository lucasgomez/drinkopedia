package ch.lgo.drinks.simple.service;

import java.text.DecimalFormat;
import java.util.List;

import org.odftoolkit.odfdom.doc.OdfTextDocument;
import org.odftoolkit.odfdom.dom.style.OdfStyleFamily;
import org.odftoolkit.odfdom.dom.style.props.OdfTextProperties;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.incubator.doc.style.OdfStyle;
import org.odftoolkit.simple.TextDocument;
import org.odftoolkit.simple.text.Paragraph;
import org.springframework.stereotype.Service;

import ch.lgo.drinks.simple.dto.BeerDTO;

@Service
public class OdtOutputService {

	private static final String TAB = "\t";
	private static final String BEER_STYLE_NAME = "BeerName";
	private static final String INFOS_STYLE_NAME = "Infos";
	private static final String BREWERY_STYLE_NAME = "Brewery";
	private static final String VOLUME_STYLE_NAME = "Volume";

	public void outputBottledBeer(BeerDTO beer, String filename) throws Exception {
		OdfTextDocument document = OdfTextDocument.newTextDocument();
		document.addText(beer.getName());
		document.save(filename);
	}

	public void outputTapBeers(List<BeerDTO> beersList, String filename) throws Exception {
		TextDocument document = TextDocument.newTextDocument();
		
		addDefaultStyles(document.getOrCreateDocumentStyles());
		
		beersList.forEach(beer -> addBeerLine(beer, document));
		document.save(filename);
	}

	private void addBeerLine(BeerDTO beer, TextDocument document) throws MuEx {
		try {
			/*
			 * E.g. : Trooper Red 'N' Black -> 5.2% -> 6.- Robinson Brewery,
			 * Lager, Blonde, DE
			 */
			Paragraph paragraph = document.addParagraph(null);
			document.newParagraph()
					.addStyledContent(BEER_STYLE_NAME, beer.getName() + TAB)
					.addStyledContent(VOLUME_STYLE_NAME, displayABV(beer.getAbv()) + TAB)
					.addStyledContent(INFOS_STYLE_NAME, displayPrice(3.5));
			document.newParagraph()
					.addStyledContent(BREWERY_STYLE_NAME, beer.getProducerName()).addContent(" (" + beer.getProducerOriginShortName() + ") ");
		} catch (Exception e) {
			throw new MuEx(e);
		}
	}

	private String displayPrice(double price) {
		DecimalFormat format = new DecimalFormat("#.##");
		return format.format(price) + " .-";
	}

	private String displayABV(double abv) {
		DecimalFormat format = new DecimalFormat("#.#%");
		return format.format(abv);
	}

	private void addDefaultStyles(OdfOfficeStyles styles) {
		OdfStyle beerNameStyle = styles.newStyle(BEER_STYLE_NAME, OdfStyleFamily.Paragraph);
		beerNameStyle.setStyleDisplayNameAttribute(BEER_STYLE_NAME);
		beerNameStyle.setProperty(OdfTextProperties.FontSize, String.valueOf(16));
		beerNameStyle.setProperty(OdfTextProperties.FontWeight, "bold");

		OdfStyle volumeStyle = styles.newStyle(VOLUME_STYLE_NAME, OdfStyleFamily.Paragraph);
		volumeStyle.setStyleDisplayNameAttribute(VOLUME_STYLE_NAME);
		volumeStyle.setProperty(OdfTextProperties.FontSize, String.valueOf(14));
		volumeStyle.setProperty(OdfTextProperties.FontWeight, "normal");

		OdfStyle breweryStyle = styles.newStyle(BREWERY_STYLE_NAME, OdfStyleFamily.Paragraph);
		breweryStyle.setStyleDisplayNameAttribute(BREWERY_STYLE_NAME);
		breweryStyle.setProperty(OdfTextProperties.FontSize, String.valueOf(12));
		breweryStyle.setProperty(OdfTextProperties.FontWeight, "bold");

		OdfStyle infosStyle = styles.newStyle(INFOS_STYLE_NAME, OdfStyleFamily.Paragraph);
		infosStyle.setStyleDisplayNameAttribute(INFOS_STYLE_NAME);
		infosStyle.setProperty(OdfTextProperties.FontSize, String.valueOf(12));
		infosStyle.setProperty(OdfTextProperties.FontWeight, "normal");
	}

	private class MuEx extends RuntimeException {
		private Exception originalException;

		public Exception getOriginalException() {
			return originalException;
		}

		public MuEx(Exception e) {
			originalException = e;
		}
	}
}

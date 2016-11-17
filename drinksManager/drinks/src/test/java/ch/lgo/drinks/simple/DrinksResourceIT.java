package ch.lgo.drinks.simple;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import ch.lgo.drinks.simple.dao.IDrinkRepository;
import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DrinksResourceIT {

	@LocalServerPort
	private int port;

	private URL base;
	private URL resource;

	@Autowired
	private TestRestTemplate template;
	
	@Autowired
	private IDrinkRepository drinkRepository;

	@Before
	public void setUp() throws Exception {
		base = new URL("http://localhost:" + port + "/");
		resource = new URL(base.toString() + "drinks/");
		
		drinkRepository.deleteAll();
	}

	@Test
	public void getAllReturnsNoContent() throws Exception {
		ResponseEntity<String> response = template
				.getForEntity(resource.toString(), String.class);
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
	}

	@Test
	public void getAllReturnsContentWhenInitialized() {
		DrinkDTO createdDrink = new DrinkDTO();
		createdDrink.setName("Dianemayte");
		createdDrink.setProducerName("ABO");
		URI postedDrinkUri = template.postForLocation(resource.toString(),
				createdDrink);

		ResponseEntity<DrinkDTO> responseGetOneUri = template
				.getForEntity(postedDrinkUri, DrinkDTO.class);
		assertThat(responseGetOneUri.getStatusCode(), equalTo(HttpStatus.OK));
		assertEquals("Drink name", createdDrink.getName(), responseGetOneUri.getBody().getName());
		assertEquals("Producer name", createdDrink.getProducerName(), responseGetOneUri.getBody().getProducerName());
		
		ResponseEntity<DrinksDTOList> responseGetAll = template
				.getForEntity(resource.toString(), DrinksDTOList.class);
		assertThat(responseGetAll.getStatusCode(), equalTo(HttpStatus.OK));
		assertEquals("1 drink only", 1, responseGetAll.getBody().getDrinks().size());
	}
}
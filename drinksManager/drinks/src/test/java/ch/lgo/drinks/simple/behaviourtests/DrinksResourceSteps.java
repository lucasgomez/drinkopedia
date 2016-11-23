package ch.lgo.drinks.simple.behaviourtests;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RequestCallback;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.lgo.drinks.simple.dao.IDrinkRepository;
import ch.lgo.drinks.simple.dao.IDrinkTypeRepository;
import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.DrinkTypeDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;
import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.entity.DrinkType;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
public class DrinksResourceSteps {

	@LocalServerPort
	private int port;
	private URL base;
	private URL resource;
	@Autowired
	private TestRestTemplate template;
	@Autowired
	private IDrinkRepository drinkRepository;
	@Autowired
	private IDrinkTypeRepository drinkTypeRepository;
    
	private ResponseEntity<DrinksDTOList> responseToList;
	private ResponseEntity<DrinkDTO> responseToSingle;
	private Long drinkId;

    @Given("^the repository is empty$")
    public void repositoryIsEmpty() {
    }

    @Given("^there are some drink types$")
    public void drinkTypesExist() {
        createAndSaveDrinkType("beer");
        createAndSaveDrinkType("soda");
    }
    
    @Given("^a drink repository with sample drinks$")
    public void repositoryHasContent() throws MalformedURLException {
    	DrinkType beerType = createAndSaveDrinkType("beer");
    	DrinkType sodaType = createAndSaveDrinkType("soda");
    	
    	createAndSaveDrink("Dianemayte", "ABO", beerType);
    	createAndSaveDrink("Marz'Ale", "FdB", beerType);
    	createAndSaveDrink("Blurg's Cola", "Aldebaran Beverages", sodaType);
    	createAndSaveDrink("Big F ale", "Failure Brew", beerType);
    }
    
    @When("^I load all drinks$")
    public void loadAllDrinks() {
		responseToList = template.getForEntity(resource.toString(), DrinksDTOList.class);
    }
    
    @When("^a drink name is updated$")
    public void loadAllDrinksPickOneThenUpdateItsName() {
		responseToList = template.getForEntity(resource.toString(), DrinksDTOList.class);
		DrinksDTOList drinks = responseToList.getBody();
		DrinkDTO drinkToUpdate = drinks.getDrinks().get(0);
		
		drinkToUpdate.setName(drinkToUpdate.getName() + " reloaded");
		drinkId = drinkToUpdate.getId();
		template.execute(resource.toString()+drinkToUpdate.getId(), HttpMethod.PUT, requestCallback(drinkToUpdate), clientHttpResponse -> null);
    }
    
    @When("^I post a new drink$")
    public void postNewDrink() {
    	DrinkTypeDTO drinkType = new DrinkTypeDTO(drinkTypeRepository.loadByName("beer"));
    	
    	DrinkDTO drinkToCreate = new DrinkDTO();
    	drinkToCreate.setName("Dianemayte");
    	drinkToCreate.setProducerName("ABO");
    	drinkToCreate.setType(drinkType);
    	
    	responseToSingle = template.postForEntity(resource.toString(), drinkToCreate, DrinkDTO.class);
    }
    
    @When("^load the aforesaid drink at its location$")
    public void getDrinkLocation() {
    	URI postedDrinkLocation = responseToSingle.getHeaders().getLocation();
    	
    	responseToSingle = template.getForEntity(postedDrinkLocation, DrinkDTO.class);
    }
    
    @When("^the aforesaid drink is loaded$")
    public void getDrinkByItsId() {
    	responseToSingle = template.getForEntity(resource.toString()+drinkId, DrinkDTO.class);
    }
    
    @When("^I delete the first drink$")
    public void deleteADrink() {
    	DrinkDTO drinkToDelete = responseToList.getBody().getDrinks().get(0);
    	
    	template.delete(resource.toString()+drinkToDelete.getId());
    }

    @When("^I search for drinks of type 'beer'$")
    public void searchDrinksByTypeBeer() {
		responseToList = template.getForEntity(resource.toString()+"types/beer", DrinksDTOList.class);
    }
    
    @When("^I search for drinks of type 'hydrazine'$")
    public void searchDrinksByTypeHydra() {
    	responseToList = template.getForEntity(resource.toString()+"types/hydrazine", DrinksDTOList.class);
    }
    
    @When("^I search for drinks with name like 'ale'$")
    public void searchForDrinksByName() {
    	responseToList = template.getForEntity(resource.toString()+"search/ale", DrinksDTOList.class);
    }
    
    @Then("^I get a list of drinks whose name contains the chars 'ale'$")
    public void shouldGetListOfDrinksWhoseNamesContainsAle() {
    	Collection<DrinkDTO> drinks = responseToList.getBody().getDrinks();
    	assertThat(drinks.size(), equalTo(2));
    	for (DrinkDTO drink : drinks) {
			assertThat(drink.getName().toLowerCase(), containsString("ale"));
		}
    }
    
    @Then("^I get all drinks whose property type is 'beer'$")
    public void shouldBeDrinksOfTypeBeer() {
    	Collection<DrinkDTO> drinks = responseToList.getBody().getDrinks();
    	assertThat(drinks.size(), equalTo(3));
    	for (DrinkDTO drink : drinks) {
			assertThat(drink.getType().getName(), equalTo("beer"));
		}
    }
    
    @Then("^the collection of drinks lacks the deleted one$")
    public void shouldLackDeletedDrink() {
    	List<DrinkDTO> drinks = responseToList.getBody().getDrinks();
    	assertThat(drinks.size(), equalTo(3));
    	for (DrinkDTO drink : drinks) {
			assertThat(drink.getName(), not(equalTo("Dianemayte")));
		}
    }
    
    @Then("^it should return code 204$")
    public void shouldReturn204() {
		assertThat(responseToList.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
    }
    
    @Then("^it should return code 200$")
    public void shouldReturn200() {
    	assertThat(responseToList.getStatusCode(), equalTo(HttpStatus.OK));
    }
    
    @Then("^it should return code 201$")
    public void shouldReturn201() {
    	assertThat(responseToSingle.getStatusCode(), equalTo(HttpStatus.CREATED));
    }
    
    @Then("^I get 404 response code$")
    public void shouldReturn404() {
    	assertThat(responseToList.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
    }
    
    @Then("^it should return the created drink$")
    public void shouldReturnCreatedDrink() {
    	DrinkDTO createdDrink = responseToSingle.getBody();
    	assertThat(createdDrink, notNullValue());
    	assertThat(createdDrink.getName(), equalTo("Dianemayte"));
    }
    
    @Then("^that reloaded drink has the new name$")
    public void shouldReturnDrinkWithNewName() {
    	DrinkDTO updatedDrink = responseToSingle.getBody();
    	assertThat(updatedDrink.getName(), equalTo("Dianemayte" + " reloaded"));
    }

	@Before
	public void setUp() throws MalformedURLException {
		base = new URL("http://localhost:" + port + "/");
		resource = new URL(base.toString() + "drinkopedia/api/drinks/");
		
		drinkRepository.deleteAll();
		drinkTypeRepository.deleteAll();
	}
	
	private DrinkType createAndSaveDrinkType(String name) {
		return drinkTypeRepository.save(new DrinkType(name));
	}
	
	private Drink createAndSaveDrink(String name, String producerName, DrinkType type) {
		return drinkRepository.save(new Drink(name, producerName, type));
	}
	
	RequestCallback requestCallback(final DrinkDTO updatedInstance) {
	    return clientHttpRequest -> {
	        ObjectMapper mapper = new ObjectMapper();
	        mapper.writeValue(clientHttpRequest.getBody(), updatedInstance);
	        clientHttpRequest.getHeaders().add(
	          HttpHeaders.CONTENT_TYPE,	MediaType.APPLICATION_JSON_VALUE);
	    };
	}
}

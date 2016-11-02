package ch.lgo.drinks.simple.behaviourtests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import ch.lgo.drinks.simple.dao.DrinkRepository;
import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.entity.Drink;
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
	private DrinkRepository drinkRepository;
    
	private ResponseEntity<String> response;

    @Given("^the repository is empty$")
    public void repositoryIsEmpty() throws MalformedURLException {
    	setUp();
    }
    
    @Given("^the repository has some content$")
    public void repositoryHasContent() throws MalformedURLException {
    	setUp();
    	
    	DrinkDTO createdDrink = new DrinkDTO();
		createdDrink.setName("Dianemayte");
		createdDrink.setProducerName("ABO");
    	
    	drinkRepository.save(new Drink(createdDrink));
    }
    
    @When("^I load all drinks")
    public void loadAllDrinks() {
		response = template.getForEntity(resource.toString(), String.class);
    }
    
    @Then("^it should return code 204$")
    public void shouldeReturn204() {
		assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
    }
    
    @Then("^it should return code 200$")
    public void shouldeReturn200() {
    	assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

	@Before
	public void setUp() throws MalformedURLException {
		base = new URL("http://localhost:" + port + "/");
		resource = new URL(base.toString() + "drinkopedia/api/drinks/");
		
		drinkRepository.deleteAll();
	}
}

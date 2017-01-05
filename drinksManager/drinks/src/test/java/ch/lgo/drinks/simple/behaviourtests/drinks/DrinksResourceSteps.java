package ch.lgo.drinks.simple.behaviourtests.drinks;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RequestCallback;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.lgo.drinks.simple.dao.BeersRepository;
import ch.lgo.drinks.simple.dao.NonAlcoolicBeverageRepository;
import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;
import ch.lgo.drinks.simple.entity.Beer;
import ch.lgo.drinks.simple.entity.DrinkTypeEnum;
import ch.lgo.drinks.simple.entity.NonAlcoolicBeverage;
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
    private BeersRepository beersRepository;
    @Autowired
    private NonAlcoolicBeverageRepository nabsRepository;
    
    @Value("${security.user.name}")
    private String basicUsername;
    @Value("${security.user.password}")
    private String basicPassword;

    private ResponseEntity<DrinksDTOList> responseToList;
    private ResponseEntity<DrinkDTO> responseToSingle;
    private DrinkDTO drinkToDelete;

    @Given("^the drink repositories are empty$")
    public void repositoryIsEmpty() {
    }
    
    @Given("^the drink repositories with sample drinks$")
    public void repositoryHasContent() throws MalformedURLException {        
        createAndSaveBeer("Dianemayte", "ABO");
        createAndSaveNab("Blurg's Cola", "Aldebaran Beverages");
        createAndSaveBeer("Big F ale", "Failure Brew");
        createAndSaveBeer("Marz'Ale", "FdB");
    }
    
    @When("^I load all drinks$")
    public void loadAllBeers() {
        responseToList = template.getForEntity(resource.toString(), DrinksDTOList.class);
    }
    
    @When("^I delete the first drink$")
    public void deleteADrink() {
        drinkToDelete = responseToList.getBody().getDrinks().get(0);
        
        template.delete(resource.toString()+drinkToDelete.getId());
    }

    @When("^I search for drinks of type 'beer'$")
    public void searchBeersByTypeBeer() {
        responseToList = template.getForEntity(resource.toString()+"types/BEER", DrinksDTOList.class);
    }
    
    @When("^I search for drinks of type 'hydrazine'$")
    public void searchBeersByTypeHydra() {
        responseToList = template.getForEntity(resource.toString()+"types/HYDRAZINE", DrinksDTOList.class);
    }
    
    @When("^I search for drinks with name like 'ale'$")
    public void searchForBeersByName() {
        responseToList = template.getForEntity(resource.toString()+"search/ale", DrinksDTOList.class);
    }
    
    @Then("^I get a list of drinks whose name contains the chars 'ale'$")
    public void shouldGetListOfBeersWhoseNamesContainsAle() {
        DrinksDTOList drinksList = responseToList.getBody();
        
        assertThat(drinksList.getDrinks().size(), equalTo(2));
        assertTrue(drinksList.getDrinks().stream().allMatch(drink -> drink.getName().toLowerCase().contains("ale")));
    }
    
    @Then("^I get all drinks whose property type is 'beer'$")
    public void shouldBeBeersOfTypeBeer() {
        DrinksDTOList drinksList = responseToList.getBody();
        
        assertThat(drinksList.getDrinks().size(), equalTo(3));
        assertTrue(drinksList.getDrinks().stream().allMatch(drink -> DrinkTypeEnum.BEER.equals(drink.getType())));
    }
    
    @Then("^the collection of drinks lacks the deleted one$")
    public void shouldLackDeletedBeer() {
        List<DrinkDTO> drinks = responseToList.getBody().getDrinks();
        
        assertThat(drinks.size(), equalTo(3));
        drinks.stream().forEach(drink -> assertThat(drink.getName(), not(equalTo(drinkToDelete.getName()))));
    }

    @Before
    public void setUp() throws MalformedURLException {
        template = template.withBasicAuth(basicUsername, basicPassword);
        
        base = new URL("http://localhost:" + port + "/");
        resource = new URL(base.toString() + "drinkopedia/api/drinks/");
        beersRepository.deleteAll();
        nabsRepository.deleteAll();
    }
    
    protected void assertShouldReturnErrorCode(ResponseEntity<?> response, HttpStatus statusExpected) {
        assertThat(response.getStatusCode(), equalTo(statusExpected));
    }
    
    private Beer createAndSaveBeer(String name, String producerName) {
        Beer newBeer = new Beer();
        newBeer.setName(name);
        newBeer.setProducerName(producerName);
        newBeer.setAbv(0.05);
        newBeer.setIbu(30L);
        newBeer.setSrm(20L);
        return beersRepository.save(newBeer);
    }
    
    private NonAlcoolicBeverage createAndSaveNab(String name, String producerName) {
        NonAlcoolicBeverage newNab = new NonAlcoolicBeverage();
        newNab.setName(name);
        newNab.setProducerName(producerName);
        return nabsRepository.save(newNab);
    }
    
    RequestCallback requestCallback(final DrinkDTO updatedInstance) {
        return clientHttpRequest -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(clientHttpRequest.getBody(), updatedInstance);
            clientHttpRequest.getHeaders().add(
              HttpHeaders.CONTENT_TYPE,    MediaType.APPLICATION_JSON_VALUE);
        };
    }
    
    @Then("^it should return code 204$")
    public void shouldReturn204() {
        assertShouldReturnErrorCode(responseToList, HttpStatus.NO_CONTENT);
    }
    
    @Then("^it should return code 200$")
    public void shouldReturn200() {
        assertShouldReturnErrorCode(responseToList, HttpStatus.OK);
    }
    
    @Then("^it should return code 201$")
    public void shouldReturn201() {
        assertShouldReturnErrorCode(responseToSingle, HttpStatus.CREATED);
    }
    
    @Then("^I get 404 response code$")
    public void shouldReturn404() {
        assertShouldReturnErrorCode(responseToList, HttpStatus.NOT_FOUND);
    }
}

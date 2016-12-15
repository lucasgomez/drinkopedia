package ch.lgo.drinks.simple.behaviourtests;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import ch.lgo.drinks.simple.dto.DrinkDTO;
import ch.lgo.drinks.simple.dto.list.DrinksDTOList;
import ch.lgo.drinks.simple.entity.Drink;
import ch.lgo.drinks.simple.entity.DrinkTypeEnum;
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
    
    @Value("${security.user.name}")
    private String basicUsername;
    @Value("${security.user.password}")
    private String basicPassword;

    private ResponseEntity<DrinksDTOList> responseToList;
    private ResponseEntity<DrinkDTO> responseToSingle;
    private Long drinkId;

    @Given("^the repository is empty$")
    public void repositoryIsEmpty() {
    }
    
    @Given("^a drink repository with sample drinks$")
    public void repositoryHasContent() throws MalformedURLException {        
        createAndSaveDrink("Dianemayte", "ABO", DrinkTypeEnum.BEER);
        createAndSaveDrink("Marz'Ale", "FdB", DrinkTypeEnum.BEER);
        createAndSaveDrink("Blurg's Cola", "Aldebaran Beverages", DrinkTypeEnum.NON_ALCOOLIC_BEVERAGE);
        createAndSaveDrink("Big F ale", "Failure Brew", DrinkTypeEnum.BEER);
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
        DrinkDTO drinkToCreate = new DrinkDTO();
        drinkToCreate.setName("Dianemayte");
        drinkToCreate.setProducerName("ABO");
        drinkToCreate.setType(DrinkTypeEnum.BEER);
        
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
        responseToList = template.getForEntity(resource.toString()+"types/BEER", DrinksDTOList.class);
    }
    
    @When("^I search for drinks of type 'hydrazine'$")
    public void searchDrinksByTypeHydra() {
        //TODO Test failing due to 'HYDRAZINE' not being member of enum DrinkType. Should be catched by an ex manager and thrown with custom error instead of 500
        responseToList = template.getForEntity(resource.toString()+"types/HYDRAZINE", DrinksDTOList.class);
    }
    
    @When("^I search for drinks with name like 'ale'$")
    public void searchForDrinksByName() {
        responseToList = template.getForEntity(resource.toString()+"search/ale", DrinksDTOList.class);
    }
    
    @Then("^I get a list of drinks whose name contains the chars 'ale'$")
    public void shouldGetListOfDrinksWhoseNamesContainsAle() {
        DrinksDTOList drinksList = responseToList.getBody();
        
        assertThat(drinksList.getDrinks().size(), equalTo(2));
        assertTrue(drinksList.getDrinks().stream().allMatch(drink -> drink.getName().toLowerCase().contains("ale")));
    }
    
    @Then("^I get all drinks whose property type is 'beer'$")
    public void shouldBeDrinksOfTypeBeer() {
        DrinksDTOList drinksList = responseToList.getBody();
        
        assertThat(drinksList.getDrinks().size(), equalTo(3));
        assertTrue(drinksList.getDrinks().stream().allMatch(drink -> DrinkTypeEnum.BEER.equals(drink.getType())));
    }
    
    @Then("^the collection of drinks lacks the deleted one$")
    public void shouldLackDeletedDrink() {
        List<DrinkDTO> drinks = responseToList.getBody().getDrinks();
        
        assertThat(drinks.size(), equalTo(3));
        drinks.stream().forEach(drink -> assertThat(drink.getName(), not(equalTo("Dianemayte"))));
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
        template = template.withBasicAuth(basicUsername, basicPassword);
        
        base = new URL("http://localhost:" + port + "/");
        resource = new URL(base.toString() + "drinkopedia/api/drinks/");
        drinkRepository.deleteAll();
    }
    
    private Drink createAndSaveDrink(String name, String producerName, DrinkTypeEnum type) {
        return drinkRepository.save(new Drink(name, producerName, type));
    }

    protected void assertShouldReturnErrorCode(ResponseEntity<?> response, HttpStatus statusExpected) {
        assertThat(response.getStatusCode(), equalTo(statusExpected));
    }
    
    RequestCallback requestCallback(final DrinkDTO updatedInstance) {
        return clientHttpRequest -> {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(clientHttpRequest.getBody(), updatedInstance);
            clientHttpRequest.getHeaders().add(
              HttpHeaders.CONTENT_TYPE,    MediaType.APPLICATION_JSON_VALUE);
        };
    }
}

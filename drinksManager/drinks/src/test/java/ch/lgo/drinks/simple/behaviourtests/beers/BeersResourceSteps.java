package ch.lgo.drinks.simple.behaviourtests.beers;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import ch.lgo.drinks.simple.dao.BeersRepository;
import ch.lgo.drinks.simple.dao.ProducerRepository;
import ch.lgo.drinks.simple.dto.BeerDTO;
import ch.lgo.drinks.simple.dto.list.BeersDTOList;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration
public class BeersResourceSteps {
    
    @LocalServerPort
    protected int port;
    protected URL base;
    protected URL resource;

    @Autowired
    protected TestRestTemplate template;
    protected ResponseEntity<BeersDTOList> responseToList;
    protected ResponseEntity<BeerDTO> responseToSingle;

    @Value("${security.user.name}")
    private String basicUsername;
    @Value("${security.user.password}")
    private String basicPassword;
    @Autowired
    private BeersRepository drinkRepository;
    @Autowired
    private ProducerRepository producerRepository;
    
    private Long beerId;

//    @Given("^the repository is empty$")
//    public void repositoryIsEmpty() {
//    }
//    
//    @Given("^a beer repository with sample beers$")
//    public void repositoryHasContent() throws MalformedURLException {
//        createAndSaveBeer("Dianemayte", createAndSaveProducer("ABO"));
//        createAndSaveBeer("Marz'Ale", createAndSaveProducer("FdB"));
//        createAndSaveBeer("Big F ale", createAndSaveProducer("Failure Brew"));
//    }
//
//	@When("^I load all beers$")
//    public void loadAllBeers() {
//        responseToList = template.getForEntity(resource.toString(), BeersDTOList.class);
//    }
//    
//    @When("^a beer name is updated$")
//    public void loadAllBeersPickOneThenUpdateItsName() {
//        responseToList = template.getForEntity(resource.toString(), BeersDTOList.class);
//        BeersDTOList beers = responseToList.getBody();
//        BeerDTO beerToUpdate = beers.getBeers().get(0);
//        
//        beerToUpdate.setName(beerToUpdate.getName() + " reloaded");
//        beerId = beerToUpdate.getId();
//        template.execute(resource.toString()+beerToUpdate.getId(), HttpMethod.PUT, requestCallback(beerToUpdate), clientHttpResponse -> null);
//    }
//    
//    @When("^I post a new beer$")
//    public void postNewBeer() {
//        BeerDTO drinkToCreate = new BeerDTO();
//        drinkToCreate.setName("Dianemayte");
//        drinkToCreate.setProducerName("ABO");
//        drinkToCreate.setType(DrinkTypeEnum.BEER);
//        
//        responseToSingle = template.postForEntity(resource.toString(), drinkToCreate, BeerDTO.class);
//    }
//    
//    @When("^load the aforesaid beer at its location$")
//    public void getBeerLocation() {
//        URI postedBeerLocation = responseToSingle.getHeaders().getLocation();
//        
//        responseToSingle = template.getForEntity(postedBeerLocation, BeerDTO.class);
//    }
//    
//    @When("^the aforesaid beer is loaded$")
//    public void getBeerByItsId() {
//        responseToSingle = template.getForEntity(resource.toString()+beerId, BeerDTO.class);
//    }
//    
//    @When("^I delete the first beer$")
//    public void deleteABeer() {
//        BeerDTO drinkToDelete = responseToList.getBody().getBeers().get(0);
//        
//        template.delete(resource.toString()+drinkToDelete.getId());
//    }
//    
//    @When("^I search for beers with name like 'ale'$")
//    public void searchForBeersByName() {
//        responseToList = template.getForEntity(resource.toString()+"search/ale", BeersDTOList.class);
//    }
//    
//    @Then("^I get a list of beers whose name contains the chars 'ale'$")
//    public void shouldGetListOfBeersWhoseNamesContainsAle() {
//        BeersDTOList drinksList = responseToList.getBody();
//        
//        assertThat(drinksList.getBeers().size(), equalTo(2));
//        assertTrue(drinksList.getBeers().stream().allMatch(drink -> drink.getName().toLowerCase().contains("ale")));
//    }
//    
//    @Then("^I get all beers whose property type is 'beer'$")
//    public void shouldBeBeersOfTypeBeer() {
//        BeersDTOList drinksList = responseToList.getBody();
//        
//        assertThat(drinksList.getBeers().size(), equalTo(3));
//        assertTrue(drinksList.getBeers().stream().allMatch(drink -> DrinkTypeEnum.BEER.equals(drink.getType())));
//    }
//    
//    @Then("^the collection of beers lacks the deleted one$")
//    public void shouldLackDeletedBeer() {
//        List<BeerDTO> drinks = responseToList.getBody().getBeers();
//        drinks.forEach(drink -> System.out.println(drink.getType() + ") " + drink.getId() + " - " + drink.getName()));
//        assertThat(drinks.size(), equalTo(2));
//        drinks.stream().forEach(drink -> assertThat(drink.getName(), not(equalTo("Dianemayte"))));
//    }
//    
//    @Then("^it should return the created beer$")
//    public void shouldReturnCreatedBeer() {
//        BeerDTO createdBeer = responseToSingle.getBody();
//        assertThat(createdBeer, notNullValue());
//        assertThat(createdBeer.getName(), equalTo("Dianemayte"));
//    }
//    
//    @Then("^that reloaded beer has the new name$")
//    public void shouldReturnBeerWithNewName() {
//        BeerDTO updatedBeer = responseToSingle.getBody();
//        assertThat(updatedBeer.getName(), equalTo("Dianemayte" + " reloaded"));
//    }
//    
//    protected Producer createAndSaveProducer(String name) {
//		Producer newProducer = new Producer();
//		newProducer.setName(name);
//		return producerRepository.save(newProducer);
//	}
//    
//    private Beer createAndSaveBeer(String name, Producer producer) {
//        Beer newBeer = new Beer();
//        newBeer.setName(name);
//        newBeer.setProducer(producer);
//        newBeer.setAbv(0.05);
//        newBeer.setIbu(30L);
//        newBeer.setSrm(20L);
//        return drinkRepository.save(newBeer);
//    }
//    
//    RequestCallback requestCallback(final BeerDTO updatedInstance) {
//        return clientHttpRequest -> {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.writeValue(clientHttpRequest.getBody(), updatedInstance);
//            clientHttpRequest.getHeaders().add(
//              HttpHeaders.CONTENT_TYPE,    MediaType.APPLICATION_JSON_VALUE);
//        };
//    }
//    
//    @Then("^it should return code 204$")
//    public void shouldReturn204() {
//        assertShouldReturnErrorCode(responseToList, HttpStatus.NO_CONTENT);
//    }
//    
//    @Then("^it should return code 200$")
//    public void shouldReturn200() {
//        assertShouldReturnErrorCode(responseToList, HttpStatus.OK);
//    }
//    
//    @Then("^it should return code 201$")
//    public void shouldReturn201() {
//        assertShouldReturnErrorCode(responseToSingle, HttpStatus.CREATED);
//    }
//    
//    @Then("^I get 404 response code$")
//    public void shouldReturn404() {
//        assertShouldReturnErrorCode(responseToList, HttpStatus.NOT_FOUND);
//    }
//
//    protected void assertShouldReturnErrorCode(ResponseEntity<?> response, HttpStatus statusExpected) {
//        assertThat(response.getStatusCode(), equalTo(statusExpected));
//    }
//
//    @Before
//    public void setUp() throws MalformedURLException {
//        template = template.withBasicAuth(basicUsername, basicPassword);
//        
//        base = new URL("http://localhost:" + port + "/");
//        resource = new URL(base.toString() + "drinkopedia/api/beers/");
//        drinkRepository.deleteAll();
//    }
}

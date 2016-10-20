package ch.lgo.drinks.simple;


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DrinksResourceIT {

    @LocalServerPort
    private int port;

    private URL base;
    private URL resource;

    @Autowired
    private TestRestTemplate template;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/");
        this.resource = new URL(base.toString()+"drinks/");
    }

    @Test
    public void getAllReturnsNoContent() throws Exception {
        ResponseEntity<String> response = template.getForEntity(resource.toString(),
                String.class);
        assertThat(response.getStatusCode(), equalTo(HttpStatus.NO_CONTENT));
    }
    
//    @Test
//    public void putDrinkReturnsNewDrink() {
//    	DrinkDTO drinkToCreate = new DrinkDTO();
//    	drinkToCreate.setName("The Trooper");
//    	drinkToCreate.setProducerName("Robinson's Brewery");
//    	ResponseEntity<String> response = template.postForEntity(resource, drinkToCreate, null, null);
//    	assertThat(response.getStatusCode(), equalTo(HttpStatus.ACCEPTED));
//    }
}
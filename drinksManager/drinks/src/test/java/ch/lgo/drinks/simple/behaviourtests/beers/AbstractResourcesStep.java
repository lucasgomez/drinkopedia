package ch.lgo.drinks.simple.behaviourtests.beers;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import ch.lgo.drinks.simple.dao.ProducerRepository;
import ch.lgo.drinks.simple.entity.Producer;

/**
 * @param <D> Dto
 * @param <L> DtoList
 */
public class AbstractResourcesStep <D, L> {

    @LocalServerPort
    protected int port;
    protected URL base;
    protected URL resource;

    @Autowired
    protected TestRestTemplate template;
    protected ResponseEntity<L> responseToList;
    protected ResponseEntity<D> responseToSingle;

    @Value("${security.user.name}")
    protected String basicUsername;
    @Value("${security.user.password}")
    protected String basicPassword;
    @Autowired
    protected ProducerRepository producerRepository;
    
    protected Producer createAndSaveProducer(String name) {
		Producer newProducer = new Producer(name);
		return producerRepository.save(newProducer);
	}
    
}

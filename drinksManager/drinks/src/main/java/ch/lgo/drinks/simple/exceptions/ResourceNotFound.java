package ch.lgo.drinks.simple.exceptions;

public class ResourceNotFound extends Exception {

    private String message;
    
    public ResourceNotFound(String message) {
        this.message = message;
    }

    public ResourceNotFound() {
    }

}

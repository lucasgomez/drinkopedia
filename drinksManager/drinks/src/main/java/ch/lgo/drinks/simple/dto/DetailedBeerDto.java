package ch.lgo.drinks.simple.dto;

public class DetailedBeerDto extends BeerDTO {
	
	private String producerOriginName;
	private String fermenting;
    private String comment;
	
	public String getProducerOriginName() {
		return producerOriginName;
	}
	public void setProducerOriginName(String producerOriginName) {
		this.producerOriginName = producerOriginName;
	}

    public String getFermenting() {
        return fermenting;
    }
    public void setFermenting(String fermenting) {
        this.fermenting = fermenting;
    }
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
}

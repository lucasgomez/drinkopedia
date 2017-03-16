package ch.lgo.drinks.simple.dto;

public class ProducerDto {
	
	private Long id;
	private String name;	
	private Long originId;
	private String originName;
	private String originShortName;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public Long getOriginId() {
		return originId;
	}
	public void setOriginId(Long originId) {
		this.originId = originId;
	}
	
	public String getOriginName() {
		return originName;
	}
	public void setOriginName(String originName) {
		this.originName = originName;
	}
	
	public String getOriginShortName() {
		return originShortName;
	}
	public void setOriginShortName(String originShortName) {
		this.originShortName = originShortName;
	}
}

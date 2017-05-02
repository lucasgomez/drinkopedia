package ch.lgo.drinks.simple.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Beer {

	private Long id;
	private String externalId; //Code Amstein
	private String name;
	private Producer producer;
    private Double abv; //Alcool
    private Long ibu; //Bitterness
    private Long srm; //Color
    private BeerColor color;
	private Long plato;
	private BeerStyle style;
    private FermentingEnum fermenting;
	private String comment;
	private BottledBeer bottle;
	private TapBeer tap;
	private Set<Bar> bars = new HashSet<>();
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@ManyToOne
	public Producer getProducer() {
		return producer;
	}
	public void setProducer(Producer producer) {
		this.producer = producer;
	}
	
    public Double getAbv() {
        return abv;
    }
    public void setAbv(Double abv) {
        this.abv = abv;
    }
    
    public Long getIbu() {
        return ibu;
    }
    public void setIbu(Long ibu) {
        this.ibu = ibu;
    }
    
    public Long getSrm() {
        return srm;
    }
    public void setSrm(Long srm) {
        this.srm = srm;
    }

    @ManyToOne
    public BeerColor getColor() {
		return color;
	}
	public void setColor(BeerColor color) {
		this.color = color;
	}

    public Long getPlato() {
		return plato;
	}
	public void setPlato(Long plato) {
		this.plato = plato;
	}
	
	@ManyToOne
	public BeerStyle getStyle() {
		return style;
	}
	public void setStyle(BeerStyle style) {
		this.style = style;
	}

	@Enumerated(EnumType.STRING)
	public FermentingEnum getFermenting() {
		return fermenting;
	}
	public void setFermenting(FermentingEnum fermenting) {
		this.fermenting = fermenting;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	@OneToOne(optional=true, fetch = FetchType.LAZY, mappedBy="beer")
	public BottledBeer getBottle() {
		return bottle;
	}
	public void setBottle(BottledBeer bottle) {
		this.bottle = bottle;
	}
	
	@OneToOne(optional=true, fetch = FetchType.LAZY, mappedBy="beer")
	public TapBeer getTap() {
		return tap;
	}
	public void setTap(TapBeer tap) {
		this.tap = tap;
	}

	@OneToMany(mappedBy="beers")
	public Set<Bar> getBars() {
		return bars;
	}
	public void setBars(Set<Bar> bars) {
		this.bars = bars;
	}

	public Beer() {}
}

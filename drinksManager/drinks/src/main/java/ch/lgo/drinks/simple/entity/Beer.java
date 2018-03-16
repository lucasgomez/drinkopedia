package ch.lgo.drinks.simple.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import ch.lgo.drinks.simple.dao.DescriptiveLabel;

@Entity
public class Beer implements HasId, DescriptiveLabel {

	private Long id;
	private String externalId; //Code Amstein
	private String name;
	private Producer producer;
    private Double abv; //Alcool
    private Long ibu; //Bitterness
    private Long srm; //Color
    private StrengthEnum bitterness;
    private StrengthEnum sourness;
    private StrengthEnum sweetness;
    private StrengthEnum hopping;
	private BeerColor color;
	private Long plato;
	private BeerStyle style;
    private FermentingEnum fermenting;
	private String comment;
	private BottledBeer bottle;
	private TapBeer tap;
	
	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}
	public Beer setId(Long id) {
		this.id = id;
		return this;
	}

	public String getExternalId() {
		return externalId;
	}
	public Beer setExternalId(String externalId) {
		this.externalId = externalId;
		return this;
	}
	
	public String getName() {
		return name;
	}
	public Beer setName(String name) {
		this.name = name;
		return this;
	}
	
	@ManyToOne
	public Producer getProducer() {
		return producer;
	}
	public Beer setProducer(Producer producer) {
		this.producer = producer;
		return this;
	}
	
    public Double getAbv() {
        return abv;
    }
    public Beer setAbv(Double abv) {
        this.abv = abv;
		return this;
    }
    
    public Long getIbu() {
        return ibu;
    }
    public Beer setIbu(Long ibu) {
        this.ibu = ibu;
		return this;
    }
    
    public Long getSrm() {
        return srm;
    }
    public Beer setSrm(Long srm) {
        this.srm = srm;
		return this;
    }

	@Enumerated(EnumType.STRING)
    public StrengthEnum getBitterness() {
		return bitterness;
	}
	public Beer setBitterness(StrengthEnum bitterness) {
		this.bitterness = bitterness;
		return this;
	}

	@Enumerated(EnumType.STRING)
	public StrengthEnum getSourness() {
		return sourness;
	}
	public Beer setSourness(StrengthEnum sourness) {
		this.sourness = sourness;
		return this;
	}

	@Enumerated(EnumType.STRING)
	public StrengthEnum getSweetness() {
		return sweetness;
	}
	public Beer setSweetness(StrengthEnum sweetness) {
		this.sweetness = sweetness;
		return this;
	}

	@Enumerated(EnumType.STRING)
	public StrengthEnum getHopping() {
		return hopping;
	}
	public Beer setHopping(StrengthEnum hopping) {
		this.hopping = hopping;
		return this;
	}

    @ManyToOne
    public BeerColor getColor() {
		return color;
	}
	public Beer setColor(BeerColor color) {
		this.color = color;
		return this;
	}

    public Long getPlato() {
		return plato;
	}
	public Beer setPlato(Long plato) {
		this.plato = plato;
		return this;
	}
	
	@ManyToOne
	public BeerStyle getStyle() {
		return style;
	}
	public Beer setStyle(BeerStyle style) {
		this.style = style;
		return this;
	}

	@Enumerated(EnumType.STRING)
	public FermentingEnum getFermenting() {
		return fermenting;
	}
	public Beer setFermenting(FermentingEnum fermenting) {
		this.fermenting = fermenting;
		return this;
	}
	
	public String getComment() {
		return comment;
	}
	public Beer setComment(String comment) {
		this.comment = comment;
		return this;
	}
	
	@OneToOne(optional=true, fetch = FetchType.LAZY, mappedBy="beer")
	public BottledBeer getBottle() {
		return bottle;
	}
	public Beer setBottle(BottledBeer bottle) {
		this.bottle = bottle;
		return this;
	}
	
	@OneToOne(optional=true, fetch = FetchType.LAZY, mappedBy="beer")
	public TapBeer getTap() {
		return tap;
	}
	public Beer setTap(TapBeer tap) {
		this.tap = tap;
		return this;
	}

	public Beer() {}
}

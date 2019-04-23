package ch.lgo.drinks.simple.entity;

import static java.util.Comparator.comparing;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public class TapBeer implements HasId, HasBar<TapBeer> {

    public static Long VOLUME_SMALL_CL = 25L;
    public static Long VOLUME_BIG_CL = 50L;

    private Long id;
    private Beer beer;
    private Double priceSmall;
    private Double priceBig;
    private Double buyingPricePerLiter;
    private Availability availability;
    private AssortmentCategory assortment;
    private LocalDateTime availabilityDate;
    private Set<Bar> bars = new HashSet<>();

    private static Comparator<TapBeer> byPrice = comparing(TapBeer::getPriceBig,
            Comparator.nullsLast(Comparator.naturalOrder()));
    private static Comparator<TapBeer> byName = comparing(
            tap -> tap.getBeer().getName(),
            Comparator.nullsLast(Comparator.naturalOrder()));

    public TapBeer(Beer beer) {
        this.setBeer(beer);
    }

    public TapBeer() {
    }

    @Override
    @Id
    public Long getId() {
        return id;
    }
    @Override
    public TapBeer setId(Long id) {
        this.id = id;
        return this;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    public Beer getBeer() {
        return beer;
    }
    public TapBeer setBeer(Beer beer) {
        this.beer = beer;
        return this;
    }

    public Double getPriceSmall() {
        return priceSmall;
    }
    public TapBeer setPriceSmall(Double priceSmall) {
        this.priceSmall = priceSmall;
        return this;
    }

    public Double getPriceBig() {
        return priceBig;
    }
    public TapBeer setPriceBig(Double priceBig) {
        this.priceBig = priceBig;
        return this;
    }

    public Double getBuyingPricePerLiter() {
        return buyingPricePerLiter;
    }
    public TapBeer setBuyingPricePerLiter(Double buyingPricePerLiter) {
        this.buyingPricePerLiter = buyingPricePerLiter;
        return this;
    }

    @Enumerated(EnumType.STRING)
    public Availability getAvailability() {
        return availability;
    }
    public void setAvailability(Availability availability) {
        this.availability = availability;
    }

    @Enumerated(EnumType.STRING)
    public AssortmentCategory getAssortment() {
        return assortment;
    }
    public void setAssortment(AssortmentCategory assortment) {
        this.assortment = assortment;
    }

    public LocalDateTime getAvailabilityDate() {
        return availabilityDate;
    }
    public void setAvailabilityDate(LocalDateTime availabilityDate) {
        this.availabilityDate = availabilityDate;
    }

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "tapBeers")
    public Set<Bar> getBars() {
        return bars;
    }
    public TapBeer setBars(Set<Bar> bars) {
        this.bars = bars;
        return this;
    }

    @Transient
    public Set<Long> getBarsIds() {
        return getBars().stream()
                .filter(Objects::nonNull)
                .map(Bar::getId)
                .collect(Collectors.toSet());
    }
    
    public static Comparator<TapBeer> byPrice() {
        return byPrice;
    }
    public static Comparator<TapBeer> byName() {
        return byName;
    }
}

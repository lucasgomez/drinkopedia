package ch.lgo.drinks.simple.dto;

import ch.lgo.drinks.simple.dao.DescriptiveLabel;
import ch.lgo.drinks.simple.entity.HasId;

public class DescriptiveLabelDto implements DescriptiveLabel, HasId {

    private Long id;
    private String name;
    private String comment;

    public Long getId() {
        return id;
    }
    public DescriptiveLabelDto setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }
    public DescriptiveLabelDto setName(String name) {
        this.name = name;
        return this;
    }
    
    public String getComment() {
        return comment;
    }
    public DescriptiveLabelDto setComment(String comment) {
        this.comment = comment;
        return this;
    }
}

package model;

import java.io.Serializable;
import java.util.List;

public class CardAbilityPropEntry implements Serializable {
    public Integer type;
    public List<CardCostProp> cost;
    public MultiLanguageString value;
}

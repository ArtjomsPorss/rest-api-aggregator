package com.artjomsporss.restapiaggregator.exchange;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Exchange {
    private String _id;
    private String code;
    private String currency;
    private String name;
}

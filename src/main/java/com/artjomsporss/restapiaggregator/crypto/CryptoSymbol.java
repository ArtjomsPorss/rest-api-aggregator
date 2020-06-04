package com.artjomsporss.restapiaggregator.crypto;

import com.artjomsporss.restapiaggregator.common.ApiElement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoSymbol extends ApiElement {
    private String description;
    private String displaySymbol;
    private String symbol;
    // Exchange is required for future referencing to exchange where it is coming from
    private String exchangeName;
}

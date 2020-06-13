package com.artjomsporss.restapiaggregator.crypto;

import com.artjomsporss.restapiaggregator.common.ApiElement;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.util.List;


@Data
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CryptoExchange extends ApiElement {
    private String exchangeName;
    private List<String> resolutions;
}

package com.artjomsporss.restapiaggregator.common;

import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
public abstract class ApiElement {
    @Getter
    private LocalDateTime date;

    public void initDate() {
        this.date = LocalDateTime.now();
    }
}

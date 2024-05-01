package com.megapro.invoicesync.util.classes;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Revenue {
    private String month;
    private BigDecimal revenue;
    
    public Revenue(String month, BigDecimal revenue) {
        this.month = month;
        this.revenue = revenue;
    }

    @Override
    public String toString() {
        return String.format("[\"%s\", %.2f]", month, revenue.doubleValue()); 
    }
}

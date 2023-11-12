package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JsonData {

    private Float temperature;
    private Integer CO2;


    public JsonData(JsonData another) {
        this.temperature = another.temperature;
        this.CO2 = another.CO2;
    }
}

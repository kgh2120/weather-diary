package zerobase.weather.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDate;

@Setter
@Getter
@Entity
public class DateWeather {

    @Id
    private LocalDate date;

    private String weather;
    private String icon;
    private double temperature;
}

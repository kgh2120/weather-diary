package zerobase.weather.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String text;
    private LocalDate date;
    private  String weather;
    private  String icon;
    private  double temperature;


    public void updateText(String text) {
        this.text = text;
    }

    public void setDateWeather(DateWeather dateWeather) {
        date = dateWeather.getDate();
        weather = dateWeather.getWeather();
        icon = dateWeather.getIcon();
        temperature = dateWeather.getTemperature();
    }
}

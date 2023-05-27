package zerobase.weather.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DiaryRepository;

import javax.transaction.Transactional;
import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class DiaryService {

    public static final String WEATHER = "weather";
    @Value("${api.weather}")
    private String weatherApi;

    private final DiaryRepository diaryRepository;

    public void createDiary(LocalDate date, String text) {
        log.info(weatherApi);
        Map<String, Object> weather = getWeather();

        Diary diary = new Diary();
        diary.setWeather((String)weather.get("main"));
        diary.setIcon((String) weather.get("icon"));
        diary.setTemperature((Double)weather.get("temp"));
        diary.setText(text);
        diary.setDate(date);

        diaryRepository.save(diary);
    }

    private Map<String,Object> getWeather() {
        return parseWeather(connectToOpenWeatherMap());
    }

    private String connectToOpenWeatherMap() {
        String apiUrl = String.format(weatherApi, "seoul");
        try {
            URI uri = new URI(apiUrl);
            RestTemplate restTemplate = new RestTemplate();
            return restTemplate.getForObject(uri, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("fail to get weather Info");
            throw new RuntimeException("fail to get weather Info");
        }

    }

    private Map<String,Object> parseWeather(String json) {
        Map<String, Object> resultMap = new HashMap<>();
        JsonElement element = JsonParser.parseString(json);
        JsonElement weather = element.getAsJsonObject().get(WEATHER).getAsJsonArray().get(0);
        String main = weather.getAsJsonObject().get("main").getAsString();
        String icon = weather.getAsJsonObject().get("icon").getAsString();
        double temp = element.getAsJsonObject().get("main").getAsJsonObject().get("temp")
                .getAsDouble();

        resultMap.put("main",main);
        resultMap.put("icon",icon);
        resultMap.put("temp",temp);

        return resultMap;
    }
}

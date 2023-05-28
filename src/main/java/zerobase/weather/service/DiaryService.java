package zerobase.weather.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;


import java.net.URI;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DiaryService {

    public static final String WEATHER = "weather";
    @Value("${api.weather}")
    private String weatherApi;

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("Start create Diary");
        log.info("Start create Diary");
        DateWeather dateWeather = getDateWeather(date);

        Diary diary = new Diary();
        diary.setDateWeather(dateWeather);
        diary.setText(text);
        diaryRepository.save(diary);
        logger.info("end to  create Diary");
        log.info("end create Diary");
    }

    private DateWeather getDateWeather(LocalDate date) {
        return dateWeatherRepository.findById(date)
                .orElseGet(this::createDateWeatherFromApi);
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void saveDateWeather(){
        dateWeatherRepository.save(createDateWeatherFromApi());
    }

    private DateWeather createDateWeatherFromApi() {
        Map<String, Object> weather = getWeather();
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather((String)weather.get("main"));
        dateWeather.setIcon((String) weather.get("icon"));
        dateWeather.setTemperature((Double)weather.get("temp"));
        return dateWeather;
    }

    public List<Diary> readDiary(LocalDate date) {
        logger.debug("read diary");
        if(date.isAfter(LocalDate.ofYearDay(3050,1)))
            throw new InvalidDate();

        return diaryRepository.findAllByDate(date);

    }

    private Map<String,Object> getWeather() {
        return parseWeather(connectToOpenWeatherMap());
    }
    @Transactional
    public void updateDiary(LocalDate date, String text) {
        Diary diary = diaryRepository.getFirstByDate(date);
        diary.updateText(text);
    }

    @Transactional
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
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

    public List<Diary> readDiaries(LocalDate from, LocalDate to) {
        return diaryRepository.findAllByDateBetween(from,to);
    }


}

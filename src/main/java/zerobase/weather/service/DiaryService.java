package zerobase.weather.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;

@Transactional
@RequiredArgsConstructor
@Service
public class DiaryService {
    public void createDiary(LocalDate date, String text) {

    }
}

package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import zerobase.weather.domain.Diary;

public interface DiaryRepository extends JpaRepository<Diary,Integer> {
}

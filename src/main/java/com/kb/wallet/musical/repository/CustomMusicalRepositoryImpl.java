package com.kb.wallet.musical.repository;

import com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse;
import com.kb.wallet.seat.dto.response.SectionAvailability;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class CustomMusicalRepositoryImpl implements CustomMusicalRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<MusicalSeatAvailabilityResponse> findMusicalSeatAvailability(Long musicalId,
            LocalDate specificDate) {

        // 기본 데이터 불러오기
        String jpql =
                "SELECT DISTINCT NEW com.kb.wallet.musical.dto.response.MusicalSeatAvailabilityResponse("
                        +
                        "sec.schedule.id, " +
                        "sec.schedule.startTime " + ")" +
                        "FROM Section sec " +
                        "JOIN sec.schedule s " +
                        "JOIN sec.musical m " +
                        "WHERE m.id = :musicalId AND s.date = :specificDate " +
                        "ORDER BY s.startTime ASC";

        List<MusicalSeatAvailabilityResponse> responses = entityManager.createQuery(jpql,
                        MusicalSeatAvailabilityResponse.class)
                .setParameter("musicalId", musicalId)
                .setParameter("specificDate", specificDate)
                .getResultList();

        // 배우 이름 불러오기
        for (MusicalSeatAvailabilityResponse response : responses) {
            String actorJpql = "SELECT a.name FROM Actor a WHERE a.schedule.id = :scheduleId";
            List<String> actorNames = entityManager.createQuery(actorJpql, String.class)
                    .setParameter("scheduleId", response.getScheduleId())
                    .getResultList();

            response.setActorNames(actorNames); // 배우 이름을 설정
        }

        // 구역 정보 불러오기
        for (MusicalSeatAvailabilityResponse response : responses) {
            String sectionJpql =
                    "SELECT NEW com.kb.wallet.seat.dto.response.SectionAvailability(sec.grade, " +
                            "sec.availableSeats) " +
                            "FROM Section sec " +
                            "WHERE sec.schedule.id = :scheduleId";

            List<SectionAvailability> sections = entityManager.createQuery(sectionJpql,
                            SectionAvailability.class)
                    .setParameter("scheduleId", response.getScheduleId())
                    .getResultList();

            response.setSections(sections);
        }

        return responses;
    }
}


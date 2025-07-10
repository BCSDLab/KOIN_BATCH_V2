package in.koreatech.batch.domain.dining.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import in.koreatech.batch.domain.dining.model.DiningType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class MenuJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job mealMenuPollingJob;

    @Scheduled(fixedDelay = 10000) // 10초마다 실행
    public void runMealMenuPollingJob() {
        if (!DiningType.isMealTimeNow()) {
            log.info("식사시간 종료됨. 반복 중단.");
            return; // polling 중단
        }

        try {
            jobLauncher.run(
                mealMenuPollingJob,
                new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 매 실행마다 파라미터 다르게
                    .toJobParameters()
            );
        } catch (Exception e) {
            log.error("식단 Job 실행 중 오류 발생", e);
        }
    }

    @Scheduled(cron = "0 0 6,8 * * *")
    public void updateBreakfastMenu() {
        try {
            jobLauncher.run(
                mealMenuPollingJob,
                new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters()
            );
        } catch (Exception e) {
            log.error("식단 Job 실행 중 오류 발생", e);
        }
    }

    @Scheduled(cron = "0 30 10,11 * * *")
    public void updateLunchMenu() {
        try {
            jobLauncher.run(
                mealMenuPollingJob,
                new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters()
            );
        } catch (Exception e) {
            log.error("식단 Job 실행 중 오류 발생", e);
        }
    }

    @Scheduled(cron = "0 0 15 * * *")
    @Scheduled(cron = "0 30 17 * * *")
    public void updateDinnerMenu() {
        try {
            jobLauncher.run(
                mealMenuPollingJob,
                new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters()
            );
        } catch (Exception e) {
            log.error("식단 Job 실행 중 오류 발생", e);
        }
    }
}

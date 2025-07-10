package in.koreatech.batch.domain.bus.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CityBusJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job cityBusTimetableCrawlingBatchJob;

    @Scheduled(cron = "0 0 0 * * 0")
    public void runCityBusTimetableBatchJob() {
        try {
            log.info("시내버스 시간표 크롤링 배치 실행 시작");

            jobLauncher.run(
                cityBusTimetableCrawlingBatchJob,
                new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters()
            );
        } catch (Exception e) {
            log.error("시내버스 시간표 배치 실행 중 오류 발생", e);
        }
    }
}

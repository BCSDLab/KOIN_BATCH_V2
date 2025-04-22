package in.koreatech.batch.domain.dining;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import in.koreatech.batch._common.util.PortalLoginUtil;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class TestController {

    private final JobLauncher jobLauncher;
    private final Job todayDiningCrawlingJob;
    private final PortalLoginUtil portalLoginUtil;

    @GetMapping("/today-dining")
    public String runTodayDiningBatch() {
        try {
            portalLoginUtil.getOrRefreshLoginCookie();

            // JobParameters는 매 실행마다 달라야 하므로 timestamp 추가
            JobParameters jobParameters = new JobParametersBuilder()
                .addString("run.id", LocalDateTime.now().toString())
                .toJobParameters();

            JobExecution jobExecution = jobLauncher.run(todayDiningCrawlingJob, jobParameters);

            return "Batch job started: " + jobExecution.getStatus();
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to start batch: " + e.getMessage();
        }
    }
}

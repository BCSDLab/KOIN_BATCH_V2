package in.koreatech.batch.domain.dining.job;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import in.koreatech.batch._common.util.PortalLoginUtil;
import in.koreatech.batch.domain.dining.model.DiningMenu;
import in.koreatech.batch.domain.dining.model.Meal;
import in.koreatech.batch.domain.dining.repository.DiningMenuRepository;
import in.koreatech.batch.domain.dining.util.DiningMenuRequester;
import in.koreatech.batch.domain.dining.util.DiningResponseParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiningCrawlingTasklet implements Tasklet {

    private final PortalLoginUtil portalLoginUtil;
    private final DiningMenuRequester diningMenuRequester;
    private final DiningResponseParser diningResponseParser;
    private final DiningMenuRepository diningMenuRepository;

    private static final Clock KST_CLOCK = Clock.system(ZoneId.of("Asia/Seoul"));
    private static final String[] RESTAURANTS = {"한식", "일품", "특식-전골/뚝배기", "능수관"};
    private static final String CAMPUS1 = "Campus1";
    private static final String CAMPUS2 = "Campus2";

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("[DiningCrawling] 실행 시작");

        String loginCookie = portalLoginUtil.getOrRefreshLoginCookie();

        LocalDate today = LocalDate.now(KST_CLOCK);
        List<Meal> remainingMeals = Meal.remainingMeals(KST_CLOCK);

        for (Meal meal : remainingMeals) {
            crawlAndSave(today, meal, loginCookie);
        }

        for (int i = 1; i <= 7; i++) {
            LocalDate futureDate = today.plusDays(i);
            for (Meal meal : Meal.values()) {
                crawlAndSave(futureDate, meal, loginCookie);
            }
        }

        log.info("[DiningCrawling] 크롤링 완료");
        return RepeatStatus.FINISHED;
    }

    private void crawlAndSave(LocalDate date, Meal meal, String loginCookie) {
        List<DiningMenu> menus = new ArrayList<>();

        for (String restaurant : RESTAURANTS) {
            addIfNotNull(menus, getMenu(date, meal, restaurant, CAMPUS1, loginCookie));
        }

        addIfNotNull(menus, getMenu(date, meal, "코너1", CAMPUS2, loginCookie));

        if (!menus.isEmpty()) {
            diningMenuRepository.saveAll(menus);
            log.info("[DiningCrawlingTasklet] {} {}건 저장 완료", meal.name(), menus.size());
        }
    }

    private DiningMenu getMenu(LocalDate date, Meal meal, String restaurant, String campus, String cookie) {
        try {
            Response response = diningMenuRequester.sendRequest(cookie, date.toString(), meal.name().toLowerCase(), restaurant, campus);
            return diningResponseParser.parse(response);
        } catch (Exception e) {
            log.warn("식단 파싱 실패 - {}, {}, {}, {}", date, meal.name(), restaurant, campus);
            return null;
        }
    }

    private <T> void addIfNotNull(List<T> list, T item) {
        if (item != null) {
            list.add(item);
        }
    }
}

package in.koreatech.batch.domain.dining.job;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import in.koreatech.batch.domain.dining.client.DiningClient;
import in.koreatech.batch.domain.dining.model.DiningType;
import in.koreatech.batch.domain.dining.model.Menu;
import in.koreatech.batch.domain.dining.repository.MenuRepository;
import in.koreatech.batch.domain.dining.util.MenuComparator;
import in.koreatech.batch.domain.portal.client.PortalLoginClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DiningJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PortalLoginClient loginClient;
    private final DiningClient diningClient;
    private final DataSource dataDBSource;
    private final MenuRepository menuRepository;

    public DiningJobConfig(
        JobRepository jobRepository,
        @Qualifier(value = "dataTransactionManager") PlatformTransactionManager transactionManager,
        PortalLoginClient loginClient,
        DiningClient diningClient,
        @Qualifier(value = "dataDBSource") DataSource dataDBSource,
        MenuRepository menuRepository
    ) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.loginClient = loginClient;
        this.diningClient = diningClient;
        this.dataDBSource = dataDBSource;
        this.menuRepository = menuRepository;
    }

    /**
     * 1. 아우누리 식단을 크롤링한다.
     * 1. redis에 저장된 쿠키가 있으면 사용한다.
     * 2. redis에 저장된 쿠키가 없으면 아우누리에 로그인하여 쿠키를 저장한다.
     * 2. 크롤링한 식단 정보를 DB에 저장한다.
     * 1. DB에 저장된 식단 정보와 비교하여 변경된 식단이 있으면 업데이트한다.
     * 2. 이 때는 식단이 변경되어도 is_changed를 업데이트하지 않는다.
     * 3. 식단에 '천원의 아침'이 포함되어있으면 image_url에 천원의 아침 이미지 url을 삽입한다.
     * 3. 현재 시간이 식사 시간인지 확인한다.
     * 4. 식사시간이 아니면 종료된다.
     * 5. 식사시간이면 현재 식사시간 메뉴를 크롤링한다.
     * 6. 이전에 크롤링했던 메뉴 정보와 새로 크롤링한 메뉴 정보를 비교한다.
     * 7. 변경된 메뉴가 있으면 DB를 업데이트한다.
     * 1. 이 때는 is_changed를 현재 시간으로 업데이트한다.
     * 8. 식사시간이 끝날 때까지 5번부터 반복한다.
     */

    @Bean
    public Job crawlDiningMenusJob() {
        return new JobBuilder("crawlDiningMenusJob", jobRepository)
            .start(ensureLoginStep())
            .next(crawlMenusStep())
            .next(updateMenusStep())
            .next(checkMealTimeStep())
            .on("NOT_MEAL_TIME").end() // 식사시간 아니면 종료
            .from(checkMealTimeStep()).on("*").to(crawlCurrentMenuStep()) // 식사시간이면 메뉴 크롤링
            .end()
            .build();
    }

    /**
     * 아우누리 로그인
     */
    @Bean
    public Step ensureLoginStep() {
        return new StepBuilder("ensureLoginStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                JobExecution jobExecution = contribution.getStepExecution().getJobExecution();
                jobExecution.getExecutionContext().put("loginToken", loginClient.getOrRefreshLoginCookie());
                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    /**
     * 크롤링
     */
    @Bean
    public Step crawlMenusStep() {
        return new StepBuilder("crawlMealsStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                String loginToken = (String)chunkContext.getStepContext().getJobExecutionContext()
                    .get("loginToken");
                List<Menu> menus = diningClient.crawlWeekDiningMenus(loginToken);

                JobExecution jobExecution = contribution.getStepExecution().getJobExecution();
                jobExecution.getExecutionContext().put("menus", menus);

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    /**
     * 크롤링 데이터 영속화
     */
    @Bean
    public Step updateMenusStep() {
        return new StepBuilder("updateMealsStep", jobRepository)
            .<Menu, Menu>chunk(10, transactionManager)
            .reader(new ItemReader<>() { // 크롤링 데이터 가져오기

                @Value("#{stepExcution.jobExcution.excutionContext['menus']}")
                private LinkedList<Menu> menus;

                @Override
                public Menu read() {
                    return (menus == null || menus.isEmpty()) ? null : menus.poll();
                }
            })
            .writer(menuUpsertWriter())
            .build();
    }

    /**
     * 메뉴 정보 영속화
     */
    @Bean
    public JdbcBatchItemWriter<Menu> menuUpsertWriter() {
        return new JdbcBatchItemWriterBuilder<Menu>()
            .dataSource(dataDBSource)
            .sql("""
                    INSERT INTO koin.dining_menus(date, type, place, price_card, price_cash, kcal, menu, image_url, is_changed)
                    VALUES (:date, :type, :place, :priceCard, :priceCash, :kcal, :menu, :imageUrl, :isChanged)
                    ON DUPLICATE KEY UPDATE
                      price_card = :priceCard,
                      price_cash = :priceCash,
                      kcal = :kcal,
                      menu = :menu,
                      is_changed = :isChanged
                """)
            .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
            .build();
    }

    /**
     * 현재 식사시간인지 확인
     */
    @Bean
    public Step checkMealTimeStep() {
        return new StepBuilder("checkMealTimeStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                if (DiningType.isMealTimeNow()) {
                    return RepeatStatus.FINISHED;
                } else {
                    contribution.setExitStatus(new ExitStatus("NOT_MEAL_TIME"));
                    return RepeatStatus.FINISHED;
                }
            }, transactionManager)
            .build();
    }

    /**
     * 현재 식사시간 메뉴 최신화(이후 스케줄러에 의해 반복)
     */
    @Bean
    public Step crawlCurrentMenuStep() {
        return new StepBuilder("crawlCurrentMenuStep", jobRepository)
            .tasklet((contribution, chunkContext) -> {
                if (!DiningType.isMealTimeNow()) {
                    log.info("식사 시간이 아닙니다. Job 종료.");
                    return RepeatStatus.FINISHED;
                }

                // read
                String loginToken = loginClient.getOrRefreshLoginCookie();
                List<Menu> newMenus = diningClient.crawlCurrentDiningMenu(loginToken, LocalDateTime.now());
                List<Menu> oldMenus = menuRepository.findByDateAndType(
                    LocalDateTime.now().toLocalDate(),
                    DiningType.fromTime(LocalDateTime.now().toLocalTime()).name()
                );

                // process
                List<Menu> menus = MenuComparator.checkDuplicationMenu(oldMenus, newMenus);

                // write
                if (!menus.isEmpty()) {
                    JdbcBatchItemWriter<Menu> writer = new JdbcBatchItemWriterBuilder<Menu>()
                        .dataSource(dataDBSource)
                        .sql("""
                              INSERT INTO koin.dining_menus(date, type, place, price_card, price_cash, kcal, menu, image_url, is_changed)
                              VALUES (:date, :type, :place, :priceCard, :priceCash, :kcal, :menu, :imageUrl, :isChanged)
                              ON DUPLICATE KEY UPDATE
                                price_card = :priceCard,
                                price_cash = :priceCash,
                                kcal = :kcal,
                                menu = :menu,
                                is_changed = :isChanged
                            """)
                        .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                        .build();
                    writer.afterPropertiesSet();
                    writer.write(new Chunk<>(menus));
                    log.info("식단 정보가 변경되어 DB를 업데이트했습니다.");
                }

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    /**
     * 현재 식사시간 메뉴 크롤링
     * - 스케줄러에 의해 주기적으로 실행됨
     */
    @Bean
    public Job mealMenuPollingJob() {
        return new JobBuilder("mealMenuPollingJob", jobRepository)
            .start(crawlCurrentMenuStep())
            .build();
    }
}

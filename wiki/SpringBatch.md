# Spring Batch
>
> Spring Batch란 로깅/추적, 트랜잭션 관리, 작업 처리 통계, 작업 재시작, 건너뛰기, 리소스 관리 등 **대용량 레코드 처리에 필수적인 기능을 제공**한다.  
> 또한 최적화 및 파티셔닝 기술을 통해 대용량 및 고성능 배치 작업을 가능하게 하는 고급 기술 서비스 및 기능을 제공한다.  

- 사용 이유  
  이 프로젝트에서는 일정한 시간에 **모든 유저의 리뷰**를 조회해서 비즈니스 로직을 처리하기 때문에 Spring Batch를 적용하였다.

```java
// multi module에서 Spring Batch application 설정

@EnableScheduling   // 스케줄링 사용   
@EnableBatchProcessing    // Batch 사용하기 위해 설정
@ComponentScan({"com.ssafy.core"})  // core 모듈에 있는 component scan
@ComponentScan({"com.ssafy.batch"})
@EntityScan("com.ssafy.core")
@EnableJpaRepositories("com.ssafy.core")
@SpringBootApplication
public class SpringBatchApplication {

    public static void main(String[] args) {
        System.setProperty("spring.config.name",
                "application,application-real,application-aws,application-coolsms,application-login,application-pay");
        SpringApplication.run(SpringBatchApplication.class, args);
    }

}

```

### Job

- **Batch 처리 과정을 하나의 단위로 만들어 놓은 객체**이며 Batch 처리 과정에 있어 전체 계층 최상단에 위치하고 있다.

### JobScheduler

- 하나의 Batch job이 실행되면 JobInstance가 생기고, 생겨난 JobInstance는 JobName + JobParameters로 식별된다.
- 이를 통해 정상적으로 실행이 성공되면 해당 JobInstance는 다시 실행할 수 없기 때문에  
  JobParameters에 현재 시간을 넣어 중복을 없앤다.

```java
// cron 표현식 = (초 분 시 일 월 요일 년)
@Scheduled(cron = "0 0 0 * * ?")        // 매일 자정에 실행
public void statusRun() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
    JobParameters jobParameters = new JobParameters(
            Collections.singletonMap("requestTime", new JobParameter(System.currentTimeMillis()))
    );

    try {
        jobLauncher.run(jobRegistry.getJob("changeReservingJob"), jobParameters);
    } catch (NoSuchJobException e) {
        log.info("changeReservingJob 를 찾을 수 없습니다.");
        throw new RuntimeException(e);
    }
}
```
<a href="https://khj93.tistory.com/entry/Spring-Batch%EB%9E%80-%EC%9D%B4%ED%95%B4%ED%95%98%EA%B3%A0-%EC%82%AC%EC%9A%A9%ED%95%98%EA%B8%B0">출처</a>

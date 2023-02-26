# QueryDSL

## QueryDSL 사용 이유 및 장점

1. 문자가 아닌 코드로 쿼리를 작성함으로써, 컴파일 시점에서 문법 오류를 확인할 수 있다.
2. 자동 완성 등 IDE의 도움을 받을 수 있다.
3. 동적인 쿼리 작성이 편리하다.
4. 쿼리 작성 시 제약 조건 등을 메서드 추출을 통해 재사용할 수 있다.  
<a href="https://tecoble.techcourse.co.kr/post/2021-08-08-basic-querydsl/">출처</a>

> **querydsl 설정은 버전에 따라 적용 방식에 차이가 있다**고 하여 적용하는데 시간을 꽤나 소비하였다.  
검색을 통해 정리된 내용을 가져와서 적용해봐도 Q클래스가 생성되지 않았었다.  
그래서 관련 내용들을 계속 찾아서 적용해보고 다른 프로젝트를 생성하여 적용도 해보았지만 해당 프로젝트에 Q클래스가 생성이 되지 않아 해당 이슈를 팀원들과 공유하였고 **기존 build 내역을 비우고 프로젝트를 rebuild함으로써 Q클래스를 생성하여 적용할 수 있었다**.

```java
// querydsl 설정 
// EntityManager를 등록해주어야 한다.
@Configuration
public class QueryDSLConfiguration {

    @PersistenceContext
    EntityManager entityManager;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

}
```

- querydsl 사용 예시

```java
public List<CategoriesQdslDto> findCategoriesByPhotographerId(Long photographerId){
    QPhotographerNCategories photographerNCategories = QPhotographerNCategories.photographerNCategories;
    QCategories categories = QCategories.categories;

    return jpaQueryFactory
            .select(Projections.constructor(CategoriesQdslDto.class, categories.id, categories.name, photographerNCategories.price, photographerNCategories.description))
            .from(categories)
            .join(photographerNCategories).on(categories.eq(photographerNCategories.category))
            .where(photographerNCategories.photographer.id.eq(photographerId))
            .fetch();
}
```

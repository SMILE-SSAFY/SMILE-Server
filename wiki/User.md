# 회원가입 및 로그인

## Spring Security, JWT

### why Security

- spring security는 보안과 관련해서 체계적으로 많은 옵션을 제공해주기 때문에 개발자 입장에서는 일일이 보안 관련 로직을 작성하지 않아도 되는 장점이 있기 때문이다.  
  <a href="https://codingdiary99.tistory.com/entry/kotlin-01-Spring-security-Jwt-%EB%A1%9C%EA%B7%B8%EC%9D%B8">출처</a>

### why JWT

- 토큰 기반 인증을 사용하는 이유는 **확장성**에 있다.  
  일반적으로 웹 어플리케이션의 <u>서버 확장 방식은 수평 확장을 사용</u>한다.  
  (수평 확장 : 여러 대의 서버를 추가 설치)  
  이 때 별도의 작업을 해주지 않으면, 세션 기반 인증 방식은 세션 불일치 문제가 발생한다.
  하지만 토큰 기반 인증 방식은 클라이언트가 저장하기 때문에 **세션 불일치 문제로부터 자유롭다**.
  또한, 이런 특징으로 **HTTP의 Stateless 특성을 그대로 활용**할 수 있고 높은 확장성을 가질 수 있다.
  (HTTP의 Stateless : 서버는 클라이언트의 상태를 저장하지 않으며, 이전 요청과 다음 요청의 맥락이 이어지지 않는다.)  
- 클라이언트가 인증 데이터를 직접 가지고 있기 때문에 서버에 세션 데이터를 저장하는 세션 기반 인증 방식에 비해 **서버의 부담이 없다.**  
  <a href="https://hudi.blog/session-based-auth-vs-token-based-auth/">출처</a>

<br>

### Security, JWT 흐름

1. 사용자가 인증을 요청한다.
2. `JwtAuthenticationFilter`에서 jwt 토큰에 대한 처리를 한다.
3. `resolveToken(request)`에서 토큰 값을 읽어온다.
4. `validateToken(token)`에서 토큰의 유효성 검사한다.
5. `getAuthentication(token)`에서 Authentication 객체 반환한다.
6. `SecurityContextHolder`에 Authentication 객체 저장한다.

- SecurityContextHolder란?  
    SecurityContext 객체를 저장하고 감싸고 있는 클래스로 현재 보안 컨텍스트에 대한 세부 정보가 저장된다.  
- SecurityContext란?
  Authentication 객체가 저장되는 보관소로 필요 시 언제든지 Authentication 객체를 꺼내어 쓸 수 있도록 제공되는 클래스이다.  
<a href="https://catsbi.oopy.io/f9b0d83c-4775-47da-9c81-2261851fe0d0">출처</a>

<br>

> User

- UserDetails를 상속받아서 오버라이드를 해준다. 설명은 code에 명시하였다.
    <details>
    <summary>code</summary>

    ```java
    @Entity
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Table(name = "member")
    public class User implements UserDetails {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private Role role;

        /**
        * 유저 role을 통해 권한 생성
        *
        * @return authorities  // 권한 리턴
        */
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            Collection<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(Role.USER.getName()));
            return authorities;
        }

        /**
        * 유저 email 반환
        *
        * @return email
        */
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @Override
        public String getUsername() {
            return this.email;
        }

        /**
        * 유저 password 반환
        *
        * @return password
        */
        @Override
        public String getPassword(){
            return this.password;
        }

        /**
        * 계정의 만료 여부 리턴
        * true 만료 안됨
        * false 만료
        *
        * @return true
        */
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        /**
        * 계정의 잠김 여부 리턴
        * true 잠기지 않음
        * false 잠김
        *
        * @return true
        */
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        /**
        * 비밀번호 만료 여부 리턴
        * true 만료 안됨
        * false 만료
        *
        * @return true
        */
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        /**
        * 계정의 활성화 여부 리턴
        * true 활성화
        * false 비활성화
        *
        * @return true
        */
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        @Override
        public boolean isEnabled() {
            return true;
        }

    }


    ```

    </details>

<br>

> SecurityConfig
>

```java
@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 비밀번호를 암호화한다.
     *
     * @return
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 해당 uri에 대한 필터 적용 제외
     *
     * @return
     */
    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring().mvcMatchers(
                "/h2-console/**",
                "/favicon.ico"
        );
    }

    /**
     * security 설정
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("[filterChain] 접근");
        http
                .authorizeRequests()
                // 회원가입, 로그인, 휴대폰 인증, 중복 확인, 예외 처리 허용
                .antMatchers("/api/user/register", "/api/user/login", "/api/user/sns").permitAll()
                .antMatchers( "/api/user/check/**").permitAll()
                .antMatchers("/send-one").permitAll()
                .antMatchers("/exception/**").permitAll()
                .anyRequest().authenticated() // 위의 접근이 아니면 권한이 있어야 한다.
                .and()

                // csrf 해제
                .httpBasic().disable() // Http basic Auth  기반으로 로그인 인증창이 뜸.  disable 시에 인증창 뜨지 않음. 
                .formLogin().disable()  // custom login 사용
                .cors().disable()  // cors 설정 해제
                .csrf().disable()  // rest api이므로 csrf 보안이 필요없으므로 disable처리.

                // h2 db 접근 허용
                .headers()
                .frameOptions()
                .sameOrigin()
                .and()

                // session 정책 설정(session 사용 안 함)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 인증이 실패했을 때
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())

                // 권한이 없는 사용자가 접근할 때
                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())

                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 넣는다
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
        ;


        return http.build();

    }
}
```

- 세션 설정  

  ```java
  .sessionManagement()
  .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
  ```

  **생성된 계정으로 서버로부터 인증을 받게 되면 서버에 하나의 세션이 생성**된다. 그리고 다른 브라우저, 또는 다른 PC에서 같은 계정으로 로그인을 할 경우 이전에 서버에 만들어진 세션을 같이 공유하는 것이 아닌 새로운 세션을 생성하여 사용하게 된다.  
  그러나 <u>여기서는 JWT를 활용하여 세션을 사용하지 않기 때문에</u>
  `SessionCreationPolicy.STATELESS` 으로 설정한다.  
  <a href="https://velog.io/@seongwon97/Spring-Security-세션-관리">[출처] 세션 관리</a>

<br>

### Role 설정

- security 구현 방법에 대해 찾아보니 Role을 List 형태로 선언하여 User가 여러 개의 역할을 가진 것으로 구현되었지만 이 프로젝트에서는 **User가 하나의 Role만 가지면 되기 때문에** 이 부분을 Custom하게 처리하기 위해서 Role의 쓰임이 어떻게 되는지 내부 흐름을 찾아보고 따라가면서 이해하였고 이를 통해 Role을 구현하였다.

- 또한, 이 프로젝트에서는 Role 권한을 체크하지 않았지만 Security 구현 방법을 찾아보면 Role에 ROLE_을 붙여서 사용되는 것을 볼 수 있는데(ex. ROLE_USER) 이것은 SecurityConfig에서
    ```java
    // 예시
    .antMatchers("/api/user/**").hasRole("USER")
    ```
    을 사용하면 내부적으로 `ROLE_`을 붙여서 권한이 있는지 검사한다.

    만약 `ROLE_`를 사용하지 않고 권한 검사를 하고 싶다면
    ```java
    .antMatchers("/api/user/**").hasAuthority("USER")
    ```
    을 사용하면 된다.

<br>

### Filter 예외 처리

- api 테스트를 하면서 `JwtAuthenticationFilter`에서 발생하는 예외를 처리하지 못하는 이슈를 발견하였고 확인해보니 <u>servlet filter에서 발생한 예외를 처리하기 위해서는 예외를 catch하여 따로 설정이 필요하였다.</u>  

    <details>
    <summary>code</summary>

    ```java
    // JwtAuthenticationFilter.class

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            log.info("[doFilter] token: {}", token);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("------------------ SecurityContextHolder auth 등록");
            }
        } catch (Exception e) {
            log.info("[Filter] error");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), ErrorResponseEntity.toResponseEntity(ErrorCode.INTER_SERVER_ERROR).getBody());
        }
        chain.doFilter(request, response);
    }
    ```

    </details>

    <a href="https://codingdog.tistory.com/entry/spring-security-filter-exception-%EC%9D%84-custom-%ED%95%98%EA%B2%8C-%EC%B2%98%EB%A6%AC%ED%95%B4-%EB%B4%85%EC%8B%9C%EB%8B%A4">출처</a>

<br>

### JWT

<details>
  <summary>JWT란</summary>
  <div markdown="1">

- 권한 인가(Authorization)를 위해 사용하는 토큰

### 구조

  Header.Payload.Signature

  > Header
  >
- 토큰의 타입과 알고리즘(변조 방지를 위한)을 지정하는 정보
- **Base64(Url Safe) Encode**를 이용해 값을 생성한다.
- Base64 Decode를 하여 값을 확인한다.

      ```
      eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9
      {"typ":"JWT","alg":"HS256"}
      
      typ = 타입
      alg = 사용한 알고리즘 ( HS256 = HMAC-SHA256 )
      ```

  > Payload
  >
- **토큰에 담을 정보**가 들어간다.
- 정보의 한 뭉치를 **클레임(claim)**이라고 한다.
- claim은 key-value로 이루어져 있다.
- 클레임의 종류는 **등록된 클레임**, **공개 클레임**, **비공개 클레임**으로 나누어져 있다.
- **Base64(Url Safe) Encode**를 이용해 값을 생성한다.
- 따라서 Base64 Decode를 하면 값을 확인할 수 있다.

  ```
  eyJpc3MiOiJ0ZXN0IiwiaWF0IjoxNjM5OTg1NTg5LCJleHAiOjE2NDAwNzE5ODksInN1YiI6ImhlbGxvIEp3dCJ9
  {"iss":"test","iat":1639985589,"exp":1640071989,"sub":"hello Jwt"}

  iss = 발급자 (issuer)
  iat = 토큰 발급 시간 (issued at)
  exp = 토큰 만료 시간 (expiraton)
  sub = 토큰 제목 (subject)

  그 외에도
  aud(대상), nbf(활성 일자), jti(고유식별자)가 존재하며
  사용자 맘대로 key-value 형태로 저장할 수도 있다. -> 비공개 클레임
  ```

### Claim

  [등록된 (registered) 클레임]

  등록된 클레임들은 서비스에서 필요한 정보들이 아닌, **토큰에 대한 정보들을 담기 위하여 이름이 이미 정해진 클레임들**이다.

  ```
  iss = 발급자 (issuer)
  iat = 토큰 발급 시간 (issued at)
  exp = 토큰 만료 시간 (expiraton)
  sub = 토큰 제목 (subject)
  aud = 대상 (audience)
  nbf = 활성 일자 (Not Before)
  jti = 고유식별자, 중복적인 처리를 방지하기 위하여 사용, 따라서 일회용 토큰에 사용하면 유용하다.
  ```

  </div>
</details>

<br>

> JwtAuthenticationFilter

- 해당 필터에서 토큰에 대한 유효성을 검증하고 Authentication(User 객체)를 만들어서 SecurityContextHolder에 등록한다.  

    <details>
    <summary>code</summary>

    ```java
    public class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtTokenProvider jwtTokenProvider;

        // Jwt Provider 주입
        public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
            this.jwtTokenProvider = jwtTokenProvider;
        }


        /**
        * Request로 들어오는 Jwt Token의 유효성을 검증(jwtTokenProvider.validateToken)하는 filter를 filterChain에 등록합니다.
        *
        * @param request  The request to process
        * @param response The response associated with the request
        * @param chain    Provides access to the next filter in the chain for this
        *                 filter to pass the request and response to for further
        *                 processing
        *
        * @throws ServletException
        * @throws IOException
        */
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
            String token = jwtTokenProvider.resolveToken(request);
            log.info("[doFilter] token: {}", token);
            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("------------------ SecurityContextHolder auth 등록");
            }
            chain.doFilter(request, response);
        }
    }
    ```

    </details>

<br>

> JwtTokenProvider

- JWT 토큰 생성 관련 클래스

    <details>
    <summary>code</summary>

    ```java
    public class JwtTokenProvider {


        @Value("${spring.jwt.secret}")
        private String secretKey;

        // 유효기간 30일
        private long tokenValidMilisecond = 1000L * 60 * 60 * 24 * 30;

        private final UserDetailsService userDetailsService;

        /**
        * secreat 키 초기화
        */
        @PostConstruct
        protected void init() {
            secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        }

        /**
        * 유저 인덱스와 role 기반으로 jwt토큰 생성
        *
        * @param id
        * @param role
        * @return
        */
        public String createToken(String id, String role) {
            Claims claims = Jwts.claims().setSubject(id);
            claims.put("role", role);
            claims.put("id", id);
            Date now = new Date();

            return Jwts.builder()
                    .setClaims(claims)
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + tokenValidMilisecond))
                    .signWith(SignatureAlgorithm.HS256, secretKey)
                    .compact();
        }

        /**
        * 사용자 정보와 권한을 담은 Authentication 반환
        *
        * @param token
        * @return authentication
        */
        public Authentication getAuthentication(String token) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserIdx(token));
            return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
        }

        /**
        * 토큰에서 유저 idx 추출
        *
        * @param token
        * @return 유저idx
        */
        public String getUserIdx(String token) {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
        }

        /**
        * Request Header 에서 토큰 정보 추출
        *
        * @param request
        * @return jwt token
        */
        public String resolveToken(HttpServletRequest request) {
            String bearerToken = request.getHeader("Authorization");
            log.info("bearerToken : {}", bearerToken);
            if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
                log.info("token : {}", bearerToken.substring(7));
                return bearerToken.substring(7);
            }
            return null;
        }

        /**
        * 유효값 검증
        *
        * @param jwtToken
        * @return  유효값 검증 boolean
        */
        public boolean validateToken(String jwtToken) {
            try {
                Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
                return !claims.getBody().getExpiration().before(new Date());
            } catch (Exception e) {
                return false;
            }
        }

    }
    ```

    </details>

---

## Kakao 소셜 로그인

### OAuth2.0 의 장점

1. 회원가입 및 로그인 과정을 간소화하여 진행할 수 있다.
2. 보안 수준을 알 수 없는 애플리케이션에서 일일이 계정을 만들면 ID/PW 관리가 어렵고 개인정보 유출 시 연쇄적 피해가 심각하지만 보안 수준이 어느 정도 검증된 사이트의 API를 이용해서 인증을 받으면 보안상 장점이 있다.
3. 회원 정보뿐만 아니라 기타 API에 대한 정보에도 접근이 가능하다.

OAuth2.0 프로토콜을 이용하는 방식에는 3가지가 있다고 한다.

> 1. [WEB] 백엔드에서 Authentication Server와 Resource Server 모두 통신하여 프론트에게 JWT 토큰만 던져주는 방식
> 2. [WEB] Authentication server에서 프론트에게 바로 access token을 주는 것이 아니라 Authorization code만 넘겨주고 해당 code를 받은 백엔드가 access/refresh token을 받는 방식
> 3. [ANDROID] 프론트가 Access Token까지 받은 뒤 백엔드에서 해당 토큰을 기반으로 Resource 서버와 통신하는 방식
>
<a href="https://velog.io/@sophia5460/Spring-Boot-OAuth-2.0-JWT">출처</a>

이 프로젝트에서는 모바일 안드로이드와 통신하는 3번의 방식으로 적용하였다.

- 앱에서는 redirect url을 받을 수 없어서 인가 코드를 받을 수 없다.  
  그렇기 때문에 앱 측에서 카카오로 로그인을 통해 access token을 발급 받아 서버로 전달한다.
  
<details>
<summary>code</summary>

```java
    /**
    * 카카오 로그인을 통해 회원가입 하고 로그인하여 jwt 토큰을 발행한다.
    *
    * @param accessToken    // 클라이언트로부터 받은 accessToken
    * @param fcmToken       // fcm 알림 때 쓰이는 토큰
    * @return 카카오로부터 받은 정보로 회원 가입 후 로그인 진행하여 jwt 토큰 리턴
    */
public TokenRoleDto kakaoLogin(String accessToken, String fcmToken) {
    log.info("accessToken : {}", accessToken);
    ResponseEntity<String> profileResponse = getKakaoProfileResponse(accessToken);

    KakaoProfileDto kakaoProfileDto = setKakaoProfile(profileResponse);
    log.info("카카오 아이디(번호) : {}", kakaoProfileDto.getId());
    log.info("카카오 닉네임 : {}", kakaoProfileDto.getProperties().getNickname());
    log.info("카카오 이메일 : {}", kakaoProfileDto.getKakao_account().getEmail());

    RegisterFormDto registerFormDto = RegisterFormDto.builder()
            .email(kakaoProfileDto.getKakao_account().getEmail())
            .password(kakaoPassword)
            .name(kakaoProfileDto.getProperties().getNickname())
            .phoneNumber("11112345678")     // 고객의 휴대폰 정보를 받기 위해서는 사업자 등록을 요구하기 때문에 임의의 번호 지정
            .fcmToken(fcmToken)
            .build();

    return registerUser(registerFormDto);
}

    /**
    * 모바일에서 넘겨준  access 토큰을 통해 회원 정보가 담긴 response를 받는다.
    *
    * @param accessToken
    * @return 요청한 회원 정보가 담긴 response 반환
    */
public ResponseEntity<String> getKakaoProfileResponse(String accessToken) {
    // POST방식으로 key=value 데이터를 요청(카카오쪽으로)
    RestTemplate rt = new RestTemplate();
    rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());

    //HttpHeader 오브젝트 생성
    HttpHeaders headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + accessToken);
    headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

    // HttpHeader와 HttpBody를 하나의 오브젝트에 담기
    HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

    // Http 요청하기 - Post 방식으로 - 그리고 response 변수의 응답 받음.
    return rt.exchange(
            "https://kapi.kakao.com/v2/user/me",
            HttpMethod.POST,
            kakaoProfileRequest,
            String.class
    );
}

    /**
    * 회원정보가 담긴 response를 객체화하여 kakaoProfileDto에 담는다.
    *
    * @param response // 회원 정보가 담긴 response
    * @return 회원정보가 담긴 dto 반환
    */
public KakaoProfileDto setKakaoProfile(ResponseEntity<String> response) {
    ObjectMapper objectMapper = new ObjectMapper();
    KakaoProfileDto kakaoProfileDto = null;
    try {
        kakaoProfileDto = objectMapper.readValue(response.getBody(), KakaoProfileDto.class);
    } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
    }
    return kakaoProfileDto;
}
```

</details>

<details>
<summary>KakaoProfileDto</summary>

```java
// 카카오에서 제공하는 유저 정보에 대한 Dto
@Data
public class KakaoProfileDto {

    public Long id;
    public String connected_at;
    public Properties properties;
    public KakaoAccount kakao_account;

    @Data
    public class Properties {
        public String nickname;
    }

    @Data
    public class KakaoAccount {
        public boolean profile_nickname_needs_agreement;
        public Profile profile;
        public Boolean has_email;
        public boolean email_needs_agreement;
        public boolean is_email_valid;
        public boolean is_email_verified;
        public String email;

        @Data
        public class Profile {
            public String nickname;
        }

    }

}
```

</details>

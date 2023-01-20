package com.ssafy.api.controller;

import com.ssafy.api.config.security.jwt.JwtTokenProvider;
import com.ssafy.api.dto.User.LoginUserDto;
import com.ssafy.api.dto.User.MessageFormDto;
import com.ssafy.api.dto.User.RegisterFormDto;
import com.ssafy.api.dto.User.TokenRoleDto;
import com.ssafy.api.dto.User.UserDto;
import com.ssafy.api.service.UserService;
import com.ssafy.core.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 유저 관련 Controller
 * author @서재건
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private String apiKey;
    private String apiSecret;
    private String fromNumber;

    final DefaultMessageService messageService;

    @Autowired
    public UserController(
            UserService userService,
            JwtTokenProvider jwtTokenProvider,
            @Value("${coolsms.smile.apiKey}") String apiKey,
            @Value("${coolsms.smile.apiSecret}") String apiSecret,
            @Value("${coolsms.smile.fromNumber}") String fromNumber
    ) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        this.fromNumber = fromNumber;
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        // 반드시 계정 내 등록된 유효한 API 키, API Secret Key를 입력해주셔야 합니다!
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    /**
     * 토큰으로부터 userId를 추출
     *
     * @param request
     * @return userId 값 리턴
     */
    @GetMapping("/userId")
    public Long getUserId(HttpServletRequest request) {
        String token = jwtTokenProvider.resolveToken(request);
        log.info(token);
        log.info(jwtTokenProvider.getUserIdx(token));
        String userId = jwtTokenProvider.getUserIdx(token);

        return Long.valueOf(userId);
    }

    /**
     * 회원가입 후 로그인 진행
     *
     * @param registerFormDto // email, password, name, nickname, phoneNumber
     * @return token    // login을 통한 jwt token 리턴
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenRoleDto> register(@RequestBody @Valid RegisterFormDto registerFormDto) {
        TokenRoleDto tokenRoleDto = userService.registerUser(registerFormDto);
        return ResponseEntity.ok().body(tokenRoleDto);
    }

    /**
     * 로그인
     *
     * @param loginUserDto // email, password
     * @return token    // jwt token 리턴
     */
    @PostMapping(value = "/login")
    public ResponseEntity<TokenRoleDto> login(@RequestBody LoginUserDto loginUserDto) {
        TokenRoleDto tokenRoleDto = userService.login(loginUserDto);
        return ResponseEntity.ok().body(tokenRoleDto);
    }

    /**
     * 이메일 중복 체크
     *
     * @param email
     * @return HttpStatus.OK    //
     */
    @GetMapping(value = "/check/email/{email}")
    public ResponseEntity<HttpStatus> checkEmail(@PathVariable String email) {
        userService.checkEmail(email);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /**
     * 난수 생성 후 수신인한테 문자 발송 및 난수 리턴
     *
     * @param phoneNumber // 수신인의 번호
     * @return randomNumber // 4자리 난수 리턴
     */
    @GetMapping("/check/phone/{phoneNumber}")
    public String sendMessage(@PathVariable String phoneNumber) {
        MessageFormDto messageFormDto = userService.createMessageForm(fromNumber, phoneNumber);
        String randomNumber = messageFormDto.getRandomNumber();

        // 메세지 전송
        SingleMessageSentResponse response = this.messageService.sendOne(new SingleMessageSendingRequest(messageFormDto.getMessage()));
        log.info("메세지 전송 완료");

        return randomNumber;
    }

    /**
     * 토큰에서 유저 정보 조회
     *
     * @return userDto
     * id, name, role
     */
    @GetMapping
    public UserDto getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User)authentication.getPrincipal();
        UserDto userDto = new UserDto();
        return userDto.of(user);
    }
}

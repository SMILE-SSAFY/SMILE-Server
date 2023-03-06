# FCM
> Firebase Cloud Messaging
- 무료로 메시지를 안정적으로 전송할 수 있는 교차 플랫폼 메시징 솔루션

## 동작 방식
1. 앱 설치 시 FCM 토큰을 발급 받음.
2. 로그인 시 FCM 토큰을 백엔드로 넘김.
3. 백엔드에서 DB에 저장해두고, 필요 시 알림을 요청.
> DB에 저장한 이유<br>
사용자가 예약 시 사진작가에게 알림을 보내야 했기 때문에 DB에 저장해두었다가 필요 시마다 찾아야했음.

## 동작하는 시간
- 사용자가 예약을 등록했을 때<br>
    -> 예약한 사진작가에게 예약 알림을 보냄.
- 사용자 혹은 사진작가가 예약 상태를 변경했을 때<br>
    -> 상대방에게 예약 알림을 보냄.

## 고려한 부분
1. 같은 아이디를 사용하는 사용자가 **여러 기기**에서 로그인한 경우<br>
    -> 사용자가 여러 토큰을 가질 수 있도록 설계
2. **같은 기기에서 다른 아이디**로 로그인한 경우<br>
    ->로그아웃 시 FCM 토큰 삭제 
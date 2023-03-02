# ​🧡📷스마일(​SMILE) - Server📷🧡

![스마일 로고](https://github.com/SMILE-SSAFY/.github/blob/main/image/logo.PNG.gif)

- SSAFY 8th PJT **Team D102**​ 🌞
- 프로젝트 기간 : `2023.01.03` ~ `2023.02.17`
- 구성원 : 김정은, 신민철, 서재건

<br>

# :green_book:​Contents

[1️⃣​ Specification](#one-specification)<br>
[2️⃣ ​Server Architecture](#two-server-architecture)<br>
[3️⃣​ Package Structure](#three-package-structure)<br>
[4️⃣​ ERD](#four-erd)<br>
[5️⃣​ API Document](#five-api-document)<br>
[6️⃣​ 핵심 기능 및 구현 방법 설명](#five-핵심-기능-구현-방법-설명)<br>
[7️⃣ Contributor](#seven-contributor)<br>

<br>

## ​:one:​ Specification

<table class="tg">
<tbody>
  <tr>
    <td><b>Architecture</b></td>
    <td>MVC</td>
  </tr>
<tr>
    <td><b>Design Pattern</b></td>
<td>Builder Pattern/Singleton Pattern</td>
</tr>
<tr>
    <td><b>DB</b></td>
<td>MySQL 8.0.23</td>
</tr>
<tr>
    <td><b>Dependency Injection</b></td>
<td>Gradle 7.6</td>
</tr>
<tr>
    <td><b>Strategy</b></td>
<td>Git Flow</td>
</tr>

<tr>
    <td><b>Third Party Library</b></td>
    <td> OAuth2, Kakao API, Google Cloud API, coolsms, AWS spring boot</td>

</tr>
<tr>
    <td><b>Other Tool</b></td>
<td>Notion, Slack</td>
</tr>
</tbody>
</table>

<br>


## 2️⃣ : Server Architecture

![img](https://github.com/SMILE-SSAFY/.github/blob/main/image/server_architecture.png)

<br>

## 3️⃣ : Package Structure

```
📦 backend-smile
 ┣ 📂 api-module
 ┃ ┗ 📂 src/main
 ┃ ┃ ┃ ┗ 📂 java
 ┃ ┃ ┃ ┃ ┗ 📂 com.ssafy.api
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 config
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 controller
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 dto
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 service
 ┃ ┃ ┃ ┗ 📂 resources
 ┃ ┃ ┃ ┃ ┗ 🐘 build.gradle
 ┣ 📂 batch-module
 ┃ ┗ 📂 src/main
 ┃ ┃ ┃ ┗ 📂 java
 ┃ ┃ ┃ ┃ ┗ 📂 com.ssafy.batch
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 config
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 dto
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 job
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 service
 ┃ ┃ ┃ ┗ 📂 resources
 ┃ ┃ ┃ ┃ ┗ 🐘 build.gradle
 ┣ 📂 core-module
 ┃ ┗ 📂 src
 ┃ ┃ ┣ 📂 main
 ┃ ┃ ┃ ┗ 📂 java
 ┃ ┃ ┃ ┃ ┗ 📂 com.ssafy.core
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 code
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 entity
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 exception
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 repository
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 service
 ┃ ┃ ┃ ┃ ┃ ┗ 📂 utils
 ┃ ┃ ┃ ┗ 📂 resources
 ┃ ┃ ┃ ┃ ┗ 🐘 build.gradle
 ┣ 📂 src
 ┃ ┣ 📂 main
 ┃ ┃ ┗ 📂 resources
 ┗ 🐘 build.gradle
```

<br>

## 4️⃣  : ERD

![img](https://github.com/SMILE-SSAFY/.github/blob/main/image/erd.png)

<br>

## 5️⃣ : API Document

<a href="https://documenter.getpostman.com/view/25240917/2s8ZDcxenB">👉 API Document</a>

<br>

## 6️⃣ : 핵심 기능 및 구현 방법 설명

```
👉 WIKI에 핵심 기능 구현 코드 및 방법 정리
```

[1. 멀티 모듈](https://github.com/SMILE-SSAFY/.github/wiki/5.5.1-%EB%A9%80%ED%8B%B0-%EB%AA%A8%EB%93%88)

[2. QueryDSL](https://github.com/SMILE-SSAFY/.github/wiki/5.5.2-QueryDSL)

[3. 회원관리](https://github.com/SMILE-SSAFY/.github/wiki/5.5.3-%ED%9A%8C%EC%9B%90%EA%B4%80%EB%A6%AC)

[4. 주변 작가 조회](https://github.com/SMILE-SSAFY/.github/wiki/5.5.4-%EC%A3%BC%EB%B3%80-%EC%9E%91%EA%B0%80-%EC%A1%B0%ED%9A%8C)

[5. 이미지를 포함한 게시글 업로드](https://github.com/SMILE-SSAFY/.github/wiki/5.5.5-%EC%9D%B4%EB%AF%B8%EC%A7%80%EB%A5%BC-%ED%8F%AC%ED%95%A8%ED%95%9C-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A0%80%EC%9E%A5)

[6. 주변 게시글 목록(클러스터링)](https://github.com/SMILE-SSAFY/.github/wiki/5.5.6-%EC%A3%BC%EB%B3%80-%EA%B2%8C%EC%8B%9C%EA%B8%80-%EB%AA%A9%EB%A1%9D-(%ED%81%B4%EB%9F%AC%EC%8A%A4%ED%84%B0%EB%A7%81))

[7. 추천 시스템](https://github.com/SMILE-SSAFY/.github/wiki/5.5.7-%EC%9E%91%EA%B0%80-%EC%B6%94%EC%B2%9C(%EC%B6%94%EC%B2%9C-%EC%8B%9C%EC%8A%A4%ED%85%9C))

[8. FCM](https://github.com/SMILE-SSAFY/.github/wiki/5.5.8-FCM)

[9. 스프링 배치](https://github.com/SMILE-SSAFY/.github/wiki/5.5.9-%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B0%B0%EC%B9%98)

<br>

## 7️⃣ : Contributor

```
👉 팀원 소개와 역할 분담
```


<table class="tg">
<tbody>
    <tr>
        <td>김정은</td>
        <td>신민철</td>
        <td>서재건</td>
    </tr>
    <tr>
        <td><a href="https://github.com/kjjee99">@kjjee99</a></td>
        <td><a href="https://github.com/ringcho">@ringcho</a></td>
        <td><a href="https://github.com/RUNGOAT">@RUNGOAT</a></td>
    </tr>
    <tr>
        <td><img src="https://github.com/SMILE-SSAFY/.github/blob/main/image/profile_jungeun.jpeg" width="300px"/></td>
        <td><img src="https://github.com/SMILE-SSAFY/.github/blob/main/image/profile_ppitibbaticuttie_minchul.jpeg" width="300px"/></td>
        <td><img src="https://github.com/SMILE-SSAFY/.github/blob/main/image/profile_jaegun.jpeg" width="300px"/></td>
    </tr>
    <tr>
        <td>Server Dev</td>
        <td>Server Dev</td>
        <td>Server Dev</td>
    </tr>
    <tr>
        <td>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/FCM.md">예약, 예약 알림</a><br>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/MultiModule.md">멀티 모듈 구성</a><br>
        작가관리<br>
        결제<br>
        마이페이지<br>
        서버 배포 환경 구축<br></td>
        <td>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/이미지를포함한데이터저장.md">게시글/리뷰등록</a><br>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/클러스터링.md">주변 게시글 목록</a><br>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/추천.md">작가 추천</a><br>
        포트폴리오/게시글 조회<br>
        마이페이지<br>
        <td>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/User.md">회원관리</a><br>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/주변작가조회.md">주변 작가 조회</a><br>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/SpringBatch.md">스프링 배치</a><br>
        작가/게시글 검색<br>
        마이페이지<br>
    </tr>
</tbody>
</table>

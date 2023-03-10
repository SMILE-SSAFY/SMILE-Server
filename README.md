# βπ§‘π·μ€λ§μΌ(βSMILE) - Serverπ·π§‘

![μ€λ§μΌ λ‘κ³ ](https://github.com/SMILE-SSAFY/.github/blob/main/image/logo.PNG.gif)

- SSAFY 8th PJT **Team D102**β π
- νλ‘μ νΈ κΈ°κ° : `2023.01.03` ~ `2023.02.17`
- κ΅¬μ±μ : κΉμ μ, μ λ―Όμ² , μμ¬κ±΄

<br>

# :green_book:βContents

[1οΈβ£β Specification](#one-specification)<br>
[2οΈβ£ βServer Architecture](#two-server-architecture)<br>
[3οΈβ£β Package Structure](#three-package-structure)<br>
[4οΈβ£β ERD](#four-erd)<br>
[5οΈβ£β API Document](#five-api-document)<br>
[6οΈβ£β ν΅μ¬ κΈ°λ₯ λ° κ΅¬ν λ°©λ² μ€λͺ](#five-ν΅μ¬-κΈ°λ₯-κ΅¬ν-λ°©λ²-μ€λͺ)<br>
[7οΈβ£ Contributor](#seven-contributor)<br>

<br>

## β:one:β Specification

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


## 2οΈβ£ : Server Architecture

![img](https://github.com/SMILE-SSAFY/.github/blob/main/image/server_architecture.png)

<br>

## 3οΈβ£ : Package Structure

```
π¦ backend-smile
 β£ π api-module
 β β π src/main
 β β β β π java
 β β β β β π com.ssafy.api
 β β β β β β π config
 β β β β β β π controller
 β β β β β β π dto
 β β β β β β π service
 β β β β π resources
 β β β β β π build.gradle
 β£ π batch-module
 β β π src/main
 β β β β π java
 β β β β β π com.ssafy.batch
 β β β β β β π config
 β β β β β β π dto
 β β β β β β π job
 β β β β β β π service
 β β β β π resources
 β β β β β π build.gradle
 β£ π core-module
 β β π src
 β β β£ π main
 β β β β π java
 β β β β β π com.ssafy.core
 β β β β β β π code
 β β β β β β π entity
 β β β β β β π exception
 β β β β β β π repository
 β β β β β β π service
 β β β β β β π utils
 β β β β π resources
 β β β β β π build.gradle
 β£ π src
 β β£ π main
 β β β π resources
 β π build.gradle
```

<br>

## 4οΈβ£  : ERD

![img](https://github.com/SMILE-SSAFY/.github/blob/main/image/erd.png)

<br>

## 5οΈβ£ : API Document

<a href="https://documenter.getpostman.com/view/25240917/2s8ZDcxenB">π API Document</a>

<br>

## 6οΈβ£ : ν΅μ¬ κΈ°λ₯ λ° κ΅¬ν λ°©λ² μ€λͺ

```
π WIKIμ ν΅μ¬ κΈ°λ₯ κ΅¬ν μ½λ λ° λ°©λ² μ λ¦¬
```

[1. λ©ν° λͺ¨λ](https://github.com/SMILE-SSAFY/.github/wiki/5.5.1-%EB%A9%80%ED%8B%B0-%EB%AA%A8%EB%93%88)

[2. QueryDSL](https://github.com/SMILE-SSAFY/.github/wiki/5.5.2-QueryDSL)

[3. νμκ΄λ¦¬](https://github.com/SMILE-SSAFY/.github/wiki/5.5.3-%ED%9A%8C%EC%9B%90%EA%B4%80%EB%A6%AC)

[4. μ£Όλ³ μκ° μ‘°ν](https://github.com/SMILE-SSAFY/.github/wiki/5.5.4-%EC%A3%BC%EB%B3%80-%EC%9E%91%EA%B0%80-%EC%A1%B0%ED%9A%8C)

[5. μ΄λ―Έμ§λ₯Ό ν¬ν¨ν κ²μκΈ μλ‘λ](https://github.com/SMILE-SSAFY/.github/wiki/5.5.5-%EC%9D%B4%EB%AF%B8%EC%A7%80%EB%A5%BC-%ED%8F%AC%ED%95%A8%ED%95%9C-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EC%A0%80%EC%9E%A5)

[6. μ£Όλ³ κ²μκΈ λͺ©λ‘(ν΄λ¬μ€ν°λ§)](https://github.com/SMILE-SSAFY/.github/wiki/5.5.6-%EC%A3%BC%EB%B3%80-%EA%B2%8C%EC%8B%9C%EA%B8%80-%EB%AA%A9%EB%A1%9D-(%ED%81%B4%EB%9F%AC%EC%8A%A4%ED%84%B0%EB%A7%81))

[7. μΆμ² μμ€ν](https://github.com/SMILE-SSAFY/.github/wiki/5.5.7-%EC%9E%91%EA%B0%80-%EC%B6%94%EC%B2%9C(%EC%B6%94%EC%B2%9C-%EC%8B%9C%EC%8A%A4%ED%85%9C))

[8. FCM](https://github.com/SMILE-SSAFY/.github/wiki/5.5.8-FCM)

[9. μ€νλ§ λ°°μΉ](https://github.com/SMILE-SSAFY/.github/wiki/5.5.9-%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B0%B0%EC%B9%98)

<br>

## 7οΈβ£ : Contributor

```
π νμ μκ°μ μ­ν  λΆλ΄
```


<table class="tg">
<tbody>
    <tr>
        <td>κΉμ μ</td>
        <td>μ λ―Όμ² </td>
        <td>μμ¬κ±΄</td>
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
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/FCM.md">μμ½, μμ½ μλ¦Ό</a><br>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/MultiModule.md">λ©ν° λͺ¨λ κ΅¬μ±</a><br>
        μκ°κ΄λ¦¬<br>
        κ²°μ <br>
        λ§μ΄νμ΄μ§<br>
        μλ² λ°°ν¬ νκ²½ κ΅¬μΆ<br></td>
        <td>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/μ΄λ―Έμ§λ₯Όν¬ν¨νλ°μ΄ν°μ μ₯.md">κ²μκΈ/λ¦¬λ·°λ±λ‘</a><br>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/ν΄λ¬μ€ν°λ§.md">μ£Όλ³ κ²μκΈ λͺ©λ‘</a><br>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/μΆμ².md">μκ° μΆμ²</a><br>
        ν¬νΈν΄λ¦¬μ€/κ²μκΈ μ‘°ν<br>
        λ§μ΄νμ΄μ§<br>
        <td>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/User.md">νμκ΄λ¦¬</a><br>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/μ£Όλ³μκ°μ‘°ν.md">μ£Όλ³ μκ° μ‘°ν</a><br>
        <a href="https://github.com/SMILE-SSAFY/SMILE-Server/blob/main/wiki/SpringBatch.md">μ€νλ§ λ°°μΉ</a><br>
        μκ°/κ²μκΈ κ²μ<br>
        λ§μ΄νμ΄μ§<br>
    </tr>
</tbody>
</table>

# βπ§‘π·μ€λ§μΌ(βSMILE)π·π§‘

![img](../wiki/image/logo.PNG)


- SSAFY 8th PJT **Team D102**β π
- νλ‘μ νΈ κΈ°κ° : `2023.01.03` ~ `2023.02.17`


<br>

# :green_book:βContents

[:one: Specification](#one-specification)<br>
[:two: Flow Chart](#two-flow-chart)<br>
[:three: ERD](#three-erd)<br>
[:four: WIKI](#four-wiki)<br>
[:five: Contributor](#six-contributor)<br>




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
    <td> OAuth2, Kakao API, Google Cloud API, coolsms, </td>

</tr>
<tr>
    <td><b>Other Tool</b></td>
<td>Notion, Slack</td>
</tr>
</tbody>
</table>

<br>

<br>

## :two: API Document

<a href="https://documenter.getpostman.com/view/25240917/2s8ZDcxenB">API Document</a>

<br>

## :three: ERD

![img](../wiki/image/erd.png)

## :four: Server Architecture


<br>

## :five: Package Structure

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



## :six: WIKI
```
π Team Rules(Git, Coding Convention) λ° νμλ‘
```
- [HOME](https://lab.ssafy.com/s08-webmobile4-sub1/S08P11D102/-/wikis/Home)
    - [1. Team Rules](https://lab.ssafy.com/s08-webmobile4-sub1/S08P11D102/-/wikis/1.-Team-Rules)
    - [2. Server Coding Convention]()
    - [3. νμλ‘]()


<br>


## :seven: Contributor

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
        <td><a href="https://github.com/sjk1052005">@sjk1052005</a></td>
    </tr>
    <tr>
        <td><img src="../wiki/contributor/profile_jungeun.jpg" width="300px"/></td>
        <td><img src="../wiki/contributor/profile_ppitibbaticuttie_minchul.jpg" width="300px"/></td>
        <td><img src="../wiki/contributor/profile_jaegun.jpg" width="300px"/></td>
    </tr>
    <tr>
        <td>Server Dev</td>
        <td>Server Dev</td>
        <td>Server Dev</td>
    </tr>
    <tr>
        <td>μκ° νλ‘ν, μμ½ κ΄λ ¨ κΈ°λ₯ κ΅¬ν / λ°°ν¬</td>
        <td>κ²μκΈ, λ¦¬λ·°, μκ° μΆμ² λ° ν΄λ¬μ€ν°λ§ κ΄λ ¨ κΈ°λ₯ κ΅¬ν</td>
        <td>λ‘κ·ΈμΈ/νμκ΄λ¦¬, νν°λ§, μ€νλ§ λ°°μΉ κ΄λ ¨ κΈ°λ₯ κ΅¬ν</td>
    </tr>
</tbody>
</table>



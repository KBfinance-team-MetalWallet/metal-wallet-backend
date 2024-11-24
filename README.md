# 👜 프로젝트 소개

### 암표방지 + 전자지갑 = 💪🏼강철지갑

<aside>
💡 '강철지갑'은 사용자가 QR로 결제하고 계좌를 확인할 뿐만 아니라, 티켓을 예매하고 안전하게 사용할 수 있는 플랫폼을 제공하는 서비스입니다.

</aside>

**🚩 프로젝트 기간**

---

**`2024.09.05 ~ 2024.10.16`**

### 코드 살펴보기 👀

 **FrontEnd**

https://github.com/KBfinance-team-MetalWallet/metal-wallet-frontend

### 둘러보기 👀

![Service Preview](https://github.com/user-attachments/assets/3ef0f7de-e949-4e3a-a94c-f7077045343b)

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;티켓 예매하기&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;티켓 사용하기&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;거래 내역 확인하기

## ✍️ 기획 의도

---

📌 암표 거래 방지를 위한 전자지갑 기반 티켓팅 플랫폼

주제 선정은 `4.**편리한 금융 생활을 위한 전자 지갑 서비스`를 선택하여 발전시켰습니다.**

전자 지갑 서비스를 직접 구현하기보다는 기존 서비스에 새로운 기능을 추가하는 방향으로 생각을 발전시켰습니다.

이 과정에서 전자 지갑의 핵심 기능인 사용자 식별에 주목하게 되었고, 이를 활용한 티켓 거래 플랫폼으로 아이디어를 확장하였습니다.

## 📑 설계하기

---

### 1. 컨벤션 ✅

[코딩 컨벤션](https://www.notion.so/72ce16a1040a466b911bbcd05920dcd3?pvs=21) 

- **코드 컨벤션 :** 코드를 작성할 때 개인마다 스타일이 다릅니다. 그렇기 때문에 미리 팀원들과 협의하여 어떤 방식으로 코드를 작성할 것인지 정리해놨습니다.
- **깃허브 규칙 :** 깃허브 커밋과 PR 규칙도 협업할 때 매우 중요한 역할을 합니다.

### 2. 와이어프레임 🖼️

![image](https://github.com/user-attachments/assets/cf79d7a4-985b-4763-9a4e-a05d6d7fd124)


### 3. ERD 📚

![image](https://github.com/user-attachments/assets/d2997b3a-e0f5-45cf-93b4-4d6134b61092)


### 4. API 명세서 📑

## 🏗 아키텍처

---

### **최종 아키텍처** 🎯
<table>
  <tr>
    <td align="center" width="50%">
      <b>Frontend</b><br/>
      <img src="https://github.com/user-attachments/assets/2769c606-f0c5-409c-8f24-06110e41837f" width="400px" />
    </td>
    <td align="center" width="50%">
      <b>Backend</b><br/>
      <img src="https://github.com/user-attachments/assets/0a217b5e-2b3a-4364-ab7d-694b21c810d2" width="400px" />
    </td>
  </tr>
</table>

### Frontend
- Github Actions을 통한 build → export 후 정적 파일(HTML, CSS, JS) 생성
- gh-pages를 통해 배포
- AWS S3 bucket에 정적 웹 사이트 호스팅

### Backend
- Local 개발 환경에서 코드 Push
- Github Actions를 통한 자동 빌드
- AWS EC2 인스턴스에 Spring Framework 애플리케이션 배포
- AWS RDS MySQL 데이터베이스 연동

## 🍀 주요 기술

---

**Back**

- **☕ JDK 17**
- **🚀 SpringFramework**
- **🔒 Spring Security**

**Infra**

- **🟩 Nginx**
- 🏋🏻 **Certbot**
- GithubActions
- **🌐 AWS** (IAM, EC2, S3)

**DB & Front**

- **MySQL**
- Redis
- Vue.js

## 🤓 기술적 의사결정

---

# 🛠 주요 기술 구현

## 1. 암표 방지
<details>
<summary><b>구현 방식 및 프로세스</b></summary>

![Anti-Scalping System](https://github.com/user-attachments/assets/b078e98f-91e6-4f70-bf54-547c4e848f4a)

### 구현 프로세스
1. 티켓 예매 단계
   - 사용자의 전자지갑 CI와 좌석 정보가 서버로 전송
   - 서버에서 비밀키를 통한 정보 암호화 및 저장

2. 티켓 검증 단계
   - QR 코드를 통해 서버는 미리 저장된 CI 값으로 검증
   - 서버만이 비밀키 보유로 정보 탈취 시에도 위조 여부 확인 가능
   - 티켓 불법 복제 원천 차단
</details>

<details>
<summary><b>도입 배경 및 장점</b></summary>

![Scalping Prevention Background](https://github.com/user-attachments/assets/8410d353-7476-4a1e-b143-33e59a6993d1)

### 기존 시스템의 한계
- 문화 활동의 암표 방지를 위한 정부와 기업의 노력
- 단순 신분증 확인 방식의 한계 존재

### RSA 도입 장점
1. 보안성 강화
   - 정보 탈취/위변조 시에도 판별 가능
   - 정확한 사용자 검증 가능
2. 전자 지갑 연동
   - 사용자 식별 보안 강화
   - 효율적인 인증 시스템 구축
</details>

## 2. 동시성
<details>
<summary><b>문제 상황</b></summary>

### 동시 예매 문제점
- 여러 명의 사용자가 같은 좌석을 동시에 티켓 예매 시 발생하는 이슈
  1. 티켓 중복 예매 발생
  2. 재고 없는 상태에서 예매 완료

### 영향
- 사용자의 성공적 예매 후 중복 예매 취소
- 부정적인 사용자 경험 초래
- 서비스 신뢰도 하락
</details>

<details>
<summary><b>문제 원인</b></summary>

### 트랜잭션 격리 수준 이슈
- **`REPEATABLE READ`** 격리 수준으로 인한 문제점:
  - 중복된 데이터 갱신 방지 불가
  - 다수의 트랜잭션이 동시에 가용 좌석으로 판단
  - DB 레코드 동시 업데이트로 데이터 일관성 훼손

### 초기 해결 시도
- Ticket의 외래키(좌석 id값)에 `UNIQUE` 제약 조건 설정
- 한계점:
  - 데드락 발생 가능성
  - 트랜잭션 재시도로 인한 성능 저하
  - 트랜잭션의 순차적 처리 필요성 대두

### 해결 방향
- Redis 분산 락 도입을 통한 동시성 제어
- 단일 사용자만 예약 성공하도록 설계
</details>

<details>
<summary><b>해결 방안</b></summary>

### 작동 원리
![image](https://github.com/user-attachments/assets/203369e5-ccf0-4d0f-a104-27d7d168d687)

- Redis의 Key-Value 구조 활용
- 좌석별 고유 락 관리
- 선착순 락 획득자만 예약 진행 가능
- 동시 요청 시 중복 예매 방지

### 개선 효과
#### 적용 전
![image](https://github.com/user-attachments/assets/dfa593ee-32e4-45e1-b57f-b61e9d9b4ce4)
- 4명의 사용자 중복 예매 발생
- 데이터 무결성 훼손

#### 적용 후
![image](https://github.com/user-attachments/assets/3de81b3d-6060-4957-b7f5-9fab88f822e2)
- 재고 초과 문제 해결
- 고부하 상황에서도 안정적인 티켓팅 처리
- 다중 서버 환경에서의 동시성 제어 가능성 확보

*Note: 테스트 코드 및 성능 개선 관련 내용 추가 예정*
</details>

## 3. 트래픽 관리
<details>
<summary><b>문제 상황</b></summary>

![Traffic Issue](https://github.com/user-attachments/assets/6937f6d1-8666-4319-abcc-eec833b5a770)

### 서비스 구조상 문제점
- 전자지갑과 티켓 서비스의 단일 서버 운영
- 특정 시간대 트래픽 집중으로 인한 과부하 위험
- 전체 서비스(전자지갑, 결제)에 영향을 미칠 수 있는 구조
</details>

<details>
<summary><b>해결 방안</b></summary>

![Solution Architecture](https://github.com/user-attachments/assets/29204c83-d922-4dc8-9557-915137bf6e50)

### 검토된 솔루션
1. **Nginx Reverse Proxy 방식**
   - Rate Bucket을 통한 외부 트래픽 관리
   - Spring 서버 앞단에서 효율적인 트래픽 분산
   - 티켓 서비스 부하 감소

2. **Spring 가상 대기열 방식**
   - 필터와 인터셉터 활용한 대기열 구성
   - 사용자 새로고침에도 대기열 순서 유지
   - 커스터마이징 가능

### 최종 선택: Nginx Reverse Proxy
- 시간적 효율성
- 향후 서버 이중화 및 로드밸런서 확장 용이성 고려

![Implementation Detail](https://github.com/user-attachments/assets/e4e20b68-dde6-4592-8e88-e0273765a2a5)

### 구현 상세
- Lua 스크립트를 통한 실시간 접수자 수 카운팅
- 동시 요청 모니터링 시스템 구축
- Bucket Head 구성 및 대기자 수 Counting 로직 구현

*Note: 현재는 기본적인 트래픽 관리 기능만 구현된 상태로, 추후 확장 예정*
</details>
## 🚨 트러블슈팅

---

<aside>
💡

 팀원이 어떤 문제를 겪었고 해결 했는지 알 수 있어요!

</aside>

[[MusicalRepository]테스트 코드](https://www.notion.so/MusicalRepository-13794635e71680a6b157c60807d2ffd4?pvs=21)

[티켓 부분 통합 테스트](https://www.notion.so/13794635e71680b8bf4dc2bdbae27a61?pvs=21)

[Test DB 설정](https://www.notion.so/Test-DB-13794635e71680fb8bfef4f4fb229c54?pvs=21)

[**RSA 암호화/복호화 문제 해결 회고: Vue와 Spring 통합 과정**](https://www.notion.so/RSA-Vue-Spring-13794635e71680219787f6d534692b51?pvs=21)

[N+1 문제 해결](https://www.notion.so/N-1-13794635e71680ffa8bfc73cea7d8704?pvs=21)

[[Repository] No ServletContext set 에러](https://www.notion.so/Repository-No-ServletContext-set-13794635e7168075b72bc6ecc314ef02?pvs=21)

[[Controller] @AuthenticationPrincipal에 대한 관리](https://www.notion.so/Controller-AuthenticationPrincipal-13794635e71680978b3fedbf5a8b3d73?pvs=21)

# 🤝 팀 소개 및 회고

---

# 👥 팀원 소개

| [손서원](https://github.com/seowonn) | [이준렬](https://github.com/lee-JunR) | [이현희](https://github.com/heegane) | [최민준](https://github.com/veniharuka) | [최한솔](https://github.com/chuseok) | [최호진](https://github.com/gentle-tiger) |
|:---:| :---: | :---: | :---: | :---: | :---: |
|![image](https://github.com/user-attachments/assets/778e702a-77b9-4d5e-9ecb-431bc52aa5b6)|![image](https://github.com/user-attachments/assets/0aecee06-4363-48da-ae27-adb73c321a17)|![image](https://github.com/user-attachments/assets/4e0a6e24-bb61-445d-b117-7e487da587fb)|![image](https://github.com/user-attachments/assets/3c3090a3-e0b9-439b-a16b-0506020b94ea)|![image](https://github.com/user-attachments/assets/c72046ed-c9d5-44ce-9bd3-2ebb24e2e008)|![image](https://github.com/user-attachments/assets/05a62117-2d66-499e-b9e2-9108549fb6e3)|

### 협업 도구 🛠️

---

- Github : 개발 및 일정관리
- Slack : 실시간 소통
- Notion : WBS를 이용한 일정관리 및 자료 관리
- Figma : 와이어프레임 작성
- ERDcloud : ERD 작성
- Google Docs : 실시간 기획 관련 문서 작성

## 📚 자료

---

- 최종 발표 ppt
    
    [10.16_강철지갑_최종발표.pdf](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/3b914b7c-1af7-4cbc-bedc-748cc0785624/10.16_%E1%84%80%E1%85%A1%E1%86%BC%E1%84%8E%E1%85%A5%E1%86%AF%E1%84%8C%E1%85%B5%E1%84%80%E1%85%A1%E1%86%B8_%E1%84%8E%E1%85%AC%E1%84%8C%E1%85%A9%E1%86%BC%E1%84%87%E1%85%A1%E1%86%AF%E1%84%91%E1%85%AD.pdf)
    
    [시연영상.mp4](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/c05b0e59-f70d-4a63-932b-cd3954429416/%EC%8B%9C%EC%97%B0%EC%98%81%EC%83%81.mp4)

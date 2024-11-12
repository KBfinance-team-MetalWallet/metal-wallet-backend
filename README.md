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

 **BackEnd**

https://github.com/KBfinance-team-MetalWallet/metal-wallet-backend

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/3032c7b1-27d6-432b-94b9-19285a38bcac/image.png)

티켓 예매하기

티켓 사용하기

거래 내역 확인하기

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

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/4b883345-f381-4669-8ec0-940b0aab6a1b/image.png)

### 3. ERD 📚

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/4eba287e-b94a-4e6e-b869-8b25e9659b8f/image.png)

### 4. API 명세서 📑

## 🏗 아키텍처

---

### **최종 아키텍처** 🎯

`FrontEnd`

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/e2bd3cbd-66e9-4551-8c42-eddee6d30f94/image.png)

`BackEnd`

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/2eefbeb8-fc99-40a7-a78e-7fe0e74c9bc9/image.png)

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

### 1. 암표방지

> 어떻게 구현했을까?
> 

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/f89fce8b-08cf-459c-98fd-c9943b9a39b7/image.png)

1. 사용자가 티켓을 예매할 때, 전자지갑의 CI와 좌석 정보가 서버로 전송된다.
2. 티켓 사용 시 QR 코드를 통해 서버는 미리 저장된 CI 값을 활용하여 검증을 수행한다. 이때 서버만이 비밀키를 보유하고 있어 중간에 정보가 탈취되더라도 위조 여부를 확인할 수 있다.

이를 통해 티켓 불법 복제를 원천적으로 차단할 수 있다.

> 왜 도입하게 되었을까?
> 

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/e8ad3f7d-cae9-4094-aca6-5077d6e73f00/image.png)

문화 활동의 암표 방지를 위해 정부와 기업에서 노력하고 있다. 하지만 단순 신분증 확인만으로는 한계가 있다는 것을 확인할 수 있었다.

1. 정보가 중간에 탈취되어 위/변조 되어도 그 여부 판별가능
2. 사용자 검증 가능

RSA는 위와 같은 장점을 갖고 있기에, 전자 지갑의 사용자 식별에 보안을 강화할 수 있을 것이라 생각하여 해당 기술을 도입했다.

### 2. 동시성

> 어떻게 구현했을까?
> 

### 3. 트래픽

**문제상황 :** 

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/aa0f3eb9-b0a5-442a-b675-1ead8c50c986/image.png)

우리 서비스는 전자지갑과 티켓 서비스가 동일한 서버에서 운영된다.

예매가 몰리는 특정 시간대에 트래픽이 집중되면서 서버에 과부하가 걸리면, 전자지갑 서비스와 결제 서비스 모두 영향을 받을 수 있기에 트래픽 처리 방안을 고려해야 했다.

**솔루션 검토 :** 

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/5b070eaf-b02a-4273-9138-b31712d63ffa/image.png)

1. Nginx Reverse Proxy를 통해 Rate Bucket을 설정하여 트래픽을 서버 외부에서 관리하는 방법.이를 통해 Spring 서버 앞단에서 트래픽을 효율적으로 분산하여 티켓 서비스의 부하를 줄일 수 있다.
2. Spring 서버를 하나 구현하여 가상 대기열을 만들어 관리하는 방법.필터와 인터셉터를 활용해 대기열을 구성할 수 있다.사용자의 새로고침에도 대기열 순서가 유지되게 커스텀할 수 있다는 장점이 있다.

시간적인 요인과 향후 서버 이중화 시 로드밸런서 등으로 확장이 가능한 Nginx를 선택했다.

![image.png](https://prod-files-secure.s3.us-west-2.amazonaws.com/0579513f-8bff-4034-8d2b-3f7209197aaa/73af5720-d216-40f5-99a6-f878ef8d81ae/image.png)

Nginx로 요청이 들어오면 Lua 스크립트를 통해 실시간으로 접수자 수를 counting하여 해당 요청이 동시에 얼마나 들어오는지를 체크하게 했다.

*물론 해당 로직만으로는 부족한 점이 많다. Bucket Head를 구성하고 이에 Lua 스크립트를 통해 대기자 수를 Counting 하는 로직뿐이다.*

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
| **Name** | **Position** | **Role** | **Github** |
| --- | --- | --- | --- |
| [손서원](https://github.com/seowonn) | Backend & Frontend | • [BE] QR 암호화, 티켓 생성 및 사용 로직<br>• [FE] 메인페이지, 공지 티켓 페이지 | [@seowonn](https://github.com/seowonn) |
| [이준렬](https://github.com/lee-JunR) | Backend & Frontend | • [BE] 글로벌 예외 처리, CI/CD, 트래픽 처리<br>• [FE] 메세 티켓 신청 페이지, 관리자 페이지 | [@lee-JunR](https://github.com/lee-JunR) |
| [이현희](https://github.com/heegane) | Backend & Frontend | • [BE] 회원가입 & 로그인, 예매 동시성 처리<br>• [FE] 뮤직픽 넥슨/서버 내장/어버버 페이지 | [@heegane](https://github.com/heegane) |
| [최민준](https://github.com/veniharuka) | Backend & Frontend | • [BE] QR 암호화, 티켓 생성 및 사용<br>• [FE] 티켓 서비스 페이지, 티켓 사용 | [@veniharuka](https://github.com/veniharuka) |
| [최한솔](https://github.com/chuseok) | Backend & Frontend | • [BE] QR 암호화, 티켓 생성 및 사용<br>• [FE] 홈 페이지, 티켓 내역 페이지 | [@chuseok](https://github.com/chuseok) |
| [최호진](https://github.com/gentle-tiger) | Backend & Frontend | • [BE] 계좌 생성 및 조회, 예매 동시성 처리<br>• [FE] 티켓 사용 페이지, 이벤트 날짜 신청 페이지 | [@gentle-tiger](https://github.com/gentle-tiger) |

**`KPT 회고`** 

[KPT 회고](https://www.notion.so/KPT-13794635e7168021b9cec11384c9b943?pvs=21)

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

# AGENTS.md — Antigravity 개발 지침서

이 문서는 **업무 말투 변환기 (BizTalk Antigravity)** 프로젝트를 개발할 때 Antigravity AI가 준수해야 하는 에이전트 전용 행동 수칙을 정의합니다. 상세한 제품 명세 및 완료 기준은 PRD 문서를 참조합니다.

---

## 1. 프로젝트 개요 및 참조 문서

- **프로젝트 명**: 업무 말투 변환기 (BizTalk Antigravity)
- **개발 기준 문서**: [PRD_업무말투변환기.md](file:///C:/skrookies6/vibeantigravity/biztalk_antigravity/PRD_업무말투변환기.md)
- **완료 체크리스트**: [PRD 2장 참조](file:///C:/skrookies6/vibeantigravity/biztalk_antigravity/PRD_업무말투변환기.md#2-완료-체크리스트)
- **디렉토리 구조 및 API 명세**: [PRD 6장/7장 참조](file:///C:/skrookies6/vibeantigravity/biztalk_antigravity/PRD_업무말투변환기.md#6-디렉토리-구조)

---

## 2. Antigravity 핵심 행동 수칙 (Rules for Antigravity)

### 2-1. 바이브 코딩 3원칙 준수 (PRD 1장 기반)
1. **완료 기준 우선 정의**: 코드를 작성하기 전 [PRD 완료 체크리스트](file:///C:/skrookies6/vibeantigravity/biztalk_antigravity/PRD_업무말투변환기.md#2-완료-체크리스트)를 확인하고, 명시된 기능 범위 내에서만 구현합니다. (로그인, DB 저장 등 범위 외 기능 임의 추가 금지)
2. **조사 우선, 구현 나중**: 새로운 모듈 설치나 API 연동 시 공식 문서 및 라이브러리 사용법을 사전 확인 후 작성합니다.
3. **버그 발생 시 원인 분석 우선**: 에러 발생 시 코드를 즉시 덮어쓰지 않고 원인을 파악/설명한 뒤 해결책을 제안합니다.

### 2-2. 개발 환경 및 보안 준수사항
- **기술 스택**: Python 3.11+ / `uv` / FastAPI / LangChain (`solar-pro3`) / Vanilla HTML, CSS, JS
- **가상환경 및 패키지 관리**: 패키지 관리자 `uv`를 사용하며, 로컬 테스트 및 실행 시 루트의 `.venv` 가상환경(`uv venv`, `uv pip install`, `uv run`)을 사용합니다.
- **보안 및 자격증명**:
  - API 키 등 민감 정보는 루트의 `.env` 파일로 관리합니다.
  - **`.env` 파일의 내용이나 비밀키 정보를 절대 출력하거나 수정하지 않으며**, Git에 포함되지 않도록 유지합니다.

### 2-3. 문서 최신화 및 완료 체크리스트 관리 수칙
- **문서 동기화**: 코드 변경, 요구사항 변경, 화면 명세 변경, 라이브러리 버전 변경 등 프로젝트 변경사항 발생 시 [PRD_업무말투변환기.md](file:///C:/skrookies6/vibeantigravity/biztalk_antigravity/PRD_업무말투변환기.md) 및 관련 Markdown 문서를 즉시 최신화합니다.
- **완료 체크리스트 동기화**: 기능 구현 완료 시 [PRD 2. 완료 체크리스트](file:///C:/skrookies6/vibeantigravity/biztalk_antigravity/PRD_업무말투변환기.md#2-완료-체크리스트)의 해당 항목을 `[x]`로 체크합니다.
- **단계별 구현 순서 동기화**: 단계별 구현 완료 시 [PRD 8. 단계별 구현 순서](file:///C:/skrookies6/vibeantigravity/biztalk_antigravity/PRD_업무말투변환기.md#8-단계별-구현-순서)의 해당 STEP 및 세부 항목을 `[x]`로 체크합니다.

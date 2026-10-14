---
name: code-review
description: 코드 품질 리뷰 요청 시 사용. "리뷰해줘", "코드 검토해줘" 라고 하면 자동 실행.
allowed-tools: Read Bash
---

# 코드 리뷰 스킬

아래 파일을 먼저 읽고 체크리스트를 파악해:
@checklist.md

그 다음 reviewer와 fixer sub-agent를 순서대로 실행해:

## 1단계 — reviewer (분석)
reviewer sub-agent에게 $1 파일을 분석하게 해.
checklist.md 기준으로 문제점 목록을 만들어서 review-report.md 에 저장.

## 2단계 — fixer (수정)
reviewer가 완료되면, fixer sub-agent에게 review-report.md 를 읽고
심각도 "높음" 항목만 실제 코드에서 수정하게 해.

## 완료 보고
- 발견된 문제 총 개수
- 자동 수정된 항목
- 수동 확인 필요한 항목
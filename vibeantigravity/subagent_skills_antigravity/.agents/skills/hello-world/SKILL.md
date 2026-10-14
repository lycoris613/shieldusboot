---
name: hello-world
description: writer와 tester sub-agent를 병렬 실행하는 hello world 예제. "hello world 실행해줘", "hello world 테스트해줘" 요청 시 실행.
---

writer와 tester sub-agent를 아래 순서로 실행해줘:

## 1단계 — writer 2개 병렬 실행

두 writer를 동시에 실행해:
- writer-1: output/hello_en.txt 에 "Hello, World!" 작성
- writer-2: output/hello_ko.txt 에 "안녕, 세계!" 작성

두 writer는 서로 다른 파일을 담당하므로 반드시 동시에 병렬로 실행.

## 2단계 — tester 실행 (writer 완료 후)

writer 2개가 모두 완료된 후 tester를 실행해:
- tester: output/hello_en.txt 와 output/hello_ko.txt 가 올바르게 생성됐는지 확인

## 완료 보고
- 생성된 파일 목록
- 각 파일 내용 확인 결과
- 전체 통과 / 실패 여부
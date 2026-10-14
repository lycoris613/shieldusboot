"""문제 1-2: 2단계 Multi Chain 만들기 - 여행지 정보 시스템"""
from dotenv import load_dotenv
from langchain_core.prompts import ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough
from langchain_openai import ChatOpenAI

load_dotenv()

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0.5)

# 1단계: 여행지 -> 대표 명소 1가지 (이름만)
prompt1 = ChatPromptTemplate.from_messages([
    ("system", "당신은 여행 전문가입니다. 질문받은 여행지의 대표 관광 명소를 딱 1곳만 추천하세요. 설명 없이 명소 이름만 출력하세요."),
    ("user", "여행지: {destination}"),
])

# 2단계: 명소 -> 상세 정보
prompt2 = ChatPromptTemplate.from_messages([
    ("system", "당신은 친절한 관광 가이드입니다. 아래 형식으로 한국어로 답하세요.\n"
               "명소: (이름)\n역사:\n(설명)\n특징:\n(설명)\n방문 팁:\n(설명)"),
    ("user", "다음 관광 명소에 대해 자세히 알려주세요: {attraction}"),
])

chain1 = prompt1 | llm | StrOutputParser()
chain2 = prompt2 | llm | StrOutputParser()

# LCEL 연결: 1단계 결과를 attraction 키로 넘기면서 1단계 결과도 함께 유지
full_chain = (
    RunnablePassthrough.assign(attraction=chain1)
    | RunnablePassthrough.assign(detail=chain2)
)

if __name__ == "__main__":
    for destination in ["로마", "뉴욕", "도쿄"]:
        result = full_chain.invoke({"destination": destination})
        print(f"입력: {destination}")
        print(f"1단계 결과: {result['attraction']}")
        print(f"2단계 결과:\n{result['detail']}")
        print("=" * 50)

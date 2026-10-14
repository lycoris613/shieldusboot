"""문제 1-1: 기본 Chain 만들기 - AI 요리사"""
from dotenv import load_dotenv
from langchain_core.prompts import PromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_openai import ChatOpenAI

load_dotenv()

prompt = PromptTemplate.from_template(
    """당신은 친절한 AI 요리사입니다.
사용자가 가진 재료: {ingredients}

이 재료로 만들 수 있는 요리를 하나 추천하고, 아래 형식으로 답하세요.

{ingredients}(으)로 만들 수 있는 요리를 추천드립니다!

추천 요리: (요리 이름)
재료: (사용 재료)
조리법:
1. ...
2. ...
3. ..."""
)

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0.7)
chain = prompt | llm | StrOutputParser()

if __name__ == "__main__":
    for ingredients in ["계란, 밥, 김치", "토마토, 양파, 치즈"]:
        print(f"입력: {ingredients}")
        print(chain.invoke({"ingredients": ingredients}))
        print("=" * 50)

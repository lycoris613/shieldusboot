"""문제 2-2: 영화 리뷰 감정 분석기 (EnumOutputParser + OutputFixingParser)"""
from enum import Enum

from dotenv import load_dotenv
from langchain_core.prompts import PromptTemplate
from langchain_classic.output_parsers import EnumOutputParser, OutputFixingParser
from langchain_openai import ChatOpenAI

load_dotenv()


class Sentiment(Enum):
    POSITIVE = "긍정"
    NEGATIVE = "부정"
    NEUTRAL = "보통"


llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)

parser = EnumOutputParser(enum=Sentiment)
# 선택: 분석 실패 시 LLM으로 출력을 고쳐서 다시 파싱
fixing_parser = OutputFixingParser.from_llm(parser=parser, llm=llm)

prompt = PromptTemplate(
    template=(
        "다음 영화 리뷰의 감정을 분류하세요.\n"
        "리뷰: {review}\n"
        "{format_instructions}"
    ),
    input_variables=["review"],
    partial_variables={"format_instructions": parser.get_format_instructions()},
)

# 모델 출력은 문자열로 받고, 파싱은 fixing_parser가 담당
chain = prompt | llm | fixing_parser

if __name__ == "__main__":
    reviews = [
        "이 영화 정말 재미없어요. 시간 낭비였습니다.",
        "배우들의 연기가 훌륭하고 스토리도 감동적이었어요!",
        "그냥 무난한 영화였습니다. 나쁘지도 좋지도 않아요.",
    ]
    for review in reviews:
        result = chain.invoke({"review": review})
        print(f"리뷰: {review}")
        print(f"감정: {result.value}\n")

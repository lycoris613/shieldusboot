"""문제 2-1: 콤마 구분 리스트 파서 활용 (CommaSeparatedListOutputParser)"""
from dotenv import load_dotenv
from langchain_core.prompts import PromptTemplate
from langchain_core.output_parsers import CommaSeparatedListOutputParser
from langchain_openai import ChatOpenAI

load_dotenv()

parser = CommaSeparatedListOutputParser()

prompt = PromptTemplate(
    template=(
        "'{field}' 분야와 관련된 한국의 유명한 장소나 활동 5가지를 추천하세요.\n"
        "반드시 실제로 존재하는 장소와 활동만 추천하고, 불확실하거나 지어낸 것은 포함하지 마세요.\n"
        "{format_instructions}"
    ),
    input_variables=["field"],
    partial_variables={"format_instructions": parser.get_format_instructions()},
)

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0.5)
chain = prompt | llm | parser

if __name__ == "__main__":
    field = input("관심 분야를 입력하세요 (예: 음식, 스포츠, 영화): ").strip() or "음식"
    print(chain.invoke({"field": field}))

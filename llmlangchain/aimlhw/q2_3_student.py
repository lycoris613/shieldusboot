"""문제 2-3: 학생 정보 구조화 시스템 (PydanticOutputParser)"""
from typing import List

from dotenv import load_dotenv
from langchain_core.prompts import PromptTemplate
from langchain_core.output_parsers import PydanticOutputParser
from langchain_openai import ChatOpenAI
from pydantic import BaseModel, Field

load_dotenv()


class Student(BaseModel):
    name: str = Field(description="학생의 이름")
    age: int = Field(description="학생의 나이 (숫자)")
    major: str = Field(description="학생의 전공")
    hobbies: List[str] = Field(description="학생의 취미 리스트")
    goal: str = Field(description="학생의 목표")


parser = PydanticOutputParser(pydantic_object=Student)

prompt = PromptTemplate(
    template=(
        "다음 자기소개에서 학생 정보를 추출하세요.\n"
        "자기소개: {introduction}\n"
        "{format_instructions}"
    ),
    input_variables=["introduction"],
    partial_variables={"format_instructions": parser.get_format_instructions()},
)

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)
chain = prompt | llm | parser

if __name__ == "__main__":
    text = (
        "안녕하세요! 저는 김민수이고 22살입니다. 컴퓨터공학을 전공하고 있어요. "
        "취미로는 게임하기, 영화보기, 코딩을 좋아합니다. 앞으로 훌륭한 개발자가 되는 것이 목표입니다."
    )
    student = chain.invoke({"introduction": text})
    print(student)
    print(student.model_dump_json(indent=4, ensure_ascii=False))

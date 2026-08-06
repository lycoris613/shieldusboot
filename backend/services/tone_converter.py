import os
from dotenv import load_dotenv
from fastapi import HTTPException
from langchain_upstage import ChatUpstage
from langchain_core.output_parsers import StrOutputParser

from backend.prompts.templates import get_chat_prompt

# .env 파일 로드
load_dotenv()


class ToneConverterService:
    def __init__(self):
        self.api_key = os.getenv("UPSTAGE_API_KEY")
        if not self.api_key or self.api_key == "your_api_key_here":
            # API 키가 미설정된 경우 경고 처리용 식별자 저장
            self.llm = None
        else:
            self.llm = ChatUpstage(
                model="solar-pro3",
                upstage_api_key=self.api_key,
                temperature=0.7
            )

    async def convert(self, text: str, target_audience: str) -> str:
        """원문 텍스트를 수신 대상에 맞게 변환합니다."""
        if not self.llm:
            # 환경변수를 재확인
            api_key = os.getenv("UPSTAGE_API_KEY")
            if not api_key or api_key == "your_api_key_here":
                raise HTTPException(
                    status_code=500,
                    detail="UPSTAGE_API_KEY가 설정되지 않았습니다. .env 파일을 확인해 주세요."
                )
            self.llm = ChatUpstage(
                model="solar-pro3",
                upstage_api_key=api_key,
                temperature=0.7
            )

        try:
            prompt = get_chat_prompt(target_audience)
            chain = prompt | self.llm | StrOutputParser()
            result = await chain.ainvoke({"text": text})
            return result.strip()
        except Exception as e:
            raise HTTPException(
                status_code=500,
                detail=f"LLM API 호출 중 오류가 발생했습니다: {str(e)}"
            )


# 싱글톤 인스턴스 생성
tone_converter_service = ToneConverterService()

from pydantic import BaseModel, Field, field_validator


class ConvertRequest(BaseModel):
    text: str = Field(
        ...,
        min_length=1,
        description="변환할 원문 텍스트",
        example="내일까지 보고서 제출 어려울 것 같음"
    )
    target_audience: str = Field(
        ...,
        description="수신 대상 코드 (boss / colleague / client / team)",
        example="boss"
    )

    @field_validator("target_audience")
    @classmethod
    def validate_target_audience(cls, v: str) -> str:
        allowed = {"boss", "colleague", "client", "team"}
        if v not in allowed:
            raise ValueError(f"target_audience는 {allowed} 중 하나여야 합니다. (입력값: {v})")
        return v


class ConvertResponse(BaseModel):
    converted_text: str = Field(..., description="말투가 변환된 텍스트")
    target_audience: str = Field(..., description="적용된 수신 대상 코드")
    original_text: str = Field(..., description="입력된 원문 텍스트")

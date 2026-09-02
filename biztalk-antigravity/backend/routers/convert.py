from fastapi import APIRouter
from backend.models.schemas import ConvertRequest, ConvertResponse
from backend.services.tone_converter import tone_converter_service

router = APIRouter(tags=["Tone Conversion"])


@router.post("/convert", response_model=ConvertResponse, summary="업무 말투 변환")
async def convert_tone(request: ConvertRequest):
    """
    입력받은 원문 텍스트를 선택한 수신 대상(boss / colleague / client / team)에 알맞은 비즈니스 말투로 변환합니다.
    """
    converted_text = await tone_converter_service.convert(
        text=request.text,
        target_audience=request.target_audience
    )

    return ConvertResponse(
        converted_text=converted_text,
        target_audience=request.target_audience,
        original_text=request.text
    )

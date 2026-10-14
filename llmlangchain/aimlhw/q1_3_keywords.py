"""문제 1-3: FewShotPromptTemplate과 시스템 메시지 활용 - 뉴스 키워드 추출기"""
from dotenv import load_dotenv
from langchain_core.prompts import FewShotChatMessagePromptTemplate, ChatPromptTemplate
from langchain_core.output_parsers import StrOutputParser
from langchain_openai import ChatOpenAI

load_dotenv()

# 1. 예시 프롬프트 (대화형)
example_prompt = ChatPromptTemplate.from_messages([
    ("human", "{news}"),
    ("ai", "키워드: {keywords}"),
])

# 2. 예시 데이터 (다양한 분야, 3개 이상)
examples = [
    {
        "news": "삼성전자가 내년 초에 자체적으로 개발한 인공지능(AI) 가속기를 처음으로 출시할 예정이다. 이는 AI 반도체 시장에서 지배적인 위치를 차지하고 있는 엔비디아의 독점을 도전하고, 세계 최고의 반도체 제조업체로서의 지위를 다시 확립하려는 삼성전자의 노력으로 해석된다.",
        "keywords": "삼성전자, 인공지능, 엔비디아",
    },
    {
        "news": "세계보건기구(WHO)는 최근 새로운 건강 위기에 대응하기 위해 국제 협력의 중요성을 강조했다. 전염병 대응 역량의 강화와 글로벌 보건 시스템의 개선이 필요하다고 발표했다.",
        "keywords": "세계보건기구, 건강위기, 국제협력",
    },
    {
        "news": "한국은행이 기준금리를 연 3.5%로 동결했다. 물가 상승률이 둔화되고 있지만 가계부채 부담이 여전히 크다는 판단에 따른 것으로, 시장에서는 하반기 금리 인하 가능성에 주목하고 있다.",
        "keywords": "한국은행, 기준금리, 가계부채",
    },
    {
        "news": "손흥민이 프리미어리그 경기에서 멀티골을 터뜨리며 팀의 3대 1 승리를 이끌었다. 이로써 토트넘은 리그 4위로 올라서며 챔피언스리그 진출 경쟁에 불을 붙였다.",
        "keywords": "손흥민, 프리미어리그, 토트넘",
    },
]

# 3. Few-Shot 프롬프트
few_shot_prompt = FewShotChatMessagePromptTemplate(
    example_prompt=example_prompt,
    examples=examples,
)

# 4. 최종 프롬프트
final_prompt = ChatPromptTemplate.from_messages([
    ("system", "뉴스 키워드 추출 전문가입니다. 핵심 키워드 3개를 추출하세요. 예시와 동일한 형식('키워드: A, B, C')으로만 답하세요."),
    few_shot_prompt,
    ("human", "{input}"),
])

llm = ChatOpenAI(model="gpt-4o-mini", temperature=0)
chain = final_prompt | llm | StrOutputParser()

if __name__ == "__main__":
    test_news = [
        "제미나이 2.0 플래시는 현재 구글 AI 스튜디오(Google AI Studio) 및 버텍스 AI(Vertex AI)에서 제미나이 API를 통해 개발자에게 실험 모델로 제공됩니다. 모든 개발자는 멀티모달 입력 및 텍스트 출력을 사용할 수 있으며, 텍스트 음성 변환(text-to-speech) 및 네이티브 이미지 생성은 일부 파트너들을 대상으로 제공됩니다. 내년 1월에는 더 많은 모델 사이즈와 함께 일반에 공개될 예정입니다.",
        # 다양한 분야 테스트
        "영화 '파묘'가 개봉 한 달 만에 관객 1000만 명을 돌파했다. 오컬트 장르로는 이례적인 흥행으로, 배우들의 열연과 감독의 연출이 호평을 받았다.",
        "정부는 내년부터 전기차 구매 보조금을 확대하고 충전 인프라 구축에 5000억 원을 투입하기로 했다. 탄소중립 목표 달성을 위한 조치다.",
    ]
    for news in test_news:
        print(f"뉴스: {news[:60]}...")
        print(chain.invoke({"input": news}))
        print("-" * 50)

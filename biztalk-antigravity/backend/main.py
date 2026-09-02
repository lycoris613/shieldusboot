import os
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles

from backend.routers import convert

app = FastAPI(
    title="업무 말투 변환기 API",
    description="Upstage Solar-Pro3 기반 비즈니스 커뮤니케이션 말투 변환 백엔드 서비스",
    version="1.0.0"
)

# CORS 설정 (프론트엔드에서 API 호출 가능하도록 허용)
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# 백엔드 API 라우터 포함
app.include_router(convert.router, prefix="/api")


# Health Check 엔드포인트
@app.get("/health", tags=["Health Check"])
def health_check():
    return {"status": "ok"}


# 프론트엔드 정적 파일 서빙 (frontend 디렉터리가 존재하는 경우)
# index.html이 상대경로(css/, js/)로 자산을 참조하므로 루트("/")에 직접 마운트한다.
frontend_dir = os.path.join(os.path.dirname(os.path.dirname(__file__)), "frontend")
if os.path.exists(os.path.join(frontend_dir, "index.html")):
    app.mount("/", StaticFiles(directory=frontend_dir, html=True), name="frontend")
else:
    @app.get("/", include_in_schema=False)
    def serve_index():
        return {"message": "Frontend index.html page not created yet."}

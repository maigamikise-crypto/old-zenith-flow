from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from zenith_ai.api.endpoints import router as api_router
from zenith_ai.core.config import settings

def create_app() -> FastAPI:
    app = FastAPI(
        title="Zenith Flow AI Engine",
        description="AI Quantitative Trading Engine API",
        version="0.1.0",
    )

    # CORS Configuration
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["*"],
        allow_credentials=True,
        allow_methods=["*"],
        allow_headers=["*"],
    )

    # Include API Router
    app.include_router(api_router, prefix="/api/v1")

    @app.get("/health")
    async def health_check():
        return {"status": "ok", "version": "0.1.0"}

    return app

app = create_app()

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)


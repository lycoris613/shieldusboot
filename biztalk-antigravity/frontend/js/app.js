const API_BASE = "http://localhost:8000"; // 배포 시 실제 백엔드 URL로 변경

const form = document.getElementById("convertForm");
const inputText = document.getElementById("inputText");
const charCount = document.getElementById("charCount");
const targetButtons = document.querySelectorAll(".target-btn");
const convertBtn = document.getElementById("convertBtn");
const convertBtnLabel = document.getElementById("convertBtnLabel");
const statusMessage = document.getElementById("statusMessage");
const emptyState = document.getElementById("emptyState");
const resultText = document.getElementById("resultText");
const resultOutput = document.getElementById("resultOutput");
const copyBtn = document.getElementById("copyBtn");
const issueDateEl = document.getElementById("issueDate");
const issueNoEl = document.getElementById("issueNo");

const CHAR_LIMIT = 500;
const prefersReducedMotion = window.matchMedia("(prefers-reduced-motion: reduce)").matches;

let selectedTarget = null;
let copyResetTimer = null;
let typeTimer = null;

setIssueStamp();

inputText.addEventListener("input", () => {
  const length = inputText.value.length;
  charCount.textContent = `${length} / ${CHAR_LIMIT}`;
  charCount.classList.toggle("is-over", length > CHAR_LIMIT);
});

targetButtons.forEach((btn) => {
  btn.addEventListener("click", () => {
    selectedTarget = btn.dataset.target;
    targetButtons.forEach((b) => {
      const isSelected = b === btn;
      b.setAttribute("aria-pressed", isSelected ? "true" : "false");
      b.querySelector(".checkbox").textContent = isSelected ? "[x]" : "[ ]";
    });
    setStatus("");
  });
});

form.addEventListener("submit", async (event) => {
  event.preventDefault();

  const text = inputText.value.trim();

  if (!text) {
    setStatus("원문을 입력해 주세요.");
    inputText.focus();
    return;
  }

  if (!selectedTarget) {
    setStatus("수신 대상을 선택해 주세요.");
    return;
  }

  setStatus("");
  setLoading(true);

  try {
    const response = await fetch(`${API_BASE}/api/convert`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ text, target_audience: selectedTarget }),
    });

    if (!response.ok) {
      throw new Error(`요청 실패 (${response.status})`);
    }

    const data = await response.json();
    showResult(data.converted_text);
  } catch (error) {
    showError();
  } finally {
    setLoading(false);
  }
});

copyBtn.addEventListener("click", async () => {
  try {
    await navigator.clipboard.writeText(resultText.textContent);
    const original = copyBtn.textContent;
    copyBtn.textContent = "[ 복사 완료 ]";
    clearTimeout(copyResetTimer);
    copyResetTimer = setTimeout(() => {
      copyBtn.textContent = original;
    }, 1500);
  } catch (error) {
    setStatus("복사에 실패했습니다. 직접 선택해 복사해 주세요.");
  }
});

function setIssueStamp() {
  const now = new Date();
  const y = now.getFullYear();
  const m = String(now.getMonth() + 1).padStart(2, "0");
  const d = String(now.getDate()).padStart(2, "0");
  issueDateEl.textContent = `${y}.${m}.${d}`;
  issueNoEl.textContent = String(now.getHours() * 100 + now.getMinutes()).padStart(4, "0");
}

function setLoading(isLoading) {
  convertBtn.disabled = isLoading;
  convertBtn.classList.toggle("is-loading", isLoading);
  convertBtn.setAttribute("aria-busy", String(isLoading));
  convertBtnLabel.textContent = isLoading ? "인 쇄 중" : "변 환 하 기";
}

function setStatus(message) {
  statusMessage.textContent = message;
}

function showResult(convertedText) {
  emptyState.hidden = true;
  resultText.hidden = false;
  resultOutput.classList.remove("has-error");
  copyBtn.disabled = false;

  typeOut(resultText, convertedText);
}

function showError() {
  emptyState.hidden = true;
  resultText.hidden = false;
  resultOutput.classList.add("has-error");
  copyBtn.disabled = true;

  clearTimeout(typeTimer);
  resultText.classList.remove("is-typing");
  resultText.textContent = "변환에 실패했습니다. 잠시 후 다시 시도해 주세요.";
}

function typeOut(el, fullText) {
  clearTimeout(typeTimer);

  if (prefersReducedMotion) {
    el.textContent = fullText;
    el.classList.remove("is-typing");
    return;
  }

  el.textContent = "";
  el.classList.add("is-typing");

  let i = 0;
  const step = () => {
    i += 1;
    el.textContent = fullText.slice(0, i);
    if (i < fullText.length) {
      typeTimer = setTimeout(step, 14);
    } else {
      el.classList.remove("is-typing");
    }
  };
  step();
}

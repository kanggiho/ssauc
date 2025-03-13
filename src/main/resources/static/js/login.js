document.addEventListener("DOMContentLoaded", function() {
    const loginForm = document.getElementById("login-form");
    const errorMessageElement = document.getElementById("login-error");

    // 페이지 로드시 URL 쿼리 파라미터에 error=true가 있으면 로그인 실패로 간주하고 에러 메시지 표시
    const params = new URLSearchParams(window.location.search);
    if (params.get("error") === "true") {
        errorMessageElement.textContent = "아이디 또는 비밀번호를 확인해주세요";
    }

    // 폼 제출 시 클라이언트 유효성 검사
    loginForm.addEventListener("submit", function(event) {
        // 기존 에러 메시지 초기화
        errorMessageElement.textContent = "";

        const email = document.getElementById("email").value.trim();
        const password = document.getElementById("password").value.trim();

        // 아이디가 빈 값이면
        if (email === "") {
            errorMessageElement.textContent = "아이디를 입력해주세요";
            event.preventDefault(); // form 제출 중단
            return;
        }
        // 비밀번호가 빈 값이면
        if (password === "") {
            errorMessageElement.textContent = "비밀번호를 입력해주세요";
            event.preventDefault(); // form 제출 중단
            return;
        }
        // 둘 다 입력된 경우, form은 정상 제출되어 서버에서 로그인 처리를 진행함
    });
});

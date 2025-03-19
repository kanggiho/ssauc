document.addEventListener("DOMContentLoaded", () => {
    console.log("withdraw.js loaded.");

    // 요소 참조
    const withdrawForm = document.getElementById("withdrawForm");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const passwordError = document.getElementById("passwordError");
    const confirmPasswordError = document.getElementById("confirmPasswordError");
    const withdrawErrorMsg = document.getElementById("withdrawErrorMsg");
    const cancelBtn = document.getElementById("cancelBtn");

    // 필수 요소 체크
    if (!withdrawForm || !passwordInput || !confirmPasswordInput || !cancelBtn) {
        console.error("필수 DOM 요소를 찾을 수 없습니다.");
        return;
    }

    // (1) 비밀번호 유효성 검사
    function validatePassword() {
        const pw = passwordInput.value.trim();
        if (pw.length < 8) {
            passwordError.textContent = "비밀번호는 8자 이상이어야 합니다.";
        } else {
            passwordError.textContent = "";
        }
    }

    // (2) 비밀번호 일치 검사
    function checkPasswordsMatch() {
        const pw = passwordInput.value.trim();
        const pwConfirm = confirmPasswordInput.value.trim();
        if (pw !== pwConfirm) {
            confirmPasswordError.textContent = "비밀번호가 일치하지 않습니다.";
        } else {
            confirmPasswordError.textContent = "";
        }
    }

    // 실시간 입력 이벤트
    passwordInput.addEventListener("input", () => {
        validatePassword();
        checkPasswordsMatch();
    });

    confirmPasswordInput.addEventListener("input", () => {
        checkPasswordsMatch();
    });

    // (3) 폼 제출 시 (회원 탈퇴)
    withdrawForm.addEventListener("submit", async (e) => {
        e.preventDefault();
        console.log("회원 탈퇴 폼 제출");

        // 기존 에러 메시지 초기화
        withdrawErrorMsg.textContent = "";

        // 값 가져오기
        const pw = passwordInput.value.trim();
        const pwConfirm = confirmPasswordInput.value.trim();

        // 간단한 유효성 검사
        if (!pw || !pwConfirm) {
            withdrawErrorMsg.textContent = "모든 필드를 입력해주세요.";
            return;
        }
        if (pw.length < 8) {
            withdrawErrorMsg.textContent = "비밀번호는 8자 이상이어야 합니다.";
            return;
        }
        if (pw !== pwConfirm) {
            withdrawErrorMsg.textContent = "비밀번호가 일치하지 않습니다.";
            return;
        }

        // confirm 팝업
        const isConfirmed = window.confirm("정말 회원 탈퇴하시겠습니까?");
        if (!isConfirmed) {
            console.log("회원 탈퇴 취소");
            return;
        }

        // 탈퇴 요청
        try {
            const res = await fetch("/mypage/withdraw", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ password: pw })
            });

            if (!res.ok) {
                const errorMsg = await res.text();
                throw new Error(errorMsg);
            }

            // 성공 시
            const msg = await res.text();
            alert(msg); // 회원 탈퇴가 완료되었습니다. 등
            window.location.href = "/login"; // 탈퇴 후 로그인 페이지로 이동
        } catch (err) {
            console.error("회원 탈퇴 실패:", err);
            withdrawErrorMsg.textContent = err.message;
        }
    });

    // (4) 취소 버튼
    cancelBtn.addEventListener("click", () => {
        console.log("취소 버튼 클릭 => 마이페이지 이동");
        window.location.href = "/mypage";
    });
});

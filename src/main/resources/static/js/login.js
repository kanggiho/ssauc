document.addEventListener("DOMContentLoaded", function() {
    const loginForm = document.getElementById("login-form");
    if (!loginForm) return;

    loginForm.addEventListener("submit", function(event) {
        event.preventDefault();
        console.log("로그인 폼 제출 시작");

        const formData = new FormData(loginForm);
        for (const [key, value] of formData.entries()) {
            console.log(`폼 데이터 - ${key}: ${value}`);
        }

        const data = new URLSearchParams();
        for (const pair of formData.entries()) {
            data.append(pair[0], pair[1]);
        }

        fetch("/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: data,
            credentials: "same-origin"
        })
            .then(response => {
                console.log("POST /login 응답 상태:", response.status);
                if (response.redirected) {
                    console.log("리다이렉트 URL:", response.url);
                    window.location.href = response.url;
                } else if (!response.ok) {
                    return response.text().then(text => {
                        console.error("로그인 실패 응답 텍스트:", text);
                        throw new Error(text || "로그인 실패");
                    });
                } else {
                    return response.text().then(text => {
                        console.log("로그인 응답 텍스트:", text);
                        alert("로그인 실패: " + text);
                    });
                }
            })
            .catch(error => {
                console.error("로그인 요청 중 에러:", error);
                alert("로그인 요청 중 에러가 발생했습니다: " + error.message);
            });
    });
});

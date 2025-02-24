    document.getElementById("login-form").addEventListener("submit", function(event) {
    event.preventDefault(); // 기본 폼 제출 방지
    window.location.href = "index.html"; // 로그인 성공 후 index.html로 이동
});

    document.addEventListener("DOMContentLoaded", function() {
        fetch('/checkSession')
            .then(response => response.json())
            .then(data => {
                if (data && data.username) {
                    document.getElementById("login-section").innerHTML =
                        `<span>${data.username}님 환영합니다! | <a href='/logout'>로그아웃</a></span>`;
                }
            })
            .catch(error => console.error("세션 확인 실패", error));
    }); 
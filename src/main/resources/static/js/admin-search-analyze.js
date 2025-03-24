document.addEventListener("DOMContentLoaded", function () {
    const analyzeForm = document.getElementById("analyzeForm");
    const resultDiv = document.getElementById("analyzeResult");

    analyzeForm.addEventListener("submit", function(e) {
        e.preventDefault();

        // 분석기는 항상 "nori_analyzer"로 설정
        const analyzer = "nori_analyzer";
        const text = document.getElementById("text").value.trim();
        if (!text) return;

        // 분석 요청을 보냄
        fetch(`/admin/search/analyze?analyzer=${encodeURIComponent(analyzer)}&text=${encodeURIComponent(text)}`)
            .then(response => response.text())
            .then(data => {
                resultDiv.textContent = data;
            })
            .catch(err => {
                resultDiv.textContent = "오류 발생: " + err;
            });
    });
});

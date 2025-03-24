document.addEventListener("DOMContentLoaded", function () {
    const morphForm = document.getElementById("morphForm");
    const morphList = document.getElementById("morphList");

    function loadMorphEntries() {
        fetch("/admin/search/morph")
            .then(response => response.json())
            .then(data => {
                morphList.innerHTML = "";
                data.forEach(entry => {
                    const li = document.createElement("li");
                    li.textContent = `${entry.word} - ${entry.analysis}`;
                    // 수정 버튼
                    const editBtn = document.createElement("button");
                    editBtn.textContent = "수정";
                    editBtn.addEventListener("click", () => {
                        document.getElementById("morphId").value = entry.id;
                        document.getElementById("morphWord").value = entry.word;
                        document.getElementById("morphAnalysis").value = entry.analysis;
                    });
                    // 삭제 버튼
                    const delBtn = document.createElement("button");
                    delBtn.textContent = "삭제";
                    delBtn.addEventListener("click", () => {
                        fetch(`/admin/search/morph/${entry.id}`, {
                            method: "DELETE"
                        })
                            .then(() => loadMorphEntries())
                            .catch(err => console.error(err));
                    });
                    li.appendChild(editBtn);
                    li.appendChild(delBtn);
                    morphList.appendChild(li);
                });
            })
            .catch(err => console.error(err));
    }

    morphForm.addEventListener("submit", function(e) {
        e.preventDefault();
        const id = document.getElementById("morphId").value;
        const word = document.getElementById("morphWord").value.trim();
        const analysis = document.getElementById("morphAnalysis").value.trim();
        const payload = { word, analysis };

        const method = id ? "PUT" : "POST";
        const url = id ? `/admin/search/morph/${id}` : "/admin/search/morph";
        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
            .then(() => {
                morphForm.reset();
                document.getElementById("morphId").value = "";
                loadMorphEntries();
            })
            .catch(err => console.error(err));
    });

    loadMorphEntries();
});

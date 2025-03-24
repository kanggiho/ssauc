document.addEventListener("DOMContentLoaded", function () {
    const synonymForm = document.getElementById("synonymForm");
    const synonymEntries = document.getElementById("synonymEntries");

    function loadSynonymEntries() {
        fetch("/admin/search/synonym")
            .then(response => response.json())
            .then(data => {
                synonymEntries.innerHTML = "";
                data.forEach(entry => {
                    const li = document.createElement("li");
                    li.textContent = `${entry.word} - [${entry.synonyms}]`;
                    // 수정 버튼
                    const editBtn = document.createElement("button");
                    editBtn.textContent = "수정";
                    editBtn.addEventListener("click", () => {
                        document.getElementById("synonymId").value = entry.id;
                        document.getElementById("synonymWord").value = entry.word;
                        document.getElementById("synonymList").value = entry.synonyms;
                    });
                    // 삭제 버튼
                    const delBtn = document.createElement("button");
                    delBtn.textContent = "삭제";
                    delBtn.addEventListener("click", () => {
                        fetch(`/admin/search/synonym/${entry.id}`, {
                            method: "DELETE"
                        })
                            .then(() => loadSynonymEntries())
                            .catch(err => console.error(err));
                    });
                    li.appendChild(editBtn);
                    li.appendChild(delBtn);
                    synonymEntries.appendChild(li);
                });
            })
            .catch(err => console.error(err));
    }

    synonymForm.addEventListener("submit", function(e) {
        e.preventDefault();
        const id = document.getElementById("synonymId").value;
        const word = document.getElementById("synonymWord").value.trim();
        const synonyms = document.getElementById("synonymList").value.trim();
        const payload = { word, synonyms };

        const method = id ? "PUT" : "POST";
        const url = id ? `/admin/search/synonym/${id}` : "/admin/search/synonym";
        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
            .then(() => {
                synonymForm.reset();
                document.getElementById("synonymId").value = "";
                loadSynonymEntries();
            })
            .catch(err => console.error(err));
    });

    loadSynonymEntries();
});

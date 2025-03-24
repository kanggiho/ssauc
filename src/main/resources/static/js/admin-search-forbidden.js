document.addEventListener("DOMContentLoaded", function () {
    const forbiddenForm = document.getElementById("forbiddenForm");
    const forbiddenEntries = document.getElementById("forbiddenEntries");

    function loadForbiddenEntries() {
        fetch("/admin/search/forbidden")
            .then(response => response.json())
            .then(data => {
                forbiddenEntries.innerHTML = "";
                data.forEach(entry => {
                    const li = document.createElement("li");
                    li.textContent = `${entry.word} - ${entry.reason}`;
                    // 수정 버튼
                    const editBtn = document.createElement("button");
                    editBtn.textContent = "수정";
                    editBtn.addEventListener("click", () => {
                        document.getElementById("forbiddenId").value = entry.id;
                        document.getElementById("forbiddenWord").value = entry.word;
                        document.getElementById("forbiddenReason").value = entry.reason;
                    });
                    // 삭제 버튼
                    const delBtn = document.createElement("button");
                    delBtn.textContent = "삭제";
                    delBtn.addEventListener("click", () => {
                        fetch(`/admin/search/forbidden/${entry.id}`, {
                            method: "DELETE"
                        })
                            .then(() => loadForbiddenEntries())
                            .catch(err => console.error(err));
                    });
                    li.appendChild(editBtn);
                    li.appendChild(delBtn);
                    forbiddenEntries.appendChild(li);
                });
            })
            .catch(err => console.error(err));
    }

    forbiddenForm.addEventListener("submit", function(e) {
        e.preventDefault();
        const id = document.getElementById("forbiddenId").value;
        const word = document.getElementById("forbiddenWord").value.trim();
        const reason = document.getElementById("forbiddenReason").value.trim();
        const payload = { word, reason };

        const method = id ? "PUT" : "POST";
        const url = id ? `/admin/search/forbidden/${id}` : "/admin/search/forbidden";
        fetch(url, {
            method: method,
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
            .then(() => {
                forbiddenForm.reset();
                document.getElementById("forbiddenId").value = "";
                loadForbiddenEntries();
            })
            .catch(err => console.error(err));
    });

    loadForbiddenEntries();
});

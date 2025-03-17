document.addEventListener("DOMContentLoaded", function () {
    // DOM 요소 가져오기
    const searchInput = document.getElementById("search-input");
    const searchForm = document.getElementById("search-form");
    const recentSearchesUl = document.getElementById("recent-searches");
    const popularSearchesUl = document.getElementById("popular-searches");
    const closeBtn = document.getElementById("closeBtn");
    const searchAlert = document.getElementById("searchAlert");

    // 자동완성 목록 생성 (필요한 경우 사용)
    let autoCompleteList = document.createElement("ul");
    autoCompleteList.id = "autoCompleteList-search";
    autoCompleteList.style.position = "absolute";
    autoCompleteList.style.border = "1px solid #ddd";
    autoCompleteList.style.backgroundColor = "#fff";
    autoCompleteList.style.zIndex = "9999";
    autoCompleteList.style.display = "none";
    document.body.appendChild(autoCompleteList);

    // ※ 연관 검색어 관련 DOM 요소는 search 페이지에서는 사용하지 않으므로 주석 처리
    // let relatedRow = document.createElement("div");
    // relatedRow.id = "relatedRow";
    // relatedRow.style.margin = "5px 0";
    // relatedRow.style.display = "none";
    // searchInput.parentNode.appendChild(relatedRow);

    // URL 파라미터에서 검색어(keyword)를 추출하여 입력란에 반영
    const urlParams = new URLSearchParams(window.location.search);
    const urlKeyword = urlParams.get("keyword");
    if (urlKeyword) {
        searchInput.value = urlKeyword;
    }

    // 닫기 버튼(X)을 누르면 메인 페이지로 이동
    if (closeBtn) {
        closeBtn.addEventListener("click", () => {
            window.location.href = "/";
        });
    }

    // 중복 호출 방지 플래그
    let searchLoggingInProgress = false;

    // 검색어 저장 API 호출 후 결과 페이지(plp 페이지)로 이동하는 함수
    function saveSearchAndRedirect(keyword) {
        if (searchLoggingInProgress) return;
        searchLoggingInProgress = true;
        const query = keyword.trim();
        if (!query) {
            if (searchAlert) searchAlert.style.display = "block";
            alert("검색어를 입력하세요.");
            searchLoggingInProgress = false;
            return;
        }
        if (searchAlert) searchAlert.style.display = "none";
        fetch("/api/save-search", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ keyword: query })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("검색어 저장 실패");
                }
                return response.json();
            })
            .then(data => {
                console.log("✅ 검색어 저장 완료:", query);
                // 결과 페이지(plp)로 이동
                window.location.href = `/plp?keyword=${encodeURIComponent(query)}`;
            })
            .catch(error => {
                console.error("❌ 검색어 저장 오류:", error);
                window.location.href = `/plp?keyword=${encodeURIComponent(query)}`;
            })
            .finally(() => {
                searchLoggingInProgress = false;
            });
    }

    // form 제출 이벤트 처리
    if (searchForm) {
        searchForm.addEventListener("submit", function(e) {
            e.preventDefault();
            saveSearchAndRedirect(searchInput.value);
        });
    } else if (searchInput) {
        searchInput.addEventListener("keydown", function(e) {
            if (e.key === "Enter") {
                e.preventDefault();
                saveSearchAndRedirect(searchInput.value);
            }
        });
    }

    // 자동완성 API 및 관련 이벤트
    searchInput.addEventListener("input", function(e) {
        const prefix = e.target.value.trim();
        if (!prefix) {
            autoCompleteList.style.display = "none";
            // 관련 검색어 부분 주석 처리
            // relatedRow.style.display = "none";
            return;
        }
        // 자동완성 API 호출
        fetch(`/api/autocomplete?prefix=${encodeURIComponent(prefix)}`)
            .then(res => res.json())
            .then(suggestions => {
                renderAutoComplete(suggestions);
            })
            .catch(err => console.error("❌ 자동완성 오류:", err));

        // 연관 검색어 API 호출 (search 페이지에서는 사용하지 않음)
        // fetch(`/api/related-search?keyword=${encodeURIComponent(prefix)}`)
        //     .then(res => res.json())
        //     .then(relatedData => {
        //         showRelatedKeywords(relatedData);
        //     })
        //     .catch(err => console.error("❌ 연관 검색어 오류:", err));
    });

    function renderAutoComplete(suggestions) {
        if (!suggestions || suggestions.length === 0) {
            autoCompleteList.style.display = "none";
            return;
        }
        const rect = searchInput.getBoundingClientRect();
        autoCompleteList.style.left = rect.left + "px";
        autoCompleteList.style.top = (rect.bottom + window.scrollY) + "px";
        autoCompleteList.style.width = rect.width + "px";
        autoCompleteList.innerHTML = "";
        suggestions.forEach(sugg => {
            const li = document.createElement("li");
            li.textContent = sugg;
            li.style.padding = "5px 10px";
            li.style.cursor = "pointer";
            li.addEventListener("click", () => {
                searchInput.value = sugg;
                autoCompleteList.style.display = "none";
                saveSearchAndRedirect(sugg);
            });
            autoCompleteList.appendChild(li);
        });
        autoCompleteList.style.display = "block";
    }

    // 연관 검색어 관련 함수는 주석 처리 (search 페이지에서는 사용하지 않음)
    // function showRelatedKeywords(keywords) {
    //     if (!keywords || keywords.length === 0) {
    //         relatedRow.style.display = "none";
    //         relatedRow.innerHTML = "";
    //         return;
    //     }
    //     relatedRow.style.display = "block";
    //     relatedRow.innerHTML = `
    //       <div style="font-weight:bold; margin-bottom:5px;">연관 검색어</div>
    //       <div style="display:flex; gap:10px; flex-wrap:wrap;">
    //         ${keywords.map(k => `<span class="relatedItem" style="cursor:pointer; color:blue;">${k}</span>`).join("")}
    //       </div>
    //     `;
    //     document.querySelectorAll(".relatedItem").forEach(item => {
    //         item.addEventListener("click", () => {
    //             searchInput.value = item.textContent;
    //             saveSearchAndRedirect(item.textContent);
    //         });
    //     });
    // }

    // 검색 input 'Enter' 키 이벤트
    searchInput.addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            saveSearchAndRedirect(searchInput.value);
        }
    });

    // 검색 아이콘 클릭 이벤트
    if (document.getElementById("searchIcon")) {
        document.getElementById("searchIcon").addEventListener("click", function () {
            saveSearchAndRedirect(searchInput.value);
        });
    }

    // 최근 검색어 API 호출
    fetch("/api/recent-searches", {
        method: "GET",
        headers: { "Content-Type": "application/json" }
    })
        .then(response => {
            if (!response.ok) return response.json().then(err => { throw new Error(err.error || "서버 오류"); });
            return response.json();
        })
        .then(data => {
            console.log("✅ 최근 검색어:", data.recentSearches);
            updateRecentSearches(data.recentSearches);
        })
        .catch(error => {
            console.error("❌ 최근 검색어 API 오류:", error);
            if (recentSearchesUl) {
                recentSearchesUl.innerHTML = `<li class="error">❌ 최근 검색어를 불러올 수 없습니다.</li>`;
            }
        });

    // 인기 검색어 API 호출
    fetch("/api/popular-searches", {
        method: "GET",
        headers: { "Content-Type": "application/json" }
    })
        .then(response => {
            if (!response.ok) return response.json().then(err => { throw new Error(err.error || "서버 오류"); });
            return response.json();
        })
        .then(data => {
            console.log("✅ 인기 검색어:", data.popularSearches);
            updatePopularSearches(data.popularSearches);
        })
        .catch(error => {
            console.error("❌ 인기 검색어 API 오류:", error);
            if (popularSearchesUl) {
                popularSearchesUl.innerHTML = `<li class="error">❌ 인기 검색어를 불러올 수 없습니다.</li>`;
            }
        });

    function updateRecentSearches(searches) {
        if (!recentSearchesUl) return;
        recentSearchesUl.innerHTML = "";
        if (!searches || searches.length === 0) {
            recentSearchesUl.innerHTML = `<li class="empty">최근 검색어가 없습니다.</li>`;
            return;
        }
        const uniqueSearches = Array.from(new Set(searches.filter(s => s && s.trim().length > 0)));
        uniqueSearches.forEach(search => {
            let li = document.createElement("li");
            li.className = "recent-search-item";
            li.textContent = search;
            let deleteBtn = document.createElement("button");
            deleteBtn.className = "delete-btn";
            deleteBtn.textContent = "X";
            deleteBtn.addEventListener("click", (event) => {
                event.stopPropagation();
                deleteRecentSearch(search);
            });
            li.appendChild(deleteBtn);
            li.addEventListener("click", () => {
                searchInput.value = search;
                saveSearchAndRedirect(search);
            });
            recentSearchesUl.appendChild(li);
        });
    }

    function updatePopularSearches(list) {
        if (!popularSearchesUl) return;
        popularSearchesUl.innerHTML = "";
        if (!list || list.length === 0) {
            popularSearchesUl.innerHTML = `<li class="empty">인기 검색어가 없습니다.</li>`;
            return;
        }
        list.forEach((item, index) => {
            let li = document.createElement("li");
            li.className = "popular-search-item";
            li.innerHTML = `<span class="rank">${index + 1}</span> <span class="keyword-span">${item.keyword}</span>`;
            li.addEventListener("click", () => {
                searchInput.value = item.keyword;
                saveSearchAndRedirect(item.keyword);
            });
            popularSearchesUl.appendChild(li);
        });
    }

    function deleteRecentSearch(searchKeyword) {
        fetch("/api/recent-searches?keyword=" + encodeURIComponent(searchKeyword), {
            method: "DELETE",
            headers: { "Content-Type": "application/json" }
        })
            .then(response => {
                if (!response.ok) return response.json().then(err => { throw new Error(err.error || "검색어 삭제 실패"); });
                return response.json();
            })
            .then(data => {
                console.log("✅ 삭제된 검색어:", searchKeyword);
                fetch("/api/recent-searches")
                    .then(resp => resp.json())
                    .then(data => updateRecentSearches(data.recentSearches))
                    .catch(err => console.error("❌ 최근 검색어 API 오류:", err));
            })
            .catch(error => console.error("❌ 최근 검색어 삭제 오류:", error));
    }
});

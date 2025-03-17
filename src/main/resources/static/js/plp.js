// 자동완성 추천어를 표시할 DOM 요소
let autoCompleteList = null;
// 연관 검색어 표시할 DOM 요소
let relatedRow = null;

document.addEventListener("DOMContentLoaded", function () {
    console.log("🚀 PLP 페이지 로딩됨");

    // -------------------------
    // 주요 DOM 요소
    // -------------------------
    const searchInput = document.getElementById("searchInput");
    const searchIcon = document.getElementById("searchIcon");
    const productGrid = document.getElementById("productGrid");
    const paginationEl = document.getElementById("pagination");
    const sortSelect = document.getElementById("sortSelect");
    const auctionOnlyCheckbox = document.getElementById("auctionOnlyCheckbox");
    const filterCategoryEl = document.getElementById("filterCategory");
    const minPriceInput = document.getElementById("minPriceInput");
    const maxPriceInput = document.getElementById("maxPriceInput");
    const filterResetBtn = document.getElementById("filterResetBtn");
    const totalCountLabel = document.getElementById("totalCountLabel");
    const searchAlert = document.getElementById("searchAlert"); // 검색어 미입력 경고

    // ① 자동완성 목록 생성 (UL 등)
    autoCompleteList = document.createElement("ul");
    autoCompleteList.id = "autoCompleteList";
    autoCompleteList.style.position = "absolute";
    autoCompleteList.style.border = "1px solid #ddd";
    autoCompleteList.style.backgroundColor = "#fff";
    autoCompleteList.style.zIndex = "9999";
    autoCompleteList.style.display = "none";
    // 문서 body에 붙이거나, 검색창 부모요소에 appendChild 할 수 있음
    document.body.appendChild(autoCompleteList);

    // ② 연관 검색어 표시부 생성
    relatedRow = document.createElement("div");
    relatedRow.id = "relatedRow";
    relatedRow.style.margin = "5px 0";
    relatedRow.style.display = "none";
    // 검색 입력칸 바로 밑에 표시하고 싶다면, searchInput.parentNode.insertBefore(relatedRow, searchInput.nextSibling);
    // 여기서는 body 말미에 붙여두고 위치를 CSS로 조정 가능
    searchInput.parentNode.appendChild(relatedRow);

    // -------------------------
    // 전역 상태 관리
    // -------------------------
    let currentKeyword = "";
    let currentPage = 1;
    let pageSize = 30; // 한 페이지에 30개씩
    let currentSort = "VIEW_DESC"; // 기본 정렬: 조회수 많은 순
    let auctionOnly = false; // 경매중 상품만 보기
    let selectedCategories = []; // 카테고리 다중 선택
    let minPrice = null; // 최소 가격
    let maxPrice = null; // 최대 가격

    // -------------------------
    // URL 파라미터에서 검색어 추출
    // -------------------------
    const urlParams = new URLSearchParams(window.location.search);
    const keywordParam = urlParams.get("keyword");
    if (keywordParam) {
        currentKeyword = keywordParam.trim();
        if (searchInput) searchInput.value = currentKeyword;
    }

    // -------------------------
    // 상품 목록 로딩 (API 호출)
    // -------------------------
    function loadProducts() {
        const params = new URLSearchParams();

        if (currentKeyword) params.append("keyword", currentKeyword);
        params.append("page", currentPage);
        params.append("size", pageSize);
        params.append("sort", currentSort); // ✅ 정렬 옵션 반영

        if (auctionOnly) params.append("auctionOnly", "true"); // ✅ 경매 상품 필터 반영
        if (selectedCategories.length > 0) params.append("categories", selectedCategories.join(",")); // ✅ 선택한 카테고리 필터 적용
        if (minPrice !== null && maxPrice !== null) {
            params.append("minPrice", minPrice);
            params.append("maxPrice", maxPrice);
        }

        const url = "/api/products/plp?" + params.toString();
        console.log("📡 API 호출:", url);

        fetch(url)
            .then(response => response.json())
            .then(data => {
                console.log("📦 API 응답:", data);
                renderProducts(data.products);
                renderPagination(data.page, data.totalPages);

                if (totalCountLabel) {
                    totalCountLabel.textContent = `총 ${data.totalCount}개의 상품`;
                }
            })
            .catch(err => console.error("❌ 상품 API 오류:", err));
    }

    // -------------------------
    // 상품 리스트 렌더링
    // -------------------------
    function renderProducts(products) {
        if (!productGrid) return;
        if (!products || products.length === 0) {
            productGrid.innerHTML = `<p class="text-center">❌ 검색된 상품이 없습니다.</p>`;
            return;
        }

        productGrid.innerHTML = products.map(product => `
          <div class="col">
              <div class="card product-card position-relative">
                  <!-- 좋아요 버튼 -->
                  <button class="icon-btn position-absolute top-0 end-0" data-product-id="${product.productId}" onclick="toggleHeart(this)">
                      <i class="${product.liked ? 'bi bi-heart-fill' : 'bi bi-heart'}"></i>
                  </button>
                  <a href="/bid/bid?productId=${product.productId}">
                      <img src="${product.imageUrl || '/img/noimage.png'}" class="card-img-top" alt="상품 이미지">
                      <div class="card-body">
                          <p class="product-title">${product.name}</p>
                          <p class="product-price">${(product.price || 0).toLocaleString()}원</p>
                          <p class="product-info">경매 시작가: ${(product.startPrice || 0).toLocaleString()}원</p>
                          <p class="product-info">현재 입찰가: ${(product.tempPrice || 0).toLocaleString()}원</p>
                          <p class="product-info">입찰 수: ${product.bidCount}회 | ❤️ ${product.likeCount}</p>
                          <p class="product-info">조회수: ${product.viewCount}</p>
                          <p class="product-info">카테고리: ${product.categoryName}</p>
                          <p class="product-status">상태: ${product.status}</p>
                      </div>
                  </a>
              </div>
          </div>
        `).join("");
    }

    // -------------------------
    // 좋아요 기능
    // -------------------------
    window.toggleHeart = function (button) {
        const productId = button.getAttribute("data-product-id");
        const icon = button.querySelector("i");
        const likeCountElement = button.closest(".product-card").querySelector(".like-count");
        let currentCount = parseInt(likeCountElement?.textContent?.replace(/,/g, ''), 10) || 0;

        fetch("/api/like", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({productId})
        })
            .then(response => response.json())
            .then(data => {
                icon.classList.toggle("bi-heart-fill", data.liked);
                icon.classList.toggle("bi-heart", !data.liked);
                if (likeCountElement) {
                    likeCountElement.textContent = data.liked ? ++currentCount : Math.max(0, --currentCount);
                }
            })
            .catch(error => console.error("❌ 좋아요 API 오류:", error));
    };

    // -------------------------
    // 검색 기능
    // -------------------------
    function performSearch() {
        const query = searchInput.value.trim();
        if (!query) {
            if (searchAlert) searchAlert.style.display = "block";
            return;
        }
        if (searchAlert) searchAlert.style.display = "none";
        currentKeyword = query;
        currentPage = 1;

        // 🔥 검색어 저장 API 호출 (검색어를 최근검색어 및 인기검색어에 저장)
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
                window.location.href = `/plp?keyword=${encodeURIComponent(query)}`;
            })
            .catch(error => {
                console.error("❌ 검색어 저장 오류:", error);
                window.location.href = `/plp?keyword=${encodeURIComponent(query)}`;
            });
    }

    // ③ 자동완성 관련 이벤트
    searchInput.addEventListener("input", function(e) {
        const prefix = e.target.value.trim();
        if (!prefix) {
            autoCompleteList.style.display = "none";
            relatedRow.style.display = "none";
            return;
        }
        // 자동완성 API 호출
        fetch(`/api/autocomplete?prefix=${encodeURIComponent(prefix)}`)
            .then(res => res.json())
            .then(suggestions => {
                showAutoComplete(suggestions);
            })
            .catch(err => console.error("❌ 자동완성 API 오류:", err));

        // 연관 검색어 API 호출 (plp 검색 입력칸 밑)
        fetch(`/api/related-search?keyword=${encodeURIComponent(prefix)}`)
            .then(res => res.json())
            .then(relatedData => {
                showRelatedKeywords(relatedData);
            })
            .catch(err => console.error("❌ 연관 검색어 API 오류:", err));
    });

    function showAutoComplete(suggestions) {
        if (!suggestions || suggestions.length === 0) {
            autoCompleteList.style.display = "none";
            return;
        }
        // 검색창 위치 찾기
        const rect = searchInput.getBoundingClientRect();
        autoCompleteList.style.left = rect.left + "px";
        autoCompleteList.style.top = (rect.bottom + window.scrollY) + "px";
        autoCompleteList.style.width = rect.width + "px";

        autoCompleteList.innerHTML = "";
        suggestions.forEach(sugg => {
            const li = document.createElement("li");
            li.textContent = sugg;
            li.style.cursor = "pointer";
            li.style.padding = "5px 10px";
            li.addEventListener("click", () => {
                searchInput.value = sugg;
                autoCompleteList.style.display = "none";
                performSearch(); // 즉시 검색 수행 or 사용자가 엔터 치게 할 수도 있음
            });
            autoCompleteList.appendChild(li);
        });
        autoCompleteList.style.display = "block";
    }

    function showRelatedKeywords(keywords) {
        if (!keywords || keywords.length === 0) {
            relatedRow.style.display = "none";
            relatedRow.innerHTML = "";
            return;
        }
        relatedRow.style.display = "block";
        relatedRow.innerHTML = `
          <div style="font-weight:bold; margin-bottom:5px;">연관 검색어</div>
          <div style="display:flex; gap:10px; flex-wrap:wrap;">
            ${keywords.map(k => `<span class="relatedItem" style="cursor:pointer; color:blue;">${k}</span>`).join("")}
          </div>
        `;
        // 클릭 시 검색
        document.querySelectorAll(".relatedItem").forEach(item => {
            item.addEventListener("click", () => {
                searchInput.value = item.textContent;
                performSearch();
            });
        });
    }

    // 검색 input 'Enter' 키 이벤트
    searchInput.addEventListener("keypress", function (e) {
        if (e.key === "Enter") {
            e.preventDefault();
            performSearch();
        }
    });
    // 검색 아이콘 클릭 이벤트
    if (searchIcon) {
        searchIcon.addEventListener("click", performSearch);
    }

    // -------------------------
    // 카테고리 목록 불러오기
    // -------------------------
    function loadCategories() {
        fetch("/api/products/categories")
            .then(response => {
                if (!response.ok) {
                    throw new Error("서버 응답 오류");
                }
                return response.json();
            })
            .then(categories => {
                if (!Array.isArray(categories) || categories.length === 0) {
                    categories = ["기타"];
                }

                filterCategoryEl.innerHTML = categories.map(category => `
                    <li>
                        <label>
                            <input type="checkbox" value="${category}">
                            ${category}
                        </label>
                    </li>
                `).join("");

                filterCategoryEl.querySelectorAll("input[type=checkbox]").forEach(chk => {
                    chk.addEventListener("change", function () {
                        selectedCategories = Array.from(
                            filterCategoryEl.querySelectorAll("input[type=checkbox]:checked")
                        ).map(chk => chk.value);
                        loadProducts();
                    });
                });
            })
            .catch(err => console.error("❌ 카테고리 API 오류:", err));
    }

    // 초기 로딩 시 카테고리 불러오기
    loadCategories();

    // -------------------------
    // 페이지네이션 렌더링
    // -------------------------
    function renderPagination(currentPage, totalPages) {
        if (!paginationEl) return;
        paginationEl.innerHTML = "";

        if (totalPages <= 1) return;

        for (let i = 1; i <= totalPages; i++) {
            const pageBtn = document.createElement("button");
            pageBtn.className = `page-btn ${i === currentPage ? "active" : ""}`;
            pageBtn.textContent = i;
            pageBtn.addEventListener("click", () => {
                currentPage = i;
                loadProducts();
            });
            paginationEl.appendChild(pageBtn);
        }
    }

    // -------------------------
    // 필터 & 정렬 이벤트 바인딩
    // -------------------------
    if (sortSelect) {
        sortSelect.addEventListener("change", function () {
            currentSort = this.value;
            currentPage = 1;
            loadProducts();
        });
    }

    auctionOnlyCheckbox.addEventListener("change", function () {
        auctionOnly = this.checked;
        currentPage = 1;
        loadProducts();
    });

    window.filterByInputPrice = function () {
        minPrice = parseInt(minPriceInput.value) || 0;
        maxPrice = parseInt(maxPriceInput.value) || 0;
        if (minPrice > maxPrice) {
            alert("최소 가격이 최대 가격보다 클 수 없습니다.");
            return;
        }
        currentPage = 1;
        loadProducts();
    };

    filterResetBtn.addEventListener("click", function () {
        auctionOnlyCheckbox.checked = false;
        auctionOnly = false;
        selectedCategories = [];
        minPrice = maxPrice = null;
        minPriceInput.value = maxPriceInput.value = "";
        currentSort = "VIEW_DESC";
        sortSelect.value = "VIEW_DESC";
        currentPage = 1;
        loadProducts();
    });

    // 초기 로딩
    loadProducts();
});

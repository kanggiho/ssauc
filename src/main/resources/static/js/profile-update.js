document.addEventListener("DOMContentLoaded", () => {
    // ===== 요소 참조 =====
    const profileForm = document.getElementById("profileUpdateForm");
    const cancelBtn = document.getElementById("cancelBtn");

    // 닉네임 관련
    const userNameInput = document.getElementById("userName");
    const nickCheckBtn = document.getElementById("nickCheckBtn");
    const nickError = document.getElementById("nickError");
    let nickVerified = false;
    const currentNick = userNameInput.value.trim(); // 기존 닉네임

    // 비밀번호 관련
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const passwordError = document.getElementById("passwordError");
    const confirmPasswordError = document.getElementById("confirmPasswordError");

    // 휴대폰 인증 관련
    const phoneInput = document.getElementById("phone");
    const phoneValidateBtn = document.getElementById("phoneValidateBtn");
    const phoneError = document.getElementById("phoneError");
    const smsCodeInput = document.getElementById("smsCode");
    const verifySmsBtn = document.getElementById("verifySmsBtn");
    const smsCodeError = document.getElementById("smsCodeError");
    let phoneVerified = false;
    let recaptchaVerifier = null;
    let firebaseToken = null;

    // 주소 관련
    const addAddressBtn = document.getElementById("addAddressBtn");
    const zipcodeInput = document.getElementById("zipcode");
    const addressInput = document.getElementById("address");
    const addressDetailInput = document.getElementById("addressDetail");
    const addressError = document.getElementById("addressError");

    // 프로필 이미지 관련
    const dropzone = document.getElementById("dropzone");
    const attachFileBtn = document.getElementById("attachFileBtn");
    const uploadProfileImageBtn = document.getElementById("uploadProfileImageBtn");
    const fileInput = document.getElementById("profileImageInput");
    const profileImageHidden = document.getElementById("profileImage");
    let selectedFile = null;

    // 커스텀 Alert 모달 요소
    const customAlertModal = document.getElementById("customAlertModal");
    const customAlertMessage = document.getElementById("customAlertMessage");
    const customAlertConfirmBtn = document.getElementById("customAlertConfirmBtn");

    // ===== 공용 Alert 함수 =====
    function showAlert(message, callback = null) {
        if (customAlertModal && customAlertMessage && customAlertConfirmBtn) {
            customAlertMessage.textContent = message;
            customAlertModal.style.display = "flex";
            customAlertConfirmBtn.onclick = () => {
                customAlertModal.style.display = "none";
                if (callback) callback();
            };
        } else {
            alert(message);
            if (callback) callback();
        }
    }

    // ===== 닉네임 중복 확인 =====
    nickCheckBtn.addEventListener("click", async () => {
        const newNick = userNameInput.value.trim();
        if (newNick === currentNick) {
            // 현재 사용하는 닉네임과 같으면 중복확인 통과
            nickError.textContent = "현재 사용 중인 닉네임은 사용 가능합니다.";
            nickError.style.color = "green";
            nickVerified = true;
            return;
        }
        if (newNick.length < 2) {
            nickError.textContent = "닉네임은 최소 2글자 이상이어야 합니다.";
            nickError.style.color = "red";
            nickVerified = false;
            return;
        }
        try {
            const res = await fetch(`/api/user/check-username?username=${encodeURIComponent(newNick)}`);
            if (!res.ok) {
                const errText = await res.text();
                throw new Error(errText);
            }
            const msg = await res.text();
            nickError.textContent = msg;
            nickError.style.color = "green";
            nickVerified = true;
        } catch (err) {
            nickError.textContent = err.message;
            nickError.style.color = "red";
            nickVerified = false;
        }
    });

    // ===== 비밀번호 유효성 및 확인 =====
    passwordInput.addEventListener("input", () => {
        const pw = passwordInput.value.trim();
        if (pw === "") {
            passwordError.textContent = "";
            return;
        }
        const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&]).{8,}$/;
        if (pwRegex.test(pw)) {
            passwordError.textContent = "";
        } else {
            passwordError.textContent = "비밀번호는 최소 8자, 영문, 숫자, 특수문자를 포함해야 합니다.";
            passwordError.style.color = "red";
        }
    });
    confirmPasswordInput.addEventListener("input", () => {
        if (passwordInput.value.trim() !== confirmPasswordInput.value.trim()) {
            confirmPasswordError.textContent = "비밀번호가 일치하지 않습니다.";
            confirmPasswordError.style.color = "red";
        } else {
            confirmPasswordError.textContent = "";
        }
    });

    // ===== 휴대폰 번호 유효성 검사 =====
    phoneInput.addEventListener("blur", () => {
        const phoneVal = phoneInput.value.trim();
        const regex = /^\d{10,11}$/;
        if (!regex.test(phoneVal)) {
            phoneError.textContent = "유효한 휴대폰 번호를 입력하세요. (예: 01012345678)";
            phoneError.style.color = "red";
            phoneValidateBtn.disabled = true;
        } else {
            phoneError.textContent = "";
            phoneValidateBtn.disabled = false;
        }
    });

    // ===== Firebase reCAPTCHA 초기화 =====
    function initRecaptcha() {
        recaptchaVerifier = new firebase.auth.RecaptchaVerifier("recaptcha-container", {
            size: "invisible",
            callback: (token) => {
                console.log("reCAPTCHA solved, token:", token);
            },
            "expired-callback": () => {
                phoneError.textContent = "reCAPTCHA 토큰이 만료되었습니다. 다시 시도해주세요.";
                phoneError.style.color = "red";
            }
        });
        recaptchaVerifier.render().then(widgetId => {
            console.log("reCAPTCHA rendered, widgetId:", widgetId);
        });
    }
    initRecaptcha();

    // ===== 휴대폰 인증 =====
    phoneValidateBtn.addEventListener("click", async () => {
        const phoneVal = phoneInput.value.trim();
        if (!/^\d{10,11}$/.test(phoneVal)) {
            phoneError.textContent = "유효한 휴대폰 번호를 입력하세요. (예: 01012345678)";
            phoneError.style.color = "red";
            return;
        }
        // 항상 새 인증 필요
        phoneVerified = false;
        smsCodeInput.disabled = false;
        verifySmsBtn.disabled = false;
        let formattedPhone = phoneVal;
        if (phoneVal.startsWith("010")) {
            formattedPhone = "+82" + phoneVal.substring(1);
        }
        try {
            await recaptchaVerifier.verify();
            firebase.auth().signInWithPhoneNumber(formattedPhone, recaptchaVerifier)
                .then((confirmationResult) => {
                    window.confirmationResult = confirmationResult;
                    phoneError.textContent = "인증번호가 전송되었습니다.";
                    phoneError.style.color = "green";
                })
                .catch(err => {
                    phoneError.textContent = "SMS 전송 실패: " + err.message;
                    phoneError.style.color = "red";
                });
        } catch (err) {
            phoneError.textContent = "reCAPTCHA 실패: " + err.message;
            phoneError.style.color = "red";
        }
    });

    verifySmsBtn.addEventListener("click", () => {
        const codeVal = smsCodeInput.value.trim();
        if (!codeVal) {
            smsCodeError.textContent = "인증번호를 입력하세요.";
            smsCodeError.style.color = "red";
            return;
        }
        window.confirmationResult.confirm(codeVal)
            .then(async () => {
                smsCodeError.textContent = "핸드폰 인증 완료";
                smsCodeError.style.color = "green";
                phoneVerified = true;
                try {
                    firebaseToken = await firebase.auth().currentUser.getIdToken(true);
                    console.log("Firebase Token:", firebaseToken);
                } catch (tokenErr) {
                    smsCodeError.textContent = "토큰 획득 실패: " + tokenErr.message;
                    smsCodeError.style.color = "red";
                }
            })
            .catch(err => {
                smsCodeError.textContent = "인증번호가 올바르지 않습니다.";
                smsCodeError.style.color = "red";
            });
    });

    // ===== 주소 찾기 (Daum Postcode API) =====
    addAddressBtn.addEventListener("click", () => {
        new daum.Postcode({
            oncomplete: function(data) {
                if (!data.jibunAddress || data.jibunAddress.trim() === "") {
                    addressError.textContent = "지번 주소가 제공되지 않습니다. 다시 선택해주세요.";
                    addressError.style.color = "red";
                    return;
                }
                zipcodeInput.value = data.zonecode;
                addressInput.value = data.jibunAddress;
                addressDetailInput.focus();
            }
        }).open();
    });

    // ===== 파일 첨부 및 프로필 이미지 미리보기 =====
    attachFileBtn.addEventListener("click", () => {
        fileInput.click();
    });
    fileInput.addEventListener("change", (e) => {
        const file = e.target.files[0];
        if (!file) return;
        selectedFile = file;
        dropzone.innerHTML = "";
        if (file.type.startsWith("image/")) {
            const reader = new FileReader();
            reader.onload = (evt) => {
                const img = document.createElement("img");
                img.src = evt.target.result;
                dropzone.style.lineHeight = "normal";
                dropzone.appendChild(img);
            };
            reader.readAsDataURL(file);
        } else {
            dropzone.textContent = file.name;
        }
    });

    // ===== 프로필 이미지 업로드 (S3) =====
    uploadProfileImageBtn.addEventListener("click", async () => {
        if (!selectedFile) {
            showAlert("업로드할 파일을 선택하세요.");
            return;
        }
        if (selectedFile.size > 3 * 1024 * 1024) {
            showAlert("파일 크기는 3MB를 초과할 수 없습니다.");
            return;
        }
        if (!selectedFile.type.startsWith("image/")) {
            showAlert("이미지 파일만 업로드 가능합니다.");
            return;
        }
        try {
            const formData = new FormData();
            formData.append("file", selectedFile);
            const res = await fetch("/mypage/uploadImage", {
                method: "POST",
                body: formData
            });
            if (!res.ok) {
                const errText = await res.text();
                throw new Error(errText);
            }
            const data = await res.json();
            // S3 업로드 후 받은 URL을 hidden 필드에 저장
            profileImageHidden.value = data.url;
            showAlert("이미지 업로드 성공!\nURL: " + data.url);
        } catch (err) {
            showAlert("이미지 업로드 실패: " + err.message);
        }
    });

    // ===== 프로필 수정 폼 제출 =====
    profileForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        // 필수 입력값 및 유효성 검사
        const newNick = userNameInput.value.trim();
        if (newNick.length < 2) {
            showAlert("닉네임은 최소 2글자 이상이어야 합니다.");
            return;
        }
        // 닉네임 중복은 이미 확인되었거나, 변경하지 않은 경우 통과
        if (newNick !== currentNick && !nickVerified) {
            showAlert("닉네임 중복 확인을 진행해주세요.");
            return;
        }
        const pw = passwordInput.value.trim();
        const pwConfirm = confirmPasswordInput.value.trim();
        if (pw) {
            const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&]).{8,}$/;
            if (!pwRegex.test(pw)) {
                showAlert("비밀번호는 최소 8자, 영문, 숫자, 특수문자를 포함해야 합니다.");
                return;
            }
            if (pw !== pwConfirm) {
                showAlert("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
                return;
            }
        }
        const phoneVal = phoneInput.value.trim();
        if (!/^\d{10,11}$/.test(phoneVal)) {
            showAlert("휴대폰 번호 형식이 올바르지 않습니다. (예: 01012345678)");
            return;
        }
        if (!phoneVerified) {
            showAlert("휴대폰 인증을 반드시 완료해야 합니다.");
            return;
        }
        // 주소: 만약 주소 변경을 시도하면 세 필드를 모두 입력해야 함 (입력하지 않으면 기존 값 유지)
        const newZipcode = zipcodeInput.value.trim();
        const newAddress = addressInput.value.trim();
        const newAddressDetail = addressDetailInput.value.trim();
        if ((newZipcode || newAddress || newAddressDetail) &&
            (!newZipcode || !newAddress || !newAddressDetail)) {
            showAlert("주소를 변경하려면 우편번호, 기본주소, 상세주소 모두 입력하세요.");
            return;
        }
        // (선택) 프로필 이미지 URL은 이미 업로드 후 hidden에 저장되어 있음
        const profileImgUrl = profileImageHidden.value.trim();

        // DTO 구성 (이메일은 수정 불가)
        const dto = {
            userName: newNick,
            password: pw, // 비밀번호 미입력 시 서버에서 기존 비밀번호 유지
            confirmPassword: pwConfirm,
            phone: phoneVal,
            zipcode: newZipcode,
            address: newAddress,
            addressDetail: newAddressDetail,
            profileImage: profileImgUrl,
            firebaseToken: firebaseToken
        };

        console.log("ProfileUpdate DTO:", dto);

        try {
            const res = await fetch("/mypage/profile-update", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(dto)
            });
            if (!res.ok) {
                const errMsg = await res.text();
                throw new Error(errMsg);
            }
            const resultMsg = await res.text();
            showAlert(resultMsg, () => {
                window.location.href = "/";  // 수정 완료 후 홈으로 리다이렉트
            });
        } catch (err) {
            showAlert("프로필 수정 실패: " + err.message);
        }
    });

    // ===== 취소 버튼 =====
    if (cancelBtn) {
        cancelBtn.addEventListener("click", () => {
            window.location.href = "/mypage";  // 취소 시 마이페이지로 이동
        });
    } else {
        console.error("취소 버튼을 찾을 수 없습니다.");
    }

    // ===== 공용 Alert 함수 =====
    function showAlert(message, callback = null) {
        if (customAlertModal && customAlertMessage && customAlertConfirmBtn) {
            customAlertMessage.textContent = message;
            customAlertModal.style.display = "flex";
            customAlertConfirmBtn.onclick = () => {
                customAlertModal.style.display = "none";
                if (callback) callback();
            };
        } else {
            alert(message);
            if (callback) callback();
        }
    }
});

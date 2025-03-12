/***********************************************
 * 전역 변수 및 CSRF (옵션)
 ***********************************************/
let firebaseToken = null;

// (선택) CSRF 토큰
const csrfTokenMeta = document.querySelector('meta[name="_csrf"]');
const csrfHeaderMeta = document.querySelector('meta[name="_csrf_header"]');
let csrfToken = csrfTokenMeta ? csrfTokenMeta.getAttribute("content") : "";
let csrfHeader = csrfHeaderMeta ? csrfHeaderMeta.getAttribute("content") : "";

/***********************************************
 * 요소 참조
 ***********************************************/
const signupForm = document.getElementById('signupForm');
const emailInput = document.getElementById("email");
const emailCheckBtn = document.getElementById("emailCheckBtn");
const nickInput = document.getElementById("userName");
const nickCheckBtn = document.getElementById("nickCheckBtn");
const passwordInput = document.getElementById("password");
const confirmPasswordInput = document.getElementById("confirmPassword");
const phoneInput = document.getElementById("phone");
const phoneValidateBtn = document.getElementById("phoneValidateBtn");
const smsCodeInput = document.getElementById("smsCode");
const verifySmsBtn = document.getElementById("verifySmsBtn");

const emailError = document.getElementById("emailError");
const nickError = document.getElementById("userNameError");
const passwordError = document.getElementById("passwordError");
const confirmPasswordError = document.getElementById("confirmPasswordError");
const phoneError = document.getElementById("phoneError");
const smsCodeError = document.getElementById("smsCodeError");
const passwordFeedback = document.getElementById("passwordFeedback");
const confirmFeedback = document.getElementById("confirmFeedback");
const cancelBtn = document.getElementById("cancelBtn");

// 이메일/닉네임 중복 확인 플래그
let emailVerified = false;
let nickVerified = false;

// 전역 reCAPTCHA 인스턴스 변수 (사용하지 않으므로 null)
let recaptchaVerifier = null;

/***********************************************
 * 0. 유틸리티 함수
 ***********************************************/
async function callApi(endpoint, method = 'GET', body = null) {
    const url = '/api/user' + endpoint;
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
            ...(csrfHeader && csrfToken ? { [csrfHeader]: csrfToken } : {})
        },
        ...(body ? { body: JSON.stringify(body) } : {})
    };

    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            const text = await response.text();
            throw new Error(text);
        }
        return await response.text();
    } catch (error) {
        console.error(`API 호출 실패 (${method} ${url}):`, error);
        throw error;
    }
}

function displayError(element, message) {
    element.textContent = message;
}

/***********************************************
 * 1. 동적 reCAPTCHA 컨테이너 생성 및 인스턴스 생성
 ***********************************************/
function createNewRecaptchaContainer() {
    // 기존 컨테이너 제거
    const oldContainer = document.getElementById('recaptcha-container');
    if (oldContainer) {
        oldContainer.parentNode.removeChild(oldContainer);
    }
    // 새로운 컨테이너 생성 후 body에 추가 (필요 시 원하는 부모 요소로 변경)
    const newContainer = document.createElement('div');
    newContainer.id = 'recaptcha-container';
    document.body.appendChild(newContainer);
    return newContainer;
}

function getRecaptchaVerifier() {
    // 항상 새로운 컨테이너를 생성하여 사용
    createNewRecaptchaContainer();

    // 새 recaptchaVerifier 인스턴스 생성
    recaptchaVerifier = new firebase.auth.RecaptchaVerifier('recaptcha-container', {
        size: 'invisible',
        callback: (token) => {
            console.log("✅ [reCAPTCHA] 해결 완료, 토큰:", token);
        },
        'expired-callback': () => {
            console.log("⚠️ [reCAPTCHA] 토큰 만료됨");
            alert("⚠️ reCAPTCHA가 만료되었습니다. 다시 시도해주세요.");
        }
    });

    recaptchaVerifier.render()
        .then((widgetId) => {
            console.log("✅ [reCAPTCHA] 렌더링 완료, widgetId:", widgetId);
        })
        .catch((error) => {
            console.error("❌ [reCAPTCHA] 렌더링 실패:", error);
        });
    return recaptchaVerifier;
}

/***********************************************
 * 2. 이메일 중복 확인
 ***********************************************/
emailCheckBtn.addEventListener("click", async function () {
    const email = emailInput.value.trim();
    if (!email) {
        alert("이메일을 입력하세요.");
        return;
    }
    console.log("이메일 중복 확인 요청, email:", email);

    try {
        const msg = await callApi(`/check-email?email=${encodeURIComponent(email)}`);
        alert(msg);
        emailVerified = true;
    } catch (err) {
        console.error("이메일 중복 확인 에러:", err);
        alert(err.message);
        emailVerified = false;
    }
});

/***********************************************
 * 3. 닉네임 중복 확인
 ***********************************************/
nickCheckBtn.addEventListener("click", async function () {
    const nick = nickInput.value.trim();
    if (nick.length < 2) {
        alert("닉네임은 최소 2글자 이상이어야 합니다.");
        nickVerified = false;
        return;
    }
    console.log("닉네임 중복 확인 요청, nick:", nick);

    try {
        const msg = await callApi(`/check-username?username=${encodeURIComponent(nick)}`);
        alert(msg);
        nickVerified = true;
    } catch (err) {
        console.error("닉네임 중복 확인 에러:", err);
        alert(err.message);
        nickVerified = false;
    }
});

/***********************************************
 * 4. 비밀번호 유효성 & 확인
 ***********************************************/
passwordInput.addEventListener("input", function () {
    const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    if (pwRegex.test(passwordInput.value)) {
        passwordFeedback.textContent = "적합한 비밀번호입니다.";
        passwordFeedback.style.color = "green";
    } else {
        passwordFeedback.textContent = "비밀번호는 최소 8자, 영문, 숫자, 특수문자를 포함해야 합니다.";
        passwordFeedback.style.color = "red";
    }
});

confirmPasswordInput.addEventListener("input", function () {
    if (passwordInput.value === confirmPasswordInput.value) {
        confirmFeedback.textContent = "비밀번호가 일치합니다.";
        confirmFeedback.style.color = "green";
    } else {
        confirmFeedback.textContent = "비밀번호가 일치하지 않습니다.";
        confirmFeedback.style.color = "red";
    }
});

/***********************************************
 * 5. 휴대폰 번호 유효성 검사
 ***********************************************/
phoneInput.addEventListener("blur", function () {
    let rawPhone = phoneInput.value.trim();
    if (!rawPhone.startsWith("+") && rawPhone.startsWith("0")) {
        rawPhone = "+82" + rawPhone.substring(1);
        phoneInput.value = rawPhone;
    }
    console.log("휴대폰 번호 검사, phone:", phoneInput.value);

    const finalRegex = /^\+?\d{10,14}$/;
    if (!finalRegex.test(phoneInput.value.trim())) {
        alert("유효한 핸드폰 번호를 입력하세요.");
        phoneValidateBtn.disabled = true;
    } else {
        phoneValidateBtn.disabled = false;
    }
});

/***********************************************
 * 6. 인증번호 요청 (Firebase SMS 전송)
 ***********************************************/
async function requestSmsCode() {
    let formattedPhone = phoneInput.value.trim();
    if (!formattedPhone.startsWith("+") && formattedPhone.startsWith("0")) {
        formattedPhone = "+82" + formattedPhone.substring(1);
        phoneInput.value = formattedPhone;
    }
    console.log("📌 [SMS] 요청 시작, phone:", formattedPhone);

    try {
        const verifier = getRecaptchaVerifier();
        const recaptchaToken = await verifier.verify();
        console.log("✅ [reCAPTCHA] 토큰 새로 발급 완료:", recaptchaToken);

        firebase.auth().signInWithPhoneNumber(formattedPhone, verifier)
            .then(function (confirmationResult) {
                window.confirmationResult = confirmationResult;
                console.log("📩 [SMS] 인증번호 전송 성공");
                alert("인증번호가 전송되었습니다.");
                smsCodeInput.disabled = false;
                verifySmsBtn.disabled = false;
            })
            .catch(function (error) {
                console.error("🚨 [SMS] 전송 실패:", error);
                alert("SMS 전송 실패: " + error.message);
                displayError(phoneError, "SMS 전송에 실패했습니다.");
            });
    } catch (error) {
        console.error("❌ [reCAPTCHA] 토큰 생성 실패:", error);
        displayError(phoneError, "reCAPTCHA 인증에 실패했습니다.");
        alert("⚠️ reCAPTCHA 인증을 다시 시도해주세요.");
    }
}

phoneValidateBtn.addEventListener("click", function () {
    requestSmsCode();
});

/***********************************************
 * 7. 인증번호 검증
 ***********************************************/
verifySmsBtn.addEventListener("click", function () {
    const code = smsCodeInput.value.trim();
    if (!code) {
        alert("인증번호를 입력하세요.");
        return;
    }
    console.log("📌 [SMS] 인증번호 확인 요청, 입력값:", code);

    window.confirmationResult.confirm(code)
        .then(function (result) {
            alert("📌 핸드폰 인증 완료!");
            smsCodeInput.disabled = true;
            verifySmsBtn.disabled = true;

            firebase.auth().currentUser.getIdToken(true)
                .then(function (token) {
                    firebaseToken = token;
                    console.log("🔑 [Firebase] 토큰 획득 성공:", firebaseToken);
                })
                .catch(function (error) {
                    console.error("🚨 [Firebase] 토큰 획득 실패:", error);
                });
        })
        .catch(function (error) {
            console.error("❌ [SMS] 인증 실패:", error);
            alert("🚨 인증번호가 올바르지 않습니다.");
        });
});

/***********************************************
 * 8. 회원가입 폼 제출
 ***********************************************/
signupForm.addEventListener("submit", async function (e) {
    e.preventDefault();

    const email = emailInput.value.trim();
    const userName = nickInput.value.trim();
    const password = passwordInput.value.trim();
    const confirmPassword = confirmPasswordInput.value.trim();
    let phone = phoneInput.value.trim();
    if (!phone.startsWith("+") && phone.startsWith("0")) {
        phone = "+82" + phone.substring(1);
    }
    const smsCode = smsCodeInput.value.trim();

    let hasError = false;

    if (!/^[A-Za-z0-9+_.-]+@(.+)$/.test(email)) {
        displayError(emailError, '유효한 이메일 주소를 입력하세요.');
        hasError = true;
    } else {
        displayError(emailError, '');
    }
    if (!userName) {
        displayError(nickError, '닉네임을 입력하세요.');
        hasError = true;
    } else {
        displayError(nickError, '');
    }
    const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    if (!pwRegex.test(password)) {
        displayError(passwordError, '비밀번호는 최소 8자, 영문, 숫자, 특수문자를 포함해야 합니다.');
        hasError = true;
    } else {
        displayError(passwordError, '');
    }
    if (password !== confirmPassword) {
        displayError(confirmPasswordError, '비밀번호가 일치하지 않습니다.');
        hasError = true;
    } else {
        displayError(confirmPasswordError, '');
    }
    if (!/^\+?\d{10,14}$/.test(phone)) {
        displayError(phoneError, '유효한 핸드폰 번호를 입력하세요.');
        hasError = true;
    } else {
        displayError(phoneError, '');
    }
    if (!smsCode) {
        displayError(smsCodeError, '인증번호를 입력하세요.');
        hasError = true;
    } else {
        displayError(smsCodeError, '');
    }
    if (!emailVerified) {
        alert("이메일 중복 확인이 필요합니다.");
        hasError = true;
    }
    if (!nickVerified) {
        alert("닉네임 중복 확인이 필요합니다.");
        hasError = true;
    }
    if (!smsCodeInput.disabled) {
        alert("핸드폰 인증을 완료해주세요.");
        hasError = true;
    }
    if (!firebaseToken) {
        alert("Firebase 토큰이 확인되지 않았습니다. 휴대폰 인증을 다시 시도해주세요.");
        hasError = true;
    }
    if (hasError) return;

    const userData = {
        email,
        userName,
        password,
        confirmPassword,
        phone,
        smsCode,
        firebaseToken
    };

    console.log("회원가입 데이터:", userData);

    try {
        const result = await callApi('/register', 'POST', userData);
        alert(result);
        if (result === '회원가입 성공') {
            window.location.href = '/login';
        }
    } catch (error) {
        alert(error.message);
    }
});

/***********************************************
 * 9. 취소 버튼 -> 로그인 페이지로
 ***********************************************/
if (cancelBtn) {
    cancelBtn.addEventListener('click', function () {
        window.location.href = '/login';
    });
}

/***********************************************
 * 초기화 (페이지 로드시 reCAPTCHA 생성)
 ***********************************************/
document.addEventListener('DOMContentLoaded', function() {
    getRecaptchaVerifier();
});

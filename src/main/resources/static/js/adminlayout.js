function toggleSubmenu(element, submenuId) {
    // 메뉴 토글 로직 (변경 없음)
    const submenus = document.querySelectorAll('.submenu');
    const menuItems = document.querySelectorAll('.menu-item');

    // 클릭한 상위 메뉴가 이미 활성화 상태인지 확인
    const isActive = element.classList.contains('active');

    // 모든 메뉴 항목의 활성화 상태 제거
    menuItems.forEach(item => item.classList.remove('active'));

    // 모든 서브메뉴 숨기기
    submenus.forEach(submenu => submenu.style.display = 'none');

    if (!isActive) {
        // 상위 메뉴를 활성화 상태로 설정
        element.classList.add('active');

        // 해당 서브메뉴 보이기
        const submenu = document.getElementById(submenuId);
        if (submenu) submenu.style.display = 'block';
    }
}

function activateSubmenu(element) {
    // 모든 서브메뉴 항목에서 active 클래스 제거
    const submenuItems = document.querySelectorAll('.submenu-item');
    submenuItems.forEach(item => item.classList.remove('active'));

    // 클릭한 서브메뉴 항목에 active 클래스 추가
    element.classList.add('active');
}

function logout(event) {
    event.stopPropagation();
    alert('로그아웃 되었습니다.');
    location.href = '/member/logout';
}
<%@ page contentType="text/html;charset=UTF-8" %>
<link rel="manifest" href="${pageContext.request.contextPath}/manifest.json">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Change 4: Mobile top bar (hidden on desktop) -->
<div class="mobile-topbar">
    <button class="hamburger-btn" onclick="toggleSidebar()" aria-label="Open menu">☰</button>
    <div class="mobile-logo">Carpe<em>Diem</em></div>
</div>

<!-- Overlay for mobile sidebar -->
<div class="sidebar-overlay" id="sidebarOverlay" onclick="closeSidebar()"></div>

<nav class="sidebar" id="sidebar">
    <!-- Close button (mobile only) -->
    <button class="sidebar-close-btn" onclick="closeSidebar()" aria-label="Close menu">✕</button>

    <div class="logo">Carpe<em>Diem</em></div>
    <div class="nav-group">
        <div class="nav-label">Menu</div>
        <a href="${pageContext.request.contextPath}/dashboard"
           class="nav-btn ${param.active == 'dashboard' ? 'active' : ''}">
            <span>📊</span> Dashboard
        </a>
        <!-- Change 8: Squad nav link -->
        <a href="${pageContext.request.contextPath}/group"
           class="nav-btn ${param.active == 'group' ? 'active' : ''}">
            <span>👥</span> Squad
        </a>
        <a href="${pageContext.request.contextPath}/notes"
           class="nav-btn ${param.active == 'notes' ? 'active' : ''}">
            <span>📄</span> Notes Library
        </a>
        <a href="${pageContext.request.contextPath}/todo"
           class="nav-btn ${param.active == 'todo' ? 'active' : ''}">
            <span>✅</span> Todo List
        </a>
        <a href="${pageContext.request.contextPath}/timetable"
           class="nav-btn ${param.active == 'timetable' ? 'active' : ''}">
            <span>🗓</span> Timetable
        </a>
        <a href="${pageContext.request.contextPath}/profile"
           class="nav-btn ${param.active == 'profile' ? 'active' : ''}">
            <span>👤</span> Profile
        </a>
    </div>
    <div class="nav-footer">
        <!-- Change 5: Demo mode banner -->
        <c:if test="${sessionScope.isDemo == true}">
            <div class="demo-banner">🎭 Demo Mode — resets at midnight</div>
        </c:if>
        <div class="session-user">${sessionScope.userName}</div>
        <a href="${pageContext.request.contextPath}/logout" class="logout-btn">Logout</a>
    </div>
</nav>

<!-- Change 9: Service Worker registration -->
<script>
(function() {
    const CTX_PATH = '${pageContext.request.contextPath}';
    if ('serviceWorker' in navigator) {
        window.addEventListener('load', function() {
            navigator.serviceWorker.register(CTX_PATH + '/sw.js')
                .catch(function(err) { console.log('SW registration failed:', err); });
        });
    }
})();

// Change 4: Hamburger sidebar toggle
function toggleSidebar() {
    document.getElementById('sidebar').classList.add('open');
    document.getElementById('sidebarOverlay').classList.add('active');
    document.body.style.overflow = 'hidden';
}
function closeSidebar() {
    document.getElementById('sidebar').classList.remove('open');
    document.getElementById('sidebarOverlay').classList.remove('active');
    document.body.style.overflow = '';
}
</script>

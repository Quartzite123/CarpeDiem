<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CarpeDiem — Squad</title>
    <link rel="manifest" href="${pageContext.request.contextPath}/manifest.json">
    <meta name="theme-color" content="#7c6af7">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;500;600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="header.jsp"><jsp:param name="active" value="group"/></jsp:include>
    <main class="main-content">
        <div class="page-title">👥 Squad</div>
        <div class="page-sub">The Grind Squad — your study crew</div>

        <!-- Invite Code -->
        <div class="invite-card" style="margin-bottom:24px">
            Squad Invite Code: <strong id="invite-code">${inviteCode}</strong>
            <button onclick="navigator.clipboard.writeText('${inviteCode}').then(()=>showCopied())"
                    class="btn btn-sm">Copy</button>
            <span id="copied-msg" style="display:none;color:var(--green);font-size:12px">✓ Copied!</span>
        </div>

        <!-- Member count -->
        <div style="font-size:13px;color:var(--muted);margin-bottom:16px;">
            ${members.size()} member${members.size() != 1 ? 's' : ''}
        </div>

        <!-- Member cards grid -->
        <div class="group-grid">
            <c:forEach var="u" items="${members}" varStatus="s">
                <div class="group-card ${u.id == myId ? 'group-card-me' : ''}">

                    <!-- Rank medal -->
                    <div class="group-rank ${s.index == 0 ? 'rank-gold' : s.index == 1 ? 'rank-silver' : s.index == 2 ? 'rank-bronze' : 'rank-plain'}">
                        ${s.index == 0 ? '🥇' : s.index == 1 ? '🥈' : s.index == 2 ? '🥉' : (s.index + 1)}
                    </div>

                    <!-- Avatar -->
                    <div class="group-avatar-wrap">
                        <c:choose>
                            <c:when test="${u.photoPath != null && !empty u.photoPath}">
                                <img src="${pageContext.request.contextPath}/api/photo?id=${u.id}"
                                     class="group-avatar-img" alt="${u.name}">
                            </c:when>
                            <c:otherwise>
                                <div class="group-avatar-initials">${u.initials}</div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Name + YOU badge -->
                    <div class="group-name-row">
                        <span class="group-member-name">${u.name}</span>
                        <c:if test="${u.id == myId}">
                            <span class="you-badge">YOU</span>
                        </c:if>
                    </div>

                    <!-- Stats -->
                    <div class="group-stats-row">
                        <div class="group-stat">
                            <div class="group-stat-val" style="color:var(--green)">${u.monthPct}%</div>
                            <div class="group-stat-label">Monthly</div>
                        </div>
                        <div class="group-stat">
                            <div class="group-stat-val" style="color:var(--amber)">${u.monthStreak}🔥</div>
                            <div class="group-stat-label">Mo.Streak</div>
                        </div>
                        <div class="group-stat">
                            <div class="group-stat-val" style="color:#a78bfa">${u.overallStreak}💜</div>
                            <div class="group-stat-label">Overall</div>
                        </div>
                    </div>

                    <!-- Progress bar -->
                    <div class="group-progress-bar">
                        <div class="group-progress-fill" style="width:${u.monthPct}%"></div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </main>
</div>

<script>
function showCopied() {
    const msg = document.getElementById('copied-msg');
    msg.style.display = 'inline';
    setTimeout(() => msg.style.display = 'none', 2000);
}
</script>
</body>
</html>

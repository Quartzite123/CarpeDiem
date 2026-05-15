<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CarpeDiem — Profile</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;500;600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="header.jsp"><jsp:param name="active" value="profile"/></jsp:include>
    <main class="main-content">
        <div class="page-title">👤 Profile</div>
        <div class="page-sub">Your public info and account settings</div>

        <c:if test="${param.updated == '1'}">
            <div class="alert alert-success">Profile updated successfully!</div>
        </c:if>
        <c:if test="${param.error == 'size'}">
            <div class="alert alert-error">Photo too large. Maximum size is 2MB.</div>
        </c:if>

        <div class="profile-card">
            <!-- Current avatar -->
            <div class="profile-avatar-section">
                <c:choose>
                    <c:when test="${me.photoPath != null && !empty me.photoPath}">
                        <img src="${pageContext.request.contextPath}/api/photo?id=${me.id}"
                             class="profile-big-avatar" alt="Profile photo">
                    </c:when>
                    <c:otherwise>
                        <div class="profile-big-avatar profile-initials-avatar">${me.initials}</div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Change 5/6b: Hide edit form in demo mode -->
            <c:choose>
                <c:when test="${sessionScope.isDemo == true}">
                    <div class="demo-disabled-notice" style="margin: 1rem 0;">
                        🔒 Profile editing disabled in demo mode.
                    </div>
                    <div class="field-group">
                        <label>Email</label>
                        <input type="email" value="${me.email}" disabled
                               style="opacity:.5;cursor:not-allowed">
                    </div>
                </c:when>
                <c:otherwise>
                    <form method="post" action="${pageContext.request.contextPath}/profile"
                          enctype="multipart/form-data" class="profile-form">
                        <div class="field-group">
                            <label>Display Name</label>
                            <input type="text" name="name" value="${me.name}" required>
                        </div>
                        <div class="field-group">
                            <label>Email</label>
                            <input type="email" value="${me.email}" disabled
                                   style="opacity:.5;cursor:not-allowed">
                        </div>
                        <div class="field-group">
                            <label>Profile Photo</label>
                            <input type="file" name="file" accept="image/*" class="file-input">
                            <div class="field-hint">JPG or PNG. Max 2MB.</div>
                        </div>
                        <button type="submit" class="btn btn-primary">Save Changes</button>
                    </form>
                </c:otherwise>
            </c:choose>

            <div class="profile-divider"></div>

            <div class="profile-section-title">Public Stats (visible to squad)</div>
            <div class="profile-stats-grid">
                <div class="profile-stat">
                    <div class="profile-stat-val" style="color:var(--green)">–</div>
                    <div class="profile-stat-label">Monthly %</div>
                </div>
                <div class="profile-stat">
                    <div class="profile-stat-val" style="color:var(--amber)">–</div>
                    <div class="profile-stat-label">Month Streak</div>
                </div>
                <div class="profile-stat">
                    <div class="profile-stat-val" style="color:#a78bfa">–</div>
                    <div class="profile-stat-label">Overall Streak</div>
                </div>
            </div>
            <div class="profile-note">📊 Full stats visible on the Dashboard leaderboard</div>
        </div>
    </main>
</div>
</body>
</html>

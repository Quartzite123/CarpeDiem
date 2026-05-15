<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CarpeDiem — Login</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;500;600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-body">
<div class="auth-card">
    <div class="auth-logo">Carpe<em>Diem</em></div>
    <p class="auth-sub">Squad Habit Tracker</p>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/login">
        <div class="field-group">
            <label>Email</label>
            <input type="email" name="email" required placeholder="you@email.com">
        </div>
        <div class="field-group">
            <label>Password</label>
            <input type="password" name="password" required placeholder="••••••••">
        </div>
        <button type="submit" class="btn btn-primary btn-full">Login</button>
    </form>

    <!-- Change 5: Try Demo button -->
    <div style="margin-top: 0.75rem;">
        <a href="${pageContext.request.contextPath}/demo" class="btn btn-outline btn-full"
           style="display:block; text-align:center; text-decoration:none;">
            🎭 Try Demo
        </a>
    </div>

    <div class="auth-switch">
        No account? <a href="${pageContext.request.contextPath}/signup">Sign up</a>
    </div>
</div>
</body>
</html>

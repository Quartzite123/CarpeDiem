<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CarpeDiem — Sign Up</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-body">
<div class="auth-card" style="max-width:480px">
    <div class="auth-logo">Carpe<em>Diem</em></div>
    <p class="auth-sub">Create your account</p>

    <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-error"><%= request.getAttribute("error") %></div>
    <% } %>

    <form method="post" action="${pageContext.request.contextPath}/signup" id="signup-form">
        <div class="field-group">
            <label>Your Name</label>
            <input type="text" name="name" required placeholder="e.g. Quartz">
        </div>
        <div class="field-group">
            <label>Email</label>
            <input type="email" name="email" required placeholder="you@email.com">
        </div>
        <div class="field-group">
            <label>Password</label>
            <input type="password" name="password" required placeholder="min 6 characters" minlength="6">
        </div>

        <!-- Toggle create vs join -->
        <div class="toggle-row">
            <button type="button" class="toggle-btn active" id="btn-create" onclick="setAction('create')">Create Group</button>
            <button type="button" class="toggle-btn" id="btn-join" onclick="setAction('join')">Join Group</button>
        </div>
        <input type="hidden" name="action" id="action-field" value="create">

        <div id="create-section" class="field-group">
            <label>Squad Name</label>
            <input type="text" name="groupName" id="groupName" placeholder="e.g. The Grind Squad">
        </div>
        <div id="join-section" class="field-group" style="display:none">
            <label>Invite Code</label>
            <input type="text" name="inviteCode" id="inviteCode" placeholder="6-character code" maxlength="6" style="text-transform:uppercase">
        </div>

        <button type="submit" class="btn btn-primary btn-full" style="margin-top:16px">Create Account</button>
    </form>

    <div class="auth-switch">
        Already have an account? <a href="${pageContext.request.contextPath}/login">Login</a>
    </div>
</div>
<script>
function setAction(action) {
    document.getElementById('action-field').value = action;
    document.getElementById('create-section').style.display = action === 'create' ? '' : 'none';
    document.getElementById('join-section').style.display   = action === 'join'   ? '' : 'none';
    document.getElementById('btn-create').classList.toggle('active', action === 'create');
    document.getElementById('btn-join').classList.toggle('active',   action === 'join');
    document.getElementById('groupName').required   = action === 'create';
    document.getElementById('inviteCode').required  = action === 'join';
}
setAction('create');
</script>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CarpeDiem — Try Demo</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;500;600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .demo-body {
            min-height: 100vh;
            background: var(--bg);
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            padding: 2rem 1rem;
            font-family: 'Space Grotesk', sans-serif;
        }
        .demo-logo {
            font-size: 2.8rem;
            font-weight: 700;
            color: var(--text);
            letter-spacing: -1px;
            margin-bottom: 0.5rem;
        }
        .demo-logo em { color: var(--accent); font-style: normal; }
        .demo-tagline {
            color: var(--text2);
            font-size: 1.05rem;
            text-align: center;
            max-width: 420px;
            margin-bottom: 2.5rem;
            line-height: 1.6;
        }
        .demo-features {
            display: grid;
            grid-template-columns: repeat(2, 1fr);
            gap: 1rem;
            max-width: 480px;
            width: 100%;
            margin-bottom: 2.5rem;
        }
        .demo-feature-card {
            background: var(--s1);
            border: 1px solid var(--border);
            border-radius: 12px;
            padding: 1.1rem 1rem;
        }
        .demo-feature-card .feat-icon { font-size: 1.4rem; margin-bottom: 0.4rem; }
        .demo-feature-card .feat-title {
            font-size: 0.85rem;
            font-weight: 600;
            color: var(--text);
            margin-bottom: 0.25rem;
        }
        .demo-feature-card .feat-desc {
            font-size: 0.75rem;
            color: var(--text2);
            line-height: 1.4;
        }
        .demo-btn-wrap { text-align: center; width: 100%; max-width: 280px; }
        .btn-demo-main {
            display: block;
            width: 100%;
            padding: 0.9rem 1.5rem;
            background: var(--accent);
            color: #fff;
            border: none;
            border-radius: 10px;
            font-family: 'Space Grotesk', sans-serif;
            font-size: 1.05rem;
            font-weight: 600;
            cursor: pointer;
            transition: opacity 0.15s;
        }
        .btn-demo-main:hover { opacity: 0.88; }
        .demo-note {
            margin-top: 0.8rem;
            font-size: 0.75rem;
            color: var(--text2);
        }
        .demo-stack {
            display: flex;
            flex-wrap: wrap;
            gap: 0.5rem;
            justify-content: center;
            margin-top: 2rem;
            max-width: 480px;
        }
        .stack-pill {
            background: var(--s1);
            border: 1px solid var(--border);
            border-radius: 999px;
            padding: 0.3rem 0.75rem;
            font-size: 0.7rem;
            color: var(--text2);
            font-weight: 500;
        }
        .demo-real-login {
            margin-top: 1.5rem;
            font-size: 0.82rem;
            color: var(--text2);
        }
        .demo-real-login a { color: var(--accent); text-decoration: none; }
        .alert-error {
            background: rgba(239,68,68,0.12);
            color: #f87171;
            border-radius: 8px;
            padding: 0.7rem 1rem;
            font-size: 0.88rem;
            margin-bottom: 1rem;
            max-width: 340px;
            text-align: center;
        }
        @media (max-width: 480px) {
            .demo-features { grid-template-columns: 1fr; }
        }
    </style>
</head>
<body class="demo-body">

    <div class="demo-logo">Carpe<em>Diem</em></div>
    <p class="demo-tagline">
        A squad habit tracker for competitive study groups. Track daily habits,
        share notes, climb the leaderboard — together.
    </p>

    <c:if test="${not empty error}">
        <div class="alert-error">${error}</div>
    </c:if>

    <div class="demo-features">
        <div class="demo-feature-card">
            <div class="feat-icon">📊</div>
            <div class="feat-title">Habit Grid</div>
            <div class="feat-desc">Check off daily habits. See your streak at a glance.</div>
        </div>
        <div class="demo-feature-card">
            <div class="feat-icon">🏆</div>
            <div class="feat-title">Leaderboard</div>
            <div class="feat-desc">Compete with your squad on completion rate & streaks.</div>
        </div>
        <div class="demo-feature-card">
            <div class="feat-icon">📄</div>
            <div class="feat-title">PDF Notes</div>
            <div class="feat-desc">Share study notes with your entire squad instantly.</div>
        </div>
        <div class="demo-feature-card">
            <div class="feat-icon">✅</div>
            <div class="feat-title">Todo List</div>
            <div class="feat-desc">Personal task manager with priorities and due dates.</div>
        </div>
    </div>

    <div class="demo-btn-wrap">
        <form method="post" action="${pageContext.request.contextPath}/demo">
            <button type="submit" class="btn-demo-main">🎭 Try as Guest</button>
        </form>
        <div class="demo-note">No signup. Data resets every midnight.</div>
    </div>

    <div class="demo-stack">
        <span class="stack-pill">Java 17</span>
        <span class="stack-pill">Servlets</span>
        <span class="stack-pill">JSP + JSTL</span>
        <span class="stack-pill">MySQL 8</span>
        <span class="stack-pill">Tomcat 9</span>
        <span class="stack-pill">Maven</span>
        <span class="stack-pill">Docker</span>
        <span class="stack-pill">Railway</span>
    </div>

    <div class="demo-real-login">
        <a href="${pageContext.request.contextPath}/login">← Real login</a>
    </div>

</body>
</html>

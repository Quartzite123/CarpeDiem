<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CarpeDiem — Dashboard</title>
    <link rel="manifest" href="${pageContext.request.contextPath}/manifest.json">
    <meta name="theme-color" content="#7c6af7">
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;500;600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
<div class="app-layout">
    <jsp:include page="header.jsp"><jsp:param name="active" value="dashboard"/></jsp:include>

    <main class="main-content">
        <div class="page-title">Dashboard</div>
        <div class="page-sub" id="today-label"></div>

        <!-- Stat Cards -->
        <div class="stat-row">
            <div class="stat-card">
                <div class="stat-tag">Monthly %</div>
                <div class="stat-val" style="color:var(--green)">${me.monthPct}%</div>
                <div class="stat-sub">completion this month</div>
            </div>
            <div class="stat-card">
                <div class="stat-tag">Month Streak 🔥</div>
                <div class="stat-val" style="color:var(--amber)">${me.monthStreak}</div>
                <div class="stat-sub">consecutive full days</div>
            </div>
            <div class="stat-card">
                <div class="stat-tag">Overall Streak 💜</div>
                <div class="stat-val" style="color:#a78bfa">${me.overallStreak}</div>
                <div class="stat-sub">all-time best run</div>
            </div>
            <div class="stat-card">
                <div class="stat-tag">This Week</div>
                <div class="stat-val" style="color:var(--accent)">${me.weekFullDays}</div>
                <div class="stat-sub">full days in 7 days</div>
            </div>
        </div>

        <!-- Subject % chips -->
        <div class="task-pct-row">
            <c:forEach var="task" items="${tasks}">
                <div class="task-pct-chip">
                    <span class="task-pct-name">${task.name}</span>
                    <div class="task-pct-bar">
                        <div class="task-pct-fill" style="width:${taskPcts[task.id] != null ? taskPcts[task.id] : 0}%;background:${taskPcts[task.id] != null && taskPcts[task.id] >= 70 ? 'var(--green)' : taskPcts[task.id] != null && taskPcts[task.id] >= 40 ? 'var(--amber)' : 'var(--red)'}"></div>
                    </div>
                    <span class="task-pct-val">${taskPcts[task.id] != null ? taskPcts[task.id] : 0}%</span>
                </div>
            </c:forEach>
        </div>

        <!-- Habit Grid -->
        <div class="grid-card">
            <div class="grid-card-title">
                <span>Habit Grid — <c:out value="${month}"/>/<c:out value="${year}"/></span>
                <button class="edit-tasks-btn" onclick="toggleEditMode()">✏️ Edit Subjects</button>
            </div>
            <div class="g-wrap" id="habit-grid">
                <!-- Day % row -->
                <div class="g-head-row">
                    <div class="g-label-wrap"><div class="g-label dim" style="font-size:9px">Day%</div></div>
                    <c:forEach begin="1" end="${daysInMonth}" var="d">
                        <div class="g-day-pct" id="daypct-${d}"></div>
                    </c:forEach>
                </div>
                <!-- Day numbers -->
                <div class="g-head-row">
                    <div class="g-label-wrap"><div class="g-label dim"></div></div>
                    <c:forEach begin="1" end="${daysInMonth}" var="d">
                        <div class="g-day-lbl ${d == today ? 'today' : ''}">${d}</div>
                    </c:forEach>
                </div>
                <!-- Task rows -->
                <c:forEach var="task" items="${tasks}">
                    <div class="g-row" data-task-id="${task.id}">
                        <div class="g-label-wrap">
                            <span class="g-label task-label" id="label-${task.id}">${task.name}</span>
                            <input class="g-label-input" id="input-${task.id}" value="${task.name}"
                                   style="display:none"
                                   onblur="renameTask(${task.id}, this.value)"
                                   onkeydown="if(event.key==='Enter')this.blur()">
                            <span class="g-pct" id="tpct-${task.id}">${taskPcts[task.id] != null ? taskPcts[task.id] : 0}%</span>
                        </div>
                        <c:forEach begin="1" end="${daysInMonth}" var="d">
                            <c:set var="done" value="${grid[task.id] != null && grid[task.id][d] == true}"/>
                            <c:set var="isFuture" value="${d > today}"/>
                            <c:set var="isPast"   value="${d < today}"/>
                            <div class="cell
                                ${done ? ' done' : ''}
                                ${isFuture ? ' locked future' : ''}
                                ${isPast   ? ' locked past'   : ''}"
                                 data-task="${task.id}"
                                 data-day="${d}"
                                 data-year="${year}"
                                 data-month="${month}"
                                 onclick="${(!isFuture && !isPast) ? 'toggleCell(this)' : ''}">
                            </div>
                        </c:forEach>
                    </div>
                </c:forEach>

                <!-- Mood row -->
                <div class="g-row row-divider">
                    <div class="g-label-wrap"><div class="g-label dim">😊 Mood</div></div>
                    <c:forEach begin="1" end="${daysInMonth}" var="d">
                        <div class="mood-cell ${d != today ? 'locked' : ''}"
                             id="mood-${d}"
                             onclick="${d == today ? 'openMood(event,' + d + ')' : ''}">
                            ${d == today && todayMeta != null ? todayMeta.mood : ''}
                        </div>
                    </c:forEach>
                </div>
                <!-- Hours row -->
                <div class="g-row">
                    <div class="g-label-wrap"><div class="g-label dim">⏰ Hrs</div></div>
                    <c:forEach begin="1" end="${daysInMonth}" var="d">
                        <div class="hours-wrap">
                            <input class="hours-input" type="number" min="0" max="24" step="0.5"
                                   value="${d == today && todayMeta != null ? todayMeta.studyHours : ''}"
                                   placeholder="0"
                                   ${d != today ? 'disabled' : ''}
                                   onchange="saveMeta()">
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>

        <!-- Today's Notes -->
        <div class="notes-card">
            <div class="notes-card-title">📝 Today's Notes</div>
            <textarea id="day-notes" placeholder="What did you learn today?" rows="3"
                      onblur="saveMeta()">${todayMeta != null ? todayMeta.notes : ''}</textarea>
        </div>

        <!-- Leaderboard -->
        <div class="section-title" style="margin:24px 0 12px">Squad Leaderboard</div>
        <div class="rank-card" id="leaderboard-container">
            <div class="rank-thead">
                <span>#</span><span>Member</span><span>Monthly%</span><span>Month🔥</span><span>Overall💜</span><span>Week✓</span>
            </div>
            <div id="rank-rows">
                <c:forEach var="u" items="${leaderboard}" varStatus="s">
                    <div class="rank-row">
                        <span class="rank-num ${s.index==0?'g1':s.index==1?'g2':s.index==2?'g3':''}">${s.index+1}</span>
                        <div class="person-cell">
                            <c:choose>
                                <c:when test="${u.photoPath != null && !empty u.photoPath}">
                                    <div class="nav-avatar" style="background-image:url('${pageContext.request.contextPath}/api/photo?id=${u.id}');background-size:cover"></div>
                                </c:when>
                                <c:otherwise>
                                    <div class="nav-avatar" style="background:rgba(124,106,247,.2);color:var(--accent)">${u.initials}</div>
                                </c:otherwise>
                            </c:choose>
                            <span>${u.name}</span>
                            <div class="pct-bar"><div class="pct-fill" style="width:${u.monthPct}%"></div></div>
                        </div>
                        <span class="fw6">${u.monthPct}%</span>
                        <span class="streak-val">${u.monthStreak}🔥</span>
                        <span class="overall-streak">${u.overallStreak}💜</span>
                        <span class="week-val">${u.weekFullDays}</span>
                    </div>
                </c:forEach>
            </div>
        </div>

        <!-- Chart -->
        <div class="chart-card">
            <canvas id="lb-chart" height="100"></canvas>
        </div>

        <!-- Invite Code -->
        <div class="invite-card">
            Squad Invite Code: <strong id="invite-code">${inviteCode}</strong>
            <button onclick="navigator.clipboard.writeText('${inviteCode}').then(()=>alert('Copied!'))" class="btn btn-sm">Copy</button>
        </div>
    </main>
</div>

<!-- Mood Picker -->
<div id="mood-picker-popup" class="mood-picker" style="display:none">
    <c:forEach var="m" items="${['😄','🙂','😐','😞','😴']}">
        <span class="mood-opt" onclick="selectMood('${m}')">${m}</span>
    </c:forEach>
</div>

<!-- Change 11: Toast container -->
<div id="toast" class="toast" role="status" aria-live="polite"></div>

<script src="${pageContext.request.contextPath}/js/dashboard.js"></script>
<script>
const CTX_PATH = '${pageContext.request.contextPath}';
const TODAY_NUM = ${today};
const YEAR      = ${year};
const MONTH     = ${month};
const DAYS_IN_MONTH = ${daysInMonth};

// Change 11: Toast helper (exposed on window so dashboard.js can use it)
window.showToast = function(message, type) {
    const t = document.getElementById('toast');
    if (!t) return;
    t.textContent = message;
    t.className = 'toast show' + (type ? ' toast-' + type : '');
    clearTimeout(window._toastTimer);
    window._toastTimer = setTimeout(() => { t.className = 'toast'; }, 2400);
};

// Change 11: Auto-scroll habit grid so today's column is centered in view
requestAnimationFrame(() => {
    const grid = document.querySelector('.grid-card');
    const todayLbl = document.querySelector('.g-day-lbl.today');
    if (!grid || !todayLbl) return;
    const gridRect = grid.getBoundingClientRect();
    const lblRect  = todayLbl.getBoundingClientRect();
    const target = grid.scrollLeft + (lblRect.left - gridRect.left)
                   - (grid.clientWidth / 2) + (lblRect.width / 2);
    grid.scrollLeft = Math.max(0, target);
});

// Chart.js leaderboard bar chart
const lbData = {
    labels: [<c:forEach var="u" items="${leaderboard}" varStatus="s">'${u.name}'${!s.last ? ',' : ''}</c:forEach>],
    datasets: [{
        label: 'Monthly Completion %',
        data: [<c:forEach var="u" items="${leaderboard}" varStatus="s">${u.monthPct}${!s.last ? ',' : ''}</c:forEach>],
        backgroundColor: 'rgba(124,106,247,0.35)',
        borderColor: 'rgba(124,106,247,1)',
        borderWidth: 2, borderRadius: 6
    }]
};
const lbChart = new Chart(document.getElementById('lb-chart'), {
    type: 'bar',
    data: lbData,
    options: {
        responsive: true,
        plugins: { legend: { display: false } },
        scales: {
            y: { min:0, max:100, grid:{ color:'rgba(255,255,255,0.05)' }, ticks:{ color:'#52527a' } },
            x: { grid:{ display:false }, ticks:{ color:'#e8e8f0' } }
        }
    }
});

document.getElementById('today-label').textContent =
    new Date().toLocaleDateString('en-IN', {weekday:'long', year:'numeric', month:'long', day:'numeric'});

computeDayPcts();
</script>
</body>
</html>

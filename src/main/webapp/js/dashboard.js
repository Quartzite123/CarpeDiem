// CarpeDiem — Dashboard JS
// Handles: toggle cells, mood picker, day meta save, leaderboard polling

const MOODS = ['😄','🙂','😐','😞','😴'];
let editMode  = false;
let activeMoodDay = null;

// ── TOGGLE CELL ──────────────────────────────────────────
function toggleCell(cellEl) {
    const taskId = cellEl.dataset.task;
    const day    = cellEl.dataset.day;
    const year   = cellEl.dataset.year;
    const month  = String(cellEl.dataset.month).padStart(2, '0');
    const day0   = String(day).padStart(2, '0');
    const date   = `${year}-${month}-${day0}`;

    fetch(CTX_PATH + '/api/toggle', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `taskId=${taskId}&date=${date}`
    })
    .then(r => r.json())
    .then(data => {
        if (data.done !== undefined) {
            cellEl.classList.toggle('done', data.done);
            // pop animation
            cellEl.classList.add('pop');
            setTimeout(() => cellEl.classList.remove('pop'), 200);
            computeDayPcts();
        }
    })
    .catch(console.error);
}

// ── DAY % COMPUTATION ────────────────────────────────────
function computeDayPcts() {
    const taskRows = document.querySelectorAll('.g-row[data-task-id]');
    const taskCount = taskRows.length;
    if (taskCount === 0) return;

    for (let d = 1; d <= DAYS_IN_MONTH; d++) {
        let done = 0;
        taskRows.forEach(row => {
            const cell = row.querySelector(`.cell[data-day="${d}"]`);
            if (cell && cell.classList.contains('done')) done++;
        });
        const pctEl = document.getElementById('daypct-' + d);
        if (!pctEl) continue;
        if (d > TODAY_NUM) { pctEl.textContent = ''; continue; }
        const pct = Math.round(done / taskCount * 100);
        pctEl.textContent = pct + '%';
        pctEl.className = 'g-day-pct' + (pct === 100 ? ' full' : pct >= 50 ? ' good' : '');
    }
}

// ── MOOD PICKER ──────────────────────────────────────────
function openMood(event, day) {
    event.stopPropagation();
    const existing = document.getElementById('mood-picker-popup');
    if (existing) {
        existing.style.display = 'none';
        if (activeMoodDay === day) { activeMoodDay = null; return; }
    }
    activeMoodDay = day;
    const moodCell = document.getElementById('mood-' + day);
    const picker = document.getElementById('mood-picker-popup');
    picker.innerHTML = MOODS.map(m =>
        `<span class="mood-opt" onclick="selectMood('${m}')">${m}</span>`).join('');
    picker.style.display = 'flex';

    const rect = moodCell.getBoundingClientRect();
    picker.style.top  = (rect.bottom + 6 + window.scrollY) + 'px';
    picker.style.left = (rect.left + window.scrollX) + 'px';
}

function selectMood(mood) {
    if (activeMoodDay === null) return;
    document.getElementById('mood-' + activeMoodDay).textContent = mood;
    document.getElementById('mood-picker-popup').style.display = 'none';
    activeMoodDay = null;
    saveMeta();
}

document.addEventListener('click', () => {
    const p = document.getElementById('mood-picker-popup');
    if (p) p.style.display = 'none';
    activeMoodDay = null;
});

// ── SAVE META (mood + hours + notes) ─────────────────────
function saveMeta() {
    const moodEl  = document.getElementById('mood-' + TODAY_NUM);
    const hoursEl = document.querySelector('.hours-input:not([disabled])');
    const notesEl = document.getElementById('day-notes');

    const mood  = moodEl  ? moodEl.textContent.trim()  : '';
    const hours = hoursEl ? hoursEl.value : '0';
    const notes = notesEl ? notesEl.value : '';

    const body = `mood=${encodeURIComponent(mood)}&hours=${hours}&notes=${encodeURIComponent(notes)}`;
    fetch(CTX_PATH + '/api/meta', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body
    }).catch(console.error);
}

// ── TASK RENAME (edit mode) ──────────────────────────────
function toggleEditMode() {
    editMode = !editMode;
    document.querySelectorAll('.g-row[data-task-id]').forEach(row => {
        const taskId = row.dataset.taskId;
        const label  = document.getElementById('label-' + taskId);
        const input  = document.getElementById('input-' + taskId);
        if (label && input) {
            label.style.display = editMode ? 'none' : '';
            input.style.display = editMode ? '' : 'none';
        }
    });
    const btn = document.querySelector('.edit-tasks-btn');
    if (btn) btn.textContent = editMode ? '✅ Done Editing' : '✏️ Edit Subjects';
}

function renameTask(taskId, newName) {
    newName = newName.trim();
    if (!newName) return;
    fetch(CTX_PATH + '/api/task/rename', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `taskId=${taskId}&name=${encodeURIComponent(newName)}`
    })
    .then(r => r.json())
    .then(data => {
        if (data.ok) {
            const label = document.getElementById('label-' + taskId);
            if (label) label.textContent = data.name;
        }
    })
    .catch(console.error);
}

// ── LEADERBOARD POLLING (every 30s) ──────────────────────
function pollLeaderboard() {
    fetch(CTX_PATH + '/api/leaderboard')
    .then(r => r.json())
    .then(data => {
        const container = document.getElementById('rank-rows');
        if (!container || !Array.isArray(data)) return;
        const rankClass = ['g1','g2','g3',''];
        container.innerHTML = data.map((u, i) => `
            <div class="rank-row">
                <span class="rank-num ${rankClass[i] || ''}">${u.rank}</span>
                <div class="person-cell">
                    <div class="nav-avatar" style="${u.hasPhoto
                        ? `background-image:url('${CTX_PATH}/api/photo?id=${u.id}');background-size:cover`
                        : 'background:rgba(124,106,247,.2);color:var(--accent)'}">
                        ${u.hasPhoto ? '' : u.initials}
                    </div>
                    <span>${u.name}</span>
                    <div class="pct-bar"><div class="pct-fill" style="width:${u.monthPct}%"></div></div>
                </div>
                <span class="fw6">${u.monthPct}%</span>
                <span class="streak-val">${u.monthStreak}🔥</span>
                <span class="overall-streak">${u.overallStreak}💜</span>
                <span class="week-val">${u.weekFullDays}</span>
            </div>`).join('');
    })
    .catch(console.error);
}

setInterval(pollLeaderboard, 30000);

// Timetable overlay canvas drawing

const canvas  = document.getElementById('overlay-canvas');
const ctx2d   = canvas.getContext('2d');
const base    = document.getElementById('tt-image') || document.getElementById('tt-iframe');
let drawing   = false;
let lastX = 0, lastY = 0;

function resizeCanvas() {
    const view = document.getElementById('tt-view');
    canvas.width  = view.offsetWidth;
    canvas.height = view.offsetHeight;
    if (SAVED_EDITS) loadEdits();
}

window.addEventListener('resize', resizeCanvas);
setTimeout(resizeCanvas, 300); // wait for image/iframe to load

// Draw
canvas.addEventListener('mousedown', e => { drawing = true; [lastX, lastY] = getPos(e); });
canvas.addEventListener('mousemove', e => {
    if (!drawing) return;
    ctx2d.strokeStyle = document.getElementById('pen-color').value;
    ctx2d.lineWidth   = document.getElementById('pen-size').value;
    ctx2d.lineCap     = 'round';
    ctx2d.lineJoin    = 'round';
    ctx2d.beginPath();
    ctx2d.moveTo(lastX, lastY);
    const [x, y] = getPos(e);
    ctx2d.lineTo(x, y);
    ctx2d.stroke();
    [lastX, lastY] = [x, y];
});
canvas.addEventListener('mouseup',    () => drawing = false);
canvas.addEventListener('mouseleave', () => drawing = false);

// Touch support
canvas.addEventListener('touchstart', e => {
    e.preventDefault();
    drawing = true;
    [lastX, lastY] = getPos(e.touches[0]);
});
canvas.addEventListener('touchmove', e => {
    e.preventDefault();
    if (!drawing) return;
    ctx2d.strokeStyle = document.getElementById('pen-color').value;
    ctx2d.lineWidth   = document.getElementById('pen-size').value;
    ctx2d.lineCap = 'round'; ctx2d.lineJoin = 'round';
    ctx2d.beginPath();
    ctx2d.moveTo(lastX, lastY);
    const [x, y] = getPos(e.touches[0]);
    ctx2d.lineTo(x, y);
    ctx2d.stroke();
    [lastX, lastY] = [x, y];
});
canvas.addEventListener('touchend', () => drawing = false);

function getPos(e) {
    const rect = canvas.getBoundingClientRect();
    return [e.clientX - rect.left, e.clientY - rect.top];
}

function clearCanvas() {
    if (!confirm('Clear all annotations?')) return;
    ctx2d.clearRect(0, 0, canvas.width, canvas.height);
}

function saveEdits() {
    const dataUrl = canvas.toDataURL();
    const status  = document.getElementById('save-status');
    fetch(CTX_PATH + '/timetable', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: `action=saveEdits&edits=${encodeURIComponent(dataUrl)}`
    })
    .then(r => r.json())
    .then(() => { status.textContent = '✅ Saved'; setTimeout(() => status.textContent = '', 2000); })
    .catch(() => { status.textContent = '❌ Save failed'; });
}

function loadEdits() {
    if (!SAVED_EDITS || SAVED_EDITS.trim() === '') return;
    const img = new Image();
    img.onload = () => ctx2d.drawImage(img, 0, 0);
    img.src = SAVED_EDITS;
}

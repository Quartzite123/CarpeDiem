// CarpeDiem — Service Worker (Change 9)
const CACHE_NAME = 'carpediem-offline-v1';

// Static assets to cache on install
const STATIC_ASSETS = [
    '/css/style.css',
    '/js/dashboard.js',
    '/js/timetable.js'
];

// ── INSTALL: cache static assets ─────────────────────────
self.addEventListener('install', event => {
    event.waitUntil(
        caches.open(CACHE_NAME).then(cache => {
            // Use individual adds so one failure doesn't abort the whole install
            return Promise.allSettled(
                STATIC_ASSETS.map(url => cache.add(url).catch(() => {}))
            );
        })
    );
    self.skipWaiting();
});

// ── ACTIVATE: clean up old caches ────────────────────────
self.addEventListener('activate', event => {
    event.waitUntil(
        caches.keys().then(keys =>
            Promise.all(
                keys.filter(k => k !== CACHE_NAME).map(k => caches.delete(k))
            )
        )
    );
    self.clients.claim();
});

// ── FETCH: routing strategy ───────────────────────────────
self.addEventListener('fetch', event => {
    if (event.request.method !== 'GET') return;
    const url = new URL(event.request.url);

    // PDF download — cache-first
    if (url.pathname.includes('/notes') && url.searchParams.get('action') === 'download') {
        event.respondWith(cacheFirst(event.request));
        return;
    }

    // Timetable serve — cache-first
    if (url.pathname.includes('/timetable') && url.searchParams.get('action') === 'serve') {
        event.respondWith(cacheFirst(event.request));
        return;
    }

    // Everything else — network-first with cache fallback
    event.respondWith(networkFirst(event.request));
});

// ── Strategies ───────────────────────────────────────────

async function cacheFirst(request) {
    const cached = await caches.match(request);
    if (cached) return cached;
    try {
        const response = await fetch(request);
        if (response.ok) {
            const cache = await caches.open(CACHE_NAME);
            cache.put(request, response.clone());
        }
        return response;
    } catch {
        return new Response('Offline — resource not cached.', { status: 503 });
    }
}

async function networkFirst(request) {
    try {
        const response = await fetch(request);
        if (response.ok) {
            const cache = await caches.open(CACHE_NAME);
            cache.put(request, response.clone());
        }
        return response;
    } catch {
        const cached = await caches.match(request);
        return cached || new Response('Offline', { status: 503 });
    }
}

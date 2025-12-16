const apiBase = '';

function getToken() {
    return localStorage.getItem('jwt');
}

function setToken(token) {
    if (token) {
        localStorage.setItem('jwt', token);
    }
}

function setFiId(id) {
    if (id) {
        localStorage.setItem('fiId', id);
    }
}

function getFiId() {
    const params = new URLSearchParams(window.location.search);
    const fromQuery = params.get('fiId');
    if (fromQuery) {
        setFiId(fromQuery);
        return fromQuery;
    }
    return localStorage.getItem('fiId');
}

function authHeaders() {
    const token = getToken();
    return token ? { 'Authorization': 'Bearer ' + token } : {};
}

async function apiFetch(path, options = {}) {
    const response = await fetch(apiBase + path, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            ...(options.headers || {}),
            ...authHeaders()
        }
    });
    const text = await response.text();
    let data = text;
    try {
        data = text ? JSON.parse(text) : null;
    } catch (e) {
        // leave as text
    }
    if (!response.ok) {
        throw { status: response.status, data };
    }
    return data;
}

function showMessage(elementId, message, isError = false) {
    const el = document.getElementById(elementId);
    if (el) {
        el.textContent = message;
        el.className = isError ? 'status error' : 'status';
    }
}

function renderStatus(data, targetId = 'status') {
    const el = document.getElementById(targetId);
    if (!el || !data) return;
    el.textContent = `Status: ${data.status || 'n/a'} | FI ID: ${data.id || 'n/a'} | Name: ${data.legalName || ''}`;
    el.className = 'status';
}

function populateFiLink(id) {
    const links = document.querySelectorAll('.fi-link');
    links.forEach(link => {
        const href = new URL(link.getAttribute('href'), window.location.origin);
        href.searchParams.set('fiId', id);
        link.setAttribute('href', href.pathname + href.search);
    });
}

function loadExistingFi(targetId = 'currentFi') {
    const fiId = getFiId();
    if (!fiId) {
        showMessage(targetId, 'No FI selected yet.', true);
        return;
    }
    apiFetch(`/api/registry/${fiId}`)
        .then(data => {
            renderStatus(data, targetId);
            populateFiLink(fiId);
        })
        .catch(err => showMessage(targetId, `Load failed: ${JSON.stringify(err.data || err)}`, true));
}

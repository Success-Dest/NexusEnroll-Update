/* ---------------------------
   Helper: toast notifications
   --------------------------- */
function toast(message, type = 'info', ttl = 3500) {
    const id = 't' + Math.random().toString(36).slice(2, 9);
    const wrap = document.getElementById('toastContainer');
    const color = type === 'error' ? 'bg-red-600' : (type === 'success' ? 'bg-emerald-600' : 'bg-slate-800');
    const el = document.createElement('div');
    el.id = id;
    el.className = color + ' text-white px-4 py-3 rounded shadow mb-2';
    el.style.minWidth = '200px';
    el.innerText = message;
    wrap.appendChild(el);
    setTimeout(() => {
        el.classList.add('opacity-0');
        setTimeout(() => el.remove(), 300);
    }, ttl);
}
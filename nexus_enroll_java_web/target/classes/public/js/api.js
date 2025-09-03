/* ---------------------------
   API helper
   --------------------------- */
async function api(path, method = 'GET', body = null) {
    const opts = { method, headers: {} };
    if (body) {
        opts.headers['Content-Type'] = 'application/json';
        opts.body = JSON.stringify(body);
    }
    
    try {
        const res = await fetch(path, opts);
        if (!res.ok) {
            const text = await res.text();
            throw new Error(res.status + ' ' + res.statusText + ' - ' + text);
        }
        return await res.json();
    } catch (err) {
        throw err;
    }
}

/* ---------------------------
   Catalog rendering
   --------------------------- */
async function renderCatalog() {
    const container = document.getElementById('catalog');
    container.innerHTML = '<div class="py-8 text-center text-slate-400">Loading catalog…</div>';
    try {
        const resp = await api('/api/catalog');
        const courses = resp.courses || [];
        if (courses.length === 0) {
            container.innerHTML = '<div class="py-6 text-center text-slate-400">No courses available</div>';
            return;
        }

        container.innerHTML = '';
        courses.forEach(course => {
            const card = document.createElement('div');
            card.className = 'border border-slate-100 rounded p-4 bg-white shadow-sm';
            const title = document.createElement('div');
            title.innerHTML = `<div class="flex items-center justify-between"><div><div class="text-sm font-medium">${escape(course.courseId)} — ${escape(course.name)}</div><div class="text-xs text-slate-400">Prereqs: ${course.prerequisites.join(', ') || 'None'}</div></div><div class="text-xs text-slate-500">${(course.sections || []).length} section(s)</div></div>`;
            card.appendChild(title);

            const tableWrap = document.createElement('div');
            tableWrap.className = 'mt-3 overflow-x-auto';
            const tbl = document.createElement('table');
            tbl.className = 'min-w-full text-sm';
            tbl.innerHTML = `<thead class="text-xs text-slate-500"><tr><th class="px-2 py-2 text-left">Section</th><th class="px-2 py-2 text-left">Instructor</th><th class="px-2 py-2 text-left">Capacity</th><th class="px-2 py-2 text-left">Enrolled</th><th class="px-2 py-2 text-left">Day/Time</th></tr></thead>`;
            const tbody = document.createElement('tbody');

            (course.sections || []).forEach(s => {
                const tr = document.createElement('tr');
                tr.innerHTML = `<td class="px-2 py-2">${escape(s.sectionId)}</td>
                        <td class="px-2 py-2">${escape(s.instructorId || '')}</td>
                        <td class="px-2 py-2">${s.capacity}</td>
                        <td class="px-2 py-2">${s.enrolledCount}</td>
                        <td class="px-2 py-2">${escape(s.day || '-')} ${escape(s.start || '')}-${escape(s.end || '')}</td>`;
                tbody.appendChild(tr);
            });

            tbl.appendChild(tbody);
            tableWrap.appendChild(tbl);
            card.appendChild(tableWrap);
            container.appendChild(card);
        });

    } catch (err) {
        container.innerHTML = `<div class="py-6 text-center text-red-600">Failed to load catalog: ${escape(err.message)}</div>`;
        console.error(err);
    }
}

/* ---------------------------
   Students: load & add
   --------------------------- */
async function loadStudents() {
    const tbody = document.getElementById('studentsList');
    tbody.innerHTML = '<tr><td colspan="4" class="px-3 py-4 text-slate-400">Loading…</td></tr>';
    try {
        const data = await api('/api/students');
        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="px-3 py-4 text-slate-400">No students found</td></tr>';
            return;
        }
        tbody.innerHTML = '';
        data.forEach(s => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td class="px-3 py-2">${escape(s.id)}</td>
                      <td class="px-3 py-2">${escape(s.name)}</td>
                      <td class="px-3 py-2">${escape(s.email || '')}</td>
                      <td class="px-3 py-2">${(s.completedCourses || []).join(', ')}</td>`;
            tbody.appendChild(tr);
        });
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="4" class="px-3 py-4 text-red-600">Error: ${escape(err.message)}</td></tr>`;
    }
}

async function addStudent() {
    const id = document.getElementById('newStudentId').value.trim();
    const name = document.getElementById('newStudentName').value.trim();
    const email = document.getElementById('newStudentEmail').value.trim();
    if (!name) {
        toast('Name is required', 'error');
        return;
    }
    try {
        const body = { id: id || null, name, email: email || '' };
        const res = await api('/api/students', 'POST', body);
        if (res && res.id) {
            document.getElementById('addStudentResult').innerText = 'Added: ' + res.id;
            toast('Student created: ' + res.id, 'success');
            // clear form
            document.getElementById('newStudentId').value = '';
            document.getElementById('newStudentName').value = '';
            document.getElementById('newStudentEmail').value = '';
            loadStudents();
        } else {
            toast('Unexpected response creating student', 'error');
        }
    } catch (err) {
        toast('Create student failed: ' + err.message, 'error');
        console.error(err);
    }
}

/* ---------------------------
   Enroll / Drop / Enrollments
   --------------------------- */
async function enrollAction() {
    const sid = document.getElementById('studentId').value.trim();
    const sec = document.getElementById('sectionId').value.trim();
    const admin = document.getElementById('adminOverride').checked;
    if (!sid || !sec) {
        toast('Student ID and Section ID are required', 'error');
        return;
    }

    try {
        const res = await api('/api/enroll', 'POST', { studentId: sid, sectionId: sec, admin: admin });
        document.getElementById('enrollResult').innerText = res.result || JSON.stringify(res);
        toast('Enroll: ' + (res.result || 'done'), 'success');
        await Promise.all([loadEnrollments(), renderCatalog()]);
    } catch (err) {
        document.getElementById('enrollResult').innerText = 'Error: ' + err.message;
        toast('Enroll failed: ' + err.message, 'error');
    }
}

async function dropAction() {
    const sid = document.getElementById('studentId').value.trim();
    const sec = document.getElementById('sectionId').value.trim();
    if (!sid || !sec) {
        toast('Student ID and Section ID are required', 'error');
        return;
    }

    try {
        const res = await api('/api/drop', 'POST', { studentId: sid, sectionId: sec });
        document.getElementById('enrollResult').innerText = res.result || JSON.stringify(res);
        toast('Drop: ' + (res.result || 'done'), 'success');
        await Promise.all([loadEnrollments(), renderCatalog()]);
    } catch (err) {
        document.getElementById('enrollResult').innerText = 'Error: ' + err.message;
        toast('Drop failed: ' + err.message, 'error');
    }
}

async function loadEnrollments() {
    const tbody = document.getElementById('enrollments');
    tbody.innerHTML = '<tr><td colspan="4" class="px-3 py-4 text-slate-400">Loading…</td></tr>';
    try {
        const data = await api('/api/enrollments');
        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="4" class="px-3 py-4 text-slate-400">No enrollments</td></tr>';
            return;
        }
        tbody.innerHTML = '';
        data.forEach(e => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td class="px-3 py-2">${escape(e.enrollmentId)}</td>
                      <td class="px-3 py-2">${escape(e.studentId)}</td>
                      <td class="px-3 py-2">${escape(e.sectionId)}</td>
                      <td class="px-3 py-2">${escape(e.status)}</td>`;
            tbody.appendChild(tr);
        });
    } catch (err) {
        tbody.innerHTML = `<tr><td colspan="4" class="px-3 py-4 text-red-600">Error: ${escape(err.message)}</td></tr>`;
    }
}

/* ---------------------------
   Utilities & wiring
   --------------------------- */
function escape(s) {
    if (s === null || s === undefined) return '';
    return String(s).replaceAll('&', '&amp;').replaceAll('<', '&lt;').replaceAll('>', '&gt;');
}

// Wire up buttons
document.getElementById('btnLoad').addEventListener('click', () => {
    renderCatalog();
    loadStudents();
    loadEnrollments();
});

document.getElementById('btnCatalogRefresh').addEventListener('click', renderCatalog);
document.getElementById('btnInit').addEventListener('click', async () => {
    try {
        await api('/api/init', 'POST'); toast('Sample data initialized', 'success');
        renderCatalog(); loadStudents(); loadEnrollments();
    } catch (err) { toast('Init failed: ' + err.message, 'error'); }
});
document.getElementById('btnLoadStudents').addEventListener('click', loadStudents);
document.getElementById('btnAddStudent').addEventListener('click', addStudent);
document.getElementById('btnEnroll').addEventListener('click', enrollAction);
document.getElementById('btnDrop').addEventListener('click', dropAction);
document.getElementById('btnRefreshEnroll').addEventListener('click', loadEnrollments);

// initial load
renderCatalog();
loadStudents();
loadEnrollments();
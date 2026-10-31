/* UI context is never authorization: the API still checks the signed-in doctor and encounter. */
(function (win) {
    'use strict';
    var params = new URLSearchParams(win.location.search);
    if (!params.has('patientId') && !params.has('appointmentId')) return;
    var patient = params.get('patientId'), appointment = params.get('appointmentId');
    var valid = /^[1-9][0-9]*$/.test(patient || '') && /^[1-9][0-9]*$/.test(appointment || '');
    var app = win.document.getElementById('finance-app');
    if (!app || app.getAttribute('data-role') !== 'doctor') return;
    var note = win.document.createElement('blockquote');
    note.className = 'layui-elem-quote';
    note.setAttribute('role', 'status');
    app.parentNode.insertBefore(note, app);
    function encounter() {
        return valid && (win.financeEncounters || []).find(function (row) {
            return String(row.patientId) === patient && String(row.appointmentId) === appointment;
        });
    }
    function refresh() {
        var selected = encounter(), form = win.document.getElementById('order');
        note.textContent = !valid ? '缺少有效的本次预约信息，不能在此创建药品订单。'
            : selected ? '已绑定本次预约 #' + appointment + ' 的接诊记录；订单仅用于当前患者。'
            : '尚未找到当前医生本人完成的本次接诊记录。请先完成下方对应预约的接诊登记，再创建订单。';
        if (form) {
            var select = form.querySelector('[name="encounterId"]'), button = form.querySelector('[type="submit"]');
            if (select && selected && select.getAttribute('data-context-encounter') !== String(selected.id)) {
                select.replaceChildren(new Option('本次接诊 #' + selected.id, String(selected.id)));
                select.setAttribute('data-context-encounter', String(selected.id));
            }
            if (button && !selected) { button.disabled = true; button.setAttribute('data-context-blocked', 'true'); }
            else if (button && button.hasAttribute('data-context-blocked')) {
                button.disabled = false; button.removeAttribute('data-context-blocked');
            }
        }
        Array.prototype.forEach.call(app.querySelectorAll('.layui-card'), function (card) {
            var table = card.querySelector('table'), header = table && table.querySelector('thead');
            var completion = header && header.textContent.indexOf('预约') >= 0;
            card.hidden = !card.contains(form) && !completion;
            if (completion) Array.prototype.forEach.call(table.querySelectorAll('tbody tr'), function (row) {
                row.hidden = !valid || !row.cells[0] || row.cells[0].textContent.trim() !== appointment;
            });
        });
    }
    app.addEventListener('submit', function (event) {
        if (event.target.id !== 'order') return;
        var selected = encounter(), select = event.target.querySelector('[name="encounterId"]');
        if (!selected || !select || select.value !== String(selected.id)) {
            event.preventDefault(); event.stopImmediatePropagation();
            note.textContent = '当前接诊信息不匹配，未提交订单。请刷新并核对本次预约。';
        }
    }, true);
    new win.MutationObserver(refresh).observe(app, {childList: true, subtree: true});
    refresh();
}(window));

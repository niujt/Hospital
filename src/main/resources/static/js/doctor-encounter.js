/* Same-patient navigation only; all records and mutations remain server-authorized. */
(function (win) {
    'use strict';
    var patient = win.document.querySelector('input[name="patientid"]');
    var appointment = new URLSearchParams(win.location.search).get('appointmentId') || '';
    if (!patient || !/^[1-9][0-9]*$/.test(patient.value)) return;
    var frames = [win.document.getElementById('encounter-history'), win.document.getElementById('encounter-orders')];
    frames.forEach(function (frame, index) {
        if (!frame) return;
        var loaded = false, pane = frame.closest('.layui-tab-item');
        frame.style.display = 'block';
        function fit() { try { win.HospitalFrame.fit(frame); } catch (error) {} }
        frame.addEventListener('load', function () {
            if (!loaded) return;
            try {
                var child = win.HospitalFrame.prepare(frame);
                if (win.ResizeObserver) new win.ResizeObserver(fit).observe(child.document.body);
                new win.MutationObserver(fit).observe(child.document.body, {childList: true, subtree: true});
                fit();
            } catch (error) { /* Server login/authorization behavior is preserved. */ }
        });
        function activate() {
            if (!pane.classList.contains('layui-show')) return;
            if (!loaded) {
                loaded = true;
                frame.src = index === 0 ? '/doctor/medicalhistory/' + encodeURIComponent(patient.value)
                    : '/finance/doctor?patientId=' + encodeURIComponent(patient.value) + '&appointmentId=' + encodeURIComponent(appointment);
            } else fit();
        }
        new win.MutationObserver(activate).observe(pane, {attributes: true, attributeFilter: ['class']});
        activate();
    });
}(window));

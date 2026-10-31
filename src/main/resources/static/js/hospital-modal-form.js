/* Same-origin iframe bridge. Existing onsubmit handlers and request payloads stay unchanged. */
(function (root) {
    'use strict';
    var attached = false;
    function activeForm() {
        var forms = Array.prototype.slice.call(root.document.querySelectorAll('form[data-modal-form]'));
        return forms.find(function (form) {
            var tab = form.closest('.layui-tab-item');
            return !tab || tab.classList.contains('layui-show');
        }) || null;
    }
    root.HospitalModalForm = {
        attach: function (host) {
            if (attached) return null;
            attached = true;
            var drafts = root.HospitalDrafts ? root.HospitalDrafts.track(root) : null;
            var busy = false, timer, original = root.HospitalFeedback.request;
            var rows = Array.prototype.slice.call(root.document.querySelectorAll('[data-modal-actions]'));
            var hidden = rows.map(function (row) { var old = row.hidden; row.hidden = true; return old; });
            function update() {
                var form = activeForm();
                host.update({
                    ready: !!form,
                    pending: busy,
                    label: form ? (form.getAttribute('data-modal-submit-label') || '保存') : '保存'
                });
            }
            function release() { busy = false; update(); }
            function request(options) {
                var method = String(options.type || options.method || 'GET').toUpperCase();
                if (method === 'GET') return original.call(root.HospitalFeedback, options);
                if (busy) return false;
                busy = true; update();
                var complete = options.complete, success = options.success, succeeded = false;
                var wrapped = Object.assign({}, options, {
                    success: function (data) {
                        var message = data && data.message;
                        succeeded = typeof message === 'string' && /成功|已生成/.test(message)
                            && !/失败|无法|错误|但是|不足/.test(message);
                        if (success) return success.apply(this, arguments);
                    },
                    complete: function () {
                        try { if (complete) complete.apply(this, arguments); }
                        finally {
                            // Keep the footer locked during the existing successful feedback/reload delay.
                            if (succeeded) timer = root.setTimeout(release, 2300);
                            else release();
                        }
                    }
                });
                try {
                    var result = original.call(root.HospitalFeedback, wrapped);
                    if (result === false) release();
                    return result;
                } catch (error) { release(); throw error; }
            }
            root.HospitalFeedback.request = request;
            var observer = new root.MutationObserver(update);
            Array.prototype.forEach.call(root.document.querySelectorAll('.layui-tab-item'), function (tab) {
                observer.observe(tab, {attributes: true, attributeFilter: ['class']});
            });
            update();
            return {
                hasChanges: function () { return drafts ? drafts.dirty() : false; },
                isPending: function () { return busy || (drafts ? drafts.pending() : false); },
                submit: function () {
                    var form = activeForm();
                    if (busy || !form) return false;
                    if (!form.reportValidity()) return false;
                    var verified = Array.prototype.slice.call(form.querySelectorAll('[lay-verify]')).filter(function (field) {
                        return !field.disabled && !field.readOnly && field.type !== 'hidden';
                    });
                    if (root.layui.form && typeof root.layui.form.validate === 'function'
                            && !root.layui.form.validate(verified)) return false;
                    // requestSubmit dispatches the original inline submit handler; never bypass validation with form.submit().
                    if (typeof form.requestSubmit === 'function') form.requestSubmit();
                    else form.dispatchEvent(new root.Event('submit', {bubbles: true, cancelable: true}));
                    return true;
                },
                dispose: function () {
                    observer.disconnect();
                    if (timer) root.clearTimeout(timer);
                    if (root.HospitalFeedback.request === request) root.HospitalFeedback.request = original;
                    rows.forEach(function (row, i) { row.hidden = hidden[i]; });
                    attached = false;
                }
            };
        }
    };
}(window));

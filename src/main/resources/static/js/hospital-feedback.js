/* Shared feedback only: business endpoints and payloads remain unchanged. */
(function (root) {
    'use strict';
    function escapeText(value) {
        return String(value == null ? '操作未完成，请稍后重试。' : value)
            .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;').replace(/'/g, '&#39;').replace(/\n/g, '<br>');
    }
    function succeeded(message) {
        return typeof message === 'string' && /成功|已生成/.test(message)
            && !/失败|无法|错误|但是|不足/.test(message);
    }
    function display(message, success, afterSuccess) {
        var patient = root.document.body.classList.contains('patient-portal')
            || root.document.body.classList.contains('hospital-public');
        root.layui.use('layer', function () {
            var content = escapeText(message);
            if (patient) {
                var path = success ? '<path d="M8 12.5l3 3 5.5-6"/>' : '<path d="M9 9l6 6m0-6l-6 6"/>';
                content = '<span class="patient-feedback-mark ' + (success ? 'is-success' : 'is-error') + '" aria-hidden="true">'
                    + '<svg viewBox="0 0 24 24" focusable="false"><circle cx="12" cy="12" r="9"/>' + path + '</svg></span>'
                    + '<span class="patient-feedback-text">' + (success ? content : '操作未完成') + '</span>'
                    + (success ? '' : '<details class="patient-feedback-details"><summary>查看原因</summary><p>' + content + '</p></details>');
            }
            root.layui.layer.msg(content, {
                time: success ? 2200 : (patient ? 0 : 4000),
                closeBtn: patient && !success ? 1 : 0,
                icon: patient ? -1 : (success ? 1 : 0),
                skin: patient ? 'patient-dialog patient-feedback' : '',
                success: function (layero) {
                    layero.attr('role', success ? 'status' : 'alert');
                    layero.attr('aria-live', success ? 'polite' : 'assertive');
                }
            }, function () {
                // Failed or partial requests leave their form intact for correction.
                if (success && typeof afterSuccess === 'function') afterSuccess();
            });
        });
    }
    var pending = Object.create(null);
    function request(options) {
        var ajax = root.jQuery || root.$;
        var method = String(options.type || options.method || 'GET').toUpperCase();
        if (method === 'GET') return ajax.ajax(options);
        var key = method + ':' + options.url;
        if (pending[key]) return false;
        pending[key] = true;
        var controls = Array.prototype.slice.call(root.document.querySelectorAll('button, input[type="submit"]'))
            .map(function (node) { var old = node.disabled; node.disabled = true; return {node: node, old: old}; });
        var complete = options.complete, success = options.success, wasSuccess = false;
        function release() {
            delete pending[key];
            controls.forEach(function (item) { item.node.disabled = item.old; });
        }
        var wrapped = Object.assign({}, options, {
            success: function (data) {
                wasSuccess = succeeded(data && data.message);
                if (success) return success.apply(this, arguments);
            },
            complete: function () {
                try { if (complete) complete.apply(this, arguments); }
                finally { if (wasSuccess) root.setTimeout(release, 2300); else release(); }
            }
        });
        try { return ajax.ajax(wrapped); } catch (error) { release(); throw error; }
    }
    root.HospitalFeedback = {
        request: request,
        confirm: function (message, onConfirm) {
            var trigger = root.document.activeElement, handler, layerIndex;
            var patient = root.document.body.classList.contains('patient-portal');
            layerIndex = root.layui.layer.confirm(escapeText(message), {
                btn: ['确定', '取消'], skin: patient ? 'patient-dialog' : '',
                success: function (layero, index) {
                    var dialog = layero[0];
                    dialog.setAttribute('role', 'alertdialog');
                    dialog.setAttribute('aria-modal', 'true');
                    dialog.setAttribute('aria-label', String(message));
                    Array.prototype.forEach.call(dialog.querySelectorAll('a'), function (node) { node.setAttribute('tabindex', '0'); node.setAttribute('role', 'button'); });
                    var cancel = dialog.querySelector('.layui-layer-btn1');
                    if (cancel) { cancel.setAttribute('tabindex', '0'); cancel.focus(); }
                    handler = function (event) {
                        if ((event.key === 'Enter' || event.key === ' ') && event.target.tagName === 'A' && dialog.contains(event.target)) { event.preventDefault(); event.target.click(); return; }
                        if (event.key === 'Escape') { event.preventDefault(); root.layui.layer.close(index); }
                        if (event.key === 'Tab') {
                            var nodes = Array.prototype.slice.call(dialog.querySelectorAll('a,button,input')).filter(function(n){return !n.disabled;});
                            if (!nodes.length) return;
                            var first = nodes[0], last = nodes[nodes.length - 1];
                            if (event.shiftKey && root.document.activeElement === first) { event.preventDefault(); last.focus(); }
                            else if (!event.shiftKey && root.document.activeElement === last) { event.preventDefault(); first.focus(); }
                        }
                    };
                    root.document.addEventListener('keydown', handler);
                },
                end: function () {
                    if (handler) root.document.removeEventListener('keydown', handler);
                    if (trigger && typeof trigger.focus === 'function') trigger.focus();
                }
            }, function (index) {
                root.layui.layer.close(index);
                if (typeof onConfirm === 'function') onConfirm();
            });
            return layerIndex;
        },
        result: function (message, afterSuccess) {
            display(message, succeeded(message), afterSuccess);
        },
        error: function (xhr) {
            var message = xhr && xhr.responseJSON && xhr.responseJSON.message;
            display(typeof message === 'string' && message ? message
                : '请求未完成，请检查网络或稍后重试。', false);
        }
    };
}(window));

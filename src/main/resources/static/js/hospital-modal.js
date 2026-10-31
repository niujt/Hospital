/* Native Layui iframe modal with one persistent footer; no staff theme overrides. */
(function (root) {
    'use strict';
    function focusable(doc, scope) {
        return Array.prototype.slice.call(scope.querySelectorAll(
            'a[href],button,input:not([type="hidden"]),select,textarea,[tabindex]'
        )).filter(function (node) {
            return node.tabIndex >= 0 && !node.disabled && node.getAttribute('aria-disabled') !== 'true'
                && node.getClientRects().length > 0;
        });
    }
    root.HospitalModal = {
        open: function (options) {
            var editable = options.editable !== false;
            var trigger = root.document.activeElement, dialog, frame, child, bridge;
            var pending = false, ready = false, index, parentKey, childKey, connect;
            var discard = false, confirmLayer;
            var save, cancel, originalLabel = '保存';
            function update(state) {
                ready = state.ready; pending = state.pending;
                originalLabel = state.label || '保存';
                if (save) {
                    save.hidden = !ready && !pending;
                    if (save.style) save.style.display = save.hidden ? 'none' : '';
                    save.textContent = pending ? '保存中…' : originalLabel;
                    save.setAttribute('aria-disabled', String(pending || !ready));
                }
                if (cancel) cancel.setAttribute('aria-disabled', String(pending));
                if (dialog) dialog.setAttribute('aria-busy', String(pending));
            }
            function close() {
                if (pending || (bridge && bridge.isPending && bridge.isPending())) return false;
                if (!discard && bridge && bridge.hasChanges && bridge.hasChanges()) {
                    if (confirmLayer && root.document.getElementById('layui-layer' + confirmLayer)) return false;
                    confirmLayer = root.HospitalFeedback.confirm('表单有未保存的内容，确认放弃并关闭吗？', function () {
                        discard = true; root.layui.layer.close(index);
                    });
                    return false;
                }
                root.layui.layer.close(index);
                return false;
            }
            function childNodes() {
                try { return child ? focusable(child.document, child.document) : []; }
                catch (error) { return []; }
            }
            function footerNodes() {
                return dialog ? focusable(root.document, dialog.querySelector('.layui-layer-btn')) : [];
            }
            function key(event, fromChild) {
                if (event.key === 'Escape') { event.preventDefault(); close(); return; }
                if (!fromChild && (event.key === 'Enter' || event.key === ' ')
                        && event.target.tagName === 'A' && dialog.contains(event.target)) {
                    event.preventDefault();
                    if (event.target.getAttribute('aria-disabled') !== 'true') event.target.click();
                    return;
                }
                if (event.key !== 'Tab') return;
                var fields = childNodes(), buttons = footerNodes();
                if (!buttons.length && !fields.length) { event.preventDefault(); return; }
                var first = fields[0], last = fields[fields.length - 1];
                if (fromChild) {
                    if (event.shiftKey && event.target === first) {
                        event.preventDefault(); (buttons[buttons.length - 1] || last).focus();
                    } else if (!event.shiftKey && event.target === last) {
                        event.preventDefault(); (buttons[0] || first).focus();
                    }
                } else if (event.shiftKey && event.target === buttons[0]) {
                    event.preventDefault(); (last || buttons[buttons.length - 1]).focus();
                } else if (!event.shiftKey && event.target === buttons[buttons.length - 1]) {
                    event.preventDefault(); (first || buttons[0]).focus();
                }
            }
            function disconnectChild() {
                if (child && childKey) {
                    try { child.document.removeEventListener('keydown', childKey); } catch (error) { /* navigating iframe */ }
                }
                if (bridge) bridge.dispose();
                bridge = null; child = null;
            }
            index = root.layui.layer.open({
                type: 2, title: options.title || '详情', closeBtn: 0, shadeClose: false,
                area: [Math.min(1100, Math.max(280, root.innerWidth - 32)) + 'px',
                    Math.max(240, Math.floor(root.innerHeight * 0.8)) + 'px'],
                content: [options.url, 'auto'],
                btn: editable ? ['保存', '取消'] : ['关闭'],
                yes: function () {
                    if (!editable) return close();
                    if (pending || !ready || !bridge) return false;
                    try { bridge.submit(); }
                    catch (error) { root.HospitalFeedback.error(); }
                    return false;
                },
                btn2: close,
                cancel: close,
                success: function (layero, layerIndex) {
                    index = layerIndex;
                    dialog = layero[0];
                    dialog.setAttribute('role', 'dialog');
                    dialog.setAttribute('aria-modal', 'true');
                    dialog.setAttribute('aria-label', options.title || '详情');
                    // The iframe document is the only scroll area; the native footer remains outside it.
                    dialog.querySelector('.layui-layer-content').style.overflow = 'hidden';
                    frame = dialog.querySelector('iframe');
                    frame.setAttribute('title', options.title || '详情表单');
                    var anchors = dialog.querySelectorAll('.layui-layer-btn a');
                    Array.prototype.forEach.call(anchors, function (node) {
                        node.setAttribute('role', 'button'); node.setAttribute('tabindex', '0');
                    });
                    save = editable ? anchors[0] : null;
                    cancel = anchors[editable ? 1 : 0];
                    update({ready: false, pending: false, label: '保存'});
                    parentKey = function (event) { key(event, false); };
                    root.document.addEventListener('keydown', parentKey);
                    connect = function () {
                        disconnectChild();
                        try {
                            child = frame.contentWindow;
                            childKey = function (event) { key(event, true); };
                            child.document.addEventListener('keydown', childKey);
                            if (editable && child.HospitalModalForm) bridge = child.HospitalModalForm.attach({update: update});
                        } catch (error) { update({ready: false, pending: false, label: '保存'}); }
                    };
                    frame.addEventListener('load', connect);
                    connect();
                    if (cancel) cancel.focus();
                },
                end: function () {
                    if (parentKey) root.document.removeEventListener('keydown', parentKey);
                    if (frame && connect) frame.removeEventListener('load', connect);
                    disconnectChild();
                    if (trigger && typeof trigger.focus === 'function') trigger.focus();
                }
            });
            return index;
        }
    };
}(window));

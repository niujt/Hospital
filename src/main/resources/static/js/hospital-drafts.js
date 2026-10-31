/* Draft snapshots stay in memory only. No persistent storage, logging, or transmission of form values. */
(function (root) {
    'use strict';
    var documents = new WeakMap();
    function editable(form) {
        return form.hasAttribute('data-modal-form') || typeof form.onsubmit === 'function'
            || String(form.method || '').toLowerCase() === 'post';
    }
    function snapshot(form) {
        return JSON.stringify(Array.prototype.slice.call(form.querySelectorAll('input,select,textarea')).filter(function (field) {
            return !field.readOnly && !['hidden', 'submit', 'button', 'reset'].includes(field.type);
        }).map(function (field, index) {
            return [field.name || field.id || index, /^(checkbox|radio)$/.test(field.type) ? field.checked : field.value];
        }));
    }
    function track(win) {
        var doc = win.document;
        if (documents.has(doc)) return documents.get(doc);
        var baselines = new WeakMap(), states = new WeakMap(), frames = new WeakSet();
        function forms() { return Array.prototype.slice.call(doc.querySelectorAll('form')).filter(editable); }
        function scan() {
            forms().forEach(function (form) {
                if (!baselines.has(form)) {
                    baselines.set(form, snapshot(form));
                    states.set(form, Array.prototype.slice.call(form.querySelectorAll('button[type="submit"],input[type="submit"]'))
                        .map(function (button) { return {button: button, disabled: button.disabled}; }));
                }
            });
            Array.prototype.forEach.call(doc.querySelectorAll('iframe'), function (frame) {
                if (!frames.has(frame)) {
                    frames.add(frame);
                    frame.addEventListener('load', function () { try { track(frame.contentWindow); } catch (error) {} });
                }
                try { track(frame.contentWindow); } catch (error) { /* Never inspect cross-origin documents. */ }
            });
        }
        function descendants(method) {
            return Array.prototype.some.call(doc.querySelectorAll('iframe'), function (frame) {
                try { return track(frame.contentWindow)[method](); } catch (error) { return false; }
            });
        }
        var api = {
            dirty: function () {
                scan();
                return forms().some(function (form) { return baselines.get(form) !== snapshot(form); }) || descendants('dirty');
            },
            pending: function () {
                scan();
                return !!doc.querySelector('[role="dialog"][aria-busy="true"]') || forms().some(function (form) {
                    return (states.get(form) || []).some(function (state) { return !state.disabled && state.button.disabled; });
                }) || descendants('pending');
            }
        };
        documents.set(doc, api);
        var observer = new win.MutationObserver(scan);
        observer.observe(doc.documentElement, {childList: true, subtree: true});
        doc.addEventListener('reset', function (event) {
            var form = event.target;
            win.setTimeout(function () { if (editable(form)) baselines.set(form, snapshot(form)); }, 0);
        });
        scan();
        return api;
    }
    root.HospitalDrafts = {track: track, snapshot: snapshot, editable: editable};
}(window));

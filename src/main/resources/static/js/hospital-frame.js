/* Same-origin workspace structure only. Native Layui theme and page scripts stay intact. */
(function (root) {
    'use strict';
    root.HospitalFrame = {
        prepare: function (frame) {
            var win = frame.contentWindow, doc = win.document;
            win.HospitalWorkspaceEmbedded = true;
            Array.prototype.forEach.call(doc.querySelectorAll('.layui-header,.layui-side'), function (node) {
                node.hidden = true;
                node.style.display = 'none';
            });
            doc.body.classList.remove('layui-layout-body');
            Array.prototype.forEach.call(doc.querySelectorAll('.layui-layout-admin'), function (node) {
                node.classList.remove('layui-layout-admin');
            });
            Array.prototype.forEach.call(doc.querySelectorAll('.layui-body'), function (node) {
                node.classList.remove('layui-body');
                node.classList.add('layui-fluid');
            });
            return win;
        },
        fit: function (frame) {
            try {
                var doc = frame.contentDocument;
                frame.height = Math.max(300, doc.body.scrollHeight, doc.documentElement.scrollHeight) + 'px';
            } catch (error) { /* Same-origin only. */ }
        }
    };
}(window));

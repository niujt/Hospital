/* Native Layui 2.13 tabs. Routes remain server-authorized; this is not an authorization layer. */
(function (win) {
    'use strict';
    var routes = {
        '/admin/doctorManage': '医生管理', '/admin/patientManage': '患者管理', '/admin/adminManage': '管理员管理',
        '/admin/appointmentManage': '预约管理', '/admin/medicalhistoryManage': '病史管理',
        '/admin/hospitalizationManage': '住院信息', '/admin/optionManage': '检查科目',
        '/admin/illnessManage': '疾病管理', '/admin/drugsManage': '药品管理', '/finance/admin': '财务与经营'
    };
    var container = win.document.getElementById('admin-workspace');
    if (!container) return;
    win.layui.use(['tabs', 'element', 'layer'], function () {
        var tabs = win.layui.tabs, entries = new Map(), sequence = 0, active = 'overview', allowLeave = false;
        tabs.render({elem: '#admin-workspace', closable: true, headerMode: 'scroll'});
        function current(id) { return entries.get(id || active); }
        function some(method) {
            return Array.from(entries.values()).some(function (entry) { return entry.drafts && entry.drafts[method](); });
        }
        function permission(entry, action) {
            if (entry && entry.drafts && entry.drafts.pending()) {
                win.HospitalFeedback.result('请求正在处理，请等待完成后再关闭或刷新。'); return;
            }
            if (entry && entry.drafts && entry.drafts.dirty()) {
                win.HospitalFeedback.confirm('此标签有未保存的内容，确认放弃并继续吗？', action);
            } else action();
        }
        function open(url) {
            if (!Object.prototype.hasOwnProperty.call(routes, url)) return;
            var existing = Array.from(entries.values()).find(function (entry) { return entry.url === url; });
            if (existing) { tabs.change('admin-workspace', existing.id); return; }
            var id = 'module-' + (++sequence), entry = {id: id, url: url};
            entries.set(id, entry);
            tabs.add('admin-workspace', {
                id: id, title: routes[url], closable: true, active: true,
                content: '<p class="layui-text">正在载入模块…</p>',
                done: function (data) {
                    var pane = data.bodyItem[0], frame = win.document.createElement('iframe');
                    var loading = pane.firstElementChild;
                    frame.title = routes[url];
                    frame.width = '100%';
                    frame.height = Math.max(180, win.innerHeight - container.getBoundingClientRect().top - 76) + 'px';
                    frame.style.border = '0';
                    frame.style.display = 'none';
                    frame.hidden = true;
                    entry.frame = frame;
                    frame.addEventListener('load', function () {
                        try {
                            // Ignore the iframe's initial empty-document event; it is not an authentication failure.
                            if (frame.contentWindow.location.href === 'about:blank') return;
                            if (frame.contentWindow.location.pathname !== entry.url) {
                                loading.textContent = '会话已失效或页面跳转，请返回登录后重新打开。';
                                return;
                            }
                            var child = win.HospitalFrame.prepare(frame);
                            entry.drafts = win.HospitalDrafts.track(child);
                            loading.hidden = true;
                            frame.hidden = false; frame.style.display = 'block';
                            child.dispatchEvent(new child.Event('resize'));
                        } catch (error) { loading.textContent = '无法载入模块，请检查登录状态后重试。'; }
                    });
                    // Set the destination before insertion so no initial about:blank navigation is requested.
                    frame.src = url;
                    pane.appendChild(frame);
                }
            });
        }
        tabs.on('beforeClose(admin-workspace)', function (data) {
            var id = this.getAttribute('lay-id'), entry = current(id);
            if (!entry) return;
            permission(entry, function () { tabs.close('admin-workspace', id, true); entries.delete(id); });
            return false;
        });
        tabs.on('afterChange(admin-workspace)', function () {
            active = this.getAttribute('lay-id');
            var entry = current();
            if (entry && entry.frame && entry.frame.contentWindow) {
                entry.frame.contentWindow.dispatchEvent(new entry.frame.contentWindow.Event('resize'));
            }
        });
        win.document.addEventListener('click', function (event) {
            var link = event.target.closest('a[href]');
            if (!link || event.ctrlKey || event.metaKey || event.shiftKey || event.altKey || event.button > 0) return;
            var url = new URL(link.href, win.location.href);
            if (url.origin !== win.location.origin) return;
            if (routes[url.pathname]) { event.preventDefault(); open(url.pathname); }
            else if (url.pathname === '/hospital/admin/index') {
                event.preventDefault(); tabs.change('admin-workspace', 'overview');
            } else if (url.pathname === '/loginout' && (some('dirty') || some('pending'))) {
                event.preventDefault();
                if (some('pending')) { win.HospitalFeedback.result('请求正在处理，请稍候再退出。'); return; }
                win.HospitalFeedback.confirm('有未保存的内容，确认放弃并退出登录吗？', function () {
                    allowLeave = true; win.location.href = url.href;
                });
            }
        });
        win.document.getElementById('workspace-refresh').addEventListener('click', function () {
            var entry = current();
            if (entry) permission(entry, function () { entry.frame.contentWindow.location.reload(); });
        });
        win.addEventListener('resize', function () {
            entries.forEach(function (entry) { if (entry.frame) entry.frame.height = Math.max(180, win.innerHeight - container.getBoundingClientRect().top - 76) + 'px'; });
        });
        win.addEventListener('beforeunload', function (event) {
            if (!allowLeave && (some('dirty') || some('pending'))) { event.preventDefault(); event.returnValue = ''; }
        });
    });
}(window));

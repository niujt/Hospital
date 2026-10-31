/* Read-only authenticated receipt renderer. No payment or order mutation is performed here. */
(function (root, factory) {
    'use strict';
    var api = factory();
    if (typeof module === 'object' && module.exports) module.exports = api;
    else if (root.document.readyState === 'loading') {
        root.document.addEventListener('DOMContentLoaded', function () { api.start(root); });
    } else api.start(root);
}(typeof window === 'object' ? window : this, function () {
    'use strict';
    var NOTICE = '模拟票据，非报销凭证';
    function saleId(search) {
        var params = new URLSearchParams(search);
        var id = params.get('saleId');
        if (params.getAll('saleId').length !== 1 || !id || !/^[A-Za-z0-9_-]{1,80}$/.test(id)) {
            throw new Error('订单编号无效，请从已完成的购买记录重新打开凭证。');
        }
        return id;
    }
    function text(value, field) {
        if (typeof value !== 'string' || !value.trim()) throw new Error('票据数据缺少' + field + '，请稍后重试。');
        return value.trim();
    }
    function money(value) {
        if ((typeof value !== 'number' && typeof value !== 'string') || String(value).trim() === ''
                || !/^\d+(?:\.\d+)?$/.test(String(value))) throw new Error('票据金额格式不正确，暂时不能打印。');
        var parts = String(value).split('.');
        var fraction = parts[1] || '';
        if (fraction.length > 2 && /[1-9]/.test(fraction.slice(2))) {
            throw new Error('票据金额精度不正确，暂时不能打印。');
        }
        return parts[0].replace(/^0+(?=\d)/, '') + '.' + (fraction + '00').slice(0, 2);
    }
    function maskedName(value) {
        var name = text(value, '就诊人');
        var characters = Array.from(name);
        return characters.length <= 1 ? '*' : characters[0] + '*'.repeat(Math.min(characters.length - 1, 8));
    }
    function issuedAt(value) {
        var raw = text(value, '开具时间');
        var parts = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2})(?:\.\d{1,9})?(Z|[+-]\d{2}:\d{2})$/.exec(raw);
        if (!parts) throw new Error('票据开具时间格式不正确，暂时不能打印。');
        var year = Number(parts[1]), month = Number(parts[2]), day = Number(parts[3]);
        var leap = year % 4 === 0 && (year % 100 !== 0 || year % 400 === 0);
        var days = [31, leap ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
        var instant = new Date(raw);
        if (month < 1 || month > 12 || day < 1 || day > days[month - 1]
                || Number(parts[4]) > 23 || Number(parts[5]) > 59 || Number(parts[6]) > 59
                || !Number.isFinite(instant.getTime())) {
            throw new Error('票据开具时间格式不正确，暂时不能打印。');
        }
        var formatted = new Intl.DateTimeFormat('zh-CN', {
            timeZone: 'Asia/Shanghai', year: 'numeric', month: '2-digit', day: '2-digit',
            hour: '2-digit', minute: '2-digit', second: '2-digit', hourCycle: 'h23'
        }).formatToParts(instant);
        var values = {};
        formatted.forEach(function (part) { values[part.type] = part.value; });
        return values.year + '-' + values.month + '-' + values.day + ' '
            + values.hour + ':' + values.minute + ':' + values.second + ' 北京时间';
    }
    function normalize(data, requestedId) {
        if (!data || data.receiptType !== 'DEMO_PURCHASE_RECEIPT') throw new Error('当前订单没有可打印的模拟购买凭证。');
        if (String(data.saleId) !== requestedId) throw new Error('订单编号与票据不一致，暂时不能打印。');
        if (!Array.isArray(data.items) || data.items.length === 0) throw new Error('票据没有购买明细，暂时不能打印。');
        var statuses = {NONE: '未退款', PARTIAL: '部分退款', FULL: '已全部退款'};
        if (!Object.prototype.hasOwnProperty.call(statuses, data.refundStatus)) throw new Error('票据退款状态不完整，暂时不能打印。');
        var methods = {BALANCE: '账户余额', WALLET: '账户余额', CASH: '现金登记', CARD: '演示卡（非真实银行卡）', DEMO_CARD: '演示卡（非真实银行卡）'};
        var method = text(data.paymentMethod, '支付方式');
        var tax=data.tax;
        if (!tax || tax.inclusive !== true || typeof tax.configured !== 'boolean' || !/^\d+(?:\.\d{1,4})?$/.test(String(tax.rate)) || Number(tax.rate)>100) throw new Error('票据税率信息不完整，暂时不能打印。');
        return {
            saleId: requestedId,
            taxRate: tax.configured ? String(tax.rate)+'%' : '未配置（演示0%）',
            taxNet: money(tax.netAmount), taxAmount: money(tax.taxAmount), taxRefunded: money(tax.refundedTaxAmount),
            issuedAt: issuedAt(data.issuedAt),
            nameNote: data.nameSource === 'CURRENT_RECORD' ? '名称按当前档案显示，成交金额以原订单记录为准。' : '',
            patientName: maskedName(data.patient && data.patient.name),
            paymentMethod: Object.prototype.hasOwnProperty.call(methods, method) ? methods[method] : method,
            items: data.items.map(function (item) {
                if (!item || !['number', 'string'].includes(typeof item.quantity)
                        || !/^\d+$/.test(String(item.quantity))
                        || !Number.isSafeInteger(Number(item.quantity)) || Number(item.quantity) <= 0) {
                    throw new Error('票据明细数量不正确，暂时不能打印。');
                }
                return {name: text(item.name, '药品名称'), quantity: String(item.quantity),
                    unitPrice: money(item.unitPrice), amount: money(item.amount)};
            }),
            total: money(data.totalAmount), refunded: money(data.refundedAmount),
            net: money(data.netAmount), status: statuses[data.refundStatus], notice: NOTICE
        };
    }
    function render(doc, receipt) {
        function set(id, value) { doc.getElementById(id).textContent = value; }
        set('receipt-sale-id', receipt.saleId);
        set('receipt-issued-at', receipt.issuedAt);
        set('receipt-patient', receipt.patientName);
        set('receipt-name-note', receipt.nameNote);
        doc.getElementById('receipt-name-note').hidden = !receipt.nameNote;
        set('receipt-payment-method', receipt.paymentMethod);
        var body = doc.getElementById('receipt-items');
        body.textContent = '';
        receipt.items.forEach(function (item, index) {
            var row = doc.createElement('tr');
            [String(index + 1), item.name, item.quantity, item.unitPrice, item.amount].forEach(function (value) {
                var cell = doc.createElement('td');
                cell.textContent = value;
                row.appendChild(cell);
            });
            body.appendChild(row);
        });
        set('receipt-tax-rate', receipt.taxRate);
        set('receipt-tax-net', receipt.taxNet);
        set('receipt-tax-amount', receipt.taxAmount);
        set('receipt-tax-refunded', receipt.taxRefunded);
        set('receipt-total', receipt.total);
        set('receipt-refunded', receipt.refunded);
        set('receipt-net', receipt.net);
        set('receipt-refund-status', receipt.status);
    }
    async function start(win) {
        var doc = win.document, button = doc.getElementById('print-receipt');
        var state = doc.getElementById('receipt-state'), sheet = doc.getElementById('purchase-receipt');
        var loaded = false;
        var download = doc.getElementById('download-receipt');
        download.hidden = true;
        button.addEventListener('click', function () {
            if (loaded && !button.disabled) win.print();
        });
        var controller = new win.AbortController();
        var timer = win.setTimeout(function () { controller.abort(); }, 15000);
        try {
            var id = saleId(win.location.search);
            var response = await win.fetch('/api/finance/sales/' + encodeURIComponent(id) + '/receipt', {
                method: 'GET', credentials: 'same-origin', cache: 'no-store', redirect: 'error',
                headers: {'Accept': 'application/json'}, signal: controller.signal
            });
            if (response.status === 401 || response.status === 403) throw new Error('请重新登录有权查看此订单的账号后，再打开购买凭证。');
            if (response.status === 404) throw new Error('未找到可访问的购买凭证，请返回购买记录核对。');
            if (!response.ok) throw new Error('暂时无法读取购买凭证，请稍后重试。');
            var data;
            try { data = await response.json(); }
            catch (error) { throw new Error('返回的票据数据无法识别，请重新登录后再试。'); }
            render(doc, normalize(data, id));
            state.hidden = true;
            sheet.hidden = false;
            loaded = true;
            button.disabled = false;
            download.href = '/api/finance/sales/' + encodeURIComponent(id) + '/receipt.pdf';
            download.hidden = false;
        } catch (error) {
            state.setAttribute('role', 'alert');
            state.textContent = error && error.name === 'AbortError'
                ? '读取购买凭证超时，请检查网络后重新打开。'
                : (error && error.message && error.name === 'Error' ? error.message : '无法读取购买凭证，请检查登录状态与网络后重试。');
            sheet.hidden = true;
            button.disabled = true;
        } finally {
            win.clearTimeout(timer);
        }
    }
    return {saleId: saleId, money: money, maskedName: maskedName, issuedAt: issuedAt, normalize: normalize, render: render, start: start};
}));

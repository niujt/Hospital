// Login/register panel navigation only. Authentication remains in the existing page handlers.
document.addEventListener('DOMContentLoaded', function () {
    var loginTab = document.getElementById('signIn');
    var registerTab = document.getElementById('signUp');
    if (!loginTab || !registerTab) return;
    var loginPanel = document.getElementById('login-panel');
    var registerPanel = document.getElementById('register-panel');

    function selectTab(register, focusTab) {
        loginTab.setAttribute('aria-selected', String(!register));
        registerTab.setAttribute('aria-selected', String(register));
        loginTab.tabIndex = register ? -1 : 0;
        registerTab.tabIndex = register ? 0 : -1;
        loginPanel.hidden = register;
        registerPanel.hidden = !register;
        if (focusTab) (register ? registerTab : loginTab).focus();
    }
    loginTab.addEventListener('click', function () { selectTab(false, false); });
    registerTab.addEventListener('click', function () { selectTab(true, false); });
    [loginTab, registerTab].forEach(function (tab) {
        tab.addEventListener('keydown', function (event) {
            if (event.key === 'ArrowLeft' || event.key === 'Home') {
                event.preventDefault();
                selectTab(false, true);
            } else if (event.key === 'ArrowRight' || event.key === 'End') {
                event.preventDefault();
                selectTab(true, true);
            }
        });
    });
    selectTab(false, false);
});

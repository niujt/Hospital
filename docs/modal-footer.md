# Native modal footer refactor

## Scope

The existing admin and doctor workflows use Layui iframe dialogs. Editable dialogs now have one outer, persistent footer: Save and Cancel. The iframe document is the only scrolling content area. No custom staff theme or new business endpoint is introduced.

- Admin add: adminadd, appointmentadd, doctoradd, drugadd, hospitalizationadd, illnessadd, medicalhistoryadd, optionadd, patientadd (9).
- Admin edit: admininfo, appointmentInfo, doctorinfo, drugsinfo, hospitalizationinfo, illnessinfo, medicalhistoryInfo, optioninfo, patientinfo (9).
- Doctor diagnosis: seek.ftl has three independently submitted tabs: diagnosis, drugs, admission. The footer label follows the active tab; it never submits all three forms.
- Doctor medical history: read-only iframe, Close only; removes the legacy overflow-hidden body class so long records can scroll.

## Contracts

hospital-modal.js owns the parent Layui footer and keyboard boundary. hospital-modal-form.js is a same-origin bridge to the existing child form. It invokes native reportValidity, validates editable non-hidden lay-verify controls, then calls requestSubmit so the existing inline onsubmit handler remains the submission entrypoint. It never calls form.submit.

The original request URLs, HTTP methods, serializers and success callbacks remain unchanged. The bridge observes requests through the existing HospitalFeedback.request helper, preserving its duplicate-request guard. During a mutation, Save and Cancel are guarded; failures leave the form intact and release the footer. Successful requests retain the existing feedback delay and parent reload.

Embedded child action rows are hidden only after the bridge attaches. Direct standalone navigation retains its original submit button. Existing duplicate-value errors now use the shared visible feedback instead of a message span inside the hidden action row.

Footer anchors expose role/button/tabindex, Enter/Space activation, Escape cancellation (not while pending), a Tab boundary across the iframe/footer, and restoration of the opening control's focus.

## Checks and limits

Run: node tools/check-modal.cjs

The VM/source tests cover all 18 admin templates, active doctor-tab selection, native/Layui validation, readonly/hidden field exclusion, request payload preservation, duplicate and synchronous-error guards, success-delay/error release, read-only footer, Escape/Tab/Enter behavior and standalone fallback.

All 56 FreeMarker templates were parsed separately using the existing FreeMarker dependency into temporary output; JS syntax and git diff checks passed.

No browser interaction, target-resource synchronization, restart or remote change was performed during the user's manual handoff. Actual long-form/narrow-viewport/save/error/cancel screenshots must wait until the user returns browser control. VM mocks are not a browser/layout acceptance test.

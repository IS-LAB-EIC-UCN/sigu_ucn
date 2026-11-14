@param String flashOk
@param String flashError

@if(flashOk != null && !flashOk.isBlank())
    <div class="tl-alert tl-alert-ok">${flashOk}</div>
@endif
@if(flashError != null && !flashError.isBlank())
    <div class="tl-alert tl-alert-error">${flashError}</div>
@endif

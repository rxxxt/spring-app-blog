<#import "parts/common.ftl" as c>
<#import "parts/login.ftl" as l>

<@c.page>
Login page
<@l.login "/login" />
<a href="/registration">Add new user</a>
<#if Session.SPRING_SECURITY_LAST_EXCEPTION??>
    <div>Invalid username or password</div>
</#if>
</@c.page>
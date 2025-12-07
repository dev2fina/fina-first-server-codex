<#compress>
<div id="header">
${data.header}
</div>
<div id="details">
    <#if data.files?has_content>
        <UL>
            <#list data.files as file>
                <li>${file.fileName} | Status: ${file.status}</li>
                <#if file.xmls?has_content>
                    <ul>
                        <#list file.xmls as xml>
                            <LI>
                                RETURN CODE: ${xml.returnCode} , RESULT: ${xml.message}
                            </LI>
                        </#list>
                    </ul>
                </#if>
            </#list>
        </UL>
    </#if>
    <#if data.errorFiles?has_content>
    ${data.errorMail}
        <#list data.errorFiles as file>
            <p>${file.fileName}</p>
            <#if file.reason??>
                <UL>
                    <LI>
                    ${file.reason}
                    </LI>
                </UL>
            </#if>
        </#list>
    </#if>
</div>
    <#if data.receivedDate??>
    <div id="timeStamp">
        <h5>
        ${data.sent} @ ${data.receivedDate?string('yyyy.MM.dd G HH:mm:ss z')} | ${data.received}
            @ ${data.sentDate?string('yyyy.MM.dd G HH:mm:ss z')}
        </h5>
    </div>
    </#if>
<div id="footer">
${data.footer}
</div>
</#compress>
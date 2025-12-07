<html>
<head>
    <title>${title}</title>
    <style>
        .returnHeader {
            font: 110% sans-serif;
            text-align: left;
            border-bottom: 1px solid #6F7277 !important;
            color: #4B4A4A !important;
            margin: 0 10px 12px !important;
            padding: 10px 0 !important;
        }

        .period returnCode fi {
            margin: 4px;
        }

        .returnTables {
            margin: 10px;
        }

        .returnTableDescription {
            color: #4B4A4A;
            font: 100% sans-serif;
            text-align: left;
            margin-top: 10px;
            margin-bottom: 10px;
        }

        table.returnTable {
            font-family: verdana, arial, sans-serif;
            font-size: 11px;
            color: #333333;
            border: 1px #666666;
            border-collapse: collapse;
        }

        table.returnTable th {
            padding: 8px;
            border: 1px solid #666666;
            background-color: #dedede;
        }

        table.returnTable td {
            padding: 8px;
            border: 1px solid #666666;
            background-color: #ffffff;
        }

        .variableItem {
            background-color: #a3e0f9;
        }
    </style>
</head>
<body>
<div class="returnHeader">
    <div class="fi">FI: [${(return.fiCode)!"NOCODE"} ] ${(return.fiDescription)!"NONAME"}</div>
    <div class="returnCode">Return: ${(return.returnCode)!"NONAME"}</div>
    <div class="period">Period: ${return.fromDate?string('dd/MM/yyyy')} - ${return.toDate?string('dd/MM/yyyy')}</div>
</div>
<div class="returnTables">
<#list return.tables as table>
    <div class="returnTableDescription">${table.description}</div>

    <table class="returnTable">
        <#if table.type=="MCT">
            <#list table.rows as rows>
                <#if rows?index==0>
                <tr>
                    <#list rows.rowItems as item>
                    <th>${(item.description)!""}
                    </#list>
                <#else>
                <tr>
                    <#list rows.rowItems as item>
                    <td>
                        <#if item?index==0>
                        ${(item.description)!""}
                        <#else>
                            <#if item.nodeType==3>
                            <div class="variableItem" title="${(item.code)!""}"> ${(item.value)!"N/A"}
                            <div>
                            <#elseif item.dataType?? && item.dataType.ordinal() == 1>
                                <div title="${(item.code)!""}">${(item.value?number)!"N/A"}</div>
                            <#else>
                                <div title="${(item.code)!""}">${(item.value)!"N/A"}</div>
                            </#if>
                        </#if>
                    </#list>
                </#if>
            </#list>
        </#if>
        <#if table.type=="VCT">
            <#list table.rows as rows>
                <#if rows?index==0>
                <tr>
                    <#list rows.rowItems as item>
                    <th>${item.description}
                    </#list>
                <#else>
                <tr>
                    <#list rows.rowItems as item>
                    <td>
                        <#if item.nodeType==3>
                        <div class="variableItem" title="${(item.code)!""}"> ${(item.value)!"N/A"}
                        <div>
                        <#elseif item.dataType?? && item.dataType.ordinal() == 1>
                            <div title="${(item.code)!""}">${(item.value?number)!"N/A"}</div>
                        <#else>
                            <div title="${(item.code)!""}">${(item.value)!"N/A"}</div>
                        </#if>
                    </#list>
                </#if>
            </#list>
        </#if>
    </table>
</#list>
</div>
</body>
</html>
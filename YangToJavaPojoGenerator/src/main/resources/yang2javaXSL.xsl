<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="module">

<xsl:for-each select="grouping">

import com.google.gson.annotations.SerializedName;
import java.util.*;

<xsl:variable name="groupName" select="@name"/>

public class <xsl:value-of select="$groupName"/> {
<xsl:for-each select="leaf">
@SerializedName("<xsl:value-of select="@name"/>")
private List&lt;<xsl:value-of select="type/@name"/>&gt; <xsl:value-of select="@name"/>;
</xsl:for-each>

<xsl:for-each select="leaf">
public <xsl:value-of select="type/@name"/> geT<xsl:value-of select="@name"/>() {
return this._<xsl:value-of select="@name"/>;
}

public void seT<xsl:value-of select="@name"/>(<xsl:value-of select="type/@name"/> <xsl:value-of select="@name"/>) {
 this._<xsl:value-of select="@name"/> = <xsl:value-of select="@name"/>;
}
</xsl:for-each>
}
</xsl:for-each>
</xsl:template>


</xsl:stylesheet>
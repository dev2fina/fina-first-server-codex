package net.fina.common.client.dcs;

/**
 * Enumeration defines all kind of documents to be uploaded.
 *
 * @author dato.java.
 */
public enum DocumentType {
    /**
     * Type is unknown when system is not able to identify document content.
     */
    UNKNOWN,
    /**
     * Type is excel when document content is any version of excel document.
     */
    EXCEL,
    /**
     * Type is zip when document content is zip.
     */
    ZIP,
    /**
     * Type is xml when document content is xml.
     */
    XML,
    /**
     * Type is FinA when document content is FinA.
     */
    FINA,
    /**
     * Type is FinA when document content is VipNet.
     */
    VIP_NET;

}

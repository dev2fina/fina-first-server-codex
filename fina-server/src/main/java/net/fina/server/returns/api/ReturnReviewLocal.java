package net.fina.server.returns.api;

import net.fina.common.client.exception.OfficeTypeException;
import net.fina.server.returns.model.RReviewModel;

import java.util.List;

public interface ReturnReviewLocal {
    RReviewModel loadConvertedReturnsContent(List<Long> returnIds, String fileType, long versionId, long langId) throws OfficeTypeException;

    RReviewModel loadConvertedReturnsContent(List<Long> returnIds, String fileType, long versionId, long langId, String returnTypeCode) throws OfficeTypeException;
}

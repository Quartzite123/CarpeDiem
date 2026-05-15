<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CarpeDiem — Notes Library</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;500;600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="header.jsp"><jsp:param name="active" value="notes"/></jsp:include>
    <main class="main-content">
        <div class="page-title">📄 Notes Library</div>
        <div class="page-sub">Shared PDFs — visible to all squad members</div>

        <c:if test="${param.error == 'size'}">
            <div class="alert alert-error">File too large. Maximum size is 5MB.</div>
        </c:if>

        <!-- Change 6b: Hide upload form entirely in demo mode -->
        <c:choose>
            <c:when test="${sessionScope.isDemo == true}">
                <div class="upload-card demo-disabled-notice">
                    <span>🔒</span> Uploads disabled in demo mode
                </div>
            </c:when>
            <c:otherwise>
                <div class="upload-card">
                    <div class="upload-card-title">Upload PDF</div>
                    <form method="post" action="${pageContext.request.contextPath}/notes"
                          enctype="multipart/form-data" class="upload-form">
                        <div class="upload-fields">
                            <div class="field-group">
                                <label>Title</label>
                                <input type="text" name="title" placeholder="e.g. DSA Notes Week 3" required>
                            </div>
                            <div class="field-group">
                                <label>Subject</label>
                                <input type="text" name="subject" placeholder="e.g. DSA, DBMS, CN">
                            </div>
                            <div class="field-group">
                                <label>PDF File</label>
                                <input type="file" name="file" accept=".pdf" required class="file-input">
                            </div>
                        </div>
                        <button type="submit" class="btn btn-primary">Upload PDF</button>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Filter tabs -->
        <div class="filter-tabs" id="subject-tabs">
            <button class="filter-tab active" onclick="filterPDFs('all', this)">All</button>
            <c:forEach var="pdf" items="${pdfs}">
                <c:if test="${not empty pdf.subjectTag}">
                    <button class="filter-tab" onclick="filterPDFs('${pdf.subjectTag}', this)">${pdf.subjectTag}</button>
                </c:if>
            </c:forEach>
        </div>

        <!-- PDF List -->
        <div class="pdf-grid" id="pdf-list">
            <c:choose>
                <c:when test="${empty pdfs}">
                    <div class="empty-state">No PDFs uploaded yet. Be the first!</div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="pdf" items="${pdfs}">
                        <div class="pdf-card" data-subject="${pdf.subjectTag}">
                            <div class="pdf-icon">📄</div>
                            <div class="pdf-info">
                                <div class="pdf-title">${pdf.title}</div>
                                <div class="pdf-meta">
                                    <span class="pdf-tag">${not empty pdf.subjectTag ? pdf.subjectTag : 'General'}</span>
                                    <span>by ${pdf.uploaderName}</span>
                                    <span>${pdf.uploadedAt}</span>
                                </div>
                            </div>
                            <div class="pdf-actions">
                                <a href="${pageContext.request.contextPath}/notes?action=download&id=${pdf.id}"
                                   target="_blank" class="btn btn-sm">View</a>
                                <c:if test="${sessionScope.userId == pdf.userId && sessionScope.isDemo != true}">
                                    <form method="post" action="${pageContext.request.contextPath}/notes"
                                          style="display:inline"
                                          onsubmit="return confirm('Delete this PDF?')">
                                        <input type="hidden" name="action" value="delete">
                                        <input type="hidden" name="id" value="${pdf.id}">
                                        <button type="submit" class="btn btn-sm btn-danger">Delete</button>
                                    </form>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>
<script>
function filterPDFs(subject, btn) {
    document.querySelectorAll('.filter-tab').forEach(t => t.classList.remove('active'));
    btn.classList.add('active');
    document.querySelectorAll('.pdf-card').forEach(card => {
        card.style.display = (subject === 'all' || card.dataset.subject === subject) ? '' : 'none';
    });
}
// Remove duplicate filter tabs
const seen = new Set(['all']);
document.querySelectorAll('.filter-tab').forEach(btn => {
    const t = btn.textContent.trim();
    if (seen.has(t)) btn.remove(); else seen.add(t);
});
</script>
</body>
</html>

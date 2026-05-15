<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CarpeDiem — Timetable</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Space+Grotesk:wght@300;400;500;600;700&family=DM+Sans:wght@300;400;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="header.jsp"><jsp:param name="active" value="timetable"/></jsp:include>
    <main class="main-content">
        <div class="page-title">🗓 Timetable</div>
        <div class="page-sub">Upload your college timetable. Add personal notes as an overlay.</div>

        <c:if test="${param.error == 'size'}">
            <div class="alert alert-error">File too large. Maximum size is 5MB.</div>
        </c:if>

        <!-- Change 6b: Hide upload form in demo mode -->
        <c:choose>
            <c:when test="${sessionScope.isDemo == true}">
                <div class="upload-card demo-disabled-notice">
                    <span>🔒</span> Timetable upload disabled in demo mode
                </div>
            </c:when>
            <c:otherwise>
                <div class="upload-card">
                    <div class="upload-card-title">${timetable != null && timetable.hasBase() ? 'Replace Timetable' : 'Upload Timetable'}</div>
                    <form method="post" action="${pageContext.request.contextPath}/timetable"
                          enctype="multipart/form-data" class="upload-form">
                        <div class="field-group">
                            <label>Upload image (JPG/PNG) or PDF of your timetable</label>
                            <input type="file" name="file" accept=".jpg,.jpeg,.png,.pdf" required class="file-input">
                        </div>
                        <button type="submit" class="btn btn-primary">Upload</button>
                    </form>
                </div>
            </c:otherwise>
        </c:choose>

        <!-- Timetable viewer -->
        <c:choose>
            <c:when test="${timetable != null && timetable.hasBase()}">
                <div class="timetable-container">
                    <div class="timetable-label">Base Timetable (faded)</div>
                    <div class="timetable-view" id="tt-view">
                        <c:choose>
                            <c:when test="${timetable.baseFileType == 'pdf'}">
                                <iframe src="${pageContext.request.contextPath}/timetable?action=serve"
                                        class="tt-iframe" id="tt-iframe"></iframe>
                            </c:when>
                            <c:otherwise>
                                <img src="${pageContext.request.contextPath}/timetable?action=serve"
                                     class="tt-image" id="tt-image" alt="Timetable">
                            </c:otherwise>
                        </c:choose>
                        <canvas id="overlay-canvas" class="overlay-canvas"></canvas>
                    </div>

                    <div class="overlay-toolbar">
                        <div class="toolbar-label">Your Annotations</div>
                        <div class="toolbar-controls">
                            <label>Color: <input type="color" id="pen-color" value="#7c6af7"></label>
                            <label>Size: <input type="range" id="pen-size" min="1" max="10" value="3"></label>
                            <button class="btn btn-sm" onclick="clearCanvas()">Clear All</button>
                            <button class="btn btn-primary btn-sm" onclick="saveEdits()">Save Notes</button>
                        </div>
                    </div>
                    <div id="save-status" style="font-size:12px;color:var(--muted);margin-top:6px"></div>
                </div>

                <script>
                const CTX_PATH = '${pageContext.request.contextPath}';
                const SAVED_EDITS = `${timetable.personalEdits != null ? timetable.personalEdits : ''}`;
                </script>
                <script src="${pageContext.request.contextPath}/js/timetable.js"></script>
            </c:when>
            <c:otherwise>
                <div class="empty-state" style="margin-top:40px">
                    Upload your timetable above to get started.
                </div>
            </c:otherwise>
        </c:choose>
    </main>
</div>
</body>
</html>

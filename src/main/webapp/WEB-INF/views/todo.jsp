<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>CarpeDiem — Todo List</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="header.jsp"><jsp:param name="active" value="todo"/></jsp:include>
    <main class="main-content">
        <div class="page-title">✅ Todo List</div>
        <div class="page-sub">Private — only you can see this</div>

        <c:if test="${param.error == 'limit'}">
            <div class="alert alert-error">Demo limit reached (20 tasks). Resets at midnight.</div>
        </c:if>

        <!-- Add form -->
        <div class="upload-card">
            <div class="upload-card-title">Add Task</div>
            <form method="post" action="${pageContext.request.contextPath}/todo" class="upload-form">
                <input type="hidden" name="action" value="add">
                <div class="upload-fields">
                    <div class="field-group" style="flex:2">
                        <label>Task</label>
                        <input type="text" name="title" required placeholder="e.g. Solve 5 DP problems">
                    </div>
                    <div class="field-group">
                        <label>Subject</label>
                        <input type="text" name="subject" placeholder="DSA, DBMS...">
                    </div>
                    <div class="field-group">
                        <label>Due Date</label>
                        <input type="date" name="dueDate">
                    </div>
                    <div class="field-group">
                        <label>Priority</label>
                        <select name="priority">
                            <option value="HIGH">🔴 High</option>
                            <option value="MEDIUM" selected>🟡 Medium</option>
                            <option value="LOW">🟢 Low</option>
                        </select>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Add Task</button>
            </form>
        </div>

        <!-- Stats bar -->
        <div class="todo-stats">
            <c:set var="total" value="${todos.size()}"/>
            <c:set var="done" value="0"/>
            <c:forEach var="t" items="${todos}"><c:if test="${t.done}"><c:set var="done" value="${done + 1}"/></c:if></c:forEach>
            <span>${done} / ${total} completed</span>
            <c:if test="${total > 0}">
                <div class="mini-bar-wrap" style="width:200px;display:inline-block;margin-left:12px">
                    <div class="mini-bar-fill" style="width:${total > 0 ? done * 100 / total : 0}%;background:var(--green)"></div>
                </div>
            </c:if>
        </div>

        <!-- Todo items -->
        <div class="todo-list">
            <c:choose>
                <c:when test="${empty todos}">
                    <div class="empty-state">No tasks yet. Add one above!</div>
                </c:when>
                <c:otherwise>
                    <c:forEach var="t" items="${todos}">
                        <div class="todo-item ${t.done ? 'todo-done' : ''} ${t.overdue ? 'todo-overdue' : ''}">
                            <div class="todo-left">
                                <!-- Toggle -->
                                <form method="post" action="${pageContext.request.contextPath}/todo" style="display:inline">
                                    <input type="hidden" name="action" value="toggle">
                                    <input type="hidden" name="id" value="${t.id}">
                                    <button type="submit" class="todo-check ${t.done ? 'checked' : ''}">
                                        ${t.done ? '✓' : ''}
                                    </button>
                                </form>
                                <div class="todo-text-wrap">
                                    <div class="todo-title">${t.title}</div>
                                    <div class="todo-meta">
                                        <c:if test="${not empty t.subjectTag}">
                                            <span class="pdf-tag">${t.subjectTag}</span>
                                        </c:if>
                                        <span class="priority-badge priority-${t.priority.toLowerCase()}">${t.priority}</span>
                                        <c:if test="${t.dueDate != null}">
                                            <span class="${t.overdue ? 'overdue-text' : ''}">
                                                Due: ${t.dueDate}
                                                ${t.overdue ? '⚠️ Overdue' : ''}
                                            </span>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                            <form method="post" action="${pageContext.request.contextPath}/todo">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${t.id}">
                                <button type="submit" class="btn btn-sm btn-danger" onclick="return confirm('Delete?')">✕</button>
                            </form>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </main>
</div>
</body>
</html>

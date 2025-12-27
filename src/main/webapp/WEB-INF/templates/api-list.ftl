<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Public APIs Parser</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
            background: #fafafa;
            padding: 20px;
            min-height: 100vh;
            color: #0a0a0a;
        }

        .container {
            max-width: 1400px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            border: 1px solid #e4e4e7;
            box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }

        .header {
            background: white;
            border-bottom: 1px solid #e4e4e7;
            color: #0a0a0a;
            padding: 30px;
        }

        .header h1 {
            font-size: 2.5em;
            margin-bottom: 10px;
            font-weight: 700;
        }

        .header p {
            font-size: 1.1em;
            color: #71717a;
        }

        .controls {
            padding: 30px;
            background: #fafafa;
            border-bottom: 1px solid #e4e4e7;
        }

        .control-row {
            display: flex;
            gap: 15px;
            margin-bottom: 20px;
            flex-wrap: wrap;
            align-items: center;
        }

        .control-group {
            flex: 1;
            min-width: 200px;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-weight: 500;
            color: #0a0a0a;
            font-size: 14px;
        }

        select, input[type="text"] {
            width: 100%;
            padding: 10px 12px;
            border: 1px solid #e4e4e7;
            border-radius: 6px;
            font-size: 14px;
            transition: all 0.2s;
            background: white;
            color: #0a0a0a;
        }

        select:hover, input[type="text"]:hover {
            border-color: #d4d4d8;
        }

        select:focus, input[type="text"]:focus {
            outline: none;
            border-color: #0a0a0a;
            box-shadow: 0 0 0 3px rgba(0, 0, 0, 0.1);
        }

        .checkbox-group {
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
            align-items: center;
        }

        .checkbox-label {
            display: flex;
            align-items: center;
            gap: 8px;
            cursor: pointer;
            user-select: none;
            font-size: 14px;
        }

        .checkbox-label input[type="checkbox"] {
            width: 16px;
            height: 16px;
            cursor: pointer;
            border-radius: 4px;
        }

        .button-group {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        button {
            padding: 10px 20px;
            border: 1px solid #e4e4e7;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            transition: all 0.2s;
            background: white;
            color: #0a0a0a;
        }

        button:hover {
            background: #fafafa;
            border-color: #d4d4d8;
        }

        .btn-primary {
            background: #0a0a0a;
            color: white;
            border-color: #0a0a0a;
        }

        .btn-primary:hover {
            background: #27272a;
            border-color: #27272a;
        }

        .btn-secondary {
            background: white;
            color: #0a0a0a;
            border-color: #e4e4e7;
        }

        .btn-secondary:hover {
            background: #f4f4f5;
        }

        .btn-random {
            background: #71717a;
            color: white;
            border-color: #71717a;
        }

        .btn-random:hover {
            background: #52525b;
            border-color: #52525b;
        }

        .stats {
            padding: 20px 30px;
            background: white;
            border-bottom: 1px solid #e4e4e7;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 15px;
        }

        .stats-info {
            font-size: 14px;
            color: #52525b;
            font-weight: 500;
        }

        .loading {
            text-align: center;
            padding: 40px;
            font-size: 18px;
            color: #71717a;
        }

        .table-container {
            padding: 30px;
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
        }

        th {
            background: #fafafa;
            color: #0a0a0a;
            padding: 12px 15px;
            text-align: left;
            font-weight: 600;
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            border-bottom: 1px solid #e4e4e7;
            position: sticky;
            top: 0;
            z-index: 10;
        }

        td {
            padding: 12px 15px;
            border-bottom: 1px solid #f4f4f5;
            font-size: 14px;
        }

        tr:hover {
            background: #fafafa;
        }

        .badge {
            display: inline-block;
            padding: 3px 10px;
            border-radius: 4px;
            font-size: 12px;
            font-weight: 500;
            border: 1px solid;
            min-width: 55px;
        }

        .badge-yes {
            background: white;
            color: #16a34a;
            border-color: #16a34a;
        }

        .badge-no {
            background: white;
            color: #dc2626;
            border-color: #dc2626;
        }

        .badge-auth {
            background: white;
            color: #ea580c;
            border-color: #ea580c;
        }

        .api-link {
            color: #0a0a0a;
            text-decoration: underline;
            font-weight: 500;
        }

        .api-link:hover {
            color: #52525b;
        }

        .no-results {
            text-align: center;
            padding: 60px 20px;
            color: #71717a;
            font-size: 16px;
        }

        .category-count {
            background: #0a0a0a;
            color: white;
            padding: 2px 8px;
            border-radius: 4px;
            font-size: 12px;
            margin-left: 5px;
            font-weight: 500;
        }

        @media (max-width: 768px) {
            .control-row {
                flex-direction: column;
            }

            .control-group {
                min-width: 100%;
            }

            table {
                font-size: 13px;
            }

            th, td {
                padding: 10px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Public APIs Parser</h1>
            <p>Исследуйте и фильтруйте публичные API из разных категорий</p>
        </div>

        <div class="controls">
            <div class="control-row">
                <div class="control-group">
                    <label for="categorySelect">Категория:</label>
                    <select id="categorySelect">
                        <option value="All">Все категории</option>
                        <#list categories as category>
                            <option value="${category?html}">
                                ${category?html}
                                <#if categoryCounts[category]??>
                                    (${categoryCounts[category]})
                                </#if>
                            </option>
                        </#list>
                    </select>
                </div>

                <div class="control-group">
                    <label for="searchInput">Поиск:</label>
                    <input type="text" id="searchInput" placeholder="Поиск по названию или описанию...">
                </div>
            </div>

            <div class="control-row">
                <div class="checkbox-group">
                    <label class="checkbox-label">
                        <input type="checkbox" id="onlyHttps">
                        <span>Только HTTPS</span>
                    </label>
                    <label class="checkbox-label">
                        <input type="checkbox" id="noAuth">
                        <span>Без Auth</span>
                    </label>
                    <label class="checkbox-label">
                        <input type="checkbox" id="withCors">
                        <span>С CORS</span>
                    </label>
                    <label class="checkbox-label">
                        <input type="checkbox" id="sortByName">
                        <span>Сортировать по имени</span>
                    </label>
                </div>

                <div class="button-group">
                    <button class="btn-primary" onclick="loadApis()">Применить фильтры</button>
                    <button class="btn-secondary" onclick="resetFilters()">Сбросить</button>
                    <button class="btn-random" onclick="openRandomApi()">Случайный API</button>
                </div>
            </div>
        </div>

        <div class="stats">
            <div class="stats-info">
                Найдено API: <span id="apiCount">0</span>
            </div>
            <div class="stats-info">
                Категорий: <span id="categoryCount">${categories?size}</span>
            </div>
        </div>

        <div class="table-container">
            <div id="loadingMessage" class="loading">Загрузка данных...</div>
            <div id="tableContent" style="display: none;">
                <table id="apiTable">
                    <thead>
                        <tr>
                            <th>Название</th>
                            <th>Описание</th>
                            <th>Auth</th>
                            <th>HTTPS</th>
                            <th>CORS</th>
                            <th>Категория</th>
                            <th>Ссылка</th>
                        </tr>
                    </thead>
                    <tbody id="apiTableBody">
                    </tbody>
                </table>
            </div>
            <div id="noResults" class="no-results" style="display: none;">
                Ничего не найдено. Попробуйте изменить фильтры.
            </div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            loadApis();

            document.getElementById('searchInput').addEventListener('input', debounce(loadApis, 500));
        });

        function debounce(func, wait) {
            let timeout;
            return function executedFunction(...args) {
                const later = () => {
                    clearTimeout(timeout);
                    func(...args);
                };
                clearTimeout(timeout);
                timeout = setTimeout(later, wait);
            };
        }

        function loadApis() {
            const category = document.getElementById('categorySelect').value;
            const search = document.getElementById('searchInput').value;
            const onlyHttps = document.getElementById('onlyHttps').checked;
            const noAuth = document.getElementById('noAuth').checked;
            const withCors = document.getElementById('withCors').checked;
            const sortByName = document.getElementById('sortByName').checked;

            // Формируем URL с параметрами
            const params = new URLSearchParams({
                action: 'getApis',
                category: category,
                search: search,
                onlyHttps: onlyHttps,
                noAuth: noAuth,
                withCors: withCors,
                sort: sortByName ? 'name' : ''
            });

            document.getElementById('loadingMessage').style.display = 'block';
            document.getElementById('tableContent').style.display = 'none';
            document.getElementById('noResults').style.display = 'none';

            fetch('apis?' + params.toString())
                .then(response => response.json())
                .then(data => {
                    displayApis(data);
                })
                .catch(error => {
                    console.error('Ошибка загрузки данных:', error);
                    document.getElementById('loadingMessage').innerHTML =
                        '<div style="color: red;">❌ Ошибка загрузки данных</div>';
                });
        }

        function displayApis(apis) {
            const tbody = document.getElementById('apiTableBody');
            tbody.innerHTML = '';

            document.getElementById('apiCount').textContent = apis.length;

            if (apis.length === 0) {
                document.getElementById('loadingMessage').style.display = 'none';
                document.getElementById('noResults').style.display = 'block';
                return;
            }

            apis.forEach(api => {
                const row = document.createElement('tr');

                // Название
                const nameCell = document.createElement('td');
                nameCell.innerHTML = '<strong>' + escapeHtml(api.name) + '</strong>';
                row.appendChild(nameCell);

                // Описание
                const descCell = document.createElement('td');
                descCell.textContent = api.description || '-';
                row.appendChild(descCell);

                // Auth
                const authCell = document.createElement('td');
                if (api.auth && api.auth !== 'No') {
                    authCell.innerHTML = '<span class="badge badge-auth">' + escapeHtml(api.auth) + '</span>';
                } else {
                    authCell.innerHTML = '<span class="badge badge-yes">No</span>';
                }
                row.appendChild(authCell);

                // HTTPS
                const httpsCell = document.createElement('td');
                httpsCell.innerHTML = api.https
                    ? '<span class="badge badge-yes">✓ Yes</span>'
                    : '<span class="badge badge-no">✗ No</span>';
                row.appendChild(httpsCell);

                // CORS
                const corsCell = document.createElement('td');
                const corsValue = api.cors || 'Unknown';
                const corsBadgeClass = corsValue === 'Yes' ? 'badge-yes' :
                                      corsValue === 'No' ? 'badge-no' : 'badge-auth';
                corsCell.innerHTML = '<span class="badge ' + corsBadgeClass + '">' + escapeHtml(corsValue) + '</span>';
                row.appendChild(corsCell);

                // Категория
                const categoryCell = document.createElement('td');
                categoryCell.textContent = api.category || '-';
                row.appendChild(categoryCell);

                // Ссылка
                const linkCell = document.createElement('td');
                linkCell.innerHTML = '<a href="' + escapeHtml(api.link) + '" target="_blank" class="api-link">Документация →</a>';
                row.appendChild(linkCell);

                tbody.appendChild(row);
            });

            document.getElementById('loadingMessage').style.display = 'none';
            document.getElementById('tableContent').style.display = 'block';
        }

        function resetFilters() {
            document.getElementById('categorySelect').value = 'All';
            document.getElementById('searchInput').value = '';
            document.getElementById('onlyHttps').checked = false;
            document.getElementById('noAuth').checked = false;
            document.getElementById('withCors').checked = false;
            document.getElementById('sortByName').checked = false;
            loadApis();
        }

        // Открыть случайный API
        function openRandomApi() {
            window.open('apis?action=randomApi', '_blank');
        }

        // Экранирование HTML
        function escapeHtml(text) {
            const div = document.createElement('div');
            div.textContent = text;
            return div.innerHTML;
        }
    </script>
</body>
</html>

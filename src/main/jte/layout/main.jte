@param gg.jte.Content content

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CRM - Lead Management</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50">
    <header class="bg-blue-600 text-white p-4 shadow-md flex justify-between items-center">
        <h1 class="text-2xl font-bold">CRM System</h1>
        <nav class="flex gap-4">
            <a href="/leads" class="hover:underline">Лиды</a>
            <a href="/deals" class="hover:underline">Сделки</a>
            <a href="/deals/kanban" class="hover:underline">Kanban</a>
        </nav>
    </header>

    <main class="container mx-auto p-6">
        ${content}
    </main>

    <footer class="bg-gray-800 text-white p-4 text-center mt-8">
        <p>&copy; 2025 CRM Project</p>
    </footer>
</body>
</html>
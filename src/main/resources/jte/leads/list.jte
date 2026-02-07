@param java.util.List<ru.mentee.power.crm.model.Lead> leads
@param String search
@param String status

<form method="get" action="/leads" class="bg-white shadow rounded-lg p-4 mb-6">
  <div class="flex flex-wrap items-end gap-4">
    <div>
      <label for="search" class="block text-sm font-medium text-gray-700 mb-1">Поиск по имени/email</label>
      <input type="text" id="search" name="search" value="${search != null ? search : ""}"
             class="border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-blue-500 focus:border-blue-500 min-w-[200px]"
             placeholder="Имя или email">
    </div>
    <div>
      <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Статус</label>
      <select id="status" name="status" class="border border-gray-300 rounded-md px-3 py-2 shadow-sm focus:ring-blue-500 focus:border-blue-500">
        @if(status == null || status.isEmpty())
        <option value="" selected>Все статусы</option>
        @else
        <option value="">Все статусы</option>
        @endif
        @if(status != null && status.equals("NEW"))
        <option value="NEW" selected>NEW</option>
        @else
        <option value="NEW">NEW</option>
        @endif
        @if(status != null && status.equals("CONTACTED"))
        <option value="CONTACTED" selected>CONTACTED</option>
        @else
        <option value="CONTACTED">CONTACTED</option>
        @endif
        @if(status != null && status.equals("QUALIFIED"))
        <option value="QUALIFIED" selected>QUALIFIED</option>
        @else
        <option value="QUALIFIED">QUALIFIED</option>
        @endif
      </select>
    </div>
    <div class="flex gap-2">
      <button type="submit" class="px-4 py-2 bg-blue-500 text-white rounded-md hover:bg-blue-600 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
        Найти
      </button>
      <a href="/leads" class="px-4 py-2 bg-gray-200 text-gray-800 rounded-md hover:bg-gray-300 focus:ring-2 focus:ring-gray-400 focus:ring-offset-2 inline-block">
        Сбросить фильтры
      </a>
    </div>
  </div>
</form>

<div class="mb-4">
  <a href="/leads/new"
     class="inline-flex items-center px-4 py-2 bg-green-600 text-white font-medium rounded-md hover:bg-green-700 focus:ring-2 focus:ring-green-500 focus:ring-offset-2">
    Добавить лида
  </a>
</div>

<!-- Таблица лидов -->
<div class="overflow-x-auto relative shadow-md sm:rounded-lg">
  <table class="w-full text-sm text-left text-gray-500 dark:text-gray-400">
    <thead class="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
      <tr>
        <th scope="col" class="py-3 px-6">Имя</th>
        <th scope="col" class="py-3 px-6">Компании</th>
        <th scope="col" class="py-3 px-6">Статус</th>
        <th scope="col" class="py-3 px-6">Действия</th>
      </tr>
    </thead>
    <tbody>
      @for(var lead : leads)
      <tr class="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
        <td class="py-4 px-6">${lead.contact().email()}</td>
        <td class="py-4 px-6">${lead.company()}</td>
        <td class="py-4 px-6">${lead.status()}</td>
        <td class="py-4 px-6">
          <a href="/leads/${lead.id()}/edit" class="text-blue-600 hover:underline">Редактировать</a>
          <form action="/leads/${lead.id().toString()}/delete" method="post" class="inline ml-2">
            <button type="submit" class="px-2 py-1 rounded text-sm" style="background-color: #dc2626; color: white;" onmouseover="this.style.backgroundColor='#b91c1c'" onmouseout="this.style.backgroundColor='#dc2626'" onclick="return confirm('Удалить лида?')">Удалить</button>
          </form>
        </td>
      </tr>
      @endfor
    </tbody>
  </table>
</div>

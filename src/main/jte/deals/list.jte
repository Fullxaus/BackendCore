@param java.util.List<ru.mentee.power.crm.domain.Deal> deals
@param java.util.Map<java.util.UUID, String> leadNames

<div class="mb-4">
  <a href="/deals/kanban"
     class="inline-flex items-center px-4 py-2 bg-indigo-600 text-white font-medium rounded-md hover:bg-indigo-700 focus:ring-2 focus:ring-indigo-500 focus:ring-offset-2">
    Kanban доска
  </a>
</div>

<div class="bg-white rounded-lg shadow-md p-6">
  <h2 class="text-2xl font-bold mb-4">Список сделок</h2>

  <table class="min-w-full bg-white border border-gray-200">
    <thead class="bg-gray-100">
      <tr>
        <th class="px-4 py-2 text-left">Клиент</th>
        <th class="px-4 py-2 text-left">Сумма</th>
        <th class="px-4 py-2 text-left">Статус</th>
        <th class="px-4 py-2 text-left">Создана</th>
        <th class="px-4 py-2 text-left">Действия</th>
      </tr>
    </thead>
    <tbody>
      @for(var deal : deals)
      <tr class="border-t hover:bg-gray-50">
        <td class="px-4 py-2">${leadNames != null && leadNames.containsKey(deal.getLeadId()) ? leadNames.get(deal.getLeadId()) : "—"}</td>
        <td class="px-4 py-2">${deal.getAmount()}</td>
        <td class="px-4 py-2">
          <span class="px-2 py-1 rounded text-sm bg-blue-100 text-blue-800">${deal.getStatus()}</span>
        </td>
        <td class="px-4 py-2">${deal.getCreatedAt() != null ? deal.getCreatedAt().toString() : "—"}</td>
        <td class="px-4 py-2">
          <a href="/deals/kanban" class="text-blue-600 hover:underline">На Kanban</a>
        </td>
      </tr>
      @endfor
    </tbody>
  </table>
</div>

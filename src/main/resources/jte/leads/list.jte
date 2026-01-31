@param java.util.List<ru.mentee.power.crm.model.Lead> leads
@param ru.mentee.power.crm.model.LeadStatus currentFilter

<div class="mb-4 flex gap-2">
  <a href="/leads"
     class='px-4 py-2 rounded ${currentFilter == null ? "bg-blue-500 text-white" : "bg-gray-200"}'>
    Все
  </a>
  <a href="/leads?status=NEW"
     class='px-4 py-2 rounded ${currentFilter == ru.mentee.power.crm.model.LeadStatus.NEW ? "bg-blue-500 text-white" : "bg-gray-200"}'>
    NEW
  </a>
  <a href="/leads?status=CONTACTED"
     class='px-4 py-2 rounded ${currentFilter == ru.mentee.power.crm.model.LeadStatus.CONTACTED ? "bg-blue-500 text-white" : "bg-gray-200"}'>
    CONTACTED
  </a>
  <a href="/leads?status=QUALIFIED"
     class='px-4 py-2 rounded ${currentFilter == ru.mentee.power.crm.model.LeadStatus.QUALIFIED ? "bg-blue-500 text-white" : "bg-gray-200"}'>
    QUALIFIED
  </a>
</div>

@if(currentFilter != null)
<p class="text-sm text-gray-600 mb-2">Показаны лиды со статусом: ${currentFilter}</p>
@endif

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
      </tr>
    </thead>
    <tbody>
      @for(var lead : leads)
      <tr class="bg-white border-b dark:bg-gray-800 dark:border-gray-700">
        <td class="py-4 px-6">${lead.contact().email()}</td>
        <td class="py-4 px-6">${lead.company()}</td>
        <td class="py-4 px-6">${lead.status()}</td>
      </tr>
      @endfor
    </tbody>
  </table>
</div>





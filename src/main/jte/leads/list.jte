@param java.util.List<ru.mentee.power.crm.model.Lead> leads
@param ru.mentee.power.crm.model.LeadStatus currentFilter

<div class="mb-4 flex gap-2">
  <a href="/leads"
     class='px-4 py-2 rounded ${currentFilter == null ? "bg-blue-500 text-white" : "bg-gray-200"}'>
    Все
  </a>
  <a href="/leads?status=NEW"
     class='px-4 py-2 rounded ${currentFilter != null && currentFilter.name().equals("NEW") ? "bg-blue-500 text-white" : "bg-gray-200"}'>
    NEW
  </a>
  <a href="/leads?status=CONTACTED"
     class='px-4 py-2 rounded ${currentFilter != null && currentFilter.name().equals("CONTACTED") ? "bg-blue-500 text-white" : "bg-gray-200"}'>
    CONTACTED
  </a>
  <a href="/leads?status=QUALIFIED"
     class='px-4 py-2 rounded ${currentFilter != null && currentFilter.name().equals("QUALIFIED") ? "bg-blue-500 text-white" : "bg-gray-200"}'>
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

<div class="bg-white rounded-lg shadow-md p-6">
    <h2 class="text-2xl font-bold mb-4">Lead List</h2>

    <table class="min-w-full bg-white border border-gray-200">
        <thead class="bg-gray-100">
            <tr>
                <th class="px-4 py-2 text-left">Email</th>
                <th class="px-4 py-2 text-left">Company</th>
                <th class="px-4 py-2 text-left">Status</th>
                <th class="px-4 py-2 text-left">Действия</th>
            </tr>
        </thead>
        <tbody>
            @for(var lead : leads)
                <tr class="border-t hover:bg-gray-50">
                    <td class="px-4 py-2">${lead.contact().email()}</td>
                    <td class="px-4 py-2">${lead.company()}</td>
                    <td class="px-4 py-2">
                        <span class="px-2 py-1 rounded text-sm bg-green-100 text-green-800">
                            ${lead.status()}
                        </span>
                    </td>
                    <td class="px-4 py-2">
                        <a href="/leads/${lead.id().toString()}/edit" class="text-blue-600 hover:underline">Редактировать</a>
                    </td>
                </tr>
            @endfor
        </tbody>
    </table>
</div>

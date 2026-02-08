@param java.util.Map<ru.mentee.power.crm.domain.DealStatus, java.util.List<ru.mentee.power.crm.domain.Deal>> dealsByStatus
@param ru.mentee.power.crm.domain.DealStatus[] statuses
@param java.util.Map<java.util.UUID, String> leadNames

<div class="mb-4">
  <a href="/deals"
     class="inline-flex items-center px-4 py-2 bg-gray-600 text-white font-medium rounded-md hover:bg-gray-700 focus:ring-2 focus:ring-gray-500 focus:ring-offset-2">
    Список сделок
  </a>
</div>

<h2 class="text-2xl font-bold mb-6">Воронка продаж — Kanban</h2>

<div class="flex gap-4 overflow-x-auto pb-4">
  @for(var status : statuses)
  <div class="flex-shrink-0 w-72 bg-gray-100 rounded-lg p-4 min-h-[400px]">
    <h3 class="font-semibold text-gray-800 mb-3 border-b pb-2">${status}</h3>
    <div class="space-y-3">
      @if(dealsByStatus != null && dealsByStatus.containsKey(status))
      @for(var deal : dealsByStatus.get(status))
      <div class="bg-white rounded-lg shadow p-3 border border-gray-200">
        <div class="text-sm font-medium text-gray-900 mb-1">
          ${leadNames != null && leadNames.containsKey(deal.getLeadId()) ? leadNames.get(deal.getLeadId()) : "—"}
        </div>
        <div class="text-lg font-bold text-indigo-600 mb-2">${deal.getAmount()} ₽</div>
        <form action="/deals/${deal.getId().toString()}/transition" method="post" class="mt-2">
          <select name="newStatus" onchange="if(this.value) this.form.submit()"
                  class="text-xs border border-gray-300 rounded px-2 py-1 w-full">
            <option value="">— Сменить статус —</option>
            @for(var s : statuses)
            @if(deal.getStatus().canTransitionTo(s))
            <option value="${s.name()}">→ ${s}</option>
            @endif
            @endfor
          </select>
        </form>
      </div>
      @endfor
      @endif
    </div>
  </div>
  @endfor
</div>

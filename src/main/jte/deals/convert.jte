@param ru.mentee.power.crm.model.Lead lead

<div class="max-w-md mx-auto mt-8 p-6 bg-white rounded-lg shadow-md">
  <h2 class="text-2xl font-bold mb-6 text-gray-800">Конвертировать Lead в сделку</h2>

  <div class="mb-4 p-3 bg-gray-50 rounded">
    <p class="text-sm text-gray-600">Lead: ${lead.company()} (${lead.contact().email()})</p>
  </div>

  <form action="/deals/convert" method="post" class="space-y-4">
    <input type="hidden" name="leadId" value="${lead.id().toString()}" />
    <div>
      <label for="amount" class="block text-sm font-medium text-gray-700 mb-1">Сумма сделки (₽)</label>
      <input type="number" id="amount" name="amount" step="0.01" min="0" required
             class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
             placeholder="150000.00" />
    </div>
    <div class="flex gap-2">
      <button type="submit"
              class="px-4 py-2 bg-green-600 text-white font-medium rounded-md hover:bg-green-700 focus:ring-2 focus:ring-green-500 focus:ring-offset-2">
        Создать сделку
      </button>
      <a href="/leads" class="px-4 py-2 text-gray-600 hover:underline">Отмена</a>
    </div>
  </form>

  <a href="/leads" class="mt-4 inline-block text-sm text-blue-600 hover:underline">← К списку лидов</a>
</div>

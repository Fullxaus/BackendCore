@param ru.mentee.power.crm.model.Lead lead
@param ru.mentee.power.crm.model.LeadStatus[] statuses


<div class="max-w-md mx-auto mt-8 p-6 bg-white rounded-lg shadow-md">
  <h2 class="text-2xl font-bold mb-6 text-gray-800">Редактировать лида</h2>

  <form action="/leads/${lead.id().toString()}" method="post" class="space-y-4">
    <input type="hidden" name="id" value="${lead.id().toString()}">

    <div>
      <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
      <input type="email" id="email" name="email" value="${lead.contact().email()}" required
             class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
             placeholder="test@example.com" />
    </div>


    <div>
      <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">Телефон</label>
      <input type="tel" id="phone" name="phone" value="${lead.contact().phone()}"
             class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
             placeholder="+7 999 123-45-67" />
    </div>

    <div>
      <label for="company" class="block text-sm font-medium text-gray-700 mb-1">Компания</label>
      <input type="text" id="company" name="company" value="${lead.company()}" required
             class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
             placeholder="Acme" />
    </div>

    <div>
      <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Статус</label>
      <select id="status" name="status" required
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500">
        @for(var s : statuses)
        @if(lead.status().equals(s.name()))
        <option value="${s.name()}" selected>${s.name()} — ${s.getDescription()}</option>
        @else
        <option value="${s.name()}">${s.name()} — ${s.getDescription()}</option>
        @endif
        @endfor
      </select>
    </div>

    <div class="flex gap-2">
      <button type="submit"
              class="px-4 py-2 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
        Сохранить
      </button>
      <a href="/leads" class="px-4 py-2 text-blue-600 hover:underline">Отмена</a>
    </div>
  </form>
</div>

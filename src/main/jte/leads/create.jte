@param ru.mentee.power.crm.model.LeadStatus[] statuses

<div class="max-w-md mx-auto mt-8 p-6 bg-white rounded-lg shadow-md">
  <h2 class="text-2xl font-bold mb-6 text-gray-800">Новый лид</h2>

  <form action="/leads" method="post" class="space-y-4">
    <div>
      <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
      <input type="email" id="email" name="email" required
             class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
             placeholder="test@example.com" />
    </div>


    <div>
      <label for="company" class="block text-sm font-medium text-gray-700 mb-1">Компания</label>
      <input type="text" id="company" name="company" required
             class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500"
             placeholder="Acme" />
    </div>


    <div>
      <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Статус</label>
      <select id="status" name="status" required
              class="w-full px-3 py-2 border border-gray-300 rounded-md focus:ring-blue-500 focus:border-blue-500">
        @for(var s : statuses)

        <option value="${s.name()}">${s.name()}  —  ${s.getDescription()}</option>

        @endfor
      </select>
    </div>

    <button type="submit"
            class="w-full py-2 px-4 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
      Создать
    </button>
  </form>

  <a href="/leads" class="mt-4 inline-block text-sm text-blue-600 hover:underline">← К списку лидов</a>
</div>

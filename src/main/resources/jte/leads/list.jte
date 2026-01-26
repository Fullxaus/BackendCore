@param java.util.List<ru.mentee.power.crm.model.Lead> leads

@param List<Lead> leads
@param LeadStatus currentFilter

<div class="mb-4 flex gap-2">
  <a href="/leads"
     class="px-4 py-2 rounded ${currentFilter == null ? 'bg-blue-500 text-white' : 'bg-gray-200'}">
    Все
  </a>
  <a href="/leads?status=NEW"
     class="px-4 py-2 rounded ${currentFilter == LeadStatus.NEW ? 'bg-blue-500 text-white' : 'bg-gray-200'}">
    NEW
  </a>
  <a href="/leads?status=CONTACTED"
     class="px-4 py-2 rounded ${currentFilter == LeadStatus.CONTACTED ? 'bg-blue-500 text-white' : 'bg-gray-200'}">
    CONTACTED
  </a>
  <a href="/leads?status=QUALIFIED"
     class="px-4 py-2 rounded ${currentFilter == LeadStatus.QUALIFIED ? 'bg-blue-500 text-white' : 'bg-gray-200'}">
    QUALIFIED
  </a>
</div>

@if(currentFilter != null)
<p class="text-sm text-gray-600 mb-2">Показаны лиды со статусом: ${currentFilter}</p>
@endif

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
        <td class="py-4 px-6">${lead.getName()}</td>
        <td class="py-4 px-6">${lead.getCompany()}</td>
        <td class="py-4 px-6">${lead.getStatus()}</td>
      </tr>
      @endfor
    </tbody>
  </table>
</div>

<form action="/lead" method="post">
    <input name="email" placeholder="Email" />
    <input name="company" placeholder="Компания" />
    <input name="phone" placeholder="Телефон" />

    <select name="status">
        <option value="NEW">NEW</option>
        <option value="IN_PROGRESS">IN_PROGRESS</option>
        <option value="CLOSED">CLOSED</option>
    </select>

    <input name="city" placeholder="Город" />
    <input name="street" placeholder="Улица" />
    <input name="zip" placeholder="Индекс" />

    <button type="submit">Создать лид</button>
</form>



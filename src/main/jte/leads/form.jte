@param ru.mentee.power.crm.model.LeadForm lead
@param ru.mentee.power.crm.model.LeadStatus[] statuses
@param org.springframework.validation.Errors errors
@param java.util.UUID leadId

<div class="max-w-md mx-auto mt-8 p-6 bg-white rounded-lg shadow-md">
  @if(leadId != null)
  <h2 class="text-2xl font-bold mb-6 text-gray-800">Редактировать лида</h2>
  @else
  <h2 class="text-2xl font-bold mb-6 text-gray-800">Новый лид</h2>
  @endif

  @if(errors != null && errors.hasGlobalErrors())
  <div class="mb-4 p-3 bg-red-50 border border-red-200 rounded-md text-red-700 text-sm">
    @for(var err : errors.getGlobalErrors())
    <div>${err.getDefaultMessage()}</div>
    @endfor
  </div>
  @endif

  <form action="@if(leadId != null)/leads/${leadId.toString()}@else/leads@endif" method="post" class="space-y-4">
    <div class="mb-4">
      <label for="name" class="block text-sm font-medium text-gray-700 mb-1">Имя</label>
      <input type="text" id="name" name="name" value="${lead.name() != null ? lead.name() : ""}"
             class="w-full px-3 py-2 border rounded-md focus:ring-blue-500 focus:border-blue-500 ${errors != null && errors.hasFieldErrors("name") ? 'border-red-500' : 'border-gray-300'}"
             placeholder="Имя" />
      @if(errors != null && errors.hasFieldErrors("name"))
      <p class="mt-1 text-sm text-red-600">${errors.getFieldError("name").getDefaultMessage()}</p>
      @endif
    </div>

    <div class="mb-4">
      <label for="email" class="block text-sm font-medium text-gray-700 mb-1">Email</label>
      <input type="email" id="email" name="email" value="${lead.email() != null ? lead.email() : ""}"
             class="w-full px-3 py-2 border rounded-md focus:ring-blue-500 focus:border-blue-500 ${errors != null && errors.hasFieldErrors("email") ? 'border-red-500' : 'border-gray-300'}"
             placeholder="test@example.com" />
      @if(errors != null && errors.hasFieldErrors("email"))
      <p class="mt-1 text-sm text-red-600">${errors.getFieldError("email").getDefaultMessage()}</p>
      @endif
    </div>

    <div class="mb-4">
      <label for="phone" class="block text-sm font-medium text-gray-700 mb-1">Телефон</label>
      <input type="tel" id="phone" name="phone" value="${lead.phone() != null ? lead.phone() : ""}"
             class="w-full px-3 py-2 border rounded-md focus:ring-blue-500 focus:border-blue-500 ${errors != null && errors.hasFieldErrors("phone") ? 'border-red-500' : 'border-gray-300'}"
             placeholder="+7 999 123-45-67" />
      @if(errors != null && errors.hasFieldErrors("phone"))
      <p class="mt-1 text-sm text-red-600">${errors.getFieldError("phone").getDefaultMessage()}</p>
      @endif
    </div>

    <div class="mb-4">
      <label for="status" class="block text-sm font-medium text-gray-700 mb-1">Статус</label>
      <select id="status" name="status"
              class="w-full px-3 py-2 border rounded-md focus:ring-blue-500 focus:border-blue-500 ${errors != null && errors.hasFieldErrors("status") ? 'border-red-500' : 'border-gray-300'}">
        @for(var s : statuses)
        @if(lead.status() != null && lead.status().equals(s))
        <option value="${s.name()}" selected>${s.name()} — ${s.getDescription()}</option>
        @else
        <option value="${s.name()}">${s.name()} — ${s.getDescription()}</option>
        @endif
        @endfor
      </select>
      @if(errors != null && errors.hasFieldErrors("status"))
      <p class="mt-1 text-sm text-red-600">${errors.getFieldError("status").getDefaultMessage()}</p>
      @endif
    </div>

    @if(leadId != null)
    <div class="flex gap-2">
      <button type="submit"
              class="px-4 py-2 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
        Сохранить
      </button>
      <a href="/leads" class="px-4 py-2 text-blue-600 hover:underline">Отмена</a>
    </div>
    @else
    <button type="submit"
            class="w-full py-2 px-4 bg-blue-600 text-white font-medium rounded-md hover:bg-blue-700 focus:ring-2 focus:ring-blue-500 focus:ring-offset-2">
      Создать
    </button>
    @endif
  </form>

  <a href="/leads" class="mt-4 inline-block text-sm text-blue-600 hover:underline">← К списку лидов</a>
</div>

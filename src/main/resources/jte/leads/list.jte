@param java.util.List<ru.mentee.power.crm.model.Lead> leads

<div class="bg-white rounded-lg shadow-md p-6">
    <h2 class="text-2xl font-bold mb-4">Lead List</h2>

    <table class="min-w-full bg-white border border-gray-200">
        <thead class="bg-gray-100">
            <tr>
                <th class="px-4 py-2 text-left">Email</th>
                <th class="px-4 py-2 text-left">Company</th>
                <th class="px-4 py-2 text-left">Status</th>
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



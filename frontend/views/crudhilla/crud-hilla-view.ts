import { ValidationError } from '@hilla/form';
import { EndpointError } from '@hilla/frontend';
import '@polymer/iron-icon';
import '@vaadin/button';
import '@vaadin/checkbox';
import '@vaadin/crud';
import { CrudDataProviderCallback, CrudDataProviderParams, CrudDeleteEvent, CrudSaveEvent } from '@vaadin/crud';
import '@vaadin/date-picker';
import '@vaadin/date-time-picker';
import '@vaadin/form-layout';
import '@vaadin/notification';
import { Notification } from '@vaadin/notification';
import '@vaadin/polymer-legacy-adapter';
import '@vaadin/text-field';
import '@vaadin/upload';
import '@vaadin/vaadin-icons';
import SamplePerson from 'Frontend/generated/com/example/application/data/entity/SamplePerson';
import Sort from 'Frontend/generated/dev/hilla/mappedtypes/Sort';
import Direction from 'Frontend/generated/org/springframework/data/domain/Sort/Direction';
import * as SamplePersonEndpoint from 'Frontend/generated/SamplePersonEndpoint';
import { html } from 'lit';
import { customElement } from 'lit/decorators.js';
import { View } from '../view';

@customElement('crud-hilla-view')
export class CrudHillaView extends View {
  private dataProvider = this.getData.bind(this);

  render() {
    return html`
      <vaadin-crud
        class="w-full h-full"
        id="crud"
        exclude="Id"
        no-filter
        .dataProvider=${this.dataProvider}
        @save=${this.save}
        @delete=${this.delete}
        @edited-item-changed=${this.requestUpdate}
      >
        <vaadin-form-layout slot="form"
          ><vaadin-text-field label="First name" id="firstName" path="firstName"></vaadin-text-field
          ><vaadin-text-field label="Last name" id="lastName" path="lastName"></vaadin-text-field
          ><vaadin-text-field label="Email" id="email" path="email"></vaadin-text-field
          ><vaadin-text-field label="Phone" id="phone" path="phone"></vaadin-text-field
          ><vaadin-date-picker label="Date of birth" id="dateOfBirth" path="dateOfBirth"></vaadin-date-picker
          ><vaadin-text-field label="Occupation" id="occupation" path="occupation"></vaadin-text-field
          ><vaadin-checkbox id="important" path="important" label="Important"></vaadin-checkbox>
        </vaadin-form-layout>
      </vaadin-crud>
    `;
  }

  private async getData(params: CrudDataProviderParams, callback: CrudDataProviderCallback<SamplePerson | undefined>) {
    const sort: Sort = {
      orders: params.sortOrders.map((order) => ({
        property: order.path,
        direction: order.direction == 'asc' ? Direction.ASC : Direction.DESC,
        ignoreCase: false,
      })),
    };
    const data = await SamplePersonEndpoint.list({ pageNumber: params.page, pageSize: params.pageSize, sort });
    const size = await SamplePersonEndpoint.count();
    callback(data, size);
  }

  async firstUpdated() {
    this.classList.add('flex', 'flex-col', 'h-full');
  }

  private async save(e: CrudSaveEvent<SamplePerson>) {
    await this.doServerAction(() => SamplePersonEndpoint.update(e.detail.item), 'SamplePerson details stored.');
  }

  private async delete(e: CrudDeleteEvent<SamplePerson>) {
    await this.doServerAction(() => SamplePersonEndpoint.delete(e.detail.item.id!), 'SamplePerson deleted.');
  }

  private async doServerAction(fnc: () => void, msg: string) {
    try {
      await fnc();
      Notification.show(msg, { position: 'bottom-start' });
    } catch (error: any) {
      if (error instanceof EndpointError || error instanceof ValidationError) {
        Notification.show(`Server error. ${error.message}`, { theme: 'error', position: 'bottom-start' });
      } else {
        throw error;
      }
    }
  }
}

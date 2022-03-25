import '@vaadin/checkbox';
import '@vaadin/vaadin-crud';
import '@vaadin/vaadin-date-picker';
import '@vaadin/vaadin-date-time-picker';
import '@vaadin/vaadin-form-layout';
import '@vaadin/vaadin-text-field';
import { html, LitElement } from 'lit';
import { customElement } from 'lit/decorators.js';

@customElement('crud-template-view')
export class CrudTemplateView extends LitElement {
  createRenderRoot() {
    // Do not use a shadow root
    return this;
  }

  render() {
    return html`
      <vaadin-crud id="crud">
        <vaadin-form-layout slot="form" id="form">
          <vaadin-text-field label="First name" id="firstName" path="firstName"></vaadin-text-field
          ><vaadin-text-field label="Last name" id="lastName" path="lastName"></vaadin-text-field
          ><vaadin-text-field label="Email" id="email" path="email"></vaadin-text-field
          ><vaadin-text-field label="Phone" id="phone" path="phone"></vaadin-text-field
          ><vaadin-date-picker label="Date of birth" id="dateOfBirth"></vaadin-date-picker
          ><vaadin-text-field label="Occupation" id="occupation" path="occupation"></vaadin-text-field
          ><vaadin-checkbox id="important">Important</vaadin-checkbox>
        </vaadin-form-layout>
      </vaadin-crud>
    `;
  }
}

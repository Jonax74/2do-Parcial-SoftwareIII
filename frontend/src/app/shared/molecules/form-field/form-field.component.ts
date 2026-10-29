import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { InputTextComponent } from '../../atoms/input-text/input-text.component';

@Component({
  selector: 'pa-form-field',
  standalone: true,
  imports: [InputTextComponent],
  template: `
    <div class="field">
      <label [for]="inputId">{{ label }}</label>
      <pa-input-text
        [inputId]="inputId"
        [type]="type"
        [placeholder]="placeholder"
        [autocomplete]="autocomplete"
        [control]="control"
      />
      @if (control.touched && control.invalid) {
        <small class="error">{{ validationMessage }}</small>
      }
    </div>
  `,
  styleUrl: './form-field.component.scss',
})
export class FormFieldComponent {
  @Input({ required: true }) inputId = '';
  @Input({ required: true }) label = '';
  @Input({ required: true }) control!: FormControl<string>;
  @Input() type: 'text' | 'email' | 'password' = 'text';
  @Input() placeholder = '';
  @Input() autocomplete = 'off';
  @Input() validationMessage = 'Revisa este campo.';
}

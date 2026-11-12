import { Component, Input } from '@angular/core';
import { FormControl } from '@angular/forms';
import { InputTextComponent } from '../../atoms/input-text/input-text.component';

@Component({
  selector: 'pa-form-field',
  standalone: true,
  imports: [InputTextComponent],
  templateUrl: './form-field.component.html',
  styleUrl: './form-field.component.css',
})
export class FormFieldComponent {
  @Input({ required: true }) inputId = '';
  @Input({ required: true }) label = '';
  @Input({ required: true }) control!: FormControl<string>;
  @Input() type: 'text' | 'email' | 'password' = 'text';
  @Input() placeholder = '';
  @Input() autocomplete = 'off';
  @Input() maxLength?: number;
  @Input() inputMode?: 'numeric' | 'text';
  @Input() digitsOnly = false;
  @Input() validationMessage = 'Revisa este campo.';
}
